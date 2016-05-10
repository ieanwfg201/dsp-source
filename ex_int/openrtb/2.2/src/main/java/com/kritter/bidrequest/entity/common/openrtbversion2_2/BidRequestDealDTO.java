package com.kritter.bidrequest.entity.common.openrtbversion2_2;

import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This object constitutes a specific deal that was struck a priori between a buyer and a seller. Its presence
 with the Pmp collection indicates that this impression is available under the terms of that deal. Refer to
 Section 7.2 for more details.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BidRequestDealDTO {

    /*A unique identifier for the direct deal.*/
    @JsonProperty("id")
    @Setter
    private String dealId;

    @JsonIgnore
    public String getDealId(){
        return dealId;
    }

    /*Minimum bid for this impression expressed in CPM.*/
    @JsonProperty("bidfloor")
    @Setter
    private Float bidFloor;

    @JsonIgnore
    public Float getBidFloor(){
        return bidFloor;
    }

    /*Currency specified using ISO-4217 alpha codes. This may be
      different from bid currency returned by bidder if this is
      allowed by the exchange.*/
    @JsonProperty("bidfloorcur")
    @Setter
    private String bidFloorCurrency;

    @JsonIgnore
    public String getBidFloorCurrency(){
        return bidFloorCurrency;
    }

    /*Optional override of the overall auction type of the bid
      request, where 1 = First Price, 2 = Second Price Plus, 3 = the
      value passed in bidfloor is the agreed upon deal price.
      Additional auction types can be defined by the exchange.
    */
    @JsonProperty("at")
    @Setter
    private Integer auctionType;

    @JsonIgnore
    public Integer getAuctionType(){
        return auctionType;
    }

    /**
     * Whitelist of buyer seats allowed to bid on this deal. Seat IDs
     must be communicated between bidders and the exchange a
     priori. Omission implies no seat restrictions.
     */
    @JsonProperty("wseat")
    @Setter
    private String[] whitelistedBuyerSeats;

    @JsonIgnore
    public String[] getWhitelistedBuyerSeats(){
        return whitelistedBuyerSeats;
    }

    /**
     * Placeholder for exchange-specific extensions to OpenRTB.
     */
    @JsonProperty("ext")
    @Setter
    private Object extensionObject;

    @JsonIgnore
    public Object getExtensionObject(){
        return extensionObject;
    }
}
