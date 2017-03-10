

REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;

rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' USING PigStorage('') as (process_time,time:chararray,siteId:int,adId:int,event:chararray,terminationReason:chararray,count:int,
            campaignId:int , adv_inc_id:int ,pub_inc_id:int);

convert_data = FOREACH raw_data GENERATE com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time,siteId as siteId, adId as adId,
                event as event, terminationReason as terminationReason, count as count, campaignId as campaignId , adv_inc_id as adv_inc_id ,pub_inc_id as pub_inc_id;

group_data =  GROUP raw_data BY (time,siteId,adId,event,terminationReason,campaignId , adv_inc_id ,pub_inc_id);

flatten_data = FOREACH group_data {
                GENERATE FLATTEN(group),
                SUM(raw_data.count) as count;
            }

flatten_data_store = FOREACH flatten_data GENERATE '$PROCESS_TIME', group::time,group::siteId,group::adId,group::event,group::terminationReason,count,
                        group::campaignId , group::adv_inc_id , group::pub_inc_id; 

STORE flatten_data_store INTO '$OUTPUT/fraud' USING PigStorage('');



tracking_event = load '$TRACKING_EVENT_INPUT_FILES' USING PigStorage('') as (process_time:chararray,time:chararray,pub_inc_id:int,siteId:int,ext_supply_attr_internal_id:int,adv_inc_id:int,campaignId:int,adId:int,terminationReason:chararray,tevent:chararray,teventtype:chararray,countryId:int,countryCarrierId:int,deviceManufacturerId:int,deviceOsId:int, event_count:int);

tracking_event_convert = FOREACH tracking_event GENERATE com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, pub_inc_id as pub_inc_id,siteId as siteId,ext_supply_attr_internal_id as ext_supply_attr_internal_id,adv_inc_id as adv_inc_id,campaignId as campaignId, adId as adId,terminationReason as terminationReason,tevent as tevent,teventtype as teventtype,countryId as countryId,countryCarrierId as countryCarrierId,deviceManufacturerId as deviceManufacturerId,deviceOsId as deviceOsId, event_count as event_count;

tracking_event_group =  GROUP tracking_event_convert BY  (time, pub_inc_id ,siteId ,ext_supply_attr_internal_id ,adv_inc_id ,campaignId , adId ,terminationReason ,tevent ,teventtype ,countryId ,countryCarrierId ,deviceManufacturerId, deviceOsId);

tracking_event_flatten = FOREACH tracking_event_group {
                GENERATE FLATTEN(group),
                SUM(tracking_event_convert.event_count) as event_count;
            }

tracking_event_store = FOREACH tracking_event_flatten GENERATE '$PROCESS_TIME', group::time, group::pub_inc_id ,group::siteId ,group::ext_supply_attr_internal_id ,group::adv_inc_id ,group::campaignId , group::adId ,group::terminationReason ,group::tevent ,group::teventtype ,group::countryId ,group::countryCarrierId ,group::deviceManufacturerId, group::deviceOsId ,event_count;

STORE tracking_event_store INTO '$OUTPUT/tracking_event' USING PigStorage('');

