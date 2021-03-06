package com.kritter.adexchange.mopub.enricher;

import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;
import com.kritter.bidreqres.entity.mopub2_3.*;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.caches.iab.categories.entity.IABCategoryEntity;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.*;
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
 * This class reads request from mopub rtb exchange.
 * The URL provided to mopub contains siteid as well:
 * e.g:
 * http://rtbexchange.dsp.com/b2409484c01754ef08c0e476b796fc081/a179484c01754ef08c0e476b796fc081
 * first id is publisher id and second is the siteid.
 */
public class MoPubRequestEnricher2_3 implements RTBExchangeRequestReader
{
    private Logger logger;
    private IBidRequestReader mopubBidRequestReader;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private IABCategoriesCache iabCategoriesCache;
    private MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private IConnectionTypeDetectionCache connectionTypeDetectionCache;
    private static final String ENCODING = "UTF-8";

    public MoPubRequestEnricher2_3( String loggerName,
                                    IBidRequestReader mopubBidRequestReader,
                                    SiteCache siteCache,
                                    HandsetDetectionProvider handsetDetectionProvider,
                                    IABCategoriesCache iabCategoriesCache,
                                    MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache,
                                    CountryDetectionCache countryDetectionCache,
                                    ISPDetectionCache ispDetectionCache
                                  )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.mopubBidRequestReader = mopubBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.iabCategoriesCache = iabCategoriesCache;
        this.mncMccCountryISPDetectionCache = mncMccCountryISPDetectionCache;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = null;
    }

    public MoPubRequestEnricher2_3( String loggerName,
                                    IBidRequestReader mopubBidRequestReader,
                                    SiteCache siteCache,
                                    HandsetDetectionProvider handsetDetectionProvider,
                                    IABCategoriesCache iabCategoriesCache,
                                    MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache,
                                    CountryDetectionCache countryDetectionCache,
                                    ISPDetectionCache ispDetectionCache,
                                    IConnectionTypeDetectionCache connectionTypeDetectionCache
                                  )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.mopubBidRequestReader = mopubBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.iabCategoriesCache = iabCategoriesCache;
        this.mncMccCountryISPDetectionCache = mncMccCountryISPDetectionCache;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
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

        logger.debug("Request Method inside validateAndEnrichRequest of MoPubRequestEnricher is {} ",
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
            logger.error("The bidrequest payload received from Mopub RTB Exchange is null inside validateAndEnrichRequest of MoPubRequestEnricher,cannot enrich bid request...");
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
            bidRequest = mopubBidRequestReader.
                    convertBidRequestPayloadToBusinessObject(bidRequestPayLoadReceived);
        }
        catch (Exception e)
        {
            logger.error("Exception in reading mopub bid request ", e);
            logger.error("Exception reading bid request for request id: {} ",requestId);
            return null;
        }

        //if bid request is null throw exception
        if(null == bidRequest)
            throw new Exception("BidRequest could not be read from mopub ad-exchange...Payload being: " +
                                 bidRequestPayLoadReceived);

        mopubBidRequestReader.validateBidRequestForMandatoryParameters(bidRequest);

        logger.debug("RequestIdByDSP: {} " , bidRequest.getUniqueInternalRequestId());

        //if flow comes here this means everything is correct in the bid request.
        //now populate request object for internal working.
        /********************************DETECT SITE BY REQUEST SITEID****************************************/
        Request request = new Request(requestId, INVENTORY_SOURCE.RTB_EXCHANGE);
        request.setBidRequest(bidRequest);

        String siteIdFromBidRequest = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), "/");

        logger.debug("SiteId received from bid request URL: {} ", siteIdFromBidRequest);

        Site site = fetchSiteEntityForMopubRequest(request,siteIdFromBidRequest);

        if(null==site || !(site.getStatus() == StatusIdEnum.Active.getCode()))
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
            this.logger.error("Requesting site is not fit or is not found in cache . siteid: {}" , siteIdFromBidRequest);
            return request;
        }

        request.setSite(site);

        MopubBidRequestParentNodeDTO mopubBidRequestParentNodeDTO =
                                    (MopubBidRequestParentNodeDTO)request.getBidRequest().getBidRequestParentNodeDTO();

        /******************************DETECT HANDSET BY USERAGENT**********************************************/
        String userAgent = null;
        MopubBidRequestDeviceDTO mopubBidRequestDeviceDTO =
                                                        mopubBidRequestParentNodeDTO.getMopubBidRequestDeviceDTO();

        if(null != mopubBidRequestDeviceDTO)
        {
            userAgent = mopubBidRequestDeviceDTO.getDeviceUserAgent();
        }

        if(null == userAgent)
            throw new Exception("User Agent absent in bidrequest inside MoPubRequestEnricher, cannot proceed....");

        HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);

        if(null == handsetMasterData)
        {
            this.logger.error("Device detection failed inside MoPubRequestEnricher, can not proceed further");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
            return request;
        }
        if(handsetMasterData.isBot())
        {
            this.logger.error("Device detected is BOT inside MoPubRequestEnricher, can not proceed further");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
            return request;
        }

        logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());

        request.setHandsetMasterData(handsetMasterData);

        /*******************************DETECT COUNTRY CARRIER USING MNC MCC or IP*****************************/

        //detect country and carrier and enrich request object with the same.
        //the mopub format is mnc-mcc as desired by us.
        String mncMccCode = mopubBidRequestDeviceDTO.getCarrier();
        CountryIspUiDataUsingMccMnc countryIspUiDataUsingMccMnc = null;
        Country country = null;
        InternetServiceProvider internetServiceProvider = null;

        logger.debug("MCC MNC codes combination received from mopub bid request is {} ", mncMccCode);

        //use mnc mcc,if not
        if(null != mncMccCode)
            countryIspUiDataUsingMccMnc = mncMccCountryISPDetectionCache.query(mncMccCode);

        /******************************************* ip extraction and connection type detection*********************/
        String ip = mopubBidRequestDeviceDTO.getIpV4AddressClosestToDevice();
        if(null == ip)
        {
            logger.error("Country and InternetServiceProvider could not be detected inside MoPubRequestEnricher as mnc-mcc lookup failed as well as ip address not present...");
            throw new Exception("Country and InternetServiceProvider could not be detected inside MoPubRequestEnricher as mnc-mcc lookup failed as well as ip address not present...");
        }

        request.setIpAddressUsedForDetection(ip);

        if(connectionTypeDetectionCache != null)
        {
            // Get the connection type for this ip
            request.setConnectionType(this.connectionTypeDetectionCache.getConnectionType(ip));
        }
        else
        {
            // Default to unknown if the cache is not present
            request.setConnectionType(ConnectionType.UNKNOWN);
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

        /************************** Set lat-long if available in the request **********************************/
        BidRequestGeoDTO bidRequestGeoDTO = mopubBidRequestDeviceDTO.getGeoObject();
        if(null != bidRequestGeoDTO &&
           null != bidRequestGeoDTO.getGeoLongitude() &&
           null != bidRequestGeoDTO.getGeoLatitude())
        {
            request.setRequestingLongitudeValue(bidRequestGeoDTO.getGeoLongitude().doubleValue());
            request.setRequestingLatitudeValue(bidRequestGeoDTO.getGeoLatitude().doubleValue());
        }
        /******************************************************************************************************/

        /***************************Set extra available parameters*********************************************/
        populateRequestObjectForExtraParameters(mopubBidRequestParentNodeDTO,request);
        /******************************************************************************************************/

        return request;
    }

    /**
     * All attributes must be set at runtime except hygiene ,which
     * should be taken from the entity as present in the database.
     */
    private Site fetchSiteEntityForMopubRequest(Request request,String siteIdFromBidRequest)
    {
        Site site = this.siteCache.query(siteIdFromBidRequest);

        MopubBidRequestParentNodeDTO mopubBidRequestParentNodeDTO =
                                    (MopubBidRequestParentNodeDTO)request.getBidRequest().getBidRequestParentNodeDTO();

        MopubBidRequestSiteDTO mopubBidRequestSiteDTO = mopubBidRequestParentNodeDTO.getMopubBidRequestSiteDTO();
        MopubBidRequestAppDTO mopubBidRequestAppDTO = mopubBidRequestParentNodeDTO.getMopubBidRequestAppDTO();

        String siteUrl = null != mopubBidRequestSiteDTO ? mopubBidRequestSiteDTO.getSitePageURL() : null;
        Short[] categoriesArray = null;
        String[] contentCategoriesSiteApp = null;
        Short sitePlatform;
        String applicationId = null;
        if(null != mopubBidRequestSiteDTO)
        {
            contentCategoriesSiteApp = mopubBidRequestSiteDTO.getContentCategoriesForSite();
            sitePlatform = SITE_PLATFORM.WAP.getPlatform();
        }
        else if(null != mopubBidRequestAppDTO)
        {
            contentCategoriesSiteApp = mopubBidRequestAppDTO.getContentCategoriesApplication();
            sitePlatform = SITE_PLATFORM.APP.getPlatform();
            applicationId = mopubBidRequestAppDTO.getApplicationIdOnExchange();
        }
        else
        {
            logger.error("Site/App not found in request, site/app both not present inside MoPubRequestEnricher...aborting request....");
            return null;
        }
        if(null != contentCategoriesSiteApp)
        {
            categoriesArray = findIABContentCategoryInternalIdArray(contentCategoriesSiteApp);
        }

        String blockedIABCategories[] = mopubBidRequestParentNodeDTO.getBlockedAdvertiserCategoriesForBidRequest();
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
                 mopubBidRequestParentNodeDTO.getBlockedAdvertiserDomainsForBidRequest(),
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

        if(null != mopubBidRequestSiteDTO)
        {
            externalSupplyUrl = mopubBidRequestSiteDTO.getSitePageURL();
            externalSupplyId = mopubBidRequestSiteDTO.getSiteIdOnExchange();
            externalSupplyName = mopubBidRequestSiteDTO.getSiteName();
            externalSupplyDomain = mopubBidRequestSiteDTO.getSiteDomain();
            externalAppPageUrl = mopubBidRequestSiteDTO.getSitePageURL();
        }
        else if(null != mopubBidRequestAppDTO)
        {
            externalSupplyUrl = mopubBidRequestAppDTO.getApplicationStoreUrl();
            externalSupplyId = mopubBidRequestAppDTO.getApplicationIdOnExchange();
            externalSupplyName = mopubBidRequestAppDTO.getApplicationName();
            externalSupplyDomain = mopubBidRequestAppDTO.getApplicationDomain();
            externalAppVersion = mopubBidRequestAppDTO.getApplicationVersion();
            if(mopubBidRequestAppDTO.getContentCategoriesApplication() != null){ 
                externalCategories = mopubBidRequestAppDTO.getContentCategoriesApplication();
            }
            externalAppBundle = mopubBidRequestAppDTO.getApplicationBundleName();
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
        //find iTunes categories if any and modify original array.
        List<String> contentCategoriesFinal = new ArrayList<String>();

        for(String category : contentCategoryArray)
        {
            String[] iabCategories = ITunesCategoryMappingToIAB.fetchIABCategoryArrayForITunesContentCategory(category);
            if(null != iabCategories)
            {
                for(String iabCode : iabCategories)
                {
                    contentCategoriesFinal.add(iabCode);
                }
            }
            //else it must be IAB category.
            else
            {
                contentCategoriesFinal.add(category);
            }
        }

        contentCategoryArray = contentCategoriesFinal.toArray(new String[contentCategoriesFinal.size()]);

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
            logger.error("Exception inside MoPubRequestEnricher in fetching country " , e);
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
            logger.error("Exception inside MoPubRequestEnricher in fetching isp ",e);
        }

        return internetServiceProvider;
    }

    /**
     * This function populates request object with extra parameters obtained from bid request
     * such as mcc-mnc, user-ids ,app and site attributes etc.
     * @param mopubBidRequestParentNodeDTO
     * @param request
     */
    private void populateRequestObjectForExtraParameters(MopubBidRequestParentNodeDTO mopubBidRequestParentNodeDTO,
                                                         Request request)
    {
        MopubBidRequestUserDTO mopubBidRequestUserDTO = mopubBidRequestParentNodeDTO.getMopubBidRequestUserDTO();
        MopubBidRequestDeviceDTO mopubBidRequestDeviceDTO = mopubBidRequestParentNodeDTO.getMopubBidRequestDeviceDTO();

        EnricherUtils.populateUserIdsFromBidRequestDeviceDTO(mopubBidRequestDeviceDTO, request);
        EnricherUtils.populateUserIdsFromBidRequestUserDTO(mopubBidRequestUserDTO, request);
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null || externalUserIds.size() == 0) {
            logger.debug("External user ids empty or not present");
        } else {
            logger.debug("External user ids found.");
            for(ExternalUserId externalUserId : externalUserIds) {
                logger.debug("\texternal user id = {}", externalUserId);
            }
        }

        if(null != mopubBidRequestDeviceDTO)
        {
            request.setMccMncCodeCouple(mopubBidRequestDeviceDTO.getCarrier());
            request.setUserAgent(mopubBidRequestDeviceDTO.getDeviceUserAgent());
            request.setIpAddressUsedForDetection(mopubBidRequestDeviceDTO.getIpV4AddressClosestToDevice());
        }
    }
}
