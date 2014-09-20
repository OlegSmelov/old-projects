#include <iostream>
#include <fstream>
#include <string>
#include <cstdio>

#include "blowfish.h"

using namespace std;

size_t fsize(FILE *file) {
    size_t currentPosition = ftell(file);
    fseek(file, 0, SEEK_END);
    size_t fileSize = ftell(file);
    fseek(file, currentPosition, SEEK_SET);
    return fileSize;
}

void printUsage(char *programName) {
    cerr << "Usage: " << programName << " -k keyfile [-e] [-d] [-f inputfile] [-o outputfile]" << endl;
    cerr << endl;
    cerr << "Arguments:" << endl;
    cerr << "    -e  Encrypt (default)" << endl;
    cerr << "    -d  Decrypt" << endl;
    cerr << "    -k  Key file" << endl;
    cerr << "    -f  Input file (or - for stdin)" << endl;
    cerr << "    -o  Output file (or - for stdout)" << endl;
}

int main(int argc, char* argv[]) {  
    string inputFilename, outputFilename, keyFilename;
    bool encrypt = true;
    
    for (int i = 1; i < argc; i++) {
        string argument(argv[i]);
        
        if (i + 1 < argc) {
            if (argument == "-f") {
                inputFilename = argv[++i];
            } else if (argument == "-o") {
                outputFilename = argv[++i];
            } else if (argument == "-k") {
                keyFilename = argv[++i];
            }
        }
        
        if (argument == "-d") {
            encrypt = false;
        } else if (argument == "-e") {
            encrypt = true;
        }
    }
    
    if (keyFilename == "") {
        printUsage(argv[0]);
        return -1;
    }
    
    FILE *inputFile = stdin;
    if (inputFilename != "" && inputFilename != "-") {
        inputFile = fopen(inputFilename.c_str(), "rb");
        if (inputFile == NULL) {
            cerr << "Could not open input file!" << endl;
            return -1;
        }
    }
    
    FILE *outputFile = stdout;
    if (outputFilename != "" && outputFilename != "-") {
        outputFile = fopen(outputFilename.c_str(), "wb");
        if (outputFile == NULL) {
            cerr << "Could not open output file!" << endl;
            return -1;
        }
    }

    FILE *keyFile = fopen(keyFilename.c_str(), "rb");
    if (keyFile == NULL) {
        cerr << "Could not open key file!" << endl;
        return -1;
    }
    
    size_t keySize = fsize(keyFile);
    uint8_t key[BLOWFISH_KEY_BYTES_MAX];
    
    if (keySize < BLOWFISH_KEY_BYTES_MIN) {
        cerr << "Key is too short!" << endl;
        return -1;
    }
    
    if (keySize > BLOWFISH_KEY_BYTES_MAX) {
        cerr << "Key is too long!" << endl;
        return -1;
    }
    
    if (fread(key, sizeof(uint8_t), keySize, keyFile) != keySize) {
        cerr << "Could not read key!" << endl;
        return -1;
    }
    
    
    Blowfish blowfish(key, keySize);
    if (!blowfish.isInitialized()) {
        cerr << "Could not initialize blowfish!" << endl;
        return -1;
    }
    
    size_t fileSize = fsize(inputFile); // FIXME: this is broken for stdin
    size_t bytesReadTotal = 0;
    
    while (!feof(inputFile)) {
        uint8_t buffer[1024];
        
        size_t bytesRead = fread(buffer, sizeof(uint8_t), 1000, inputFile);
        bytesReadTotal += bytesRead;
        
        bool lastBuffer = (bytesRead < 1000) || (bytesReadTotal == fileSize);
        
        size_t bytesWritten = 0;
        if (encrypt) {
            bytesWritten = blowfish.encryptBuffer(buffer, bytesRead, lastBuffer);
        } else {
            bytesWritten = blowfish.decryptBuffer(buffer, bytesRead, lastBuffer);
        }
        
        fwrite(buffer, sizeof(uint8_t), bytesWritten, outputFile);
    }
    
    fclose(inputFile);
    fclose(outputFile);
}
