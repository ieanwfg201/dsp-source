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

echo "BILLING IS RUNNING: Below is what is running"
ps -ef|grep "com.kritter.billing.dobilling.non_web_singleinstance.nfile.DoBilling" |grep -v "grep"

PIDINFO=`ps -ef|grep "com.kritter.billing.dobilling.non_web_singleinstance.nfile.DoBilling" |grep -v "grep"|awk '{print $2}'` 

if [ -e  "${BILLING_LOG}"/billing.pid ]; then

echo "Below is the BILLING PID logged" 
LOGPID=`cat "${BILLING_LOG}"/billing.pid`
echo ${LOGPID}

if [ "${PIDINFO}" = "${LOGPID}" ]; then

echo "Killing BILLING"
ps -ef|kill -9 ${LOGPID}

CHECKRUNNING=`ps -ef|grep "com.kritter.billing.dobilling.non_web_singleinstance.nfile.DoBilling" |grep -v "grep"|wc -l`

if [ "${CHECKRUNNING}" = "0" ]; then
echo "BILLING Killed"
else
echo "ERROR: Kill FAILED"
fi

else
echo "ERROR: BILLING PIDs Running and logged are different"
fi

else

echo "Error: BILLING pid file does not exist. Probably some other BILLING daemon RUNNING or someone deleted BILLING pid file"
echo "HENCE TAKEOVER MANUALLY"

fi
  
else

echo "Error: NO BILLING DEAMON RUNING"

fi 
