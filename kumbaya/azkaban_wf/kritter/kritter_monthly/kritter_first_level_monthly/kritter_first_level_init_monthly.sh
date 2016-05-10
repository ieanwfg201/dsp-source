#!/bin/sh

process_time_day=`date --date="31 day ago" +%Y-%m`
process_time_dir=`date --date="31 day ago" +%Y-%m`
process_time="${process_time_day}-01 00:00:00"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE 
