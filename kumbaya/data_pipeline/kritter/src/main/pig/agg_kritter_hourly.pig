

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

limited_raw_data = load '$LIMITED_INPUT_FILES' USING PigStorage('') as (process_time, time:chararray, pub_inc_id:int, siteId:int, deviceManufacturerId:int, deviceOsId:int, countryId:int, countryCarrierId:int, adId:int, campaignId:int, adv_inc_id:int, total_request:int, total_impression:int, total_bidValue:double, total_click:int, total_win:int, total_win_bidValue:double, total_csc:int, demandCharges:double, supplyCost:double, earning:double, conversion:int,bidprice_to_exchange:double, cpa_goal:double, exchangepayout:double, exchangerevenue:double, networkpayout:double, networkrevenue:double, billedclicks:int, billedcsc:int, marketplace:int,bidFloor:double,reqSlotIds:int,deviceType:int);

limited_convert_data = FOREACH limited_raw_data GENERATE '$PROCESS_TIME' as PROCESS_TIME,com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, pub_inc_id as pub_inc_id,siteId as siteId, deviceManufacturerId as deviceManufacturerId, deviceOsId as deviceOsId, countryId as countryId, countryCarrierId as countryCarrierId, adId as adId, campaignId as campaignId, adv_inc_id as adv_inc_id, total_request as total_request, total_impression as total_impression, total_bidValue as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, total_csc as total_csc, demandCharges as demandCharges, supplyCost as supplyCost, earning as earning,conversion as conversion,bidprice_to_exchange as bidprice_to_exchange, cpa_goal as cpa_goal, exchangepayout as exchangepayout, exchangerevenue as exchangerevenue, networkpayout as networkpayout, networkrevenue as networkrevenue, billedclicks, billedcsc,marketplace as marketplace,
bidFloor as bidFloor,reqSlotIds as reqSlotIds,deviceType as deviceType;

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

limited_flatten_data_store = FOREACH limited_flatten_data GENERATE group::PROCESS_TIME, group::time, group::pub_inc_id, group::siteId, '-1', group::deviceOsId, group::countryId, group::countryCarrierId, group::adId, group::campaignId, group::adv_inc_id, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue, billedclicks,billedcsc, group::marketplace,bidFloor,group::reqSlotIds,group::deviceType;

STORE limited_flatten_data_store INTO '$OUTPUT/limited_first_level' USING PigStorage('');


ext_site_raw_data = load '$EXT_SITE_INPUT_FILES' USING PigStorage('') as (process_time, time:chararray, exchangeId:int, siteId:int, ext_supply_attr_internal_id:int, adId:int, campaignId:int, total_request:int, total_impression:int, total_bidValue:double, total_click:int, total_win:int, total_win_bidValue:double, total_csc:int, demandCharges:double, supplyCost:double, earning:double, conversion:int,bidprice_to_exchange:double, cpa_goal:double, exchangepayout:double, exchangerevenue:double, networkpayout:double, networkrevenue:double, billedclicks:int, billedcsc:int, countryId:int);

ext_site_convert_data = FOREACH ext_site_raw_data GENERATE '$PROCESS_TIME' as PROCESS_TIME,com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, siteId as siteId, exchangeId as exchangeId, adId as adId, campaignId as campaignId, total_request as total_request, total_impression as total_impression, total_bidValue as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, total_csc as total_csc, demandCharges as demandCharges, supplyCost as supplyCost, earning as earning,conversion as conversion,bidprice_to_exchange as bidprice_to_exchange, cpa_goal as cpa_goal, exchangepayout as exchangepayout, exchangerevenue as exchangerevenue, networkpayout as networkpayout, networkrevenue as networkrevenue, billedclicks, billedcsc, ext_supply_attr_internal_id as ext_supply_attr_internal_id, countryId as countryId;

ext_site_group_data =  GROUP ext_site_convert_data BY (PROCESS_TIME, time, siteId, exchangeId, adId, campaignId, ext_supply_attr_internal_id,countryId);

ext_site_flatten_data = FOREACH ext_site_group_data {
                GENERATE FLATTEN(group),
                SUM(ext_site_convert_data.total_request) as total_request,
                SUM(ext_site_convert_data.total_impression) as total_impression,
                SUM(ext_site_convert_data.total_bidValue) as total_bidValue,
                SUM(ext_site_convert_data.total_click) as total_click,
                SUM(ext_site_convert_data.total_win) as total_win,
                SUM(ext_site_convert_data.total_win_bidValue) as total_win_bidValue,
                SUM(ext_site_convert_data.total_csc) as total_csc,
                SUM(ext_site_convert_data.demandCharges) as demandCharges,
                SUM(ext_site_convert_data.supplyCost) as supplyCost,
                SUM(ext_site_convert_data.earning) as earning,
                SUM(ext_site_convert_data.conversion) as conversion,
                SUM(ext_site_convert_data.bidprice_to_exchange) as bidprice_to_exchange,
                SUM(ext_site_convert_data.exchangepayout) as exchangepayout,
                SUM(ext_site_convert_data.exchangerevenue) as exchangerevenue,
                SUM(ext_site_convert_data.networkpayout) as networkpayout,
                SUM(ext_site_convert_data.networkrevenue) as networkrevenue,
                SUM(ext_site_convert_data.billedclicks) as billedclicks,
                SUM(ext_site_convert_data.cpa_goal) as cpa_goal,
                SUM(ext_site_convert_data.billedcsc) as billedcsc;
            }

ext_site_flatten_data_store = FOREACH ext_site_flatten_data GENERATE group::PROCESS_TIME, group::time, group::exchangeId, group::siteId, group::ext_supply_attr_internal_id, group::adId, group::campaignId, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue, billedclicks,billedcsc, group::countryId;

STORE ext_site_flatten_data_store INTO '$OUTPUT/first_level_ext_site' USING PigStorage('');

adposition_input = load '$ADPOSITION_FILES' USING PigStorage('') as (process_time, time:chararray, pub_inc_id:chararray, siteId:chararray, countryId:chararray, stateId:chararray, cityId:chararray, adv_inc_id:chararray, campaignId:chararray, adId:chararray, adpositionId:chararray, total_request:int, total_impression:int, total_bidValue:double, total_click:int, total_win:int, total_win_bidValue:int, total_csc:int, demandCharges:double, supplyCost:double, earning:double, conversion:int, bidprice_to_exchange:double, cpa_goal:double, exchangepayout:double, exchangerevenue:double, networkpayout:double, networkrevenue:double,billedclicks:int,billedcsc:int);

adposition_data = FOREACH adposition_input GENERATE com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, pub_inc_id as pub_inc_id, siteId as siteId, countryId as countryId, stateId as stateId, cityId as cityId, adv_inc_id as adv_inc_id, campaignId as campaignId, adId as adId, adpositionId as adpositionId, total_request as total_request, total_impression as total_impression, total_bidValue as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, total_csc as total_csc, demandCharges as demandCharges, supplyCost as supplyCost, earning as earning, conversion as conversion, bidprice_to_exchange as bidprice_to_exchange, cpa_goal as cpa_goal, exchangepayout as exchangepayout, exchangerevenue as exchangerevenue, networkpayout as networkpayout, networkrevenue as networkrevenue, billedclicks as billedclicks, billedcsc as billedcsc;

adposition_group =  GROUP adposition_data BY (time, pub_inc_id, siteId, countryId, stateId, cityId, adv_inc_id, campaignId, adId, adpositionId);

adposition_flatten = FOREACH adposition_group {
                GENERATE FLATTEN(group),
                SUM(adposition_data.total_request) as total_request,
                SUM(adposition_data.total_impression) as total_impression,
                SUM(adposition_data.total_bidValue) as total_bidValue,
                SUM(adposition_data.total_click) as total_click,
                SUM(adposition_data.total_win) as total_win,
                SUM(adposition_data.total_win_bidValue) as total_win_bidValue,
                SUM(adposition_data.total_csc) as total_csc,
                SUM(adposition_data.demandCharges) as demandCharges,
                SUM(adposition_data.supplyCost) as supplyCost,
                SUM(adposition_data.earning) as earning,
                SUM(adposition_data.conversion) as conversion,
                SUM(adposition_data.bidprice_to_exchange) as bidprice_to_exchange,
                SUM(adposition_data.cpa_goal) as cpa_goal,
                SUM(adposition_data.exchangepayout) as exchangepayout,
                SUM(adposition_data.exchangerevenue) as exchangerevenue,
                SUM(adposition_data.networkpayout) as networkpayout,
                SUM(adposition_data.networkrevenue) as networkrevenue,
                SUM(adposition_data.billedclicks) as billedclicks,
                SUM(adposition_data.billedcsc) as billedcsc;
            }

adposition_store = FOREACH adposition_flatten GENERATE '$PROCESS_TIME', group::time, group::pub_inc_id, group::siteId, group::countryId, group::stateId, group::cityId, group::adv_inc_id, group::campaignId, group::adId, group::adpositionId, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue, billedclicks, billedcsc;

STORE adposition_store INTO '$OUTPUT/adposition_hourly' USING PigStorage('');

channel_input = load '$CHANNEL_FILES' USING PigStorage('') as (process_time, time:chararray, pub_inc_id:chararray, siteId:chararray, adv_inc_id:chararray, campaignId:chararray, adId:chararray, channelId:chararray, total_request:int, total_impression:int, total_bidValue:double, total_click:int, total_win:int, total_win_bidValue:int, total_csc:int, demandCharges:double, supplyCost:double, earning:double, conversion:int, bidprice_to_exchange:double, cpa_goal:double, exchangepayout:double, exchangerevenue:double, networkpayout:double, networkrevenue:double,billedclicks:int,billedcsc:int);

channel_data = FOREACH channel_input GENERATE com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, pub_inc_id as pub_inc_id, siteId as siteId, adv_inc_id as adv_inc_id, campaignId as campaignId, adId as adId, channelId as channelId, total_request as total_request, total_impression as total_impression, total_bidValue as total_bidValue, total_click as total_click, total_win as total_win, total_win_bidValue as total_win_bidValue, total_csc as total_csc, demandCharges as demandCharges, supplyCost as supplyCost, earning as earning, conversion as conversion, bidprice_to_exchange as bidprice_to_exchange, cpa_goal as cpa_goal, exchangepayout as exchangepayout, exchangerevenue as exchangerevenue, networkpayout as networkpayout, networkrevenue as networkrevenue, billedclicks as billedclicks, billedcsc as billedcsc;

channel_group =  GROUP channel_data BY (time, pub_inc_id, siteId, adv_inc_id, campaignId, adId, channelId);

channel_flatten = FOREACH channel_group {
                GENERATE FLATTEN(group),
                SUM(channel_data.total_request) as total_request,
                SUM(channel_data.total_impression) as total_impression,
                SUM(channel_data.total_bidValue) as total_bidValue,
                SUM(channel_data.total_click) as total_click,
                SUM(channel_data.total_win) as total_win,
                SUM(channel_data.total_win_bidValue) as total_win_bidValue,
                SUM(channel_data.total_csc) as total_csc,
                SUM(channel_data.demandCharges) as demandCharges,
                SUM(channel_data.supplyCost) as supplyCost,
                SUM(channel_data.earning) as earning,
                SUM(channel_data.conversion) as conversion,
                SUM(channel_data.bidprice_to_exchange) as bidprice_to_exchange,
                SUM(channel_data.cpa_goal) as cpa_goal,
                SUM(channel_data.exchangepayout) as exchangepayout,
                SUM(channel_data.exchangerevenue) as exchangerevenue,
                SUM(channel_data.networkpayout) as networkpayout,
                SUM(channel_data.networkrevenue) as networkrevenue,
                SUM(channel_data.billedclicks) as billedclicks,
                SUM(channel_data.billedcsc) as billedcsc;
            }


channel_store = FOREACH channel_flatten GENERATE '$PROCESS_TIME', group::time, group::pub_inc_id, group::siteId, group::adv_inc_id, group::campaignId, group::adId, group::channelId, total_request, total_impression, total_bidValue, total_click, total_win, total_win_bidValue, total_csc, demandCharges, supplyCost, earning, conversion, bidprice_to_exchange, cpa_goal, exchangepayout, exchangerevenue, networkpayout, networkrevenue, billedclicks, billedcsc;

STORE channel_store INTO '$OUTPUT/channel_hourly' USING PigStorage('');

