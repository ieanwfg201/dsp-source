#!/bin/sh

cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.billing.ad_budget.reset.ResetMain

eventtype=${2}
dbtype=${3}
dbhost=${4}
dbport=${5}
dbname=${6}
dbuser=${7}
dbpwd=${8}

echo 'java  -cp "$CLASSPATH" $MAINCLASS "${eventtype}" "${dbtype}" "${dbhost}" "${dbport}" "${dbname}" "${dbuser}" "${dbpwd}"'

java  -cp "$CLASSPATH" $MAINCLASS ${eventtype} ${dbtype} ${dbhost} ${dbport} ${dbname} ${dbuser} ${dbpwd}

exit $?
