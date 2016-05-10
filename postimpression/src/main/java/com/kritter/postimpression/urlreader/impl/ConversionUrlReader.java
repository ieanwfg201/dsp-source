package com.kritter.postimpression.urlreader.impl;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;

import javax.servlet.http.HttpServletRequest;

/**
 * This class reads a conversion URL and extracts out all the information
 * required for associating this conversion to the appropriate click.
 */
public class ConversionUrlReader implements PostImpressionEventUrlReader
{

    private String name;
    private String conversionInfoParameterName;

    public ConversionUrlReader(String name,String conversionInfoParameterName)
    {
        this.name = name;
        this.conversionInfoParameterName = conversionInfoParameterName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void decipherPostImpressionUrl(Request postImpressionRequest,
                                          String requestURI,
                                          Context context) throws Exception
    {

        if(null == postImpressionRequest || null== requestURI)
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl " +
                                "of ConversionUrlReader");

        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        String conversionInfoValue = httpServletRequest.getParameter(this.conversionInfoParameterName);
        if(null == conversionInfoValue)
            throw new Exception("ConversionInfoValue is null inside ConversionUrlReader");

        postImpressionRequest.setConversionDataFromConversionEvent(conversionInfoValue);
    }
}
