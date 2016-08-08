import ConfigParser
import logging
import traceback

outputDBHostKey = 'host'
outputDBUserNameKey = 'user'
outputDBPasswordKey = 'password'
outputDBNameKey = 'dbname'

class ConfigParams:
    """
    Class to hold elements of configuration
    """
    def __init__(self, configFilePath):
        self.configLogger = logging.getLogger(__name__)

        cfg = ConfigParser.RawConfigParser()
        try:
            cfg.read(configFilePath)

            self.outputDBHost = cfg.get('output-db', outputDBHostKey)
            self.outputDBUserName = cfg.get('output-db', outputDBUserNameKey)
            self.outputDBPassword = cfg.get('output-db', outputDBPasswordKey)
            self.outputDBName = cfg.get('output-db', outputDBNameKey)

            self.configLogger.info('Read config at : %s and loaded all the values', configFilePath)
        except Exception, e:
            self.configLogger.error('Problem with config file : %s', traceback.format_exc())
