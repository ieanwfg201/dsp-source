#!/bin/sh

process_time=${7}
process_time_dir=${8}

ad_stats_processor_bin=${1}
ad_stats_config=${2}
ad_stats_logger_config=${3}

input_logs_dir=${4}
input_logs_prefix=${5}
input_logs_regex=${input_logs_dir}/*${input_logs_prefix}*

done_logs_dir=${6}

echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"output_file\":\"${output_file}\"}" >> $JOB_OUTPUT_PROP_FILE


echo "##########################################################################################################"
echo "python ${ad_stats_processor_bin} ${ad_stats_config} ${ad_stats_logger_config} \"${input_logs_regex}\" ${done_logs_dir}"

exec python ${ad_stats_processor_bin} ${ad_stats_config} ${ad_stats_logger_config} "${input_logs_regex}" ${done_logs_dir}

exit $?
