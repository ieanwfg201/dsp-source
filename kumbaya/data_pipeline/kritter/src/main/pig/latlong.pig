

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
       --           slotId: int
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
       --         uniqueUserId: chararray
       --       ),
       --       reqSlotIds: {
       --         t: (
       --           reqSlotIds_tuple: int
       --         )
       --       },
       --       browserId: int
       --     )

decoded = FOREACH raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
decoded_data = FOREACH decoded GENERATE ThriftBytesToTupleDef($0);

proj_data = FOREACH decoded_data GENERATE AdservingRequestResponse.terminationReason as terminationReason, 
    com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(AdservingRequestResponse.time * 1000,'yyyy-MM-dd HH:00:00', 'UTC') as time, 
    AdservingRequestResponse.siteId as siteId, (AdservingRequestResponse.endUserlatitudeValue==0.0?0:1) as present;

filter_data = FILTER proj_data BY terminationReason == 'HEALTHY_REQUEST';

group_data = GROUP filter_data BY (time,siteId);

group_data_gen = FOREACH group_data GENERATE FLATTEN(group),COUNT(filter_data) as requests, SUM(filter_data.present) as present;

STORE group_data_gen INTO '$OUTPUT/lat_long_exists.gz' USING PigStorage('');





