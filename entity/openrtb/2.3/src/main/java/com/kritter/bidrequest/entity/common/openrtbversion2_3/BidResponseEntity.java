package com.kritter.bidrequest.entity.common.openrtbversion2_3;

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
    /**
     * nbr = 0-> Unknown Error, 1-> Technical Error, 2-> Invalid Request
     * 3-> Known Web Spider, 4 -> Suspected Non Human Traffic
     * 5 -> Cloud, Data Center, or Proxy IP, 6-> Unsupported Device
     * 7 -> Blocked Publisher or Site 8-> Unmatched user
     */
    @JsonProperty("nbr")
    @Setter
    private Integer noBidReason;

    @JsonIgnore
    public Integer getNoBidReason()
    {
        return noBidReason;
    }

    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject()
    {
        return extensionObject;
    }
}
