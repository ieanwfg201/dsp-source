#!/bin/bash
LOG_FILE=""
PROPERTIES_FILE=""
DATA_DIRECTORY_PATH=""
AS_NAMESPACE=""
AS_TABLE_NAME=""
AS_ATTR_NAME=""
AS_HOST=""
AS_PORT=""

if [ -z $1 ]; then
LOG_FILE="/home/hamlin/workspace/gitlab/optimad/dsp/utils/hbase_data_loader/src/main/resources/log4j2.xml"
else
LOG_FILE=$1
fi

if [ -z $2 ]; then
PROPERTIES_FILE="/home/hamlin/workspace/gitlab/optimad/dsp/utils/hbase_data_loader/src/main/resources/data_load.properties"
else
PROPERTIES_FILE=$2
fi

if [ -z $3 ]; then
DATA_DIRECTORY_PATH="/var/data/kritter/hbase_to_aerospike"
else
DATA_DIRECTORY_PATH=$3
fi

if [ -z $4 ]; then
AS_NAMESPACE="user"
else
AS_NAMESPACE=$4
fi

if [ -z $5 ]; then
AS_TABLE_NAME="device_tag"
else
AS_TABLE_NAME=$5
fi

if [ -z $6 ]; then
AS_ATTR_NAME="tag_code"
else
AS_ATTR_NAME=$6
fi

if [ -z $7 ]; then
AS_HOST="aerospike.mad.com"
else
AS_HOST=$7
fi

if [ -z $8 ]; then
AS_PORT="3000"
else
AS_PORT=$8
fi

echo "params: $LOG_FILE $PROPERTIES_FILE $DATA_DIRECTORY_PATH $AS_NAMESPACE $AS_TABLE_NAME $AS_ATTR_NAME $AS_HOST $AS_PORT"
java -cp target/lib/*:target/* com.kritter.kumbaya.libraries.hbase_data_loader.ImportHbaseDataToAerospike $LOG_FILE $PROPERTIES_FILE $DATA_DIRECTORY_PATH $AS_NAMESPACE $AS_TABLE_NAME $AS_ATTR_NAME $AS_HOST $AS_PORT > import.log 2>&1