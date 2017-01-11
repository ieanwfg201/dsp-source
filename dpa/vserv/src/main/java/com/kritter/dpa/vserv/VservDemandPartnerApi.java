package com.kritter.dpa.vserv;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.dpa.common.DemandPartnerApi;
import com.kritter.dpa.common.entity.DemandPartnerApiResponse;
import com.kritter.utils.http_client.SynchronousHttpClient;
import com.kritter.utils.http_client.entity.HttpRequest;
import com.kritter.utils.http_client.entity.HttpResponse;

import lombok.Setter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class connects to vserv as demand partner and gets
 * ads for the current request.
 */
public class VservDemandPartnerApi implements DemandPartnerApi
{
    private Logger logger;
    private boolean isTestMode;
    private String adSpotValue;
    private String serverURL;
    @Setter
    private int connectTimeOut;
    @Setter
    private int readTimeOut;
    private SynchronousHttpClient synchronousHttpClient;

    private static Map<String,String> headersMap;

    private static final String DEFAULT_SOURCE = "unknown";
    /*parameter/header names that need to be sent in the http
     * call to vserv to fetch ads.*/
    private static final String PARAM_NAME_AD_SPOT = "adspot";
    private static final String PARAM_NAME_USER_AGENT = "ua";
    private static final String PARAM_NAME_SOURCE = "source";
    private static final String PARAM_NAME_IP = "ip";
    private static final String PARAM_NAME_XFF = "ff";
    private static final String PARAM_NAME_HEADER_VIA = "hv";
    private static final String TEST_MODE_PARAM_NAME = "tm";
    private static final String TEST_MODE_PARAM_VALUE = "1";

    public VservDemandPartnerApi(
            String loggerName,
            boolean isTestMode,
            String adSpotValue,
            String serverURL,
            int connectTimeOut,
            int readTimeOut,
            SynchronousHttpClient synchronousHttpClient
            )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.isTestMode = isTestMode;
        this.adSpotValue = adSpotValue;
        this.serverURL = serverURL;
        this.connectTimeOut = connectTimeOut;
        this.readTimeOut = readTimeOut;
        this.synchronousHttpClient = synchronousHttpClient;

        headersMap = new HashMap<String, String>();
        headersMap.put("Content-Type", "application/x-www-form-urlencoded");
        headersMap.put("charset", "UTF-8");
    }

    @Override
    public DemandPartnerApiResponse fetchDemandPartnerApiResponse(Request request)
    {
        try{
            if(null == request.getSite())
            {
                logger.error("Site is null inside VservDemandPartnerApi returning null...");
                return null;
            }

            Map<String,Object> requestParametersMap = new HashMap<String, Object>();
            requestParametersMap.put(PARAM_NAME_AD_SPOT,adSpotValue);
            requestParametersMap.put(PARAM_NAME_USER_AGENT,request.getUserAgent());
            String source = DEFAULT_SOURCE;

            if(
                    request.getSite().getSitePlatform().shortValue() == SITE_PLATFORM.WAP.getPlatform() &&
                    null != request.getSite().getSiteUrl()
                    )
                source = request.getSite().getSiteUrl();
            else if(
                    request.getSite().getSitePlatform().shortValue() == SITE_PLATFORM.APP.getPlatform() &&
                    null != request.getSite().getApplicationId()
                    )
                source = request.getSite().getApplicationId();

            requestParametersMap.put(PARAM_NAME_SOURCE,source);
            requestParametersMap.put(PARAM_NAME_IP,request.getIpAddressUsedForDetection());
            requestParametersMap.put(PARAM_NAME_XFF,request.getIpAddressUsedForDetection());
            if(isTestMode)
                requestParametersMap.put(TEST_MODE_PARAM_NAME,TEST_MODE_PARAM_VALUE);

            //requestParametersMap.put(PARAM_NAME_HEADER_VIA,null);

            String postBody = null;

            postBody =  HttpRequest.preparePostBodyFromRequestParameters(requestParametersMap);

            HttpRequest httpRequest = new HttpRequest(
                    serverURL,connectTimeOut,readTimeOut,
                    HttpRequest.REQUEST_METHOD.POST_METHOD,
                    headersMap,
                    postBody
                    );

            HttpResponse httpResponse = synchronousHttpClient.fetchResponseFromThirdPartyServer(httpRequest);

            VserveResponseEntity vserveResponseEntity = new VserveResponseEntity(httpResponse.getResponsePayload(),logger);

            DemandPartnerApiResponse demandPartnerApiResponse =
                    new DemandPartnerApiResponse(
                            httpResponse.getResponseStatusCode(),
                            vserveResponseEntity.getAdMarkup(),
                            httpResponse.getResponseContentType(),
                            vserveResponseEntity.isNoFill()
                            );

            return demandPartnerApiResponse;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }
}
