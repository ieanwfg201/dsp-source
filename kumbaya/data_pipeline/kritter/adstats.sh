#!/bin/sh

INPUT_FILES=$1
OUTPUT=$2
PIG_HOME=$3
SCRIPT_PATH=$4
THRIFT_JAR_PATH=$5
PROCESS_TIME="$6 $7"
tz=$8

if [ "$1" = "" ]; then
    INPUT_FILES=/home/rohan/testdata/adstat/input/ip-172-31-9-161-adstats-thrift.log.2016-07-07-11-39
fi

if [ "$2" = "" ]; then
    OUTPUT=/home/rohan/testdata/adstat/output/test1
fi

if [ "$3" = "" ]; then
    PIG_HOME=/usr/share/pig
fi

if [ "$4" = "" ]; then
    SCRIPT_PATH=src/main/pig/ad_stats.pig
fi

if [ "$5" = "" ]; then
    THRIFT_JAR_PATH=../../../data_structs/target/com.kritter.thrift-structs-1.0.0.jar
fi

if [ "$6" = "" ]; then
    PROCESS_TIME="2013-12-06 00:00:00"
fi
if [ "$8" = "" ]; then
    tz=UTC
fi


MAPRED=/tmp/ad_stats_`date +%s`
pig_script=ad_stats
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS

echo "$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param OUTPUT="$OUTPUT" --param thrift_jar_path="$THRIFT_JAR_PATH" --param PROCESS_TIME="$PROCESS_TIME" --param tz="$tz""
$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param OUTPUT="$OUTPUT" --param thrift_jar_path="$THRIFT_JAR_PATH" --param PROCESS_TIME="$PROCESS_TIME" --param tz="$tz"
