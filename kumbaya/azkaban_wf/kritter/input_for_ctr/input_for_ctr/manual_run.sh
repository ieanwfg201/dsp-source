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

file="./input_for_ctr.properties"

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
 
echo sh input_for_ctr_movetoinprocess.sh ${postimp_input_path} ${postimp_readytoprocess_path}/"${process_time_dir}"  \""${process_time}"\" "${process_time_dir}" "${input_for_ctr_code_path}"
eval sh input_for_ctr_movetoinprocess.sh ${postimp_input_path} ${postimp_readytoprocess_path}/"${process_time_dir}"  \""${process_time}"\" "${process_time_dir}" "${input_for_ctr_code_path}"

fi

echo sh input_for_ctr_pig.sh '${input_for_ctr_code_path}' '${pig_home}' "${postimp_readytoprocess_path}"/"${process_time_dir}" '${OUTPUT}'/'${process_time_dir}' '${thrift_jar_path}' \""${process_time}"\" '${process_time_dir}' '${tz}'

eval sh input_for_ctr_pig.sh '${input_for_ctr_code_path}' '${pig_home}' "${postimp_readytoprocess_path}"/"${process_time_dir}" '${OUTPUT}'/'${process_time_dir}' '${thrift_jar_path}' \""${process_time}"\" '${process_time_dir}' '${tz}'

export PYTHONPATH=${python_path}
eval sh ctr_prediction_lr.sh '${ctr_prediction_lr_bin}' '${ctr_prediction_lr_config}' '${ctr_prediction_lr_log_config}' "${input_path}" '${click_log_segments_path}' '${click_log_segments_prefix}' '${csv_file_path}' '${csv_file_prefix}' '${r_ctr_prediction_lr_bin}' '${output_file_path}' '${output_file_prefix}' '${ctr_lr_symlink_path}' \""${process_time}"\" '${process_time_dir}'

