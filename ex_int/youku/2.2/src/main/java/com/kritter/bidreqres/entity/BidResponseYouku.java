package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.IBidResponse;

/**
 * This class implements bid response interface for youku..
 */
public class BidResponseYouku implements IBidResponse
{
    private String bidResponseIdentifierByExchange;
    private String bidResponseIdentifierByBidder;
    private String payload;

    public BidResponseYouku(
                                String bidResponseIdentifierByExchange,
                                String bidResponseIdentifierByBidder,
                                String payload
                             )
    {
        this.bidResponseIdentifierByExchange = bidResponseIdentifierByExchange;
        this.bidResponseIdentifierByBidder = bidResponseIdentifierByBidder;
        this.payload = payload;
    }

    @Override
    public String getUniqueBidResponseIdentifierForAuctioneer()
    {
        return bidResponseIdentifierByExchange;
    }

    @Override
    public String getUniqueBidResponseIdentifierByBidder()
    {
        return bidResponseIdentifierByBidder;
    }

    @Override
    public String getPayloadToBeServedToExchange()
    {
        return payload;
    }
}
