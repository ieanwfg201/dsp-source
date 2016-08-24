#!/bin/sh

cd ${1}

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.nosql.user.targetingprofileincexc.upload.UserIdUpload

log4j_properties_path=${2}
input_dir=${3}
processed_dir=${4}
logger_name=${5}
namespace_name=${6}
table_name=${7}
primary_key_name=${8}
attribute_name=${9}
cache_name=${10}
cache_logger_name=${11}
aerospike_host=${12}
aerospike_port=${13}
max_retries=${14}
timeout=${15}
aerospike_logger_name=${16}

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${log4j_properties_path} "${input_dir}" "${processed_dir}" "${logger_name}" "${namespace_name}" "${table_name}" "${primary_key_name}" "${attribute_name}" "${cache_name}" "${cache_logger_name}" "${aerospike_host}" "${aerospike_port}" "${max_retries}" "${timeout}" "${aerospike_logger_name}"'

java  -cp "$CLASSPATH" $MAINCLASS ${log4j_properties_path} "${input_dir}" "${processed_dir}" "${logger_name}" "${namespace_name}" "${table_name}" "${primary_key_name}" "${attribute_name}" "${cache_name}" "${cache_logger_name}" "${aerospike_host}" "${aerospike_port}" "${max_retries}" "${timeout}" "${aerospike_logger_name}"

exit $?
