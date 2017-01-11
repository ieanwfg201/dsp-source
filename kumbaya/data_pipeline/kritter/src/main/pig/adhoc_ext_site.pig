

REGISTER target/lib/elephant-bird-core-4.3.jar;
REGISTER target/lib/elephant-bird-pig-4.3.jar;
REGISTER target/lib/elephant-bird-hadoop-compat-4.3.jar;
REGISTER target/lib/libthrift-0.9.0.jar;
REGISTER $thrift_jar_path;
REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;
REGISTER target/lib/piggybank-0.12.0.jar;

DEFINE ThriftBytesToTupleDef com.twitter.elephantbird.pig.piggybank.ThriftBytesToTuple('com.kritter.adserving.thrift.struct.AdservingRequestResponse');

rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' using TextLoader() as (record:chararray);

decoded = FOREACH raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
decoded_data = FOREACH decoded GENERATE ThriftBytesToTupleDef($0);

proj_data = FOREACH decoded_data GENERATE AdservingRequestResponse.terminationReason as terminationReason, 
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(AdservingRequestResponse.time * 1000,'yyyy-MM-dd HH:00:00', '$tz') as time, 
 AdservingRequestResponse.pub_inc_id as pub_inc_id,  AdservingRequestResponse.ext_supply_name as ext_supply_name,
     AdservingRequestResponse.ext_supply_id as ext_supply_id,
    FLATTEN(com.kritter.kumbaya.libraries.pigudf.ExtractFromBagOfTupleAdserving(AdservingRequestResponse.impressions , -1, '-1', -1, -1 , -1, '-1', 0.0, -1,-1, 0.0, -1, 1, 0))
     as (version:int,impressionId:chararray,creativeId:int,adId:int,campaignId:int,advertiserId:chararray,bidValue:double, slotid:int, adv_inc_id:int, predictedCTR:double, marketplace:int, total_request:int, total_impression:int);



filter_data = FILTER proj_data BY terminationReason == 'HEALTHY_REQUEST';


group_data = GROUP filter_data BY (time,pub_inc_id,ext_supply_id,ext_supply_name);


group_data_gen = FOREACH group_data GENERATE FLATTEN(group), SUM(filter_data.total_request) as total_request, SUM(filter_data.total_impression) as total_impression;

STORE group_data_gen INTO '$OUTPUT/extsitereqimp.gz' USING PigStorage('');
