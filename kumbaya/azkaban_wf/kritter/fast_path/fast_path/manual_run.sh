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

file="./fast_path.properties"

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

if [ "Y" = "$3" ]
then

mkdir -p ${postimp_readytoprocess_path}/"${process_time_dir}"

echo  sh fast_path_movetoinprocess.sh ${postimp_input_path} ${postimp_readytoprocess_path}/"${process_time_dir}" \""${process_time}"\" "${process_time_dir}" "${kritter_first_level_code_path}"
eval sh kritter_first_level_movetoinprocess.sh ${postimp_input_path} ${postimp_readytoprocess_path}/"${process_time_dir}"  \""${process_time}"\" "${process_time_dir}" "${kritter_first_level_code_path}"

fi

echo sh fast_path_pig.sh "${kritter_first_level_code_path}" "${pig_home}" "${postimp_readytoprocess_path}"/"${process_time_dir}" "${OUTPUT}"/"${process_time_dir}" "${thrift_jar_path}" \""${process_time}"\" "${process_time_dir}" "${tz}"
eval sh fast_path_pig.sh "${kritter_first_level_code_path}" "${pig_home}" "${postimp_readytoprocess_path}"/"${process_time_dir}" "${OUTPUT}"/"${process_time_dir}" "${thrift_jar_path}" \""${process_time}"\" "${process_time_dir}" "${tz}"

echo sh fast_path_upload.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" 
eval sh fast_path_upload.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" 

echo sh fast_path_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hourlyhour} 
eval sh fast_path_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hourlyhour} 


echo sh kritter_logevents_delete.sh ${logevents_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${logeventstablename} ${logeventshourlyhour} ${trackingeventtablename} ${trackingeventhour}
eval sh kritter_logevents_delete.sh ${logevents_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${logeventstablename} ${logeventshourlyhour} ${trackingeventtablename} ${trackingeventhour}
