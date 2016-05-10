package com.kritter.entity.reqres.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * This class contains ad-exchange related info of the bid request
 * per impression.
 */
public class AdExchangeInfo
{
    /*This field means that banner size has to be exactly equal
     **to dimensions requested,stored per impression id of rtb exchange.*/
    @Getter @Setter
    private Boolean strictBannerSize;
    @Getter @Setter
    private boolean exchangeAllowsTextAd = false;
    @Getter @Setter
    private int[] widths;
    @Getter @Setter
    private int[] heights;
    @Getter @Setter
    private int[] interstitalMinimumWidths;
    @Getter @Setter
    private int[] interstitialMinimumheights;

    @Getter @Setter
    private boolean privateAuction;
    @Getter
    private Set<PrivateDealInfo> privateDealInfoSet;
    @Getter @Setter
    private Boolean requiresSecureAssets;

    public static class PrivateDealInfo
    {
        @Getter
        private String dealId;
        @Getter
        private Double bidFloor;
        @Getter
        private String bidFloorCurrency;
        @Getter
        private Integer auctionType;

        public PrivateDealInfo(String dealId,Double bidFloor,String bidFloorCurrency,Integer auctionType)
        {
            this.dealId = dealId;
            this.bidFloor = bidFloor;
            this.bidFloorCurrency = bidFloorCurrency;
            this.auctionType = auctionType;
        }
    }
    
    public void addPrivateDealInfo(PrivateDealInfo privateDealInfo)
    {
        if(null == privateDealInfoSet)
            privateDealInfoSet = new HashSet<PrivateDealInfo>();

        privateDealInfoSet.add(privateDealInfo);
    }
}
