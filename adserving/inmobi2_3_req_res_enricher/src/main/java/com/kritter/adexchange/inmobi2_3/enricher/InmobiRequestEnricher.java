package com.kritter.adexchange.inmobi2_3.enricher;

import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidreqres.entity.inmobi2_3.*;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.caches.iab.categories.entity.IABCategoryEntity;
import com.kritter.common.caches.slot_size_cache.CreativeSlotSizeCache;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.StatusIdEnum;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.CountryIspUiDataUsingMccMnc;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.IConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.geo.common.entity.reader.MncMccCountryISPDetectionCache;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class reads request from inmobi 2.3 rtb exchange.
 * The URL provided to inmobi 2.3 contains siteid as well:
 * e.g:
 * http://rtbexchange.dsp.com/exchange/b2409484c01754ef08c0e476b796fc081/a179484c01754ef08c0e476b796fc081
 * first id is publisher id and second is the siteid.
 */
public class InmobiRequestEnricher implements RTBExchangeRequestReader
{
    private Logger logger;
    private IBidRequestReader inmobiBidRequestReader;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private IABCategoriesCache iabCategoriesCache;
    private MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private IConnectionTypeDetectionCache connectionTypeDetectionCache;
    private static final String ENCODING = "UTF-8";
    private CreativeSlotSizeCache creativeSlotSizeCache;

    public InmobiRequestEnricher(String loggerName,
                                    IBidRequestReader inmobiBidRequestReader,
                                    SiteCache siteCache,
                                    HandsetDetectionProvider handsetDetectionProvider,
                                    IABCategoriesCache iabCategoriesCache,
                                    MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache,
                                    CountryDetectionCache countryDetectionCache,
                                    ISPDetectionCache ispDetectionCache,
                                    IConnectionTypeDetectionCache connectionTypeDetectionCache,
                                    CreativeSlotSizeCache creativeSlotSizeCache)
    {
        this.logger = LogManager.getLogger(loggerName);
        this.inmobiBidRequestReader = inmobiBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.iabCategoriesCache = iabCategoriesCache;
        this.mncMccCountryISPDetectionCache = mncMccCountryISPDetectionCache;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
        this.creativeSlotSizeCache = creativeSlotSizeCache;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger,
                                            Logger bidRequestLogger,
                                            boolean logBidRequest,
                                            String publisherId) throws Exception
    {
        StringWriter stringWriter = new StringWriter();

        logger.debug("Request Method inside validateAndEnrichRequest of InmobiRequestEnricher is {} ",
                     httpServletRequest.getMethod());

        String encodingToUse = httpServletRequest.getCharacterEncoding();
        if(null == encodingToUse)
            encodingToUse = ENCODING;

        logger.debug("ContentLength: {}, ContentType: {} , RequestCharset: {}, UsedCharset: {} ",
                     httpServletRequest.getContentLength(),
                     httpServletRequest.getContentType(),
                     httpServletRequest.getCharacterEncoding(),
                     encodingToUse);


        IOUtils.copy(httpServletRequest.getInputStream(), stringWriter, encodingToUse);

        String bidRequestPayLoadReceived = stringWriter.toString();

        if(StringUtils.isEmpty(bidRequestPayLoadReceived))
        {
            logger.error("The bidrequest payload received from Inmobi 2.3 RTB Exchange is null inside validateAndEnrichRequest of InmobiRequestEnricher,cannot enrich bid request...");
            return null;
        }

        if(logBidRequest)
        {
            StringBuffer sb = new StringBuffer();
            sb.append(publisherId);
            sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
            sb.append(requestId);
            sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
            sb.append(bidRequestPayLoadReceived);
            bidRequestLogger.debug(sb.toString());
        }

        IBidRequest bidRequest = null;

        try
        {
            bidRequest = inmobiBidRequestReader.convertBidRequestPayloadToBusinessObject(bidRequestPayLoadReceived);
        }
        catch (Exception e)
        {
            logger.error("Exception inside InmobiRequestEnricher ",e);
            logger.error("Exception reading bid request for request id: {} ",requestId);
            return null;
        }

        inmobiBidRequestReader.validateBidRequestForMandatoryParameters(bidRequest);

        logger.debug("RequestIdByDSP: {} " , bidRequest.getUniqueInternalRequestId());

        //if flow comes here this means everything is correct in the bid request.
        //now populate request object for internal working.
        /********************************DETECT SITE BY REQUEST SITEID****************************************/
        Request request = new Request(requestId, INVENTORY_SOURCE.RTB_EXCHANGE);
        request.setBidRequest(bidRequest);

        String siteIdFromBidRequest = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), "/");

        logger.debug("SiteId received from bid request URL: {} ", siteIdFromBidRequest);

        Site site = fetchSiteEntityForInmobiRequest(request, siteIdFromBidRequest);

        if(null==site || !(site.getStatus() == StatusIdEnum.Active.getCode()))
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
            this.logger.error("Requesting site is not fit or is not found in cache . siteid: {}" , siteIdFromBidRequest);
            return request;
        }

        request.setSite(site);

        InmobiBidRequestParentNodeDTO inmobiBidRequestParentNodeDTO =
                       (InmobiBidRequestParentNodeDTO)request.getBidRequest().getBidRequestParentNodeDTO();

        /******************************DETECT HANDSET BY USERAGENT**********************************************/

        String userAgent = null;
        BidRequestDeviceDTOInmobi inmobiBidRequestDeviceDTO =
                                                        inmobiBidRequestParentNodeDTO.getBidRequestDevice();

        if(null != inmobiBidRequestDeviceDTO)
        {
            userAgent = inmobiBidRequestDeviceDTO.getDeviceUserAgent();
        }

        if(null == userAgent)
            throw new Exception("User Agent absent in bidrequest inside InmobiRequestEnricher, cannot proceed....");

        HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);

        if(null == handsetMasterData)
        {
            this.logger.debug("Device detection failed inside InmobiRequestEnricher");
        }else{
        	logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());
        }
        if(handsetMasterData != null && handsetMasterData.isBot())
        {
            this.logger.error("Device detected is BOT inside InmobiRequestEnricher, can not proceed further");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
            return request;
        }

        

        request.setHandsetMasterData(handsetMasterData);

        /*******************************DETECT COUNTRY CARRIER USING MNC MCC or IP*****************************/

        //detect country and carrier and enrich request object with the same.
        //the inmobi 2.3 format is mnc-mcc as desired by us.
        String mncMccCode = inmobiBidRequestDeviceDTO.getCarrier();
        CountryIspUiDataUsingMccMnc countryIspUiDataUsingMccMnc = null;
        Country country = null;
        InternetServiceProvider internetServiceProvider = null;

        logger.debug("MCC MNC codes combination received from inmobi 2.3 bid request is {} ", mncMccCode);

        //use mnc mcc,if not
        if(null != mncMccCode)
            countryIspUiDataUsingMccMnc = mncMccCountryISPDetectionCache.query(mncMccCode);

        /******************************************* ip extraction and connection type detection*********************/
        String ip = inmobiBidRequestDeviceDTO.getIpV4AddressClosestToDevice();
        if(null == ip)
        {
            logger.error("Country and InternetServiceProvider could not be detected inside InmobiRequestEnricher as mnc-mcc lookup failed as well as ip address not present...");
            throw new Exception("Country and InternetServiceProvider could not be detected inside InmobiRequestEnricher as mnc-mcc lookup failed as well as ip address not present...");
        }

        request.setIpAddressUsedForDetection(ip);

        if(connectionTypeDetectionCache != null)
        {
            // Get the connection type for this ip
            request.setConnectionType(this.connectionTypeDetectionCache.getConnectionType(ip));
        }
        else
        {
        	ConnectionType connectionType = ConnectionType.getEnum(inmobiBidRequestDeviceDTO.getConnectionType().shortValue());
            // Get the connection type for this ip
        	if(connectionType != null){
        		request.setConnectionType(connectionType);
        	}else{
        		// Default to unknown if the cache is not present
        		request.setConnectionType(ConnectionType.UNKNOWN);
        	}
        }
        /******************************************* ip extraction and  connection type detection ends***************/


        //if mnc mcc not present or location not detected use ip address to find location.
        if(null == countryIspUiDataUsingMccMnc)
        {
            logger.debug("No entry could be found for mcc-mnc combination: {} , using ip: {} , for location detection. "
                         ,mncMccCode,ip);

            country = findCountry(ip);
            internetServiceProvider = findCarrierEntity(ip);
            request.setCountry(country);
            request.setInternetServiceProvider(internetServiceProvider);
        }
        else if(null != countryIspUiDataUsingMccMnc &&
                null != countryIspUiDataUsingMccMnc.getCountryUiId() &&
                null != countryIspUiDataUsingMccMnc.getIspUiId())
        {
            request.setCountryUserInterfaceId(countryIspUiDataUsingMccMnc.getCountryUiId());
            request.setCarrierUserInterfaceId(countryIspUiDataUsingMccMnc.getIspUiId());

            logger.debug(
                         "Using MCC-MNC combination: {} , countryUiId: {} , ispUiId: {} ",
                         mncMccCode,
                         countryIspUiDataUsingMccMnc.getCountryUiId(),
                         countryIspUiDataUsingMccMnc.getIspUiId()
                        );
        }
        /******************************************************************************************************/

        /********************* Set strict banner size if applicable,also set text ad allowed*******************/
        BidRequestImpressionDTOInmobi[] inmobiBidRequestImpressionDTOs =
                inmobiBidRequestParentNodeDTO.getBidRequestImpressionArray();

        logger.debug("Going to set strict banner size for each impression if applicable inside InmobiRequestEnricher");

        if(null != inmobiBidRequestImpressionDTOs && inmobiBidRequestImpressionDTOs.length > 0)
        {
            for(BidRequestImpressionDTOInmobi inmobiBidRequestImpressionDTO : inmobiBidRequestImpressionDTOs)
            {
                BidRequestImpressionBannerObjectDTO inmobiBidRequestImpressionBannerObjectDTO =
                        inmobiBidRequestImpressionDTO.getBidRequestImpressionBannerObject();

                if(null != inmobiBidRequestImpressionBannerObjectDTO)
                {
                    Integer maxWidth = inmobiBidRequestImpressionBannerObjectDTO.getMaxBannerWidthInPixels();
                    Integer maxHeight = inmobiBidRequestImpressionBannerObjectDTO.getMaxBannerHeightInPixels();
                    Integer minWidth = inmobiBidRequestImpressionBannerObjectDTO.getMinBannerWidthInPixels();
                    Integer minHeight = inmobiBidRequestImpressionBannerObjectDTO.getMinBannerHeightInPixels();

                    if(null == maxWidth && null == maxHeight)
                        request.setStrictBannerSizeForImpressionIdOfRTBExchange
                                (inmobiBidRequestImpressionDTO.getBidRequestImpressionId(),true);

                    /*set max width and height required and request as interstital, where variable size is needed*/
                    else
                    {
                        int widthArray[] = new int[1];
                        int heightArray[] = new int[1];
                        int minWidthArray[] = new int[1];
                        int minHeightArray[] = new int[1];

                        widthArray[0] = maxWidth;
                        heightArray[0] = maxHeight;
                        if (null != minWidth && null != minHeight)
                        {
                            minWidthArray[0] = minWidth;
                            minHeightArray[0] = minHeight;
                        } else
                        {
                            minWidthArray[0] = 0;
                            minHeightArray[0] = 0;
                        }

                        request.setRequiredSizeArrayForImpressionIdOfRTBExchange
                                (inmobiBidRequestImpressionDTO.getBidRequestImpressionId(), widthArray, heightArray);
                        request.setInterstitalMinimumSizeArrayForImpressionIdOfRTBExchange
                                (inmobiBidRequestImpressionDTO.getBidRequestImpressionId(), minWidthArray, minHeightArray);
                        request.setInterstitialBidRequest(true);
                    }
            		if(this.creativeSlotSizeCache == null && 
            				maxWidth != null && maxHeight != null && request.getFirstImpClosestRequestedSlotIdList() == null){
            			short requestedSlotId = this.creativeSlotSizeCache.fetchSlotIdForExactSize(maxWidth,maxHeight);
                	    List<Short> firstImpClosestrequestedSlotIdList = new ArrayList<Short>();
                	    firstImpClosestrequestedSlotIdList.add(requestedSlotId);
                	    request.setFirstImpClosestRequestedSlotIdList(firstImpClosestrequestedSlotIdList);
            		}
                }
            }
        }
        /******************************************************************************************************/

        /************************** Set lat-long if available in the request **********************************/
        BidRequestGeoDTO bidRequestGeoDTO = inmobiBidRequestDeviceDTO.getGeoObject();
        if(null != bidRequestGeoDTO &&
           null != bidRequestGeoDTO.getGeoLongitude() &&
           null != bidRequestGeoDTO.getGeoLatitude())
        {
            request.setRequestingLongitudeValue(bidRequestGeoDTO.getGeoLongitude().doubleValue());
            request.setRequestingLatitudeValue(bidRequestGeoDTO.getGeoLatitude().doubleValue());
        }
        /******************************************************************************************************/

        /***************************Set extra available parameters*********************************************/
        populateRequestObjectForExtraParameters(inmobiBidRequestParentNodeDTO,request);
        /******************************************************************************************************/

        return request;
    }

    /**
     * All attributes must be set at runtime except hygiene ,which
     * should be taken from the entity as present in the database.
     */
    private Site fetchSiteEntityForInmobiRequest(Request request,String siteIdFromBidRequest)
    {
        Site site = this.siteCache.query(siteIdFromBidRequest);

        InmobiBidRequestParentNodeDTO inmobiBidRequestParentNodeDTO =
                                    (InmobiBidRequestParentNodeDTO)request.getBidRequest().getBidRequestParentNodeDTO();

        BidRequestSiteDTO inmobiBidRequestSiteDTO = inmobiBidRequestParentNodeDTO.getBidRequestSite();
        BidRequestAppDTOInmobi inmobiBidRequestAppDTO = inmobiBidRequestParentNodeDTO.getBidRequestApp();

        String siteUrl = null != inmobiBidRequestSiteDTO ? inmobiBidRequestSiteDTO.getSitePageURL() : null;
        Short[] categoriesArray = null;
        String[] contentCategoriesSiteApp = null;
        Short sitePlatform;
        String applicationId = null;
        if(null != inmobiBidRequestSiteDTO)
        {
            contentCategoriesSiteApp = inmobiBidRequestSiteDTO.getContentCategoriesForSite();
            sitePlatform = SITE_PLATFORM.WAP.getPlatform();
        }
        else if(null != inmobiBidRequestAppDTO)
        {
            contentCategoriesSiteApp = inmobiBidRequestAppDTO.getContentCategoriesApplication();
            sitePlatform = SITE_PLATFORM.APP.getPlatform();
            applicationId = inmobiBidRequestAppDTO.getApplicationIdOnExchange();
        }
        else
        {
            logger.error("Site/App not found in request, site/app both not present inside InmobiRequestEnricher...aborting request....");
            return null;
        }
        if(null != contentCategoriesSiteApp)
        {
            categoriesArray = findIABContentCategoryInternalIdArray(contentCategoriesSiteApp);
        }

        String blockedIABCategories[] = inmobiBidRequestParentNodeDTO.getBlockedAdvertiserCategoriesForBidRequest();
        Short[] categoriesArrayIncExc = null;
        if(null != blockedIABCategories)
            categoriesArrayIncExc = findIABContentCategoryInternalIdArray(blockedIABCategories);

        //creative attributes to exclude come at banner/video level for each impression required
        //in the bid response for the bid request, hence set null here and in workflow where
        //creative targeting matching is done use fields from bid request for matching up.
        Short[] creativeAttributesIncExc = null;
        boolean isCreativeAttributesForExclusion = true;
        boolean excludeDefinedAdDomains = true;
        boolean isMarkedForDeletion = false;
        Timestamp lastModifiedOn = new Timestamp(System.currentTimeMillis());
        Short appStoreId = 0;
        //keep floor value as 0.0, since its per impression, so for each impression
        //this value should be used and compared with ecpm value of each adunit.
        double ecpmFloorValue = 0.0;

        //Create a new site and set all attributes, take hygiene from the one found in cache.
        Site siteToUse = new Site.SiteEntityBuilder
                (
                 site.getSiteIncId(), site.getSiteGuid(), site.getName(),
                 site.getPublisherIncId(),site.getPublisherId(),siteUrl,
                 categoriesArray,categoriesArrayIncExc,true,
                 site.getHygieneList(),site.getOptInHygieneList(),creativeAttributesIncExc,
                 isCreativeAttributesForExclusion,sitePlatform, site.getStatus(),
                 inmobiBidRequestParentNodeDTO.getBlockedAdvertiserDomainsForBidRequest(),
                 excludeDefinedAdDomains,isMarkedForDeletion,lastModifiedOn, false, null
                )
                .setApplicationId(applicationId).setAppStoreId(appStoreId)
                .setEcpmFloor(ecpmFloorValue)
                .setIsAdvertiserIdListExcluded(site.isAdvertiserIdListExcluded())
                .setCampaignInclusionExclusionSchemaMap(site.getCampaignInclusionExclusionSchemaMap())
                .setIsRichMediaAllowed(site.isRichMediaAllowed())
                .build();

        /***************************************external supply attributes*************************************/
        String externalSupplyUrl = null;
        String externalSupplyId = null;
        String externalSupplyName = null;
        String externalSupplyDomain = null;
        String externalAppVersion = null;
        String externalAppPageUrl = null;
        String[] externalCategories = null;
        String externalAppBundle = null;

        if(null != inmobiBidRequestSiteDTO)
        {
            externalSupplyUrl = inmobiBidRequestSiteDTO.getSitePageURL();
            externalSupplyId = inmobiBidRequestSiteDTO.getSiteIdOnExchange();
            externalSupplyName = inmobiBidRequestSiteDTO.getSiteName();
            externalSupplyDomain = inmobiBidRequestSiteDTO.getSiteDomain();
            externalAppPageUrl = inmobiBidRequestSiteDTO.getSitePageURL();        
        }
        else if(null != inmobiBidRequestAppDTO)
        {
            externalSupplyUrl = inmobiBidRequestAppDTO.getApplicationStoreUrl();
            externalSupplyId = inmobiBidRequestAppDTO.getApplicationIdOnExchange();
            externalSupplyName = inmobiBidRequestAppDTO.getApplicationName();
            externalSupplyDomain = inmobiBidRequestAppDTO.getApplicationDomain();
            externalAppVersion = inmobiBidRequestAppDTO.getApplicationVersion();
            if(inmobiBidRequestAppDTO.getContentCategoriesApplication() != null){ 
                externalCategories = inmobiBidRequestAppDTO.getContentCategoriesApplication();
            }
            externalAppBundle = inmobiBidRequestAppDTO.getApplicationBundleName();
        }

        siteToUse.setExternalSupplyDomain(externalSupplyDomain);
        siteToUse.setExternalSupplyId(externalSupplyId);
        siteToUse.setExternalSupplyName(externalSupplyName);
        siteToUse.setExternalSupplyUrl(externalSupplyUrl);
        siteToUse.setExternalAppVersion(externalAppVersion);
        siteToUse.setExternalPageUrl(externalAppPageUrl);
        siteToUse.setExternalAppBundle(externalAppBundle);
        siteToUse.setExternalCategories(externalCategories);
        /******************************************************************************************************/

        return siteToUse;
    }

    private Short findIABContentCategoryInternalId(String contentCategoryCode)
    {
        IABCategoryEntity iabCategoryEntity = this.iabCategoriesCache.query(contentCategoryCode);
        if(null != iabCategoryEntity)
            return iabCategoryEntity.getInternalId();
        return null;
    }

    private Short[] findIABContentCategoryInternalIdArray(String[] contentCategoryArray)
    {
        Set<Short> contentCategoriesSet = new HashSet<Short>();
        for(String categoryCode : contentCategoryArray)
        {
            Short internalId = findIABContentCategoryInternalId(categoryCode);
            if(null==internalId)
                logger.debug("The iab category could not be found in internal database for code: {} ",categoryCode);
            else
                contentCategoriesSet.add(internalId);
        }

        if(contentCategoriesSet.size() > 0)
            return contentCategoriesSet.toArray(new Short[contentCategoriesSet.size()]);
        return null;
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
            logger.error("Exception inside InmobiRequestEnricher in fetching country " , e);
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
            logger.error("Exception inside InmobiRequestEnricher in fetching isp ",e);
        }

        return internetServiceProvider;
    }

    /**
     * This function populates request object with extra parameters obtained from bid request
     * such as mcc-mnc, user-ids ,app and site attributes etc.
     * @param inmobiBidRequestParentNodeDTO
     * @param request
     */
    private void populateRequestObjectForExtraParameters(InmobiBidRequestParentNodeDTO inmobiBidRequestParentNodeDTO,
                                                         Request request)
    {
        BidRequestUserDTOInmobi inmobiBidRequestUserDTO = inmobiBidRequestParentNodeDTO.getBidRequestUser();
        BidRequestDeviceDTOInmobi inmobiBidRequestDeviceDTO = inmobiBidRequestParentNodeDTO.getBidRequestDevice();

        EnricherUtils.populateUserIdsFromBidRequestDeviceDTO(inmobiBidRequestDeviceDTO, request);
        EnricherUtils.populateUserIdsFromBidRequestUserDTO(inmobiBidRequestUserDTO, request);
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null || externalUserIds.size() == 0) {
            logger.debug("External user ids empty or not present");
        } else {
            logger.debug("External user ids found.");
            for(ExternalUserId externalUserId : externalUserIds) {
                logger.debug("\texternal user id = {}", externalUserId);
            }
        }

        if(null != inmobiBidRequestDeviceDTO)
        {
            request.setMccMncCodeCouple(inmobiBidRequestDeviceDTO.getCarrier());
            request.setUserAgent(inmobiBidRequestDeviceDTO.getDeviceUserAgent());
            request.setIpAddressUsedForDetection(inmobiBidRequestDeviceDTO.getIpV4AddressClosestToDevice());
        }
    }
}
