#ifndef __UTILS_H
#define __UTILS_H

typedef int (*child_func_t)(void *args);

void sanitize_path(char *path);
int freadline(int filed, char **out);
int freaduntil(int fd, char **out, char c);
int is_hex_numeric(char c);
int hex_to_dec(char c);
int urldecode(char *url);
int pcreate(int *infd, int *outfd, child_func_t child_function, void *args);
void free_and_null(void **ptr);

#endif
