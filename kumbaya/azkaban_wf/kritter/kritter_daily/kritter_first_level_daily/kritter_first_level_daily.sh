#!/bin/sh

process_time="${6}"
process_time_dir="${7}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}

MAPRED=/var/data/kritter/pig_tmp/rollup_first_level_`date +%s`
pig_script=rollup_first_level
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS

input_file=${3}-*/first_level/part*
limited_input_file=${3}-*/limited_first_level/part*
ext_site_input_file=${3}-*/first_level_ext_site/part*
ext_input_file=${3}-*/externalsite.gz/part*
echo "##########################################################################################################"
echo "${2}/bin/pig -x local -f src/main/pig/rollup_first_level.pig  --param INPUT_FILES='${input_file}' --param EXT_INP_FILES='${ext_input_file}' --param OUTPUT='${4}' --param rolluptype='${5}' --param EXT_SITE_INPUT_FILES='${ext_site_input_file}' --param PROCESS_TIME='${6}' --param LIMITED_INPUT_FILES='${limited_input_file}'"

exec ${2}/bin/pig -x local -f src/main/pig/rollup_first_level.pig  --param INPUT_FILES=${input_file} --param EXT_INP_FILES=${ext_input_file} --param OUTPUT=${4} --param rolluptype=${5} --param EXT_SITE_INPUT_FILES=${ext_site_input_file} --param LIMITED_INPUT_FILES=${limited_input_file} --param PROCESS_TIME="${6}"




