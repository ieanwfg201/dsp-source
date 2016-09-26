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

echo "Error: SOME MATERAIL UPLOADER ALREADY RUNNING: Below is what is running"
ps -ef|grep "com.kritter.material_upload.materialdaemon.MaterialUploaderDaemon" |grep -v "grep"
exit 1

fi

if [ -e  "${MATERIAL_LOG}"/material.pid ]; then

echo "Below is the MATERIAL PID logged" 
cat "${MATERIAL_LOG_LOG}"/material.pid

else

echo "Error: MATERIAL_LOG pid file does not exist. Probably some other MATERIAL daemon RUNNING or someone deleted MATERIAL pid file"

fi
  

echo "STARTING MATREILA UPLOAD"
CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.material_upload.materialdaemon.MaterialUploaderDaemon

CLASSPATH=${CLASSPATH}
echo "java  -cp "$CLASSPATH" $MAINCLASS ${MATERIAL_CONF}"
nohup java  -cp "$CLASSPATH" $MAINCLASS ${MATERIAL_CONF} >/dev/null 2>&1   & 
echo $! >${MATERIAL_LOG}/material.pid
echo "STARTED MATERIAL"
CHECKRUNNING=`ps -ef|grep "com.kritter.material_upload.materialdaemon.MaterialUploaderDaemon" |grep -v "grep"`
echo "Below is process information"
echo ${CHECKRUNNING}
echo "PID LOGGED "
cat   "${MATERIAL_LOG}"/material.pid
echo "PID LOGGED and process PID should be same. Manually Confirm from above"
