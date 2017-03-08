#!/bin/sh


cd ${1}

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.data_loader.LoadData

tablename=${3}
delimiter=${4}
dbhost=${5}
dbuser=${6}
dbpwd=${7}
dbname=${8}
dbport=${9}
dbtype=${10}
processing_time=${11}

process_time_day=`date --date="" +%Y-%m-%d`
process_time_hour=`date --date="" +%H:%M:%S`
tsnow="${process_time_day} ${process_time_hour}"

echo "${tsnow}"
found=false
runtime=${tsnow}
if [ ! -f /var/data/kritter/adstatagglastrun ]; then
    echo "NotFound"
else
    runtime=`cat /var/data/kritter/agglastrun`
fi

echo "${runtime}"
echo "find  /var/data/kritter/file_journal/  -name "ad_stat*" -newermt "${runtime}" -type f|xargs ls -1tr|head -1|sed 's#.*/##'"
findout=`find  /var/data/kritter/file_journal/  -name "ad_stat*" -newermt "${runtime}" -type f|xargs ls -1tr|head -1|sed 's#.*/##'`
echo "${findout}"
if echo "${findout}" | grep 'ad_stat_'; then
    echo "here"
    #arrIN=${findout/__// }
    newtime=`stat -c %y /var/data/kritter/file_journal/${findout}`
    touch /var/data/kritter/adstatagglastrun
    echo ${newtime} > /var/data/kritter/adstatagglastrun
    process_time="${findout/ad_stat__/}"
    filepath=${2}/${process_time}/ad_stat/part*
    IFS=- # delimit on _
    set -f # disable the glob part
    array=( $process_time )
    unset -f
    actual_process_time="${array[0]}-${array[1]}-${array[2]} ${array[3]}:00:00"
    echo "{\"process_time\":\"${actual_process_time}\",\"process_time_dir\":\"${process_time}\"}" >> "$JOB_OUTPUT_PROP_FILE"
    echo "java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${actual_process_time}"
    eval java  -cp "$CLASSPATH" $MAINCLASS "${filepath}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} "${actual_process_time}"
    exit $?
fi




