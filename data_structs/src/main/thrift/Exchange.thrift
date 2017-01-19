namespace java com.kritter.adserving.thrift.struct

enum DspNoFill{
/*less than 16 characters*/
FILL = 1,
TIMEOUT = 2,
RESPERROR = 3,
LOWBID = 4,
FLOORUNMET = 5,
EMPTY_RESPONSE = 6,
}

enum ReqState{
/*less than 16 characters*/
HEALTHY = 1,
REQ_ERR = 2,
KEXEC_ERR=3,
URI_ERR=4,
ASYNC_ERR=5,
AUCTION_ERR=6,
AUC_NF=7,
}

struct DspInfo 
{
1: i16 version = 1,
2: optional i32 advincId,
3: optional i32 campaignId,
4: optional i32 adId,
5: optional double bid,
6: optional DspNoFill nofill,
7: optional string response, /*raw bid response controlled by req_logging table*/
}

struct Exchange 
{
1: i16 version = 1,
2: optional string requestId,
3: optional i32 pubincId,
4: optional i32 siteId,
5: optional i32 extSupplyAttrInternalId, 
6: optional string extSupplyId,
7: optional i32 deviceOsId,
8: optional i32 countryId,
9: optional i16 formatId, /*banner,video,native,richmedia etc from CreativeFormat.java*/
10: optional double floor,
11: optional string dealId,
12: optional list<DspInfo> dspInfos,
13: optional string bidRequest,                   /*raw bid request formed for sending to different DSPs,controlled by req_logging table*/
14: optional ReqState reqState,
15: optional double winprice,
16: optional i64 time, /* system time since epoch (in seconds) */
}
