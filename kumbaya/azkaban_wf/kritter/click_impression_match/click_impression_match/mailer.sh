#!/bin/sh

recepient=${1}
cc=${2}
output_file=${3}
process_time_dir=${4}

subject="Click Impression File for "${process_time_dir}

echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE

echo "##########################################################################################################"
echo "exec echo\"\" | mutt -a ${output_file} -s ${subject} -c ${cc} -- ${recepient}"
exec echo "" | mutt -a ${output_file} -s "${subject}" -c ${cc} -- ${recepient}

exit $?
