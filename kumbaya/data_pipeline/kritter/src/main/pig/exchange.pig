

REGISTER target/lib/elephant-bird-core-4.3.jar;
REGISTER target/lib/elephant-bird-pig-4.3.jar;
REGISTER target/lib/elephant-bird-hadoop-compat-4.3.jar;
REGISTER target/lib/libthrift-0.9.0.jar;
REGISTER $thrift_jar_path;
REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;
REGISTER target/lib/piggybank-0.12.0.jar;

DEFINE ThriftBytesToTupleDef com.twitter.elephantbird.pig.piggybank.ThriftBytesToTuple('com.kritter.adserving.thrift.struct.Exchange');

rmf $OUTPUT;
mkdir $OUTPUT;

raw_data = load '$INPUT_FILES' using TextLoader() as (record:chararray);

       --  as (
       --       version: int,
       --       requestId: chararray,
       --       pubincId: int,
       --       siteId: int,
       --       extSupplyAttrInternalId: int,
       --       extSupplyId: chararray,
       --       deviceOsId: int,
       --       countryId: int,
       --       formatId: int,
       --       floor: double,
       --       dealId: chararray,
       --       dspInfos: {
       --         t: (
       --           version: int,
       --           advincId: int,
       --           campaignId: int,
       --           adId: int,
       --           bid: double,
       --           nofill: chararray,
       --           response: chararray
       --         )
       --       },
       --       bidRequest: chararray,
       --       reqState: chararray,
       --       winprice: double,
       --       time: long
       --     )



decoded = FOREACH raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
decoded_data = FOREACH decoded GENERATE ThriftBytesToTupleDef($0);

proj_data = FOREACH decoded_data GENERATE  
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(Exchange.time * 1000,'yyyy-MM-dd HH:00:00', '$tz') as time, 
    Exchange.reqState as reqState, Exchange.floor as floor, 
    Exchange.formatId as formatId, Exchange.countryId as countryId, Exchange.deviceOsId as deviceOsId, 
    Exchange.siteId as siteId, Exchange.pubincId as pubincId, 
    FLATTEN(com.kritter.kumbaya.libraries.pigudf.ExtractFromBagOfTupleExchange(Exchange.winprice,Exchange.dspInfos , -1, -1, -1, -1 , 0.0, '-1','', 1, 0,0.0))
     as (version:int,advincId:int,campaignId:int,adId:int,bid:double, nofill:chararray, response:chararray,total_request:long, total_impression:long,winprice:double)
    ;
    


group_data = GROUP proj_data BY (time,pubincId,siteId,deviceOsId,countryId,advincId,campaignId,adId,formatId,reqState,nofill);

flatten_group_data = FOREACH group_data {
            GENERATE FLATTEN(group),
            SUM(proj_data.floor) as floor,
            SUM(proj_data.winprice) as winprice,
            SUM(proj_data.bid) as bid,
            SUM(proj_data.total_request) as total_request,
            SUM(proj_data.total_impression) as total_impression;
        }


group_data_gen = FOREACH flatten_group_data GENERATE '$PROCESS_TIME' as process_time,group::time as time, 
                group::pubincId as pubincId, group::siteId as siteId, group::deviceOsId as deviceOsId,
                group::countryId as countryId, '-1' as ex_site, group::advincId as advincId, group::campaignId as campaignId,
                group::adId as adId, group::formatId as formatId, group::reqState as reqState, group::nofill as nofill,
                floor as floor, winprice as winprice, bid as bid, total_request as total_request,total_impression as total_impression;


STORE group_data_gen INTO '$OUTPUT/exchange_hourly' USING PigStorage('');

