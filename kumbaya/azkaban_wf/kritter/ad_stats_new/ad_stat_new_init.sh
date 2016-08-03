#!/bin/sh

process_time_day=`date --date="1 hour ago" +%Y-%m-%d`
process_time_hour=`date --date="1 hour ago" +%H:00:00`
process_time_dir=`date --date="1 hour ago" +%Y-%m-%d-%H`
process_time="${process_time_day} ${process_time_hour}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE 
