#!/bin/sh

cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.billing.budget_reset.reset.BudgetReset

dbtype=${2}
dbhost=${3}
dbport=${4}
dbname=${5}
dbuser=${6}
dbpwd=${7}

echo 'java  -cp "$CLASSPATH" $MAINCLASS "${dbtype}" "${dbhost}" "${dbport}" "${dbname}" "${dbuser}" "${dbpwd}"'

java  -cp "$CLASSPATH" $MAINCLASS ${dbtype} ${dbhost} ${dbport} ${dbname} ${dbuser} ${dbpwd}

exit $?
