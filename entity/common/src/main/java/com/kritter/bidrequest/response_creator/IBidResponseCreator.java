package com.kritter.bidrequest.response_creator;

import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.exception.BidResponseException;

/**
 * This interface has methods that a bid request response creator class should implement,
 * in order to construct bid response and validate it before sending it out for end user.
 */
public interface IBidResponseCreator
{
    /**
     * This method prepares/constructs bid-response entity required for payload creation
     * for exchange.
     *
     * @return
     * @throws BidResponseException
     */
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException;

    /**
     * This method validates and checks the validity of bid response entity.
     *
     * @param bidResponseEntity
     * @throws BidResponseException
     */
    public void validateBidResponseEntity(IBidResponse bidResponseEntity) throws BidResponseException;
}
