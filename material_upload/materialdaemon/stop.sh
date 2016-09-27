#!/bin/bash

if [ "${MATERIAL_HOME}" = "" ]; then
    MATERIAL_HOME=/usr/share/kritter/material_upload/materialdaemon/
fi
echo "MATERIAL_HOME is ${MATERIAL_HOME}"
if [ "${MATERIAL_CONF}" = "" ]; then
    MATERIAL_CONF=/usr/share/kritter/material_upload/uploader/conf/current/
fi
echo "MATERIAL_CONF is ${MATERIAL_CONF}"
cd ${MATERIAL_HOME}
if [ "${MATERIAL_LOG}" = "" ]; then
    MATERIAL_LOG=/var/log/kritter/material/
fi
echo "MATERIAL_LOG is ${MATERIAL_LOG}"


RUNNING=`ps -ef|grep "com.kritter.material_upload.materialdaemon.MaterialUploaderDaemon" |grep -v "grep"|wc -l`

if [ "${RUNNING}" = "1" ]; then

echo "MATERIAL IS RUNNING: Below is what is running"
ps -ef|grep "com.kritter.material_upload.materialdaemon.MaterialUploaderDaemon" |grep -v "grep"

PIDINFO=`ps -ef|grep "com.kritter.material_upload.materialdaemon.MaterialUploaderDaemon" |grep -v "grep"|awk '{print $2}'` 

if [ -e  "${MATERIAL_LOG}"/material.pid ]; then

echo "Below is the MATERIAL PID logged" 
LOGPID=`cat "${MATERIAL_LOG}"/material.pid`
echo ${LOGPID}

if [ "${PIDINFO}" = "${LOGPID}" ]; then

echo "Killing MATERIAL"
ps -ef|kill -9 ${LOGPID}

CHECKRUNNING=`ps -ef|grep "com.kritter.material_upload.materialdaemon.MaterialUploaderDaemon" |grep -v "grep"|wc -l`

if [ "${CHECKRUNNING}" = "0" ]; then
echo "MATERIAL Killed"
else
echo "ERROR: Kill FAILED"
fi

else
echo "ERROR: MATERIAL PIDs Running and logged are different"
fi

else

echo "Error: MATERIAL pid file does not exist. Probably some other MATERIAL daemon RUNNING or someone deleted MATERIAL pid file"
echo "HENCE TAKEOVER MANUALLY"

fi
  
else

echo "Error: NO MATERIAL DEAMON RUNING"

fi 
