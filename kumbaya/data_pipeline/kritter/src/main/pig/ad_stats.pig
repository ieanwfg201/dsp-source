

REGISTER target/lib/elephant-bird-core-4.3.jar;
REGISTER target/lib/elephant-bird-pig-4.3.jar;
REGISTER target/lib/elephant-bird-hadoop-compat-4.3.jar;
REGISTER target/lib/libthrift-0.9.0.jar;
REGISTER $thrift_jar_path;
REGISTER target/lib/kumbaya_libraries_pigudf-1.0.0.jar;
REGISTER target/lib/piggybank-0.12.0.jar;

rmf $OUTPUT;
mkdir $OUTPUT;

DEFINE AdStatsThriftBytesToTupleDef com.twitter.elephantbird.pig.piggybank.ThriftBytesToTuple('com.kritter.adserving.thrift.struct.AdStats');

raw_data = load '$INPUT_FILES' using TextLoader() as (record:chararray);
       --  as (
       --       version: int,
       --       adStatsMap: map[
       --         map[
       --           (
       --             version: int,
       --             adId: int,
       --             nofillReasonCount: map[
       --               long
       --             ]
       --           )
       --         ]
       --       ],
       --       hourTotalRequestsMap: map[
       --         long
       --       ]
       --     )

raw_decoded = FOREACH raw_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.B64Decode(record));
raw_decoded_data = FOREACH raw_decoded GENERATE AdStatsThriftBytesToTupleDef($0);

proj_data = FOREACH raw_decoded_data GENERATE AdStats.version as version, AdStats.adStatsMap as adStatsMap, AdStats.hourTotalRequestsMap as hourTotalRequestsMap;

proj_adstat = FOREACH proj_data GENERATE FLATTEN(com.kritter.kumbaya.libraries.pigudf.AdStatBag(adStatsMap,hourTotalRequestsMap)) as (tstr:long,adId,nfr,count:long) ;

proj_group_by = GROUP proj_adstat BY (tstr,adId,nfr);

group_by_sum = FOREACH proj_group_by GENERATE FLATTEN(group), SUM(proj_adstat.count) as count;

data_for_store= FOREACH group_by_sum GENERATE group::adId, group::nfr, com.kritter.kumbaya.libraries.pigudf.EpochToDateStr(group::tstr,'yyyy-MM-dd HH:00:00', '$tz'), count as count, '$PROCESS_TIME';

STORE data_for_store INTO '$OUTPUT/ad_stat' USING PigStorage('');


