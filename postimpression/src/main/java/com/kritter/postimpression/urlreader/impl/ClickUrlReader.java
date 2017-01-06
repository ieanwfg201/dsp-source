package com.kritter.postimpression.urlreader.impl;

import com.kritter.core.workflow.Context;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;

/**
 * This class reads and processes click url formed by the adserving application.
 *
 */
public class ClickUrlReader implements PostImpressionEventUrlReader
{

    private String name;
    private PostImpressionUtils postImpressionUtils;

    public ClickUrlReader(String name,PostImpressionUtils postImpressionUtils)
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
                                "of ClickUrlReader");

        this.postImpressionUtils.populatePostImpressionRequestObject(postImpressionRequest,requestURI);
    }
}
