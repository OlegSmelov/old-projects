#ifndef CONFIG_H
#define	CONFIG_H

struct config_t
{
    char *document_root, *mode, *inetd_service;
    int port, cgi_enabled;
};

void config_init(struct config_t *config);
void config_destroy(struct config_t *config);
int config_load(struct config_t *config, char *filename);

#endif
