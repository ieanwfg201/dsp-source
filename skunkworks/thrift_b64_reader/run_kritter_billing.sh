#!/bin/sh

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

INABSFILEPATH=$1
THRIFTCLASS=$2
EXTRA_THRIFT_JAR=$3
MAINCLASS=$4

if [ "$1" = "" ]; then
    INABSFILEPATH=/var/log/kritter/billing/data/billing-thrift.log.2014-07-22-1*
fi

if [ "$2" = "" ]; then
    THRIFTCLASS=com.kritter.postimpression.thrift.struct.Billing
fi

if [ "$3" = "" ]; then
    EXTRA_THRIFT_JAR=/usr/share/kritter/data_structs/target/com.kritter.thrift-structs-1.0.0.jar
fi

if [ "$4" = "" ]; then
    MAINCLASS=com.kritter.skunworks.thrift_b64_reader.ReadLogs
fi

CLASSPATH=${CLASSPATH}:$EXTRA_THRIFT_JAR

echo "#####################################################"
echo "################## EXAMPLE USAGE ####################"
echo "./run_kritter_billing.sh \"/var/log/kritter/billing/data/billing-thrift.log.2014-07-22-12-51\""
echo "#####################################################"

echo "java  -cp '$CLASSPATH' $MAINCLASS '$INABSFILEPATH' $THRIFTCLASS"
eval "java  -cp '$CLASSPATH' $MAINCLASS '$INABSFILEPATH' $THRIFTCLASS"



