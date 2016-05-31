#!/bin/sh

process_time_day=`date -u +%Y-%m-%d`
process_time_dir=`date -u +%Y-%m-%d`
process_time=`date -u +%Y-%m-%d-%H-%M`
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE 
