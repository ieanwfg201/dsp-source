

REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;

rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' USING PigStorage('') as (adId:chararray, nfr:chararray, req_time:chararray, count:long, processing_time);

convert_data = FOREACH raw_data GENERATE adId as adId, nfr as nfr, count as count, com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',req_time) AS req_time;

group_data =  GROUP convert_data BY (adId, nfr, req_time);

flatten_data = FOREACH  group_data  GENERATE FLATTEN(group), SUM(convert_data.count) as count;

store_data = FOREACH flatten_data GENERATE group::adId, group::nfr as nfr, group::req_time as req_time, count as count,'$PROCESS_TIME';

STORE store_data INTO '$OUTPUT/daily_ad_stats' USING PigStorage('');



