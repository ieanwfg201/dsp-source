package com.kritter.postimpression.urlreader.impl;

import com.kritter.constants.BEvent;
import com.kritter.constants.BEventType;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * This class reads billable events received from multi sources.
 */
public class BillableEventUrlReader implements PostImpressionEventUrlReader
{
    private Logger logger;
    private String name;
    private PostImpressionUtils postImpressionUtils;

    public BillableEventUrlReader(
                                      String loggerName,
                                      String name,
                                      PostImpressionUtils postImpressionUtils
                                     )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = name;
        this.postImpressionUtils = postImpressionUtils;
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
                                "of BillableEventUrlReader");

        this.postImpressionUtils.populatePostImpressionRequestObject(postImpressionRequest,requestURI);



        //read extra request parameters and set into request object.
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

        String billableeventtype = httpServletRequest.getParameter(BEvent.btype);
        if(billableeventtype != null){
            String billableeventtypeTrim = billableeventtype.trim();
            if(!"".equals(billableeventtype)){
                try {
                    int billableeventcode = Integer.parseInt(billableeventtypeTrim);
                    BEventType beventEnum = BEventType.getEnum(billableeventcode);
                    if(beventEnum != null){
                        double bidPrice = Double.parseDouble(httpServletRequest.getParameter(BEvent.bidprice));
                        double mbr = Double.parseDouble(httpServletRequest.getParameter(BEvent.multiplier_bid_price));
                        double winBidPriceValue = (mbr*bidPrice)/100;
                        logger.debug("Setting Winbidprice:{}",winBidPriceValue);
                        postImpressionRequest.setWinBidPriceFromExchange(winBidPriceValue);
                        postImpressionRequest.setBEventType(beventEnum);
                        postImpressionRequest.setMbr(mbr);
                        postImpressionRequest.setBidPriceToExchange(bidPrice);
                    }
                }catch (NumberFormatException nfe){
                    logger.error("WinPrice value not correct inside Billeable Event Url Reader",nfe);
                }
            }
        }
    }
}
