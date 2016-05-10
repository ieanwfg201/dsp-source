
rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' USING PigStorage('') as (process_time, time:chararray, inventorySource:chararray, event_type:chararray, siteId:int, deviceId:chararray, deviceManufacturerId:int, deviceModelId:int, deviceOsId:int, countryId:int, countryCarrierId:int, countryRegionId:int, exchangeId:int, creativeId:int, adId:int, campaignId:int, advertiserId:chararray, bidderModelId:int, nofillReason:chararray, total_request:int, total_impression:int, total_bidValue:double, total_click:int, total_win:int, total_win_bidValue:double, total_csc:int, total_event_type:int, demandCharges:double, supplyCost:double, earning:double, conversion:int,bidprice_to_exchange:double, browserId:int, cpa_goal:double, exchangepayout:double, exchangerevenue:double, networkpayout:double, networkrevenue:double);


convert_data = FOREACH raw_data GENERATE time as  time, siteId as siteId, deviceManufacturerId as deviceManufacturerId, deviceOsId as deviceOsId, countryId as countryId, campaignId as campaignId, total_request as total_request, total_impression as total_impression, total_click as total_click, total_win as total_win, total_csc as total_csc, demandCharges as demandCharges, supplyCost as supplyCost,conversion as conversion, browserId as browserId, exchangepayout as exchangepayout;

yes_total_metric = FOREACH convert_data GENERATE time as time, total_request as total_request, total_impression as total_impression, total_csc as total_csc, total_win as total_win, demandCharges as demandCharges, supplyCost as supplyCost, total_click as total_click, conversion as conversion; 

yes_total_metric_grp = GROUP yes_total_metric BY time;

yes_total_metric_grp_gen = FOREACH yes_total_metric_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME,FLATTEN(group),  SUM(yes_total_metric.total_request) as total_request, SUM(yes_total_metric.total_impression) as total_impression, SUM(yes_total_metric.total_csc) as total_csc, SUM(yes_total_metric.total_win) as total_win, SUM(yes_total_metric.demandCharges) as demandCharges, SUM(yes_total_metric.supplyCost) as supplyCost, SUM(yes_total_metric.total_click) as total_click, SUM(yes_total_metric.conversion) as conversion;

STORE yes_total_metric_grp_gen INTO '$OUTPUT/dashboard_total_metric' USING PigStorage('');

yes_country = FOREACH convert_data GENERATE  time as  time, countryId as countryId, total_request as total_request;
yes_country_grp = GROUP yes_country BY (time, countryId);
yes_country_gen = FOREACH yes_country_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_country.total_request) as total_request;
STORE yes_country_gen INTO  '$OUTPUT/dashboard_yes_country' USING PigStorage('');

yes_manufacturer = FOREACH convert_data GENERATE  time as  time,  deviceManufacturerId as deviceManufacturerId, total_request as total_request;
yes_manufacturer_grp = GROUP yes_manufacturer BY (time, deviceManufacturerId);
yes_manufacturer_gen = FOREACH yes_manufacturer_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_manufacturer.total_request) as total_request;
STORE yes_manufacturer_gen INTO  '$OUTPUT/dashboard_yes_manufacturer' USING PigStorage('');

yes_os = FOREACH convert_data GENERATE  time as  time,  deviceOsId as deviceOsId, total_request as total_request;
yes_os_grp = GROUP yes_os BY (time, deviceOsId);
yes_os_gen = FOREACH yes_os_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_os.total_request) as total_request;
STORE yes_os_gen INTO  '$OUTPUT/dashboard_yes_os' USING PigStorage('');

yes_browser = FOREACH convert_data GENERATE  time as  time,  browserId as browserId, total_request as total_request;
yes_browser_grp = GROUP yes_browser BY (time, browserId);
yes_browser_gen = FOREACH yes_browser_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_browser.total_request) as total_request;
STORE yes_browser_gen INTO  '$OUTPUT/dashboard_yes_browser' USING PigStorage('');

yes_site = FOREACH convert_data GENERATE  time as  time,  siteId as siteId, supplyCost as supplyCost, exchangepayout as exchangepayout,total_impression as total_impression, total_win as total_win;
yes_site_grp = GROUP yes_site BY (time, siteId);
yes_site_gen = FOREACH yes_site_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_site.supplyCost) as supplyCost, SUM(yes_site.exchangepayout) as exchangepayout, SUM(yes_site.total_impression) as total_impression, SUM(yes_site.total_win) as total_win;
STORE yes_site_gen INTO  '$OUTPUT/dashboard_yes_site' USING PigStorage('');

yes_campaign = FOREACH convert_data GENERATE  time as  time,  campaignId as campaignId, demandCharges as demandCharges;
yes_campaign_grp = GROUP yes_campaign BY (time, campaignId);
yes_campaign_gen = FOREACH yes_campaign_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_campaign.demandCharges) as demandCharges;
STORE yes_campaign_gen INTO  '$OUTPUT/dashboard_yes_campaign' USING PigStorage('');

yes_site_v1 = FOREACH convert_data GENERATE  time as  time,  siteId as siteId, total_request as total_request, total_impression as total_impression, total_click as total_click, total_csc as total_csc ,  total_win as total_win, supplyCost as supplyCost;
yes_site_v1_grp = GROUP yes_site_v1 BY (time, siteId);
yes_site_v1_gen = FOREACH yes_site_v1_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_site_v1.total_request), SUM(yes_site_v1.total_impression), SUM(yes_site_v1.total_click), SUM(yes_site_v1.total_csc) ,  SUM(yes_site_v1.total_win), SUM(yes_site_v1.supplyCost);
STORE yes_site_v1_gen INTO  '$OUTPUT/dashboard_yes_site_v1' USING PigStorage('');

yes_campaign_v1 = FOREACH convert_data GENERATE  time as  time,  campaignId as campaignId, total_impression as total_impression, total_win as total_win, total_click as total_click, conversion as conversion, demandCharges as demandCharges, total_csc as total_csc;
yes_campaign_v1_grp = GROUP yes_campaign_v1 BY (time, campaignId);
yes_campaign_v1_gen = FOREACH yes_campaign_v1_grp GENERATE '$PROCESS_TIME' as PROCESS_TIME, FLATTEN(group), SUM(yes_campaign_v1.total_impression) as total_impression, SUM(yes_campaign_v1.total_win) as total_win, SUM(yes_campaign_v1.total_click) as total_click, SUM(yes_campaign_v1.conversion) as conversion, SUM(yes_campaign_v1.demandCharges), SUM(yes_campaign_v1.total_csc) ;
STORE yes_campaign_v1_gen INTO  '$OUTPUT/dashboard_yes_campaign_v1' USING PigStorage('');


