package com.kritter.bidreqres.entity.mopub2_3;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.constants.OpenRTBVersion;
import lombok.Getter;

/**
 * This class represents the top level mopub bid request object to be used by bidder
 * in order to read incoming bid request.
 */

public class BidRequestMopub implements IBidRequest
{
    /**
     * Internal use properties, auctioneerId to recognize the exchange and
     * internal request id to map bidrequest internally in bidder.
     */
    private String auctioneerId;
    private String uniqueInternalBidRequestId;

    @Getter
    private MopubBidRequestParentNodeDTO mopubBidRequestParentNodeDTO;

    public BidRequestMopub(
                               String auctioneerId,
                               String uniqueInternalBidRequestId,
                               MopubBidRequestParentNodeDTO mopubBidRequestParentNodeDTO
                              )
    {
        this.auctioneerId = auctioneerId;
        this.uniqueInternalBidRequestId = uniqueInternalBidRequestId;
        this.mopubBidRequestParentNodeDTO = mopubBidRequestParentNodeDTO;
    }

    @Override
    public String getAuctioneerId()
    {
        return this.auctioneerId;
    }

    @Override
    public String getUniqueBidRequestIdentifierForAuctioneer()
    {
        return this.mopubBidRequestParentNodeDTO.getBidRequestId();
    }

    @Override
    public String getUniqueInternalRequestId()
    {
        return this.uniqueInternalBidRequestId;
    }

    @Override
    public Object getBidRequestParentNodeDTO()
    {
        return mopubBidRequestParentNodeDTO;
    }

    @Override
    public OpenRTBVersion getOpenRTBVersion()
    {
        return OpenRTBVersion.VERSION_2_3;
    }
}
