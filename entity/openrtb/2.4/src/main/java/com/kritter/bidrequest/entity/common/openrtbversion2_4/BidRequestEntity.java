package com.kritter.bidrequest.entity.common.openrtbversion2_4;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.constants.OpenRTBVersion;
import lombok.Getter;

@Getter
public class BidRequestEntity implements IBidRequest {
    /**
     * Internal use properties, auctioneerId to recognize the exchange and
     * internal request id to map bidrequest internally in bidder.
     */
    private String auctioneerId;
    private String uniqueInternalBidRequestId;
    private BidRequestParentNodeDTO bidRequestParentNodeDTO;

    public BidRequestEntity(String auctioneerId,
                            String uniqueInternalBidRequestId,
                            BidRequestParentNodeDTO bidRequestParentNodeDTO) {
        this.auctioneerId = auctioneerId;
        this.uniqueInternalBidRequestId = uniqueInternalBidRequestId;
        this.bidRequestParentNodeDTO = bidRequestParentNodeDTO;
    }

    @Override
    public String getUniqueBidRequestIdentifierForAuctioneer() {
        return this.bidRequestParentNodeDTO.getId();
    }

    @Override
    public String getUniqueInternalRequestId() {
        return uniqueInternalBidRequestId;
    }

    @Override
    public OpenRTBVersion getOpenRTBVersion() {
        return OpenRTBVersion.VERSION_2_3;
    }
}
