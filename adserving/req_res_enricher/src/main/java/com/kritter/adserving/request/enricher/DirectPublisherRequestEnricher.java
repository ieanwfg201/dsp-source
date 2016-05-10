package com.kritter.adserving.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.device.HandsetDetectionProvider;
import com.kritter.device.entity.HandsetMasterData;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.ConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.constants.StatusIdEnum;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private ConnectionTypeDetectionCache connectionTypeDetectionCache;

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
                                          String kritterCookieIdName
                                         )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
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
                                          ConnectionTypeDetectionCache connectionTypeDetectionCache,
                                          String kritterCookieIdName
                                         )
    {
        this.logger = LoggerFactory.getLogger(loggerName);
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

        this.logger.debug(" UserAgent from Request: {}, Remote address: {}, Site id : {}", userAgentFromRequest, remoteAddress, siteId);

        if(null==userAgentFromRequest || null==remoteAddress || null==siteId)
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
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
            logger.error("Number of ads requested has invalid value : " + requestedAds);
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
            this.logger.error("For requestId: " + requestId +
                              " Request is malformed inside DirectPublisherRequestEnricher");
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
                this.logger.error("Requesting site is not fit or is not found in cache . siteid: " + siteId );
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
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.AGGREGATOR_USER_ID, request.getInventorySource(),
                                                   requestingUserId));

            logger.debug("Cookie :{} received from end user and set as one of externalUserIds", requestingUserId);

            //set user agent to request object.
            request.setUserAgent(userAgent);

            HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);

            if(null == handsetMasterData)
            {
                this.logger.error("Device detection failed inside DirectPublisherRequestEnricher, " +
                                  "can not proceed further");
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
                return request;
            }
            if(handsetMasterData.isBot())
            {
                this.logger.error("Device detected is BOT inside DirectPublisherRequestEnricher, " +
                                  "can not proceed further");
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
                return request;
            }

            logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());

            request.setHandsetMasterData(handsetMasterData);
            //also set requested slot size.
            int[] requiredWidths = new int[1];
            int[] requiredHeights = new int[1];
            requiredWidths[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionWidth();
            requiredHeights[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionHeight();

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
                            " DirectPublisherRequestEnricher, id being: ",
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
                            " CountryCarrier detected successfully inside " +
                              "DirectPublisherRequestEnricher, id being: ",
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
