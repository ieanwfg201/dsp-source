import ConfigParser
import re
import logging

dimension_list_key = 'dimensions'
dimension_name_list_key = 'dimension-names'
segment_map_file_name_key = 'segment-map-file'
ad_name_map_file_name_key = 'ad-name-map-file'
fields_delimiter_key = "fields_delimiter"
dim_delimiter_key = "dim_delimiter"
output_fields_delimiter_key = "output_fields_delimiter"
output_dim_delimiter_key = "output_dim_delimiter"
model_id_key = 'model-id'
time_dimension_key = "time-dimension"
time_window_duration_key = "time-window-duration"
dump_to_db_key = "dump-to-db"
model_weight_key = 'weight'
unknown_bid_ratio_key = 'unknown-bid-ratio'
network_cut_ratio_key = 'network-cut-ratio'
others_id_key = 'others-id'
campaign_id_dim_name_key = 'campaign-id-dim-name'
ctr_segment_dimension_list_key = 'ctr-dimensions'
ctr_dim_coeff_delimiter_key = 'ctr-value-coeff-delimiter'
supply_forecast_dimension_length_key = 'supply-forecast-dimension-num'
supply_forecast_publisher_pos_key = 'supply-forecast-publisher-pos'
network_publisher_id_key = 'network-publisher-id'
alpha_cut_key = 'alpha-cut'

database_host_key = 'host'
database_user_key = 'user'
database_password_key = 'password'
database_dbname_key = 'dbname'
campaign_table_name_key = 'campaign-table'
ad_table_name_key = 'ad-table'
budget_table_name_key = 'budget-table'
output_database_host_key = 'output-host'
output_database_user_key = 'output-user'
output_database_password_key = 'output-password'
output_database_dbname_key = 'output-dbname'
model_table_name_key = 'model-table'

configLogger = logging.getLogger(__name__)


class ConfigParams:
    """ Class to hold elements of the configuration """

    def __init__(self, config_file_path):
        """ Constructor for the class """
        cfg = ConfigParser.RawConfigParser()
        try:
            cfg.read(config_file_path)
            dimensions_list = cfg.get('default', dimension_list_key)
            self.dimensions = re.split(', |,| ', dimensions_list)
            dimension_name_list = cfg.get('default', dimension_name_list_key)
            self.dimensionNames = re.split(', |,| ', dimension_name_list)
            self.segmentMapFileName = cfg.get('default', segment_map_file_name_key)
            self.adNameMapFileName = cfg.get('default', ad_name_map_file_name_key)
            self.fieldsDelimiter = cfg.get('default', fields_delimiter_key)
            self.dimDelimiter = cfg.get('default', dim_delimiter_key)
            self.outputFieldsDelimiter = cfg.get('default', output_fields_delimiter_key)
            self.outputDimDelimiter = cfg.get('default', output_dim_delimiter_key)
            self.modelId = cfg.get('default', model_id_key)
            self.unknownBidRatio = float(cfg.get('default', unknown_bid_ratio_key))
            self.networkCutRatio = float(cfg.get('default', network_cut_ratio_key))
            self.othersId = cfg.getint('default', others_id_key)
            self.campaignIdDimName = cfg.get('default', campaign_id_dim_name_key)
            ctr_dimension_list = cfg.get('default', ctr_segment_dimension_list_key)
            self.ctrDimensions = re.split(', |,| ', ctr_dimension_list)
            self.ctrDimCoeffDelimiter = cfg.get('default', ctr_dim_coeff_delimiter_key)
            self.supplyForecastDimCount = cfg.getint('default', supply_forecast_dimension_length_key)
            self.publisherIdPos = cfg.getint('default', supply_forecast_publisher_pos_key)
            self.directPublisherId = cfg.getint('default', network_publisher_id_key)
            try:
                self.alphaCut = float(cfg.get('default', alpha_cut_key))
            except:
                self.alphaCut = 0

            try:
                self.dumpToDB = int(cfg.get('default', dump_to_db_key))
            except:
                self.dumpToDB = 0

            self.modelWeight = 0
            try:
                self.modelWeight = cfg.get('default', model_id_key)
            except:
                #model weight is not specified, so set to 0
                pass

            self.timeDimension = -1
            self.timeWindowDuration = 0
            try:
                self.timeDimension = int(cfg.get('default', time_dimension_key))
            except:
                #Time dimension not present
                pass

            try:
                self.timeWindowDuration = int(cfg.get('default', time_window_duration_key))
            except Exception, e:
                # time window duration must be present if time dimension is present
                if self.timeDimension >= 0:
                    raise e

            self.databaseHost = cfg.get('db', database_host_key)
            self.databaseUser = cfg.get('db', database_user_key)
            self.databasePassword = cfg.get('db', database_password_key)
            self.databaseDbName = cfg.get('db', database_dbname_key)
            self.campaignTable = cfg.get('db', campaign_table_name_key)
            self.adTable = cfg.get('db', ad_table_name_key)
            self.budgetTable = cfg.get('db', budget_table_name_key)

            self.outputDatabaseHost = cfg.get('db', output_database_host_key)
            self.outputDatabaseUser = cfg.get('db', output_database_user_key)
            self.outputDatabasePassword = cfg.get('db', output_database_password_key)
            self.outputDatabaseDbName = cfg.get('db', output_database_dbname_key)
            if self.dumpToDB != 0:
                self.modelTable = cfg.get('db', model_table_name_key)
        except Exception as e:
            configLogger.error("Problem with config file %s", e)
            raise e


if __name__ == '__main__':
    cfg = ConfigParams('../sample/config.cfg')

    print cfg.dimensions
    print cfg.segmentMapFileName
    print cfg.adNameMapFileName
    print cfg.fieldsDelimiter
    print cfg.dimDelimiter
    print cfg.modelId
    print cfg.unknownBidRatio
    print cfg.networkCutRatio
    print cfg.databaseHost
    print cfg.databaseUser
    print cfg.databasePassword
    print cfg.databaseDbName
    print cfg.campaignTable
    print cfg.adTable
    print cfg.budgetTable
    print cfg.outputDatabaseHost
    print cfg.outputDatabaseUser
    print cfg.outputDatabasePassword
    print cfg.outputDatabaseDbName
    print cfg.modelTable
