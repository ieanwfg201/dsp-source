#!/bin/sh

if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh mma.sh <Absultue FilPATH>"
    echo "Example: sh mma.sh /home/rohan/testdata/mma/mma_categories.csv"
    exit
fi


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.utils.mma_loader.MMADataLoader



filepath=$1
dbhost=localhost
dbuser=root
dbpwd=password 
dbname=kritter
dbport=3306
dbtype=MYSQL


echo "java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${dbhost} ${dbport} ${dbuser} ${dbpwd} ${dbname} ${dbtype}"
exec java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${dbhost} ${dbport} ${dbuser} ${dbpwd} ${dbname} ${dbtype}


