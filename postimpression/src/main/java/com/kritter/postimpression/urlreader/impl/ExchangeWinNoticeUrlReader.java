package com.kritter.postimpression.urlreader.impl;

import com.kritter.abstraction.cache.utils.exceptions.UnSupportedOperationException;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.common.site.entity.SiteIncIdSecondaryKey;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;
import com.kritter.utils.common.AdExchangeUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * This class reads win notification url events received from an ad-exchange.
 */
public class ExchangeWinNoticeUrlReader implements PostImpressionEventUrlReader
{
    private Logger logger;
    private String name;
    private PostImpressionUtils postImpressionUtils;
    private String bidPriceParameterName;
    private String winBidPriceParameterName;
    private String auctionIdParameterName;
    private String auctionBidIdParameterName;
    private String auctionImpressionIdParameterName;
    private Map<String,AdExchangeUtils> adExchangeUtilsMap;
    private SiteCache siteCache;

    public ExchangeWinNoticeUrlReader(
                                      String loggerName,
                                      String name,
                                      PostImpressionUtils postImpressionUtils,
                                      String bidPriceParameterName,
                                      String winBidPriceParameterName,
                                      String auctionIdParameterName,
                                      String auctionBidIdParameterName,
                                      String auctionImpressionIdParameterName
                                     )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.postImpressionUtils = postImpressionUtils;
        this.bidPriceParameterName = bidPriceParameterName;
        this.winBidPriceParameterName = winBidPriceParameterName;
        this.auctionIdParameterName = auctionIdParameterName;
        this.auctionBidIdParameterName = auctionBidIdParameterName;
        this.auctionImpressionIdParameterName = auctionImpressionIdParameterName;
    }

    public ExchangeWinNoticeUrlReader(
                                      String loggerName,
                                      String name,
                                      PostImpressionUtils postImpressionUtils,
                                      String bidPriceParameterName,
                                      String winBidPriceParameterName,
                                      String auctionIdParameterName,
                                      String auctionBidIdParameterName,
                                      String auctionImpressionIdParameterName,
                                      Map<String,AdExchangeUtils> adExchangeUtilsMap,
                                      SiteCache siteCache
                                     )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.name = name;
        this.postImpressionUtils = postImpressionUtils;
        this.bidPriceParameterName = bidPriceParameterName;
        this.winBidPriceParameterName = winBidPriceParameterName;
        this.auctionIdParameterName = auctionIdParameterName;
        this.auctionBidIdParameterName = auctionBidIdParameterName;
        this.auctionImpressionIdParameterName = auctionImpressionIdParameterName;
        this.adExchangeUtilsMap = adExchangeUtilsMap;
        this.siteCache = siteCache;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void decipherPostImpressionUrl(Request postImpressionRequest,
                                          String requestURI,Context context) throws Exception
    {

        if(null == postImpressionRequest || null== requestURI)
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl " +
                                "of ExchangeWinNoticeUrlReader");

        this.postImpressionUtils.populatePostImpressionRequestObject(postImpressionRequest,requestURI);

        /*Fetch AdExchangeUtils instance which might be required for reading different parameters.*/
        Site site = fetchSiteFromRequestParameters(postImpressionRequest);
        AdExchangeUtils adExchangeUtils = null;

        if(null != site)
        {
            adExchangeUtils = adExchangeUtilsMap.get(site.getPublisherId());
            logger.debug("Site : {}, publisher id : {}.", site.getId(), site.getPublisherId());
            if(adExchangeUtils == null) {
                logger.debug("No ad exchange util found.");
            } else {
                logger.debug("Ad exchange util found.");
            }
        }

        //read extra request parameters and set into request object.
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

        String bidPrice = httpServletRequest.getParameter(this.bidPriceParameterName);
        String winBidPrice = httpServletRequest.getParameter(this.winBidPriceParameterName);
        String auctionId = httpServletRequest.getParameter(this.auctionIdParameterName);
        String auctionBid = httpServletRequest.getParameter(this.auctionBidIdParameterName);
        String auctionImpressionId = httpServletRequest.getParameter(this.auctionImpressionIdParameterName);

        double bidPriceValue = 0.0;
        double winBidPriceValue = 0.0;

        try
        {
            bidPriceValue = Double.valueOf(bidPrice);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("BidPrice value not correct inside ExchangeWinNoticeUrlReader ",nfe);
        }

        /**In case the parameter d is present in the request url, the win bid price value has to be
         * decrypted using */
        try
        {
            logger.debug("Going to decrypt if required the win bid price from : {} ", winBidPrice);

            if(null != adExchangeUtils)
                winBidPriceValue = adExchangeUtils.decodeWinBidPrice(winBidPrice);
            else
                winBidPriceValue = Double.valueOf(winBidPrice);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("WinBidPrice value not correct inside ExchangeWinNoticeUrlReader ",nfe);
        }

        logger.debug("Setting Winbidprice:{}, bidpriceToExchange:{}, auctionId:{}, auctionbidid:{}, auctionimpid:{}",winBidPriceValue,bidPriceValue, auctionId,auctionBid,auctionImpressionId);

        postImpressionRequest.setWinBidPriceFromExchange(winBidPriceValue);
        postImpressionRequest.setBidPriceToExchange(bidPriceValue);
        postImpressionRequest.setAuctionId(auctionId);
        postImpressionRequest.setAuctionBidId(auctionBid);
        postImpressionRequest.setAuctionImpressionId(auctionImpressionId);
    }

    private Site fetchSiteFromRequestParameters(Request request)
    {
        if(null == siteCache)
            return null;

        Site site = null;
        try
        {
            Set<String> siteGuidSet = siteCache.query(new SiteIncIdSecondaryKey(request.getSiteId()));

            for(String siteGuid : siteGuidSet)
            {
                site = siteCache.query(siteGuid);
            }
        }
        catch (UnSupportedOperationException unsope)
        {
            return null;
        }

        return site;
    }
}
