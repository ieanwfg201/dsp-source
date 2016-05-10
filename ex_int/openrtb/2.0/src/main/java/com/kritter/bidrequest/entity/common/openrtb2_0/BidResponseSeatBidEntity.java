package com.kritter.bidrequest.entity.common.openrtb2_0;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class represents bid response seat bid object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseSeatBidEntity
{

    @JsonProperty("bid")
    @Setter
    private BidResponseBidEntity[] bidResponseBidEntities;

    @JsonIgnore
    public BidResponseBidEntity[] getBidResponseBidEntities()
    {
        return bidResponseBidEntities;
    }

    @JsonProperty("seat")
    @Setter
    private String bidderSeatId;

    @JsonIgnore
    public String getBidderSeatId()
    {
        return bidderSeatId;
    }

    /**
     * “1” means impressions must be won-
     * lost as a group; default is “0”.
     */
    @JsonProperty("group")
    @Setter
    private Integer group;

    @JsonIgnore
    public Integer getGroup()
    {
        return group;
    }
}
