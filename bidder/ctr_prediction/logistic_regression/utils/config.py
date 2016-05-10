import ConfigParser
import re
import logging

dimensions_key = "dimensions"
rel_threshold_list_key = "relative_thresholds"
abs_threshold_list_key = "absolute_thresholds"
decay_factor_key = "decay_factor"
others_id_key = "others_id"
intercept_str_key = "intercept_str"
out_intercept_str_key = "out_intercept_str"
r_output_file_name_key = "r_output_file_name"
all_factors_file_name_key = "all_factors_file_name"
r_lr_dimensions_key = "r_lr_dimensions"
r_lr_factors_key = "r_lr_factors"
internal_others_id_key = "internal_others_id"
time_window_length_key = "time_window_length"
input_delimiter_key = "input_delimiter"
dimension_delimiter_key = "dim_delimiter"
fields_delimiter_key = "fields_delimiter"
output_delimiter_key = "output_delimiter"
time_dimension_key = "time_dimension"
impression_pos_key = "impression_pos"
click_pos_key = "click_pos"
impression_threshold_key = "impression_threshold"
segment_impression_count_key = "segment_impression_count"
absolute_ctr_ceiling_key = "absolute_ctr_ceiling"
objective_name_key = "objective_name"
dump_to_db_key = "dump-to-db"
model_id_key = 'model-id'
model_weight_key = 'weight'

database_host_key = "host"
database_user_key = "user"
database_password_key = "password"
database_dbname_key = "dbname"
dimension_id_name_table_key = "id_name_mapping_table"

output_database_host_key = 'host'
output_database_user_key = 'user'
output_database_password_key = 'password'
output_database_dbname_key = 'dbname'
model_table_name_key = 'model-table'

configLogger = logging.getLogger(__name__)

class ConfigParams:
    """ Class to hold elements of the configuration
        Contains the following :
        impression log directory path
        list of dimensions in the model
    """
    def __init__(self, config_file_path):
        cfg = ConfigParser.RawConfigParser()
        try:
            cfg.read(config_file_path)
            # All the config options except time window length are mandatory
            dimensions_list = cfg.get('default', dimensions_key)
            self.dimensions = re.split(', |,| ', dimensions_list)
            rel_threshold_list = cfg.get('default', rel_threshold_list_key)
            rel_thresholds = re.split(', |,| ', rel_threshold_list)
            self.rel_thresholds = []
            for threshold in rel_thresholds:
                # All the thresholds must be float or int values
                self.rel_thresholds.append(float(threshold))
            abs_threshold_list = cfg.get('default', abs_threshold_list_key)
            abs_thresholds = re.split(', |,| ', abs_threshold_list)
            self.abs_thresholds = []
            for threshold in abs_thresholds:
                # All the thresholds must be float or int values
                self.abs_thresholds.append(float(threshold))
            self.decay_factor = cfg.getfloat('default', decay_factor_key)
            self.others_id = cfg.getint('default', others_id_key)
            self.intercept_str = cfg.get('default', intercept_str_key)
            self.out_intercept_str = cfg.get('default', out_intercept_str_key)
            self.r_output_file_name = cfg.get('default', r_output_file_name_key)
            self.all_factors_file_name = cfg.get('default', all_factors_file_name_key)
            r_lr_dimensions_list = cfg.get('default', r_lr_dimensions_key)
            self.r_lr_dimensions = re.split(', |,| ', r_lr_dimensions_list)

            try:
                r_lr_factors_list = cfg.get('default', r_lr_factors_key)
                self.r_lr_factors = re.split(', |,| ', r_lr_factors_list)
            except:
                self.r_lr_factors = []

            self.internal_others_id = cfg.getint('default', internal_others_id_key)
            self.input_delimiter = cfg.get('default', input_delimiter_key)
            self.dimension_delimiter = cfg.get('default', dimension_delimiter_key)
            self.fields_delimiter = cfg.get('default', fields_delimiter_key)
            self.output_delimiter = cfg.get('default', output_delimiter_key)
            self.impression_pos = cfg.getint('default', impression_pos_key)
            self.click_pos = cfg.getint('default', click_pos_key)
            self.impression_threshold = cfg.getfloat('default', impression_threshold_key)
            self.segment_impression_count = cfg.getfloat('default', segment_impression_count_key)
            self.absolute_ctr_ceiling = cfg.getfloat('default', absolute_ctr_ceiling_key)
            self.objective_name = cfg.get('default', objective_name_key)
            self.time_dimension = cfg.get('default', time_dimension_key)

            self.database_host = cfg.get('db', database_host_key)
            self.database_user = cfg.get('db', database_user_key)
            self.database_password = cfg.get('db', database_password_key)
            self.database_dbname = cfg.get('db', database_dbname_key)
            self.dimension_id_name_table = cfg.get('db', dimension_id_name_table_key)

            try:
                self.dumpToDB = int(cfg.get('default', dump_to_db_key))
            except:
                self.dumpToDB = 0

            self.modelId = cfg.get('default', model_id_key)
            self.modelWeight = 0
            try:
                self.modelWeight = cfg.getfloat('default', model_weight_key)
            except:
                #model weight is not specified, so set to 0
                pass

            if self.dumpToDB != 0:
                self.outputDatabaseHost = cfg.get('output-db', output_database_host_key)
                self.outputDatabaseUser = cfg.get('output-db', output_database_user_key)
                self.outputDatabasePassword = cfg.get('output-db', output_database_password_key)
                self.outputDatabaseDbName = cfg.get('output-db', output_database_dbname_key)
                self.modelTable = cfg.get('output-db', model_table_name_key)

            # If time window is not present, default it to -1
            try:
                self.time_window_length = cfg.getint('default', time_window_length_key)
            except:
                self.time_window_length = -1
        except Exception as e:
            configLogger.error("Problem with config file %s", e)
            raise e


if __name__ == '__main__':
    # Test
    cfg = ConfigParams('../sample/config/config.cfg')
