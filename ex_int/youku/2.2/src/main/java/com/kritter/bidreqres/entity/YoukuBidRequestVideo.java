package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionVideoObjectDTO;
import com.kritter.constants.VideoBidResponseProtocols;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestVideo extends BidRequestImpressionVideoObjectDTO
{
	@JsonProperty("protocols")
    private Integer[] videoBidResponseProtocol;

    
	public void setVideoBidResponseProtocol() {
		videoBidResponseProtocol = new Integer[1];
		videoBidResponseProtocol[0] = VideoBidResponseProtocols.NONVAST.getCode();
	}

    @JsonIgnore
	public Integer[] getVideoBidResponseProtocol() {
    	if(videoBidResponseProtocol == null){
    		this.setVideoBidResponseProtocol();
    	}
		return videoBidResponseProtocol;
	}

}
