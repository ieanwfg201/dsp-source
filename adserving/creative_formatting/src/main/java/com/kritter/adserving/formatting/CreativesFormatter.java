package com.kritter.adserving.formatting;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;

/**
 * This interface defines how a set of ads using their creatives can be formatted.
 */

public interface CreativesFormatter
{
    public static final String CLICK_URL_MACRO       = "$CLICK_URL";
    public static final String CREATIVE_IMAGE_URL    = "$CREATIVE_IMAGE";
    public static final String CREATIVE_TEXT         = "$CREATIVE_TEXT";
    public static final String CREATIVE_CSC_BEACON   = "$CREATIVE_CSC_BEACON";
    public static final String CREATIVE_ALT_TEXT     = "$CREATIVE_ALT_TEXT";
    public static final String CREATIVE_TYPE         = "$CREATIVE_TYPE";
    public static final String CREATIVE_IMAGE_WIDTH  = "$CREATIVE_WIDTH";
    public static final String CREATIVE_IMAGE_HEIGHT = "$CREATIVE_HEIGHT";
    public static final String RICHMEDIA_PAYLOAD     = "$RICHMEDIA_PAYLOAD";
    public static final String WIN_NOTIFICATION_URL  = "$WIN_NOTIFICATION_URL";

    public String formatCreatives(Request request,Response response) throws Exception;

}
