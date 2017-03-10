#!/bin/sh


cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.ext_site_app_merger.DoMerge

dbtype=$2
dbhost=$3
dbport=$4
dbname=$5
dbuser=$6
dbpwd=$7
process_time_day=`date --date="1 day ago" +%Y-%m-%d`
pathpattern=/var/data/kritter/data_pipeline/kritter_first_level_daily/$process_time_day/daily_externalsite.gz/


echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} ${tablename} ${hours}'

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${dbtype} ${dbhost} ${dbport} ${dbname} ${dbuser} ${dbpwd} "${pathpattern}"'

exec java  -cp "$CLASSPATH" $MAINCLASS ${dbtype} ${dbhost} ${dbport} ${dbname} ${dbuser} ${dbpwd} "${pathpattern}"


exit $?
