#!/bin/sh

PIG_HOME=$1
SCRIPT_PATH=$2
INPUT_FILES=$3
OUTPUT=$4
THRIFT_JAR_PATH=$5
PROCESS_TIME="$6 $7"
POST_IMP_INPUT_FILES=$8
BILLING_INPUT_FILES=$9
tz=$10

if [ "$1" = "" ]; then
    PIG_HOME=/usr/share/pig
fi

if [ "$2" = "" ]; then
    SCRIPT_PATH=src/main/pig/first_level_wo_ext_site_channel_adposition.pig
fi

if [ "$3" = "" ]; then
    INPUT_FILES=/home/rohan/testdata/kritter/adserv*
fi

if [ "$4" = "" ]; then
    OUTPUT=/home/rohan/testdata/kritter/out/hourly
fi

if [ "$5" = "" ]; then
    THRIFT_JAR_PATH=../../../data_structs/target/com.kritter.thrift-structs-1.0.0.jar
fi

if [ "$6" = "" ]; then
    PROCESS_TIME="2013-12-06 00:00:00"
fi
if [ "$8" = "" ]; then
    POST_IMP_INPUT_FILES=/home/rohan/testdata/kritter/*postim*
fi

if [ "$9" = "" ]; then
    BILLING_INPUT_FILES=/home/rohan/testdata/kritter/billing*
fi

if [ "$10" = "" ]; then
    tz=UTC
fi

MAPRED=/tmp/first_level_wo_ext_site_channel_adposition_`date +%s`
pig_script=first_level_wo_ext_site_channel_adposition
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS

echo "$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param OUTPUT="$OUTPUT" --param thrift_jar_path="$THRIFT_JAR_PATH" --param PROCESS_TIME="$PROCESS_TIME" --param POST_IMP_INPUT_FILES="$POST_IMP_INPUT_FILES" --param BILLING_INPUT_FILES="$BILLING_INPUT_FILES" --param tz="$tz""
$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param OUTPUT="$OUTPUT" --param thrift_jar_path="$THRIFT_JAR_PATH" --param PROCESS_TIME="$PROCESS_TIME" --param POST_IMP_INPUT_FILES="$POST_IMP_INPUT_FILES" --param BILLING_INPUT_FILES="$BILLING_INPUT_FILES" --param tz="$tz"
