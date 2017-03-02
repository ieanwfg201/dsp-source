#!/bin/sh

${loader_code_path} ${OUTPUT}/'${process_time_dir}' ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname} ${dbport} ${dbtype} '${process_time}' '${process_time_dir}' ${limitedtablename}




process_time="${11}"
process_time_dir="${12}"
echo "{\"process_time\":\"${process_time}\",\"process_time_dir\":\"${process_time_dir}\"}" >> $JOB_OUTPUT_PROP_FILE


cd ${1}


CLASSPATH=

for f in target/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

for f in target/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:$f;
done

MAINCLASS=com.kritter.kumbaya.libraries.data_loader.LoadData

filepath=${2}/first_level/part*
limitedfilepath=${2}/limited_first_level/part*
tablename=${3}
delimiter=${4}
dbhost=${5}
dbuser=${6}
dbpwd=${7} 
dbname=${8}
dbport=${9}
dbtype=${10}
processing_time=${11}
limitedtablename=${13}


echo 'java  -cp "$CLASSPATH" $MAINCLASS ${filepath} ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}'
java  -cp "$CLASSPATH" $MAINCLASS ${filepath} ${tablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}

echo 'java  -cp "$CLASSPATH" $MAINCLASS ${limitedfilepath} ${limitedtablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}'
java  -cp "$CLASSPATH" $MAINCLASS ${limitedfilepath} ${limitedtablename} ${delimiter} ${dbhost} ${dbuser} ${dbpwd} ${dbname}  ${dbport} ${dbtype} ${processing_time}



exit $?
