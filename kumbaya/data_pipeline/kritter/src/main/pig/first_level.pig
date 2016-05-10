

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
       --  as (
       --       version: int,
       --       requestId: chararray,
       --       inventorySource: chararray,
       --       terminationReason: chararray,
       --       time: long,
       --       siteId: int,
       --       siteCategoryId: {
       --         t: (
       --           siteCategoryId_tuple: int
       --         )
       --       },
       --       deviceId: long,
       --       deviceManufacturerId: int,
       --       deviceModelId: int,
       --       deviceOsId: int,
       --       countryId: int,
       --       countryCarrierId: int,
       --       countryRegionId: int,
       --       userAgent: chararray,
       --       ipAddress: chararray,
       --       xForwardedFor: chararray,
       --       impressions: {
       --         t: (
       --           version: int,
       --           impressionId: chararray,
       --           creativeId: int,
       --           adId: int,
       --           campaignId: int,
       --           advertiserId: chararray,
       --           bidValue: double,
       --           slotId: int,
       --           adv_inc_id: int,
       --           predictedCTR: double
       --         )
       --       },
       --       urlExtraParameters: map[
       --         chararray
       --       ],
       --       exchangeId: int,
       --       extSiteId: chararray,
       --       auction_id: chararray,
       --       auction_imp_id: chararray,
       --       bidderModelId: int,
       --       selectedSiteCategoryId: int,
       --       nofillReason: chararray,
       --       detectedCountryId: int,
       --       detectedCountryCarrierId: int,
       --       sitePlatformValue: int,
       --       endUserlatitudeValue: double,
       --       endUserlongitudeValue: double,
       --       endUserIdentificationObject: (
       --         uniqueUserId: chararray,
       --         buyerUid: chararray
       --       ),
       --       reqSlotIds: {
       --         t: (
       --           reqSlotIds_tuple: int
       --         )
       --       },
       --       browserId: int,
       --       mcc_mnc: chararray,
       --       ext_supply_url: chararray,
       --       ext_supply_id: chararray,
       --       ext_supply_name: chararray,
       --       supply_source_type: int,
       --       ext_supply_domain: chararray,
       --       ext_supply_attr_internal_id: int,
       --       connectionTypeId: int,
       --       direct_nofill_passback: int,
       --       pub_inc_id: int
       --     )

decoded = FOREACH raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
decoded_data = FOREACH decoded GENERATE ThriftBytesToTupleDef($0);

proj_data = FOREACH decoded_data GENERATE AdservingRequestResponse.terminationReason as terminationReason, 
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(AdservingRequestResponse.time * 1000,'yyyy-MM-dd HH:00:00', 'UTC') as time, 
    AdservingRequestResponse.inventorySource as inventorySource, AdservingRequestResponse.siteId as siteId, 
    AdservingRequestResponse.deviceId as deviceId, AdservingRequestResponse.deviceManufacturerId as deviceManufacturerId, 
    AdservingRequestResponse.deviceModelId as deviceModelId, AdservingRequestResponse.deviceOsId as deviceOsId, 
    AdservingRequestResponse.countryId as countryId, AdservingRequestResponse.countryCarrierId as countryCarrierId, 
    AdservingRequestResponse.countryRegionId as countryRegionId, AdservingRequestResponse.exchangeId as exchangeId, 
    FLATTEN(com.kritter.kumbaya.libraries.pigudf.ExtractFromBagOfTupleAdserving(AdservingRequestResponse.impressions , -1, '-1', -1, -1 , -1, '-1', 0.0, -1,-1, 0.0, 1, 0))
     as (version:int,impressionId:chararray,creativeId:int,adId:int,campaignId:int,advertiserId:chararray,bidValue:double, slotid:int, adv_inc_id:int, predictedCTR:double, total_request:int, total_impression:int), 
     AdservingRequestResponse.selectedSiteCategoryId as selectedSiteCategoryId, AdservingRequestResponse.time as epochtime,
     AdservingRequestResponse.bidderModelId as bidderModelId, AdservingRequestResponse.nofillReason as nofillReason,
    AdservingRequestResponse.browserId as browserId, AdservingRequestResponse.supply_source_type as supply_source_type,
    AdservingRequestResponse.ext_supply_url as ext_supply_url, AdservingRequestResponse.ext_supply_id as ext_supply_id, AdservingRequestResponse.ext_supply_name as ext_supply_name, AdservingRequestResponse.ext_supply_domain as ext_supply_domain, AdservingRequestResponse.ext_supply_attr_internal_id as ext_supply_attr_internal_id, AdservingRequestResponse.connectionTypeId as connectionTypeId, AdservingRequestResponse.pub_inc_id as pub_inc_id;



filter_data = FILTER proj_data BY terminationReason == 'HEALTHY_REQUEST';

ex_filter = FILTER filter_data BY ( ext_supply_attr_internal_id == -1 OR ext_supply_attr_internal_id == 0 ) AND ( inventorySource == 'RTB_EXCHANGE' );
ex_proj = FOREACH ex_filter GENERATE siteId as siteId, supply_source_type as supply_source_type, ext_supply_url as ext_supply_url, ext_supply_id as ext_supply_id, ext_supply_name as ext_supply_name, ext_supply_domain as ext_supply_domain, ext_supply_attr_internal_id as ext_supply_attr_internal_id, deviceOsId as deviceOsId;

STORE ex_proj INTO '$OUTPUT/externalsite.gz' USING PigStorage('');


supply_proj_data = FOREACH filter_data GENERATE com.kritter.kumbaya.libraries.pigudf.EpochToDateStrFifteenMin(epochtime * 1000, 'UTC') as time, 
    selectedSiteCategoryId as selectedSiteCategoryId, countryId as countryId, countryCarrierId as countryCarrierId, 
    countryRegionId as countryRegionId, deviceManufacturerId as deviceManufacturerId, deviceOsId as deviceOsId, 
    exchangeId as exchangeId,total_request as total_request, total_impression as total_impression, bidValue as total_bidValue, 
    0 as total_click, 0 as total_csc, 0 as total_win, 0.0 as total_win_amount, supply_source_type ;

group_data = GROUP filter_data BY (time, inventorySource, siteId, deviceId, deviceManufacturerId, deviceModelId, deviceOsId, countryId, countryCarrierId, countryRegionId, exchangeId, creativeId, adId, campaignId, advertiserId, bidderModelId, nofillReason,browserId,supply_source_type,ext_supply_attr_internal_id,connectionTypeId, adv_inc_id, pub_inc_id);

flatten_group_data = FOREACH group_data {
            GENERATE FLATTEN(group),
            SUM(filter_data.total_request) as total_request,
            SUM(filter_data.total_impression) as total_impression,
            SUM(filter_data.bidValue) as total_bidValue;
        }


group_data_gen = FOREACH flatten_group_data GENERATE '$PROCESS_TIME' as process_time,group::time as time, group::inventorySource as inventorySource, 
    '' as event_type ,group::siteId as siteId, group::deviceId as deviceId, group::deviceManufacturerId  as deviceManufacturerId, 
    group::deviceModelId as deviceModelId, group::deviceOsId as deviceOsId, group::countryId as countryId, 
    group::countryCarrierId as countryCarrierId, group::countryRegionId as countryRegionId, group::exchangeId as exchangeId, 
    group::creativeId as creativeId,group::adId as adId, group::campaignId as campaignId, group::advertiserId as advertiserId, 
    group::bidderModelId as bidderModelId, group::nofillReason as nofillReason, total_request as total_request, total_impression as total_impression, 
    total_bidValue as total_bidValue, 0 as total_click, 0 as total_win, 0.0 as total_win_bidValue, 0 as total_csc, 0 as total_event_type, 
    0.0 as demandCharges, 0.0 as supplyCost, 0.0 as earning , 0 as conversion, 0.0 as bidprice_to_exchange,group::browserId as browserId,0.0 as cpa_goal,
    0.0 as exchangepayout, 0.0 as exchangerevenue, 0.0 as networkpayout, 0.0 as networkrevenue, 0 as billedclicks, 0 as billedcsc,group::supply_source_type as supply_source_type, group::ext_supply_attr_internal_id as ext_supply_attr_internal_id, group::connectionTypeId as connectionTypeId, group::adv_inc_id as adv_inc_id, 
    group::pub_inc_id as pub_inc_id;


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
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(PostImpressionRequestResponse.eventTime * 1000,'yyyy-MM-dd HH:00:00', 'UTC') as time, 
    PostImpressionRequestResponse.siteId as siteId, PostImpressionRequestResponse.deviceId as deviceId, 
    PostImpressionRequestResponse.countryId as countryId, PostImpressionRequestResponse.adId as adId, 
    PostImpressionRequestResponse.exchangeId as exchangeId, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CLICK', '1', '0') as total_click, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'RENDER', '1', 'INT_EXCHANGE_WIN', '1','BEVENT_CSCWIN', '1' ,'0') as total_csc, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'WIN_NOTIFICATION', '1','BEVENT_CSCWIN','1', '0') as total_win,
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CONVERSION', '1', '0') as conversion, 
    PostImpressionRequestResponse.auction_price as total_win_bidValue, 
    (int)org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CLICK', '0', 'RENDER', '0', 'WIN_NOTIFICATION', '0', 'CONVERSION', '0' , 'INT_EXCHANGE_WIN', '0', 'BEVENT_CSCWIN', '0', '1') as total_event_type, 
    org.apache.pig.piggybank.evaluation.decode.Decode(PostImpressionRequestResponse.event, 'CLICK', '', 'RENDER', '', 'WIN_NOTIFICATION', '', 'CONVERSION', '', 'INT_EXCHANGE_WIN', '', 'BEVENT_CSCWIN', '' , PostImpressionRequestResponse.event) as event_type, 
    PostImpressionRequestResponse.countryCarrierId as countryCarrierId, PostImpressionRequestResponse.countryRegionId as countryRegionId, 
    PostImpressionRequestResponse.deviceManufacturerId as deviceManufacturerId, PostImpressionRequestResponse.deviceOsId as deviceOsId, 
    PostImpressionRequestResponse.selectedSiteCategoryId as selectedSiteCategoryId, PostImpressionRequestResponse.eventTime as epochTime,
    PostImpressionRequestResponse.bidderModelId as bidderModelId, '' as nofillReason,
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStrXMin(PostImpressionRequestResponse.eventTime * 1000,'UTC','yyyy-MM-dd-HH-mm', '30') as time30,
    PostImpressionRequestResponse.campaignId as campaignId, PostImpressionRequestResponse.bidprice_to_exchange as bidprice_to_exchange,
    PostImpressionRequestResponse.deviceBrowserId as browserId, com.kritter.kumbaya.libraries.pigudf.DecodeReturnDouble(PostImpressionRequestResponse.event, 'CONVERSION', PostImpressionRequestResponse.cpa_goal, '0.0') as cpa_goal, PostImpressionRequestResponse.supply_source_type as supply_source_type,
    PostImpressionRequestResponse.ext_supply_attr_internal_id as ext_supply_attr_internal_id,
    PostImpressionRequestResponse.connectionTypeId as connectionTypeId, PostImpressionRequestResponse.adv_inc_id as adv_inc_id, 
    PostImpressionRequestResponse.pub_inc_id as pub_inc_id;

post_imp_filter_data =  FILTER post_imp_proj_data BY terminationReason == 'HEALTHY_REQUEST';

supply_post_imp_proj_data = FOREACH post_imp_filter_data GENERATE com.kritter.kumbaya.libraries.pigudf.EpochToDateStrFifteenMin(epochTime * 1000, 'UTC') as time, selectedSiteCategoryId as selectedSiteCategoryId , countryId as countryId, countryCarrierId as countryCarrierId, countryRegionId as countryRegionId, deviceManufacturerId as deviceManufacturerId, deviceOsId as deviceOsId, exchangeId as exchangeId, 0 as total_request, 0 as total_impression, 0 as total_bidValue, total_click as total_click, total_csc as total_csc, total_win as total_win, total_win_bidValue as total_win_amount, supply_source_type;

post_imp_group_data = GROUP post_imp_filter_data BY (time, siteId, deviceId, countryId, adId, exchangeId, event_type, countryCarrierId, countryRegionId, deviceManufacturerId, deviceOsId, bidderModelId, nofillReason, campaignId, browserId, supply_source_type, ext_supply_attr_internal_id,connectionTypeId, adv_inc_id, pub_inc_id);

post_imp_flatten_group_data = FOREACH post_imp_group_data {
            GENERATE FLATTEN(group),
            SUM(post_imp_filter_data.total_click) as total_click,
            SUM(post_imp_filter_data.total_win) as total_win,
            SUM(post_imp_filter_data.total_win_bidValue) as total_win_bidValue,
            SUM(post_imp_filter_data.total_csc) as total_csc,
            SUM(post_imp_filter_data.total_event_type) as total_event_type,
            SUM(post_imp_filter_data.conversion) as conversion,
            SUM(post_imp_filter_data.bidprice_to_exchange) as bidprice_to_exchange,
            SUM(post_imp_filter_data.cpa_goal) as cpa_goal;
        }


post_imp_group_data_gen = FOREACH post_imp_flatten_group_data GENERATE '$PROCESS_TIME' as process_time,group::time as time, '' as inventorySource, 
    group::event_type as event_type, group::siteId as siteId, group::deviceId as deviceId, group::deviceManufacturerId as deviceManufacturerId, 
    -1 as deviceModelId, group::deviceOsId as deviceOsId, group::countryId as countryId, group::countryCarrierId as countryCarrierId, 
    group::countryRegionId as countryRegionId, group::exchangeId as exchangeId, -1 as creativeId, group::adId as adId, group::campaignId as campaignId, 
    '-1' as advertiserId, group::bidderModelId as bidderModelId, group::nofillReason as nofillReason, 0 as total_request, 0 as total_impression, 
    0.0 as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, 
    total_csc as total_csc, total_event_type as total_event_type, 0.0 as demandCharges, 0.0 as supplyCost, 0.0 as earning, conversion as conversion,
    bidprice_to_exchange as bidprice_to_exchange, group::browserId as browserId, cpa_goal as cpa_goal,
    0.0 as exchangepayout, 0.0 as exchangerevenue, 0.0 as networkpayout, 0.0 as networkrevenue, 0 as billedclicks, 0 as billedcsc,
    group::supply_source_type as supply_source_type, group::ext_supply_attr_internal_id as ext_supply_attr_internal_id,
    group::connectionTypeId as connectionTypeId, group::adv_inc_id as adv_inc_id, group::pub_inc_id as pub_inc_id;


DEFINE BillingThriftBytesToTupleDef com.twitter.elephantbird.pig.piggybank.ThriftBytesToTuple('com.kritter.postimpression.thrift.struct.Billing');
billing_raw_data = load '$BILLING_INPUT_FILES' using TextLoader() as (record:chararray);
       --  as (
       --       version: int,
       --       requestId: chararray,
       --       adId: int,
       --       campaignId: int,
       --       advertiserId: chararray,
       --       exchangeId: int,
       --       time: long,
       --       siteId: int,
       --       billingModelId: int,
       --       impressionId: chararray,
       --       countryCarrierId: int,
       --       countryRegionId: int,
       --       countryId: int,
       --       deviceId: long,
       --       deviceManufacturerId: int,
       --       deviceOsId: int,
       --       status: chararray,
       --       demandCharges: double,
       --       internalDemandCharges: double,
       --       supplyCost: double,
       --       eventTime: long,
       --       selectedSiteCategoryId: int,
       --       billingType: chararray,
       --       bidderModelId: int,
       --       earning: double,
       --       browserId: int,
       --       networkpayout: double,
       --       networkrevenue: double,
       --       exchangepayout: double,
       --       exchangerevenue: double,
       --       supply_source_type: int,
       --       ext_supply_attr_internal_id: int,
       --       connectionTypeId: int,
       --       adv_inc_id: int,
       --       pub_inc_id: int
       --     )

billing_decoded = FOREACH billing_raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
billing_decoded_data = FOREACH billing_decoded GENERATE BillingThriftBytesToTupleDef($0);

billing_proj_data = FOREACH billing_decoded_data GENERATE Billing.status as terminationReason, 
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(Billing.eventTime * 1000,'yyyy-MM-dd HH:00:00','UTC') as time, 
    Billing.siteId as siteId, Billing.deviceId as deviceId, Billing.countryId as countryId, Billing.adId as adId, Billing.exchangeId as exchangeId, 
    Billing.countryCarrierId as countryCarrierId, Billing.countryRegionId as countryRegionId, Billing.deviceManufacturerId as deviceManufacturerId, 
    Billing.deviceOsId as deviceOsId, Billing.selectedSiteCategoryId as selectedSiteCategoryId, Billing.demandCharges as demandCharges, 
    Billing.supplyCost as supplyCost, Billing.earning as earning,-1 as creativeId, Billing.campaignId as campaignId, 
    Billing.advertiserId as advertiserId,Billing.bidderModelId as bidderModelId, '' as  nofillReason,
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStrXMin(Billing.eventTime * 1000,'UTC','yyyy-MM-dd-HH-mm', '30') as time30,
    Billing.browserId as browserId, Billing.exchangepayout as exchangepayout ,Billing.exchangerevenue as exchangerevenue, 
    Billing.networkpayout as networkpayout,Billing.networkrevenue as networkrevenue, (int)org.apache.pig.piggybank.evaluation.decode.Decode(Billing.billingType, 'CPC', '1', '0') as billedclicks, (int)org.apache.pig.piggybank.evaluation.decode.Decode(Billing.billingType, 'CPM', '1', 'INTEXCWIN', '1', 'BEVENT_CSCWIN_DEM','1', '0') as billedcsc,
    Billing.supply_source_type as supply_source_type, Billing.ext_supply_attr_internal_id as ext_supply_attr_internal_id,
    Billing.connectionTypeId as connectionTypeId, Billing.adv_inc_id as adv_inc_id, Billing.pub_inc_id as pub_inc_id;

billing_filter_data = FILTER billing_proj_data BY terminationReason == 'HEALTHY_REQUEST';

billing_group_data = GROUP billing_filter_data BY (time, siteId, deviceId, countryId, adId, exchangeId, countryCarrierId, countryRegionId, deviceManufacturerId, deviceOsId, creativeId, campaignId, advertiserId, bidderModelId, nofillReason, browserId, supply_source_type,ext_supply_attr_internal_id,connectionTypeId, adv_inc_id, pub_inc_id);

billing_flatten_group_data = FOREACH billing_group_data {
            GENERATE FLATTEN(group),
            SUM(billing_filter_data.demandCharges) as demandCharges,
            SUM(billing_filter_data.supplyCost) as supplyCost,
            SUM(billing_filter_data.earning) as earning,
            SUM(billing_filter_data.exchangepayout) as exchangepayout,
            SUM(billing_filter_data.exchangerevenue) as exchangerevenue,
            SUM(billing_filter_data.networkpayout) as networkpayout,
            SUM(billing_filter_data.networkrevenue) as networkrevenue,
            SUM(billing_filter_data.billedclicks) as billedclicks,
            SUM(billing_filter_data.billedcsc) as billedcsc;
        }


billing_group_data_gen = FOREACH billing_flatten_group_data GENERATE '$PROCESS_TIME' as process_time,group::time as time, '' as inventorySource, 
    '' as event_type, group::siteId as siteId, group::deviceId as deviceId, group::deviceManufacturerId as deviceManufacturerId, 
    -1 as deviceModelId, group::deviceOsId as deviceOsId, group::countryId as countryId, group::countryCarrierId as countryCarrierId, 
    group::countryRegionId as countryRegionId, group::exchangeId as exchangeId, group::creativeId as creativeId, group::adId as adId, 
    group::campaignId as campaignId, group::advertiserId as advertiserId, group::bidderModelId as bidderModelId, group::nofillReason as nofillReason, 
    0 as total_request, 0 as total_impression, 0.0 as total_bidValue, 0 as total_click, 0 as total_win, 0.0 as total_win_bidValue, 
    0 as total_csc, 0 as total_event_type, demandCharges as demandCharges, supplyCost as supplyCost, earning as earning, 0 as conversion, 0.0 as bidprice_to_exchange,
    group::browserId as browserId, 0.0 as cpa_goal, exchangepayout as exchangepayout, exchangerevenue as exchangerevenue, networkpayout as networkpayout, networkrevenue as networkrevenue, billedclicks as billedclicks, billedcsc as billedcsc, group::supply_source_type as supply_source_type, group::ext_supply_attr_internal_id as ext_supply_attr_internal_id, group::connectionTypeId as connectionTypeId, group::adv_inc_id as adv_inc_id, group::pub_inc_id as pub_inc_id;


union_adserv_postimp = UNION group_data_gen, post_imp_group_data_gen, billing_group_data_gen;

union_adserv_postimp_group = GROUP union_adserv_postimp BY (process_time, time, inventorySource, event_type, siteId, deviceId, deviceManufacturerId, deviceModelId, deviceOsId, countryId, countryCarrierId, countryRegionId, exchangeId, creativeId, adId, campaignId, advertiserId, bidderModelId, nofillReason, browserId, supply_source_type,connectionTypeId);

union_group_flatten = FOREACH union_adserv_postimp_group {
        GENERATE FLATTEN(group),
        SUM(union_adserv_postimp.total_request) as total_request,
        SUM(union_adserv_postimp.total_impression) as total_impression,
        SUM(union_adserv_postimp.total_bidValue) as total_bidValue,
        SUM(union_adserv_postimp.total_click) as total_click,
        SUM(union_adserv_postimp.total_win) as total_win,
        SUM(union_adserv_postimp.total_win_bidValue) as total_win_bidValue,
        SUM(union_adserv_postimp.total_csc) as total_csc,
        SUM(union_adserv_postimp.total_event_type) as total_event_type,
        SUM(union_adserv_postimp.demandCharges) as demandCharges,
        SUM(union_adserv_postimp.supplyCost) as supplyCost,
        SUM(union_adserv_postimp.earning) as earning,
        SUM(union_adserv_postimp.conversion) as conversion,
        SUM(union_adserv_postimp.bidprice_to_exchange) as bidprice_to_exchange,
        SUM(union_adserv_postimp.cpa_goal) as cpa_goal,
        SUM(union_adserv_postimp.exchangepayout) as exchangepayout,
        SUM(union_adserv_postimp.exchangerevenue) as exchangerevenue,
        SUM(union_adserv_postimp.networkpayout) as networkpayout,
        SUM(union_adserv_postimp.networkrevenue) as networkrevenue,
        SUM(union_adserv_postimp.billedclicks) as billedclicks,
        SUM(union_adserv_postimp.billedcsc) as billedcsc;
    }

union_group_flatten_store = FOREACH union_group_flatten GENERATE group::process_time, group::time, group::inventorySource, group::event_type, group::siteId, group::deviceId, group::deviceManufacturerId, group::deviceModelId, group::deviceOsId, group::countryId, group::countryCarrierId, group::countryRegionId, group::exchangeId, group::creativeId, group::adId, group::campaignId, group::advertiserId, group::bidderModelId, group::nofillReason, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, total_event_type, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, group::browserId, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue,billedclicks,billedcsc, group::supply_source_type as supply_source_type, -1 as ext_supply_attr_internal_id, group::connectionTypeId as connectionTypeId;

STORE union_group_flatten_store INTO '$OUTPUT/first_level' USING PigStorage('');

first_level_limited_group = GROUP union_adserv_postimp BY (process_time, time, pub_inc_id, siteId, deviceManufacturerId, deviceOsId, countryId, countryCarrierId, adId, campaignId, adv_inc_id);

first_level_limited_group_gen = FOREACH first_level_limited_group {
        GENERATE FLATTEN(group),
        SUM(union_adserv_postimp.total_request) as total_request,
        SUM(union_adserv_postimp.total_impression) as total_impression,
        SUM(union_adserv_postimp.total_bidValue) as total_bidValue,
        SUM(union_adserv_postimp.total_click) as total_click,
        SUM(union_adserv_postimp.total_win) as total_win,
        SUM(union_adserv_postimp.total_win_bidValue) as total_win_bidValue,
        SUM(union_adserv_postimp.total_csc) as total_csc,
        SUM(union_adserv_postimp.demandCharges) as demandCharges,
        SUM(union_adserv_postimp.supplyCost) as supplyCost,
        SUM(union_adserv_postimp.earning) as earning,
        SUM(union_adserv_postimp.conversion) as conversion,
        SUM(union_adserv_postimp.bidprice_to_exchange) as bidprice_to_exchange,
        SUM(union_adserv_postimp.cpa_goal) as cpa_goal,
        SUM(union_adserv_postimp.exchangepayout) as exchangepayout,
        SUM(union_adserv_postimp.exchangerevenue) as exchangerevenue,
        SUM(union_adserv_postimp.networkpayout) as networkpayout,
        SUM(union_adserv_postimp.networkrevenue) as networkrevenue,
        SUM(union_adserv_postimp.billedclicks) as billedclicks,
        SUM(union_adserv_postimp.billedcsc) as billedcsc;
    }

first_level_limited_group_store = FOREACH first_level_limited_group_gen GENERATE group::process_time, group::time, group::pub_inc_id, group::siteId, group::deviceManufacturerId, group::deviceOsId, group::countryId, group::countryCarrierId, group::adId, group::campaignId, group::adv_inc_id, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue,billedclicks,billedcsc;

STORE first_level_limited_group_store INTO '$OUTPUT/limited_first_level' USING PigStorage('');


union_adserv_postimp_ext_ste_filter = FILTER union_adserv_postimp BY exchangeId != -1;

union_adserv_postimp_ext_site_group = GROUP union_adserv_postimp_ext_ste_filter BY (process_time, time, siteId, exchangeId,adId, campaignId,ext_supply_attr_internal_id,countryId);

union_group_flatten_ext_site = FOREACH union_adserv_postimp_ext_site_group {
        GENERATE FLATTEN(group),
        SUM(union_adserv_postimp_ext_ste_filter.total_request) as total_request,
        SUM(union_adserv_postimp_ext_ste_filter.total_impression) as total_impression,
        SUM(union_adserv_postimp_ext_ste_filter.total_bidValue) as total_bidValue,
        SUM(union_adserv_postimp_ext_ste_filter.total_click) as total_click,
        SUM(union_adserv_postimp_ext_ste_filter.total_win) as total_win,
        SUM(union_adserv_postimp_ext_ste_filter.total_win_bidValue) as total_win_bidValue,
        SUM(union_adserv_postimp_ext_ste_filter.total_csc) as total_csc,
        SUM(union_adserv_postimp_ext_ste_filter.total_event_type) as total_event_type,
        SUM(union_adserv_postimp_ext_ste_filter.demandCharges) as demandCharges,
        SUM(union_adserv_postimp_ext_ste_filter.supplyCost) as supplyCost,
        SUM(union_adserv_postimp_ext_ste_filter.earning) as earning,
        SUM(union_adserv_postimp_ext_ste_filter.conversion) as conversion,
        SUM(union_adserv_postimp_ext_ste_filter.bidprice_to_exchange) as bidprice_to_exchange,
        SUM(union_adserv_postimp_ext_ste_filter.cpa_goal) as cpa_goal,
        SUM(union_adserv_postimp_ext_ste_filter.exchangepayout) as exchangepayout,
        SUM(union_adserv_postimp_ext_ste_filter.exchangerevenue) as exchangerevenue,
        SUM(union_adserv_postimp_ext_ste_filter.networkpayout) as networkpayout,
        SUM(union_adserv_postimp_ext_ste_filter.networkrevenue) as networkrevenue,
        SUM(union_adserv_postimp_ext_ste_filter.billedclicks) as billedclicks,
        SUM(union_adserv_postimp_ext_ste_filter.billedcsc) as billedcsc;
    }

union_group_flatten_ext_site_store = FOREACH union_group_flatten_ext_site GENERATE group::process_time, group::time, group::exchangeId, group::siteId, group::ext_supply_attr_internal_id , group::adId, group::campaignId, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue,billedclicks,billedcsc,group::countryId;

STORE union_group_flatten_ext_site_store INTO '$OUTPUT/first_level_ext_site' USING PigStorage('');


supply_union = UNION supply_proj_data, supply_post_imp_proj_data;

supply_union_group = GROUP supply_union BY (time, selectedSiteCategoryId, countryId, countryCarrierId, countryRegionId, deviceManufacturerId, deviceOsId, exchangeId, supply_source_type);

supply_union_flatten = FOREACH supply_union_group {
        GENERATE FLATTEN(group),
        SUM(supply_union.total_request) as total_request,
        SUM(supply_union.total_impression) as total_impression,
        SUM(supply_union.total_bidValue) as total_bidValue,
        SUM(supply_union.total_click) as total_click,
        SUM(supply_union.total_csc) as total_csc,
        SUM(supply_union.total_win) as total_win,
        SUM(supply_union.total_win_amount) as total_win_amount;
    }

supply_union_flatten_store = FOREACH supply_union_flatten GENERATE group::time, group::selectedSiteCategoryId, group::countryId, group::countryCarrierId, group::countryRegionId, group::deviceManufacturerId, group::deviceOsId, group::exchangeId, total_request, total_impression, total_bidValue, total_click, total_csc, total_win, total_win_amount, group::supply_source_type;

STORE supply_union_flatten_store INTO '$OUTPUT/supply_forecast_first_level.gz' USING org.apache.pig.piggybank.storage.MultiStorage('$OUTPUT/supply_forecast_first_level.gz','0','gz','');

ctr_postimp_input =  FOREACH post_imp_filter_data GENERATE time30 as time30, campaignId as campaignId, adId as adId, selectedSiteCategoryId as selectedSiteCategoryId, countryId as countryId, countryCarrierId as countryCarrierId,countryRegionId as countryRegionId, deviceManufacturerId as deviceManufacturerId,deviceOsId as deviceOsId,exchangeId as exchangeId,total_csc as total_csc,total_click as total_click,total_win as total_win, conversion as conversion, supply_source_type as supply_source_type;

--- ctr_billing_input = FOREACH billing_filter_data GENERATE time30 as time30, campaignId as campaignId, adId as adId, electedSiteCategoryId as selectedSiteCategoryId, countryId as countryId, countryCarrierId as countryCarrierId,countryRegionId as countryRegionId, deviceManufacturerId as deviceManufacturerId,deviceOsId as deviceOsId,exchangeId as exchangeId,total_csc as total_csc,total_click as total_click,total_win as total_win, supply_source_type as aupply_source_type;

ctr_group = GROUP ctr_postimp_input BY (time30, campaignId, adId, supply_source_type, selectedSiteCategoryId, countryId, countryCarrierId,countryRegionId, deviceManufacturerId,deviceOsId,exchangeId);

ctr_group_gen = FOREACH ctr_group GENERATE FLATTEN(group) , SUM(ctr_postimp_input.total_csc) as total_csc, SUM(ctr_postimp_input.total_click) as total_click, SUM(ctr_postimp_input.total_win) as total_win , SUM(ctr_postimp_input.conversion) as conversion;

ctr_out = FOREACH ctr_group_gen GENERATE group::time30 as time30, group::campaignId as campaignId, group::adId as adId, group::supply_source_type as supply_source_type, group::selectedSiteCategoryId as selectedSiteCategoryId, group::countryId as countryId, group::countryCarrierId as countryCarrierId, group::countryRegionId as countryRegionId, group::deviceManufacturerId as deviceManufacturerId, group::deviceOsId as deviceOsId, group::exchangeId as exchangeId, total_csc as total_csc, total_click as total_click, total_win as total_win, conversion as conversion;

-- STORE ctr_out INTO '$OUTPUT/input_for_ctr.gz' USING org.apache.pig.piggybank.storage.MultiStorage('$OUTPUT/input_for_ctr.gz','0','gz','');




