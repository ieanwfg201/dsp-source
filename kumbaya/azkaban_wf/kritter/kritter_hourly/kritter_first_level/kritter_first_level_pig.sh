#!/bin/sh

process_time="${8}"
process_time_dir="${9}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}

MAPRED=/var/data/kritter/pig_tmp/first_level_`date +%s`
pig_script=first_level
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS


echo "##########################################################################################################"
echo "${2}/bin/pig -x local -f src/main/pig/first_level.pig  --param INPUT_FILES='${3}' --param OUTPUT='${5}' --param thrift_jar_path='${6}' --param PROCESS_TIME='${8}' --param POST_IMP_INPUT_FILES=${4} --param BILLING_INPUT_FILES=${7} --param tz='${10}'"

exec ${2}/bin/pig -x local -f src/main/pig/first_level.pig  --param INPUT_FILES=${3} --param OUTPUT=${5} --param thrift_jar_path=${6} --param PROCESS_TIME="${8}" --param POST_IMP_INPUT_FILES=${4} --param BILLING_INPUT_FILES=${7} --param tz=${10}



