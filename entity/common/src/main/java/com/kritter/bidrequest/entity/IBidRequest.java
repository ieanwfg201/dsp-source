package com.kritter.bidrequest.entity;

/* This is master interface for any type of bid request object, bid request objects
 * may change depending upon the open rtb versions and could be different for different auctioneer.
 * This class has methods which are essentially required for all bid request objects to implement.
 * Two auctioneer may come up with the same bid request id possibly.To enforce uniqueness overall,
 * there are methods to get auctioneer(exchange) id and internal task id.
 *
 * CONCEPT: The combination of (auctioneer-id, internal-request-id and bid-request-id) is unique
 * in whole universe of a demand side platform.
 * Similarly The combination of (auctioneer-id,internal-request-id and impression-id) is unique
 * in whole universe of a demand side platform.
 * 
 * The common entities like site,handset,impression etc would be in common package, however if 
 * there is specific need for any exchange in terms of these entities then that is overriden
 * and put in specific package.
 */

import com.kritter.constants.OpenRTBVersion;

public interface IBidRequest
{
	/* This method returns the top level unique bid request id contained in bid request payload.
	 */
	public String getUniqueBidRequestIdentifierForAuctioneer();

	/* This returns auctioneer id. Has to be generated by DSP.*/
	public String getAuctioneerId();

	/* This returns unique internal request id generated by DSP.*/
	public String getUniqueInternalRequestId();

    /* This function returns open rtb parent node, its upto
    *  implementing class to see what version it uses.*/
    public Object getBidRequestParentNodeDTO();

    /*This function returns open rtb version for the bid request*/
    public OpenRTBVersion getOpenRTBVersion();

}
