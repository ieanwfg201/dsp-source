namespace java com.kritter.postimpression.thrift.struct

/**
 * This is postimpression event structure.
 */
enum PostImpressionEvent{
/*event key <= 32 chars*/
THIRD_PARTY_TRACKING_EVENT = 1,
THIRD_PARTY_CLICK_EVENT = 2,
CLICK =3,
RENDER = 4,
WIN_NOTIFICATION =5,
CONVERSION =6,
INT_EXCHANGE_WIN =7,
TEVENT =8,
BEVENT_CSCWIN = 9,
USR =10,
}

enum PostImpressionTerminationReason{
/* key <= 40 chars*/
BOT_HANDSET = 1,
HANDSET_ID_MISMATCH = 2,
HANDSET_ID_MISSING_FROM_REQUEST = 3,
HANDSET_UNDETECTED = 4,
IMPRESSION_EXPIRED = 5,
IMPRESSION_ID_MISSING_FROM_REQUEST = 6,
URL_TAMPERED = 7,
URL_DATA_NOT_COMPLETE = 8,
LOCATION_MISMATCH = 9,
LOCATION_ID_MISSING_FROM_REQUEST = 10,
LOCATION_UNDETECTED = 11,
SITE_UNFIT = 12,
SITE_ID_MISSING_FROM_REQUEST = 13,
SITE_ID_NOT_PRESENT_IN_CACHE = 14,
REFERER_DOMAIN_UNKNOWN = 15,
INTERNAL_SERVER_ERROR = 16,
AD_ID_NOT_FOUND = 17,
AD_ID_MISSING = 18,
HEALTHY_REQUEST = 19,
CONVERSION_EXPIRED = 20,
DUPLICATE = 21,
BILLABLE_EVENT = 22,
RETARGETING_SEGMENT_NF = 23,
}

/**
 * This is postimpression request response object.
 */

struct PostImpressionRequestResponse {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. Addition of a field doesn't require changes except for bookkeeping.
 */

/*version of thrift struct*/
1: i32 version = 1, 

/*request id as prepared by postimpression server. internal id, always populated*/
2: optional string requestId,

/*event postimpression,always populated.*/
3: optional PostImpressionEvent event,

/*status of the event as processed by postimpression server*/
4: optional PostImpressionTerminationReason status,

/*request id as prepared by adserving server - populated in case of click,download and render*/
5: optional string srcRequestId,

/*impression id obtained from srcRequestId- populated in case of click,download and render*/
6: optional string impressionId,

/*adId obtained from srcRequestId - populated in case of click,download and render*/
7: optional i32 adId, /* also used for openrtb 2.1 macro ${AUCTION_AD_ID} */

/*campaign id corresponding to the ad - populated in case of clicks,download and render*/
8: optional i32 campaignId,

/*url hash of the url prepared for validity of the url.*/
9: optional string srcUrlHash,

/*site id where user is present and performing events. populated in case of click and render*/
10: optional i32 siteId,

/*country id from where event has come -  populated in case of click and render*/
11: optional i32 countryId,

/*device id of the end user. populated in case of click and render */
12: optional i64 deviceId,

/*device manufacturer id of the end user*/
13: optional i32 deviceManufacturerId,

/*device model id of the end user*/
14: optional i32 deviceModelId,

/*device os id of the end user*/
15: optional i32 deviceOsId,

/*in case of affiliates,masked url gives an id,this id is used for lookup of actual url*/
16: optional string aliasUrlId,

/*third party(e.g: advertiser) tracking url's event id,could be anything.e.g: download*/
17: optional string thirdPartyTrackingUrlEventId,

/*third party id(e.g: advertiser) -  populated in case of click and render*/
18: optional string thirdPartyId ,

/*third party url hash for validation*/
19: optional string thirdPartyUrlHash,

20: optional string userAgent,
21: optional string ipAddress,
22: optional string xForwardedFor,
23: optional map<string,string> urlExtraParameters,

24: optional i64 time, /* system current time since epoch (in seconds) when event was received*/
25: optional i64 eventTime, /* event occurance time since epoch (in seconds) */

26: optional double auction_price,  /* openrtb 2.1 macro ${AUCTION_PRICE} */
27: optional string auction_currency, /* openrtb 2.1 macro ${AUCTION_CURRENCY} */
28: optional string auction_seat_id, /* openrtb 2.1 macro ${AUCTION_SEAT_ID} */
29: optional string auction_bid_id, /* openrtb 2.1 macro ${AUCTION_BID_ID} */
30: optional string auction_id, /* openrtb 2.1 macro ${AUCTION_ID} */
31: optional string auction_imp_id, /* openrtb 2.1 macro ${AUCTION_IMP_ID} */
32: optional i32 exchangeId /* gets populated in case of exchange request,direct publisher,aggregator,etc.*/,
33: optional i32 countryCarrierId /*populated in case of click and render*/,
34: optional i32 countryRegionId /*populated in case of click and render */,
35: optional i16 selectedSiteCategoryId /*populated in case of exchange */,
36: optional i32 urlVersion,
37: optional i32 inventorySource /*always populated*/,
38: optional double bidprice_to_exchange,
/*maximum bid(internal) as specified by the account manager,used in application flow*/
39: optional double internal_max_bid,
/*bid value as visible to advertiser,used in reporting amounts billed to advertiser for media buy*/
40: optional double advertiser_bid,
41: optional i16 bidderModelId,
42: optional i16 marketplace_id, /*refers to id column in marketplace table == enums at com.kritter.constants.MarketPlace*/
43: optional double bid_price_to_exchange, /*bid price in ecpm , price for 1000 impressions given to exchange at time of bidding*/
44: optional i16 slotId, /*Slot id value of this impression,-1 as default.*/
45: optional i32 deviceBrowserId,
46: optional double cpa_goal,
47: optional i16 supply_source_type, /*site platform type as wap,app*/
48: optional string buyerUid, /*This field contains advertising cookie from end user set by kritter*/
49: optional i32 ext_supply_attr_internal_id, /*for an exchange request this is internal id(from sql database) for external supply attributes*/
50: optional i16 connectionTypeId, /*id of connection type corresponding to the ip*/
51: optional string referer,
52: optional i32 adv_inc_id, /*advertiser integer id from mysql,always populated,-1 in case not available*/
53: optional i32 pub_inc_id, /*publisher integer id from mysql, always populated,-1 in case not available*/
54: optional string tevent, /* tevent name */
55: optional string teventtype, /* teventtype */
56: optional i16 deviceType,           /*device Type of the requesting user,whether mobile/desktop*/
57: optional double bidFloor,          /*bid floor of the impression*/
58: optional string exchangeUserId,    /*exchange's user id*/
59: optional string kritterUserId,     /*kritter's user id*/
60: optional string externalSiteAppId, /*external supply's site/app id on exchange*/
61: optional string ifa,               /*ID sanctioned for advertiser use in the clear (i.e., not hashed)*/
62: optional string dpidmd5,           /*Platform device ID (e.g., Android ID); hashed via MD5.*/
63: optional string dpidsha1,          /*Platform device ID (e.g., Android ID); hashed via SHA1.*/
64: optional string macsha1,           /*MAC address of the device; hashed via SHA1.*/
65: optional string macmd5,            /*MAC address of the device; hashed via MD5.*/
}
