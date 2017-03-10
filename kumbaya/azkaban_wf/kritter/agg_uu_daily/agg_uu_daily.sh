#!/bin/bash



cd ${1}

MAPRED=/var/data/kritter/pig_tmp/uu_roll_up_`date +%s`
pig_script=uu_roll_up
PIG_OPTS="-Dmapred.output.dir=${MAPRED}/$pig_script/output"
PIG_OPTS="$PIG_OPTS -Dmapred.local.dir=${MAPRED}/$pig_script/local"
PIG_OPTS="$PIG_OPTS -Dmapred.system.dir=${MAPRED}/$pig_script/system"
PIG_OPTS="$PIG_OPTS -Dmapred.temp.dir=${MAPRED}/$pig_script/temp"
PIG_OPTS="$PIG_OPTS -Dpig.temp.dir=${MAPRED}/$pig_script/pig"
PIG_OPTS="$PIG_OPTS -Dhadoop.tmp.dir=${MAPRED}/$pig_script/hadoop"

export PIG_OPTS

process_time_day=`date --date="" +%Y-%m-%d`
process_time_hour=`date --date="" +%H:%M:%S`
tsnow="${process_time_day} ${process_time_hour}"

echo "${tsnow}"
found=false
runtime=${tsnow}
if [ ! -f /var/data/kritter/uudailyagglastrun ]; then
    echo "NotFound"
else
    runtime=`cat /var/data/kritter/uudailyagglastrun`        
fi

echo "${runtime}"
echo "find  /var/data/kritter/file_journal/  -name "uu_daily*" -newermt "${runtime}" -type f|xargs ls -1tr|head -1|sed 's#.*/##'"
findout=`find  /var/data/kritter/file_journal/  -name "uu_daily*" -newermt "${runtime}" -type f|xargs ls -1tr|head -1|sed 's#.*/##'`
echo "${findout}"
if echo "${findout}" | grep 'uu_daily_'; then
    echo "here"
    #arrIN=${findout/__// }
    newtime=`stat -c %y /var/data/kritter/file_journal/${findout}`
    touch /var/data/kritter/uudailyagglastrun
    echo ${newtime} > /var/data/kritter/uudailyagglastrun
    process_time="${findout/uu_daily__/}"
    input_file=${3}/${process_time}/usercount.txt
    IFS=- # delimit on _
    set -f # disable the glob part
    array=( $process_time )
    unset -f
    actual_process_time="${array[0]}-${array[1]}-${array[2]} ${array[3]}:00:00"
    echo "{\"process_time\":\"${actual_process_time}\",\"process_time_dir\":\"${process_time}\"}" >> "$JOB_OUTPUT_PROP_FILE"
    echo "${2}/bin/pig -x local -f src/main/pig/uu_roll_up.pig  --param INPUT_FILES='${input_file}' --param OUTPUT='${4}' --param rolluptype='${5}' --param  PROCESS_TIME='${actual_process_time}'  "
    eval "${2}/bin/pig -x local -f src/main/pig/uu_roll_up.pig  --param INPUT_FILES='${input_file}' --param OUTPUT='${4}' --param rolluptype='${5}' --param  PROCESS_TIME='${actual_process_time}'"
else
    echo "{\"process_time\":\"\",\"process_time_dir\":\"\"}" >> "$JOB_OUTPUT_PROP_FILE"        
fi





