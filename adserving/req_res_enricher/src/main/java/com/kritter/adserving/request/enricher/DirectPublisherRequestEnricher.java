package com.kritter.adserving.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.IConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.constants.StatusIdEnum;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.CookieUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import com.kritter.constants.INVENTORY_SOURCE;

/**
 * This class enriches the request object in case request is coming from a directly
 * integrated publisher via the javascript ad code integration.
 */

public class DirectPublisherRequestEnricher implements RequestEnricher
{
    private Logger logger;
    private String userAgentParameterName;
    private String operaMiniUserAgentSubString;
    private String[] operaMiniUAHeaderNames;
    private String remoteAddressHeaderName;
    private String xffHeaderName;
    private List<String> privateAddressPrefixList;

    private String siteIdParameterName;
    private String numberOfAdsParameterName;
    private String latitudeParameterName;
    private String longitudeParameterName;
    private String invocationCodeVersionParameterName;
    private String responseFormatParameterName;
    private SiteCache siteCache;
    private String kritterCookieIdName;

    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private IConnectionTypeDetectionCache connectionTypeDetectionCache;
    private String widthParameterName;
    private String heightParameterName;
    private String dspCookiePrefix;
    private String exactBannerSizeParameterName;

    public DirectPublisherRequestEnricher(
                                          String loggerName,
                                          String userAgentParameterName,
                                          String operaMiniUserAgentSubString,
                                          String[] operaMiniUAHeaderNames,
                                          String remoteAddressHeaderName,
                                          String xffHeaderName,
                                          List<String> privateAddressPrefixList,
                                          String siteIdParameterName,
                                          String numberOfAdsParameterName,
                                          String latitudeParameterName,
                                          String longitudeParameterName,
                                          String invocationCodeVersionParameterName,
                                          String responseFormatParameterName,
                                          SiteCache siteCache,
                                          HandsetDetectionProvider handsetDetectionProvider,
                                          CountryDetectionCache countryDetectionCache,
                                          ISPDetectionCache ispDetectionCache,
                                          String kritterCookieIdName,
                                          String dspCookiePrefix
                                         )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.userAgentParameterName = userAgentParameterName;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
        this.xffHeaderName = xffHeaderName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.siteIdParameterName = siteIdParameterName;
        this.numberOfAdsParameterName = numberOfAdsParameterName;
        this.latitudeParameterName = latitudeParameterName;
        this.longitudeParameterName = longitudeParameterName;
        this.invocationCodeVersionParameterName = invocationCodeVersionParameterName;
        this.responseFormatParameterName = responseFormatParameterName;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = null;
        this.kritterCookieIdName = kritterCookieIdName;
        this.dspCookiePrefix = dspCookiePrefix;
    }

    public DirectPublisherRequestEnricher(
                                          String loggerName,
                                          String userAgentParameterName,
                                          String operaMiniUserAgentSubString,
                                          String[] operaMiniUAHeaderNames,
                                          String remoteAddressHeaderName,
                                          String xffHeaderName,
                                          List<String> privateAddressPrefixList,
                                          String siteIdParameterName,
                                          String numberOfAdsParameterName,
                                          String latitudeParameterName,
                                          String longitudeParameterName,
                                          String invocationCodeVersionParameterName,
                                          String responseFormatParameterName,
                                          SiteCache siteCache,
                                          HandsetDetectionProvider handsetDetectionProvider,
                                          CountryDetectionCache countryDetectionCache,
                                          ISPDetectionCache ispDetectionCache,
                                          IConnectionTypeDetectionCache connectionTypeDetectionCache,
                                          String kritterCookieIdName,
                                          String dspCookiePrefix
                                         )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.userAgentParameterName = userAgentParameterName;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
        this.xffHeaderName = xffHeaderName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.siteIdParameterName = siteIdParameterName;
        this.numberOfAdsParameterName = numberOfAdsParameterName;
        this.latitudeParameterName = latitudeParameterName;
        this.longitudeParameterName = longitudeParameterName;
        this.invocationCodeVersionParameterName = invocationCodeVersionParameterName;
        this.responseFormatParameterName = responseFormatParameterName;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
        this.kritterCookieIdName = kritterCookieIdName;
        this.dspCookiePrefix = dspCookiePrefix;
    }

    public DirectPublisherRequestEnricher(
                                            String loggerName,
                                            String userAgentParameterName,
                                            String operaMiniUserAgentSubString,
                                            String[] operaMiniUAHeaderNames,
                                            String remoteAddressHeaderName,
                                            String xffHeaderName,
                                            List<String> privateAddressPrefixList,
                                            String siteIdParameterName,
                                            String numberOfAdsParameterName,
                                            String latitudeParameterName,
                                            String longitudeParameterName,
                                            String invocationCodeVersionParameterName,
                                            String responseFormatParameterName,
                                            SiteCache siteCache,
                                            HandsetDetectionProvider handsetDetectionProvider,
                                            CountryDetectionCache countryDetectionCache,
                                            ISPDetectionCache ispDetectionCache,
                                            IConnectionTypeDetectionCache connectionTypeDetectionCache,
                                            String kritterCookieIdName,
                                            String widthParameterName,
                                            String heightParameterName,
                                            String dspCookiePrefix,
                                            String exactBannerSizeParameterName
                                         )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.userAgentParameterName = userAgentParameterName;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
        this.remoteAddressHeaderName = remoteAddressHeaderName;
        this.xffHeaderName = xffHeaderName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.siteIdParameterName = siteIdParameterName;
        this.numberOfAdsParameterName = numberOfAdsParameterName;
        this.latitudeParameterName = latitudeParameterName;
        this.longitudeParameterName = longitudeParameterName;
        this.invocationCodeVersionParameterName = invocationCodeVersionParameterName;
        this.responseFormatParameterName = responseFormatParameterName;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
        this.kritterCookieIdName = kritterCookieIdName;
        this.widthParameterName = widthParameterName;
        this.heightParameterName = heightParameterName;
        this.dspCookiePrefix = dspCookiePrefix;
        this.exactBannerSizeParameterName = exactBannerSizeParameterName;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception
    {
        this.logger.info("Inside validateAndEnrichRequest() of DirectPublisherRequestEnricher");

        Request request = new Request(requestId, INVENTORY_SOURCE.DIRECT_PUBLISHER);

        String remoteAddress = httpServletRequest.getHeader(this.remoteAddressHeaderName);
        String xffHeader = httpServletRequest.getHeader(this.xffHeaderName);
        String siteId = httpServletRequest.getParameter(this.siteIdParameterName);
        String userAgentFromRequest = httpServletRequest.getParameter(this.userAgentParameterName);
        String width = null;
        if(null != this.widthParameterName)
            width = httpServletRequest.getParameter(this.widthParameterName);
        String height = null;
        if(null != this.heightParameterName)
            height = httpServletRequest.getParameter(this.heightParameterName);
        String exactBanner = null;
        if(null != this.exactBannerSizeParameterName){
        	exactBanner = httpServletRequest.getParameter(this.exactBannerSizeParameterName);
        }

        this.logger.debug(" UserAgent from Request: {}, Remote address: {}, Site id : {}", userAgentFromRequest, remoteAddress, siteId);

        if(null==userAgentFromRequest || null==remoteAddress || null==siteId)
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            logger.error("User Agent / remote address or site id is missing , terming request as malformed.");
            return request;
        }

        String ip = ApplicationGeneralUtils.findCorrectIpAddressForDetection(
                                                                             remoteAddress,
                                                                             xffHeader,
                                                                             privateAddressPrefixList
                                                                            );

        String userAgent = findCorrectUserAgent(httpServletRequest);



        String invocationCodeVersion = httpServletRequest.getParameter(this.invocationCodeVersionParameterName);
        String requestedAds   = httpServletRequest.getParameter(this.numberOfAdsParameterName);
        String requestingLatitude = httpServletRequest.getParameter(this.latitudeParameterName);
        String requestingLongitude = httpServletRequest.getParameter(this.longitudeParameterName);
        String requestingUserId = fetchAdvertisingCookieFromEndUser(httpServletRequest);
        Map<String, String> dspCookies = CookieUtils.fetchPrefixCookiesFromRequest(httpServletRequest, dspCookiePrefix,
                this.logger);

        int numberOfAds = 0;
        Double requestingLatitudeValue = null;
        Double requestingLongitudeValue = null;

        try
        {
            if(null!=requestedAds)
                numberOfAds = Integer.parseInt(requestedAds);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("Number of ads requested has invalid value : {}" , requestedAds);
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
            logger.error("requesting latitude or longitude has invalid value : lat:{} lon:{} " ,
                    requestingLatitude , requestingLongitude);
        }

        ApplicationGeneralUtils.logDebug
                (this.logger, " DirectPublisherRequestEnricher, requestId: ", requestId, "," +
                              " user-agent: ",userAgent, ", remoteAddress : ", remoteAddress,
                              " , xffHeader: ", xffHeader, ", siteId:",siteId,", Final IPAddress:",ip,
                              " , invocation_code_version:",invocationCodeVersion + " ,requestedAds:",
                              requestedAds," ,requestingLatitude: ", requestingLatitude,
                              " ,requestingLongitude: ", requestingLongitude );

        request.setInvocationCodeVersion(invocationCodeVersion);
        request.setRequestedNumberOfAds(numberOfAds);
        request.setRequestingLatitudeValue(requestingLatitudeValue);
        request.setRequestingLongitudeValue(requestingLongitudeValue);

        if( null==ip || null==invocationCodeVersion )
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            this.logger.error("For requestId: {} Request is malformed inside DirectPublisherRequestEnricher",requestId);
            return request;
        }
        else
        {
            //get the site object and decide to set error code or put site object.
            Site site = fetchSiteEntity(siteId);

            logger.debug("Site is null? {}", (null == site));

            if(null==site || !(site.getStatus() == StatusIdEnum.Active.getCode()))
            {
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
                this.logger.error("Requesting site is not fit or is not found in cache . siteid:{} " , siteId );
                return request;
            }

            logger.debug("Site status is : {}", site.getStatus());

            ApplicationGeneralUtils.logDebug(this.logger,"The site detection complete, internal site id is : ",
                                                         String.valueOf(site.getId()));
            request.setSite(site);

            Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
            if(externalUserIds == null) {
                externalUserIds = new HashSet<ExternalUserId>();
                request.setExternalUserIds(externalUserIds);
            }

            if(requestingUserId != null) {
                externalUserIds.add(new ExternalUserId(ExternalUserIdType.COOKIE_ID, request.getInventorySource(),
                        requestingUserId));

                logger.debug("Cookie :{} received from end user and set as one of externalUserIds", requestingUserId);
            } else {
                logger.debug("Cookie not received in the request. External user ids empty");
            }

            if(dspCookies != null) {
                Map<Integer, String> dspIdToUserIdMap = getDspIdUserIdMap(dspCookies);
                if(dspIdToUserIdMap != null) {
                    for(Map.Entry<Integer, String> entry : dspIdToUserIdMap.entrySet()) {
                        int dspId = entry.getKey();
                        String userId = entry.getValue();
                        logger.debug("Dsp id : {}, user id : {}.", dspId, userId);
                        ExternalUserId externalUserId = new ExternalUserId(ExternalUserIdType.DSPBUYERUID, dspId,
                                userId);
                        externalUserIds.add(externalUserId);
                    }
                } else {
                    logger.debug("Dsp id to user id map is null.");
                }
            }

            //set user agent to request object.
            request.setUserAgent(userAgent);

            HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);

            if(null == handsetMasterData)
            {
                this.logger.error("Device detection failed inside DirectPublisherRequestEnricher, cannot proceed further");
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
                return request;
            }
            if(handsetMasterData.isBot())
            {
                this.logger.error("Device detected is BOT inside DirectPublisherRequestEnricher, cannot proceed further");
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
                return request;
            }

            logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());

            request.setHandsetMasterData(handsetMasterData);

            //also set requested slot size.
            int[] requiredWidths = new int[1];
            int[] requiredHeights = new int[1];

            boolean dimensionsForBannerAvailableFromRequest = false;

            try
            {
                if(null!= width && null!=height && !width.isEmpty() && !height.isEmpty())
                {
                    requiredWidths[0] = Integer.valueOf(width);
                    requiredHeights[0] = Integer.valueOf(height);
                    if(exactBanner != null && !exactBanner.isEmpty() && exactBanner.equals("1")){
                    	request.setExactBannerSizeRequired(true);
                    }
                    dimensionsForBannerAvailableFromRequest = true;
                }
            }
            catch (NumberFormatException nfe)
            {
                logger.debug("Inside DirectPublisherRequestEnricher Requesting width,height has invalid value : width:{} height:{} " ,
                              width , height);
            }

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
                            " Country detected successfully inside DirectPublisherRequestEnricher, id being: ",
                            String.valueOf(country.getCountryInternalId()));
                }
                else
                {
                    request.setCountry(null);
                }

                //get country carrier id from country carrier file
                InternetServiceProvider internetServiceProvider = findCarrierEntity(ip);

                //check also if detected isp data source is same as country datasource.
                //if not then only country is detected and isp remains undetected.
                if(
                   null != internetServiceProvider &&
                   null != country                 &&
                   internetServiceProvider.getDataSourceName().equalsIgnoreCase(country.getDataSourceName())
                  )
                {
                    ApplicationGeneralUtils.logDebug(this.logger,
                            " CountryCarrier detected successfully inside DirectPublisherRequestEnricher, id being: ",
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

    private Map<Integer, String> getDspIdUserIdMap(Map<String, String> dspCookieId) {
        if(dspCookieId == null) {
            this.logger.debug("DSP cookie id map is null.");
            return null;
        }
        if(dspCookieId.isEmpty()) {
            this.logger.debug("DSP cookie id map is empty.");
            return null;
        }

        Map<Integer, String> dspIdToUserIdMap = new HashMap<Integer, String>();
        for(Map.Entry<String, String> entry : dspCookieId.entrySet()) {
            String dspIdWithPrefix = entry.getKey();
            String userId = entry.getValue();

            this.logger.debug("DSP id with prefix : {}, user id {}.", dspIdWithPrefix, userId);
            // The dspIdWithPrefix is formatted as <dsp cookie prefix><dsp inc id>.
            // If the dspCookiePrefix is "dspid_" and dsp inc id is 10, the dspIdWithPrefix would be : dspid_10
            // Remove the dspCookiePrefix (which is dspid_) to extract the dsp inc id
            int dspId = Integer.parseInt(dspIdWithPrefix.split(dspCookiePrefix)[1]);
            this.logger.debug("DSP inc id : {}.", dspId);

            dspIdToUserIdMap.put(dspId, userId);
        }

        return dspIdToUserIdMap;
    }

    private String findCorrectUserAgent(HttpServletRequest httpServletRequest)
    {
        String userAgent = httpServletRequest.getParameter(this.userAgentParameterName);
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
            logger.error("Exception inside DirectPublisherRequestEnricher in fetching country " , e);
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
            logger.error("Exception inside DirectPublisherRequestEnricher in fetching isp ",e);
        }

        return internetServiceProvider;
    }

    private String fetchAdvertisingCookieFromEndUser(HttpServletRequest httpServletRequest)
    {
        Cookie[] endUserCookies = httpServletRequest.getCookies();

        if(null != endUserCookies)
        {
            for(Cookie cookie : endUserCookies)
            {
                if(cookie.getName().equalsIgnoreCase(kritterCookieIdName))
                {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
