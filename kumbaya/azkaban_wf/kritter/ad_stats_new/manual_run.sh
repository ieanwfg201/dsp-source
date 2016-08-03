#!/bin/sh


if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH> <Move Files Y|N>"
    echo "Example: sh manual_run.sh 2016-01-09 01 Y"
    exit
fi

if [ "$2" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH> <Move Files Y|N>"
    echo "Example: sh manual_run.sh 2016-01-09 01 Y"
    exit
fi

if [ "$3" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH> <Move Files Y|N>"
    echo "Example: sh manual_run.sh 2016-01-09 01 Y"
    exit
fi



process_time_dir="$1-$2"
process_time="$1 $2:00:00"

export process_time_dir="$process_time_dir"
export process_time="$process_time"

echo "process_time_dir=$process_time_dir"
echo "process_time=$process_time"


d=`date +'%s'`
export  JOB_OUTPUT_PROP_FILE=/tmp/manual$d

file="./ad_stat_new.properties"

if [ -f "$file" ]
then
  echo "$file found."

  while read keyvalue
  do
    keyvalue1=`echo "$keyvalue" | sed 's/ //g'`
    if [ ! "" = "$keyvalue1" ]
    then
        if [ ! "#" = "$keyvalue1" ]
        then
            echo $keyvalue1
            export "$keyvalue1"
        fi
    fi
  done < "$file"

else
  echo "$file not found."
fi

if [ "$3" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH> <Move Files Y|N>"
    echo "Example: sh manual_run.sh 2016-01-09 01 Y"
    exit
fi

echo mkdir -p ${ad_stat_readytoprocess_path}/"${process_time_dir}"
mkdir -p ${ad_stat_readytoprocess_path}/"${process_time_dir}"

echo sh ad_stat_new_movetoinprocess.sh ${ad_stat_input_path} ${ad_stat_readytoprocess_path}/"${process_time_dir}" \""${process_time}"\" "${process_time_dir}" "${kritter_first_level_code_path}"
eval sh ad_stat_new_movetoinprocess.sh ${ad_stat_input_path} ${ad_stat_readytoprocess_path}/"${process_time_dir}" \""${process_time}"\" "${process_time_dir}" "${kritter_first_level_code_path}"


echo sh ad_stat_new_pig.sh "${kritter_first_level_code_path}" "${pig_home}" "${ad_stat_readytoprocess_path}"/"${process_time_dir}"  "${OUTPUT}"/"${process_time_dir}" "${thrift_jar_path}" \""${process_time}"\" "${process_time_dir}" "${tz}"
eval sh ad_stat_new_pig.sh "${kritter_first_level_code_path}" "${pig_home}" "${ad_stat_readytoprocess_path}"/"${process_time_dir}"  "${OUTPUT}"/"${process_time_dir}" "${thrift_jar_path}" \""${process_time}"\" "${process_time_dir}" "${tz}"

echo sh ad_stat_new_upload.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}"
eval sh ad_stat_new_upload.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}"

