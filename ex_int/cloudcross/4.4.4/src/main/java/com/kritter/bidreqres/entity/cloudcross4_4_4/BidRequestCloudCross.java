package com.kritter.bidreqres.entity.cloudcross4_4_4;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.constants.OpenRTBVersion;
import lombok.Getter;

/**
 * This class represents the top level CloudCross bid request object to be used by bidder
 * in order to read incoming bid request.
 */

public class BidRequestCloudCross implements IBidRequest {
    /**
     * Internal use properties, auctioneerId to recognize the exchange and
     * internal request id to map bidrequest internally in bidder.
     */
    private String auctioneerId;
    private String uniqueInternalBidRequestId;

    @Getter
    private CloudCrossBidRequestParentNodeDTO cloudCrossBidRequestParentNodeDTO;

    public BidRequestCloudCross(
            String auctioneerId,
            String uniqueInternalBidRequestId,
            CloudCrossBidRequestParentNodeDTO cloudCrossBidRequestParentNodeDTO
    ) {
        this.auctioneerId = auctioneerId;
        this.uniqueInternalBidRequestId = uniqueInternalBidRequestId;
        this.cloudCrossBidRequestParentNodeDTO = cloudCrossBidRequestParentNodeDTO;
    }

    @Override
    public String getAuctioneerId() {
        return this.auctioneerId;
    }

    @Override
    public String getUniqueBidRequestIdentifierForAuctioneer() {
        return this.cloudCrossBidRequestParentNodeDTO.getBidRequestId();
    }

    @Override
    public String getUniqueInternalRequestId() {
        return this.uniqueInternalBidRequestId;
    }

    @Override
    public Object getBidRequestParentNodeDTO() {
        return cloudCrossBidRequestParentNodeDTO;
    }

    @Override
    public OpenRTBVersion getOpenRTBVersion() {
        return OpenRTBVersion.VERSION_2_3;
    }
}
