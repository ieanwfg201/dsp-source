package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestContentDTO;

import lombok.Setter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestContentExtDTO extends BidRequestContentDTO
{
	/**
	 * Channel ID of video, for example: “a”. Please refer to Youku ADX channel list for detailed information.
	 */
	@JsonProperty("channel")
    @Setter
    private String channel;
	/**
	 * 2nd-level channel ID. Please refer to Youku ADX 2nd-level channel list for detailed information.
	 */
	@JsonProperty("cs")
    @Setter
    private String cs;
	/**
	 * Id of video uploader.
	 */
	@JsonProperty("usr")
    @Setter
    private String usr;
	/**
	 * Program id.
	 */
	@JsonProperty("s")
    @Setter
    private String s;
	/**
	 * Video id.
	 */
	@JsonProperty("vid")
    @Setter
    private String vid;
	
	public String getChannel(){
		return channel;
	}
	public String getCs(){
		return cs;
	}
	public String getUsr(){
		return usr;
	}
	public String getS(){
		return s;
	}
	public String getVid(){
		return vid;
	}
}
