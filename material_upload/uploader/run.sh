#!/bin/sh

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.material_upload.uploader.MaterialUploader
CONFPATH=/usr/share/kritter/material_upload/uploader/conf/current

CLASSPATH=${CLASSPATH}



echo "java  -cp '$CLASSPATH' $MAINCLASS $CONFPATH"
eval "java  -cp '$CLASSPATH' $MAINCLASS $CONFPATH"


