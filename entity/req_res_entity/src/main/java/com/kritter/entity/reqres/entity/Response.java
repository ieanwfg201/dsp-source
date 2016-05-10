package com.kritter.entity.reqres.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to capture any information related to response of the adserving
 * workflow, this may include adids,bids,bidder-bids,advertiserbids,budgets etc.
 * All this information will be used in formatting of the ad units and for logging
 * purposes.
 */
public class Response
{
    @Getter @Setter
    private Set<ResponseAdInfo> responseAdInfo;
    @Getter @Setter
    private Map<String,ResponseAdInfo> responseAdInfoAgainstAdGuidMap;
    @Getter @Setter
    private Short bidderModelId;
    @Getter @Setter
    private Short selectedSiteCategoryId;
    @Getter @Setter
    private Set<String> shortlistedAdIdSet;

    private Map<String,Set<ResponseAdInfo>> rtbExchangeResponseAdInfoSetAgainstImpressionIdMap;

    private Map<String,ResponseAdInfo> rtbExchangeResponseAdInfoSelected;

    public void addResponseAdInfoAsFinalForImpressionId(String impressionId,ResponseAdInfo responseAdInfo)
    {
        if(null == rtbExchangeResponseAdInfoSelected)
            rtbExchangeResponseAdInfoSelected = new HashMap<String, ResponseAdInfo>();

        rtbExchangeResponseAdInfoSelected.put(impressionId,responseAdInfo);
    }

    public ResponseAdInfo getFinalResponseAdInfoForExchangeImpressionId(String impressionId)
    {
        if(null == rtbExchangeResponseAdInfoSelected)
            return null;

        return rtbExchangeResponseAdInfoSelected.get(impressionId);
    }

    public void addResponseAdInfoAgainstBidRequestImpressionId(String bidRequestImpressionId,
                                                               ResponseAdInfo responseAdInfo) throws Exception
    {
        if(null == bidRequestImpressionId || null == responseAdInfo)
            throw new Exception("BidRequestImpressionId or ResponseAdInfo passed is null !!!");

        if(null == rtbExchangeResponseAdInfoSetAgainstImpressionIdMap)
            rtbExchangeResponseAdInfoSetAgainstImpressionIdMap = new HashMap<String, Set<ResponseAdInfo>>();

        Set<ResponseAdInfo> responseAdInfoAlreadyPresent =
                                rtbExchangeResponseAdInfoSetAgainstImpressionIdMap.get(bidRequestImpressionId);

        if(null == responseAdInfoAlreadyPresent)
            responseAdInfoAlreadyPresent = new HashSet<ResponseAdInfo>();

        responseAdInfoAlreadyPresent.add(responseAdInfo);

        rtbExchangeResponseAdInfoSetAgainstImpressionIdMap.put(bidRequestImpressionId,responseAdInfoAlreadyPresent);
    }

    public Set<ResponseAdInfo> getResponseAdInfoSetForBidRequestImpressionId(String bidRequestImpressionId)
    {
        if(null == bidRequestImpressionId)
            return null;

        return rtbExchangeResponseAdInfoSetAgainstImpressionIdMap.get(bidRequestImpressionId);
    }

    public Set<String> fetchRTBExchangeImpressionIdToRespondFor()
    {
        if(null == rtbExchangeResponseAdInfoSetAgainstImpressionIdMap ||
           rtbExchangeResponseAdInfoSetAgainstImpressionIdMap.size() <= 0)
            return null;

        return rtbExchangeResponseAdInfoSetAgainstImpressionIdMap.keySet();
    }

    public void removeResponseAdInfoFromCollection(ResponseAdInfo responseAdInfoEntity)
    {
        if(null != this.responseAdInfo && this.responseAdInfo.size() > 0)
            this.responseAdInfo.remove(responseAdInfoEntity);
    }

    public void addToResponseAdInfoCollection(ResponseAdInfo responseAdInfoEntity)
    {
        if(null == this.responseAdInfo)
            this.responseAdInfo = new HashSet<ResponseAdInfo>();

        this.responseAdInfo.add(responseAdInfoEntity);
    }
}
