namespace java com.kritter.postimpression.thrift.struct

enum BillingType {
EXCHANGE_PAYOUT = 1,
CPM = 2,
CPC = 3,
CPA = 4,
CPI = 5,
CPD = 6,
INTEXCWIN = 7,
BEVENT_CSCWIN = 8,
BEVENT_CSCWIN_DEM = 9,
}

enum BillingTerminationReason{
/*MP = MarketPlace, NF = NOT FOUND, CAMP = Campaign, CLK = CLICK,
DPCLK = Direct Publisher Click, INT = Internal, NoF = No Fund, 
GT = Greater Than, INTR= Internal Remaining, ADV = Advertiser,
Tbudget = Total Budget, Dbudget = Daily Budget, BAL = Balance*/
HEALTHY_REQUEST = 1,
NO_FUND = 2,
BOT_HANDSET = 3,
HANDSET_ID_MISMATCH = 4,
HANDSET_UNDETECTED = 5,
IMPRESSION_EXPIRED = 6,
URL_TAMPERED = 7,
SITE_UNFIT = 8,
INTERNAL_SERVER_ERROR = 9,
AD_ID_NOT_FOUND = 10,
AD_ID_MISSING = 11,
DPCLK_MP_NF = 12, DPCLK_SERVED_CAMP_NOT_CPC = 13, DPCLCK_CAMP_ID_NF = 14, DPCLK_INTR_Tbudget_NoF  = 15, DPCLK_MAX_BID_GT_INTR_Tbudget = 16,
DPCLK_ADV_Tbudget_NoF =17, DPCLK_MAX_BID_GT_ADV_Tbudget = 18, DPCLK_INTR_Dbudget_NoF  = 19, DPCLK_MAX_BID_GT_INTR_Dbudget = 20,
DPCLK_ADV_Dbudget_NoF =21, DPCLK_MAX_BID_GT_ADV_Dbudget = 22, DPCLK_ACCOUNT_NF = 23,DPCLK_ADV_BID_GT_ADV_BAL = 24,DPCLK_ADV_BID_GT_INT_BAL = 25,
DPCLK_SITE_ID_NF = 26,
DPIMP_MP_NF = 27, DPIMP_SERVED_CAMP_NOT_CPM = 28, DPIMP_CAMP_ID_NF = 29, DPIMP_INTR_Tbudget_NoF  = 30, DPIMP_MAX_BID_GT_INTR_Tbudget = 31,
DPIMP_ADV_Tbudget_NoF =32, DPIMP_MAX_BID_GT_ADV_Tbudget = 33, DPIMP_INTR_Dbudget_NoF  = 34, DPIMP_MAX_BID_GT_INTR_Dbudget = 35,
DPIMP_ADV_Dbudget_NoF =36, DPIMP_MAX_BID_GT_ADV_Dbudget = 37, DPIMP_ACCOUNT_NF = 38,DPIMP_ADV_BID_GT_ADV_BAL = 39,DPIMP_ADV_BID_GT_INT_BAL = 40,
DPIMP_SITE_ID_NF = 41,
DPCLK_ACCOUNT_NoF = 42,DPIMP_ACCOUNT_NoF = 43,
DPCPD_MP_NF = 44, DPCPD_SERVED_CAMP_NOT_CPD = 45, DPCPD_CAMP_ID_NF = 46, DPCPD_INTR_Tbudget_NoF  = 47, DPCPD_MAX_BID_GT_INTR_Tbudget = 48,
DPCPD_ADV_Tbudget_NoF =49, DPCPD_MAX_BID_GT_ADV_Tbudget = 50, DPCPD_INTR_Dbudget_NoF  = 51, DPCPD_MAX_BID_GT_INTR_Dbudget = 52,
DPCPD_ADV_Dbudget_NoF =53, DPCPD_MAX_BID_GT_ADV_Dbudget = 54, DPCPD_ACCOUNT_NF = 55,DPCPD_ADV_BID_GT_ADV_BAL = 56,DPCPD_ADV_BID_GT_INT_BAL = 57,
DPCPD_SITE_ID_NF = 58,DPCPD_ACCOUNT_NoF = 59,
EP_MP_NF = 60, EP_CAMP_ID_NF = 61, 
EP_SITE_ID_NF = 62,EP_ACCOUNT_NF = 63,
EIMP_MP_NF = 64, EIMP_SERVED_CAMP_NOT_CPM = 65, EIMP_CAMP_ID_NF = 66, EIMP_INTR_Tbudget_NoF  = 67, EIMP_MAX_BID_GT_INTR_Tbudget = 68,
EIMP_ADV_Tbudget_NoF = 69, EIMP_MAX_BID_GT_ADV_Tbudget = 70, EIMP_INTR_Dbudget_NoF  = 71, EIMP_MAX_BID_GT_INTR_Dbudget = 72,
EIMP_ADV_Dbudget_NoF =73, EIMP_MAX_BID_GT_ADV_Dbudget = 74, EIMP_ACCOUNT_NF = 75,EIMP_ADV_BID_GT_ADV_BAL = 76,EIMP_ADV_BID_GT_INT_BAL = 77,
EIMP_SITE_ID_NF = 78,EIMP_ACCOUNT_NoF = 79,
ECLK_MP_NF = 80, ECLK_SERVED_CAMP_NOT_CPC = 81, ECLCK_CAMP_ID_NF = 82, ECLK_INTR_Tbudget_NoF  = 83, ECLK_MAX_BID_GT_INTR_Tbudget = 84,
ECLK_ADV_Tbudget_NoF =85, ECLK_MAX_BID_GT_ADV_Tbudget = 86, ECLK_INTR_Dbudget_NoF  = 87, ECLK_MAX_BID_GT_INTR_Dbudget = 88,
ECLK_ADV_Dbudget_NoF =89, ECLK_MAX_BID_GT_ADV_Dbudget = 90, ECLK_ACCOUNT_NF = 91,ECLK_ADV_BID_GT_ADV_BAL = 92,ECLK_ADV_BID_GT_INT_BAL = 93,
ECLK_SITE_ID_NF = 94,ECLK_ACCOUNT_NoF = 95,
INTWIN_MP_NF = 96, INTWIN_SERVED_CAMP_NOT_CPM = 97, INTWIN_CAMP_ID_NF = 98, INTWIN_INTR_Tbudget_NoF  = 99, 
INTWIN_MAX_BID_GT_INTR_Tbudget = 100, INTWIN_ADV_Tbudget_NoF =101, INTWIN_MAX_BID_GT_ADV_Tbudget = 102, 
INTWIN_INTR_Dbudget_NoF  = 103, INTWIN_MAX_BID_GT_INTR_Dbudget = 104, INTWIN_ADV_Dbudget_NoF =105, 
INTWIN_MAX_BID_GT_ADV_Dbudget = 106, INTWIN_ACCOUNT_NF = 107, INTWIN_ADV_BID_GT_ADV_BAL = 108,
INTWIN_ADV_BID_GT_INT_BAL = 109, INTWIN_SITE_ID_NF = 110, INTWIN_ACCOUNT_NoF = 111,
BEVENT_CSCWIN_MP_NF = 112, BEVENT_CSCWIN_SERVED_CAMP_NOT_CPM = 113, BEVENT_CSCWIN_CAMP_ID_NF = 114, BEVENT_CSCWIN_INTR_Tbudget_NoF  = 115, 
BEVENT_CSCWIN_MAX_BID_GT_INTR_Tbudget = 116, BEVENT_CSCWIN_ADV_Tbudget_NoF =117, BEVENT_CSCWIN_MAX_BID_GT_ADV_Tbudget = 118, 
BEVENT_CSCWIN_INTR_Dbudget_NoF  = 119, BEVENT_CSCWIN_MAX_BID_GT_INTR_Dbudget = 120, BEVENT_CSCWIN_ADV_Dbudget_NoF =121, 
BEVENT_CSCWIN_MAX_BID_GT_ADV_Dbudget = 122, BEVENT_CSCWIN_ACCOUNT_NF = 123, BEVENT_CSCWIN_ADV_BID_GT_ADV_BAL = 124,
BEVENT_CSCWIN_ADV_BID_GT_INT_BAL = 125, BEVENT_CSCWIN_SITE_ID_NF = 126, BEVENT_CSCWIN_ACCOUNT_NoF = 127,
}

struct Billing {
/**
 * Change version whenever there is change in the structure, more specifically when some field is removed. 
 * Addition of a field doesn't require changes except for bookkeeping.
 */
1: i32 version = 1,
2: optional string requestId,                                           /*Populated internally*/
3: optional i32 adId,                                                   /*populated as per post imp*/
4: optional i32 campaignId,                                             /*populated as per post imp*/
5: optional string advertiserId,
6: optional i32 exchangeId,                                             /*populated as per post imp*/
7: optional i64 time, /* system time since epoch (in seconds) */
8: optional i32 siteId,                                                 /*populated as per post imp*/
9: optional i16 billingModelId, /* billing implementation version */
10: optional string impressionId,                                       /*populated as per post imp*/
11: optional i32 countryCarrierId,                                      /*populated as per post imp*/
12: optional i32 countryRegionId,                                       /*populated as per post imp*/
13: optional i32 countryId,                                             /*populated as per post imp*/
14: optional i64 deviceId,                                              /*populated as per post imp*/
15: optional i32 deviceManufacturerId,                                  /*populated as per post imp*/
16: optional i32 deviceOsId,                                            /*populated as per post imp*/
17: optional BillingTerminationReason status,
18: optional double demandCharges,
19: optional double internalDemandCharges,
20: optional double supplyCost,
21: optional i64 eventTime,                                             /*populated as per post log time*/
22: optional i16 selectedSiteCategoryId,                                /*populated as per post imp*/
23: optional BillingType billingType,
24: optional i16 bidderModelId,
25: optional double earning,
26: optional i32 browserId,
27: optional double networkpayout,
28: optional double networkrevenue,
29: optional double exchangepayout,
30: optional double exchangerevenue,
31: optional i16 supply_source_type,
32: optional i32 ext_supply_attr_internal_id,
33: optional i16 connectionTypeId, /*id of connection type corresponding to the ip*/
34: optional i32 adv_inc_id, 
35: optional i32 pub_inc_id, 
36: optional i16 deviceType,
}


