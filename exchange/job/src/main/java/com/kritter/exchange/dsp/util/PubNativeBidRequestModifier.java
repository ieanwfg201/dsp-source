package com.kritter.exchange.dsp.util;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.fanoutinfra.apiclient.common.KHttpClient;

import java.net.URLEncoder;

/**
 * This class modifies Pubnative's bid request URL or payload or any other data to be transformed
 * before a request is sent to Pubnative to fetch demand.
 */
public class PubNativeBidRequestModifier implements BidRequestModifier<BidRequestParentNodeDTO>
{
    public static final String MACRO_APP_TOKEN = ":YOURAPPTOKEN:";
    private static final String MACRO_OS = ":os:";
    private static final String MACRO_OS_VER = ":osver:";
    private static final String MACRO_AD_LAYOUT = ":al:";
    private static final String MACRO_DEVICE_MODEL= ":devicemodel:";
    private static final String MACRO_DNT = ":dnt:";
    private static final String MACRO_UA = ":ua:";
    private static final String MACRO_IP = ":ip:";
    private static final String MACRO_ZONE = ":zoneid:";
    private static final String MACRO_MF = ":mf:";

    @Override
    public BidRequestParentNodeDTO modifyBidRequest(BidRequestParentNodeDTO originalOpenRTBBidRequest)
    {
        return originalOpenRTBBidRequest;
    }

    @Override
    public String modifyRequestURL(String originalRequestURL, Request request, BidRequestParentNodeDTO originalOpenRTBBidRequest)
    {
        if(null == originalRequestURL)
            return null;

        String os = request.getHandsetMasterData().getOsName();
        String osVer = request.getHandsetMasterData().getDeviceOperatingSystemVersion();
        String modelName = request.getHandsetMasterData().getModelName();
        if(null == os)
            os = "";
        if(null == osVer)
            osVer = "";
        if(null == modelName)
            modelName = "";

        try
        {
            originalRequestURL = originalRequestURL.replace(MACRO_OS, URLEncoder.encode(os, "UTF-8"));
            originalRequestURL = originalRequestURL.replace(MACRO_OS_VER, URLEncoder.encode(osVer,"UTF-8"));
            originalRequestURL = originalRequestURL.replace(MACRO_AD_LAYOUT, "m");
            originalRequestURL = originalRequestURL.replace(MACRO_DEVICE_MODEL, URLEncoder.encode(modelName,"UTF-8"));
            originalRequestURL = originalRequestURL.replace(MACRO_DNT, "1");
            originalRequestURL = originalRequestURL.replace(MACRO_UA, URLEncoder.encode(request.getUserAgent(),"UTF-8"));
            originalRequestURL = originalRequestURL.replace(MACRO_IP, URLEncoder.encode(request.getIpAddressUsedForDetection(),"UTF-8"));
            originalRequestURL = originalRequestURL.replace(MACRO_ZONE, "1");
            originalRequestURL = originalRequestURL.replace(MACRO_MF,"revenuemodel,points");
        }
        catch (Exception e)
        {
        }

        return originalRequestURL.toString();
    }

    @Override
    public KHttpClient.REQUEST_METHOD getRequestMethod()
    {
        return KHttpClient.REQUEST_METHOD.GET_METHOD;
    }
}
