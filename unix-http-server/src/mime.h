#ifndef __MIME_H
#define __MIME_H

typedef struct {
    const char *file_ext;
    const char *mime;
} mime_item;

#define MIME_TYPE_COUNT 521

const char *get_mime_type(const char *extension);

#endif
