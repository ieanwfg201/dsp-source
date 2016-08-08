1. Dependencies : libboost-dev
2. Also change the thrift location inside "THRIFT_INCLUDES" within Makefile to match your location
3. Add the following line in your Thrift.h
#include <stdint.h>

