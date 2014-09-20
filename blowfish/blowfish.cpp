#include <cstdio>
#include <fstream>
#include <algorithm>

#include "blowfish.h"

// https://www.schneier.com/paper-blowfish-fse.html

using namespace std;

static const uint8_t piBinaryDigits[] = {
    0x24, 0x3f, 0x6a, 0x88, 0x85, 0xa3, 0x08, 0xd3, 0x13, 0x19, 0x8a, 0x2e,
    0x03, 0x70, 0x73, 0x44, 0xa4, 0x09, 0x38, 0x22, 0x29, 0x9f, 0x31, 0xd0,
    0x08, 0x2e, 0xfa, 0x98, 0xec, 0x4e, 0x6c, 0x89, 0x45, 0x28, 0x21, 0xe6,
    0x38, 0xd0, 0x13, 0x77, 0xbe, 0x54, 0x66, 0xcf, 0x34, 0xe9, 0x0c, 0x6c,
    0xc0, 0xac, 0x29, 0xb7, 0xc9, 0x7c, 0x50, 0xdd, 0x3f, 0x84, 0xd5, 0xb5,
    0xb5, 0x47, 0x09, 0x17, 0x92, 0x16, 0xd5, 0xd9, 0x89, 0x79, 0xfb, 0x1b,
    0xd1, 0x31, 0x0b, 0xa6, 0x98, 0xdf, 0xb5, 0xac, 0x2f, 0xfd, 0x72, 0xdb,
    0xd0, 0x1a, 0xdf, 0xb7, 0xb8, 0xe1, 0xaf, 0xed, 0x6a, 0x26, 0x7e, 0x96,
    0xba, 0x7c, 0x90, 0x45, 0xf1, 0x2c, 0x7f, 0x99, 0x24, 0xa1, 0x99, 0x47,
    0xb3, 0x91, 0x6c, 0xf7, 0x08, 0x01, 0xf2, 0xe2, 0x85, 0x8e, 0xfc, 0x16,
    0x63, 0x69, 0x20, 0xd8, 0x71, 0x57, 0x4e, 0x69, 0xa4, 0x58, 0xfe, 0xa3,
    0xf4, 0x93, 0x3d, 0x7e, 0x0d, 0x95, 0x74, 0x8f, 0x72, 0x8e, 0xb6, 0x58,
    0x71, 0x8b, 0xcd, 0x58, 0x82, 0x15, 0x4a, 0xee, 0x7b, 0x54, 0xa4, 0x1d,
    0xc2, 0x5a, 0x59, 0xb5, 0x9c, 0x30, 0xd5, 0x39, 0x2a, 0xf2, 0x60, 0x13,
    0xc5, 0xd1, 0xb0, 0x23, 0x28, 0x60, 0x85, 0xf0, 0xca, 0x41, 0x79, 0x18,
    0xb8, 0xdb, 0x38, 0xef, 0x8e, 0x79, 0xdc, 0xb0, 0x60, 0x3a, 0x18, 0x0e,
    0x6c, 0x9e, 0x0e, 0x8b, 0xb0, 0x1e, 0x8a, 0x3e, 0xd7, 0x15, 0x77, 0xc1,
    0xbd, 0x31, 0x4b, 0x27, 0x78, 0xaf, 0x2f, 0xda, 0x55, 0x60, 0x5c, 0x60,
    0xe6, 0x55, 0x25, 0xf3, 0xaa, 0x55, 0xab, 0x94, 0x57, 0x48, 0x98, 0x62,
    0x63, 0xe8, 0x14, 0x40, 0x55, 0xca, 0x39, 0x6a, 0x2a, 0xab, 0x10, 0xb6,
    0xb4, 0xcc, 0x5c, 0x34, 0x11, 0x41, 0xe8, 0xce, 0xa1, 0x54, 0x86, 0xaf,
    0x7c, 0x72, 0xe9, 0x93, 0xb3, 0xee, 0x14, 0x11, 0x63, 0x6f, 0xbc, 0x2a,
    0x2b, 0xa9, 0xc5, 0x5d, 0x74, 0x18, 0x31, 0xf6, 0xce, 0x5c, 0x3e, 0x16,
    0x9b, 0x87, 0x93, 0x1e, 0xaf, 0xd6, 0xba, 0x33, 0x6c, 0x24, 0xcf, 0x5c,
    0x7a, 0x32, 0x53, 0x81, 0x28, 0x95, 0x86, 0x77, 0x3b, 0x8f, 0x48, 0x98,
    0x6b, 0x4b, 0xb9, 0xaf, 0xc4, 0xbf, 0xe8, 0x1b, 0x66, 0x28, 0x21, 0x93,
    0x61, 0xd8, 0x09, 0xcc, 0xfb, 0x21, 0xa9, 0x91, 0x48, 0x7c, 0xac, 0x60,
    0x5d, 0xec, 0x80, 0x32, 0xef, 0x84, 0x5d, 0x5d, 0xe9, 0x85, 0x75, 0xb1,
    0xdc, 0x26, 0x23, 0x02, 0xeb, 0x65, 0x1b, 0x88, 0x23, 0x89, 0x3e, 0x81,
    0xd3, 0x96, 0xac, 0xc5, 0x0f, 0x6d, 0x6f, 0xf3, 0x83, 0xf4, 0x42, 0x39,
    0x2e, 0x0b, 0x44, 0x82, 0xa4, 0x84, 0x20, 0x04, 0x69, 0xc8, 0xf0, 0x4a,
    0x9e, 0x1f, 0x9b, 0x5e, 0x21, 0xc6, 0x68, 0x42, 0xf6, 0xe9, 0x6c, 0x9a,
    0x67, 0x0c, 0x9c, 0x61, 0xab, 0xd3, 0x88, 0xf0, 0x6a, 0x51, 0xa0, 0xd2,
    0xd8, 0x54, 0x2f, 0x68, 0x96, 0x0f, 0xa7, 0x28, 0xab, 0x51, 0x33, 0xa3,
    0x6e, 0xef, 0x0b, 0x6c, 0x13, 0x7a, 0x3b, 0xe4, 0xba, 0x3b, 0xf0, 0x50,
    0x7e, 0xfb, 0x2a, 0x98, 0xa1, 0xf1, 0x65, 0x1d, 0x39, 0xaf, 0x01, 0x76,
    0x66, 0xca, 0x59, 0x3e, 0x82, 0x43, 0x0e, 0x88, 0x8c, 0xee, 0x86, 0x19,
    0x45, 0x6f, 0x9f, 0xb4, 0x7d, 0x84, 0xa5, 0xc3, 0x3b, 0x8b, 0x5e, 0xbe,
    0xe0, 0x6f, 0x75, 0xd8, 0x85, 0xc1, 0x20, 0x73, 0x40, 0x1a, 0x44, 0x9f,
    0x56, 0xc1, 0x6a, 0xa6, 0x4e, 0xd3, 0xaa, 0x62, 0x36, 0x3f, 0x77, 0x06,
    0x1b, 0xfe, 0xdf, 0x72, 0x42, 0x9b, 0x02, 0x3d, 0x37, 0xd0, 0xd7, 0x24,
    0xd0, 0x0a, 0x12, 0x48, 0xdb, 0x0f, 0xea, 0xd3, 0x49, 0xf1, 0xc0, 0x9b,
    0x07, 0x53, 0x72, 0xc9, 0x80, 0x99, 0x1b, 0x7b, 0x25, 0xd4, 0x79, 0xd8,
    0xf6, 0xe8, 0xde, 0xf7, 0xe3, 0xfe, 0x50, 0x1a, 0xb6, 0x79, 0x4c, 0x3b,
    0x97, 0x6c, 0xe0, 0xbd, 0x04, 0xc0, 0x06, 0xba, 0xc1, 0xa9, 0x4f, 0xb6,
    0x40, 0x9f, 0x60, 0xc4, 0x5e, 0x5c, 0x9e, 0xc2, 0x19, 0x6a, 0x24, 0x63,
    0x68, 0xfb, 0x6f, 0xaf, 0x3e, 0x6c, 0x53, 0xb5, 0x13, 0x39, 0xb2, 0xeb,
    0x3b, 0x52, 0xec, 0x6f, 0x6d, 0xfc, 0x51, 0x1f, 0x9b, 0x30, 0x95, 0x2c,
    0xcc, 0x81, 0x45, 0x44, 0xaf, 0x5e, 0xbd, 0x09, 0xbe, 0xe3, 0xd0, 0x04,
    0xde, 0x33, 0x4a, 0xfd, 0x66, 0x0f, 0x28, 0x07, 0x19, 0x2e, 0x4b, 0xb3,
    0xc0, 0xcb, 0xa8, 0x57, 0x45, 0xc8, 0x74, 0x0f, 0xd2, 0x0b, 0x5f, 0x39,
    0xb9, 0xd3, 0xfb, 0xdb, 0x55, 0x79, 0xc0, 0xbd, 0x1a, 0x60, 0x32, 0x0a,
    0xd6, 0xa1, 0x00, 0xc6, 0x40, 0x2c, 0x72, 0x79, 0x67, 0x9f, 0x25, 0xfe,
    0xfb, 0x1f, 0xa3, 0xcc, 0x8e, 0xa5, 0xe9, 0xf8, 0xdb, 0x32, 0x22, 0xf8,
    0x3c, 0x75, 0x16, 0xdf, 0xfd, 0x61, 0x6b, 0x15, 0x2f, 0x50, 0x1e, 0xc8,
    0xad, 0x05, 0x52, 0xab, 0x32, 0x3d, 0xb5, 0xfa, 0xfd, 0x23, 0x87, 0x60,
    0x53, 0x31, 0x7b, 0x48, 0x3e, 0x00, 0xdf, 0x82, 0x9e, 0x5c, 0x57, 0xbb,
    0xca, 0x6f, 0x8c, 0xa0, 0x1a, 0x87, 0x56, 0x2e, 0xdf, 0x17, 0x69, 0xdb,
    0xd5, 0x42, 0xa8, 0xf6, 0x28, 0x7e, 0xff, 0xc3, 0xac, 0x67, 0x32, 0xc6,
    0x8c, 0x4f, 0x55, 0x73, 0x69, 0x5b, 0x27, 0xb0, 0xbb, 0xca, 0x58, 0xc8,
    0xe1, 0xff, 0xa3, 0x5d, 0xb8, 0xf0, 0x11, 0xa0, 0x10, 0xfa, 0x3d, 0x98,
    0xfd, 0x21, 0x83, 0xb8, 0x4a, 0xfc, 0xb5, 0x6c, 0x2d, 0xd1, 0xd3, 0x5b,
    0x9a, 0x53, 0xe4, 0x79, 0xb6, 0xf8, 0x45, 0x65, 0xd2, 0x8e, 0x49, 0xbc,
    0x4b, 0xfb, 0x97, 0x90, 0xe1, 0xdd, 0xf2, 0xda, 0xa4, 0xcb, 0x7e, 0x33,
    0x62, 0xfb, 0x13, 0x41, 0xce, 0xe4, 0xc6, 0xe8, 0xef, 0x20, 0xca, 0xda,
    0x36, 0x77, 0x4c, 0x01, 0xd0, 0x7e, 0x9e, 0xfe, 0x2b, 0xf1, 0x1f, 0xb4,
    0x95, 0xdb, 0xda, 0x4d, 0xae, 0x90, 0x91, 0x98, 0xea, 0xad, 0x8e, 0x71,
    0x6b, 0x93, 0xd5, 0xa0, 0xd0, 0x8e, 0xd1, 0xd0, 0xaf, 0xc7, 0x25, 0xe0,
    0x8e, 0x3c, 0x5b, 0x2f, 0x8e, 0x75, 0x94, 0xb7, 0x8f, 0xf6, 0xe2, 0xfb,
    0xf2, 0x12, 0x2b, 0x64, 0x88, 0x88, 0xb8, 0x12, 0x90, 0x0d, 0xf0, 0x1c,
    0x4f, 0xad, 0x5e, 0xa0, 0x68, 0x8f, 0xc3, 0x1c, 0xd1, 0xcf, 0xf1, 0x91,
    0xb3, 0xa8, 0xc1, 0xad, 0x2f, 0x2f, 0x22, 0x18, 0xbe, 0x0e, 0x17, 0x77,
    0xea, 0x75, 0x2d, 0xfe, 0x8b, 0x02, 0x1f, 0xa1, 0xe5, 0xa0, 0xcc, 0x0f,
    0xb5, 0x6f, 0x74, 0xe8, 0x18, 0xac, 0xf3, 0xd6, 0xce, 0x89, 0xe2, 0x99,
    0xb4, 0xa8, 0x4f, 0xe0, 0xfd, 0x13, 0xe0, 0xb7, 0x7c, 0xc4, 0x3b, 0x81,
    0xd2, 0xad, 0xa8, 0xd9, 0x16, 0x5f, 0xa2, 0x66, 0x80, 0x95, 0x77, 0x05,
    0x93, 0xcc, 0x73, 0x14, 0x21, 0x1a, 0x14, 0x77, 0xe6, 0xad, 0x20, 0x65,
    0x77, 0xb5, 0xfa, 0x86, 0xc7, 0x54, 0x42, 0xf5, 0xfb, 0x9d, 0x35, 0xcf,
    0xeb, 0xcd, 0xaf, 0x0c, 0x7b, 0x3e, 0x89, 0xa0, 0xd6, 0x41, 0x1b, 0xd3,
    0xae, 0x1e, 0x7e, 0x49, 0x00, 0x25, 0x0e, 0x2d, 0x20, 0x71, 0xb3, 0x5e,
    0x22, 0x68, 0x00, 0xbb, 0x57, 0xb8, 0xe0, 0xaf, 0x24, 0x64, 0x36, 0x9b,
    0xf0, 0x09, 0xb9, 0x1e, 0x55, 0x63, 0x91, 0x1d, 0x59, 0xdf, 0xa6, 0xaa,
    0x78, 0xc1, 0x43, 0x89, 0xd9, 0x5a, 0x53, 0x7f, 0x20, 0x7d, 0x5b, 0xa2,
    0x02, 0xe5, 0xb9, 0xc5, 0x83, 0x26, 0x03, 0x76, 0x62, 0x95, 0xcf, 0xa9,
    0x11, 0xc8, 0x19, 0x68, 0x4e, 0x73, 0x4a, 0x41, 0xb3, 0x47, 0x2d, 0xca,
    0x7b, 0x14, 0xa9, 0x4a, 0x1b, 0x51, 0x00, 0x52, 0x9a, 0x53, 0x29, 0x15,
    0xd6, 0x0f, 0x57, 0x3f, 0xbc, 0x9b, 0xc6, 0xe4, 0x2b, 0x60, 0xa4, 0x76,
    0x81, 0xe6, 0x74, 0x00, 0x08, 0xba, 0x6f, 0xb5, 0x57, 0x1b, 0xe9, 0x1f,
    0xf2, 0x96, 0xec, 0x6b, 0x2a, 0x0d, 0xd9, 0x15, 0xb6, 0x63, 0x65, 0x21,
    0xe7, 0xb9, 0xf9, 0xb6, 0xff, 0x34, 0x05, 0x2e, 0xc5, 0x85, 0x56, 0x64,
    0x53, 0xb0, 0x2d, 0x5d, 0xa9, 0x9f, 0x8f, 0xa1, 0x08, 0xba, 0x47, 0x99,
    0x6e, 0x85, 0x07, 0x6a, 0x4b, 0x7a, 0x70, 0xe9, 0xb5, 0xb3, 0x29, 0x44,
    0xdb, 0x75, 0x09, 0x2e, 0xc4, 0x19, 0x26, 0x23, 0xad, 0x6e, 0xa6, 0xb0,
    0x49, 0xa7, 0xdf, 0x7d, 0x9c, 0xee, 0x60, 0xb8, 0x8f, 0xed, 0xb2, 0x66,
    0xec, 0xaa, 0x8c, 0x71, 0x69, 0x9a, 0x17, 0xff, 0x56, 0x64, 0x52, 0x6c,
    0xc2, 0xb1, 0x9e, 0xe1, 0x19, 0x36, 0x02, 0xa5, 0x75, 0x09, 0x4c, 0x29,
    0xa0, 0x59, 0x13, 0x40, 0xe4, 0x18, 0x3a, 0x3e, 0x3f, 0x54, 0x98, 0x9a,
    0x5b, 0x42, 0x9d, 0x65, 0x6b, 0x8f, 0xe4, 0xd6, 0x99, 0xf7, 0x3f, 0xd6,
    0xa1, 0xd2, 0x9c, 0x07, 0xef, 0xe8, 0x30, 0xf5, 0x4d, 0x2d, 0x38, 0xe6,
    0xf0, 0x25, 0x5d, 0xc1, 0x4c, 0xdd, 0x20, 0x86, 0x84, 0x70, 0xeb, 0x26,
    0x63, 0x82, 0xe9, 0xc6, 0x02, 0x1e, 0xcc, 0x5e, 0x09, 0x68, 0x6b, 0x3f,
    0x3e, 0xba, 0xef, 0xc9, 0x3c, 0x97, 0x18, 0x14, 0x6b, 0x6a, 0x70, 0xa1,
    0x68, 0x7f, 0x35, 0x84, 0x52, 0xa0, 0xe2, 0x86, 0xb7, 0x9c, 0x53, 0x05,
    0xaa, 0x50, 0x07, 0x37, 0x3e, 0x07, 0x84, 0x1c, 0x7f, 0xde, 0xae, 0x5c,
    0x8e, 0x7d, 0x44, 0xec, 0x57, 0x16, 0xf2, 0xb8, 0xb0, 0x3a, 0xda, 0x37,
    0xf0, 0x50, 0x0c, 0x0d, 0xf0, 0x1c, 0x1f, 0x04, 0x02, 0x00, 0xb3, 0xff,
    0xae, 0x0c, 0xf5, 0x1a, 0x3c, 0xb5, 0x74, 0xb2, 0x25, 0x83, 0x7a, 0x58,
    0xdc, 0x09, 0x21, 0xbd, 0xd1, 0x91, 0x13, 0xf9, 0x7c, 0xa9, 0x2f, 0xf6,
    0x94, 0x32, 0x47, 0x73, 0x22, 0xf5, 0x47, 0x01, 0x3a, 0xe5, 0xe5, 0x81,
    0x37, 0xc2, 0xda, 0xdc, 0xc8, 0xb5, 0x76, 0x34, 0x9a, 0xf3, 0xdd, 0xa7,
    0xa9, 0x44, 0x61, 0x46, 0x0f, 0xd0, 0x03, 0x0e, 0xec, 0xc8, 0xc7, 0x3e,
    0xa4, 0x75, 0x1e, 0x41, 0xe2, 0x38, 0xcd, 0x99, 0x3b, 0xea, 0x0e, 0x2f,
    0x32, 0x80, 0xbb, 0xa1, 0x18, 0x3e, 0xb3, 0x31, 0x4e, 0x54, 0x8b, 0x38,
    0x4f, 0x6d, 0xb9, 0x08, 0x6f, 0x42, 0x0d, 0x03, 0xf6, 0x0a, 0x04, 0xbf,
    0x2c, 0xb8, 0x12, 0x90, 0x24, 0x97, 0x7c, 0x79, 0x56, 0x79, 0xb0, 0x72,
    0xbc, 0xaf, 0x89, 0xaf, 0xde, 0x9a, 0x77, 0x1f, 0xd9, 0x93, 0x08, 0x10,
    0xb3, 0x8b, 0xae, 0x12, 0xdc, 0xcf, 0x3f, 0x2e, 0x55, 0x12, 0x72, 0x1f,
    0x2e, 0x6b, 0x71, 0x24, 0x50, 0x1a, 0xdd, 0xe6, 0x9f, 0x84, 0xcd, 0x87,
    0x7a, 0x58, 0x47, 0x18, 0x74, 0x08, 0xda, 0x17, 0xbc, 0x9f, 0x9a, 0xbc,
    0xe9, 0x4b, 0x7d, 0x8c, 0xec, 0x7a, 0xec, 0x3a, 0xdb, 0x85, 0x1d, 0xfa,
    0x63, 0x09, 0x43, 0x66, 0xc4, 0x64, 0xc3, 0xd2, 0xef, 0x1c, 0x18, 0x47,
    0x32, 0x15, 0xd9, 0x08, 0xdd, 0x43, 0x3b, 0x37, 0x24, 0xc2, 0xba, 0x16,
    0x12, 0xa1, 0x4d, 0x43, 0x2a, 0x65, 0xc4, 0x51, 0x50, 0x94, 0x00, 0x02,
    0x13, 0x3a, 0xe4, 0xdd, 0x71, 0xdf, 0xf8, 0x9e, 0x10, 0x31, 0x4e, 0x55,
    0x81, 0xac, 0x77, 0xd6, 0x5f, 0x11, 0x19, 0x9b, 0x04, 0x35, 0x56, 0xf1,
    0xd7, 0xa3, 0xc7, 0x6b, 0x3c, 0x11, 0x18, 0x3b, 0x59, 0x24, 0xa5, 0x09,
    0xf2, 0x8f, 0xe6, 0xed, 0x97, 0xf1, 0xfb, 0xfa, 0x9e, 0xba, 0xbf, 0x2c,
    0x1e, 0x15, 0x3c, 0x6e, 0x86, 0xe3, 0x45, 0x70, 0xea, 0xe9, 0x6f, 0xb1,
    0x86, 0x0e, 0x5e, 0x0a, 0x5a, 0x3e, 0x2a, 0xb3, 0x77, 0x1f, 0xe7, 0x1c,
    0x4e, 0x3d, 0x06, 0xfa, 0x29, 0x65, 0xdc, 0xb9, 0x99, 0xe7, 0x1d, 0x0f,
    0x80, 0x3e, 0x89, 0xd6, 0x52, 0x66, 0xc8, 0x25, 0x2e, 0x4c, 0xc9, 0x78,
    0x9c, 0x10, 0xb3, 0x6a, 0xc6, 0x15, 0x0e, 0xba, 0x94, 0xe2, 0xea, 0x78,
    0xa5, 0xfc, 0x3c, 0x53, 0x1e, 0x0a, 0x2d, 0xf4, 0xf2, 0xf7, 0x4e, 0xa7,
    0x36, 0x1d, 0x2b, 0x3d, 0x19, 0x39, 0x26, 0x0f, 0x19, 0xc2, 0x79, 0x60,
    0x52, 0x23, 0xa7, 0x08, 0xf7, 0x13, 0x12, 0xb6, 0xeb, 0xad, 0xfe, 0x6e,
    0xea, 0xc3, 0x1f, 0x66, 0xe3, 0xbc, 0x45, 0x95, 0xa6, 0x7b, 0xc8, 0x83,
    0xb1, 0x7f, 0x37, 0xd1, 0x01, 0x8c, 0xff, 0x28, 0xc3, 0x32, 0xdd, 0xef,
    0xbe, 0x6c, 0x5a, 0xa5, 0x65, 0x58, 0x21, 0x85, 0x68, 0xab, 0x98, 0x02,
    0xee, 0xce, 0xa5, 0x0f, 0xdb, 0x2f, 0x95, 0x3b, 0x2a, 0xef, 0x7d, 0xad,
    0x5b, 0x6e, 0x2f, 0x84, 0x15, 0x21, 0xb6, 0x28, 0x29, 0x07, 0x61, 0x70,
    0xec, 0xdd, 0x47, 0x75, 0x61, 0x9f, 0x15, 0x10, 0x13, 0xcc, 0xa8, 0x30,
    0xeb, 0x61, 0xbd, 0x96, 0x03, 0x34, 0xfe, 0x1e, 0xaa, 0x03, 0x63, 0xcf,
    0xb5, 0x73, 0x5c, 0x90, 0x4c, 0x70, 0xa2, 0x39, 0xd5, 0x9e, 0x9e, 0x0b,
    0xcb, 0xaa, 0xde, 0x14, 0xee, 0xcc, 0x86, 0xbc, 0x60, 0x62, 0x2c, 0xa7,
    0x9c, 0xab, 0x5c, 0xab, 0xb2, 0xf3, 0x84, 0x6e, 0x64, 0x8b, 0x1e, 0xaf,
    0x19, 0xbd, 0xf0, 0xca, 0xa0, 0x23, 0x69, 0xb9, 0x65, 0x5a, 0xbb, 0x50,
    0x40, 0x68, 0x5a, 0x32, 0x3c, 0x2a, 0xb4, 0xb3, 0x31, 0x9e, 0xe9, 0xd5,
    0xc0, 0x21, 0xb8, 0xf7, 0x9b, 0x54, 0x0b, 0x19, 0x87, 0x5f, 0xa0, 0x99,
    0x95, 0xf7, 0x99, 0x7e, 0x62, 0x3d, 0x7d, 0xa8, 0xf8, 0x37, 0x88, 0x9a,
    0x97, 0xe3, 0x2d, 0x77, 0x11, 0xed, 0x93, 0x5f, 0x16, 0x68, 0x12, 0x81,
    0x0e, 0x35, 0x88, 0x29, 0xc7, 0xe6, 0x1f, 0xd6, 0x96, 0xde, 0xdf, 0xa1,
    0x78, 0x58, 0xba, 0x99, 0x57, 0xf5, 0x84, 0xa5, 0x1b, 0x22, 0x72, 0x63,
    0x9b, 0x83, 0xc3, 0xff, 0x1a, 0xc2, 0x46, 0x96, 0xcd, 0xb3, 0x0a, 0xeb,
    0x53, 0x2e, 0x30, 0x54, 0x8f, 0xd9, 0x48, 0xe4, 0x6d, 0xbc, 0x31, 0x28,
    0x58, 0xeb, 0xf2, 0xef, 0x34, 0xc6, 0xff, 0xea, 0xfe, 0x28, 0xed, 0x61,
    0xee, 0x7c, 0x3c, 0x73, 0x5d, 0x4a, 0x14, 0xd9, 0xe8, 0x64, 0xb7, 0xe3,
    0x42, 0x10, 0x5d, 0x14, 0x20, 0x3e, 0x13, 0xe0, 0x45, 0xee, 0xe2, 0xb6,
    0xa3, 0xaa, 0xab, 0xea, 0xdb, 0x6c, 0x4f, 0x15, 0xfa, 0xcb, 0x4f, 0xd0,
    0xc7, 0x42, 0xf4, 0x42, 0xef, 0x6a, 0xbb, 0xb5, 0x65, 0x4f, 0x3b, 0x1d,
    0x41, 0xcd, 0x21, 0x05, 0xd8, 0x1e, 0x79, 0x9e, 0x86, 0x85, 0x4d, 0xc7,
    0xe4, 0x4b, 0x47, 0x6a, 0x3d, 0x81, 0x62, 0x50, 0xcf, 0x62, 0xa1, 0xf2,
    0x5b, 0x8d, 0x26, 0x46, 0xfc, 0x88, 0x83, 0xa0, 0xc1, 0xc7, 0xb6, 0xa3,
    0x7f, 0x15, 0x24, 0xc3, 0x69, 0xcb, 0x74, 0x92, 0x47, 0x84, 0x8a, 0x0b,
    0x56, 0x92, 0xb2, 0x85, 0x09, 0x5b, 0xbf, 0x00, 0xad, 0x19, 0x48, 0x9d,
    0x14, 0x62, 0xb1, 0x74, 0x23, 0x82, 0x0e, 0x00, 0x58, 0x42, 0x8d, 0x2a,
    0x0c, 0x55, 0xf5, 0xea, 0x1d, 0xad, 0xf4, 0x3e, 0x23, 0x3f, 0x70, 0x61,
    0x33, 0x72, 0xf0, 0x92, 0x8d, 0x93, 0x7e, 0x41, 0xd6, 0x5f, 0xec, 0xf1,
    0x6c, 0x22, 0x3b, 0xdb, 0x7c, 0xde, 0x37, 0x59, 0xcb, 0xee, 0x74, 0x60,
    0x40, 0x85, 0xf2, 0xa7, 0xce, 0x77, 0x32, 0x6e, 0xa6, 0x07, 0x80, 0x84,
    0x19, 0xf8, 0x50, 0x9e, 0xe8, 0xef, 0xd8, 0x55, 0x61, 0xd9, 0x97, 0x35,
    0xa9, 0x69, 0xa7, 0xaa, 0xc5, 0x0c, 0x06, 0xc2, 0x5a, 0x04, 0xab, 0xfc,
    0x80, 0x0b, 0xca, 0xdc, 0x9e, 0x44, 0x7a, 0x2e, 0xc3, 0x45, 0x34, 0x84,
    0xfd, 0xd5, 0x67, 0x05, 0x0e, 0x1e, 0x9e, 0xc9, 0xdb, 0x73, 0xdb, 0xd3,
    0x10, 0x55, 0x88, 0xcd, 0x67, 0x5f, 0xda, 0x79, 0xe3, 0x67, 0x43, 0x40,
    0xc5, 0xc4, 0x34, 0x65, 0x71, 0x3e, 0x38, 0xd8, 0x3d, 0x28, 0xf8, 0x9e,
    0xf1, 0x6d, 0xff, 0x20, 0x15, 0x3e, 0x21, 0xe7, 0x8f, 0xb0, 0x3d, 0x4a,
    0xe6, 0xe3, 0x9f, 0x2b, 0xdb, 0x83, 0xad, 0xf7, 0xe9, 0x3d, 0x5a, 0x68,
    0x94, 0x81, 0x40, 0xf7, 0xf6, 0x4c, 0x26, 0x1c, 0x94, 0x69, 0x29, 0x34,
    0x41, 0x15, 0x20, 0xf7, 0x76, 0x02, 0xd4, 0xf7, 0xbc, 0xf4, 0x6b, 0x2e,
    0xd4, 0xa2, 0x00, 0x68, 0xd4, 0x08, 0x24, 0x71, 0x33, 0x20, 0xf4, 0x6a,
    0x43, 0xb7, 0xd4, 0xb7, 0x50, 0x00, 0x61, 0xaf, 0x1e, 0x39, 0xf6, 0x2e,
    0x97, 0x24, 0x45, 0x46, 0x14, 0x21, 0x4f, 0x74, 0xbf, 0x8b, 0x88, 0x40,
    0x4d, 0x95, 0xfc, 0x1d, 0x96, 0xb5, 0x91, 0xaf, 0x70, 0xf4, 0xdd, 0xd3,
    0x66, 0xa0, 0x2f, 0x45, 0xbf, 0xbc, 0x09, 0xec, 0x03, 0xbd, 0x97, 0x85,
    0x7f, 0xac, 0x6d, 0xd0, 0x31, 0xcb, 0x85, 0x04, 0x96, 0xeb, 0x27, 0xb3,
    0x55, 0xfd, 0x39, 0x41, 0xda, 0x25, 0x47, 0xe6, 0xab, 0xca, 0x0a, 0x9a,
    0x28, 0x50, 0x78, 0x25, 0x53, 0x04, 0x29, 0xf4, 0x0a, 0x2c, 0x86, 0xda,
    0xe9, 0xb6, 0x6d, 0xfb, 0x68, 0xdc, 0x14, 0x62, 0xd7, 0x48, 0x69, 0x00,
    0x68, 0x0e, 0xc0, 0xa4, 0x27, 0xa1, 0x8d, 0xee, 0x4f, 0x3f, 0xfe, 0xa2,
    0xe8, 0x87, 0xad, 0x8c, 0xb5, 0x8c, 0xe0, 0x06, 0x7a, 0xf4, 0xd6, 0xb6,
    0xaa, 0xce, 0x1e, 0x7c, 0xd3, 0x37, 0x5f, 0xec, 0xce, 0x78, 0xa3, 0x99,
    0x40, 0x6b, 0x2a, 0x42, 0x20, 0xfe, 0x9e, 0x35, 0xd9, 0xf3, 0x85, 0xb9,
    0xee, 0x39, 0xd7, 0xab, 0x3b, 0x12, 0x4e, 0x8b, 0x1d, 0xc9, 0xfa, 0xf7,
    0x4b, 0x6d, 0x18, 0x56, 0x26, 0xa3, 0x66, 0x31, 0xea, 0xe3, 0x97, 0xb2,
    0x3a, 0x6e, 0xfa, 0x74, 0xdd, 0x5b, 0x43, 0x32, 0x68, 0x41, 0xe7, 0xf7,
    0xca, 0x78, 0x20, 0xfb, 0xfb, 0x0a, 0xf5, 0x4e, 0xd8, 0xfe, 0xb3, 0x97,
    0x45, 0x40, 0x56, 0xac, 0xba, 0x48, 0x95, 0x27, 0x55, 0x53, 0x3a, 0x3a,
    0x20, 0x83, 0x8d, 0x87, 0xfe, 0x6b, 0xa9, 0xb7, 0xd0, 0x96, 0x95, 0x4b,
    0x55, 0xa8, 0x67, 0xbc, 0xa1, 0x15, 0x9a, 0x58, 0xcc, 0xa9, 0x29, 0x63,
    0x99, 0xe1, 0xdb, 0x33, 0xa6, 0x2a, 0x4a, 0x56, 0x3f, 0x31, 0x25, 0xf9,
    0x5e, 0xf4, 0x7e, 0x1c, 0x90, 0x29, 0x31, 0x7c, 0xfd, 0xf8, 0xe8, 0x02,
    0x04, 0x27, 0x2f, 0x70, 0x80, 0xbb, 0x15, 0x5c, 0x05, 0x28, 0x2c, 0xe3,
    0x95, 0xc1, 0x15, 0x48, 0xe4, 0xc6, 0x6d, 0x22, 0x48, 0xc1, 0x13, 0x3f,
    0xc7, 0x0f, 0x86, 0xdc, 0x07, 0xf9, 0xc9, 0xee, 0x41, 0x04, 0x1f, 0x0f,
    0x40, 0x47, 0x79, 0xa4, 0x5d, 0x88, 0x6e, 0x17, 0x32, 0x5f, 0x51, 0xeb,
    0xd5, 0x9b, 0xc0, 0xd1, 0xf2, 0xbc, 0xc1, 0x8f, 0x41, 0x11, 0x35, 0x64,
    0x25, 0x7b, 0x78, 0x34, 0x60, 0x2a, 0x9c, 0x60, 0xdf, 0xf8, 0xe8, 0xa3,
    0x1f, 0x63, 0x6c, 0x1b, 0x0e, 0x12, 0xb4, 0xc2, 0x02, 0xe1, 0x32, 0x9e,
    0xaf, 0x66, 0x4f, 0xd1, 0xca, 0xd1, 0x81, 0x15, 0x6b, 0x23, 0x95, 0xe0,
    0x33, 0x3e, 0x92, 0xe1, 0x3b, 0x24, 0x0b, 0x62, 0xee, 0xbe, 0xb9, 0x22,
    0x85, 0xb2, 0xa2, 0x0e, 0xe6, 0xba, 0x0d, 0x99, 0xde, 0x72, 0x0c, 0x8c,
    0x2d, 0xa2, 0xf7, 0x28, 0xd0, 0x12, 0x78, 0x45, 0x95, 0xb7, 0x94, 0xfd,
    0x64, 0x7d, 0x08, 0x62, 0xe7, 0xcc, 0xf5, 0xf0, 0x54, 0x49, 0xa3, 0x6f,
    0x87, 0x7d, 0x48, 0xfa, 0xc3, 0x9d, 0xfd, 0x27, 0xf3, 0x3e, 0x8d, 0x1e,
    0x0a, 0x47, 0x63, 0x41, 0x99, 0x2e, 0xff, 0x74, 0x3a, 0x6f, 0x6e, 0xab,
    0xf4, 0xf8, 0xfd, 0x37, 0xa8, 0x12, 0xdc, 0x60, 0xa1, 0xeb, 0xdd, 0xf8,
    0x99, 0x1b, 0xe1, 0x4c, 0xdb, 0x6e, 0x6b, 0x0d, 0xc6, 0x7b, 0x55, 0x10,
    0x6d, 0x67, 0x2c, 0x37, 0x27, 0x65, 0xd4, 0x3b, 0xdc, 0xd0, 0xe8, 0x04,
    0xf1, 0x29, 0x0d, 0xc7, 0xcc, 0x00, 0xff, 0xa3, 0xb5, 0x39, 0x0f, 0x92,
    0x69, 0x0f, 0xed, 0x0b, 0x66, 0x7b, 0x9f, 0xfb, 0xce, 0xdb, 0x7d, 0x9c,
    0xa0, 0x91, 0xcf, 0x0b, 0xd9, 0x15, 0x5e, 0xa3, 0xbb, 0x13, 0x2f, 0x88,
    0x51, 0x5b, 0xad, 0x24, 0x7b, 0x94, 0x79, 0xbf, 0x76, 0x3b, 0xd6, 0xeb,
    0x37, 0x39, 0x2e, 0xb3, 0xcc, 0x11, 0x59, 0x79, 0x80, 0x26, 0xe2, 0x97,
    0xf4, 0x2e, 0x31, 0x2d, 0x68, 0x42, 0xad, 0xa7, 0xc6, 0x6a, 0x2b, 0x3b,
    0x12, 0x75, 0x4c, 0xcc, 0x78, 0x2e, 0xf1, 0x1c, 0x6a, 0x12, 0x42, 0x37,
    0xb7, 0x92, 0x51, 0xe7, 0x06, 0xa1, 0xbb, 0xe6, 0x4b, 0xfb, 0x63, 0x50,
    0x1a, 0x6b, 0x10, 0x18, 0x11, 0xca, 0xed, 0xfa, 0x3d, 0x25, 0xbd, 0xd8,
    0xe2, 0xe1, 0xc3, 0xc9, 0x44, 0x42, 0x16, 0x59, 0x0a, 0x12, 0x13, 0x86,
    0xd9, 0x0c, 0xec, 0x6e, 0xd5, 0xab, 0xea, 0x2a, 0x64, 0xaf, 0x67, 0x4e,
    0xda, 0x86, 0xa8, 0x5f, 0xbe, 0xbf, 0xe9, 0x88, 0x64, 0xe4, 0xc3, 0xfe,
    0x9d, 0xbc, 0x80, 0x57, 0xf0, 0xf7, 0xc0, 0x86, 0x60, 0x78, 0x7b, 0xf8,
    0x60, 0x03, 0x60, 0x4d, 0xd1, 0xfd, 0x83, 0x46, 0xf6, 0x38, 0x1f, 0xb0,
    0x77, 0x45, 0xae, 0x04, 0xd7, 0x36, 0xfc, 0xcc, 0x83, 0x42, 0x6b, 0x33,
    0xf0, 0x1e, 0xab, 0x71, 0xb0, 0x80, 0x41, 0x87, 0x3c, 0x00, 0x5e, 0x5f,
    0x77, 0xa0, 0x57, 0xbe, 0xbd, 0xe8, 0xae, 0x24, 0x55, 0x46, 0x42, 0x99,
    0xbf, 0x58, 0x2e, 0x61, 0x4e, 0x58, 0xf4, 0x8f, 0xf2, 0xdd, 0xfd, 0xa2,
    0xf4, 0x74, 0xef, 0x38, 0x87, 0x89, 0xbd, 0xc2, 0x53, 0x66, 0xf9, 0xc3,
    0xc8, 0xb3, 0x8e, 0x74, 0xb4, 0x75, 0xf2, 0x55, 0x46, 0xfc, 0xd9, 0xb9,
    0x7a, 0xeb, 0x26, 0x61, 0x8b, 0x1d, 0xdf, 0x84, 0x84, 0x6a, 0x0e, 0x79,
    0x91, 0x5f, 0x95, 0xe2, 0x46, 0x6e, 0x59, 0x8e, 0x20, 0xb4, 0x57, 0x70,
    0x8c, 0xd5, 0x55, 0x91, 0xc9, 0x02, 0xde, 0x4c, 0xb9, 0x0b, 0xac, 0xe1,
    0xbb, 0x82, 0x05, 0xd0, 0x11, 0xa8, 0x62, 0x48, 0x75, 0x74, 0xa9, 0x9e,
    0xb7, 0x7f, 0x19, 0xb6, 0xe0, 0xa9, 0xdc, 0x09, 0x66, 0x2d, 0x09, 0xa1,
    0xc4, 0x32, 0x46, 0x33, 0xe8, 0x5a, 0x1f, 0x02, 0x09, 0xf0, 0xbe, 0x8c,
    0x4a, 0x99, 0xa0, 0x25, 0x1d, 0x6e, 0xfe, 0x10, 0x1a, 0xb9, 0x3d, 0x1d,
    0x0b, 0xa5, 0xa4, 0xdf, 0xa1, 0x86, 0xf2, 0x0f, 0x28, 0x68, 0xf1, 0x69,
    0xdc, 0xb7, 0xda, 0x83, 0x57, 0x39, 0x06, 0xfe, 0xa1, 0xe2, 0xce, 0x9b,
    0x4f, 0xcd, 0x7f, 0x52, 0x50, 0x11, 0x5e, 0x01, 0xa7, 0x06, 0x83, 0xfa,
    0xa0, 0x02, 0xb5, 0xc4, 0x0d, 0xe6, 0xd0, 0x27, 0x9a, 0xf8, 0x8c, 0x27,
    0x77, 0x3f, 0x86, 0x41, 0xc3, 0x60, 0x4c, 0x06, 0x61, 0xa8, 0x06, 0xb5,
    0xf0, 0x17, 0x7a, 0x28, 0xc0, 0xf5, 0x86, 0xe0, 0x00, 0x60, 0x58, 0xaa,
    0x30, 0xdc, 0x7d, 0x62, 0x11, 0xe6, 0x9e, 0xd7, 0x23, 0x38, 0xea, 0x63,
    0x53, 0xc2, 0xdd, 0x94, 0xc2, 0xc2, 0x16, 0x34, 0xbb, 0xcb, 0xee, 0x56,
    0x90, 0xbc, 0xb6, 0xde, 0xeb, 0xfc, 0x7d, 0xa1, 0xce, 0x59, 0x1d, 0x76,
    0x6f, 0x05, 0xe4, 0x09, 0x4b, 0x7c, 0x01, 0x88, 0x39, 0x72, 0x0a, 0x3d,
    0x7c, 0x92, 0x7c, 0x24, 0x86, 0xe3, 0x72, 0x5f, 0x72, 0x4d, 0x9d, 0xb9,
    0x1a, 0xc1, 0x5b, 0xb4, 0xd3, 0x9e, 0xb8, 0xfc, 0xed, 0x54, 0x55, 0x78,
    0x08, 0xfc, 0xa5, 0xb5, 0xd8, 0x3d, 0x7c, 0xd3, 0x4d, 0xad, 0x0f, 0xc4,
    0x1e, 0x50, 0xef, 0x5e, 0xb1, 0x61, 0xe6, 0xf8, 0xa2, 0x85, 0x14, 0xd9,
    0x6c, 0x51, 0x13, 0x3c, 0x6f, 0xd5, 0xc7, 0xe7, 0x56, 0xe1, 0x4e, 0xc4,
    0x36, 0x2a, 0xbf, 0xce, 0xdd, 0xc6, 0xc8, 0x37, 0xd7, 0x9a, 0x32, 0x34,
    0x92, 0x63, 0x82, 0x12, 0x67, 0x0e, 0xfa, 0x8e, 0x40, 0x60, 0x00, 0xe0,
    0x3a, 0x39, 0xce, 0x37, 0xd3, 0xfa, 0xf5, 0xcf, 0xab, 0xc2, 0x77, 0x37,
    0x5a, 0xc5, 0x2d, 0x1b, 0x5c, 0xb0, 0x67, 0x9e, 0x4f, 0xa3, 0x37, 0x42,
    0xd3, 0x82, 0x27, 0x40, 0x99, 0xbc, 0x9b, 0xbe, 0xd5, 0x11, 0x8e, 0x9d,
    0xbf, 0x0f, 0x73, 0x15, 0xd6, 0x2d, 0x1c, 0x7e, 0xc7, 0x00, 0xc4, 0x7b,
    0xb7, 0x8c, 0x1b, 0x6b, 0x21, 0xa1, 0x90, 0x45, 0xb2, 0x6e, 0xb1, 0xbe,
    0x6a, 0x36, 0x6e, 0xb4, 0x57, 0x48, 0xab, 0x2f, 0xbc, 0x94, 0x6e, 0x79,
    0xc6, 0xa3, 0x76, 0xd2, 0x65, 0x49, 0xc2, 0xc8, 0x53, 0x0f, 0xf8, 0xee,
    0x46, 0x8d, 0xde, 0x7d, 0xd5, 0x73, 0x0a, 0x1d, 0x4c, 0xd0, 0x4d, 0xc6,
    0x29, 0x39, 0xbb, 0xdb, 0xa9, 0xba, 0x46, 0x50, 0xac, 0x95, 0x26, 0xe8,
    0xbe, 0x5e, 0xe3, 0x04, 0xa1, 0xfa, 0xd5, 0xf0, 0x6a, 0x2d, 0x51, 0x9a,
    0x63, 0xef, 0x8c, 0xe2, 0x9a, 0x86, 0xee, 0x22, 0xc0, 0x89, 0xc2, 0xb8,
    0x43, 0x24, 0x2e, 0xf6, 0xa5, 0x1e, 0x03, 0xaa, 0x9c, 0xf2, 0xd0, 0xa4,
    0x83, 0xc0, 0x61, 0xba, 0x9b, 0xe9, 0x6a, 0x4d, 0x8f, 0xe5, 0x15, 0x50,
    0xba, 0x64, 0x5b, 0xd6, 0x28, 0x26, 0xa2, 0xf9, 0xa7, 0x3a, 0x3a, 0xe1,
    0x4b, 0xa9, 0x95, 0x86, 0xef, 0x55, 0x62, 0xe9, 0xc7, 0x2f, 0xef, 0xd3,
    0xf7, 0x52, 0xf7, 0xda, 0x3f, 0x04, 0x6f, 0x69, 0x77, 0xfa, 0x0a, 0x59,
    0x80, 0xe4, 0xa9, 0x15, 0x87, 0xb0, 0x86, 0x01, 0x9b, 0x09, 0xe6, 0xad,
    0x3b, 0x3e, 0xe5, 0x93, 0xe9, 0x90, 0xfd, 0x5a, 0x9e, 0x34, 0xd7, 0x97,
    0x2c, 0xf0, 0xb7, 0xd9, 0x02, 0x2b, 0x8b, 0x51, 0x96, 0xd5, 0xac, 0x3a,
    0x01, 0x7d, 0xa6, 0x7d, 0xd1, 0xcf, 0x3e, 0xd6, 0x7c, 0x7d, 0x2d, 0x28,
    0x1f, 0x9f, 0x25, 0xcf, 0xad, 0xf2, 0xb8, 0x9b, 0x5a, 0xd6, 0xb4, 0x72,
    0x5a, 0x88, 0xf5, 0x4c, 0xe0, 0x29, 0xac, 0x71, 0xe0, 0x19, 0xa5, 0xe6,
    0x47, 0xb0, 0xac, 0xfd, 0xed, 0x93, 0xfa, 0x9b, 0xe8, 0xd3, 0xc4, 0x8d,
    0x28, 0x3b, 0x57, 0xcc, 0xf8, 0xd5, 0x66, 0x29, 0x79, 0x13, 0x2e, 0x28,
    0x78, 0x5f, 0x01, 0x91, 0xed, 0x75, 0x60, 0x55, 0xf7, 0x96, 0x0e, 0x44,
    0xe3, 0xd3, 0x5e, 0x8c, 0x15, 0x05, 0x6d, 0xd4, 0x88, 0xf4, 0x6d, 0xba,
    0x03, 0xa1, 0x61, 0x25, 0x05, 0x64, 0xf0, 0xbd, 0xc3, 0xeb, 0x9e, 0x15,
    0x3c, 0x90, 0x57, 0xa2, 0x97, 0x27, 0x1a, 0xec, 0xa9, 0x3a, 0x07, 0x2a,
    0x1b, 0x3f, 0x6d, 0x9b, 0x1e, 0x63, 0x21, 0xf5, 0xf5, 0x9c, 0x66, 0xfb,
    0x26, 0xdc, 0xf3, 0x19, 0x75, 0x33, 0xd9, 0x28, 0xb1, 0x55, 0xfd, 0xf5,
    0x03, 0x56, 0x34, 0x82, 0x8a, 0xba, 0x3c, 0xbb, 0x28, 0x51, 0x77, 0x11,
    0xc2, 0x0a, 0xd9, 0xf8, 0xab, 0xcc, 0x51, 0x67, 0xcc, 0xad, 0x92, 0x5f,
    0x4d, 0xe8, 0x17, 0x51, 0x38, 0x30, 0xdc, 0x8e, 0x37, 0x9d, 0x58, 0x62,
    0x93, 0x20, 0xf9, 0x91, 0xea, 0x7a, 0x90, 0xc2, 0xfb, 0x3e, 0x7b, 0xce,
    0x51, 0x21, 0xce, 0x64, 0x77, 0x4f, 0xbe, 0x32, 0xa8, 0xb6, 0xe3, 0x7e,
    0xc3, 0x29, 0x3d, 0x46, 0x48, 0xde, 0x53, 0x69, 0x64, 0x13, 0xe6, 0x80,
    0xa2, 0xae, 0x08, 0x10, 0xdd, 0x6d, 0xb2, 0x24, 0x69, 0x85, 0x2d, 0xfd,
    0x09, 0x07, 0x21, 0x66, 0xb3, 0x9a, 0x46, 0x0a, 0x64, 0x45, 0xc0, 0xdd,
    0x58, 0x6c, 0xde, 0xcf, 0x1c, 0x20, 0xc8, 0xae, 0x5b, 0xbe, 0xf7, 0xdd,
    0x1b, 0x58, 0x8d, 0x40, 0xcc, 0xd2, 0x01, 0x7f, 0x6b, 0xb4, 0xe3, 0xbb,
    0xdd, 0xa2, 0x6a, 0x7e, 0x3a, 0x59, 0xff, 0x45, 0x3e, 0x35, 0x0a, 0x44,
    0xbc, 0xb4, 0xcd, 0xd5, 0x72, 0xea, 0xce, 0xa8, 0xfa, 0x64, 0x84, 0xbb,
    0x8d, 0x66, 0x12, 0xae, 0xbf, 0x3c, 0x6f, 0x47, 0xd2, 0x9b, 0xe4, 0x63,
    0x54, 0x2f, 0x5d, 0x9e, 0xae, 0xc2, 0x77, 0x1b, 0xf6, 0x4e, 0x63, 0x70,
    0x74, 0x0e, 0x0d, 0x8d, 0xe7, 0x5b, 0x13, 0x57, 0xf8, 0x72, 0x16, 0x71,
    0xaf, 0x53, 0x7d, 0x5d, 0x40, 0x40, 0xcb, 0x08, 0x4e, 0xb4, 0xe2, 0xcc,
    0x34, 0xd2, 0x46, 0x6a, 0x01, 0x15, 0xaf, 0x84, 0xe1, 0xb0, 0x04, 0x28,
    0x95, 0x98, 0x3a, 0x1d, 0x06, 0xb8, 0x9f, 0xb4, 0xce, 0x6e, 0xa0, 0x48,
    0x6f, 0x3f, 0x3b, 0x82, 0x35, 0x20, 0xab, 0x82, 0x01, 0x1a, 0x1d, 0x4b,
    0x27, 0x72, 0x27, 0xf8, 0x61, 0x15, 0x60, 0xb1, 0xe7, 0x93, 0x3f, 0xdc,
    0xbb, 0x3a, 0x79, 0x2b, 0x34, 0x45, 0x25, 0xbd, 0xa0, 0x88, 0x39, 0xe1,
    0x51, 0xce, 0x79, 0x4b, 0x2f, 0x32, 0xc9, 0xb7, 0xa0, 0x1f, 0xba, 0xc9,
    0xe0, 0x1c, 0xc8, 0x7e, 0xbc, 0xc7, 0xd1, 0xf6, 0xcf, 0x01, 0x11, 0xc3,
    0xa1, 0xe8, 0xaa, 0xc7, 0x1a, 0x90, 0x87, 0x49, 0xd4, 0x4f, 0xbd, 0x9a,
    0xd0, 0xda, 0xde, 0xcb, 0xd5, 0x0a, 0xda, 0x38, 0x03, 0x39, 0xc3, 0x2a,
    0xc6, 0x91, 0x36, 0x67, 0x8d, 0xf9, 0x31, 0x7c, 0xe0, 0xb1, 0x2b, 0x4f,
    0xf7, 0x9e, 0x59, 0xb7, 0x43, 0xf5, 0xbb, 0x3a, 0xf2, 0xd5, 0x19, 0xff,
    0x27, 0xd9, 0x45, 0x9c, 0xbf, 0x97, 0x22, 0x2c, 0x15, 0xe6, 0xfc, 0x2a,
    0x0f, 0x91, 0xfc, 0x71, 0x9b, 0x94, 0x15, 0x25, 0xfa, 0xe5, 0x93, 0x61,
    0xce, 0xb6, 0x9c, 0xeb, 0xc2, 0xa8, 0x64, 0x59, 0x12, 0xba, 0xa8, 0xd1,
    0xb6, 0xc1, 0x07, 0x5e, 0xe3, 0x05, 0x6a, 0x0c, 0x10, 0xd2, 0x50, 0x65,
    0xcb, 0x03, 0xa4, 0x42, 0xe0, 0xec, 0x6e, 0x0e, 0x16, 0x98, 0xdb, 0x3b,
    0x4c, 0x98, 0xa0, 0xbe, 0x32, 0x78, 0xe9, 0x64, 0x9f, 0x1f, 0x95, 0x32,
    0xe0, 0xd3, 0x92, 0xdf, 0xd3, 0xa0, 0x34, 0x2b, 0x89, 0x71, 0xf2, 0x1e,
    0x1b, 0x0a, 0x74, 0x41, 0x4b, 0xa3, 0x34, 0x8c, 0xc5, 0xbe, 0x71, 0x20,
    0xc3, 0x76, 0x32, 0xd8, 0xdf, 0x35, 0x9f, 0x8d, 0x9b, 0x99, 0x2f, 0x2e,
    0xe6, 0x0b, 0x6f, 0x47, 0x0f, 0xe3, 0xf1, 0x1d, 0xe5, 0x4c, 0xda, 0x54,
    0x1e, 0xda, 0xd8, 0x91, 0xce, 0x62, 0x79, 0xcf, 0xcd, 0x3e, 0x7e, 0x6f,
    0x16, 0x18, 0xb1, 0x66, 0xfd, 0x2c, 0x1d, 0x05, 0x84, 0x8f, 0xd2, 0xc5,
    0xf6, 0xfb, 0x22, 0x99, 0xf5, 0x23, 0xf3, 0x57, 0xa6, 0x32, 0x76, 0x23,
    0x93, 0xa8, 0x35, 0x31, 0x56, 0xcc, 0xcd, 0x02, 0xac, 0xf0, 0x81, 0x62,
    0x5a, 0x75, 0xeb, 0xb5, 0x6e, 0x16, 0x36, 0x97, 0x88, 0xd2, 0x73, 0xcc,
    0xde, 0x96, 0x62, 0x92, 0x81, 0xb9, 0x49, 0xd0, 0x4c, 0x50, 0x90, 0x1b,
    0x71, 0xc6, 0x56, 0x14, 0xe6, 0xc6, 0xc7, 0xbd, 0x32, 0x7a, 0x14, 0x0a,
    0x45, 0xe1, 0xd0, 0x06, 0xc3, 0xf2, 0x7b, 0x9a, 0xc9, 0xaa, 0x53, 0xfd,
    0x62, 0xa8, 0x0f, 0x00, 0xbb, 0x25, 0xbf, 0xe2, 0x35, 0xbd, 0xd2, 0xf6,
    0x71, 0x12, 0x69, 0x05, 0xb2, 0x04, 0x02, 0x22, 0xb6, 0xcb, 0xcf, 0x7c,
    0xcd, 0x76, 0x9c, 0x2b, 0x53, 0x11, 0x3e, 0xc0, 0x16, 0x40, 0xe3, 0xd3,
    0x38, 0xab, 0xbd, 0x60, 0x25, 0x47, 0xad, 0xf0, 0xba, 0x38, 0x20, 0x9c,
    0xf7, 0x46, 0xce, 0x76, 0x77, 0xaf, 0xa1, 0xc5, 0x20, 0x75, 0x60, 0x60,
    0x85, 0xcb, 0xfe, 0x4e, 0x8a, 0xe8, 0x8d, 0xd8, 0x7a, 0xaa, 0xf9, 0xb0,
    0x4c, 0xf9, 0xaa, 0x7e, 0x19, 0x48, 0xc2, 0x5c, 0x02, 0xfb, 0x8a, 0x8c,
    0x01, 0xc3, 0x6a, 0xe4, 0xd6, 0xeb, 0xe1, 0xf9, 0x90, 0xd4, 0xf8, 0x69,
    0xa6, 0x5c, 0xde, 0xa0, 0x3f, 0x09, 0x25, 0x2d, 0xc2, 0x08, 0xe6, 0x9f,
    0xb7, 0x4e, 0x61, 0x32, 0xce, 0x77, 0xe2, 0x5b, 0x57, 0x8f, 0xdf, 0xe3,
    0x3a, 0xc3, 0x72, 0xe6, 0xb8, 0x3a, 0xcb, 0x02, 0x20, 0x02, 0x39, 0x7a,
    0x6e, 0xc6, 0xfb, 0x5b, 0xff, 0xcf, 0xd4, 0xdd, 0x4c, 0xbf, 0x5e, 0xd1,
    0xf4, 0x3f, 0xe5, 0x82, 0x3e, 0xf4, 0xe8, 0x23, 0x2d, 0x15, 0x2a, 0xf0,
    0xe7, 0x18, 0xc9, 0x70, 0x59, 0xbd, 0x98, 0x20, 0x1f, 0x4a, 0x9d, 0x62,
    0xe7, 0xa5, 0x29, 0xba, 0x89, 0xe1, 0x24, 0x8d, 0x3b, 0xf8, 0x86, 0x56,
    0xc5, 0x11, 0x4d, 0x0e, 0xbc, 0x4c, 0xee, 0x16, 0x03, 0x4d, 0x8a, 0x39,
    0x20, 0xe4, 0x78, 0x82, 0xe9, 0xae, 0x8f, 0xbd, 0xe3, 0xab, 0xdc, 0x1f,
    0x6d, 0xa5, 0x1e, 0x52, 0x5d, 0xb2, 0xba, 0xe1, 0x01, 0xf8, 0x6e, 0x7a,
    0x6d, 0x9c, 0x68, 0xa9, 0x27, 0x08, 0xfc, 0xd9, 0x29, 0x3c, 0xbc, 0x0c,
    0xb0, 0x3c, 0x86, 0xf8, 0xa8, 0xad, 0x2c, 0x2f, 0x00, 0x42, 0x4e, 0xeb,
    0xca, 0xcb, 0x45, 0x2d, 0x89, 0xcc, 0x71, 0xfc, 0xd5, 0x9c, 0x7f, 0x91,
    0x7f, 0x06, 0x22, 0xbc, 0x6d, 0x8a, 0x08, 0xb1, 0x83, 0x4d, 0x21, 0x32,
    0x68, 0x84, 0xca, 0x82, 0xe3, 0xaa, 0xcb, 0xf3, 0x77, 0x86, 0xf2, 0xfa,
    0x2c, 0xab, 0x6e, 0x3d, 0xce, 0x53, 0x5a, 0xd1, 0xf2, 0x0a, 0xc6, 0x07,
    0xc6, 0xb8, 0xe1, 0x4f, 0x5e, 0xb4, 0x38, 0x8e, 0x77, 0x50, 0x14, 0xa6,
    0x65, 0x66, 0x65, 0xf7, 0xb6, 0x4a, 0x43, 0xe4, 0xba, 0x38, 0x3d, 0x01,
    0xb2, 0xe4, 0x10, 0x79, 0x8e, 0xb2, 0x98, 0x6f, 0x90, 0x9e, 0x0c, 0xa4,
    0x1f, 0x7b, 0x37, 0x77, 0x2c, 0x12, 0x60, 0x30, 0x85, 0x08, 0x87, 0x18,
    0xc4, 0xe7, 0xd1, 0xbd, 0x40, 0x65, 0xff, 0xce, 0x83, 0x92, 0xfd, 0x8a,
    0xaa, 0x36, 0xd1, 0x2b, 0xb4, 0xc8, 0xc9, 0xd0, 0x99, 0x4f, 0xb0, 0xb7,
    0x14, 0xf9, 0x68, 0x18, 0xf9, 0xa5, 0x39, 0x98, 0xa0, 0xa1, 0x78, 0xc6,
    0x26, 0x84, 0xa8, 0x1e, 0x8a, 0xe9, 0x72, 0xf6, 0xb8, 0x42, 0x5e, 0xb6,
    0x7a, 0x29, 0xd4, 0x86, 0x55, 0x1b, 0xd7, 0x19, 0xaf, 0x32, 0xc1, 0x89,
    0xd5, 0x14, 0x55, 0x05, 0xdc, 0x81, 0xd5, 0x3e, 0x48, 0x42, 0x4e, 0xda,
    0xb7, 0x96, 0xef, 0x46, 0xa0, 0x49, 0x8f, 0x03, 0x66, 0x7d, 0xee, 0xde,
    0x03, 0xac, 0x0a, 0xb3, 0xc4, 0x97, 0x73, 0x3d, 0x53, 0x16, 0xa8, 0x91,
    0x30, 0xa8, 0x8f, 0xcc, 0x96, 0x04, 0x44, 0x0a, 0xce, 0xeb, 0x89, 0x3a,
    0x77, 0x25, 0xb8, 0x2b, 0x0e, 0x1e, 0xf6, 0x9d, 0x30, 0x2a, 0x5c, 0x8e,
    0xe7, 0xb8, 0x4d, 0xef, 0x5a, 0x31, 0xb0, 0x96, 0xc9, 0xeb, 0xf8, 0x8d,
    0x51, 0x2d, 0x78, 0x8e, 0x7e, 0x40, 0x02, 0xee, 0x87, 0xe0, 0x2a, 0xf6,
    0xc3, 0x58, 0xa1, 0xbb, 0x02, 0xe8, 0xd7, 0xaf, 0xdf, 0x9f, 0xb0, 0xe7,
    0x79, 0x0e, 0x94, 0x2a, 0x3b, 0x3c, 0x1a, 0xba, 0xc6, 0xff, 0xa7, 0xaf,
    0x9d, 0xf7, 0x96, 0xf9, 0x32, 0x1b, 0xb9, 0x94, 0x01, 0x74, 0xa8, 0xa8,
    0xed, 0x22, 0x16, 0x2c, 0xcf, 0xf1, 0xbb, 0x99, 0xda, 0xa8, 0xd5, 0x51,
    0xa4, 0xd5, 0xe4, 0x4b, 0xec, 0xdd, 0xe3, 0xec, 0xa8, 0x0d, 0xc5, 0x09,
    0x03, 0x93, 0xee, 0xf2, 0x72, 0x52, 0x3d, 0x31, 0xd4, 0x8e, 0x3a, 0x1c,
    0x22, 0x4e, 0xb6, 0x5e, 0x60, 0x52, 0xc3, 0xa4, 0x21, 0x09, 0xc3, 0x2f,
    0x05, 0x2e, 0xe3, 0x88, 0xed, 0x9f, 0x7e, 0xa9, 0x91, 0xc6, 0x2f, 0x97,
    0x77, 0xb5, 0x5b, 0xa0, 0x15, 0x0c, 0xbc, 0xa3, 0x3a, 0xec, 0x65, 0x25,
    0xdf, 0x31, 0x83, 0x83, 0x43, 0xa9, 0xce, 0x26, 0x93, 0x62, 0xad, 0x8b,
    0x01, 0x34, 0x14, 0x0b, 0x8d, 0xf5, 0xcf, 0x81, 0x1e, 0x9f, 0xf5, 0x59,
    0x16, 0x7f, 0x05, 0x64, 0x38, 0x12, 0xf4, 0xe0, 0x58, 0x8a, 0x52, 0xb0,
    0xcb, 0xb8, 0xe9, 0x44, 0xef, 0x5b, 0x16, 0xa3, 0x73, 0xc4, 0xed, 0xa1,
    0x7d, 0xfc, 0xfe, 0xea, 0xf5, 0x4b, 0xcb, 0xbe, 0x87, 0x73, 0xe3, 0xd2,
    0xc5, 0x31, 0xdc, 0xd0, 0x55, 0xc4, 0x67, 0x29, 0x52, 0x77, 0x4f, 0x3a,
    0x57, 0xca, 0x6b, 0xc0, 0x46, 0x7d, 0x3a, 0x3b, 0x24, 0x77, 0x84, 0x25,
    0xb7, 0x99, 0x1e, 0x9a, 0xdd, 0x82, 0x5c, 0x26, 0xe4, 0x52, 0xc8, 0xee,
    0xfc, 0xac, 0xde, 0x1e, 0x84, 0x83, 0x3a, 0xf3, 0x61, 0x21, 0x1d, 0x03,
    0x17, 0x32, 0xc1, 0x31, 0xcc, 0xad, 0xb2, 0x47, 0xe6, 0x06, 0xbe, 0x8c,
    0x71, 0x2b, 0x39, 0xf1, 0x88, 0xb4, 0xef, 0x39, 0x3a, 0x9f, 0xcd, 0xc5,
    0xc5, 0x75, 0x51, 0x69, 0x1f, 0xf6, 0x99, 0x4f, 0x39, 0x82, 0x9c, 0xb0,
    0x11, 0x01, 0x65, 0x73, 0x33, 0x43, 0xcb, 0xeb, 0x61, 0xd3, 0xd0, 0xb4,
    0x44, 0xf3, 0x0a, 0xef, 0xa8, 0xae, 0x73, 0x75, 0x2a, 0x3a, 0x1c, 0x9d,
    0xb4, 0xb7, 0x09, 0x14, 0xd6, 0xab, 0x25, 0x0c, 0x85, 0x3b, 0x73, 0x28,
    0x49, 0x5f, 0x94, 0x8f, 0xd2, 0xa4, 0xed, 0x8e, 0x6c, 0xf7, 0x51, 0xe4,
    0xc3, 0x20, 0xbb, 0x75, 0xd9, 0xca, 0xa0, 0xb3, 0x8b, 0xa5, 0x62, 0x62,
    0x4e, 0x84, 0xb0, 0x3f, 0xee, 0xa8, 0x07, 0x6e, 0x74, 0xa0, 0x7f, 0xe5,
    0x80, 0x39, 0xe0, 0x0c, 0x36, 0xff, 0xda, 0xf8, 0x03, 0x73, 0x13, 0x58,
    0xb9, 0xe6, 0x71, 0xb9, 0xda, 0xc4, 0xce, 0x1c, 0xb2, 0x5b, 0x10, 0xed,
    0x4d, 0xd3, 0xd5, 0xb1, 0xfc, 0xf2, 0xb4, 0x80, 0x46, 0x34, 0xf5, 0x79,
    0x25, 0xea, 0xc4, 0x00, 0xa9, 0xac, 0x55, 0xea, 0x72, 0x89, 0x32, 0xdf,
    0x06, 0x04, 0x1d, 0x05, 0x5d, 0x31, 0xf5, 0x02, 0xc5, 0x39, 0xc2, 0xe3,
    0x2b, 0x89, 0xd9, 0xdb, 0x5b, 0xcc, 0x0a, 0x98, 0xc0, 0x5b, 0xfd, 0x6f,
    0x1b, 0x25, 0x06, 0x22, 0x2e, 0x21, 0xbe, 0x0e, 0x60, 0x97, 0x3b, 0x04,
    0xec, 0xd5, 0x4a, 0x67, 0xb5, 0x4f, 0xe6, 0x38, 0xa6, 0xed, 0x66, 0x15,
    0x98, 0x1a, 0x91, 0x0a, 0x5d, 0x92, 0x92, 0x8d, 0xac, 0x6f, 0xc6, 0x97,
    0xe7, 0x3c, 0x63, 0xad, 0x45, 0x6e, 0xdf, 0x5f, 0x45, 0x7a, 0x81, 0x45,
    0x51, 0x87, 0x5a, 0x64, 0xcd, 0x30, 0x99, 0xf1, 0x69, 0xb5, 0xf1, 0x8a,
    0x8c, 0x73, 0xee, 0x0b, 0x5e, 0x57, 0x36, 0x8f, 0x6c, 0x79, 0xf4, 0xbb,
    0x7a, 0x59, 0x59, 0x26, 0xaa, 0xb4, 0x9e, 0xc6, 0x8a, 0xc8, 0xfc, 0xfb,
    0x80, 0x00, 0x00
};

uint32_t readDigits(const uint8_t *&pi) {
    uint32_t value = *(const uint32_t *)pi;
    pi += sizeof(uint32_t);
    return value;
}

Blowfish::Blowfish() {
    initialized = false;
}

Blowfish::Blowfish(uint8_t key[], uint8_t keySize) {
    initialized = false;
    initialize(key, keySize);
}

uint32_t Blowfish::F(uint32_t value) {
    uint8_t a = (value >> 24) & 0xFF,
            b = (value >> 16) & 0xFF,
            c = (value >> 8)  & 0xFF,
            d = value & 0xFF;
    
    return ((S[0][a] + S[1][b]) ^ S[2][c]) + S[3][d];
}

uint32_t Blowfish::rearrangeBytes(uint32_t value) {
    static const uint32_t check = 1;
    if (*(uint8_t *)&check != 1) {
        // big endian, don't change anything
        return value;
    }
    
    // little endian, rearrange bytes
    return ((value & 0xFF)       << 24) |
           ((value & 0xFF00)     << 8)  |
           ((value & 0xFF0000)   >> 8)  |
           ((value & 0xFF000000) >> 24);
}

void Blowfish::initialize(uint8_t key[], uint8_t keySize) {
    const uint8_t *pi = piBinaryDigits;
    for (int i = 0; i < BLOWFISH_PN; i++) {
        P[i] = rearrangeBytes(readDigits(pi));
    }
    
    for (int i = 0; i < BLOWFISH_SN; i++) {
        for (int j = 0; j < BLOWFISH_SNN; j++) {
            S[i][j] = rearrangeBytes(readDigits(pi));
        }
    }
    
    int j = 0;
    for (int i = 0; i < BLOWFISH_PN; i++) {
        uint32_t keyPart = 0;
        for (int k = 0; k < 4; k++, j++) {
            keyPart = (keyPart << 8) | key[j % keySize];
        }
        P[i] ^= keyPart;
    }
    
    uint32_t xLeft = 0, xRight = 0;
    
    for (int i = 0; i < BLOWFISH_PN; ) {
        encryptInternal(xLeft, xRight);
        P[i++] = xLeft;
        P[i++] = xRight;
    }
    
    for (int i = 0; i < BLOWFISH_SN; i++) {
        for (int j = 0; j < BLOWFISH_SNN; ) {
            encryptInternal(xLeft, xRight);
            S[i][j++] = xLeft;
            S[i][j++] = xRight;
        }
    }
    
    initialized = true;
}

void Blowfish::encryptInternal(uint32_t& xLeft, uint32_t& xRight) {
    for (int i = 0; i < BLOWFISH_ROUNDS; i++) {
        xLeft ^= P[i];
        xRight = F(xLeft) ^ xRight;
        swap(xLeft, xRight);
    }
    
    swap(xLeft, xRight);
    xRight ^= P[16];
    xLeft ^= P[17];
}

void Blowfish::decryptInternal(uint32_t& xLeft, uint32_t& xRight) {
    for (int i = BLOWFISH_ROUNDS + 1; i > 1; i--) {
        xLeft ^= P[i];
        xRight = F(xLeft) ^ xRight;
        swap(xLeft, xRight);
    }
    
    swap(xLeft, xRight);
    xRight ^= P[1];
    xLeft ^= P[0];
}

bool Blowfish::encrypt(uint32_t& xLeft, uint32_t& xRight) {
    if (!initialized) {
        return false;
    }
    encryptInternal(xLeft, xRight);
    return true;
}

bool Blowfish::decrypt(uint32_t& xLeft, uint32_t& xRight) {
    if (!initialized) {
        return false;
    }
    decryptInternal(xLeft, xRight);
    return true;
}

void Blowfish::rearrangeHalves(BlowfishX& x) {
    x.halves[0] = Blowfish::rearrangeBytes(x.halves[0]);
    x.halves[1] = Blowfish::rearrangeBytes(x.halves[1]);
}

// Adds padding at the end if size % 8 != 0
size_t Blowfish::encryptBuffer(uint8_t *buffer, size_t size, bool lastBuffer) {
    if (!initialized) {
        return 0;
    }
    
    size_t bytesWritten = 0;
    bool paddingAdded = false;
    
    BlowfishX *block = (BlowfishX *)buffer;
    for (size_t start = 0; start < size; start += BLOWFISH_X_SIZE, block++) {
        if (start + BLOWFISH_X_SIZE > size) {
            // not a full block, add padding to it
            uint8_t paddingSize = BLOWFISH_X_SIZE - (size - start);
            for (uint8_t i = BLOWFISH_X_SIZE - paddingSize; i < BLOWFISH_X_SIZE; i++) {
                block->bytes[i] = paddingSize;
            }
            paddingAdded = true;
        }
        
        rearrangeHalves(*block);
        encryptInternal(block->halves[0], block->halves[1]);
        rearrangeHalves(*block);
        
        bytesWritten += BLOWFISH_X_SIZE;
    }
    
    if (lastBuffer && !paddingAdded) {   
        block->whole = 0x0808080808080808;
        encryptInternal(block->halves[0], block->halves[1]);
        rearrangeHalves(*block);
        
        bytesWritten += BLOWFISH_X_SIZE;
    }
    
    return bytesWritten;
}

size_t Blowfish::decryptBuffer(uint8_t *buffer, size_t size, bool lastBuffer) {
    if (!initialized) {
        return 0;
    }
    
    size_t bytesWritten = 0;
    
    BlowfishX *block = (BlowfishX *)buffer;
    for (size_t start = 0; start < size; start += BLOWFISH_X_SIZE, block++) {
        rearrangeHalves(*block);
        decryptInternal(block->halves[0], block->halves[1]);
        rearrangeHalves(*block);
        
        // Last block and last buffer => check for and remove padding
        if (lastBuffer && start + BLOWFISH_X_SIZE >= size) {
            uint8_t paddingByte = block->bytes[7];
            if (paddingByte >= 1 && paddingByte <= BLOWFISH_X_SIZE) {
                for (uint8_t i = BLOWFISH_X_SIZE - paddingByte; i < BLOWFISH_X_SIZE; i++) {
                    if (block->bytes[i] != paddingByte) {
                        paddingByte = 0;
                        // Alternatively, this should indicate that decryption failed
                        break;
                    }
                }
                // Subtracting padding size from the total
                bytesWritten -= paddingByte;
            }
        }
        
        bytesWritten += BLOWFISH_X_SIZE;
    }
    
    return bytesWritten;
}
