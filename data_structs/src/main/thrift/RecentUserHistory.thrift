namespace java com.kritter.user.thrift.struct

/**
  * Contains one event corresponding to an impression shown to a user
  */
struct ImpressionEvent {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. 
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: byte version = 1,
2: optional i32 adId, /* id of the ad corresponding to impression */
3: optional i64 timestamp, /* timestamp at which the ad was shown to the user */
}

/**
  * Contains one event corresponding to a click shown to a user
  */
struct ClickEvent {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed.
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: byte version = 1,
2: optional i32 adId, /* id of the ad corresponding to click */
3: optional i64 timestamp, /* timestamp at which the ad was clicked by the user */
4: optional i64 impressionTimestamp, /* timestamp at which the ad was shown to the user */
}

/**
  * Contains the recent impression history of the user.
  */
struct RecentImpressionHistory {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. 
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: byte version = 1,
2: optional list<ImpressionEvent> circularList, /* Circular list containing all the recent impressions shown to the user */
3: optional i32 position, /* position to indicate the beginning of the circular list */
}

/**
  * Contains the recent click history of the user.
  */
struct RecentClickHistory {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. 
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: byte version = 1,
2: optional list<ClickEvent> clickEventCircularList, /* Circular list containing all the recent clicks by the user */
3: optional i32 clickEventListPosition, /* position to indicate the beginning of click event circular list */
}

enum EventType {
IMPRESSION = 1,
CLICK = 2,
}

struct LifetimeDemandHistory {
1: byte version = 1,
2: optional map<i32, i64> demandEventCount,
3: optional EventType eventType,
}
