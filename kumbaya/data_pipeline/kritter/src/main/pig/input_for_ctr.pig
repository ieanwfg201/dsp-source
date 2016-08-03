

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
       --       pub_inc_id: int
       --     )

post_imp_decoded = FOREACH post_imp_raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
post_imp_decoded_data = FOREACH post_imp_decoded GENERATE PostImpThriftBytesToTupleDef($0);


post_imp_proj_data = FOREACH post_imp_decoded_data GENERATE PostImpressionRequestResponse.status as terminationReason, 
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(PostImpressionRequestResponse.eventTime * 1000,'yyyy-MM-dd HH:00:00', '$tz') as time, 
    PostImpressionRequestResponse.siteId as siteId, PostImpressionRequestResponse.deviceId as deviceId, 
    PostImpressionRequestResponse.countryId as countryId, PostImpressionRequestResponse.adId as adId, 
    PostImpressionRequestResponse.exchangeId as exchangeId, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CLICK', '1', '0') as total_click, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'RENDER', '1', 'INT_EXCHANGE_WIN', '1', '0') as total_csc, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'WIN_NOTIFICATION', '1', '0') as total_win,
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CONVERSION', '1', '0') as conversion, 
    PostImpressionRequestResponse.auction_price as total_win_bidValue, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CLICK', '0', 'RENDER', '0', 'WIN_NOTIFICATION', '0', 'CONVERSION', '0' , 'INT_EXCHANGE_WIN', '0', '1') as total_event_type, 
    org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CLICK', '', 'RENDER', '', 'WIN_NOTIFICATION', '', 'CONVERSION', '', 'INT_EXCHANGE_WIN', '' , PostImpressionRequestResponse.event) as event_type, 
    PostImpressionRequestResponse.countryCarrierId as countryCarrierId, PostImpressionRequestResponse.countryRegionId as countryRegionId, 
    PostImpressionRequestResponse.deviceManufacturerId as deviceManufacturerId, PostImpressionRequestResponse.deviceOsId as deviceOsId, 
    PostImpressionRequestResponse.selectedSiteCategoryId as selectedSiteCategoryId, PostImpressionRequestResponse.eventTime as epochTime,
    PostImpressionRequestResponse.bidderModelId as bidderModelId, '' as nofillReason,
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStrXMin(PostImpressionRequestResponse.eventTime * 1000,'$tz','yyyy-MM-dd-HH-mm', '30') as time30,
    PostImpressionRequestResponse.campaignId as campaignId, PostImpressionRequestResponse.bidprice_to_exchange as bidprice_to_exchange,
    PostImpressionRequestResponse.deviceBrowserId as browserId, com.kritter.kumbaya.libraries.pigudf.DecodeReturnDouble(PostImpressionRequestResponse.event, 'CONVERSION', PostImpressionRequestResponse.cpa_goal, '0.0') as cpa_goal, PostImpressionRequestResponse.supply_source_type as supply_source_type,
    PostImpressionRequestResponse.ext_supply_attr_internal_id as ext_supply_attr_internal_id,
    PostImpressionRequestResponse.connectionTypeId as connectionTypeId, PostImpressionRequestResponse.adv_inc_id as adv_inc_id, 
    PostImpressionRequestResponse.pub_inc_id as pub_inc_id;

post_imp_filter_data =  FILTER post_imp_proj_data BY terminationReason == 'HEALTHY_REQUEST';

ctr_postimp_input =  FOREACH post_imp_filter_data GENERATE time30 as time30, campaignId as campaignId, adId as adId, selectedSiteCategoryId as selectedSiteCategoryId, countryId as countryId, countryCarrierId as countryCarrierId,countryRegionId as countryRegionId, deviceManufacturerId as deviceManufacturerId,deviceOsId as deviceOsId,exchangeId as exchangeId,total_csc as total_csc,total_click as total_click,total_win as total_win, conversion as conversion, supply_source_type as supply_source_type;

ctr_group = GROUP ctr_postimp_input BY (time30, campaignId, adId, supply_source_type, selectedSiteCategoryId, countryId, countryCarrierId,countryRegionId, deviceManufacturerId,deviceOsId,exchangeId);

ctr_group_gen = FOREACH ctr_group GENERATE FLATTEN(group) , SUM(ctr_postimp_input.total_csc) as total_csc, SUM(ctr_postimp_input.total_click) as total_click, SUM(ctr_postimp_input.total_win) as total_win , SUM(ctr_postimp_input.conversion) as conversion;

ctr_out = FOREACH ctr_group_gen GENERATE group::time30 as time30, group::campaignId as campaignId, group::adId as adId, group::supply_source_type as supply_source_type, group::selectedSiteCategoryId as selectedSiteCategoryId, group::countryId as countryId, group::countryCarrierId as countryCarrierId, group::countryRegionId as countryRegionId, group::deviceManufacturerId as deviceManufacturerId, group::deviceOsId as deviceOsId, group::exchangeId as exchangeId, total_csc as total_csc, total_click as total_click, total_win as total_win, conversion as conversion;

STORE ctr_out INTO '$OUTPUT/input_for_ctr.gz' USING org.apache.pig.piggybank.storage.MultiStorage('$OUTPUT/input_for_ctr.gz','0','gz','');

