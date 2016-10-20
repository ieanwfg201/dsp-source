package com.kritter.entity.exchangethrift;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.adserving.thrift.struct.DspInfo;
import com.kritter.adserving.thrift.struct.DspNoFill;
import com.kritter.adserving.thrift.struct.Exchange;
import com.kritter.adserving.thrift.struct.ReqState;

import lombok.Getter;
import lombok.Setter;

public class CreateExchangeThrift {
	@Getter@Setter
	private HashMap<String, DspInfo> dsfInfoMap;
	@Getter@Setter
	private Exchange exchange;
	@Getter@Setter
	private Logger logger;
	public CreateExchangeThrift(String loggerName){
		if(exchange == null){
			this.exchange = new Exchange();
		}
		if(dsfInfoMap == null){
			this.dsfInfoMap = new HashMap<String, DspInfo>();
		}
		this.logger=LoggerFactory.getLogger(loggerName);
	}

	
	/*
	 * 
enum DspNoFill{
FILL = 1,
TIMEOUT = 2,
RESPERROR = 3,
LOWBID = 4,
}

struct DspInfo 
{
1: i16 version = 1,
5: optional double bid,
6: optional DspNoFill nofill,
7: optional string response, 
}

	 * */
	
	public CreateExchangeThrift setRequestId(String requestId){
		this.exchange.setRequestId(requestId);
		return this;
	}
	public CreateExchangeThrift setPubincId(int pubincId){
		this.exchange.setPubincId(pubincId);
		return this;
	}
	public CreateExchangeThrift setSiteId(int siteId){
		this.exchange.setSiteId(siteId);
		return this;
	}
	public CreateExchangeThrift setExtSupplyAttrInternalId(int extSupplyAttrInternalId){
		this.exchange.setExtSupplyAttrInternalId(extSupplyAttrInternalId);
		return this;
	}
	public CreateExchangeThrift setExtSupplyId(String extSupplyId){
		this.exchange.setExtSupplyId(extSupplyId);
		return this;
	}
	public CreateExchangeThrift setDeviceOsId(int deviceOsId){
		this.exchange.setDeviceOsId(deviceOsId);
		return this;
	}
	public CreateExchangeThrift setCountryId(int countryId){
		this.exchange.setCountryId(countryId);
		return this;
	}
	public CreateExchangeThrift setFormatId(short formatId){
		this.exchange.setFormatId(formatId);
		return this;
	}
	public CreateExchangeThrift setFloor(double floor){
		this.exchange.setFloor(floor);
		return this;
	}
	public CreateExchangeThrift setDealId(String dealId){
		this.exchange.setDealId(dealId);
		return this;
	}
	public CreateExchangeThrift setBidRequest(String bidRequest){
		this.exchange.setBidRequest(bidRequest);
		return this;
	}
	public CreateExchangeThrift setReqState(ReqState reqState){
		this.exchange.setReqState(reqState);
		return this;
	}
	public CreateExchangeThrift setWinprice(double winprice){
		this.exchange.setWinprice(winprice);
		return this;
	}
	public CreateExchangeThrift addDemand(String advertiser,int advincId,int campaignId, int adId){
		if(advertiser!=null){
			DspInfo dspInfo =this.dsfInfoMap.get(advertiser);
			boolean found=true;
			if(dspInfo==null){
				found=false;
				dspInfo= new DspInfo();
			}
			dspInfo.setAdId(adId);
			dspInfo.setCampaignId(campaignId);
			dspInfo.setAdvincId(advincId);
			if(!found){
				this.dsfInfoMap.put(advertiser, dspInfo);	
			}
		}
		return this;
	}
	public CreateExchangeThrift updateDemandBid(String advertiser,double bid){
		if(advertiser!=null){
			DspInfo dspInfo =this.dsfInfoMap.get(advertiser);
			if(dspInfo!=null){
				dspInfo.setBid(bid);
			}
		}
		return this;
	}
	public CreateExchangeThrift updateDemandState(String advertiser,DspNoFill dspNoFill){
		if(advertiser!=null){
			DspInfo dspInfo =this.dsfInfoMap.get(advertiser);
			if(dspInfo!=null){
				dspInfo.setNofill(dspNoFill);
			}
		}
		return this;
	}
	public CreateExchangeThrift updateDemandResponse(String advertiser,String demandResponse){
		if(advertiser!=null){
			DspInfo dspInfo =this.dsfInfoMap.get(advertiser);
			if(dspInfo!=null){
				dspInfo.setResponse(demandResponse);
			}
		}
		return this;
	}
	public CreateExchangeThrift updateAllTimeOut(){
		for(DspInfo dspInfo:this.dsfInfoMap.values()){
			dspInfo.setNofill(DspNoFill.TIMEOUT);
		}
		return this;
	}
	public CreateExchangeThrift updateErrorReqState(ReqState reqState){
		this.exchange.setReqState(reqState);
		this.dsfInfoMap.clear();
		return this;
	}
	public CreateExchangeThrift updateGoodReqState(ReqState reqState){
		this.exchange.setReqState(reqState);
		return this;
	}
	public CreateExchangeThrift setTime(long time){
		this.exchange.setTime(time);
		return this;
	}
	
	
	public String serializeAndBase64Encode(){
		if(this.exchange == null){
			return null;
		}
		List<DspInfo> dspInfoList = new LinkedList<DspInfo>();
		if(this.dsfInfoMap != null){
			for(DspInfo dspInfo:this.dsfInfoMap.values()){
				dspInfoList.add(dspInfo);
			}
		}
		this.exchange.setDspInfos(dspInfoList);
		try{
			TSerializer ts = new TSerializer(new TBinaryProtocol.Factory());
			Base64 encoder = new Base64(0);
			String s = new String(encoder.encode(ts.serialize(this.exchange)));
			//System.out.println(s);
			return s;
		}catch(Exception e){
			this.logger.error(e.getMessage(),e);
		}
		return null;

	}
    public Exchange deserialize(String s){
    	if(s==null || s.isEmpty()){
    		return null;
    	}
    	try{
    		TDeserializer tde =  new TDeserializer(new TBinaryProtocol.Factory());
    		Base64 decoder = new Base64(0);
    		Exchange exc= new Exchange();
    		tde.deserialize(exc, decoder.decodeBase64(s.getBytes()));
    		return exc;
    	}catch(Exception e){
    		this.logger.error(e.getMessage(),e);
    	}
    	return null;
    }

}
