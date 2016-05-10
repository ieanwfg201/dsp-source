#!/bin/bash


if [ "${BILLING_HOME}" = "" ]; then
    BILLING_HOME=/usr/share/kritter/billing/dobilling/non_web_singleinstance
fi
echo "BILLING_HOME is ${BILLING_HOME}"
if [ "${BILLING_CONF}" = "" ]; then
    BILLING_CONF=${BILLING_HOME}/conf/current/
fi
echo "BILLING_CONF is ${BILLING_CONF}"
cd ${BILLING_HOME}
if [ "${BILLING_LOG}" = "" ]; then
    BILLING_LOG=/var/log/kritter/billing/
fi
echo "BILLING_LOG is ${BILLING_LOG}"



RUNNING=`ps -ef|grep "com.kritter.billing.dobilling.non_web_singleinstance.nfile.DoBilling" |grep -v "grep"|wc -l`

if [ "${RUNNING}" = "1" ]; then

echo "Error: SOME BILLING ALREADY RUNNING: Below is what is running"
ps -ef|grep "com.kritter.billing.dobilling.non_web_singleinstance.nfile.DoBilling" |grep -v "grep"
exit 1

fi

if [ -e  "${BILLING_LOG}"/billing.pid ]; then

echo "Below is the BILLING PID logged" 
cat "${BILLING_LOG}"/billing.pid

else

echo "Error: BILLING_LOG pid file does not exist. Probably some other BILLING daemon RUNNING or someone deleted BILLING pid file"

fi
  

echo "STARTING BILLING"
CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.billing.dobilling.non_web_singleinstance.nfile.DoBilling

CLASSPATH=${CLASSPATH}
echo "java  -cp "$CLASSPATH" $MAINCLASS ${BILLING_CONF}"
nohup java  -cp "$CLASSPATH" $MAINCLASS ${BILLING_CONF} >/dev/null 2>&1   & 
echo $! >${BILLING_LOG}/billing.pid
echo "STARTED BILLING"
CHECKRUNNING=`ps -ef|grep "com.kritter.billing.dobilling.non_web_singleinstance.nfile.DoBilling" |grep -v "grep"`
echo "Below is process information"
echo ${CHECKRUNNING}
echo "PID LOGGED "
cat   "${BILLING_LOG}"/billing.pid
echo "PID LOGGED and process PID should be same. Manually Confirm from above"
