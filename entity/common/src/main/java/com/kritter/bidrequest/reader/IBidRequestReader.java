package com.kritter.bidrequest.reader;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.exception.BidRequestException;

/**
 * This interface has methods that a bid request reader class should implement,
 * in order to read and validate incoming bid request from any exchange.
 */

public interface IBidRequestReader {

    /**
     * This method would convert bid request payload to corresponding business
     * object.
     *
     * @param bidRequestPayload
     * @return
     * @throws BidRequestException
     */
	public IBidRequest convertBidRequestPayloadToBusinessObject(
			String bidRequestPayload) throws BidRequestException;

    /**
     * This method would validate bid request for mandatory parameters to be
     * present in the payload, as well as would validate their semantics and
     * syntax.
     *
     * @param bidRequest
     * @throws BidRequestException
     */
	public void validateBidRequestForMandatoryParameters(IBidRequest bidRequest)
			throws BidRequestException;

}
