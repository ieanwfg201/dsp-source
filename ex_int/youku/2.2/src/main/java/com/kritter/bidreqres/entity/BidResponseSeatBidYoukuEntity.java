package com.kritter.bidreqres.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseSeatBidEntity;

import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseSeatBidYoukuEntity extends BidResponseSeatBidEntity{
    @JsonProperty("bid")
    @Setter
    private BidResponseBidYoukuEntity[] bidResponseBidEntities;

    @JsonIgnore
    public BidResponseBidYoukuEntity[] getBidResponseBidEntities()
    {
        return bidResponseBidEntities;
    }


}
