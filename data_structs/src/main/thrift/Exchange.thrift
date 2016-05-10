namespace java com.kritter.exchange.thrift.struct

/*No Fill Reason*/
enum ExchangeNFR{
/*event key <= 32 chars
Prefix BR refers to Bid Request
Prefix RE refers to response */
HEALTHY=1,
BR_ID_NF=2,
BR_IMP_NF=3,
BR_UA_NF=4,
}

struct Bidder {
    
}

struct Exchange {
1: i32 version = 1,
}
