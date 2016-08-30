#!/bin/sh

echo "find ${1}/ -name '*postimpression-thrift*' ! -name '.*' -exec mv {} ${2}/ \;"
find ${1}/ -name '*postimpression-thrift*' ! -name '.*' -exec mv {} ${2}/ \;

cp ${5}/data/postimpression/empty-postimpression.thrift.log.empty ${2}/;

process_time="${3}"
process_time_dir="${4}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE
