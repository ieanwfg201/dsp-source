#include <stdio.h>
#include <iostream>
#include <unistd.h>
#include <fcntl.h>
#include <thrift/Transport/TFDTransport.h>
#include <thrift/protocol/TJSONProtocol.h>
#include <thrift/protocol/TBinaryProtocol.h>
#include <boost/shared_ptr.hpp>

#include "modp_b64.h"
#include "gen-cpp/AdservingRequestResponse_types.h"
#include "gen-cpp/AdservingRequestResponse_constants.h"

using namespace boost;
using namespace std;
using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;

void cat_log_line(char* line, ssize_t len, shared_ptr<TBufferedTransport>& out_transport, shared_ptr<TJSONProtocol>& out_protocol) {
    char* decoded = (char*) malloc(len * 10 * sizeof(line));
    int read_len = modp_b64_decode(decoded, line, len);

    shared_ptr<TMemoryBuffer> transport(new TMemoryBuffer(read_len));

    uint8_t* write_ptr = transport.get()->getWritePtr(read_len);
    memcpy(write_ptr, decoded, read_len);
    free(decoded);
    transport.get()->wroteBytes(read_len);

    shared_ptr<TBinaryProtocol> protocol(new TBinaryProtocol(transport));

    AdservingRequestResponse request_response;
    request_response.read(protocol.get());

    request_response.write(out_protocol.get());
    out_transport->flush();
    printf("\n");
}

void cat_func(FILE* fp, shared_ptr<TBufferedTransport>& transport, shared_ptr<TJSONProtocol>& protocol) {
    char* line = NULL;
    size_t len = 0;
    ssize_t read;

    while((read = getline(&line, &len, fp)) != -1) {
        if(read == 0)
            continue;
        char ch = line[read - 1];
        if(ch == '\n' || ch == '\r')
            --read;
        cat_log_line(line, read, transport, protocol);
    }
}

int main(int argc, char** argv) {
    FILE* fp = NULL;
    if(argc < 2) {
        fp = stdin;
    }

    int wfd = fileno(stdout);
    shared_ptr<TFDTransport> inner_transport(new TFDTransport(wfd));
    shared_ptr<TBufferedTransport> transport(new TBufferedTransport(inner_transport));
    shared_ptr<TJSONProtocol> protocol(new TJSONProtocol(transport));

    transport->open();

    if(argc < 2) {
        cat_func(fp, transport, protocol);
    } else {
        for(int i = 1; i < argc; ++i) {
            char* file_name = argv[i];
            fp = fopen(file_name, "r");
            if(fp == NULL) {
                continue;
            }

            cat_func(fp, transport, protocol);
            fclose(fp);
        }
    }

    transport->close();

    return 0;
}
