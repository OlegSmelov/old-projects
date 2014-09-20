#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "utils.h"

/**
 * Removes folder ".." from path. Also removes unnecessary slashes.
 * Everything is done in-place.
 * @param path Path to sanitize
 */
void sanitize_path(char *path)
{
    char *current = path;
    int start = 1;

    while (*current)
    {
        if (start)
        {
            if (current[0] == '.' && current[1] == '.' &&
                    (current[2] == '/' || current[2] == '\0'))
            {
                if (current[2] == '/')
                        current++;
                current += 2;
                continue;
            }

            if (current[0] == '/')
            {
                current++;
                continue;
            }

            start = 0;
        }

        if (*current == '/')
            start = 1;

        *path++ = *current++;
    }

    *path = '\0';
}

/**
 * Reads a line from a file descriptor. Allocates new memory and returns
 * the pointer to that address via parameter out. Allocated memory must be
 * freed after being used.
 * @param fd File descriptor
 * @param out Pointer to a pointer to an array
 * @return Number of bytes read
 */
int freadline(int fd, char **out)
{
    return freaduntil(fd, out, '\n');
}

/**
 * Reads a bytes from a file descriptor until a specified character is found.
 * Allocates new memory and returns the pointer to that address via parameter
 * out. Allocated memory must be freed after being used.
 * @param fd File descriptor
 * @param out Pointer to a pointer to an array
 * @param c Character that marks the end
 * @return Number of bytes read
 */
int freaduntil(int fd, char **out, char c)
{
    int buffer_size = 128, buffer_pos = 0;
    char *buffer = malloc(buffer_size * sizeof(char));

    if (!buffer)
        return -1;

    while (1)
    {
        char byte;
        int bytes_read = read(fd, &byte, sizeof(byte));

        // error
        if (bytes_read < 0)
        {
            free(buffer);
            return -1;
        }

        if (bytes_read == 0)
            break;

        if (byte == c || byte == '\n')
            break;

        buffer[buffer_pos++] = byte;

        // +1 since we need to put zero byte at the end
        if (buffer_pos + 1 >= buffer_size)
        {
            buffer_size *= 2;
            char *new_buffer = realloc(buffer, buffer_size);

            if (new_buffer == NULL)
            {
                free(buffer);
                return -1;
            }

            buffer = new_buffer;
        }
    }

    // ignore \r at the end of the line
    if (buffer_pos && buffer[buffer_pos - 1] == '\r')
        buffer_pos--;

    buffer[buffer_pos] = '\0';

    *out = buffer;
    return buffer_pos;
}

/**
 * Check if char is a hex character (A-Z, a-z).
 * @param c Character
 * @return Non-zero value if hex char
 */
int is_hex_numeric(char c)
{
    return (('0' <= c) && (c <= '9')) ||
           (('a' <= c) && (c <= 'f')) ||
           (('A' <= c) && (c <= 'F'));
}

/**
 * Converts hex character to its value (e.g. 'E' -> 14).
 * @param c Hex character
 * @return Value
 */
int hex_to_dec(char c)
{
    if (('0' <= c) && (c <= '9'))
    {
        return c - '0';
    }
    else if (('a' <= c) && (c <= 'f'))
    {
        return c - 'a' + 10;
    }
    else if (('A' <= c) && (c <= 'F'))
    {
        return c - 'A' + 10;
    }

    return -1;
}

/**
 * Decodes a URL in place.
 * @param url String to process
 * @return Length of decoded string
 */
int urldecode(char *url)
{
    int i, length = strlen(url),
        current_pos = 0;

    for (i = 0; i < length; i++)
    {
        if (url[i] == '%' && (i + 2 < length)
                && is_hex_numeric(url[i + 1]) && is_hex_numeric(url[i + 2]))
        {
            int value = (hex_to_dec(url[i + 1]) << 4) | hex_to_dec(url[i + 2]);

            if (value > 0)
            {
                url[current_pos] = value;
                current_pos++;
            }

            i += 2; // skip 2 symbols
        }
        else
        {
            url[current_pos] = url[i];
            current_pos++;
        }
    }

    url[current_pos] = '\0';

    return current_pos;
}

/**
 * Creates a fork of current process, sets up pipes for communication with the
 * child process. child_function is executed in the child process. Sets values
 * to integers infd and outfd point to.
 * @param infd Pointer to input file descriptor
 * @param outfd Pointer to output file descriptor
 * @param child_function Function to be executed in the child process
 * @param args Pointer be passed to child_function
 * @return PID of new process on success (positive value), negative value on success
 */
int pcreate(int *infd, int *outfd, child_func_t child_function, void *args)
{
    int pipes[4], pid;

    if (pipe(pipes))
        return -2;

    if (pipe(pipes + 2))
        return -2;

    switch ((pid = fork()))
    {
        case -1:
            return -3;

        case 0:
            // child
            close(pipes[0]);
            close(pipes[3]);

            dup2(pipes[2], STDIN_FILENO);
            dup2(pipes[1], STDOUT_FILENO);

            exit(child_function(args));
            break;

        default:
            // parent
            close(pipes[1]);
            close(pipes[2]);

            if (infd)
                *infd = pipes[0];
            else
                close(pipes[0]);

            if (outfd)
                *outfd = pipes[3];
            else
                close(pipes[3]);
            break;
    }

    return pid;
}

/**
 * Frees a memory segment and set pointer to it to NULL
 * @param ptr Pointer to a pointer to free
 */
void free_and_null(void **ptr)
{
    if (!ptr)
        return;

    if (*ptr)
    {
        free(*ptr);
        *ptr = NULL;
    }
}