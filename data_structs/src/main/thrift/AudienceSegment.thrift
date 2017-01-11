namespace java com.kritter.audience.thrift.struct

struct AudienceCategory {
1: optional set<i32> catId, /* id of the category corresponding to impression */
}


/**
*/
struct AudienceSegment {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. 
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: byte version = 1,
2: optional i32 gender,
3: optional i32 agerange,
4: optional map<i32,AudienceCategory> tiercategoryMap,
}
