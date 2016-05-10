package com.kritter.bidreqres.entity.mopub2_3;

import com.kritter.bidrequest.entity.IBidResponse;

/**
 * This class implements bid response interface for mopub.
 */
public class BidResponseMopub implements IBidResponse
{
    private String bidResponseIdentifierByExchange;
    private String bidResponseIdentifierByBidder;
    private String payload;

    public BidResponseMopub(
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
