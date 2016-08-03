package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestUserDTO;

import lombok.Setter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestUserDTO extends BidRequestUserDTO
{
	@JsonProperty("tag")
	@Setter
	   private YoukuBidRequestUserTagDTO tag;
	   public YoukuBidRequestUserTagDTO getTag(){
		   return tag;
	   }

}
