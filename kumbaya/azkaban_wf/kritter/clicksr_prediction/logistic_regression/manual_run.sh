#!/bin/sh

if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <Prcocess Day> <Process Hour-Minute> <Previous day>"
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH-mm> <YYYY-MM-DD>"
    echo "Example: sh manual_run.sh 2016-01-09 01-45 2016-01-08"
    exit
fi

if [ "$2" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <Prcocess Day> <Process Hour-Minute> <Previous day>"
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH-mm> <YYYY-MM-DD>"
    echo "Example: sh manual_run.sh 2016-01-09 01-45 2016-01-08"
    exit
fi

if [ "$3" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <Prcocess Day> <Process Hour-Minute> <Previous day>"
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH-mm> <YYYY-MM-DD>"
    echo "Example: sh manual_run.sh 2016-01-09 01-45 2016-01-08"
    exit
fi



process_time_dir="$1"
process_time="$1-$2"
previous_time="$3"

export process_time_dir="$process_time_dir"
export process_time="$process_time"
export previous_time="$previous_time"

echo "process_time_dir=$process_time_dir"
echo "process_time=$process_time"
echo "process_time=$previous_time"


d=`date +'%s'`
export  JOB_OUTPUT_PROP_FILE=/tmp/manual$d

file="./clicksr_prediction_lr.properties"

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

export PYTHONPATH=${python_path}

echo sh clicksr_prediction_lr.sh "${ctr_prediction_lr_bin}" "${ctr_prediction_lr_config}" "${ctr_prediction_lr_log_config}" \""${input_path}"\" "${click_log_segments_path}" "${click_log_segments_prefix}" "${csv_file_path}" "${csv_file_prefix}" "${r_ctr_prediction_lr_bin}" "${output_file_path}" "${output_file_prefix}" "${ctr_lr_symlink_path}" "${process_time}" "${process_time_dir}" "${previous_time}"
eval sh clicksr_prediction_lr.sh "${ctr_prediction_lr_bin}" "${ctr_prediction_lr_config}" "${ctr_prediction_lr_log_config}" \""${input_path}"\" "${click_log_segments_path}" "${click_log_segments_prefix}" "${csv_file_path}" "${csv_file_prefix}" "${r_ctr_prediction_lr_bin}" "${output_file_path}" "${output_file_prefix}" "${ctr_lr_symlink_path}" "${process_time}" "${process_time_dir}" "${previous_time}"
