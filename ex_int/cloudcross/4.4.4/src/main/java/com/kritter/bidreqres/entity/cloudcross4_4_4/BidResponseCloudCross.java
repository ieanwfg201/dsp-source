package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.IBidResponse;

/**
 * This class implements bid response interface for CloudCross.
 */
public class BidResponseCloudCross implements IBidResponse {
    private String bidResponseIdentifierByExchange;
    private String bidResponseIdentifierByBidder;
    private String payload;

    public BidResponseCloudCross(
            String bidResponseIdentifierByExchange,
            String bidResponseIdentifierByBidder,
            String payload
    ) {
        this.bidResponseIdentifierByExchange = bidResponseIdentifierByExchange;
        this.bidResponseIdentifierByBidder = bidResponseIdentifierByBidder;
        this.payload = payload;
    }

    @Override
    public String getUniqueBidResponseIdentifierForAuctioneer() {
        return bidResponseIdentifierByExchange;
    }

    @Override
    public String getUniqueBidResponseIdentifierByBidder() {
        return bidResponseIdentifierByBidder;
    }

    @Override
    public String getPayloadToBeServedToExchange() {
        return payload;
    }
}
