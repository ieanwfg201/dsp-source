Following are the steps required to setup the CTR module :

Repository directories required :
/usr/share/kritter/bidder
/usr/share/kritter/utils/log_configure

Dependencies :
Install MySQLdb for python.
    - sudo apt-get install python-mysqldb
Install pandas, statsmodels.api, and scipy on centos(sandbox).
    - To install scipy and numpy :
            easy_install numpy
            sudo apt-get install liblapack-dev libblas-dev
            sudo apt-get install gfortran
            easy_install scipy
     - Now install statsmodels :
            easy_install patsy
            easy_install -U statsmodels
     - Install pandas :
            easy_install pandas
Install R language package.

Paths required :
    Following directory needs to exist and azkaban user needs to have write permissions to it (preferably give write permission to azkaban for everything under ctr):
        /var/data/kritter/ctr/logistic_regression/
        /var/log/kritter/ctr/logistic_regression/

Following sql need to be executed on db, and will populate metadata tables which are input to the CTR module.
    Sql files :
    /usr/share/kritter/bidder/sql/ctr_models_table.sql
    /usr/share/kritter/bidder/sql/metadata/kritter/ctr_dimension_name_id_mapping.sql

CTR job config location :
    /usr/share/kritter/bidder/ctr_prediction/logistic_regression/config/kritter/config.cfg

In the config, the following would require to be changed :
    [db] section
        This is where the previous sql's have been executed. This is the input to the CTR module
    [output-db] section
        This is where the output from CTR prediction will go. The model will be dumped in the "model-table" , which is currently ctr_models.
        Change the individual values according to your DB configuration.

CTR job logger config location :
    /usr/share/kritter/bidder/ctr_prediction/logistic_regression/config/kritter/logger.cfg

In the section [handler_simpleFileHandler] in logger.cfg, under args replace
    /var/log/kritter/ctr/logistic_regression/log.output
    by the appropriate path (if required).

Manual run:
    To run the CTR prediction job manually, do the following :
        python /usr/share/kritter/bidder/ctr_prediction/logistic_regression/impl/kritter/logistic_regression_impl.py <config_file> <logger_config> <input_path_regex> <existing_segment_history> <new_segment_history> <intermediate_csv_file> /usr/share/kritter/bidder/ctr_prediction/logistic_regression/core/logistic_regression_solver.r <new_output_feed_file> <new_output_link>
    Example:
        python /usr/share/kritter/bidder/ctr_prediction/logistic_regression/impl/kritter/logistic_regression_impl.py /usr/share/kritter/bidder/ctr_prediction/logistic_regression/config/kritter/config.cfg /usr/share/kritter/bidder/ctr_prediction/logistic_regression/config/kritter/logger.cfg '/var/data/kritter/data_pipeline/kritter_first_level/2014-11-27-*/input_for_ctr.gz/2014-11-27-*/2*' /var/data/kritter/ctr/logistic_regression/clsegments-2014-11-26 /var/data/kritter/ctr/logistic_regression/clsegments-2014-11-27 /var/data/kritter/ctr/logistic_regression/ctr-lr-csv-2014-11-27-06-02 /usr/share/kritter/bidder/ctr_prediction/logistic_regression/core/logistic_regression_solver.r /var/data/kritter/ctr/logistic_regression/ctr-lr-out.feed-2014-11-27-06-02 /var/data/kritter/ctr/logistic_regression/ctr-lr.out

Scheduling :
    Runs in azkaban. Scheduled at 4 hour intervals.
    Create the azkaban job and properties zip in the following manner :
    zip ctr_prediction_lr.zip -r /usr/share/kritter/kumbaya/azkaban_wf/kritter/ctr_prediction/logistic_regression/

Create Project in azkaban.
    Project Name : ctr_prediction_lr
    Description : CTR lr prediction job
    upload ctr_prediction_lr.zip
    Then schedule the azkaban job.
    Recurrence should be set to 4 hours.
    Concurrency should be set at "Skip Execution".
