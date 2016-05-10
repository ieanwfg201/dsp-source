Dependency for compilation

    Download thrift 0.9.0 available @ http://archive.apache.org/dist/thrift/
    Install as per the instruction provided
    Note:
        Before installing thrift the following is required
            build-essential
                flex
                autoconf
                binutils-doc
                bison
                
            libboost-dev
            python
            python-dev
            g++
Compile:
    mvn -e clean install -Pthrift-0.9 -Pproto-2.4.1
