package com.kritter.bidreqres.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseBidEntity;

import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseBidYoukuEntity extends BidResponseBidEntity{
    @JsonProperty("pvm")
    @Setter
    private String pvm;

    @JsonIgnore
    public String getPvm()
    {
        return pvm;
    }
    @JsonProperty("clickm")
    @Setter
    private String clickm;

    @JsonIgnore
    public String getClickm()
    {
        return clickm;
    }
    /**
     * extension object.
     */
    @JsonProperty("ext")
    @Setter
    private BidResponseBidExtYoukuEntity extensionObject;

    @JsonIgnore
    public BidResponseBidExtYoukuEntity getExtensionObject(){
        return extensionObject;
    }

}
