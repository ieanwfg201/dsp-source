package com.kritter.bidreqres.entity;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.constants.OpenRTBVersion;
import lombok.Getter;

/**
 * This class represents the top level amobee bid request object to be used by bidder
 * in order to read incoming bid request.
 */

public class BidRequestYouku implements IBidRequest
{
    /**
     * Internal use properties, auctioneerId to recognize the exchange and
     * internal request id to map bidrequest internally in bidder.
     */
    private String auctioneerId;
    private String uniqueInternalBidRequestId;

    @Getter
    private YoukuBidRequestParentNodeDTO youkuBidRequestParentNodeDTO;

    public BidRequestYouku(
                            String auctioneerId,
                            String uniqueInternalBidRequestId,
                            YoukuBidRequestParentNodeDTO youkuBidRequestParentNodeDTO
                            )
    {
        this.auctioneerId = auctioneerId;
        this.uniqueInternalBidRequestId = uniqueInternalBidRequestId;
        this.youkuBidRequestParentNodeDTO = youkuBidRequestParentNodeDTO;
    }

    @Override
    public String getAuctioneerId()
    {
        return this.auctioneerId;
    }

    @Override
    public String getUniqueBidRequestIdentifierForAuctioneer()
    {
        return this.youkuBidRequestParentNodeDTO.getBidRequestId();
    }

    @Override
    public String getUniqueInternalRequestId()
    {
        return this.uniqueInternalBidRequestId;
    }

    @Override
    public Object getBidRequestParentNodeDTO()
    {
        return youkuBidRequestParentNodeDTO;
    }

    @Override
    public OpenRTBVersion getOpenRTBVersion()
    {
        return OpenRTBVersion.VERSION_2_2;
    }
}
