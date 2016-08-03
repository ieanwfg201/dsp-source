#!/bin/sh

m=`date +%M`
if [ "$m" -lt "30" ]; then
    m="00"
else
    m="30"
fi
process_time_day=`date +%Y-%m-%d`
process_time_hour=`date +%H`
process_time_dir=`date +%Y-%m-%d-%H`
process_time="${process_time_day} ${process_time_hour}:${m}:00"
process_time_cur="${process_time_day}-${process_time_hour}-${m}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"process_time_cur\":\"${process_time_cur}\"}" >> $JOB_OUTPUT_PROP_FILE 
