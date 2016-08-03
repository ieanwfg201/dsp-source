#!/bin/sh


if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD>"
    echo "Example: sh manual_run.sh 2016-01-09"
    exit
fi

process_time_dir="$1"
process_time="$1 00:00:00"

export process_time_dir="$process_time_dir"
export process_time="$process_time"

echo "process_time_dir=$process_time_dir"
echo "process_time=$process_time"


d=`date +'%s'`
export  JOB_OUTPUT_PROP_FILE=/tmp/manual$d

file="./user_daily.properties"

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


echo mkdir -p "${OUTPUT}"/"${process_time_dir}"
mkdir -p "${OUTPUT}"/"${process_time_dir}"

echo sh user_daily.sh "${uu_daily_code_path}" "${input_path}" "${process_time_dir}" "${OUTPUT}" "${delimiter}" \""${process_time}"\"
eval sh user_daily.sh "${uu_daily_code_path}" "${input_path}" "${process_time_dir}" "${OUTPUT}" "${delimiter}" \""${process_time}"\"

echo sh user_upload_daily.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} \""${dbpwd}"\" ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}"
eval sh user_upload_daily.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} \""${dbpwd}"\" ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}"

echo sh user_report_delete.sh ${daily_report_delete_code_path} ${dbhost} ${dbuser} \""${dbpwd}"\" ${dbname} ${dbport} ${dbtype} ${tablename} ${uu_daily_retention}
eval sh user_report_delete.sh ${daily_report_delete_code_path} ${dbhost} ${dbuser} \""${dbpwd}"\" ${dbname} ${dbport} ${dbtype} ${tablename} ${uu_daily_retention}

