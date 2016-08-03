#!/bin/sh

if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh channel.sh <Absultue FilPATH>"
    echo "Example: sh channel.sh /home/rohan/testdata/channel/channel.csv"
    exit
fi

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.utils.channel_loader.ChannelDataloader

filepath=$1
dbhost=localhost
dbuser=root
dbpwd=password 
dbname=kritter
dbport=3306
dbtype=MYSQL


echo "java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${dbhost} ${dbport} ${dbuser} ${dbpwd} ${dbname} ${dbtype}"
exec java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${dbhost} ${dbport} ${dbuser} ${dbpwd} ${dbname} ${dbtype}


