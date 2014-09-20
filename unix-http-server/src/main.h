#ifndef __MAIN_H
#define __MAIN_H

#define CONFIG_FILE     "http-server.conf"
#define TIMEOUT         60
#define BACKLOG         10

struct request_data_t
{
    // path to the executable
    char *path;
    // CGI variables
    char *content_length, *content_type, *query_string, *remote_addr,
         *request_method, *script_name, *server_name, *server_port,
         *cookie;
    char supports_gzip;
};

#include "config.h"

struct thread_args_t
{
    int socketfd;
    struct config_t *config;
    struct request_data_t *request;
};

#endif
