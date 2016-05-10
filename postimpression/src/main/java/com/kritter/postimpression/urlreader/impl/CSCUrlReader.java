package com.kritter.postimpression.urlreader.impl;

import com.kritter.core.workflow.Context;
import com.kritter.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;

import java.util.Map;

/**
 * This class reads csc url.
 */
public class CSCUrlReader implements PostImpressionEventUrlReader
{

    private String name;
    private PostImpressionUtils postImpressionUtils;

    public CSCUrlReader(String name,PostImpressionUtils postImpressionUtils)
    {
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
                    "of CSCUrlReader");

        this.postImpressionUtils.populatePostImpressionRequestObject(postImpressionRequest,requestURI);
    }
}