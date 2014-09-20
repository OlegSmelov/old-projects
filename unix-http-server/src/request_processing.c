#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <sys/wait.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/stat.h>
#include <signal.h>

#ifdef ENABLE_ZLIB
#include <zlib.h>
#endif

#include "request_processing.h"
#include "config.h"
#include "utils.h"
#include "main.h"
#include "mime.h"

/**
 * Process the request using appropriate method (depending on configuration).
 * @param thread_args Thread arguments
 */
void process_request(struct thread_args_t *thread_args)
{
    if (!thread_args)
        return;

    struct config_t *config = thread_args->config;

    if (!config)
        return;

    if (config->mode)
    {
        if (!strcmp(config->mode, "http"))
            http_process(thread_args);
        else if (!strcmp(config->mode, "inetd"))
            inetd_process(thread_args);
    }
    else
        // use default
        http_process(thread_args);
}

/**
 * Read a HTTP request from socket and send appropriate response.
 * @param thread_args Thread arguments
 */
void http_process(struct thread_args_t *thread_args)
{
    if (!thread_args)
        return;

    struct config_t *config = thread_args->config;

    if (!config)
        return;

    int sockfd = thread_args->socketfd;

    // Read the first line of the request
    char *line;
    int bytes_read = freadline(sockfd, &line);

    if (bytes_read < 0)
        return; // FIXME: send 400 bad request

    if (bytes_read == 0)
    {
        free(line);
        return; // FIXME: send 400 bad request
    }

    char method[11], path[101], protocol[11];
    sscanf(line, "%10s %100s %10s", method, path, protocol);
    free(line);

    char query_string[101];

    char *path_pos = path, *query_pos = query_string;
    int start_found = 0;

    while (*path_pos)
    {
        if (start_found)
        {
            *query_pos++ = *path_pos++;
        }
        else
        {
            if (*path_pos == '?')
            {
                start_found = 1;
                *path_pos = '\0';
            }

            path_pos++;
        }
    }
    *query_pos = '\0';

    urldecode(path);
    sanitize_path(path);

    char *document_path = config->document_root;
    const char separator[] = "/";
    const char default_file[] = "/index.html";

    int path_length = strlen(path);
    char *new_path = new_path = malloc(path_length + strlen(separator) + strlen(document_path) + 1);

    strcpy(new_path, document_path);
    strcat(new_path, separator);
    strcat(new_path, path);

    struct stat status;
    if (stat(new_path, &status) == 0 && S_ISDIR(status.st_mode))
    {
        free(new_path);
        new_path = malloc(path_length + strlen(separator) + strlen(document_path) + strlen(default_file) + 1);

        strcpy(new_path, document_path);
        strcat(new_path, separator);
        strcat(new_path, path);
        strcat(new_path, default_file);
    }

    struct request_data_t request_data;
    memset(&request_data, '\0', sizeof(request_data));
    int cgi_mode = 0;

    if (config->cgi_enabled && access(new_path, X_OK) == 0)
        cgi_mode = 1;

    char *header, *value;
    while (freaduntil(sockfd, &header, ' ') > 0)
    {
        freadline(sockfd, &value);
        if (!strcmp(header, "Content-Length:"))
        {
            if (request_data.content_length)
                free(request_data.content_length);
            request_data.content_length = value;
        }
        else if (!strcmp(header, "Content-Type:"))
        {
            if (request_data.content_type)
                free(request_data.content_type);
            request_data.content_type = value;
        }
        else if (!strcmp(header, "Host:"))
        {
            if (request_data.server_name)
                free(request_data.server_name);
            request_data.server_name = value;
        }
        else if (!strcmp(header, "Cookie:"))
        {
            if (request_data.cookie)
                free(request_data.cookie);
            request_data.cookie = value;
        }
        else if (!strcmp(header, "Accept-Encoding:"))
        {
            request_data.supports_gzip = strstr(value, "gzip") != NULL;
            free(value);
        }
        else
            free(value);
        free(header);
    }
    free(header);

    request_data.query_string = malloc(strlen(query_string) + 1);
    strcpy(request_data.query_string, query_string);

    char buf[INET_ADDRSTRLEN] = "";
    struct sockaddr_in name;
    socklen_t len = sizeof(name);

    if (getpeername(sockfd, (struct sockaddr *)&name, &len) != 0)
        perror("getpeername");
    else
    {
        inet_ntop(AF_INET, &name.sin_addr, buf, sizeof buf);
        request_data.remote_addr = malloc(strlen(buf) + 1);
        strcpy(request_data.remote_addr, buf);
    }

    request_data.request_method = malloc(strlen(method) + 1);
    strcpy(request_data.request_method, method);

    request_data.script_name = malloc(strlen(path) + 2);
    strcpy(request_data.script_name, separator);
    strcat(request_data.script_name, path);

    request_data.server_port = malloc(20);
    sprintf(request_data.server_port, "%d", config->port);

    request_data.path = new_path;

    thread_args->request = &request_data;

    if (cgi_mode)
    {
        const char http_header[] = "HTTP/1.1 200 OK\r\n";
        write(sockfd, http_header, strlen(http_header));

        int pid = pcreate(NULL, NULL, launch_cgi_script, thread_args);

        waitpid(pid, NULL, 0);
    }
    else
    {
        FILE *f = fopen(new_path, "r");

        if (f == NULL)
        {
            const char not_found_response[] = "HTTP/1.1 404 Not Found\r\n\r\nRequested file not found\r\n";
            write(sockfd, not_found_response, strlen(not_found_response));
        }
        else
        {
            const char endline[] = "\r\n";
            const char http_header[] = "HTTP/1.1 200 OK\r\n";
            write(sockfd, http_header, strlen(http_header));

            fseek(f, 0, SEEK_END);
            long size = ftell(f);
            fseek(f, 0, SEEK_SET);

            char sizestr[30] = "";
            sprintf(sizestr, "%ld", size);

            int text_mime = 0;
            char *ext = strrchr(new_path, '.');
            if (ext != NULL)
            {
                const char *mime = get_mime_type(ext + 1);
                if (mime != NULL)
                {
                    // FIXME: text detection is subpar
                    text_mime = strstr(mime, "text/") != NULL;

                    const char content_type_header[] = "Content-Type: ";
                    write(sockfd, content_type_header, strlen(content_type_header));
                    write(sockfd, mime, strlen(mime));
                    write(sockfd, endline, strlen(endline));
                }
            }

#ifdef ENABLE_ZLIB
            // We only compress text files and only when the browser supports it
            if (request_data.supports_gzip && text_mime)
            {
                const char gzip_encoding_header[] = "Content-Encoding: gzip";
                write(sockfd, gzip_encoding_header, strlen(gzip_encoding_header));
                write(sockfd, endline, strlen(endline));
                
                // end headers, begin output
                write(sockfd, endline, strlen(endline));

                // You might think to yourself that this code looks too much like
                // zpipe.c (zlib example). This is no coincidence, I used it as an
                // example, changing up bits and pieces to suit my program.
                // One major difference is, though, that it uses gzip headers instead.
                unsigned char *in = malloc(BUFFER_SIZE);
                unsigned char *out = malloc(BUFFER_SIZE);
                int flush, gzip_error = 0;
                z_stream stream;

                stream.zalloc = Z_NULL;
                stream.zfree = Z_NULL;
                stream.opaque = Z_NULL;
                stream.next_in = Z_NULL;
                int ret = deflateInit2(&stream, Z_DEFAULT_COMPRESSION, Z_DEFLATED,
                        16 + MAX_WBITS, 8, Z_DEFAULT_STRATEGY);

                if (ret == Z_OK)
                {
                    do
                    {
                        stream.avail_in = fread(in, 1, BUFFER_SIZE, f);
                        if (ferror(f))
                        {
                            deflateEnd(&stream);
                            gzip_error = 1;
                            break;
                        }

                        flush = feof(f) ? Z_FINISH : Z_NO_FLUSH;
                        stream.next_in = in;

                        do
                        {
                            stream.avail_out = BUFFER_SIZE;
                            stream.next_out = out;
                            ret = deflate(&stream, flush);

                            if (ret == Z_STREAM_ERROR)
                            {
                                deflateEnd(&stream);
                                gzip_error = 1;
                                break;
                            }

                            int have = BUFFER_SIZE - stream.avail_out;
                            if (write(sockfd, out, have) != have) {
                                deflateEnd(&stream);
                                gzip_error = 1;
                                break;
                            }
                        }
                        while (stream.avail_out == 0);
                    }
                    while (!gzip_error && flush != Z_FINISH);

                    if (!gzip_error)
                        deflateEnd(&stream);
                }

                free(in);
                free(out);
            }
            else
            {
#endif
                const char content_length_header[] = "Content-Length: ";
                write(sockfd, content_length_header, strlen(content_length_header));
                write(sockfd, sizestr, strlen(sizestr));
                write(sockfd, endline, strlen(endline));

                // end headers, begin output
                write(sockfd, endline, strlen(endline));

                char *buffer = malloc(BUFFER_SIZE);
                int bytes_read = 0;

                while (!feof(f))
                {
                    bytes_read = fread(buffer, 1, BUFFER_SIZE, f);
                    if (bytes_read > 0)
                        write(sockfd, buffer, bytes_read);
                }

                free(buffer);
#ifdef ENABLE_ZLIB
            }
#endif
            fclose(f);
        }
    }

    request_destroy(&request_data);
}

/**
 * Delegate all processing of the request to an external program or a script.
 * Data received through a socket can be read via stdin and sent via stdout.
 * Basically, inetd behavior is emulated.
 * @param thread_args Thread arguments
 */
void inetd_process(struct thread_args_t *thread_args)
{
    if (!thread_args)
        return;

    //int sockfd = thread_args->socketfd;
    int pid = pcreate(NULL, NULL, launch_inetd_service, thread_args);

    waitpid(pid, NULL, 0);
}

/**
 * This function is passed to pcreate (located in utils.c). It launches inetd
 * service, waits for it to finish and then returns the exit value.
 * @param args Thread arguments
 * @return Inetd service exit value
 */
int launch_inetd_service(void *args)
{
    struct thread_args_t *thread_args = args;

    if (!thread_args)
        return 1;

    int sockfd = thread_args->socketfd;

    if (!thread_args->config)
        return 1;

    char *inetd_service = thread_args->config->inetd_service;

    dup2(sockfd, STDIN_FILENO);
    dup2(sockfd, STDOUT_FILENO);

    signal(SIGPIPE, SIG_IGN);

    return system(inetd_service);
}

int launch_cgi_script(void *args)
{
    struct thread_args_t *thread_args = args;

    if (!thread_args)
        return 1;

    int sockfd = thread_args->socketfd;

    if (!thread_args->request)
        return 1;

    struct request_data_t *request_data = thread_args->request;

    setenv("CONTENT_LENGTH",    request_data->content_length,   1);
    setenv("CONTENT_TYPE",      request_data->content_type,     1);
    setenv("GATEWAY_INTERFACE", "CGI/1.1",                      1);
    setenv("QUERY_STRING",      request_data->query_string,     1);
    setenv("REMOTE_ADDR",       request_data->remote_addr,      1);
    setenv("REMOTE_HOST",       request_data->remote_addr,      1);
    setenv("REQUEST_METHOD",    request_data->request_method,   1);
    setenv("SCRIPT_NAME",       request_data->script_name,      1);
    setenv("SCRIPT_FILENAME",   request_data->path,             1);
    setenv("SERVER_NAME",       request_data->server_name,      1);
    setenv("SERVER_PORT",       request_data->server_port,      1);
    setenv("HTTP_COOKIE",       request_data->cookie,           1);
    setenv("SERVER_PROTOCOL",   "HTTP/1.1",                     1);
    setenv("SERVER_SOFTWARE",   "HTTPServer/0.1",               1);
    setenv("REDIRECT_STATUS",   "200",                          1);

    dup2(sockfd, STDIN_FILENO);
    dup2(sockfd, STDOUT_FILENO);

    signal(SIGPIPE, SIG_IGN);
    
    return system(request_data->path);
}

/**
 * Frees all memory segments associated with the request
 * @param request Request to free
 */
void request_destroy(struct request_data_t *request)
{
    if (!request)
        return;

    free_and_null((void **)&request->path);
    free_and_null((void **)&request->content_length);
    free_and_null((void **)&request->content_type);
    free_and_null((void **)&request->query_string);
    free_and_null((void **)&request->remote_addr);
    free_and_null((void **)&request->request_method);
    free_and_null((void **)&request->script_name);
    free_and_null((void **)&request->server_name);
    free_and_null((void **)&request->server_port);
    free_and_null((void **)&request->cookie);
}
