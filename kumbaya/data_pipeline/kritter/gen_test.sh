#!/bin/sh

cd ../../../skunkworks/log_gen/

./run.sh  com.kritter.adserving.thrift.struct.AdservingRequestResponse /home/rohan/testdata/kritter/gen_adserve_log.txt 10 /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.kritter.skunkworks.generate.GenerateThriftLogs

cd -
cd ../../../skunkworks/thrift_b64_reader

./run.sh com.kritter.adserving.thrift.struct.AdservingRequestResponse /home/rohan/testdata/kritter/gen_adserve_log.txt /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.kritter.skunworks.thrift_b64_reader.ReadLogs

cd -
cd ../../../skunkworks/thrift_pig_pretty_print

./run.sh com.kritter.adserving.thrift.struct.AdservingRequestResponse /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.twitter.elephantbird.pig.util.ThriftToPig

cd -
cd ../../../skunkworks/log_gen/

./run.sh  com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse /home/rohan/testdata/kritter/gen_postimp_log.txt 10 /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.kritter.skunkworks.generate.GenerateThriftLogs

cd -
cd ../../../skunkworks/thrift_b64_reader

./run.sh com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse /home/rohan/testdata/kritter/gen_postimp_log.txt /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.kritter.skunworks.thrift_b64_reader.ReadLogs

cd -
cd ../../../skunkworks/thrift_pig_pretty_print

./run.sh com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.twitter.elephantbird.pig.util.ThriftToPig

cd -
cd ../../../skunkworks/log_gen/

./run.sh  com.kritter.postimpression.thrift.struct.Billing /home/rohan/testdata/kritter/gen_billing_log.txt 10 /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.kritter.skunkworks.generate.GenerateThriftLogs

cd -
cd ../../../skunkworks/thrift_b64_reader

./run.sh com.kritter.postimpression.thrift.struct.Billing /home/rohan/testdata/kritter/gen_billing_log.txt /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.kritter.skunworks.thrift_b64_reader.ReadLogs

cd -
cd ../../../skunkworks/thrift_pig_pretty_print

./run.sh com.kritter.postimpression.thrift.struct.Billing /home/rohan/workspace/ysoserious/data_structs/target/com.kritter.thrift-structs-1.0.0.jar com.twitter.elephantbird.pig.util.ThriftToPig

