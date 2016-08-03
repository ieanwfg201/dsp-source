#!/bin/sh

process_time_day=`date +%Y-%m-%d`
process_time_dir=`date +%Y-%m-%d`
process_time=`date +%Y-%m-%d-%H-%M`
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE 
