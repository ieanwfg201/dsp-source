#!/bin/sh

if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH> <mm>"
    echo "Example: sh manual_run.sh 2016-01-09 01 45"
    exit
fi

if [ "$2" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH> <mm>"
    echo "Example: sh manual_run.sh 2016-01-09 01 45"
    exit
fi

if [ "$3" = "" ]; then
    echo "Incorrect Usage: sh manual_run.sh <YYYY-MM-DD> <HH> <mm>"
    echo "Example: sh manual_run.sh 2016-01-09 01 45"
    exit
fi


echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\",\"process_time_cur\":\"${process_time_cur}\"}" >> $JOB_OUTPUT_PROP_FILE


process_time_dir="$1-$2"
process_time="$1 $2:$3:00"
process_time_cur="$1-$2-$3"

export process_time_dir="$process_time_dir"
export process_time="$process_time"
export process_time_cur="$process_time_cur"

echo "process_time_dir=$process_time_dir"
echo "process_time=$process_time"
echo "process_time_cur=$process_time_cur"


d=`date +'%s'`
export  JOB_OUTPUT_PROP_FILE=/tmp/manual$d

file="./cpc_revmax.properties"

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

echo sh cpc_revmax_create_mps.sh "${code_prefix_path}"/"${create_mps_bin}" "${py_job_config}" "${py_job_logger}" "${supply_forecast_file}" "${mps_output_dir}"/"${mps_output_prefix}-${process_time_cur}" "${ctr_lr_output_file}" \""${process_time}"\" "${process_time_dir}" "${process_time_cur}"
eval sh cpc_revmax_create_mps.sh "${code_prefix_path}"/"${create_mps_bin}" "${py_job_config}" "${py_job_logger}" "${supply_forecast_file}" "${mps_output_dir}"/"${mps_output_prefix}-${process_time_cur}" "${ctr_lr_output_file}" \""${process_time}"\" "${process_time_dir}" "${process_time_cur}"


echo sh lp_solver.sh "${code_prefix_path}"/"${lp_solver_bin}" "${lp_solver_config}" "${mps_output_dir}"/"${mps_output_prefix}"-"${process_time_cur}" "${lp_solver_output_dir}"/"${lp_solver_output_prefix}"-"${process_time_cur}" \""${process_time}"\" "${process_time_dir}" "${process_time_cur}"
eval sh lp_solver.sh "${code_prefix_path}"/"${lp_solver_bin}" "${lp_solver_config}" "${mps_output_dir}"/"${mps_output_prefix}"-"${process_time_cur}" "${lp_solver_output_dir}"/"${lp_solver_output_prefix}"-"${process_time_cur}" \""${process_time}"\" "${process_time_dir}" "${process_time_cur}"


export PYTHONPATH=${python_path}
echo sh cpc_revmax_create_feed.sh "${code_prefix_path}"/"${convert_to_feed_bin}" "${py_job_config}" "${py_job_logger}" "${lp_solver_output_dir}"/"${lp_solver_output_prefix}-${process_time_cur}" "${feed_output_file}" \""${process_time}"\" "${process_time_dir}" "${process_time_cur}"
eval sh cpc_revmax_create_feed.sh "${code_prefix_path}"/"${convert_to_feed_bin}" "${py_job_config}" "${py_job_logger}" "${lp_solver_output_dir}"/"${lp_solver_output_prefix}-${process_time_cur}" "${feed_output_file}" \""${process_time}"\" "${process_time_dir}" "${process_time_cur}"

