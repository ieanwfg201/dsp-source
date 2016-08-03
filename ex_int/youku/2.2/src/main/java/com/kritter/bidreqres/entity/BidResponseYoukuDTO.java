package com.kritter.bidreqres.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity;

import lombok.Setter;

/**
 * This class models bid response entity for youku.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseYoukuDTO extends BidResponseEntity
{
    @JsonProperty("seatbid")
    @Setter
    private BidResponseSeatBidYoukuEntity[] bidResponseSeatBid;

    @JsonIgnore
    public BidResponseSeatBidYoukuEntity[] getBidResponseSeatBid()
    {
        return bidResponseSeatBid;
    }

}
