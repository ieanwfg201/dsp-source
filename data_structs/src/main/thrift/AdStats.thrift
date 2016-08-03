namespace java com.kritter.adserving.thrift.struct

struct AdToNofillReasonCount {
1: i32 version = 1,
2: i32 adId,
3: map<string, i64> nofillReasonCount, /*Map containing count for each no fill reason. key is the integer for no fill reason. Value is the number of times nofill occurred.*/
}

struct AdStats {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. Addition of a field doesn't require changes except for bookkeeping.
 */
1: i32 version = 1,
2: map<i64, map<i32, AdToNofillReasonCount>>  adStatsMap, /*Map to contain the ad stats per hour for all the ads. This key is the timestamp (typically would be the timestamp for the current hour in seconds). The value is a list of ad stats objects containins stats for each ad.*/
3: map<i64, i64> hourTotalRequestsMap, /*Map to contain the total requests in the given hour. Key is the long timestamp for the start of hour. Value is the number of requests in the given hour*/
}
