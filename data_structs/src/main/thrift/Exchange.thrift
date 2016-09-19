namespace java com.kritter.adserving.thrift.struct

struct Exchange 
{
1: i16 version = 1,
2: optional string requestId,
3: optional string publisherId,
4: optional string siteId,
5: optional string ip_address,
6: optional string user_agent,
7: optional string impression_type, /*banner,video,native,richmedia*/
8: optional double bid_floor,
9: optional string deal_id,
10: optional map<string,double> dsp_bid_price_map, /*map containing bid price offered by DSPs*/
11: optional string bid_request,                   /*raw bid request formed for sending to different DSPs,controlled by req_logging table*/
12: optional map<string,string> dsp_bid_response,  /*raw bid responses sent by DSPs, controlled by req_logging table*/
}
