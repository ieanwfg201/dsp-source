namespace java com.kritter.adserving.thrift.struct

/**
 * Change version whenever there is change in the structure.
 */

enum InventorySource{
/*Enum key <= 32 chars */
RTB_EXCHANGE = 2,
DIRECT_PUBLISHER = 1,
SSP = 3,
DCP = 4,
AGGREGATOR = 5,
OPENRTB_AGGREGATOR = 6,
}

enum TerminationReason{
HEALTHY_REQUEST = 1,
REQUEST_MALFORMED = 2,
SITE_NOT_FIT = 3,
DEVICE_UNDETECTED = 4,
DEVICE_BOT = 5,
}

/**
* Reason for no fill
*/
enum NoFillReason{
FILL = 1,
BIDDER_FLOOR_UNMET = 2,
NO_ADS = 3,
ALPHA_EMPTY = 4,
AD_ABSENT_IN_ALPHA = 5,
BID_ZERO = 6,
PUB_FLOOR_ABSENT = 7,
CAMPAIGN_BID_FLOOR_UNMET = 8,
NO_ADS_COUNTRY_CARRIER = 9,    /*No ads found for this country carrier combination*/
NO_ADS_BRAND = 10,             /*No ads found for this brand which also targets above country carrier combination*/
NO_ADS_MODEL = 11,             /*No ads found for this model which also targets above country carrier and brand combination*/
SITE_INC_EXC_ADV = 12,         /*Site excludes specific advertisers*/
AD_INV_SRC = 13,               /*Ad targets specific inventory source, network or exchange*/
AD_INV_SRC_TYPE = 14,          /*Ad targets specific inventory source type , wap or app*/
AD_SITE_CATEGORY_INC_EXC = 15, /*Ad includes or excludes specific site's content category*/
SITE_AD_CATEGORY_INC_EXC = 16, /*Site includes or excludes specific ad's content categories*/
AD_PUB_INC_EXC = 17,           /*Ad includes or excludes specific publishers*/
AD_SITE_INC_EXC = 18,          /*Ad includes or excludes specific sites*/
AD_SITE_HYGIENE = 19,          /*Ad and site hygiene do not match up*/
AD_OS_MIDP = 20,               /*Ad targets some OS and its version or some midp version, not found in this request*/
AD_BROWSER = 21,               /*Ad targets some browser and its version not found in this request*/
SITE_AD_DOMAIN = 22,           /*Site excludes certain ad domains */
AD_CUSTOM_IP = 23,             /*Ad targets custom ip addresses ,request is not from the ip address set.*/
AD_HOUR_DAY = 24,              /*Ad targets certain hours of day, request has bad timing.*/
CREATIVE_ATTR = 25,            /*Selected Ad's creatives could not match the requested attributes.*/
CREATIVE_SIZE = 26,            /*Selected Ad's creatives could not match the requested slot size.*/
ECPM_FLOOR_UNMET = 27,         /*Selected Ads could not meet the ecpm floor required by site (network) or impression floor(exchange)*/
SITE_INC_EXC_CAMPAIGNS = 28,   /*Site excludes specific campaigns.*/
CAMPAIGN_DATE_BUDGET = 29,     /*Ad's Campaign is expired or out of budget*/
BIDDER_BID_NEGATIVE_ZERO = 30, /*The online bidder calculated bid as negative or zero for final selected ads.*/
INTERNAL_ERROR = 31,           /*There was an internal error in the workflow or exception due to which fill could not be delivered.*/
AD_ZIPCODE = 32,               /*If ad is zipcode targeted and requesting lat-long doesnot fall into that zipcode range*/
AD_LAT_LONG = 33,              /*If ad is lat-long targeted with some radius given in kms*/
AD_EXT_SUPPLY_ATTR = 34,       /*If ad targets external supply attributes and exchange request does not qualify*/
AD_DIRECT_SUPPLY_INC_EXC = 35, /*If ad includes or excludes direct supply attributes, siteid,publisherid*/
AD_EXCHNG_SUPPLY_INC_EXC = 36, /*If ad includes or excludes supply attributes, siteid,publisherid,external attributes available from exchange etc.*/
SITE_INC_EXC_ADV_CMPGN = 37,   /*If site includes or excludes demand attributes, advertiser id or campaign id etc.*/
NO_ADS_CONNECTION_TYPE = 38,   /*No ads found targeting this connection type*/
NO_ADS_TABLET_TARGETING = 39,  /*Failed in tablet targeting matching*/
AD_SUPPLY_INC_EXC = 40,        /*If ad includes or excludes supply attributes, siteid,publisherid,external attributes available.*/
ONLY_MEDIATION_DP = 41,        /*If supply specifies only mediation as demand preference and no ad is of demand type api.*/
ONLY_DSP_DP = 42,              /*If supply specifies only dsp as demand preference and no ad is of demand type dsp is found.*/
EX_OD_REQ_CONVERT = 43,          /* Only dsp demand when request cannot be converted to bid request object*/
EX_OD_REQ_SER_NULL = 44,         /* Only dsp demand when bid request object cannot be serialized*/
EX_OD_URL_MAP_EMP = 45,          /* Only dsp demand when dsp url mapping is null*/
EX_OD_EXEC_NULL = 46,            /* Only dsp demand when executor service is null*/
EX_OD_API_INCORRECT = 47,        /* Only dsp demand when api name is null*/
EX_OD_RESP_EMPTY = 48,           /* Only dsp demand when adv response is empty*/
EX_OD_BID_RESP_EMPTY = 49,       /* Only dsp demand when bid response converted from adv response is empty*/
EX_OD_AP_INCORRECT = 50,         /* Only dsp demand when auction price is incorrect*/
EX_OD_WIN_NULL = 51,             /* Only dsp demand when win is null*/
EX_OD_EXCEPTION = 52,            /* Only dsp demand when their is exception*/
ONLY_DIRECT_DP = 53,             /* If supply specifies only direct as demand preference and no ad is of demand type dsp is found.*/
ONLY_DIRECTthenMed_DP = 54,      /* If supply specifies only direct then mediation as demand preference and no ad is of demand type dsp is found.*/
ONLY_DIRECTthenDSP_DP = 55,      /* If supply specifies only direct then dsp as demand preference and no ad is of demand type dsp is found.*/
NATIVE_MISMATCH = 56,            /* Native supply and creative mismatch */
VIDEO_MISMATCH = 57,             /* Video supply and creative mismatch */
Video_ApiFramework = 58,         /*Video supply and ApiFramework mismatch*/
Video_Mime = 59,                 /*Video supply and Mime mismatch*/
Video_Duration = 60,             /*Video supply and Duration mismatch*/
Video_Protocol = 61,             /*Video supply and Protocol mismatch*/
Video_StartDelay = 62,           /*Video supply and StartDelay mismatch*/
Video_Width = 63,                /*Video supply and Width mismatch*/
Video_Height = 64,               /*Video supply and Height mismatch*/
Video_Linearity = 65,            /*Video supply and Linearity mismatch*/
Video_MaxExtended = 66,          /*Video supply and MaxExtended mismatch*/
Video_BitRate = 67,              /*Video supply and BitRate mismatch*/
Video_Boxing = 68,               /*Video supply and Boxing mismatch*/
Video_PlayBack = 69,             /*Video supply and Playback mismatch*/
Video_Delivery = 70,             /*Video supply and Delivery mismatch*/
Video_CompanionType = 71,        /*Video supply and CompanionType mismatch*/
Video_Exception = 72,            /*Video supply and Handled Exception*/
CREATIVE_NOT_VIDEO = 73,         /*creative is not of video format*/
DEAL_ID_MISMATCH = 74,           /*deal id mismatch, if ad targets deal id and not found*/
VIDEO_PROPS_NULL = 75,           /*Video properties are missing in creative.*/
NATIVE_REQ_NULL = 76,            /*Native req object null*/
NATIVE_PROPS_NULL = 77,          /*Native Demand Props Null*/
NATIVE_REQ_ASSET_NULL = 78,      /*Native Req Asset Null*/
CREATIVE_NOT_NATIVE = 79,        /*Creative Not Native*/
NATIVE_TITLE_LEN = 80,           /*Native Title Length Mismatch*/
NATIVE_IMGSIZE = 81,             /*Native Image Size Mismatch*/
NATIVE_DESC_LEN = 82,            /*Native DESC Length Mismatch*/
DEVICE_TYPE = 83,                /*Device type mismatch*/
FREQUENCY_CAP = 84,              /*Frequency cap reached*/
CREATIVE_FORMAT_ERROR = 85,      /*Creative format neither banner nor richmedia or not found*/
NO_ADS_COUNTRY_CARRIER_OS_BRAND = 86,  /*Either of country or carrier or os or brand matching not found. To be used for dumping the no fill reason for the ads in db.*/
CAMPAIGN_NOT_FOUND = 87,         /*Campaign not found*/
RETARGETING = 88,                /*Retargeting filter applied*/
LOST_TO_COMPETITION = 89,        /*Lost to other competing ads due to not having best ECPM.*/
NO_ADS_STATE = 90,               /*The state targeting for ad(s) failed or state could not be detected and ad(s) were targeting states*/
NO_ADS_CITY = 91,                /*The city targeting for ad(s) failed or city could not be detected and ad(s) were targeting cities*/
MMA_MISMATCH = 92,               /*MMA Taregting or filter did not match*/ 
MMA_IND_FILTER = 93,             /*MMA industry filter fail*/ 
MMA_CAT_TARGET = 94,             /*MMA cat targeting fail*/ 
ADPOSITION_MISMATCH = 95,        /*Ad Position Targeting fail*/ 
CHANNEL_MISMATCH = 96,           /*Channel Targeting fail*/ 
VIDEO_DEMAND_PROPS_NF= 97,       /*VideoDemandProps Absent*/
VIDEO_SUPPLY_PROPS_NF= 98,       /*VideoSupplyProps Absent*/
USER_ID_INC_EXC_FILTER = 99,     /*User Id Inclusion/Exclusion filter applied*/
SSL_TRAFFIC_FILTER = 100,        /* Ad landing page is not https and the traffic is ssl enabled */
USER_ID_ABSENT = 101,            /* Ad is user targeted but user id is not present in the request */
AUDIENCE_MISMATCHE = 105,      /* AUDIENCE_MISMATCHE  */
BLKBYCOSTPERDAILY = 102,            /* Blocked by cost per daily */
AUDIENCE_TARGETING_FAILED = 103,    /* Ad is targeting audience but the targeting didn't match */
ADVINFOMISMATCH = 104  /* Adv Info MisMatch */
}

/**
* Demand Partner No Fill
*/
enum DPNoFill{
FILL = 1,
TIMEOUT = 2,
NO_ADS = 3,
MISMATCH = 4,
}


/**
 * This is impression object.
 */
struct Impression {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. 
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: i32 version = 1,
2: optional string impressionId,
3: optional i32 creativeId,
4: optional i32 adId,
5: optional i32 campaignId,
6: optional string advertiserId,
/**
 * Final bid value that bidder gives for bidding on exchange. 
 */
7: optional double bidValue,
8: optional i16 slotId,
9: optional i32 adv_inc_id, /*advertiser integer id from mysql,always populated*/
10: optional double predictedCTR, /*predicted ctr for this ad*/
11: optional i16 marketplace,   /*marketplace*/
/*NOTE ANY CHANGE IN IMPRESSION OBJECT requires change in first_level.pig in KUMBAYA */
}

/*User struct to keep user information from direct publisher or exchange. ***TODO*** Update it to exchange compatible.*/
/*This object should only have direct fields like age,gender,income group,phonenumber,email etc... whatever is available 
/*from exchanges in form of different segments*/
struct User {
1: optional string uniqueUserId,
}

/**
 *This is adserving request response object.
 */

struct AdservingRequestResponse {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. Addition of a field doesn't require changes except for bookkeeping.
 */
1: i32 version = 1,
2: optional string requestId,
3: optional InventorySource inventorySource,
4: optional TerminationReason terminationReason, /*termination reason could be fraud,enrichment error or any other internal error reason.*/
5: optional i64 time, /* system time since epoch (in seconds) */
6: optional i32 siteId,
7: optional list<i16> siteCategoryId,
8: optional i64 deviceId,
9: optional i32 deviceManufacturerId,
10: optional i32 deviceModelId,
11: optional i32 deviceOsId,
12: optional i32 countryId,
13: optional i32 countryCarrierId,
14: optional i32 countryRegionId,
15: optional string userAgent,
16: optional string ipAddress,
17: optional string xForwardedFor,
18: optional list<Impression> impressions,
19: optional map<string,string> urlExtraParameters,
20: optional i32 exchangeId, /* gets populated in case of exchange request,direct publisher,aggregator,etc.*/
21: optional string extSiteId,
22: optional string auction_id, /* openrtb 2.1 macro ${AUCTION_ID} */
23: optional string auction_imp_id, /* openrtb 2.1 macro ${AUCTION_IMP_ID} */
24: optional i16 bidderModelId,
25: optional i16 selectedSiteCategoryId, 
26: optional NoFillReason nofillReason,
27: optional i32 detectedCountryId,
28: optional i32 detectedCountryCarrierId,
29: optional i16 sitePlatformValue, /*Codes found in SITE_PLATFORM enum in java application.*/
30: optional double endUserlatitudeValue, /*latitude value as obtained from direct publisher or exchange.*/
31: optional double endUserlongitudeValue, /*longitude value as obtained from direct publisher or exchange.*/
32: optional User endUserIdentificationObject,
33: optional list<i16> reqSlotIds, /*contains slotid being requested either by direct publisher or by exchange*/
34: optional i32 browserId,
35: optional string mcc_mnc, /*This field contains mcc mnc couple.*/
36: optional string ext_supply_url, /*This field contains requesting supply source complete url.*/ 
37: optional string ext_supply_id, /*This field contains requesting supply source id, app-id or wap-id.*/ 
38: optional string ext_supply_name, /*This field contains supply source name if available from exchange*/
39: optional i16 supply_source_type, /*supply source type as wap,app ids, 2 for wap and 3 for app*/
41: optional string ext_supply_domain, /*supply source's site domain from exchange*/
42: optional i32 ext_supply_attr_internal_id, /*for an exchange request this is internal id(from sql database) for external supply attributes*/
43: optional i16 connectionTypeId, /*id of connection type corresponding to the ip*/
44: optional i16 direct_nofill_passback, /*id from SITE_PASSBACK.java, in case for direct publisher site nofill event,0 means passback not done,1 means done*/
45: optional i32 pub_inc_id, /*publisher integer id from mysql, always populated,-1 in case not available*/
46: optional DPNoFill dpNoFill,
47: optional list<string> externalUserIds, /*external user ids obtained from the supply source*/
48: optional i16 deviceType,
49: optional list<i16> bcat,
50: optional list<i16> battr,
51: optional string ds_country, /*The name of the datasource used for country detection,example: maxmind,ip2location or some personal client's database.*/
52: optional string ds_isp,     /*The name of the datasource used for carrier detection,example: maxmind,ip2location or some personal client's database.*/
53: optional string ds_state,   /*The name of the datasource used for state detection,example: maxmind,ip2location or some personal client's database.*/
54: optional string ds_city,    /*The name of the datasource used for city detection,example: maxmind,ip2location or some personal client's database.*/
55: optional i32 stateId,       /*state id(ui_targeting_state) of the end user.*/
56: optional i32 cityId,        /*city id(ui_targeting_city) of the end user.*/
57: optional i32 adpositionId,  /*ad position ui id*/
58: optional i32 channelId,     /*channel internal id*/
59: optional double bidFloor,   /*bid floor of ad-request,for network supply either from api request or from site,in ad-exchange case from bidrequest*/
60: optional map<string,double> dsp_bid_price, /*map of dsp guid and bid price offered by them during auction/pmp-deal*/
61: optional list<string> deal_ids, /*list of deal ids that were present in the bid request of an ad-exchange*/
}
