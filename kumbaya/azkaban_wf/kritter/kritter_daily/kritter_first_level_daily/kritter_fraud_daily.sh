#!/bin/sh

process_time="${6}"
process_time_dir="${7}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}

MAPRED=/var/data/kritter/pig_tmp/fraud_roll_up_`date +%s`
pig_script=fraud_roll_up
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS

input_files=${3}-*/fraud/part*
tracking_files=${3}-*/tracking_event/part*

echo "##########################################################################################################"
echo "exec ${2}/bin/pig -x local -f src/main/pig/fraud_roll_up.pig --param rolluptype=${5} --param OUTPUT=${4}  --param PROCESS_TIME="${6}" --param INPUT_FILES=${input_files} --param TRACKING_EVENT_INPUT_FILES=${tracking_files}"

exec ${2}/bin/pig -x local -f src/main/pig/fraud_roll_up.pig --param rolluptype=${5} --param OUTPUT=${4}  --param PROCESS_TIME="${6}" --param INPUT_FILES=${input_files} --param TRACKING_EVENT_INPUT_FILES=${tracking_files}


