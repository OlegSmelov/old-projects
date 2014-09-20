#ifndef __BLOWFISH_H
#define __BLOWFISH_H

#include <stdint.h>

#define BLOWFISH_PN  18
#define BLOWFISH_SN  4
#define BLOWFISH_SNN 256
#define BLOWFISH_N   16
#define BLOWFISH_X_SIZE 8
#define BLOWFISH_ROUNDS 16

#define BLOWFISH_KEY_BYTES_MIN 4
#define BLOWFISH_KEY_BYTES_MAX 56

typedef union {
    uint8_t  bytes[BLOWFISH_X_SIZE];
    uint32_t halves[BLOWFISH_X_SIZE / 4];
    uint64_t whole;
} BlowfishX;

class Blowfish {
    bool initialized;
    uint32_t P[BLOWFISH_PN];
    uint32_t S[BLOWFISH_SN][BLOWFISH_SNN];
    
    uint32_t F(uint32_t value);
    void encryptInternal(uint32_t& xLeft, uint32_t& xRight);
    void decryptInternal(uint32_t& xLeft, uint32_t& xRight);
    
    void rearrangeHalves(BlowfishX& x);
public:
    Blowfish();
    Blowfish(uint8_t key[], uint8_t keySize);
    
    void initialize(uint8_t key[], uint8_t keySize);
    bool encrypt(uint32_t& xLeft, uint32_t& xRight);
    bool decrypt(uint32_t& xLeft, uint32_t& xRight);
    size_t encryptBuffer(uint8_t *buffer, size_t size, bool lastBuffer = false);
    size_t decryptBuffer(uint8_t *buffer, size_t size, bool lastBuffer = false);
    
    bool isInitialized() { return initialized; }
    
    static uint32_t rearrangeBytes(uint32_t value);
};

#endif
