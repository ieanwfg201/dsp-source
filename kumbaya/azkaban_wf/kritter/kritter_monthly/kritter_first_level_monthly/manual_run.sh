#!/bin/sh


if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM>"
    echo "Example: sh manual_run.sh 2016-01"
    exit
fi

process_time_dir="$1-01"
process_time="$1-01 00:00:00"

export process_time_dir="$process_time_dir"
export process_time="$process_time"

echo "process_time_dir=$process_time_dir"
echo "process_time=$process_time"


d=`date +'%s'`
export  JOB_OUTPUT_PROP_FILE=/tmp/manual$d

file="./kritter_first_level_monthly.properties"

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

echo sh kritter_first_level_monthly.sh "${kritter_first_level_code_path}" "${pig_home}" "${input_path}"/"${process_time_dir}" "${OUTPUT}"/"${process_time_dir}" "${rolluptype}" \""${process_time}"\" "${process_time_dir}"
eval sh kritter_first_level_monthly.sh "${kritter_first_level_code_path}" "${pig_home}" "${input_path}"/"${process_time_dir}" "${OUTPUT}"/"${process_time_dir}" "${rolluptype}" \""${process_time}"\" "${process_time_dir}"

echo sh kritter_first_level_upload_monthly.sh "${loader_code_path}" "${OUTPUT}"/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" ${limitedtablename} ${extsitetablename}
eval sh kritter_first_level_upload_monthly.sh "${loader_code_path}" "${OUTPUT}"/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" ${limitedtablename} ${extsitetablename}

