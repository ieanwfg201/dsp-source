Setup instructions :

    1) untar the tar ball. The directory will contain three directories : 
        bidder
        utils
        rtb

    2) Run rtb/lp_solver/clp_install/clp_install.sh
    
    3) Go into rtb/lp_solver/clp_dual_solver/ directory and execute the following command :
        make

    4) Move the bidder and utils directories into the following location :
        <python base directory>/site-packages/

    5) export PYTHONPATH=<python base directory>/site-packages/

    6) Go to bidder/sql and execute bidder_models_table.sql

    7) Go to bidder/sql/metadata and execute dimension_name_id_mapping.sql
    
    8) Ensure Demand data tables are present in the database

1. Supply forecast job :
    inputs : 
        a) reporting job summary
        b) demand data (billing)

    output :
        a) supply forecast file (with the schema as specified.  and  delimited file).

    command :
        python <python base dir>/site-packages/bidder/traffic_forecasting/impl/kritter/kritter_summary_supply_forecast.py <config file> <logger config> <reporting_summary_path> <forecast_output_file>

    *** Notes : The config file and logger config in the command have some paths in them. Change them according to your system. The following keys will have to be changed 
        Config file :
            a) key : existing_forecast
        Logger config :
            a) key : args 
                Change first value within quotes

2. Offline Bidder jobs :

    (A) MPS creation job :
        inputs :
            a) supply forecast
            b) demand data (targeting)

        output :
            a) MPS file representing the linear programming equation 
            b) metadata for converting lp solution to feed. The metadata consists of mapping from campaign id's to the variable names in MPS file

        command :
            python <python base dir>/site-packages/bidder/bidding_strategies/cpm_cpm/rev_max/impl/create_model.py <config_path>/<config file> <config_path>/logger.cfg <forecast output file> <mps output file>
        python 

        *** Notes : The config file and logger config in the command have some paths in them. Change them according to your system. The following keys will have to be changed 
            Config file :
                a) key : segment-map-file
                b) key : ad-name-map-file
            Logger config :
                a) key : args 
                    Change first value within quotes

    (B) LP solution job :
        inputs :
            a) MPS file (representing the LP)

        output :
            a) solution of LP 
                schema :
                    A<variable name><output alpha value> or
                    B<variable name><output beta value>

        command :
            ./rtb/lp_solver/clp_dual_solver/bin/dual_solver <lp solver config> <mps output file> <lp solution file>

    (C) LP solution to feed conversion job :
        inputs :
            a) LP solution file
            b) Metadata from MPS creation job.

        output :
            a) row in bidder_models table.
                schema of the feed data :
                    a<campaignid>{alpha value>
                    b<dimension 1><dimension 2><dimension 3>......<dimension 4>{beta value>
                    
        command :
            python <python base dir>/site-packages/bidder/bidding_strategies/cpm_cpm/rev_max/impl/convert_lp_results.py <config path> <logger config path> <lp solution file>

    *** Notes : config files for job (A) and (C) are the same

