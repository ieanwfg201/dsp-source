package com.kritter.adserving.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.StatusIdEnum;
import com.kritter.device.HandsetDetectionProvider;
import com.kritter.device.entity.HandsetMasterData;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.ConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.utils.common.ApplicationGeneralUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * This enricher is similar to aggregator enricher however difference is that
 * it uses request parameters to utilize XFF header.
 */
public class AggregatorEnricherWithRequestParameters implements RequestEnricher
{
    private Logger logger;
    private String userAgentParameterName;
    private String operaMiniUserAgentSubString;
    private String[] operaMiniUAHeaderNames;
    private List<String> privateAddressPrefixList;

    private String siteIdParameterName;
    private String ipAddressParameterName;
    private String xffParameterName;
    private String invocationCodeVersionParameterName;
    private String responseFormatParameterName;
    private String numberOfAdsParameterName;
    private String siteAppWapParameterName;
    private String uidParameterName;
    private String latitudeParameterName;
    private String longitudeParameterName;
    private String richMediaParameterName;
    private String widthParameterName;
    private String heightParameterName;
    private SiteCache siteCache;

    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private ConnectionTypeDetectionCache connectionTypeDetectionCache;

    public AggregatorEnricherWithRequestParameters(
                                                    String loggerName,
                                                    String userAgentParameterName,
                                                    String operaMiniUserAgentSubString,
                                                    String[] operaMiniUAHeaderNames,
                                                    String ipAddressParameterName,
                                                    String xffParameterName,
                                                    List<String> privateAddressPrefixList,
                                                    String siteIdParameterName,
                                                    String invocationCodeVersionParameterName,
                                                    String responseFormatParameterName,
                                                    String numberOfAdsParameterName,
                                                    String siteAppWapParameterName,
                                                    String uidParameterName,
                                                    String latitudeParameterName,
                                                    String longitudeParameterName,
                                                    String widthParameterName,
                                                    String heightParameterName,
                                                    SiteCache siteCache,
                                                    HandsetDetectionProvider handsetDetectionProvider,
                                                    CountryDetectionCache countryDetectionCache,
                                                    ISPDetectionCache ispDetectionCache,
                                                    String richMediaParameterName
                                                  )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.userAgentParameterName = userAgentParameterName;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
        this.ipAddressParameterName = ipAddressParameterName;
        this.xffParameterName = xffParameterName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.siteIdParameterName = siteIdParameterName;
        this.invocationCodeVersionParameterName = invocationCodeVersionParameterName;
        this.responseFormatParameterName = responseFormatParameterName;
        this.numberOfAdsParameterName = numberOfAdsParameterName;
        this.siteAppWapParameterName = siteAppWapParameterName;
        this.uidParameterName = uidParameterName;
        this.latitudeParameterName = latitudeParameterName;
        this.longitudeParameterName = longitudeParameterName;
        this.widthParameterName = widthParameterName;
        this.heightParameterName = heightParameterName;
        this.richMediaParameterName = richMediaParameterName;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = null;
    }

    public AggregatorEnricherWithRequestParameters(
            String loggerName,
            String userAgentParameterName,
            String operaMiniUserAgentSubString,
            String[] operaMiniUAHeaderNames,
            String ipAddressParameterName,
            String xffParameterName,
            List<String> privateAddressPrefixList,
            String siteIdParameterName,
            String invocationCodeVersionParameterName,
            String responseFormatParameterName,
            String numberOfAdsParameterName,
            String siteAppWapParameterName,
            String uidParameterName,
            String latitudeParameterName,
            String longitudeParameterName,
            String widthParameterName,
            String heightParameterName,
            SiteCache siteCache,
            HandsetDetectionProvider handsetDetectionProvider,
            CountryDetectionCache countryDetectionCache,
            ISPDetectionCache ispDetectionCache,
            ConnectionTypeDetectionCache connectionTypeDetectionCache,
            String richMediaParameterName
    )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.userAgentParameterName = userAgentParameterName;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
        this.ipAddressParameterName = ipAddressParameterName;
        this.xffParameterName = xffParameterName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.siteIdParameterName = siteIdParameterName;
        this.invocationCodeVersionParameterName = invocationCodeVersionParameterName;
        this.responseFormatParameterName = responseFormatParameterName;
        this.numberOfAdsParameterName = numberOfAdsParameterName;
        this.siteAppWapParameterName = siteAppWapParameterName;
        this.uidParameterName = uidParameterName;
        this.latitudeParameterName = latitudeParameterName;
        this.longitudeParameterName = longitudeParameterName;
        this.widthParameterName = widthParameterName;
        this.heightParameterName = heightParameterName;
        this.richMediaParameterName = richMediaParameterName;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception
    {
        this.logger.info("Inside validateAndEnrichRequest() of AggregatorEnricherWithRequestParameters");

        Request request = new Request(requestId, INVENTORY_SOURCE.AGGREGATOR);

        String xffHeaderAsParameterValue = trimAndClean(httpServletRequest.getParameter(this.xffParameterName));
        String siteId = trimAndClean(httpServletRequest.getParameter(this.siteIdParameterName));
        String userAgentFromRequest = trimAndClean(httpServletRequest.getParameter(this.userAgentParameterName));
        String ipAddressFromRequest = trimAndClean(httpServletRequest.getParameter(this.ipAddressParameterName));

        this.logger.debug(" UserAgent from Request: {}, Request IP address : {}, Site id : {}", userAgentFromRequest, ipAddressFromRequest, siteId );

        
        if(userAgentFromRequest == null){
        	request.setRequestEnrichmentErrorCodeAndMessage(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED, "Parameter " + this.uidParameterName + " is not found or empty");
        	return request;
        }
        if(ipAddressFromRequest == null){
        	request.setRequestEnrichmentErrorCodeAndMessage(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED, "Parameter " + this.ipAddressParameterName + " is not found or empty");
        	return request;
        }
        if(siteId == null){
        	request.setRequestEnrichmentErrorCodeAndMessage(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED, "Parameter " + this.siteIdParameterName + " is not found or empty");
        	return request;
        }

        String ip = ApplicationGeneralUtils.findCorrectIpAddressForDetection(
                                                                             ipAddressFromRequest,
                                                                             xffHeaderAsParameterValue,
                                                                             privateAddressPrefixList
                                                                            );

        String userAgent = findCorrectUserAgent(httpServletRequest);



        String invocationCodeVersion = trimAndClean(httpServletRequest.getParameter(this.invocationCodeVersionParameterName));

        // response format is for only server to server calls,
        // would be missing in js invocation code.
        String responseFormat = trimAndClean(httpServletRequest.getParameter(this.responseFormatParameterName));
        String requestedAds   = trimAndClean(httpServletRequest.getParameter(this.numberOfAdsParameterName));
        String appWapForSiteCode = trimAndClean(httpServletRequest.getParameter(this.siteAppWapParameterName));
        String requestingUserId = trimAndClean(httpServletRequest.getParameter(this.uidParameterName));
        String requestingLatitude = trimAndClean(httpServletRequest.getParameter(this.latitudeParameterName));
        String requestingLongitude = trimAndClean(httpServletRequest.getParameter(this.longitudeParameterName));
        String width = trimAndClean(httpServletRequest.getParameter(this.widthParameterName));
        String height = trimAndClean(httpServletRequest.getParameter(this.heightParameterName));
        String richMediaParameterNameValue = httpServletRequest.getParameter(this.richMediaParameterName);

        int numberOfAds = 0;
        Integer richMediaParameterValue = null;
        short appWapForSiteCodeValue = 0;
        Double requestingLatitudeValue = null;
        Double requestingLongitudeValue = null;

        try
        {
            if(null!=requestedAds)
                numberOfAds = Integer.parseInt(requestedAds);
            if(null != richMediaParameterNameValue)
                richMediaParameterValue = Integer.parseInt(richMediaParameterNameValue);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("Number of ads:{} or richmedia parameter value :{} have invalid value : " ,
                         requestedAds,richMediaParameterNameValue);
        }

        try
        {
            if(null!=appWapForSiteCode)
                appWapForSiteCodeValue = Short.valueOf(appWapForSiteCode);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("appWapForSiteCode requested has invalid value : " + appWapForSiteCode);
        }

        try
        {
            if(null!=requestingLatitude && null!=requestingLongitude)
            {
                requestingLatitudeValue = Double.valueOf(requestingLatitude);
                requestingLongitudeValue = Double.valueOf(requestingLongitude);
            }
        }
        catch (NumberFormatException nfe)
        {
            logger.error("requesting latitude or longitude has invalid value : lat: " +
                    requestingLatitude + ", lon: " + requestingLongitude);
        }

        //also set requested slot size.
        int[] requiredWidths = new int[1];
        int[] requiredHeights = new int[1];
        boolean dimensionsForBannerAvailableFromRequest = false;

        try
        {
            if(null!= width && null!=height)
            {
                requiredWidths[0] = Integer.valueOf(width);
                requiredHeights[0] = Integer.valueOf(height);
                dimensionsForBannerAvailableFromRequest = true;
            }
        }
        catch (NumberFormatException nfe)
        {
            logger.error("requesting width or height has invalid value : width: " +
                         width + ", height: " + height);
        }

        ApplicationGeneralUtils.logDebug
                (this.logger, " AggregatorEnricherWithRequestParameters, requestId: ", requestId, "," +
                        " user-agent: ", userAgent, ", Final IPAddress : ", ip,
                        " , xffHeaderAsRequestParameter: ", xffHeaderAsParameterValue,
                        ", siteId:", siteId, ", ip:", ip,
                        " , invocation_code_version:", invocationCodeVersion,
                        ",response_format:",responseFormat +
                        ",requested ads: " + requestedAds +
                        ",appWapForSiteCode: " + appWapForSiteCode +
                        ",requestingUserId: " + requestingUserId +
                        ",requestingLatitude: " + requestingLatitude +
                        ",requestingLongitude: " + requestingLongitude +
                        ",requestingWidth: " + width +
                        ",requestingHeight: " + height +
                        ",richMediaParameterValue: " + richMediaParameterValue);

        request.setInvocationCodeVersion(invocationCodeVersion);
        request.setResponseFormat(responseFormat);

        /*other extra parameters being.*/
        request.setRequestedNumberOfAds(numberOfAds);
        short sitePlatformValue = SITE_PLATFORM.NO_VALID_VALUE.getPlatform();

        if(appWapForSiteCodeValue == SITE_PLATFORM.APP.getPlatform())
            sitePlatformValue = SITE_PLATFORM.APP.getPlatform();
        else if(appWapForSiteCodeValue == SITE_PLATFORM.APP.getPlatform())
            sitePlatformValue = SITE_PLATFORM.WAP.getPlatform();
        request.setSitePlatformValue(sitePlatformValue);
        request.setUserId(requestingUserId);
        request.setRequestingLongitudeValue(requestingLongitudeValue);
        request.setRequestingLatitudeValue(requestingLatitudeValue);
        request.setRichMediaParameterValue(richMediaParameterValue);
        /*other extra parameters end.*/

        if(ip == null){
        	request.setRequestEnrichmentErrorCodeAndMessage(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED, "IP address cannot be deturmined");
            this.logger.error("For requestId: " + requestId + " Request is malformed inside AggregatorEnricherWithRequestParameters");
        	return request;
        }
        if(invocationCodeVersion == null){
        	request.setRequestEnrichmentErrorCodeAndMessage(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED, "Parameter " + this.invocationCodeVersionParameterName + " is not found or empty");
            this.logger.error("For requestId: " + requestId + " Request is malformed inside AggregatorEnricherWithRequestParameters");
        	return request;
        }        

        {
            //get the site object and decide to set error code or put site object.
            Site site = fetchSiteEntity(siteId);

            logger.debug("Site is null? {}", (null == site));

            if(null==site || !(site.getStatus() == StatusIdEnum.Active.getCode()))
            {
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
                this.logger.error("Requesting site is not fit or is not found in cache . siteid: " + siteId );
               return request;
            }

            logger.debug("Site status is : {}", site.getStatus());

            ApplicationGeneralUtils.logDebug(this.logger,"The site detection complete, internal site id is : ",
                    String.valueOf(site.getId()));

            //set the site platform value if received from aggregator.
            if(!(sitePlatformValue == SITE_PLATFORM.NO_VALID_VALUE.getPlatform()))
                site.setSitePlatform(sitePlatformValue);

            request.setSite(site);

            //set user agent to request object.
            request.setUserAgent(userAgent);

            HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);

            if(null == handsetMasterData)
            {
                this.logger.error("Device detection failed inside AggregatorEnricherWithRequestParameters, " +
                        "can not proceed further");
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
                return request;
            }
            if(handsetMasterData.isBot())
            {
                this.logger.error("Device detected is BOT inside AggregatorEnricherWithRequestParameters, " +
                        "can not proceed further");
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
                return request;
            }

            logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());

            request.setHandsetMasterData(handsetMasterData);

            if(!dimensionsForBannerAvailableFromRequest)
            {
                requiredWidths[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionWidth();
                requiredHeights[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionHeight();
            }

            request.setRequestedSlotWidths(requiredWidths);
            request.setRequestedSlotHeights(requiredHeights);
            /***************Done setting handset attributes ******************/

            if(null != ip)
            {
                //set final ip address into request object, used for detection.
                request.setIpAddressUsedForDetection(ip);

                //get country id from country file.
                Country country =  findCountry(ip);
                if(null != country)
                {
                    request.setCountry(country);
                    ApplicationGeneralUtils.logDebug(this.logger,
                            " Country detected successfully inside " +
                                    " AggregatorEnricherWithRequestParameters, id being: ",
                            String.valueOf(country.getCountryInternalId()));
                }
                else
                {
                    request.setCountry(null);
                }

                //get country carrier id from country carrier file
                InternetServiceProvider internetServiceProvider = findCarrierEntity(ip);

                //check also if detected isp datasource is same as country datasource.
                //if not then only country is detected and isp remains undetected.
                if(
                        null != internetServiceProvider &&
                                null != country                 &&
                                internetServiceProvider.getDataSourceName().equalsIgnoreCase(country.getDataSourceName())
                        )
                {
                    ApplicationGeneralUtils.logDebug(this.logger,
                            " CountryCarrier detected successfully inside " +
                                    "AggregatorEnricherWithRequestParameters, id being: ",
                            String.valueOf(internetServiceProvider.getOperatorInternalId()));

                    request.setInternetServiceProvider(internetServiceProvider);
                }

                if(connectionTypeDetectionCache != null) {
                    // Get the connection type for this ip
                    request.setConnectionType(this.connectionTypeDetectionCache.getConnectionType(ip));
                } else {
                    // Default to unknown if the cache is not present
                    request.setConnectionType(ConnectionType.UNKNOWN);
                }
            }
        }

        return request;
    }

    private String findCorrectUserAgent(HttpServletRequest httpServletRequest)
    {
        String userAgent = trimAndClean(httpServletRequest.getParameter(this.userAgentParameterName));
        if(null == userAgent)
            return null;

        // if opera mini is the browser use different header
        // names in sequence to find the correct user agent.
        if(userAgent.toLowerCase().contains(operaMiniUserAgentSubString))
        {
            for(String operaMiniUAHeader : operaMiniUAHeaderNames)
            {
                userAgent = httpServletRequest.getHeader(operaMiniUAHeader);

                if(null != userAgent)
                    break;
            }
        }

        return userAgent;
    }

    private Site fetchSiteEntity(String siteId)
    {
        return this.siteCache.query(siteId);
    }

    private Country findCountry(String ip)
    {
        Country country = null;

        try
        {
            country = this.countryDetectionCache.findCountryForIpAddress(ip);
        }
        catch (Exception e)
        {
            logger.error("Exception inside AggregatorEnricherWithRequestParameters in fetching country " , e);
        }

        return country;
    }


    private InternetServiceProvider findCarrierEntity(String ip)
    {
        InternetServiceProvider internetServiceProvider = null;

        try
        {
            internetServiceProvider = this.ispDetectionCache.fetchISPForIpAddress(ip);
        }
        catch (Exception e)
        {
            logger.error("Exception inside AggregatorEnricherWithRequestParameters in fetching isp ",e);
        }

        return internetServiceProvider;
    }
    private static String trimAndClean(String input){
        input = StringUtils.trim(input);
        input = StringUtils.trimToNull(input);
        //input = StringUtils.removeStart(input, "[");
        //input = StringUtils.removeEnd(input, "]");
        return StringUtils.trim(input);
    }
}
