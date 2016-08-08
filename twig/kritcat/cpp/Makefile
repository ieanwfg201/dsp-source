CC = g++

LDIR = /usr/local/lib
THRIFT_INCLUDES = /media/extdrive/libs/thrift-0.9.0/lib/cpp/src
GEN_DIR = gen-cpp
ODIR = obj

CFLAGS = -I$(GEN_DIR) -I$(THRIFT_INCLUDES) -Iinclude
EXECUTABLE = kritcat
LFLAGS = -lthrift -lssl
TLDIR = /usr/local/lib

.PHONY: all clean

all: main-build

pre-build: 
	thrift -gen cpp ../../data_structs/src/main/thrift/AdservingRequestResponse.thrift
	thrift -gen cpp ../../data_structs/src/main/thrift/PostImpressionRequestResponse.thrift

SOURCES = $(shell find $(GEN_DIR) -name '*.cpp')
OBJS = $(patsubst $(GEN_DIR)/%.cpp, $(ODIR)/%.o, $(SOURCES))
MAIN_OBJ = $(ODIR)/main.o
MOD_OBJ = $(ODIR)/modp_b64.o

$(MOD_OBJ) : modp_b64.c
	$(CC) -c -o $@ $< $(CFLAGS)

$(MAIN_OBJ) : main.cpp 
	$(CC) -c -o $@ $< $(CFLAGS) -DHAVE_NETINET_IN_H

$(ODIR)/%.o: $(GEN_DIR)/%.cpp
	mkdir -p $(ODIR)
	$(CC) -c -o $@ $< $(CFLAGS) -DHAVE_NETINET_IN_H

main-build: pre-build
	@$(MAKE) --no-print-directory target
	rm -rf $(GEN_DIR)

target: $(OBJS) $(MAIN_OBJ) $(MOD_OBJ) $(TLDIR)/libthrift.a 
#target: $(OBJS) $(MAIN_OBJ)
	$(CC) -o $(EXECUTABLE) $^ $(LFLAGS) 
	#$(CC) -o $(EXECUTABLE) $^ $(LFLAGS) $(WFLAGS)

clean :
	rm -rf $(ODIR)
	rm -f $(EXECUTABLE)
