#!/bin/sh

process_time=${5}
process_time_dir=${6}

click_impression_match_bin=${1}
click_input_regex=${2}/${process_time_dir}*/*${process_time_dir}*
impression_input_dir=${3}
output_file=${4}-${process_time_dir}

echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"output_file\":\"${output_file}\"}" >> $JOB_OUTPUT_PROP_FILE

#python click_impression_matcher.py "/var/data/kritter/log_readytoprocess/postimpression/2016-05-24*/*2016-05-24*" /var/data/kritter/log_readytoprocess/adserving/ > final_24.txt

echo "##########################################################################################################"
echo "python ${click_impression_match_bin} \"${click_input_regex}\" \"${impression_input_dir}\" > ${output_file}"

exec python ${click_impression_match_bin} "${click_input_regex}" "${impression_input_dir}" > ${output_file}

exit $?
