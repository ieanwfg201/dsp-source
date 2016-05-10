REGISTER target/lib/elephant-bird-core-4.3.jar;
REGISTER target/lib/elephant-bird-pig-4.3.jar;
REGISTER target/lib/elephant-bird-hadoop-compat-4.3.jar;
REGISTER target/lib/libthrift-0.9.0.jar;
REGISTER $thrift_jar_path;
REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;
REGISTER target/lib/piggybank-0.12.0.jar;

       --  as (
       --       version: int,
       --       requestId: chararray,
       --       event: chararray,
       --       status: chararray,
       --       srcRequestId: chararray,
       --       impressionId: chararray,
       --       adId: int,
       --       campaignId: int,
       --       srcUrlHash: chararray,
       --       siteId: int,
       --       countryId: int,
       --       deviceId: long,
       --       deviceManufacturerId: int,
       --       deviceModelId: int,
       --       deviceOsId: int,
       --       aliasUrlId: chararray,
       --       thirdPartyTrackingUrlEventId: chararray,
       --       thirdPartyId: chararray,
       --       thirdPartyUrlHash: chararray,
       --       userAgent: chararray,
       --       ipAddress: chararray,
       --       xForwardedFor: chararray,
       --       urlExtraParameters: map[
       --         chararray
       --       ],
       --       time: long,
       --       eventTime: long,
       --       auction_price: double,
       --       auction_currency: chararray,
       --       auction_seat_id: chararray,
       --       auction_bid_id: chararray,
       --       auction_id: chararray,
       --       auction_imp_id: chararray,
       --       exchangeId: int,
       --       countryCarrierId: int,
       --       countryRegionId: int,
       --       selectedSiteCategoryId: int,
       --       urlVersion: int,
       --       inventorySource: int,
       --       bidprice_to_exchange: double,
       --       internal_max_bid: double,
       --       advertiser_bid: double,
       --       bidderModelId: int,
       --       marketplace_id: int,
       --       bid_price_to_exchange: double,
       --       slotId: int,
       --       deviceBrowserId: int,
       --       cpa_goal: double,
       --       supply_source_type: int,
       --       buyerUid: chararray,
       --       ext_supply_attr_internal_id: int
       --       connectionTypeId: int
       --       referer: chararray
       --       adv_inc_id: int,
       --       pub_inc_id: int
       --       tevent: chararray
       --       teventtype: chararray
       --     )

rmf $OUTPUT;
mkdir $OUTPUT;

DEFINE PostImpThriftBytesToTupleDef com.twitter.elephantbird.pig.piggybank.ThriftBytesToTuple('com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse');

post_imp_raw_data = load '$POST_IMP_INPUT_FILES' using TextLoader() as (record:chararray);

post_imp_decoded = FOREACH post_imp_raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
post_imp_decoded_data = FOREACH post_imp_decoded GENERATE PostImpThriftBytesToTupleDef($0);

post_imp_proj_data = FOREACH post_imp_decoded_data GENERATE '$PROCESS_TIME' as process_time,
            com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(PostImpressionRequestResponse.time * 1000,'yyyy-MM-dd HH:mm:ss', 'UTC'),
            com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(PostImpressionRequestResponse.eventTime * 1000,'yyyy-MM-dd HH:mm:ss', 'UTC'),
            PostImpressionRequestResponse.status, PostImpressionRequestResponse.event as event, PostImpressionRequestResponse.campaignId, 	
            PostImpressionRequestResponse.adId, PostImpressionRequestResponse.marketplace_id,
            PostImpressionRequestResponse.exchangeId, PostImpressionRequestResponse.siteId, PostImpressionRequestResponse.ext_supply_attr_internal_id, 
            PostImpressionRequestResponse.inventorySource, PostImpressionRequestResponse.supply_source_type,
            PostImpressionRequestResponse.advertiser_bid, PostImpressionRequestResponse.internal_max_bid, PostImpressionRequestResponse.bidprice_to_exchange, 
            PostImpressionRequestResponse.cpa_goal, PostImpressionRequestResponse.countryId, 
            PostImpressionRequestResponse.countryCarrierId, PostImpressionRequestResponse.deviceId, 
            PostImpressionRequestResponse.deviceManufacturerId, PostImpressionRequestResponse.deviceModelId,
            PostImpressionRequestResponse.deviceOsId, PostImpressionRequestResponse.deviceBrowserId, 
            PostImpressionRequestResponse.ipAddress, PostImpressionRequestResponse.userAgent,
            PostImpressionRequestResponse.xForwardedFor, PostImpressionRequestResponse.urlExtraParameters,  
            PostImpressionRequestResponse.version , PostImpressionRequestResponse.requestId, PostImpressionRequestResponse.srcRequestId,
            PostImpressionRequestResponse.impressionId , PostImpressionRequestResponse.srcUrlHash,  
            PostImpressionRequestResponse.aliasUrlId, PostImpressionRequestResponse.thirdPartyTrackingUrlEventId, 
            PostImpressionRequestResponse.thirdPartyId, PostImpressionRequestResponse.thirdPartyUrlHash,
            PostImpressionRequestResponse.auction_price, PostImpressionRequestResponse.auction_currency, PostImpressionRequestResponse.auction_seat_id, 
            PostImpressionRequestResponse.auction_bid_id, PostImpressionRequestResponse.auction_id, PostImpressionRequestResponse.auction_imp_id,  
            PostImpressionRequestResponse.countryRegionId, PostImpressionRequestResponse.selectedSiteCategoryId, PostImpressionRequestResponse.urlVersion,  
            PostImpressionRequestResponse.bidderModelId, PostImpressionRequestResponse.slotId, PostImpressionRequestResponse.buyerUid,
            PostImpressionRequestResponse.connectionTypeId, PostImpressionRequestResponse.referer;

post_imp_filter = FILTER post_imp_proj_data BY event == 'CLICK' OR event == 'CONVERSION';

STORE post_imp_filter INTO '$OUTPUT/logevents' USING PigStorage('');

post_imp_fraud = FOREACH post_imp_decoded_data GENERATE com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(PostImpressionRequestResponse.eventTime * 1000,'yyyy-MM-dd HH:00:00', 'UTC') as time,PostImpressionRequestResponse.status as terminationReason,
    PostImpressionRequestResponse.event as event,PostImpressionRequestResponse.siteId as siteId,
    PostImpressionRequestResponse.adId as adId,'$PROCESS_TIME' as process_time;

post_imp_fraud_group_data =  GROUP post_imp_fraud BY (process_time,time,siteId,adId,event,terminationReason);

post_imp_fraud_group_data_gen = FOREACH post_imp_fraud_group_data GENERATE FLATTEN(group), COUNT(post_imp_fraud);

STORE post_imp_fraud_group_data_gen INTO '$OUTPUT/fraud.gz' USING PigStorage('');

tracking_event_data = FOREACH post_imp_decoded_data GENERATE 
    PostImpressionRequestResponse.event as event,
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(PostImpressionRequestResponse.eventTime * 1000,'yyyy-MM-dd HH:00:00', 'UTC') as time,
    PostImpressionRequestResponse.pub_inc_id as pub_inc_id, 
    PostImpressionRequestResponse.siteId as siteId,
    PostImpressionRequestResponse.ext_supply_attr_internal_id as ext_supply_attr_internal_id, 
    PostImpressionRequestResponse.adv_inc_id as adv_inc_id,
    PostImpressionRequestResponse.campaignId as campaignId,
    PostImpressionRequestResponse.adId as adId,
    PostImpressionRequestResponse.status as terminationReason,
    PostImpressionRequestResponse.tevent as tevent,
    PostImpressionRequestResponse.teventtype as teventtype,
    PostImpressionRequestResponse.countryId as countryId, 
    PostImpressionRequestResponse.countryCarrierId as countryCarrierId,
    PostImpressionRequestResponse.deviceManufacturerId as deviceManufacturerId, 
    PostImpressionRequestResponse.deviceOsId as deviceOsId,
    '$PROCESS_TIME' as process_time;

tracking_event_filter = FILTER tracking_event_data BY event == 'TEVENT';

tracking_event_filter_grp = GROUP tracking_event_filter BY (process_time,time,pub_inc_id,siteId,ext_supply_attr_internal_id,adv_inc_id,campaignId,adId,terminationReason,tevent,teventtype,countryId,countryCarrierId,deviceManufacturerId,deviceOsId);


tracking_event_filter_grp_gen = FOREACH tracking_event_filter_grp GENERATE FLATTEN(group), COUNT(tracking_event_filter);

STORE tracking_event_filter_grp_gen INTO '$OUTPUT/tracking_event' USING PigStorage('');




