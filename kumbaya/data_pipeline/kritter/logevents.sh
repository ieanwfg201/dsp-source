#!/bin/sh

PIG_HOME=$1
SCRIPT_PATH=$2
OUTPUT=$3
THRIFT_JAR_PATH=$4
PROCESS_TIME="$5 $6"
POST_IMP_INPUT_FILES=$7
tz=$8

if [ "$1" = "" ]; then
    PIG_HOME=/usr/share/pig
fi

if [ "$2" = "" ]; then
    SCRIPT_PATH=src/main/pig/logevents.pig
fi

if [ "$3" = "" ]; then
    OUTPUT=/home/rohan/testdata/kritter/out/logevents
fi

if [ "$4" = "" ]; then
    THRIFT_JAR_PATH=../../../data_structs/target/com.kritter.thrift-structs-1.0.0.jar
fi

if [ "$5" = "" ]; then
    PROCESS_TIME="2013-12-06 00:00:00"
fi
if [ "$7" = "" ]; then
    POST_IMP_INPUT_FILES=/home/rohan/testdata/kritter/postim*
fi
if [ "$8" = "" ]; then
    tz=UTC
fi


MAPRED=/tmp/logevents_`date +%s`
pig_script=logevents
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS

echo "$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param OUTPUT="$OUTPUT" --param thrift_jar_path="$THRIFT_JAR_PATH" --param PROCESS_TIME="$PROCESS_TIME" --param POST_IMP_INPUT_FILES="$POST_IMP_INPUT_FILES" --param tz="$tz""
$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH --param OUTPUT="$OUTPUT" --param thrift_jar_path="$THRIFT_JAR_PATH" --param PROCESS_TIME="$PROCESS_TIME" --param POST_IMP_INPUT_FILES="$POST_IMP_INPUT_FILES" --param tz="$tz"
