package com.kritter.auction_strategies.common;

import java.util.Map;

import com.kritter.entity.exchangethrift.CreateExchangeThrift;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.winner.WinEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;

/**
 * This interface defines methods used by implementations to define how a winner is selected.
 * Methods are available for all versions of open rtb, the implementation has to define for
 * all open rtb version methods present here.
 */
public interface KAuction
{
    public WinEntity getWinnerOpenRTB2_3(Map<String, BidResponseEntity> bidderResponses, Request request, CreateExchangeThrift cet) throws Exception;

    public WinEntity getWinnerOpenRTB2_2(Map<String, com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity> bidderResponses, Request request,CreateExchangeThrift cet) throws Exception;
}
