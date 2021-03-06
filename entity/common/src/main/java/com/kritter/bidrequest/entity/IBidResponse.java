package com.kritter.bidrequest.entity;

/**
 * This interface dictates methods that should be implemented by any
 * bid response class.
 */
public interface IBidResponse
{
    /**
     * This must be the top­level ID of the bid request that this is a response for.
     */
    public String getUniqueBidResponseIdentifierForAuctioneer();

    /**
     * This method returns bid response id as generated by bidder for tracking purposes.
     */
    public String getUniqueBidResponseIdentifierByBidder();

    /**
     * This method returns the final payload to be served to exchange.
     */
    public String getPayloadToBeServedToExchange();
}
