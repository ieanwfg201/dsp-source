#!/bin/sh

PIG_HOME=$1
SCRIPT_PATH=$2
INPUT_FILES=$3
EXT_INP_FILES=$4
OUTPUT=$5
ROLLUPTYPE=$6
PROCESS_TIME="$7 $8"
EXT_SITE_INPUT_FILES="$9"
LIMITED_INPUT_FILES=$10
ADPOSITION_FILES=$11
CHANNEL_FILES=$12

if [ "$1" = "" ]; then
    PIG_HOME=/usr/share/pig
fi

if [ "$2" = "" ]; then
    SCRIPT_PATH=src/main/pig/rollup_first_level_wo_ext_site_channel_adposition.pig
fi

if [ "$3" = "" ]; then
    INPUT_FILES=/home/rohan/testdata/kritter/out/hourly/first_level/part*
fi

if [ "$4" = "" ]; then
    EXT_INP_FILES=/home/rohan/testdata/kritter/manual/extsite.gz/part* 
fi

if [ "$5" = "" ]; then
    OUTPUT=/home/rohan/testdata/kritter/daily_first_level_out
fi

if [ "$6" = "" ]; then
    ROLLUPTYPE="D"
fi

if [ "$7" = "" ]; then
    PROCESS_TIME="2013-12-06 00:00:00"
fi
if [ "$9" = "" ]; then
    EXT_SITE_INPUT_FILES=/home/rohan/testdata/kritter/out/hourly/first_level_ext_site/part*
fi

if [ "$10" = "" ]; then
    LIMITED_INPUT_FILES=/home/rohan/testdata/kritter/out/hourly/limited_first_level/part*
fi

if [ "$11" = "" ]; then
    ADPOSITION_FILES=/home/rohan/testdata/kritter/out/hourly/adposition_hourly/part*
fi

if [ "$12" = "" ]; then
    CHANNEL_FILES=/home/rohan/testdata/kritter/out/hourly/channel_hourly/part*
fi



MAPRED=/tmp/rollup_first_level_`date +%s`
pig_script=rollup_first_level
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS


echo "$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param EXT_INP_FILES="$EXT_INP_FILES" --param OUTPUT="$OUTPUT" --param rolluptype="$ROLLUPTYPE" --param PROCESS_TIME="$PROCESS_TIME" --param EXT_SITE_INPUT_FILES="$EXT_SITE_INPUT_FILES" --param LIMITED_INPUT_FILES="$LIMITED_INPUT_FILES" --param ADPOSITION_FILES="$ADPOSITION_FILES" --param CHANNEL_FILES="$CHANNEL_FILES""

$PIG_HOME/bin/pig -x local -f $SCRIPT_PATH  --param INPUT_FILES="$INPUT_FILES" --param EXT_INP_FILES="$EXT_INP_FILES" --param OUTPUT="$OUTPUT" --param rolluptype="$ROLLUPTYPE" --param PROCESS_TIME="$PROCESS_TIME" --param EXT_SITE_INPUT_FILES="$EXT_SITE_INPUT_FILES" --param LIMITED_INPUT_FILES="$LIMITED_INPUT_FILES" --param ADPOSITION_FILES="$ADPOSITION_FILES" --param CHANNEL_FILES="$CHANNEL_FILES"
