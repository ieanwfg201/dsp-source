import sys
import glob
import MySQLdb

from bidder.traffic_forecasting.core.utils.traffic_forecasting import *
from bidder.traffic_forecasting.conf_utils.conf_summary import *
from bidder.input_log_adapter.summary_supply_input_log_adapter import *
from bidder.metadata_utils.dimension_id_name_map_reader import *

#def createSupplySegments(self, logLines, existingForecast, decayFactor, minTrafficThreshold):

def get_diff_current_old(log_lines, existing_forecast, min_threshold, diff_percent):
    """

    :param log_lines:
    :param existing_forecast:
    :param min_threshold:
    :return:
    """
    sorted_log_lines = sortAndConsolidateLogLines(log_lines)
    sorted_existing_forecast = sortAndConsolidateLogLines(existing_forecast)
    log_line_size = len(sorted_log_lines)
    existing_forecast_size = len(sorted_existing_forecast)
    log_line_index = 0
    existing_forecast_index = 0
    diff_factor = diff_percent * 1.0 / 100
    log_lines_diff = []
    existing_forecast_diff = []

    while log_line_index < log_line_size and existing_forecast_index < existing_forecast_size:
        log_line_value = sorted_log_lines[log_line_index]
        existing_forecast_value = sorted_existing_forecast[existing_forecast_index]
        comp_result = compLists(log_line_value.dimension_list, existing_forecast_value.dimension_list)
        if log_line_value.count < min_threshold and existing_forecast_value.count < min_threshold:
            # Both don't match thresholds
            log_line_index += 1
            existing_forecast_index += 1
            continue
        if comp_result == 0:
            # Both the dimension lists are equal
            high_value = max(log_line_value.count, existing_forecast_value.count)
            low_value = min(log_line_value.count, existing_forecast_value.count)
            log_line_index += 1
            existing_forecast_index += 1
            if (low_value * 1.0 / high_value) < diff_factor:
                log_lines_diff.append(log_line_value)
                existing_forecast_diff.append(existing_forecast_value)
        elif comp_result > 0:
            # Dimension list not found in new log lines
            existing_forecast_index += 1
            if existing_forecast_value.count < min_threshold:
                continue
            existing_forecast_diff.append(existing_forecast_value)
        else:
            # Dimension list not found in existing forecast
            log_line_index += 1
            if log_line_value.count < min_threshold:
                continue
            log_lines_diff.append(log_line_value)

    return (log_lines_diff, existing_forecast_diff)

def load_and_parse_new_files(log_adapter, files_to_process, dim_delimiter, fields_delimiter):
    """

    :param log_adapter:
    :param files_to_process:
    :param dim_delimiter:
    :param fields_delimiter:
    :return:
    """
    log_lines = []
    for file_name in files_to_process:
        log_lines.extend(log_adapter.readLogsFromFile(file_name))
    parsed_log_lines = parseLogLinesToObjects(log_lines, dim_delimiter, fields_delimiter)
    sorted_log_lines = sortAndConsolidateLogLines(parsed_log_lines)
    return sorted_log_lines

if __name__ == '__main__':
    if len(sys.argv) != 6:
        print "Usage : python diff.py <config file for main module> <current files regex> <existing forecast> <minimum request threshold> <percentage difference between current and history>"
        sys.exit(0)

    cfg_params = ConfigParams(sys.argv[1])
    dir_regex = sys.argv[2]
    existing_forecast_file = sys.argv[3]
    min_threshold = float(sys.argv[4])
    diff_percent = float(sys.argv[5])
    fields_delimiter = u'\x02'

    dbConnection = MySQLdb.connect(host=cfg_params.database_host, user=cfg_params.database_user, passwd=cfg_params.database_password, db=cfg_params.database_dbname)
    tableName = cfg_params.dimension_id_name_table

    fullDimIdNameMap = getDimIdNameMap(dbConnection, tableName)
    fullDimNameIdMap = {}
    for dimId, dimName in fullDimIdNameMap.items():
        fullDimNameIdMap[dimName] = dimId

    timeDimId = -1
    dimIdNameMap = {}
    dim_list = []
    for dim in cfg_params.dimensions:
        if dim.lower() == 'time':
            timeDimId = fullDimNameIdMap[dim]
        if dim in fullDimNameIdMap:
            dimIdNameMap[dim] = fullDimNameIdMap[dim]
            dim_list.append(fullDimNameIdMap[dim])
        else:
            raise Exception("Dimension id " + dim + " not found in metadata")

    timeWindowLength = cfg_params.time_window_length
    log_adapter = SummarySupplyInputLogAdapter(cfg_params.dim_delimiter, cfg_params.fields_delimiter, dim_list, str(u'\u0001'), int(cfg_params.request_pos_summary), timeDimId, timeWindowLength)

    files_to_process = glob.glob(dir_regex)

    log_lines = load_and_parse_new_files(log_adapter, files_to_process, cfg_params.dim_delimiter, cfg_params.fields_delimiter)
    existing_forecast = loadExistingForecastFile(existing_forecast_file, cfg_params.dim_delimiter, cfg_params.fields_delimiter)

    new_log_diff, existing_log_diff = get_diff_current_old(log_lines, existing_forecast, min_threshold, diff_percent)
    new_diff_file = open('new_diff.txt', 'w')
    existing_diff_file = open('existing_diff.txt', 'w')
    new_diff_file.write('number of lines in new = ' + str(len(log_lines)) + '\n')
    for new_log_diff_line in new_log_diff:
        new_diff_file.write(str(new_log_diff_line))
        new_diff_file.write('\n')
    existing_diff_file.write('number of lines in existing = ' + str(len(existing_forecast)) + '\n')
    for existing_log_diff_line in existing_log_diff:
        existing_diff_file.write(str(existing_log_diff_line))
        existing_diff_file.write('\n')

    new_diff_file.close()
    existing_diff_file.close()
    dbConnection.close()

