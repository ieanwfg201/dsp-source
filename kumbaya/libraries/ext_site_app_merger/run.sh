#!/bin/sh

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

dbtype=$1
dbhost=$2
dbport=$3
dbname=$4
dbuser=$5
dbpwd=$6
pathpattern=$7
EXTSITEAUTOAPPROVAL=$8

if [ "$1" = "" ]; then
    dbtype=MYSQL
fi

if [ "$2" = "" ]; then
    dbhost=localhost
fi

if [ "$3" = "" ]; then
    dbport=3306
fi

if [ "$4" = "" ]; then
    dbname=kritter
fi

if [ "$5" = "" ]; then
    dbuser=root
fi

if [ "$6" = "" ]; then
    dbpwd=password
fi

if [ "$7" = "" ]; then
    pathpattern="/home/rohan/testdata/kritter/manual/daily_externalsite.gz/part*"
fi

if [ "$8" = "" ]; then
    EXTSITEAUTOAPPROVAL=true
fi


MAINCLASS=com.kritter.kumbaya.libraries.ext_site_app_merger.DoMerge

CLASSPATH=${CLASSPATH}

echo "#####################################################"

echo "java  -cp '$CLASSPATH' $MAINCLASS $dbtype $dbhost $dbport $dbname $dbuser $dbpwd '$pathpattern' $EXTSITEAUTOAPPROVAL"
eval "java  -cp '$CLASSPATH' $MAINCLASS $dbtype $dbhost $dbport $dbname $dbuser $dbpwd '$pathpattern' $EXTSITEAUTOAPPROVAL"


