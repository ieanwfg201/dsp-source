namespace java com.kritter.user.thrift.struct

struct Segment {
1: byte version = 1,
2: optional i32 segmentId, /* id of the ad corresponding to impression */
}


/**
*/
struct UserSegment {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. 
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: byte version = 1,
2: optional set<Segment> segmentSet,
}
