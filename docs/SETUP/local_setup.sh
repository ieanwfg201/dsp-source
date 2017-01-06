#!/bin/sh

LINKNAME=/usr/share/kritter

echo "###########################################################"
echo "# This script sets up path as per the expected PROD PATHS #"
echo "###########################################################"

if [ "$1" = "" ]; then
    echo "#################################################################################################################################"
    echo "# ERROR in UASGE: sh local_setup.sh <path to ysoserious dir>  <log_dir_USER> <log_dir_GROUP> <reporting USER> <reporting GROUP> #"
    echo "#                     Example: sh local_setup.sh /home/rohan/workspace/ysoserious tomcat7 tomcat7 rohan rohan                   #"
    echo "#################################################################################################################################"
    exit
fi

if [ "$2" = "" ]; then
    echo "#################################################################################################################################"
    echo "# ERROR in UASGE: sh local_setup.sh <path to ysoserious dir>  <log_dir_USER> <log_dir_GROUP> <reporting USER> <reporting GROUP> #"
    echo "#                     Example: sh local_setup.sh /home/rohan/workspace/ysoserious tomcat7 tomcat7 rohan rohan                   #"
    echo "#################################################################################################################################"
    exit
fi

if [ "$3" = "" ]; then
    echo "#################################################################################################################################"
    echo "# ERROR in UASGE: sh local_setup.sh <path to ysoserious dir>  <log_dir_USER> <log_dir_GROUP> <reporting USER> <reporting GROUP> #"
    echo "#                     Example: sh local_setup.sh /home/rohan/workspace/ysoserious tomcat7 tomcat7 rohan rohan                   #"
    echo "#################################################################################################################################"
    exit
fi

if [ "$4" = "" ]; then
    echo "#################################################################################################################################"
    echo "# ERROR in UASGE: sh local_setup.sh <path to ysoserious dir>  <log_dir_USER> <log_dir_GROUP> <reporting USER> <reporting GROUP> #"
    echo "#                     Example: sh local_setup.sh /home/rohan/workspace/ysoserious tomcat7 tomcat7 rohan rohan                   #"
    echo "#################################################################################################################################"
    exit
fi

if [ "$5" = "" ]; then
    echo "#################################################################################################################################"
    echo "# ERROR in UASGE: sh local_setup.sh <path to ysoserious dir>  <log_dir_USER> <log_dir_GROUP> <reporting USER> <reporting GROUP> #"
    echo "#                     Example: sh local_setup.sh /home/rohan/workspace/ysoserious tomcat7 tomcat7 rohan rohan                   #"
    echo "#################################################################################################################################"
    exit
fi

cd /usr/share

if [ ! -L $LINKNAME ]; then
    echo "sudo ln -s $1 $LINKNAME"
    sudo ln -s $1 $LINKNAME
else
    echo "$LINKNAME exists"
fi

cd -

echo "mkdir -p /var/app/kritter/alarms"
mkdir -p /var/app/kritter/alarms
chown -R $2:$3 /var/app/kritter/alarms

if [ ! -L /var/data/kritter/wurfl/wurfl_19700101.xml ]; then
    touch /var/data/kritter/wurfl/wurfl_19700101.xml
fi

echo "mkdir -p /var/data/kritter/wurfl"
mkdir -p /var/data/kritter/wurfl
chown -R $2:$3 /var/data/kritter/wurfl

echo "mkdir -p /var/data/kritter/51degrees"
mkdir -p /var/data/kritter/51degrees
chown -R $2:$3 /var/data/kritter/51degrees

if [ ! -L /var/data/kritter/wurfl/wurfl_19700101.xml ]; then
    touch /var/data/kritter/wurfl/wurfl_19700101.xml
fi

echo "mkdir -p /var/data/kritter/ctr/logistic_regression"
mkdir -p /var/data/kritter/ctr/logistic_regression

echo "mkdir -p /var/data/kritter/location/country/"
mkdir -p /var/data/kritter/location/country/
if [ ! -L /var/data/kritter/location/country/maxmind_country.csv ]; then
    touch /var/data/kritter/location/country/maxmind_country.csv
fi

if [ ! -L /var/data/kritter/location/country/maxmind_isp.csv ]; then
    touch /var/data/kritter/location/country/maxmind_isp.csv
fi

if [ ! -L /var/data/kritter/location/country/mobiclicks_country.csv ]; then
    touch /var/data/kritter/location/country/mobiclicks_country.csv
fi

if [ ! -L /var/data/kritter/location/country/mobiclicks_isp.csv ]; then
    touch /var/data/kritter/location/country/mobiclicks_isp.csv
fi

if [ ! -L /var/data/kritter/location/country/maxmind_country_input.csv ]; then
    touch /var/data/kritter/location/country/maxmind_country_input.csv
fi

if [ ! -L /var/data/kritter/location/country/maxmind_isp_input.csv ]; then
    touch /var/data/kritter/location/country/maxmind_isp_input.csv
fi

if [ ! -L /var/data/kritter/location/country/maxmind_block_id_input.csv ]; then
    touch /var/data/kritter/location/country/maxmind_block_id_input.csv
fi

if [ ! -L /var/data/kritter/location/country/maxmind_state_city_input.csv ]; then
    touch /var/data/kritter/location/country/maxmind_state_city_input.csv
fi

if [ ! -L /var/data/kritter/location/country/maxmind_state_code_input.csv ]; then
    touch /var/data/kritter/location/country/maxmind_state_code_input.csv
fi

if [ ! -L /var/data/kritter/location/country/maxmind_state_city.csv ]; then
    touch /var/data/kritter/location/country/maxmind_state_city.csv
fi

echo "mkdir -p /var/data/kritter/location/country_region/"
mkdir -p /var/data/kritter/location/country_region/
if [ ! -L /var/data/kritter/location/country_region/country_region.csv ]; then
    touch /var/data/kritter/location/country_region/country_region_empty.csv
    ln -s /var/data/kritter/location/country_region/country_region_empty.csv /var/data/kritter/location/country_region/country_region.csv
fi

echo "mkdir -p /var/data/kritter/data_pipeline/kritter_first_level"
mkdir -p /var/data/kritter/data_pipeline/kritter_first_level
chown -R $4:$5 /var/data/kritter/data_pipeline/kritter_first_level

echo "mkdir -p /var/data/kritter/data_pipeline/kritter_first_level_daily"
mkdir -p /var/data/kritter/data_pipeline/kritter_first_level_daily
chown -R $4:$5 /var/data/kritter/data_pipeline/kritter_first_level_daily

echo "mkdir -p /var/data/kritter/log_readytoprocess/billing"
mkdir -p /var/data/kritter/log_readytoprocess/billing
chown -R $4:$5 /var/data/kritter/log_readytoprocess/billing


echo "mkdir -p /var/data/kritter/billing/data/inprocess"
mkdir -p /var/data/kritter/billing/data/inprocess
chown -R $4:$5 /var/data/kritter/billing/data/inprocess
echo "mkdir -p /var/data/kritter/billing/data/done"
mkdir -p /var/data/kritter/billing/data/done
chown -R $4:$5 /var/data/kritter/billing/data/done
echo "mkdir -p /var/data/kritter/billing/data/fail"
mkdir -p /var/data/kritter/billing/data/fail
chown -R $4:$5 /var/data/kritter/billing/data/fail

echo "mkdir -p /var/data/kritter/log_readytoprocess/postimpression"
mkdir -p /var/data/kritter/log_readytoprocess/postimpression
chown -R $4:$5 /var/data/kritter/log_readytoprocess/postimpression

echo "mkdir -p /var/data/kritter/log_readytoprocess/adserving"
mkdir -p /var/data/kritter/log_readytoprocess/adserving
chown -R $4:$5 /var/data/kritter/log_readytoprocess/adserving

echo "mkdir -p /var/data/kritter/log_readytoprocess/adstats"
mkdir -p /var/data/kritter/log_readytoprocess/adstats
chown -R $4:$5 /var/data/kritter/log_readytoprocess/adstats

echo "mkdir -p /var/data/kritter/log_readytoprocess/exchange"
mkdir -p /var/data/kritter/log_readytoprocess/exchange
chown -R $4:$5 /var/data/kritter/log_readytoprocess/exchange


echo "mkdir -p /var/data/kritter/bidder/offline_bidder/cpm_cpm/rev_max"
mkdir -p /var/data/kritter/bidder/offline_bidder/cpm_cpm/rev_max
chown -R $4:$5 /var/data/kritter/bidder/offline_bidder/cpm_cpm/rev_max

echo "mkdir -p /var/log/kritter"
mkdir -p /var/log/kritter
chown -R $4:$5 /var/log/kritter

echo "mkdir -p /var/log/kritter/heapdump/adserving"
mkdir -p /var/log/kritter/heapdump/adserving
chown -R $4:$5 /var/log/kritter/heapdump/adserving

echo "mkdir -p /var/log/kritter/heapdump/postimpression"
mkdir -p /var/log/kritter/heapdump/postimpression
chown -R $4:$5 /var/log/kritter/heapdump/postimpression


echo "mkdir -p /var/log/kritter/postimpression"
mkdir -p /var/log/kritter/postimpression
chown -R $2:$3 /var/log/kritter/postimpression

echo "mkdir -p /var/log/kritter/adserving"
mkdir -p /var/log/kritter/adserving
chown -R $2:$3 /var/log/kritter/adserving

echo "mkdir -p /var/log/kritter/billing/data"
mkdir -p /var/log/kritter/billing/data
chown -R $4:$5 /var/log/kritter/billing

echo "mkdir -p /var/log/kritter/tern_file_pull/sample"
mkdir -p /var/log/kritter/tern_file_pull/sample
chown -R $4:$5 /var/log/kritter/tern_file_pull/sample

echo "mkdir -p /var/log/kritter/tern_file_pull/adserving_cpm_cpm_rev_max"
mkdir -p /var/log/kritter/tern_file_pull/adserving_cpm_cpm_rev_max
chown -R $4:$5 /var/log/kritter/tern_file_pull/adserving_cpm_cpm_rev_max

echo "mkdir -p /var/log/kritter/tern_file/adserving"
mkdir -p /var/log/kritter/tern_file/adserving
chown -R $4:$5 /var/log/kritter/tern_file/adserving

echo "mkdir -p /var/data/kritter/tern_file/destination/adserving"
mkdir -p /var/data/kritter/tern_file/destination/adserving
chown -R $4:$5 /var/data/kritter/tern_file/destination/adserving

echo "mkdir -p /var/data/kritter/tern_file/db/adserving"
mkdir -p /var/data/kritter/tern_file/db/adserving
chown -R $4:$5 /var/data/kritter/tern_file/db/adserving
su - $4 -c "python /usr/share/kritter/tern/tern_file/lib/journal/create_journal.py '/usr/share/kritter/tern/tern_file/sql/setup.sql' '/var/data/kritter/tern_file/db/adserving' 'tern_journal.db'"

echo "mkdir -p /var/log/kritter/tern_file/adstats"
mkdir -p /var/log/kritter/tern_file/adstats
chown -R $4:$5 /var/log/kritter/tern_file/adstats

echo "mkdir -p /var/log/kritter/tern_file/exchange"
mkdir -p /var/log/kritter/tern_file/exchange
chown -R $4:$5 /var/log/kritter/tern_file/exchange


echo "mkdir -p /var/data/kritter/tern_file/destination/adstats"
mkdir -p /var/data/kritter/tern_file/destination/adstats
chown -R $4:$5 /var/data/kritter/tern_file/destination/adstats

echo "mkdir -p /var/data/kritter/tern_file/destination/exchange"
mkdir -p /var/data/kritter/tern_file/destination/exchange
chown -R $4:$5 /var/data/kritter/tern_file/destination/exchange


echo "mkdir -p /var/data/kritter/tern_file/db/adstats"
mkdir -p /var/data/kritter/tern_file/db/adstats
chown -R $4:$5 /var/data/kritter/tern_file/db/adstats
su - $4 -c "python /usr/share/kritter/tern/tern_file/lib/journal/create_journal.py '/usr/share/kritter/tern/tern_file/sql/setup.sql' '/var/data/kritter/tern_file/db/adstats' 'tern_journal.db'"

echo "mkdir -p /var/data/kritter/tern_file/db/exchange"
mkdir -p /var/data/kritter/tern_file/db/exchange
chown -R $4:$5 /var/data/kritter/tern_file/db/exchange
su - $4 -c "python /usr/share/kritter/tern/tern_file/lib/journal/create_journal.py '/usr/share/kritter/tern/tern_file/sql/setup.sql' '/var/data/kritter/tern_file/db/exchange' 'tern_journal.db'"


echo "mkdir -p /var/log/kritter/tern_file/postimpression"
mkdir -p /var/log/kritter/tern_file/postimpression
chown -R $4:$5 /var/log/kritter/tern_file/postimpression

echo "mkdir -p /var/data/kritter/tern_file/destination/postimpression"
mkdir -p /var/data/kritter/tern_file/destination/postimpression
chown -R $4:$5 /var/data/kritter/tern_file/destination/postimpression

echo "mkdir -p /var/data/kritter/tern_file/db/postimpression"
mkdir -p /var/data/kritter/tern_file/db/postimpression
chown -R $4:$5 /var/data/kritter/tern_file/db/postimpression
su - $4 -c "python /usr/share/kritter/tern/tern_file/lib/journal/create_journal.py '/usr/share/kritter/tern/tern_file/sql/setup.sql' '/var/data/kritter/tern_file/db/postimpression' 'tern_journal.db'"

echo "mkdir -p /var/log/kritter/tern_file/postimpressionforbilling"
mkdir -p /var/log/kritter/tern_file/postimpressionforbilling
chown -R $4:$5 /var/log/kritter/tern_file/postimpressionforbilling

echo "mkdir -p /var/data/kritter/tern_file/destination/postimpressionforbilling"
mkdir -p /var/data/kritter/tern_file/destination/postimpressionforbilling
chown -R $4:$5 /var/data/kritter/tern_file/destination/postimpressionforbilling

echo "mkdir -p /var/data/kritter/tern_file/db/postimpressionforbilling"
mkdir -p /var/data/kritter/tern_file/db/postimpressionforbilling
chown -R $4:$5 /var/data/kritter/tern_file/db/postimpressionforbilling
su - $4 -c "python /usr/share/kritter/tern/tern_file/lib/journal/create_journal.py '/usr/share/kritter/tern/tern_file/sql/setup.sql' '/var/data/kritter/tern_file/db/postimpressionforbilling' 'tern_journal.db'"


echo "mkdir -p /var/log/kritter/tern_file/billing"
mkdir -p /var/log/kritter/tern_file/billing
chown -R $4:$5 /var/log/kritter/tern_file/billing

echo "mkdir -p /var/data/kritter/tern_file/destination/billing"
mkdir -p /var/data/kritter/tern_file/destination/billing
chown -R $4:$5 /var/data/kritter/tern_file/destination/billing

echo "mkdir -p /var/data/kritter/tern_file/db/billing"
mkdir -p /var/data/kritter/tern_file/db/billing
chown -R $4:$5 /var/data/kritter/tern_file/db/billing
su - $4 -c "python /usr/share/kritter/tern/tern_file/lib/journal/create_journal.py '/usr/share/kritter/tern/tern_file/sql/setup.sql' '/var/data/kritter/tern_file/db/billing' 'tern_journal.db'"

echo "mkdir -p /var/log/kritter/ctr/logistic_regression/"
mkdir -p /var/log/kritter/ctr/logistic_regression/
chown -R $2:$3  /var/log/kritter/ctr/logistic_regression/

echo "mkdir -p /var/data/kritter/bidder/daily/supply_forecast/"
mkdir -p /var/data/kritter/bidder/daily/supply_forecast/
chown -R $2:$3  /var/data/kritter/bidder/daily/supply_forecast/

echo "mkdir -p /var/data/kritter/bidder/cpc_cpm/rev_max/"
mkdir -p /var/data/kritter/bidder/cpc_cpm/rev_max/
chown -R $2:$3  /var/data/kritter/bidder/cpc_cpm/rev_max/

echo "mkdir -p /var/log/kritter/clicksr/logistic_regression"
mkdir -p /var/log/kritter/clicksr/logistic_regression
chown -R $2:$3 /var/log/kritter/clicksr/logistic_regression

echo "mkdir -p /var/log/kritter/kumbaya/ad_stats"
mkdir -p /var/log/kritter/kumbaya/ad_stats
chown -R $2:$3 /var/log/kritter/kumbaya/ad_stats

echo "mkdir -p /var/data/kritter"
mkdir -p /var/data/kritter
chown -R $4:$5 /var/data/kritter

echo "mkdir -p /var/log/kritter"
mkdir -p /var/log/kritter
chown -R $4:$5 /var/log/kritter

