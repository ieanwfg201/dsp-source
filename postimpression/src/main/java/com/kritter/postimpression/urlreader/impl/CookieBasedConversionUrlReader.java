package com.kritter.postimpression.urlreader.impl;

import com.kritter.core.workflow.Context;
import com.kritter.core.workflow.Workflow;
import com.kritter.entity.postimpression.entity.Request;
import com.kritter.postimpression.urlreader.PostImpressionEventUrlReader;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * This class receives cookie based conversion url event.
 * The javascript is provided to advertisers to be put on their thank you page
 * and the event therefore received is considered as the conversion for that
 * ad,requesting site,handset etc, by virtue of cookie content which is
 * essentially conversion information.
 */
public class CookieBasedConversionUrlReader implements PostImpressionEventUrlReader
{

    private String name;
    private String cookieKeyForConversionData;

    public CookieBasedConversionUrlReader(String name,String cookieKeyForConversionData)
    {
        this.name = name;
        this.cookieKeyForConversionData = cookieKeyForConversionData;
    }


    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void decipherPostImpressionUrl(Request postImpressionRequest,String requestURI,Context context) throws Exception
    {

        if(null == postImpressionRequest || null== requestURI)
            throw new Exception("The supplied request object is null inside decipherPostImpressionUrl " +
                                "of CookieBasedConversionUrlReader");

        HttpServletRequest httpServletRequest = (HttpServletRequest)context.getValue(Workflow.CONTEXT_REQUEST_KEY);

        String cookieValueConversionData = fetchCookieValueConversionDataFromEndUser(httpServletRequest,cookieKeyForConversionData);

        if(null == cookieValueConversionData)
            throw new Exception("CookieValueConversionData is null inside CookieBasedConversionUrlReader");

        postImpressionRequest.setConversionDataFromCookieBasedConversionEvent(cookieValueConversionData);
    }

    private String fetchCookieValueConversionDataFromEndUser(HttpServletRequest httpServletRequest,String cookieKeyName)
    {
        Cookie[] endUserCookies = httpServletRequest.getCookies();

        if(null != endUserCookies)
        {
            for(Cookie cookie : endUserCookies)
            {
                if(cookie.getName().equalsIgnoreCase(cookieKeyName))
                {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
