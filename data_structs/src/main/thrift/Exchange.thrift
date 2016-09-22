namespace java com.kritter.adserving.thrift.struct

enum DspNoFill{
FILL = 1,
TIMEOUT = 2,
RESPERROR = 3,
LOWBID = 4,
}


struct DspInfo 
{
1: i16 version = 1,
2: optional i32 advincId,
3: optional i32 campaignId,
4: optional i32 adId,
5: optional double bid,
6: optional DspNoFill nofill,
7: optional string resp, /*raw bid response controlled by req_logging table*/
}

struct Exchange 
{
1: i16 version = 1,
2: optional string requestId,
3: optional i32 pubincId,
4: optional i32 siteId,
5: optional i32 extsupplyattr_internalid, 
6: optional string extsupplyid,
7: optional i32 deviceOsId,
8: optional i32 countryId,
9: optional i16 formatid, /*banner,video,native,richmedia etc from CreativeFormat.java*/
10: optional double floor,
11: optional string dealid,
12: optional list<DspInfo> dspInfos,
13: optional string bidrequest,                   /*raw bid request formed for sending to different DSPs,controlled by req_logging table*/
}
