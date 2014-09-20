#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>

#include "main.h"
#include "request_processing.h"
#include "utils.h"
#include "config.h"

void *thread_func(void *args)
{
    pthread_detach(pthread_self());
    signal(SIGPIPE, SIG_IGN);
    struct thread_args_t *thread_args = args;

    if (thread_args)
    {
        int sockfd = thread_args->socketfd;

        process_request(thread_args);

        shutdown(sockfd, SHUT_WR);

        fcntl(sockfd, F_SETFL, O_NONBLOCK);
        char n;
        while (read(sockfd, &n, 1) > 0);

        close(sockfd);

        // clean up after receiving socket fd
        free(thread_args);
    }

    return NULL;
}

void print_error(const char *msg)
{
    fputs(msg, stderr);
}

int main()
{
    signal(SIGPIPE, SIG_IGN);

    struct config_t config;

    config_init(&config);
    if (!config_load(&config, CONFIG_FILE))
    {
        print_error("Error reading config file, using default values\n");
    }

    char *resolved_path = realpath(config.document_root, NULL);

    if (resolved_path == NULL)
    {
        print_error("Could not resolve document_root\n");
        exit(1);
    }

    config.document_root = resolved_path;

    int sockfd;

    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0)
    {
        print_error("socket(): could not open socket\n");
        exit(1);
    }

    int value = 1;
    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &value, sizeof(value)) == -1)
    {
        print_error("setsockopt(): reuse error\n");
        exit(1);
    }

    struct timeval tv;
    tv.tv_sec = TIMEOUT;
    tv.tv_usec = 0;

    if (setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) == -1)
    {
        print_error("setsockopt(): timeout error\n");
        exit(1);
    }

    struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = ((in_addr_t) 0);
    serv_addr.sin_port = htons(config.port);

    if (bind(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
    {
        print_error("bind(): error\n");
        exit(1);
    }

    printf("Server started\n");
    printf("Using port: %d\n", config.port);
    printf("Document root: %s\n", resolved_path);

    listen(sockfd, BACKLOG);

    while (1)
    {
        struct thread_args_t *args = malloc(sizeof(struct thread_args_t));

        if (args == NULL)
        {
            print_error("malloc(): error\n");
            exit(1);
        }

        args->socketfd = accept(sockfd, NULL, NULL);
        args->config = &config;

        if (args->socketfd < 0)
        {
            free(args);
            continue;
        }

        pthread_t thread;

        if (pthread_create(&thread, NULL, thread_func, args) != 0)
        {
            close(args->socketfd);
            free(args);

            print_error("pthread_create(): failed to create a thread\n");
        }
    }

    config_destroy(&config);

    return 0;
}
