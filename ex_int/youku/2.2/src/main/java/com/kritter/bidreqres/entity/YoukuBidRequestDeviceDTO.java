package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestDeviceDTO;

import lombok.Setter;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YoukuBidRequestDeviceDTO extends BidRequestDeviceDTO
{
	@JsonProperty("ip")
	private String ipV4AddressClosestToDevice;
	
	public void setIpV4AddressClosestToDevice(String ipV4AddressClosestToDevice){
		if(ipV4AddressClosestToDevice != null &&
				ipV4AddressClosestToDevice.contains(":")){
			setIpV6Address(ipV4AddressClosestToDevice);
		}else{
			this.ipV4AddressClosestToDevice = ipV4AddressClosestToDevice;
		}
	}

    @JsonIgnore
    public String getIpV4AddressClosestToDevice(){
        return ipV4AddressClosestToDevice;
    }
    
    /*ID sanctioned for advertiser use in the clear (i.e., not hashed).*/
    @JsonProperty("idfa")
    private String idfa;

    public void setIdfa(String idfa){
        this.idfa = idfa;
    }
    
    @JsonIgnore
    public String getIdfa(){
        return idfa;
    }
    /**
     * If os = android, this field is valid. Transmitted in clear text. Default value is empty string.
     */
    @JsonProperty("androidid")
    @Setter
    private String androidid;

    @JsonIgnore
    public String getAndroidid(){
        return androidid;
    }
    /**
     * IMEI, transmitted in clear text. Default value is empty string.
     */
    @JsonProperty("imei")
    @Setter
    private String imei;

    @JsonIgnore
    public String getImei(){
        return imei;
    }
    /**
     * MAC, transmitted in clear text. Default value is empty string
     */
    @JsonProperty("mac")
    @Setter
    private String mac;

    @JsonIgnore
    public String getMac(){
        return mac;
    }

    /**
     * Screen width. Unit: pixel. Default value is empty string.
     */
    @JsonProperty("screenwidth")
    @Setter
    private String screenwidth;

    @JsonIgnore
    public String getScreenwidth(){
        return screenwidth;
    }

    @JsonProperty("screenhight")
    @Setter
    private String screenhight;

    @JsonIgnore
    public String getcreenhight(){
        return screenhight;
    }
}
