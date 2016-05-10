package com.kritter.bidrequest.entity.common.openrtbversion2_2;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This class represents bid entity of the bid response.
 * Each bid submitted has to have a unique identifier
 * associated for debugging purposes.
 * Used when we are submitting more than one bid for a
 * single impression for the same seat.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BidResponseBidEntity
{
    @JsonProperty("id")
    @Setter
    private String bidId;

    @JsonIgnore
    public String getBidId()
    {
        return bidId;
    }

    @JsonProperty("impid")
    @Setter
    private String requestImpressionId;

    @JsonIgnore
    public String getRequestImpressionId()
    {
        return requestImpressionId;
    }

    @JsonProperty("price")
    @Setter
    private float price;

    @JsonIgnore
    public float getPrice()
    {
        return price;
    }

    @JsonProperty("adid")
    @Setter
    private String adId;

    @JsonIgnore
    public String getAdId()
    {
        return getAdId();
    }

    @JsonProperty("nurl")
    @Setter
    private String winNotificationUrl;

    @JsonIgnore
    public String getWinNotificationUrl()
    {
        return winNotificationUrl;
    }

    /**
     * ad markup, xhtml if banner otherwise VAST if video.
     */
    @JsonProperty("adm")
    @Setter
    private String adMarkup;

    @JsonIgnore
    public String getAdMarkup()
    {
        return adMarkup;
    }

    @JsonProperty("adomain")
    @Setter
    private String[] advertiserDomains;

    @JsonIgnore
    public String[] getAdvertiserDomains()
    {
        return advertiserDomains;
    }

    @JsonProperty("iurl")
    @Setter
    private String sampleImageUrl;

    @JsonIgnore
    public String getSampleImageUrl()
    {
        return sampleImageUrl;
    }

    @JsonProperty("cid")
    @Setter
    private String campaignId;

    @JsonIgnore
    public String getCampaignId()
    {
        return campaignId;
    }

    @JsonProperty("crid")
    @Setter
    private String creativeId;

    @JsonIgnore
    public String getCreativeId()
    {
        return creativeId;
    }

    @JsonProperty("attr")
    @Setter
    private Integer[] creativeAttributes ;

    @JsonIgnore
    public Integer[] getCreativeAttributes()
    {
        return creativeAttributes;
    }

    /**
     * A unique	identifier	for the direct deal associated with the bid.
     * If the bid is associated	and in	response to	a dealid in
     * the request object	it	is	required.
     * */
    @JsonProperty("dealid")
    @Setter
    private String dealId;

    @JsonIgnore
    public String getDealId()
    {
        return dealId;
    }
}
