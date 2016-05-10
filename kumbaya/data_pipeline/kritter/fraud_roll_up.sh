#!/bin/sh

PIG_HOME=$1
SCRIPT_PATH=$2
OUTPUT=$3
PROCESS_TIME="$4 $5"
INPUT_FILES=$6
ROLLUPTYPE=$7

if [ "$1" = "" ]; then
    PIG_HOME=/usr/share/pig
fi

if [ "$2" = "" ]; then
    SCRIPT_PATH=src/main/pig/fraud_roll_up.pig
fi

if [ "$3" = "" ]; then
    OUTPUT=/home/rohan/testdata/kritter/out/fraud_roll_up
fi

if [ "$4" = "" ]; then
    PROCESS_TIME="2013-12-06 00:00:00"
fi
if [ "$6" = "" ]; then
    INPUT_FILES=/home/rohan/testdata/kritter/out/logevents/fraud.gz/part*
fi
if [ "$7" = "" ]; then
    ROLLUPTYPE="D"
fi
if [ "$8" = "" ]; then
    TRACKING_EVENT_INPUT_FILES=/home/rohan/testdata/kritter/out/logevents/tracking_event/part*
fi

MAPRED=/tmp/fraud_roll_up_`date +%s`
pig_script=fraud_roll_up
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS

echo "$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param rolluptype="$ROLLUPTYPE" --param OUTPUT="$OUTPUT"  --param PROCESS_TIME="$PROCESS_TIME" --param INPUT_FILES="$INPUT_FILES" --param="$TRACKING_EVENT_INPUT_FILES""
$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH --param rolluptype="$ROLLUPTYPE" --param OUTPUT="$OUTPUT"  --param PROCESS_TIME="$PROCESS_TIME" --param INPUT_FILES="$INPUT_FILES" --param TRACKING_EVENT_INPUT_FILES="$TRACKING_EVENT_INPUT_FILES"
