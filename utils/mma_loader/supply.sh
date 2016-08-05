#!/bin/sh

if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh supply.sh <Absultue FilPATH>"
    echo "Example: sh supply.sh /home/rohan/testdata/mma/supplycode_mma_mapping.csv"
    exit
fi

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.utils.mma_loader.SupplyDataloader

filepath=$1
dbhost=10.10.80.8
dbuser=optimad
dbpwd=optimad@2016 
dbname=kritter
dbport=3306
dbtype=MYSQL


echo "java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${dbhost} ${dbport} ${dbuser} ${dbpwd} ${dbname} ${dbtype}"
exec java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${dbhost} ${dbport} ${dbuser} ${dbpwd} ${dbname} ${dbtype}


