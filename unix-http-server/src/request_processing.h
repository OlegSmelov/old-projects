#ifndef __REQUEST_PROCESSING_H
#define __REQUEST_PROCESSING_H

#include "main.h"

// Buffer size that is used for reading from files
// Curently 128 KB
#define BUFFER_SIZE (128 * 1024)

void process_request(struct thread_args_t *thread_args);

void http_process(struct thread_args_t *thread_args);
void inetd_process(struct thread_args_t *thread_args);

int launch_inetd_service(void *args);
int launch_cgi_script(void *args);

void request_destroy(struct request_data_t *request);

#endif
