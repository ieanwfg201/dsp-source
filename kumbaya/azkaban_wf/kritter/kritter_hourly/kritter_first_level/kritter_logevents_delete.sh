#!/bin/sh


cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.report_delete.hourly.LogEventDelete
TRACKINGMAINCLASS=com.kritter.kumbaya.libraries.report_delete.hourly.TrackingEventDelete


dbhost=${2}
dbuser=${3}
dbpwd=${4}
dbname=${5}
dbport=${6}
dbtype=${7}
tablename=${8}
hours=${9}
trackingeventtablename=${10}
trackingeventhours=${11}
fraudtablename=fraud_hourly
fraudhours=36

echo 'java  -cp "$CLASSPATH" $TRACKINGMAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${fraudtablename} ${fraudhours}'
exec java  -cp "$CLASSPATH" $TRACKINGMAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${fraudtablename} ${fraudhours}


echo 'java  -cp "$CLASSPATH" $TRACKINGMAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${trackingeventtablename} ${trackingeventhours}'
exec java  -cp "$CLASSPATH" $TRACKINGMAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${trackingeventtablename} ${trackingeventhours}

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}'

exec java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}

exit $?
