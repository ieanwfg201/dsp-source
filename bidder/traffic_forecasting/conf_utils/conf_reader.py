import ConfigParser
import re
import logging

impression_log_dir_path_key = "impression_log_dir_path"
dimensions_key = "dimensions"
decay_factor_key = "decay_factor"
existing_forecast_key = "existing_forecast"
fields_delimiter_key = "fields_delimiter"
dim_delimiter_key = "dim_delimiter"
output_forecast_key = "output_forecast"
time_window_length_key = "time_window_length"
to_process_time_key = "next_process_date"
last_processed_table_name_key = "last_processed_table"
database_host_key = "host"
database_user_key = "user"
database_password_key = "password"
database_dbname_key = "dbname"
dimension_id_name_table_key = "id_name_mapping_table"

configLogger = logging.getLogger(__name__)

class ConfigParams :
    """ Class to hold elements of the configuration
        Contains the following :
        impression log directory path
        list of dimensions in the model
    """
    def __init__(self, config_file_path) :
        cfg = ConfigParser.RawConfigParser()
        try :
            cfg.read(config_file_path)
            # All the config options except time window length are mandatory
            self.impression_log_dir = cfg.get('default', impression_log_dir_path_key)
            dimensions_list = cfg.get('default', dimensions_key)
            self.dimensions = re.split(', |,| ', dimensions_list)
            self.decay_factor = cfg.getfloat('default', decay_factor_key)
            self.existing_forecast = cfg.get('default', existing_forecast_key)
            self.fields_delimiter = cfg.get('default', fields_delimiter_key)
            self.dim_delimiter = cfg.get('default', dim_delimiter_key)
            self.last_processed_table_name = cfg.get('default', last_processed_table_name_key)

            try :
                self.to_process_time = cfg.get('default', to_process_time_key)
            except :
                self.to_process_time = None

            self.output_forecast = cfg.get('default', output_forecast_key)
            self.database_host = cfg.get('db', database_host_key)
            self.database_user = cfg.get('db', database_user_key)
            self.database_password = cfg.get('db', database_password_key)
            self.database_dbname = cfg.get('db', database_dbname_key)
            self.dimension_id_name_table = cfg.get('db', dimension_id_name_table_key)

            # If time window is not present, default it to -1
            try :
                self.time_window_length = cfg.getint('default', time_window_length_key)
            except :
                self.time_window_length = -1
        except Exception as e:
            configLogger.error("Problem with config file %s", e)
            raise e


if __name__ == '__main__' :
    # Test
    cfg = ConfigParams('../sample/config/config.cfg')
