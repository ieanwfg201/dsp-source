package com.kritter.postimpression.urlreader.impl;

import javax.servlet.http.HttpServletRequest;

import com.kritter.constants.TEvent;
import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;

/**
 * This class reads tracking events received.
 */
public class TrackingEventUrlReader implements PostImpressionEventUrlReader
{

    private String name;
    private PostImpressionUtils postImpressionUtils;

    public TrackingEventUrlReader(String name,PostImpressionUtils postImpressionUtils)
    {
        this.name = name;
        this.postImpressionUtils = postImpressionUtils;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void decipherPostImpressionUrl(Request postImpressionRequest,
                                          String requestURI,Context context) throws Exception
    {

        if(null == postImpressionRequest || null== requestURI)
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl " +
                                "of TrackingEventUrlReader");

        this.postImpressionUtils.populatePostImpressionRequestObject(postImpressionRequest,requestURI);
        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);
        String tevent = httpServletRequest.getParameter(TEvent.tevent);
        String ttype = httpServletRequest.getParameter(TEvent.ttype);
        postImpressionRequest.setTevent(tevent);
        postImpressionRequest.setTtype(ttype);
    }
}
