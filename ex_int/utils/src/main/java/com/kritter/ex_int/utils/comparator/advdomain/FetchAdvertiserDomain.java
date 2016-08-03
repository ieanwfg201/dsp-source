package com.kritter.ex_int.utils.comparator.advdomain;

import java.net.MalformedURLException;
import java.net.URL;

public class FetchAdvertiserDomain {
    public static String fetchAdvertiserDomain(String landingUrl)
    {
        if(null == landingUrl)
            return null;

        URL url = null;

        try
        {
            url = new URL(landingUrl);
        }
        catch (MalformedURLException use)
        {
            return null;
        }

        return url.getHost();
    }
}
