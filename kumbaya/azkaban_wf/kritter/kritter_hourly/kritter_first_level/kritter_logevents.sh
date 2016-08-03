#!/bin/sh

process_time="${6}"
process_time_dir="${7}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}

MAPRED=/var/data/kritter/pig_tmp/logevents_`date +%s`
pig_script=logevents
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS


echo "##########################################################################################################"
echo "${2}/bin/pig -x local -f src/main/pig/logevents.pig --param OUTPUT='${4}' --param thrift_jar_path='${5}' --param PROCESS_TIME='${6}' --param POST_IMP_INPUT_FILES=${3} --param tz='${8}'"

exec ${2}/bin/pig -x local -f src/main/pig/logevents.pig  --param OUTPUT=${4} --param thrift_jar_path=${5} --param PROCESS_TIME="${6}" --param POST_IMP_INPUT_FILES=${3} --param tz=${8}



