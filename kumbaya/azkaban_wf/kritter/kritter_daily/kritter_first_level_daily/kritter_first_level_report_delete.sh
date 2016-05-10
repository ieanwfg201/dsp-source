#!/bin/sh


cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.report_delete.daily.DailyDelete


dbhost=${2}
dbuser=${3}
dbpwd=${4}
dbname=${5}
dbport=${6}
dbtype=${7}
tablename=${8}
hours=${9}
exttableupload=${10}
exttablename=${11}
limitedtablename=${12}
limitedhours=${13}
tracking_event_daily_table=${14}

if [ "$10" = "true" ]; then
    echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${exttablename} ${hours}'
    java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${exttablename} ${hours}
fi

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tracking_event_daily_table} ${limitedhours}'
java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tracking_event_daily_table} ${limitedhours}

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}'
java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${limitedtablename} ${limitedhours}'
java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${limitedtablename} ${limitedhours}


exit $?
