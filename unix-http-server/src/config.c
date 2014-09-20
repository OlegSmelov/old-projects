#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "config.h"
#include "utils.h"

const char *default_document_root = "www";
const int default_port = 8080;
const int default_cgi_enabled = 0;

/**
 * Initialize configuration with default values
 * @param config Configuration to initialize
 */
void config_init(struct config_t *config)
{
    config->document_root = realpath(default_document_root, NULL);
    config->mode = NULL;
    config->inetd_service = NULL;
    config->cgi_enabled = default_cgi_enabled;
    config->port = default_port;
}

/**
 * Free memory associated with the configuration
 * @param config Configuration to destroy
 */
void config_destroy(struct config_t *config)
{
    if (config->document_root)
    {
        free(config->document_root);
        config->document_root = NULL;
    }

    if (config->inetd_service)
    {
        free(config->inetd_service);
        config->inetd_service = NULL;
    }

    if (config->mode)
    {
        free(config->mode);
        config->mode = NULL;
    }
}

/**
 * Load configuration from file.
 * @param config Configuration to load
 * @param filename File name to load configuration from
 * @return Non-zero value on success
 */
int config_load(struct config_t *config, char *filename)
{
    FILE *file = fopen(filename, "r");

    if (file != NULL)
    {
        for (;;)
        {
            char *line;
            if (freaduntil(fileno(file), &line, '=') == 0)
                break;

            if (!strcmp(line, "document_root"))
            {
                char *document_root;
                freadline(fileno(file), &document_root);

                if (config->document_root)
                    free(config->document_root);

                config->document_root = realpath(document_root, NULL);
                free(document_root);
            }
            else if (!strcmp(line, "mode"))
            {
                char *mode;
                freadline(fileno(file), &mode);

                config->mode = mode;
            }
            else if (!strcmp(line, "inetd_service"))
            {
                char *inetd_service;
                freadline(fileno(file), &inetd_service);

                config->inetd_service = inetd_service;
            }
            else if (!strcmp(line, "cgi_enabled"))
            {
                char *cgi_enabled;
                freadline(fileno(file), &cgi_enabled);

                config->cgi_enabled = atoi(cgi_enabled);
                free(cgi_enabled);
            }
            else if (!strcmp(line, "port"))
            {
                char *port;
                freadline(fileno(file), &port);

                config->port = atoi(port);
                free(port);
            }

            free(line);
        }
    }
    else
    {
        return 0;
    }

    return 1;
}