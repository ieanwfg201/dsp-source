#!/bin/sh

echo "find ${1}/ -name '*adserving-thrift*' ! -name '.*' -exec mv {} ${2}/ \;"
find ${1}/ -name '*adserving-thrift*' ! -name '.*' -exec mv {} ${2}/ \;

#echo "find ${3}/ -name '*postimpression-thrift*' ! -name '.*' -exec mv {} ${4}/ \;"
#find ${3}/ -name '*postimpression-thrift*' ! -name '.*' -exec mv {} ${4}/ \;

echo "find ${5}/ -name '*billing-thrift*' ! -name '.*' -exec mv {} ${6}/ \;"
find ${5}/ -name '*billing-thrift*' ! -name '.*' -exec mv {} ${6}/ \;
cp ${9}/data/adserving/empty-adserving.thrift.log.empty ${2}/;
cp ${9}/data/postimpression/empty-postimpression.thrift.log.empty ${4}/;
cp ${9}/data/billing/empty-billing.thrift.log.empty ${6}/;

process_time="${7}"
process_time_dir="${8}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE
