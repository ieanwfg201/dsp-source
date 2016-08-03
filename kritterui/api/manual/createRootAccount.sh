#!/bin/sh

CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

userid=$1
userpwd=$2
dbtype=$3
dbhost=$4
dbport=$5
dbname=$6
dbuser=$7
dbpwd=$8

if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh createRootAccount.sh <userid> <pwd>"
    echo "Example: sh createRootAccount.sh userid1 pwd1"
    exit
fi


if [ "$2" = "" ]; then
    echo "Incorrect Usage: sh createRootAccount.sh <userid> <pwd>"
    echo "Example: sh createRootAccount.sh userid1 pwd1"
    exit
fi

if [ "$3" = "" ]; then
    dbtype=MYSQL
fi

if [ "$4" = "" ]; then
    dbhost=localhost
fi

if [ "$5" = "" ]; then
    dbport=3306
fi

if [ "$6" = "" ]; then
    dbname=kritter
fi

if [ "$7" = "" ]; then
    dbuser=root
fi

if [ "$8" = "" ]; then
    dbpwd=password
fi

MAINCLASS=com.kritter.kritterui.api.manual.createadmin.CreateRootAccount

CLASSPATH=${CLASSPATH}:$EXTRA_THRIFT_JAR

echo "java  -cp "$CLASSPATH" $MAINCLASS $userid $userpwd $dbtype $dbhost "$dbport" $dbname $dbuser $dbpwd"
exec java  -cp "$CLASSPATH" $MAINCLASS $userid $userpwd $dbtype $dbhost "$dbport" $dbname $dbuser $dbpwd



