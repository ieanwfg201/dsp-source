package com.kritter.bidrequest.entity.common.openrtb2_0;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class is used to map open rtb 2_1 bid response message.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseEntity
{

    @JsonProperty("id")
    @Setter
    private String bidRequestId;

    @JsonIgnore
    public String getBidRequestId()
    {
        return bidRequestId;
    }

    @JsonProperty("seatbid")
    @Setter
    private BidResponseSeatBidEntity[] bidResponseSeatBid;

    @JsonIgnore
    public BidResponseSeatBidEntity[] getBidResponseSeatBid()
    {
        return bidResponseSeatBid;
    }

    /**
     * The bid id could be used for cross referencing in case of any
     * discrepancies or for any other purpose.
     * This could be request id that adserving generates.
     */
    @JsonProperty("bidid")
    @Setter
    private String bidderGeneratedUniqueId;

    @JsonIgnore
    public String getBidderGeneratedUniqueId()
    {
        return bidderGeneratedUniqueId;
    }

    @JsonProperty("cur")
    @Setter
    private String currency;

    @JsonIgnore
    public String getCurrency()
    {
        return currency;
    }

    @JsonProperty("customdata")
    @Setter
    private String customData;

    @JsonIgnore
    public String getCustomData()
    {
        return customData;
    }
}
