#!/bin/sh

PIG_HOME=$1
SCRIPT_PATH=$2
INPUT_FILES=$3
OUTPUT=$4
PROCESS_TIME="$5 $6"

if [ "$1" = "" ]; then
    PIG_HOME=/usr/share/pig
fi

if [ "$2" = "" ]; then
    SCRIPT_PATH=src/main/pig/dashboard.pig
fi

if [ "$3" = "" ]; then
    INPUT_FILES=/home/rohan/testdata/kritter/daily_first_level_out
fi

if [ "$4" = "" ]; then
    OUTPUT=/home/rohan/testdata/kritter/daily_dashboard
fi

if [ "$5" = "" ]; then
    PROCESS_TIME="2013-12-06 00:00:00"
fi
MAPRED=/tmp/dashboard_`date +%s`
pig_script=dashboard
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS


echo "$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param OUTPUT="$OUTPUT"  --param PROCESS_TIME="$PROCESS_TIME""

$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param OUTPUT="$OUTPUT"  --param PROCESS_TIME="$PROCESS_TIME"
