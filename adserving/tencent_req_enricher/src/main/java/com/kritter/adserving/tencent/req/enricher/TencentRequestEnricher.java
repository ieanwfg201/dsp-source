package com.kritter.adserving.tencent.req.enricher;


import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestAppDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.*;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.geo.common.entity.reader.MncMccCountryISPDetectionCache;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.tencent.reader_v20150313.entity.BidRequestTencent;
import com.kritter.tencent.reader_v20150313.entity.TencentBidRequestParentNodeDTO;
import com.kritter.tencent.reader_v20150313.reader.TencentBidRequestReader;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class enriches tencent request to enable DSP for choosing right ads.
 */
public class TencentRequestEnricher implements RTBExchangeRequestReader
{
    private Logger logger;
    private String auctioneerId;
    private UUIDGenerator uuidGenerator;
    private TencentBidRequestReader tencentBidRequestReader;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;

    public TencentRequestEnricher(String loggerName,
                              String auctioneerId,
                              TencentBidRequestReader tencentBidRequestReader,
                              SiteCache siteCache,
                              HandsetDetectionProvider handsetDetectionProvider,
                              IABCategoriesCache iabCategoriesCache,
                              MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache,
                              CountryDetectionCache countryDetectionCache,
                              ISPDetectionCache ispDetectionCache,
                              DatabaseManager databaseManager,
                              String datasource)
    {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.auctioneerId = auctioneerId;
        this.uuidGenerator = new UUIDGenerator();
        this.tencentBidRequestReader = tencentBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger,
                                            Logger bidRequestLogger,
                                            boolean logBidRequest,
                                            String publisherId) throws Exception {

        Request request = new Request(requestId, INVENTORY_SOURCE.RTB_EXCHANGE);

        /*For tencent exchange the response body has to be written inside its own response creator adaptor*/
        request.setWriteResponseInsideExchangeAdaptor(true);

        TencentBidRequestParentNodeDTO tencentBidRequestParentNodeDTO = this.tencentBidRequestReader.
                readAndConvertBidRequestPayLoadToOpenRTB_2_3(httpServletRequest.getInputStream());

        String uniqueInternalBidRequestId = uuidGenerator.generateUniversallyUniqueIdentifier().toString();
        IBidRequest bidRequestTencent = new BidRequestTencent(auctioneerId,
                                                      uniqueInternalBidRequestId,
                                                      tencentBidRequestParentNodeDTO);
        request.setBidRequest(bidRequestTencent);

        /**
         * Set site object, generally there will be only one site for tencent(any exchange for that matter).
         */
        String siteIdFromBidRequest = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), "/");

        logger.debug("SiteId received from bid request URL: {} ", siteIdFromBidRequest);


        Site site = fetchSiteEntityForTencentRequest(request,siteIdFromBidRequest);

        logger.debug("Site extracted inside TencentRequestEnricher is null ? : {} ", (null == site) );

        if(null != site)
            request.setSite(site);

        if(null==site || !(site.getStatus() == StatusIdEnum.Active.getCode()))
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
            this.logger.error("Requesting site from Google Tencent is not fit or is not found in cache . siteid: {} ",
                              siteIdFromBidRequest);
            return request;
        }

        /***********************Detect handset of the request.Use the user agent from bid request.*******************/
        /***********************************DETECT HANDSET BY USERAGENT**********************************************/
        String userAgent = null;
        BidRequestDeviceDTO tencentBidRequestDeviceDTO = tencentBidRequestParentNodeDTO.getBidRequestDevice();

        if(null != tencentBidRequestDeviceDTO)
        {
            userAgent = tencentBidRequestDeviceDTO.getDeviceUserAgent();
        }

        if(null == userAgent)
            throw new Exception("User Agent absent in bidrequest inside TencentRequestEnricher, cannot proceed....");

        HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);

        if(null == handsetMasterData)
        {
            this.logger.error("Device detection failed inside TencentRequestEnricher, can not proceed further");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
            return request;
        }
        if(handsetMasterData.isBot())
        {
            this.logger.error("Device detected is BOT inside TencentRequestEnricher,can not proceed further");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
            return request;
        }

        logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());

        request.setHandsetMasterData(handsetMasterData);
        /************************************Done detecting handset****************************************************/

        /*******************************DETECT COUNTRY CARRIER USING MNC MCC or IP*****************************/
        String ip = tencentBidRequestDeviceDTO.getIpV4AddressClosestToDevice();
        if(ip ==null){
            ip =tencentBidRequestDeviceDTO.getIpV6Address();
        }
        InternetServiceProvider internetServiceProvider = null;
        Country country = null;
        if(null != ip)
        {
            country = findCountry(ip);
            internetServiceProvider = findCarrierEntity(ip);
            request.setCountry(country);
            request.setInternetServiceProvider(internetServiceProvider);
        }
        BidRequestGeoDTO bidRequestGeoDTO = tencentBidRequestDeviceDTO.getGeoObject();
        
        if(null == ip)
        {
            logger.error("Ip address  not present inside Tencent bid request.");
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            return request;
        }

        request.setIpAddressUsedForDetection(ip);

        request.setConnectionType(ConnectionType.UNKNOWN);
        /************************Location detection completes,country,isp,connectiontype******************************/

        /************************** Set lat-long if available in the request **********************************/
        if(null != bidRequestGeoDTO &&
           null != bidRequestGeoDTO.getGeoLongitude() &&
           null != bidRequestGeoDTO.getGeoLatitude())
        {
            request.setRequestingLongitudeValue(bidRequestGeoDTO.getGeoLongitude().doubleValue());
            request.setRequestingLatitudeValue(bidRequestGeoDTO.getGeoLatitude().doubleValue());
        }
        /******************************************************************************************************/

        boolean isInterstitial = false;
        request.setInterstitialBidRequest(isInterstitial);

        /**********Set width and height array to the Request object.Also set if interstitial or not.******************/
        for (BidRequestImpressionDTO bidRequestImpressionDTO:tencentBidRequestParentNodeDTO.getBidRequestImpressionArray())
        {
/*            ImpressionExtDTO impressionExtDTO = (ImpressionExtDTO)bidRequestImpressionDTO.getExtensionObject();
            request.setRequiredSizeArrayForImpressionIdOfRTBExchange
                    (bidRequestImpressionDTO.getBidRequestImpressionId(),
                     impressionExtDTO.getAllowedAdWidths(),impressionExtDTO.getAllowedAdHeights());*/

            if(!isInterstitial)
                request.setStrictBannerSizeForImpressionIdOfRTBExchange
                        (bidRequestImpressionDTO.getBidRequestImpressionId(),true);
            else
            {
                /*Set the required minimum size values as per tencent policy.*/
                /*int[] interstitialWidthArray = new int[impressionExtDTO.getAllowedAdWidths().length];
                int[] interstitialHeightArray = new int[impressionExtDTO.getAllowedAdHeights().length];
                 
                int counter = 0;

                for(int width : impressionExtDTO.getAllowedAdWidths())
                {
                    int height = impressionExtDTO.getAllowedAdHeights()[counter];

                    interstitialWidthArray[counter] = (int)(width * 0.5);
                    interstitialHeightArray[counter] = (int)(height * 0.4);

                    counter ++;
                }

                request.setInterstitalMinimumSizeArrayForImpressionIdOfRTBExchange
                        (bidRequestImpressionDTO.getBidRequestImpressionId(),
                         interstitialWidthArray,
                         interstitialHeightArray);
                         */
            }
        }
        /************************************************************************************************************/

        /***************************Set extra available parameters*********************************************/
        populateRequestObjectForExtraParameters(tencentBidRequestParentNodeDTO,request);
        /******************************************************************************************************/

        return request;
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
            logger.error("Exception inside TencentRequestEnricher in fetching country " , e);
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
            logger.error("Exception inside TencentRequestEnricher in fetching isp ",e);
        }

        return internetServiceProvider;
    }

    /**
     * All attributes must be set at runtime except hygiene ,which
     * should be taken from the entity as present in the database.
     */
    private Site fetchSiteEntityForTencentRequest(Request request,String siteIdFromBidRequest)
    {
        Site site = this.siteCache.query(siteIdFromBidRequest);

        if(null == site)
            return null;

        TencentBidRequestParentNodeDTO tencentBidRequestParentNodeDTO =
                (TencentBidRequestParentNodeDTO)request.getBidRequest().getBidRequestParentNodeDTO();

        BidRequestSiteDTO tencentBidRequestSiteDTO = tencentBidRequestParentNodeDTO.getBidRequestSite();
        BidRequestAppDTO tencentBidRequestAppDTO = tencentBidRequestParentNodeDTO.getBidRequestApp();

        String siteUrl = null != tencentBidRequestSiteDTO ? tencentBidRequestSiteDTO.getSitePageURL() : null;
        Short sitePlatform;
        String applicationId = null;
        if(null != tencentBidRequestSiteDTO)
        {
            sitePlatform = SITE_PLATFORM.WAP.getPlatform();
        }
        else if(null != tencentBidRequestAppDTO)
        {
            sitePlatform = SITE_PLATFORM.APP.getPlatform();
            applicationId = tencentBidRequestAppDTO.getApplicationIdOnExchange();
        }
        else
        {
            logger.error("Site/App not found in request, site/app both not present inside TencentRequestEnricher.Aborting request....");
            return null;
        }
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
                        null,null,true,
                        site.getHygieneList(),site.getOptInHygieneList(),null,
                        false,sitePlatform, site.getStatus(),
                        tencentBidRequestParentNodeDTO.getBlockedAdvertiserDomainsForBidRequest(),
                        false,isMarkedForDeletion,lastModifiedOn, false, null
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

        if(null != tencentBidRequestSiteDTO)
        {
            externalSupplyUrl = tencentBidRequestSiteDTO.getSitePageURL();
            externalSupplyId = tencentBidRequestSiteDTO.getSiteIdOnExchange();
            externalSupplyName = tencentBidRequestSiteDTO.getSiteName();
            externalSupplyDomain = tencentBidRequestSiteDTO.getSiteDomain();
            externalAppPageUrl = tencentBidRequestSiteDTO.getSitePageURL();
        }
        else if(null != tencentBidRequestAppDTO)
        {
            externalSupplyUrl = tencentBidRequestAppDTO.getApplicationStoreUrl();
            externalSupplyId = tencentBidRequestAppDTO.getApplicationIdOnExchange();
            externalSupplyName = tencentBidRequestAppDTO.getApplicationName();
            externalSupplyDomain = tencentBidRequestAppDTO.getApplicationDomain();
            externalAppVersion = tencentBidRequestAppDTO.getApplicationVersion();
            if(tencentBidRequestAppDTO.getContentCategoriesApplication() != null){ 
                externalCategories = tencentBidRequestAppDTO.getContentCategoriesApplication();
            }
            externalAppBundle = tencentBidRequestAppDTO.getApplicationBundleName();
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

    /**
     * This function populates request object with extra parameters obtained from bid request
     * such as mcc-mnc, user-ids ,app and site attributes etc.
     * @param tencentBidRequestParentNodeDTO
     * @param request
     */
    private void populateRequestObjectForExtraParameters(TencentBidRequestParentNodeDTO tencentBidRequestParentNodeDTO,
                                                         Request request)
    {
        BidRequestUserDTO tencentBidRequestUserDTO = tencentBidRequestParentNodeDTO.getBidRequestUser();
        BidRequestDeviceDTO tencentBidRequestDeviceDTO = tencentBidRequestParentNodeDTO.getBidRequestDevice();

        EnricherUtils.populateUserIdsFromBidRequestDeviceDTO(tencentBidRequestDeviceDTO, request);
        EnricherUtils.populateUserIdsFromBidRequestUserDTO(tencentBidRequestUserDTO, request);
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null || externalUserIds.size() == 0) {
            logger.debug("External user ids empty or not present");
        } else {
            logger.debug("External user ids found.");
            for(ExternalUserId externalUserId : externalUserIds) {
                logger.debug("\texternal user id = {}", externalUserId);
            }
        }

        if(null != tencentBidRequestDeviceDTO)
        {
            request.setUserAgent(tencentBidRequestDeviceDTO.getDeviceUserAgent());
            if(tencentBidRequestDeviceDTO.getIpV4AddressClosestToDevice() != null){
                request.setIpAddressUsedForDetection(tencentBidRequestDeviceDTO.getIpV4AddressClosestToDevice());
            }else if(tencentBidRequestDeviceDTO.getIpV6Address() != null){
                request.setIpAddressUsedForDetection(tencentBidRequestDeviceDTO.getIpV6Address());
            }
        }
    }
}
