package com.kritter.utils.common.dsp;

/**
 * This interface defines signature of methods required for enhancing bid requests before
 * sending them to a DSP for bidding.
 */
public interface BidRequestModifier<R>
{
    public R modifyBidRequest(R originalOpenRTBBidRequest);
}
