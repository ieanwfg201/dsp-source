
A tool to read base64 encoded serialized thrift object represented by a line in a file

Compile:

    mvn -e clean install -Pthrift-0.9 -Pproto-2.4.1

CONFIG to CHANGE before running:
    Change in File run.sh
    Change value of variable of THRIFTCLASS as per your top level thrift struct name (with package)
    Change value of variable of INABSFILEPATH (which is absolute file name (including full path))
    Change value of variable of EXTRA_THRIFT_JAR which is the absolute path to the thrift jar which you have created and against which you want to read the logs

HOW TO RUN:
    ./run.sh

