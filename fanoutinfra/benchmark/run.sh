#!/bin/bash

CLASSPATH=

for f in compare/target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in compare/target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.fanoutinfra.benchmark.compare.Compare


CLASSPATH=${CLASSPATH}
echo $CLASSPATH

exec /usr/lib/jvm/default-java/bin/java  -cp "$CLASSPATH" $MAINCLASS "http://localhost/50x.html" -c 2 -n 5 -l 1000 -k 1


