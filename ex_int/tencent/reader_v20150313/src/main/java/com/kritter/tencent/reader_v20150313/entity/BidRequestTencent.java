package com.kritter.tencent.reader_v20150313.entity;

import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.constants.OpenRTBVersion;
import lombok.Getter;

/**
 * This class represents the top level tencent bid request object to be used by bidder
 * in order to read incoming bid request.
 */

public class BidRequestTencent implements IBidRequest
{
    /**
     * Internal use properties, auctioneerId to recognize the exchange and
     * internal request id to map bidrequest internally in bidder.
     */
    private String auctioneerId;
    private String uniqueInternalBidRequestId;

    @Getter
    private TencentBidRequestParentNodeDTO tencentBidRequestParentNodeDTO;

    public BidRequestTencent(
                               String auctioneerId,
                               String uniqueInternalBidRequestId,
                               TencentBidRequestParentNodeDTO tencentBidRequestParentNodeDTO
                              )
    {
        this.auctioneerId = auctioneerId;
        this.uniqueInternalBidRequestId = uniqueInternalBidRequestId;
        this.tencentBidRequestParentNodeDTO = tencentBidRequestParentNodeDTO;
    }

    @Override
    public String getAuctioneerId()
    {
        return this.auctioneerId;
    }

    @Override
    public String getUniqueBidRequestIdentifierForAuctioneer()
    {
        return this.tencentBidRequestParentNodeDTO.getBidRequestId();
    }

    @Override
    public String getUniqueInternalRequestId()
    {
        return this.uniqueInternalBidRequestId;
    }

    @Override
    public Object getBidRequestParentNodeDTO()
    {
        return tencentBidRequestParentNodeDTO;
    }

    @Override
    public OpenRTBVersion getOpenRTBVersion()
    {
        return OpenRTBVersion.VERSION_2_3;
    }
}
