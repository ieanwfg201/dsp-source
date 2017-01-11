package com.kritter.bidreqres.entity.inmobi2_3;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.constants.OpenRTBVersion;
import lombok.Getter;

/**
 * This class represents the top level inmobi 2.3 bid request object to be used by bidder
 * in order to read incoming bid request.
 */

public class BidRequestInmobi implements IBidRequest
{
    /**
     * Internal use properties, auctioneerId to recognize the exchange and
     * internal request id to map bidrequest internally in bidder.
     */
    private String auctioneerId;
    private String uniqueInternalBidRequestId;

    @Getter
    private InmobiBidRequestParentNodeDTO inmobiBidRequestParentNodeDTO;

    public BidRequestInmobi(
                               String auctioneerId,
                               String uniqueInternalBidRequestId,
                               InmobiBidRequestParentNodeDTO inmobiBidRequestParentNodeDTO
                              )
    {
        this.auctioneerId = auctioneerId;
        this.uniqueInternalBidRequestId = uniqueInternalBidRequestId;
        this.inmobiBidRequestParentNodeDTO = inmobiBidRequestParentNodeDTO;
    }

    @Override
    public String getAuctioneerId()
    {
        return this.auctioneerId;
    }

    @Override
    public String getUniqueBidRequestIdentifierForAuctioneer()
    {
        return this.inmobiBidRequestParentNodeDTO.getBidRequestId();
    }

    @Override
    public String getUniqueInternalRequestId()
    {
        return this.uniqueInternalBidRequestId;
    }

    @Override
    public Object getBidRequestParentNodeDTO()
    {
        return inmobiBidRequestParentNodeDTO;
    }

    @Override
    public OpenRTBVersion getOpenRTBVersion()
    {
        return OpenRTBVersion.VERSION_2_3;
    }
}
