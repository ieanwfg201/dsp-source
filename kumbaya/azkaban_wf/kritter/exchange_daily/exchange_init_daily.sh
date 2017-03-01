#!/bin/sh

process_time_day=`date --date="1 day ago" +%Y-%m-%d`
process_time_dir=`date --date="1 day ago" +%Y-%m-%d`
process_time="${process_time_day} 00:00:00"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE 
