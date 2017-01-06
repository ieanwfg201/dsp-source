package com.kritter.adserving.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.StatusIdEnum;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.IConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used to enrich aggregator requests who are integrated via
 * the server to server api calls.
 */
public class AggregatorRequestEnricher implements RequestEnricher
{
    private Logger logger;
    private String userAgentParameterName;
    private String operaMiniUserAgentSubString;
    private String[] operaMiniUAHeaderNames;
    private String xffHeaderName;
    private List<String> privateAddressPrefixList;

    private String siteIdParameterName;
    private String ipAddressParameterName;
    private String invocationCodeVersionParameterName;
    private String responseFormatParameterName;
    private String numberOfAdsParameterName;
    private String siteAppWapParameterName;
    private String uidParameterName;
    private String latitudeParameterName;
    private String longitudeParameterName;
    private String widthParameterName;
    private String heightParameterName;
    private String exactBannerSizeParameterName = "eb";
    private String richMediaParameterName;
    private String bidFloorParameterName;
    private SiteCache siteCache;

    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private IConnectionTypeDetectionCache connectionTypeDetectionCache;

    public AggregatorRequestEnricher(
                                     String loggerName,
                                     String userAgentParameterName,
                                     String operaMiniUserAgentSubString,
                                     String[] operaMiniUAHeaderNames,
                                     String ipAddressParameterName,
                                     String xffHeaderName,
                                     List<String> privateAddressPrefixList,
                                     String siteIdParameterName,
                                     String invocationCodeVersionParameterName,
                                     String responseFormatParameterName,
                                     String numberOfAdsParameterName,
                                     String siteAppWapParameterName,
                                     String uidParameterName,
                                     String latitudeParameterName,
                                     String longitudeParameterName,
                                     SiteCache siteCache,
                                     HandsetDetectionProvider handsetDetectionProvider,
                                     CountryDetectionCache countryDetectionCache,
                                     ISPDetectionCache ispDetectionCache,
                                     String richMediaParameterName,
                                     String widthParameterName,
                                     String heightParameterName,
                                     String bidFloorParameterName
                                     )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.userAgentParameterName = userAgentParameterName;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
        this.ipAddressParameterName = ipAddressParameterName;
        this.xffHeaderName = xffHeaderName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.siteIdParameterName = siteIdParameterName;
        this.invocationCodeVersionParameterName = invocationCodeVersionParameterName;
        this.responseFormatParameterName = responseFormatParameterName;
        this.numberOfAdsParameterName = numberOfAdsParameterName;
        this.siteAppWapParameterName = siteAppWapParameterName;
        this.uidParameterName = uidParameterName;
        this.latitudeParameterName = latitudeParameterName;
        this.longitudeParameterName = longitudeParameterName;
        this.richMediaParameterName = richMediaParameterName;
        this.widthParameterName = widthParameterName;
        this.heightParameterName = heightParameterName;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = null;
        this.bidFloorParameterName = bidFloorParameterName;
    }

    public AggregatorRequestEnricher(
                                     String loggerName,
                                     String userAgentParameterName,
                                     String operaMiniUserAgentSubString,
                                     String[] operaMiniUAHeaderNames,
                                     String ipAddressParameterName,
                                     String xffHeaderName,
                                     List<String> privateAddressPrefixList,
                                     String siteIdParameterName,
                                     String invocationCodeVersionParameterName,
                                     String responseFormatParameterName,
                                     String numberOfAdsParameterName,
                                     String siteAppWapParameterName,
                                     String uidParameterName,
                                     String latitudeParameterName,
                                     String longitudeParameterName,
                                     SiteCache siteCache,
                                     HandsetDetectionProvider handsetDetectionProvider,
                                     CountryDetectionCache countryDetectionCache,
                                     ISPDetectionCache ispDetectionCache,
                                     IConnectionTypeDetectionCache connectionTypeDetectionCache,
                                     String richMediaParameterName,
                                     String widthParameterName,
                                     String heightParameterName,
                                     String bidFloorParameterName
                                    )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.userAgentParameterName = userAgentParameterName;
        this.operaMiniUserAgentSubString = operaMiniUserAgentSubString;
        this.operaMiniUAHeaderNames = operaMiniUAHeaderNames;
        this.ipAddressParameterName = ipAddressParameterName;
        this.xffHeaderName = xffHeaderName;
        this.privateAddressPrefixList = privateAddressPrefixList;
        this.siteIdParameterName = siteIdParameterName;
        this.invocationCodeVersionParameterName = invocationCodeVersionParameterName;
        this.responseFormatParameterName = responseFormatParameterName;
        this.numberOfAdsParameterName = numberOfAdsParameterName;
        this.siteAppWapParameterName = siteAppWapParameterName;
        this.uidParameterName = uidParameterName;
        this.latitudeParameterName = latitudeParameterName;
        this.longitudeParameterName = longitudeParameterName;
        this.richMediaParameterName = richMediaParameterName;
        this.widthParameterName = widthParameterName;
        this.heightParameterName = heightParameterName;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
        this.bidFloorParameterName = bidFloorParameterName;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception
    {
        this.logger.info("Inside validateAndEnrichRequest() of AggregatorRequestEnricher");

        Request request = new Request(requestId, INVENTORY_SOURCE.AGGREGATOR);

        String xffHeader = httpServletRequest.getHeader(this.xffHeaderName);
        String siteId = httpServletRequest.getParameter(this.siteIdParameterName);
        String userAgentFromRequest = httpServletRequest.getParameter(this.userAgentParameterName);
        String ipAddressFromRequest = httpServletRequest.getParameter(this.ipAddressParameterName);

        this.logger.debug(" UserAgent from Request: {}, Request IP address : {}, Site id : {}", userAgentFromRequest, ipAddressFromRequest, siteId );

        this.logger.debug("XFF header will not be used inside AggregatorRequestEnricher as ip in the request matters and XFF would be wrong to use.");

        if(null==userAgentFromRequest || null==ipAddressFromRequest || null==siteId)
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            return request;
        }


        String ip = ApplicationGeneralUtils.findCorrectIpAddressForDetection(
                                                                             ipAddressFromRequest,
                                                                             null,
                                                                             privateAddressPrefixList
                                                                            );

        String userAgent = findCorrectUserAgent(httpServletRequest);



        String invocationCodeVersion = httpServletRequest.getParameter(this.invocationCodeVersionParameterName);

        // response format is for only server to server calls,
        // would be missing in js invocation code.
        String responseFormat = httpServletRequest.getParameter(this.responseFormatParameterName);
        String requestedAds   = httpServletRequest.getParameter(this.numberOfAdsParameterName);
        String appWapForSiteCode = httpServletRequest.getParameter(this.siteAppWapParameterName);
        String requestingUserId = httpServletRequest.getParameter(this.uidParameterName);
        String requestingLatitude = httpServletRequest.getParameter(this.latitudeParameterName);
        String requestingLongitude = httpServletRequest.getParameter(this.longitudeParameterName);
        String richMediaParameterNameValue = httpServletRequest.getParameter(this.richMediaParameterName);
        String width = httpServletRequest.getParameter(this.widthParameterName);
        String height = httpServletRequest.getParameter(this.heightParameterName);
        String exactBanner = null;
        if(null != this.exactBannerSizeParameterName){
        	exactBanner = httpServletRequest.getParameter(this.exactBannerSizeParameterName);
        }
        String bidFloorValue = httpServletRequest.getParameter(this.bidFloorParameterName);

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
            logger.error("Inside AggregatorRequestEnricher Number of ads : {} or richMedia parameter :{} have invalid value : " ,
                    requestedAds,richMediaParameterNameValue);
        }

        try
        {
            if(null!=appWapForSiteCode)
                appWapForSiteCodeValue = Short.valueOf(appWapForSiteCode);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("appWapForSiteCode requested has invalid value : {}" , appWapForSiteCode);
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

        Double bidFloor = null;
        try
        {
            if(null != bidFloorValue)
                bidFloor = Double.valueOf(bidFloorValue);
        }
        catch (NumberFormatException nfe)
        {
            logger.error("BidFloor value has incorrect value: {} ",bidFloorValue);
        }

        ApplicationGeneralUtils.logDebug
                (this.logger, " AggregatorRequestEnricher, requestId: ", requestId, "," +
                              " user-agent: ", userAgent, ", Final IPAddress : ", ip,
                              " , xffHeader: ", xffHeader, ", siteId:", siteId, ", ip:", ip,
                              " , invocation_code_version:", invocationCodeVersion,
                              ",response_format:",responseFormat +
                              ",requested ads: " + requestedAds +
                              ",appWapForSiteCode: " + appWapForSiteCode +
                              ",requestingUserId: " + requestingUserId +
                              ",requestingLatitude: " + requestingLatitude +
                              ",requestingLongitude: " + requestingLongitude +
                              ",requestingWidth: " + width +
                              ",requestingHeight: " + height +
                              ",richMediaParameterValue: " + richMediaParameterValue +
                              ",bidFloor: " + bidFloor);

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
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(requestingUserId != null) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.AGGREGATOR_USER_ID, request.getInventorySource(),
                    requestingUserId));
        }

        request.setRequestingLongitudeValue(requestingLongitudeValue);
        request.setRequestingLatitudeValue(requestingLatitudeValue);
        request.setRichMediaParameterValue(richMediaParameterValue);
        request.setBidFloorValueForNetworkSupply(bidFloor);
        /*other extra parameters end.*/

        if( null==ip || null==invocationCodeVersion )
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            this.logger.error("For requestId: {} Request is malformed inside AggregatorRequestEnricher",requestId );
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
                this.logger.error("Requesting site is not fit or is not found in cache . siteid: {}" , siteId );
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
                this.logger.error("Device detection failed inside AggregatorRequestEnricher, cannot proceed further");
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
                return request;
            }
            if(handsetMasterData.isBot())
            {
                this.logger.error("Device detected is BOT inside AggregatorRequestEnricher, cannot proceed further");
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
                if(null!= width && null!=height)
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
                logger.debug("Inside AggregatorRequestEnricher Requesting width,height has invalid value : width:{} height:{} " ,
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
                            " Country detected successfully inside AggregatorRequestEnricher, id being: ",
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
                            " CountryCarrier detected successfully inside AggregatorRequestEnricher, id being: ",
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
            logger.error("Exception inside AggregatorRequestEnricher in fetching country " , e);
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
            logger.error("Exception inside AggregatorRequestEnricher in fetching isp ",e);
        }

        return internetServiceProvider;
    }
}
