package com.kritter.postimpression.urlreader.impl;

import com.kritter.constants.NoFraudPostImpEvents;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * This class reads nofrdp events received from an exchanges like tencent etc
 * which gives all notification from already uploaded url and pass in macro param.
 */
public class NOFraudParamUrlReader implements PostImpressionEventUrlReader
{
    private Logger logger;
    private String name;
    private PostImpressionUtils postImpressionUtils;
    private String nfrdp_type;
    private String nfrdp_impid;
    private String nfrdp_price;
    private String nfrdp_ext;
    private String nfrdp_ext2;
    private String nfrdp_key_ver;

    public NOFraudParamUrlReader(
                                      String loggerName,
                                      String name,
                                      PostImpressionUtils postImpressionUtils,
                                      String nfrdp_type,
                                      String nfrdp_impid,
                                      String nfrdp_price,
                                      String nfrdp_ext,
                                      String nfrdp_ext2,
                                      String nfrdp_key_ver
                                     )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.name = name;
        this.postImpressionUtils = postImpressionUtils;
        this.nfrdp_type = nfrdp_type;
        this.nfrdp_impid = nfrdp_impid;
        this.nfrdp_price = nfrdp_price;
        this.nfrdp_ext = nfrdp_ext;
        this.nfrdp_ext2 = nfrdp_ext2;
        this.nfrdp_key_ver = nfrdp_key_ver;
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
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl of NOFraudParamUrlReader");
        if(this.nfrdp_ext == null)
            throw new Exception("nfrdp_ext inside decipherPostImpressionUrl of NOFraudParamUrlReader");

        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        String nfrdpTypefromParams = httpServletRequest.getParameter(this.nfrdp_type);
        String requesUrifromParams = httpServletRequest.getParameter(this.nfrdp_ext);
        
        if(requesUrifromParams== null)
            throw new Exception("requesUrifromParams inside decipherPostImpressionUrl of NOFraudParamUrlReader");
        if(nfrdpTypefromParams== null)
            throw new Exception("nfrdpTypefromParams inside decipherPostImpressionUrl of NOFraudParamUrlReader");
        Base64 base64 = new Base64(0);
        
        String requesUrifromParamsDecoded = new String(base64.decode(requesUrifromParams.getBytes()));
        
        this.postImpressionUtils.populatePostImpressionRequestObjectForNoFRDP(postImpressionRequest,requesUrifromParamsDecoded, false);
        String impidFromParams = httpServletRequest.getParameter(this.nfrdp_impid);
        String pricefromParams = httpServletRequest.getParameter(this.nfrdp_price);
        String ext2FromParams = httpServletRequest.getParameter(this.nfrdp_ext2);
        String keyVerFromParams = httpServletRequest.getParameter(this.nfrdp_key_ver);
        if(nfrdpTypefromParams.equals(NoFraudPostImpEvents.win.getName())){
        	postImpressionRequest.setNfrdpType(NoFraudPostImpEvents.win);
            double winBidPriceValue = 0.0;
            if(pricefromParams == null){
            	throw new Exception("pricefromParams null inside decipherPostImpressionUrl of NOFraudParamUrlReader");
            }
            try
            {
                winBidPriceValue = Double.valueOf(pricefromParams);
                postImpressionRequest.setWinBidPriceFromExchange(winBidPriceValue);
            }
            catch (NumberFormatException nfe)
            {
                logger.error("pricefromParams not double decipherPostImpressionUr NOFraudParamUrlReader ",nfe);
                throw new Exception("pricefromParams not double decipherPostImpressionUrl of NOFraudParamUrlReader");
            }


            logger.debug("Setting Winbidprice:{}",winBidPriceValue);

        }else if(nfrdpTypefromParams.equals(NoFraudPostImpEvents.clk.getName())){
        	postImpressionRequest.setNfrdpType(NoFraudPostImpEvents.clk);
        }else if(nfrdpTypefromParams.equals(NoFraudPostImpEvents.csc.getName())){
        	postImpressionRequest.setNfrdpType(NoFraudPostImpEvents.csc);
        }else{
        	throw new Exception("nfrdpTypefromParams does not match inside decipherPostImpressionUrl of NOFraudParamUrlReader");
        }
        
        

    }


}
