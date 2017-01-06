

REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;

rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' USING PigStorage('') as (process_time:chararray,time:chararray,
                pubincId:int, siteId:int, deviceOsId:int,countryId:int, ext_site:int, advincId:int, campaignId:int,
                adId:int, formatId:int, reqState:chararray, nofill:chararray,
                floor:double, winprice:double, bid:double, total_request:long,total_impression:long);

convert_data = FOREACH raw_data GENERATE com.kritter.kumbaya.libraries.pigudf.RollUpDateConversion('$rolluptype',time) AS time, 
                pubincId as pubincId, siteId as siteId, deviceOsId as deviceOsId,
                countryId as countryId, ext_site as ext_site, advincId as advincId, campaignId as campaignId,
                adId as adId, formatId as formatId, reqState as reqState, nofill as nofill,
                floor as floor, winprice as winprice, bid as bid, total_request,total_impression;
                

group_data =  GROUP convert_data BY (time,
                pubincId, siteId, deviceOsId,
                countryId, ext_site, advincId, campaignId,
                adId, formatId, reqState, nofill);

flatten_data = FOREACH group_data {
                GENERATE FLATTEN(group),
                SUM(convert_data.floor) as floor,
                SUM(convert_data.winprice) as winprice,
                SUM(convert_data.bid) as bid,
                SUM(convert_data.total_request) as total_request,
                SUM(convert_data.total_impression) as total_impression;
            }

flatten_data_store = FOREACH flatten_data GENERATE '$PROCESS_TIME', group::time,
                group::pubincId, group::siteId, group::deviceOsId,
                group::countryId, group::ext_site, group::advincId, group::campaignId,
                group::adId, group::formatId, group::reqState, group::nofill,
                floor,winprice,bid,total_request,total_impression;

STORE flatten_data_store INTO '$OUTPUT/exchange_daily' USING PigStorage('');
