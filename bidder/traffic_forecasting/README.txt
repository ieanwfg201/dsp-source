To run :
python kritter_supply_forecast.py config.cfg logger.cfg

Dependencies :
1) thrift - Download thrift from  http://archive.apache.org/dist/thrift/0.9.0/
    Untar the thrift archive at some location (say THRIFT_ROOT)
    Internal dependency on g++ 
    Build the thrift codebase using g++ 
    Create a directory called site-packages under /usr/lib/python<version>/ . Create directory called 'thrift' under site-packages . 
    Copy the contents of THRIFT_ROOT/lib/py into /usr/lib/python<version>/site-packages/thrift
    export PYTHONPATH = /usr/lib/python<version>/site-packages/
2) MySQLdb - package MySQL-python

Code organisation :

utils/log_configure - For configuring the logger
bidder - All code related to input log lines, supply forecast and bidder (to come later).
bidder/input_log_adapter - Input log reader. The base64 encoded thrift serialized binary logs are decoded and deserialized into python objects
bidder/metadata_utils - Utilities for the metadata. The metadata are dimension id to name mapping and last processed metadata. last_processed_meta contains which process has processed till what time.
bidder/sql/metadata - sql for creating the metadata tables.
bidder/tools - tools for debugging.
bidder/utils - utilities to be used for log processing and creating different data structures and consumption of the same
bidder/traffic_forecasting - Supply forecasting specific code
bidder/traffic_forecasting/conf_utils - Utilities for reading the configuration file and creating the map corresponding to the same
bidder/traffic_forecasting/core - The core functionality of supply forecast segment generation.
bidder/traffic_forecasting/core/algorithm - The specific algorithms for supply forecasting. 
    generic_forecasting_algorithm contains the super class for all the algorithms. 
    exponential_forecasting_algorithm contains the specific algorithm for forecasting
bidder/traffic_forecasting/core/utils - Utilities to be used for traffic forecasting that are not specific to any algorithm
bidder/traffic_forecasting/impl/kritter - Specific implementation of supply forecasting.

