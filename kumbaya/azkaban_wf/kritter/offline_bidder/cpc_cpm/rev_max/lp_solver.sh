#!/bin/sh

process_time="${5}"
process_time_dir="${6}"
process_time_cur="${7}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"process_time_cur\":\"${process_time_cur}\"}" >> $JOB_OUTPUT_PROP_FILE 

echo '##############################'
echo "${1} ${2} ${3} ${4}"

exec ${1} ${2} ${3} ${4}

exit $?
