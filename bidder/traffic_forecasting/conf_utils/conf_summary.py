import ConfigParser
import re
import logging

dimensions_key = "dimensions"
decay_factor_key = "decay_factor"
existing_forecast_key = "existing_forecast"
fields_delimiter_key = "fields_delimiter"
dim_delimiter_key = "dim_delimiter"
time_window_length_key = "time_window_length"
request_pos_in_summary_key = "request_pos_summary"
min_traffic_threshold_key = 'min_traffic_threshold'
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
            #self.summary_dir = cfg.get('default', summary_directory_key)
            #self.intermediate_dir = cfg.get('default', intermediate_dir_name_key)
            dimensions_list = cfg.get('default', dimensions_key)
            self.dimensions = re.split(', |,| ', dimensions_list)
            self.decay_factor = cfg.getfloat('default', decay_factor_key)
            self.existing_forecast = cfg.get('default', existing_forecast_key)
            self.fields_delimiter = cfg.get('default', fields_delimiter_key)
            self.dim_delimiter = cfg.get('default', dim_delimiter_key)
            self.request_pos_summary = cfg.get('default', request_pos_in_summary_key)
            self.min_traffic_threshold = cfg.getfloat('default', min_traffic_threshold_key)

            self.output_forecast = None
            #self.output_forecast = cfg.get('default', output_forecast_key)
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