package com.kritter.valuemaker.reader_v20160817.entity;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.constants.OpenRTBVersion;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the top level valuemaker bid request object to be used by bidder
 * in order to read incoming bid request.
 */

public class BidRequestVam implements IBidRequest
{
    /**
     * Internal use properties, auctioneerId to recognize the exchange and
     * internal request id to map bidrequest internally in bidder.
     */
    private String auctioneerId;
    private String uniqueInternalBidRequestId;

    @Getter @Setter
    private VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO;

    public BidRequestVam(
                               String auctioneerId,
                               String uniqueInternalBidRequestId,
                               VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO
                              )
    {
        this.auctioneerId = auctioneerId;
        this.uniqueInternalBidRequestId = uniqueInternalBidRequestId;
        this.vamBidRequestParentNodeDTO = vamBidRequestParentNodeDTO;
    }

    @Override
    public String getAuctioneerId()
    {
        return this.auctioneerId;
    }

    @Override
    public String getUniqueBidRequestIdentifierForAuctioneer()
    {
        return this.vamBidRequestParentNodeDTO.getBidRequestId();
    }

    @Override
    public String getUniqueInternalRequestId()
    {
        return this.uniqueInternalBidRequestId;
    }

    @Override
    public Object getBidRequestParentNodeDTO()
    {
        return vamBidRequestParentNodeDTO;
    }

    @Override
    public OpenRTBVersion getOpenRTBVersion()
    {
        return OpenRTBVersion.VERSION_2_3;
    }
}
