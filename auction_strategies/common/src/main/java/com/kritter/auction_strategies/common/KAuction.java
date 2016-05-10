package com.kritter.auction_strategies.common;

import java.util.Map;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.winner.WinEntity;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;

public interface KAuction {
    WinEntity getWinner(Map<String, BidResponseEntity> bidderResponses, Request request) throws Exception;
}
