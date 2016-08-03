

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
       --         uniqueUserId: chararray
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
       --       pub_inc_id: int,
       --       dpNoFill: chararray,
       --       externalUserIds: {
       --         t: (
       --           externalUserIds_tuple: chararray
       --         )
       --       },
       --       deviceType: int,
       --       bcat: {
       --         t: (
       --           bcat_tuple: int
       --         )
       --       },
       --       battr: {
       --         t: (
       --           battr_tuple: int
       --         )
       --       }
       --     )

decoded = FOREACH raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
decoded_data = FOREACH decoded GENERATE ThriftBytesToTupleDef($0);

proj_data = FOREACH decoded_data GENERATE FLATTEN(AdservingRequestResponse.externalUserIds) as externalUserIds; 

group_data = GROUP proj_data by externalUserIds;

group_data_gen = FOREACH group_data GENERATE FLATTEN(group) , COUNT(proj_data) as uucount;

generate_final = FOREACH group_data_gen GENERATE uucount as uucount;

group_final = GROUP generate_final ALL;

group_final_output = FOREACH  group_final GENERATE COUNT(generate_final);

STORE group_final_output INTO '$OUTPUT/total_uu_count' USING PigStorage('');
