PROGRAM_NAME := http-server
C_FILES := $(wildcard src/*.c)
OBJ_FILES := $(addprefix obj/,$(notdir $(C_FILES:.c=.o)))
LD_FLAGS := -lpthread -lz
CC_FLAGS := -ggdb -Wall -O3 -D ENABLE_ZLIB

all: $(OBJ_FILES)
	gcc $^ -o bin/$(PROGRAM_NAME) $(LD_FLAGS)

obj/%.o: src/%.c
	gcc $(CC_FLAGS) -c $< -o $@

clean:
	rm -f bin/$(PROGRAM_NAME) obj/*.o
