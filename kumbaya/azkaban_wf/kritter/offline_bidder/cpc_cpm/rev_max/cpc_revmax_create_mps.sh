#!/bin/sh

process_time="${7}"
process_time_dir="${8}"
process_time_cur="${9}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"process_time_cur\":\"${process_time_cur}\"}" >> $JOB_OUTPUT_PROP_FILE 

echo "python ${1} ${2} ${3} ${4} ${5} ${6}"

python ${1} ${2} ${3} ${4} ${5} ${6}

exit $?
