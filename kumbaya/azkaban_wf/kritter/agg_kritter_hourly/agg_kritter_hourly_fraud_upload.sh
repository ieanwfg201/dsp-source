#!/bin/sh

process_time="";
if [ -z "$11" ]
    then
        process_time=""
    else
        process_time="${11}"
fi

process_time_dir=""
if [ -z "$12" ]
    then
        process_time_dir=""
    else
        process_time_dir="${12}"
fi

echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.data_loader.LoadData

filepath=${14}/logevents/part*
tablename=${3}
delimiter=${4}
dbhost=${5}
dbuser=${6}
dbpwd=${7} 
dbname=${8}
dbport=${9}
dbtype=${10}
processing_time="${process_time}"
trackingeventtablename=${13}
trackingevent=${2}/tracking_event/part*
fraudtablename=fraud_hourly
fraudevent=${2}/fraud/part*


if [ "${process_time_dir}" != "stop" ]; then

    echo 'java  -cp "$CLASSPATH" $MAINCLASS "${fraudevent}" ${fraudtablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}'
    java  -cp "$CLASSPATH" $MAINCLASS "${fraudevent}" ${fraudtablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}

    echo 'java  -cp "$CLASSPATH" $MAINCLASS "${trackingevent}" ${trackingeventtablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}'
    java  -cp "$CLASSPATH" $MAINCLASS "${trackingevent}" ${trackingeventtablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}

    echo 'java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}'

    java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}

    exit $?

fi

