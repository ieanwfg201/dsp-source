#!/bin/sh

previous_time=`date -u --date="1 day ago" +%Y-%m-%d`
process_time_day=`date -u +%Y-%m-%d`
process_time_dir=`date -u +%Y-%m-%d`
process_time=`date -u +%Y-%m-%d-%H-%M`

#cd ${1}

input_file=${4}/${process_time_dir}-*/input_for_ctr.gz/${process_time_dir}-*/2*
existing_click_log_segments_file=${5}/${6}-${previous_time}
new_click_log_segments_file=${5}/${6}-${process_time_dir}
csv_file_name=${7}/${8}-${process_time}
output_file_name=${10}/${11}-${process_time}
output_link_file=${10}/${12}

echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"forecast_output_file\":\"${output_file}\",\"previous_time\":\"${previous_time}\"}" >> $JOB_OUTPUT_PROP_FILE

echo "##########################################################################################################"
echo "python ${1} ${2} ${3} ${input_file} ${existing_click_log_segments_file} ${new_click_log_segments_file} ${csv_file_name} ${9} ${output_file_name} ${output_link_file}"

exec python ${1} ${2} ${3} "${input_file}" ${existing_click_log_segments_file} ${new_click_log_segments_file} ${csv_file_name} ${9} ${output_file_name} ${output_link_file}

exit $?
