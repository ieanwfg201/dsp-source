

REGISTER target/lib/elephant-bird-core-4.3.jar;
REGISTER target/lib/elephant-bird-pig-4.3.jar;
REGISTER target/lib/elephant-bird-hadoop-compat-4.3.jar;
REGISTER target/lib/libthrift-0.9.0.jar;
REGISTER $thrift_jar_path;
REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;
REGISTER target/lib/piggybank-0.12.0.jar;

rmf $OUTPUT;
mkdir $OUTPUT;

DEFINE PostImpThriftBytesToTupleDef com.twitter.elephantbird.pig.piggybank.ThriftBytesToTuple('com.kritter.postimpression.thrift.struct.PostImpressionRequestResponse');

post_imp_raw_data = load '$POST_IMP_INPUT_FILES' using TextLoader() as (record:chararray);
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
       --       ext_supply_attr_internal_id: int,
       --       connectionTypeId: int,
       --       referer: chararray,
       --       adv_inc_id: int,
       --       pub_inc_id: int,
       --       tevent: chararray,
       --       teventtype: chararray,
       --       deviceType: int,
       --       bidFloor: double,
       --       exchangeUserId: chararray,
       --       kritterUserId: chararray,
       --       externalSiteAppId: chararray,
       --       ifa: chararray,
       --       dpidmd5: chararray,
       --       dpidsha1: chararray,
       --       macsha1: chararray,
       --       macmd5: chararray,
       --       stateId: int,
       --       cityId: int,
       --       adpositionId: int,
       --       channelId: int
       --     )

post_imp_decoded = FOREACH post_imp_raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
post_imp_decoded_data = FOREACH post_imp_decoded GENERATE PostImpThriftBytesToTupleDef($0);


post_imp_proj_data = FOREACH post_imp_decoded_data GENERATE PostImpressionRequestResponse.status as terminationReason, 
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(PostImpressionRequestResponse.eventTime * 1000,'yyyy-MM-dd HH:00:00', '$tz') as time, 
    PostImpressionRequestResponse.siteId as siteId, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CLICK', '1', '0') as total_click, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'RENDER', '1', 'INT_EXCHANGE_WIN', '1','BEVENT_CSCWIN', '1' ,'0') as total_csc, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'WIN_NOTIFICATION', '1','BEVENT_CSCWIN','1', '0') as total_win,
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CONVERSION', '1', '0') as conversion, 
    PostImpressionRequestResponse.auction_price as total_win_bidValue, PostImpressionRequestResponse.eventTime as epochTime,
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStrXMin(PostImpressionRequestResponse.eventTime * 1000,'$tz','yyyy-MM-dd-HH-mm', '30') as time30,
    PostImpressionRequestResponse.campaignId as campaignId, PostImpressionRequestResponse.bidprice_to_exchange as bidprice_to_exchange,
    com.kritter.kumbaya.libraries.pigudf.DecodeReturnDouble(PostImpressionRequestResponse.event, 'CONVERSION', PostImpressionRequestResponse.cpa_goal, '0.0') as cpa_goal, 
    PostImpressionRequestResponse.ext_supply_attr_internal_id as ext_supply_attr_internal_id, PostImpressionRequestResponse.adv_inc_id as adv_inc_id, 
    PostImpressionRequestResponse.pub_inc_id as pub_inc_id, PostImpressionRequestResponse.adId as adId;

post_imp_filter_data =  FILTER post_imp_proj_data BY terminationReason == 'HEALTHY_REQUEST';

post_imp_group_data = GROUP post_imp_filter_data BY (time, pub_inc_id, siteId, ext_supply_attr_internal_id, adv_inc_id, campaignId, adId);

post_imp_flatten_group_data = FOREACH post_imp_group_data {
            GENERATE FLATTEN(group),
            SUM(post_imp_filter_data.total_click) as total_click,
            SUM(post_imp_filter_data.total_win) as total_win,
            SUM(post_imp_filter_data.total_win_bidValue) as total_win_bidValue,
            SUM(post_imp_filter_data.total_csc) as total_csc,
            SUM(post_imp_filter_data.conversion) as conversion,
            SUM(post_imp_filter_data.bidprice_to_exchange) as bidprice_to_exchange,
            SUM(post_imp_filter_data.cpa_goal) as cpa_goal;
        }


post_imp_group_data_gen = FOREACH post_imp_flatten_group_data GENERATE '$PROCESS_TIME' as process_time,group::time as time, 
    group::pub_inc_id as pub_inc_id, group::siteId as siteId, group::ext_supply_attr_internal_id as ext_supply_attr_internal_id,
    group::adv_inc_id as adv_inc_id, group::campaignId as campaignId, group::adId as adId,
    0 as total_request, 0 as total_impression, 
    0.0 as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, 
    total_csc as total_csc, 0.0 as demandCharges, 0.0 as supplyCost, 0.0 as earning, conversion as conversion,
    bidprice_to_exchange as bidprice_to_exchange, cpa_goal as cpa_goal,
    0.0 as exchangepayout, 0.0 as exchangerevenue, 0.0 as networkpayout, 0.0 as networkrevenue, 0 as billedclicks, 0 as billedcsc;

STORE post_imp_group_data_gen INTO '$OUTPUT/fast_path' USING PigStorage('');


