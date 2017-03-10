

REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;

rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' USING PigStorage('') as (time:chararray,advId:int,campaignId:int,adId:int,uu_count:long);

convert_data = FOREACH raw_data GENERATE com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time,advId as advId, adId as adId,
                campaignId as campaignId , uu_count as uu_count;

group_data =  GROUP raw_data BY (time,advId,campaignId,adId);

flatten_data = FOREACH group_data {
                GENERATE FLATTEN(group),
                SUM(raw_data.uu_count) as uu_count;
            }

flatten_data_store = FOREACH flatten_data GENERATE '$PROCESS_TIME',group::advId,group::campaignId,group::adId,uu_count;

STORE flatten_data_store INTO '$OUTPUT/usercount.txt' USING PigStorage('');


