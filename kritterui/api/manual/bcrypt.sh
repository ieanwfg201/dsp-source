#!/bin/sh

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

PWD=$1

if [ "$1" = "" ]; then
    PWD=rohan
fi


MAINCLASS=com.kritter.kritterui.api.manual.common.GenerateBCryptPwd

CLASSPATH=${CLASSPATH}

echo "java  -cp "$CLASSPATH" $MAINCLASS $PWD"
exec java  -cp "$CLASSPATH" $MAINCLASS $PWD



