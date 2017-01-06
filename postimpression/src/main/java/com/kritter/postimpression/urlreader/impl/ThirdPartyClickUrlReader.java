package com.kritter.postimpression.urlreader.impl;

import com.kritter.core.workflow.Context;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import com.kritter.postimpression.utils.PostImpressionUtils;

/**
 * This class reads third party click url.
 */

public class ThirdPartyClickUrlReader implements PostImpressionEventUrlReader {

    private String name;
    private PostImpressionUtils postImpressionUtils;

    public ThirdPartyClickUrlReader(String name,PostImpressionUtils postImpressionUtils){

        this.name = name;
        this.postImpressionUtils = postImpressionUtils;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void decipherPostImpressionUrl(Request postImpressionRequest, String requestURI,
                                             Context context) throws Exception {

        if(null == postImpressionRequest || null== requestURI)
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl() of " +
                    "ThirdPartyClickURLReader.");

        String requestURIParts[] = requestURI.split(this.postImpressionUtils.getUriFieldsDelimiter());

        if(null == requestURIParts || requestURIParts.length != 3)
            throw new Exception("Inside decipherPostImpressionUrl() of ThirdPartyClickURLReader, URI is malformed");

        try{

            String aliasUrlId = requestURIParts[2].split(this.postImpressionUtils.getThirdPartyClickUrlSuffix())[0];
            postImpressionRequest.setAliasUrlId(aliasUrlId);

        }
        catch (RuntimeException e){
            throw new Exception("Error in aliasurl inside decipherPostImpressionUrl() of ThirdPartyClickURLReader", e);
        }
    }
}
