package com.kritter.bidrequest.entity.common.openrtbversion2_3;

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
        return adId;
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

    /**
     * Bundle or package name (e.g., com.foo.mygame) of the app being advertised, 
     * if applicable; intended to be a unique ID across exchanges.
     */
    @JsonProperty("bundle")
    @Setter
    private String appBundle;

    @JsonIgnore
    public String getAppBundle()
    {
        return appBundle;
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

    @JsonProperty("cat")
    @Setter
    private String[] creativeCategories;

    @JsonIgnore
    public String[] getCreativeCategories()
    {
        return creativeCategories;
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
     * Reference to the deal.id from the bid request 
     * if this bid pertains to a private marketplace direct deal.
     */
    @JsonProperty("dealid")
    @Setter
    private String dealId ;

    @JsonIgnore
    public String getDealId()
    {
        return dealId;
    }

    /**
     * Height of creative in pixels
     */
    @JsonProperty("h")
    @Setter
    private Integer creativeHeight ;

    @JsonIgnore
    public Integer getCreativeHeight()
    {
        return creativeHeight;
    }

    /**
     * Width of creative in pixels
     */
    @JsonProperty("w")
    @Setter
    private Integer creativeWidth ;

    @JsonIgnore
    public Integer getCreativeWidth()
    {
        return creativeWidth;
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
