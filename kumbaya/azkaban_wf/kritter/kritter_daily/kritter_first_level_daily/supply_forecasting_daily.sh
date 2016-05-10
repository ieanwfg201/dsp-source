#!/bin/sh

process_time=${7}
process_time_dir=${8}

input_file=${4}/${process_time_dir}-*/supply_forecast*/${process_time_dir}-*/${process_time_dir}-*
output_file=${5}/${process_time_dir}

echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"forecast_output_file\":\"${output_file}\"}" >> $JOB_OUTPUT_PROP_FILE

echo "##########################################################################################################"
echo "python ${1} ${2} ${3} ${input_file} ${output_file} ${6}"

eval "python ${1} ${2} ${3} '${input_file}' '${output_file}' ${6}"

exit $?
