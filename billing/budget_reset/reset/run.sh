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
MAINCLASS=$7

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
    dbname=optimad
fi
if [ "$5" = "" ]; then
    dbuser=root
fi
if [ "$6" = "" ]; then
    dbpwd=password
fi
if [ "$7" = "" ]; then
    MAINCLASS=com.kritter.billing.budget_reset.reset.BudgetReset
fi

CLASSPATH=${CLASSPATH}

echo "######################## START #############################"

echo "java  -cp '$CLASSPATH' $MAINCLASS $dbtype $dbhost $dbport $dbname $dbuser $dbpwd"
eval "java  -cp '$CLASSPATH' $MAINCLASS $dbtype $dbhost $dbport $dbname $dbuser $dbpwd"

echo "######################## END   #############################"


