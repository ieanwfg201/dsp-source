

REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;

rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' USING PigStorage('') as (process_time, time:chararray, inventorySource:chararray, event_type:chararray, siteId:int, deviceId:chararray, deviceManufacturerId:int, deviceModelId:int, deviceOsId:int, countryId:int, countryCarrierId:int, countryRegionId:int, exchangeId:int, creativeId:int, adId:int, campaignId:int, advertiserId:chararray, bidderModelId:int, nofillReason:chararray, total_request:int, total_impression:int, total_bidValue:double, total_click:int, total_win:int, total_win_bidValue:double, total_csc:int, total_event_type:int, demandCharges:double, supplyCost:double, earning:double, conversion:int,bidprice_to_exchange:double, browserId:int, cpa_goal:double, exchangepayout:double, exchangerevenue:double, networkpayout:double, networkrevenue:double, billedclicks:int, billedcsc:int, supply_source_type:int, ext_supply_attr_internal_id:int, connectionTypeId:int);

convert_data = FOREACH raw_data GENERATE '$PROCESS_TIME' as PROCESS_TIME,com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, inventorySource as inventorySource, event_type as event_type, siteId as siteId, deviceId as deviceId, deviceManufacturerId as deviceManufacturerId, deviceModelId as deviceModelId, deviceOsId as deviceOsId, countryId as countryId, countryCarrierId as countryCarrierId, countryRegionId as countryRegionId, exchangeId as exchangeId, creativeId as creativeId, adId as adId, campaignId as campaignId, advertiserId as advertiserId, bidderModelId as bidderModelId, nofillReason as nofillReason, total_request as total_request, total_impression as total_impression, total_bidValue as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, total_csc as total_csc, total_event_type as total_event_type, demandCharges as demandCharges, supplyCost as supplyCost, earning as earning,conversion as conversion,bidprice_to_exchange as bidprice_to_exchange, browserId as browserId, cpa_goal as cpa_goal, exchangepayout as exchangepayout, exchangerevenue as exchangerevenue, networkpayout as networkpayout, networkrevenue as networkrevenue, billedclicks, billedcsc, supply_source_type as supply_source_type, -1 as ext_supply_attr_internal_id, connectionTypeId as connectionTypeId;

group_data =  GROUP convert_data BY (PROCESS_TIME, time, inventorySource, event_type, siteId, deviceId, deviceManufacturerId, deviceModelId, deviceOsId, countryId, countryCarrierId, countryRegionId, exchangeId, creativeId, adId, campaignId, advertiserId, bidderModelId, nofillReason, browserId, supply_source_type, ext_supply_attr_internal_id,connectionTypeId);

flatten_data = FOREACH group_data {
                GENERATE FLATTEN(group),
                SUM(convert_data.total_request) as total_request,
                SUM(convert_data.total_impression) as total_impression,
                SUM(convert_data.total_bidValue) as total_bidValue,
                SUM(convert_data.total_click) as total_click,
                SUM(convert_data.total_win) as total_win,
                SUM(convert_data.total_win_bidValue) as total_win_bidValue,
                SUM(convert_data.total_csc) as total_csc,
                SUM(convert_data.total_event_type) as total_event_type,
                SUM(convert_data.demandCharges) as demandCharges,
                SUM(convert_data.supplyCost) as supplyCost,
                SUM(convert_data.earning) as earning,
                SUM(convert_data.conversion) as conversion,
                SUM(convert_data.bidprice_to_exchange) as bidprice_to_exchange,
                SUM(convert_data.cpa_goal) as cpa_goal,
                SUM(convert_data.exchangepayout) as exchangepayout,
                SUM(convert_data.exchangerevenue) as exchangerevenue,
                SUM(convert_data.networkpayout) as networkpayout,
                SUM(convert_data.networkrevenue) as networkrevenue,
                SUM(convert_data.billedclicks) as billedclicks,
                SUM(convert_data.billedcsc) as billedcsc;
            }

flatten_data_store = FOREACH flatten_data GENERATE group::PROCESS_TIME, group::time, group::inventorySource, group::event_type, group::siteId, group::deviceId, group::deviceManufacturerId, group::deviceModelId, group::deviceOsId, group::countryId, group::countryCarrierId, group::countryRegionId, group::exchangeId, group::creativeId, group::adId, group::campaignId, group::advertiserId, group::bidderModelId, group::nofillReason, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, total_event_type, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, group::browserId, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue, billedclicks,billedcsc, group::supply_source_type, group::ext_supply_attr_internal_id, group::connectionTypeId;

STORE flatten_data_store INTO '$OUTPUT/first_level' USING PigStorage('');

limited_raw_data = load '$LIMITED_INPUT_FILES' USING PigStorage('') as (process_time, time:chararray, pub_inc_id:int, siteId:int, deviceManufacturerId:int, deviceOsId:int, countryId:int, countryCarrierId:int, adId:int, campaignId:int, adv_inc_id:int, total_request:int, total_impression:int, total_bidValue:double, total_click:int, total_win:int, total_win_bidValue:double, total_csc:int, demandCharges:double, supplyCost:double, earning:double, conversion:int,bidprice_to_exchange:double, cpa_goal:double, exchangepayout:double, exchangerevenue:double, networkpayout:double, networkrevenue:double, billedclicks:int, billedcsc:int,marketplace:int,bidFloor:double,reqSlotIds:int,deviceType:int);

limited_convert_data = FOREACH limited_raw_data GENERATE '$PROCESS_TIME' as PROCESS_TIME,com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, pub_inc_id as pub_inc_id,siteId as siteId, deviceManufacturerId as deviceManufacturerId, deviceOsId as deviceOsId, countryId as countryId, countryCarrierId as countryCarrierId, adId as adId, campaignId as campaignId, adv_inc_id as adv_inc_id, total_request as total_request, total_impression as total_impression, total_bidValue as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, total_csc as total_csc, demandCharges as demandCharges, supplyCost as supplyCost, earning as earning,conversion as conversion,bidprice_to_exchange as bidprice_to_exchange, cpa_goal as cpa_goal, exchangepayout as exchangepayout, exchangerevenue as exchangerevenue, networkpayout as networkpayout, networkrevenue as networkrevenue, billedclicks, billedcsc, marketplace,bidFloor as bidFloor,reqSlotIds as reqSlotIds,deviceType as deviceType;

limited_group_data =  GROUP limited_convert_data BY (PROCESS_TIME, time, pub_inc_id, siteId, deviceOsId, countryId, countryCarrierId, adId, campaignId, adv_inc_id,marketplace,reqSlotIds,deviceType);

limited_flatten_data = FOREACH limited_group_data {
                GENERATE FLATTEN(group),
                SUM(limited_convert_data.total_request) as total_request,
                SUM(limited_convert_data.total_impression) as total_impression,
                SUM(limited_convert_data.total_bidValue) as total_bidValue,
                SUM(limited_convert_data.total_click) as total_click,
                SUM(limited_convert_data.total_win) as total_win,
                SUM(limited_convert_data.total_win_bidValue) as total_win_bidValue,
                SUM(limited_convert_data.total_csc) as total_csc,
                SUM(limited_convert_data.demandCharges) as demandCharges,
                SUM(limited_convert_data.supplyCost) as supplyCost,
                SUM(limited_convert_data.earning) as earning,
                SUM(limited_convert_data.conversion) as conversion,
                SUM(limited_convert_data.bidprice_to_exchange) as bidprice_to_exchange,
                SUM(limited_convert_data.cpa_goal) as cpa_goal,
                SUM(limited_convert_data.exchangepayout) as exchangepayout,
                SUM(limited_convert_data.exchangerevenue) as exchangerevenue,
                SUM(limited_convert_data.networkpayout) as networkpayout,
                SUM(limited_convert_data.networkrevenue) as networkrevenue,
                SUM(limited_convert_data.billedclicks) as billedclicks,
                SUM(limited_convert_data.billedcsc) as billedcsc,
                SUM(limited_convert_data.bidFloor) as bidFloor;
            }

limited_flatten_data_store = FOREACH limited_flatten_data GENERATE group::PROCESS_TIME, group::time, group::pub_inc_id, group::siteId, '-1', group::deviceOsId, group::countryId, group::countryCarrierId, group::adId, group::campaignId, group::adv_inc_id, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue, billedclicks,billedcsc,group::marketplace,bidFloor,group::reqSlotIds,group::deviceType;

STORE limited_flatten_data_store INTO '$OUTPUT/limited_first_level' USING PigStorage('');



