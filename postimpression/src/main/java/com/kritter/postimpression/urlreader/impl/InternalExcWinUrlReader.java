package com.kritter.postimpression.urlreader.impl;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * This class reads win notification url events received from an ad-exchange.
 */
public class InternalExcWinUrlReader implements PostImpressionEventUrlReader
{
    private Logger logger;
    private String name;
    private PostImpressionUtils postImpressionUtils;
    private String winPriceParameterName;

    public InternalExcWinUrlReader(
                                      String loggerName,
                                      String name,
                                      PostImpressionUtils postImpressionUtils,
                                      String winPriceParameterName
                                     )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = name;
        this.postImpressionUtils = postImpressionUtils;
        this.winPriceParameterName = winPriceParameterName;
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
                                "of InternalExcWinUrlReader");

        this.postImpressionUtils.populatePostImpressionRequestObject(postImpressionRequest,requestURI);



        //read extra request parameters and set into request object.
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

        String winPrice = httpServletRequest.getParameter(this.winPriceParameterName);

        double winBidPriceValue = 0.0;

        try
        {
            winBidPriceValue = Double.valueOf(winPrice);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("WinPrice value not correct inside InternalExcWinUrlReader ",nfe);
        }


        logger.debug("Setting Winbidprice:{}",winBidPriceValue);

        postImpressionRequest.setWinBidPriceFromExchange(winBidPriceValue);
    }


}
