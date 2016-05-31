#!/bin/sh

process_time="${6}"
process_time_dir="${3}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.countdistinct.kritter_user_counts.KritterUserCount

inputFilePattern=${2}/${3}-*/*
outputFilePath=${4}/${3}/usercount.txt
delimiter=${5}
logType="adserving"
processingTime=${3}

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${inputFilePattern} ${outputFilePath} ${delimiter} ${logType} ${processingTime}'
java  -cp "$CLASSPATH" $MAINCLASS ${inputFilePattern} ${outputFilePath} ${delimiter} ${logType} ${processingTime}


exit $?
