#!/bin/sh

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.data_loader.LoadData

filepath='/home/rohan/testr/part-*'
tablename=bidder_reporting
delimiter=
dbhost=localhost
dbuser=root
dbpwd=password 
dbname=vserv_bidder
dbport=3306
dbtype=MYSQL
processing_time='2013-12-06 00:00:00'



exec java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}



