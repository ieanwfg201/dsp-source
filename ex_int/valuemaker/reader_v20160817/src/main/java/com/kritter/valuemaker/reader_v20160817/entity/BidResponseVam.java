package com.kritter.valuemaker.reader_v20160817.entity;

import com.kritter.bidrequest.entity.IBidResponse;


public class BidResponseVam implements IBidResponse
{
    private String bidResponseIdentifierByExchange;
    private String bidResponseIdentifierByBidder;
    private String payload;

    public BidResponseVam(
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
