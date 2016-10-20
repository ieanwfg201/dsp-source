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
email=$3
dbtype=$4
dbhost=$5
dbport=$6
dbname=$7
dbuser=$8
dbpwd=$9

if [ "$1" = "" ]; then
    echo "Incorrect Usage: sh createRootAccount.sh <userid> <pwd> <email>"
    echo "Example: sh createRootAccount.sh userid1 pwd1 email@email.com"
    exit
fi


if [ "$2" = "" ]; then
    echo "Incorrect Usage: sh createRootAccount.sh <userid> <pwd> <email>"
    echo "Example: sh createRootAccount.sh userid1 pwd1 email@email.com"
    exit
fi

if [ "$3" = "" ]; then
    echo "Incorrect Usage: sh createRootAccount.sh <userid> <pwd> <email>"
    echo "Example: sh createRootAccount.sh userid1 pwd1 email@email.com"
    exit
fi

if [ "$4" = "" ]; then
    dbtype=MYSQL
fi

if [ "$5" = "" ]; then
    dbhost=localhost
fi

if [ "$6" = "" ]; then
    dbport=3306
fi

if [ "$7" = "" ]; then
    dbname=kritter
fi

if [ "$8" = "" ]; then
    dbuser=root
fi

if [ "$9" = "" ]; then
    dbpwd=password
fi

MAINCLASS=com.kritter.kritterui.api.manual.createadmin.CreateRootAccount

CLASSPATH=${CLASSPATH}:$EXTRA_THRIFT_JAR

echo "java  -cp "$CLASSPATH" $MAINCLASS $userid $userpwd $dbtype $dbhost "$dbport" $dbname $dbuser $dbpwd $email"
exec java  -cp "$CLASSPATH" $MAINCLASS $userid $userpwd $dbtype $dbhost "$dbport" $dbname $dbuser $dbpwd $email



