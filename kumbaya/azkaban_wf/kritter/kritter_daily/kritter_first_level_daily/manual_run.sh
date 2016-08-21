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

file="./kritter_first_level_daily.properties"

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

echo sh kritter_first_level_daily.sh "${kritter_first_level_code_path}" "${pig_home}" "${input_path}"/"${process_time_dir}" "${OUTPUT}"/"${process_time_dir}" "${rolluptype}" \""${process_time}"\" "${process_time_dir}"
eval sh kritter_first_level_daily.sh "${kritter_first_level_code_path}" "${pig_home}" "${input_path}"/"${process_time_dir}" "${OUTPUT}"/"${process_time_dir}" "${rolluptype}" \""${process_time}"\" "${process_time_dir}"

echo sh kritter_first_level_upload_daily.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" ${exttableupload} ${exttablename} ${limitedtablename} ${adposition} ${channel}
eval sh kritter_first_level_upload_daily.sh ${loader_code_path} ${OUTPUT}/"${process_time_dir}" ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" ${exttableupload} ${exttablename} ${limitedtablename} ${adposition} ${channel}

export PYTHONPATH=${python_path}
echo sh supply_forecasting_daily.sh "${supply_forecast_bin}" "${supply_forecast_config}" "${supply_forecast_logger_config}" "${input_path}" "${FORECAST_OUTPUT_DIR}" "${supply_forecast_file}" \""${process_time}"\" "${process_time_dir}"
eval sh supply_forecasting_daily.sh "${supply_forecast_bin}" "${supply_forecast_config}" "${supply_forecast_logger_config}" "${input_path}" "${FORECAST_OUTPUT_DIR}" "${supply_forecast_file}" \""${process_time}"\" "${process_time_dir}"

echo  sh kritter_dashboard_daily.sh "${kritter_first_level_code_path}" "${pig_home}" "${OUTPUT}"/"${process_time_dir}" "${dashboard_output}"/"${process_time_dir}" \""${process_time}"\" "${process_time_dir}"
eval sh kritter_dashboard_daily.sh "${kritter_first_level_code_path}" "${pig_home}" "${OUTPUT}"/"${process_time_dir}" "${dashboard_output}"/"${process_time_dir}" \""${process_time}"\" "${process_time_dir}"

echo sh kritter_fraud_daily.sh "${kritter_first_level_code_path}" "${pig_home}" "${fraud_input}"/"${process_time_dir}" "${fraud_output}"/"${process_time_dir}" "${rolluptype}" \""${process_time}"\" "${process_time_dir}"
eval sh kritter_fraud_daily.sh "${kritter_first_level_code_path}" "${pig_home}" "${fraud_input}"/"${process_time_dir}" "${fraud_output}"/"${process_time_dir}" "${rolluptype}" \""${process_time}"\" "${process_time_dir}"

echo sh kritter_fraud_upload.sh ${loader_code_path} ${fraud_output}/"${process_time_dir}" ${fraud_daily_table} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" ${tracking_event_daily_table}
eval sh kritter_fraud_upload.sh ${loader_code_path} ${fraud_output}/"${process_time_dir}" ${fraud_daily_table} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} \""${process_time}"\" "${process_time_dir}" ${tracking_event_daily_table}

echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_total_metric} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_total_metric} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_country} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_country} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_manufacturer} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_manufacturer} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_os} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_os} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_browser} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_browser} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_site} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_site} ${dashboard_retention}
echo  sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_campaign} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_campaign} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_site_v1} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_site_v1} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_campaign_v1} ${dashboard_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${dashboard_campaign_v1} ${dashboard_retention}
echo sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${fraud_daily_table} ${fraud_retention}
eval sh kritter_dashboard_delete.sh ${report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${fraud_daily_table} ${fraud_retention}


echo sh kritter_first_level_report_delete.sh ${daily_report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${daily_retention} ${exttableupload} ${exttablename} ${limitedtablename} ${limiteddaily_retention} ${tracking_event_daily_table} ${adposition} ${channel}
eval sh kritter_first_level_report_delete.sh ${daily_report_delete_code_path} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${daily_retention} ${exttableupload} ${exttablename} ${limitedtablename} ${limiteddaily_retention} ${tracking_event_daily_table} ${adposition} ${channel}

echo sh kritter_ext_site_app.sh ${ext_site_app_code_path} ${dbtype} ${dbhost} ${dbport} ${dbname} ${dbuser} ${dbpwd} ${dashboard_total_metric}
eval sh kritter_ext_site_app.sh ${ext_site_app_code_path} ${dbtype} ${dbhost} ${dbport} ${dbname} ${dbuser} ${dbpwd} ${dashboard_total_metric}
