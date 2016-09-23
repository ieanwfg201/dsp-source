package com.kritter.utils.common.dsp;

import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestParentNodeDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This class modifies bid request so that it can be consumed by mediamath.
 */
public class MediaMathBidRequestModifier implements BidRequestModifier<BidRequestParentNodeDTO>
{
    /*Mediamath's provided SSP id for us as per the client.*/
    private String sspId;

    /*If the client wants to include only certain advertiser seats  (non-pmp) then
    * they can be included here.*/
    private String[] wseatIds;
    private boolean isTestMode;

    private static final String[] WSEAT_TEST = {"101338"};

    public MediaMathBidRequestModifier(String sspId,String[] wseatIds,boolean isTestMode)
    {
        this.sspId = sspId;
        this.wseatIds = wseatIds;
        this.isTestMode = isTestMode;
    }

    @Override
    public BidRequestParentNodeDTO modifyBidRequest(BidRequestParentNodeDTO originalOpenRTBBidRequest)
    {
        /*Not supported: bcat, badv, tmax, allimps, regs*/
        originalOpenRTBBidRequest.setBlockedAdvertiserCategoriesForBidRequest(null);
        originalOpenRTBBidRequest.setBlockedAdvertiserDomainsForBidRequest(null);
        originalOpenRTBBidRequest.setMaxTimeoutForBidSubmission(null);
        originalOpenRTBBidRequest.setAllImpressions(null);
        originalOpenRTBBidRequest.setBidRequestRegsDTO(null);

        ParentBidExtensionObject extensionObject = new ParentBidExtensionObject();
        extensionObject.setSsp(this.sspId);
        originalOpenRTBBidRequest.setExtensionObject(extensionObject);

        if(this.isTestMode)
            originalOpenRTBBidRequest.setBidderSeatIds(WSEAT_TEST);
        else if(null != this.wseatIds && this.wseatIds.length > 0)
            originalOpenRTBBidRequest.setBidderSeatIds(this.wseatIds);

        return originalOpenRTBBidRequest;
    }

    private static class ParentBidExtensionObject
    {
        @Getter @Setter
        private String ssp;
    }
}
