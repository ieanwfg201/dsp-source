package com.kritter.utils.common;

import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieUtils {
    public static String fetchCookieFromRequest(HttpServletRequest httpServletRequest, String cookieName,
                                                Logger logger) {
        logger.debug("Cookie name to extract : {}.", cookieName);
        Cookie[] cookies = httpServletRequest.getCookies();

        if(null == cookies) {
            logger.debug("No cookies found.");
            return null;
        }

        for(Cookie cookie : cookies) {
            logger.debug("Cookie name : {}, cookie value : {}", cookie.getName(), cookie.getValue());
            if (cookie.getName().equalsIgnoreCase(cookieName)) {
                logger.debug("Matched the cookie name.");
                return cookie.getValue();
            }
        }

        return null;
    }

    public static void setCookie(HttpServletResponse httpServletResponse,
                                 Logger logger,
                                 String cookieName,
                                 String cookieValue,
                                 int cookieExpireAgeInSeconds) {
        logger.debug("Setting value of cookie : {} as : {} ", cookieName, cookieValue);
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(cookieExpireAgeInSeconds);
        httpServletResponse.addCookie(cookie);
    }

    /**
     * Given an http servlet request, extracts the headers and finds all the cookies starting with the given prefix.
     * @param httpServletRequest The web request
     * @param prefix Prefix for which the cookie names should start.
     * @return Map containing matching cookie names with corresponding values
     */
    public static Map<String, String> fetchPrefixCookiesFromRequest(HttpServletRequest httpServletRequest,
                                                                    String prefix, Logger logger) {
        logger.debug("Prefix required : {}.", prefix);
        Cookie[] cookies = httpServletRequest.getCookies();

        if(cookies == null) {
            logger.debug("No cookies found in request");
            return null;
        }

        Map<String, String> cookieNameValueMap = new HashMap<String, String>();
        for(Cookie cookie : cookies) {
            logger.debug("Cookie name : {}, cookie value : {}.", cookie.getName(), cookie.getValue());
            if(cookie.getName().startsWith(prefix)) {
                logger.debug("Cookie name matches the prefix required.");
                cookieNameValueMap.put(cookie.getName(), cookie.getValue());
            }
        }

        return cookieNameValueMap;
    }
}
