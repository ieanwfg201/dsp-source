#!/bin/sh

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.report_delete.hourly.HourlyDelete

dbhost=localhost
dbuser=root
dbpwd=password 
dbname=vserv_bidder
dbport=3306
dbtype=MYSQL
tablename=first_level
hours=720


exec java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}



