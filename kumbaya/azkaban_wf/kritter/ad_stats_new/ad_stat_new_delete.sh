#!/bin/sh


process_time="${11}"
process_time_dir="${12}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.report_delete.hourly.AdStatHourlyDelete


dbhost=${2}
dbuser=${3}
dbpwd=${4}
dbname=${5}
dbport=${6}
dbtype=${7}
tablename=${8}
hours=${9}

if [ "$10" = "true" ]; then
    echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}'
    java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}
    exit $?
fi
