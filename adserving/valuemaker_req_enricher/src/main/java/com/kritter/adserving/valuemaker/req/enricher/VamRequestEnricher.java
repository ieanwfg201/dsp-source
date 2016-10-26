package com.kritter.adserving.valuemaker.req.enricher;


import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import com.kritter.common.caches.iab.categories.IABCategoriesCache;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.StatusIdEnum;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.CountryIspUiDataUsingMccMnc;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.geo.common.entity.reader.MncMccCountryISPDetectionCache;
import com.kritter.utils.databasemanager.DatabaseManager;
import com.kritter.utils.uuid.mac.UUIDGenerator;
import com.kritter.valuemaker.reader_v20160817.entity.BidRequestVam;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;
import com.kritter.valuemaker.reader_v20160817.reader.VamBidRequestReader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Set;

/**
 * This class enriches valuemaker request to enable DSP for choosing right ads.
 */
public class VamRequestEnricher implements RTBExchangeRequestReader {
    private Logger logger;
    private String auctioneerId;
    private UUIDGenerator uuidGenerator;
    private VamBidRequestReader vamBidRequestReader;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private IABCategoriesCache iabCategoriesCache;

    public VamRequestEnricher(String loggerName,
                              String auctioneerId,
                              VamBidRequestReader vamBidRequestReader,
                              SiteCache siteCache,
                              HandsetDetectionProvider handsetDetectionProvider,
                              IABCategoriesCache iabCategoriesCache,
                              MncMccCountryISPDetectionCache mncMccCountryISPDetectionCache,
                              CountryDetectionCache countryDetectionCache,
                              ISPDetectionCache ispDetectionCache,
                              DatabaseManager databaseManager,
                              String datasource) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.auctioneerId = auctioneerId;
        this.uuidGenerator = new UUIDGenerator();
        this.vamBidRequestReader = vamBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.mncMccCountryISPDetectionCache = mncMccCountryISPDetectionCache;
        this.iabCategoriesCache = iabCategoriesCache;
        this.ispDetectionCache = ispDetectionCache;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger,
                                            Logger bidRequestLogger,
                                            boolean logBidRequest,
                                            String publisherId) throws Exception {

        Request request = null;
        try {
            request = new Request(requestId, INVENTORY_SOURCE.RTB_EXCHANGE);

            /*For valuemaker exchange the response body has to be written inside its own response creator adaptor*/
            request.setWriteResponseInsideExchangeAdaptor(true);

            VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO = this.vamBidRequestReader.readAndConvertBidRequestPayLoadToOpenRTB_2_3(httpServletRequest.getInputStream());

            String uniqueInternalBidRequestId = uuidGenerator.generateUniversallyUniqueIdentifier().toString();
            IBidRequest bidRequestVam = new BidRequestVam(auctioneerId, uniqueInternalBidRequestId, vamBidRequestParentNodeDTO);
            request.setBidRequest(bidRequestVam);

            /**
             * Set site object, generally there will be only one site for valuemaker(any exchange for that matter).
             */
            String siteIdFromBidRequest = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), "/");

            logger.debug("SiteId received from bid request URL: {} ", siteIdFromBidRequest);

            Site site = fetchSiteEntityForVamRequest(request, siteIdFromBidRequest);

            logger.debug("Site extracted inside VamRequestEnricher is null ? : {} ", (null == site));

            if (null != site)
                request.setSite(site);

            if (null == site || !(site.getStatus() == StatusIdEnum.Active.getCode())) {
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
                this.logger.error("Requesting site from Valuemaker is not fit or is not found in cache . siteid: {} ", siteIdFromBidRequest);
                return request;
            }

            /***********************Detect handset of the request.Use the user agent from bid request.*******************/
            /***********************************DETECT HANDSET BY USERAGENT**********************************************/
            String userAgent = null;
            BidRequestDeviceDTO vamBidRequestDeviceDTO = vamBidRequestParentNodeDTO.getBidRequestDevice();

            if (null != vamBidRequestDeviceDTO) {
                userAgent = vamBidRequestDeviceDTO.getDeviceUserAgent();
            }

            if (null == userAgent)
                throw new Exception("User Agent absent in bidrequest inside VamRequestEnricher, cannot proceed....");

            HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);

            if (null == handsetMasterData) {
                this.logger.warn("Device detection failed inside VamRequestEnricher, proceeding with  undetected handset,{}", userAgent);
            } else {
                logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());
                if (handsetMasterData.isBot()) {
                    this.logger.error("Device detected is BOT inside VamRequestEnricher, cannot proceed further");
                    request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
                    return request;
                }
            }
            request.setHandsetMasterData(handsetMasterData);
            /************************************Done detecting handset****************************************************/

            /*******************************DETECT COUNTRY CARRIER USING MNC MCC or IP*****************************/
            //ip非必填
            String ip = vamBidRequestDeviceDTO.getIpV4AddressClosestToDevice();
            InternetServiceProvider internetServiceProvider = null;
            Country country = null;
            if (null != ip) {
                request.setIpAddressUsedForDetection(ip);
                country = findCountry(ip);
                internetServiceProvider = findCarrierEntity(ip);
                request.setCountry(country);
                request.setInternetServiceProvider(internetServiceProvider);
            }
            BidRequestGeoDTO bidRequestGeoDTO = vamBidRequestDeviceDTO.getGeoObject();

            String mncMccCode = vamBidRequestDeviceDTO.getCarrier();
            CountryIspUiDataUsingMccMnc countryIspUiDataUsingMccMnc = null;
            logger.debug("MCC MNC codes combination received from cloudCross bid request is {} ", mncMccCode);
            //use mnc mcc,if not
            if (null != mncMccCode) {
                countryIspUiDataUsingMccMnc = mncMccCountryISPDetectionCache.query(mncMccCode);
            }
            //if mnc mcc not present or location not detected use ip address to find location.
            if (null != countryIspUiDataUsingMccMnc && null != countryIspUiDataUsingMccMnc.getCountryUiId() && null != countryIspUiDataUsingMccMnc.getIspUiId()) {
                request.setCountryUserInterfaceId(countryIspUiDataUsingMccMnc.getCountryUiId());
                request.setCarrierUserInterfaceId(countryIspUiDataUsingMccMnc.getIspUiId());
                logger.debug("Using MCC-MNC combination: {} , countryUiId: {} , ispUiId: {} ", mncMccCode, countryIspUiDataUsingMccMnc.getCountryUiId(), countryIspUiDataUsingMccMnc.getIspUiId());
            }

            request.setConnectionType(ConnectionType.getEnum(vamBidRequestDeviceDTO.getConnectionType().shortValue()));

            /************************Location detection completes,country,isp,connectiontype******************************/

            /************************** Set lat-long if available in the request **********************************/
            if (null != bidRequestGeoDTO && null != bidRequestGeoDTO.getGeoLongitude() && null != bidRequestGeoDTO.getGeoLatitude()) {
                request.setRequestingLongitudeValue(bidRequestGeoDTO.getGeoLongitude().doubleValue());
                request.setRequestingLatitudeValue(bidRequestGeoDTO.getGeoLatitude().doubleValue());
            }
            /******************************************************************************************************/

            request.setInterstitialBidRequest(false);

            /**********Set width and height array to the Request object.Also set if interstitial or not.******************/
            BidRequestImpressionDTO bidRequestImpressionDTO = vamBidRequestParentNodeDTO.getBidRequestImpressionArray()[0];

            request.setStrictBannerSizeForImpressionIdOfRTBExchange(bidRequestImpressionDTO.getBidRequestImpressionId(), true);

            /***************************Set extra available parameters*********************************************/
            populateRequestObjectForExtraParameters(vamBidRequestParentNodeDTO, request);
            /******************************************************************************************************/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    private Country findCountry(String ip) {
        Country country = null;

        try {
            country = this.countryDetectionCache.findCountryForIpAddress(ip);
        } catch (Exception e) {
            logger.error("Exception inside VamRequestEnricher in fetching country ", e);
        }

        return country;
    }

    private InternetServiceProvider findCarrierEntity(String ip) {
        InternetServiceProvider internetServiceProvider = null;

        try {
            internetServiceProvider = this.ispDetectionCache.fetchISPForIpAddress(ip);
        } catch (Exception e) {
            logger.error("Exception inside VamRequestEnricher in fetching isp ", e);
        }

        return internetServiceProvider;
    }

    /**
     * All attributes must be set at runtime except hygiene ,which
     * should be taken from the entity as present in the database.
     */
    private Site fetchSiteEntityForVamRequest(Request request, String siteIdFromBidRequest) {
        Site site = this.siteCache.query(siteIdFromBidRequest);

        if (null == site)
            return null;

        VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO = (VamBidRequestParentNodeDTO) request.getBidRequest().getBidRequestParentNodeDTO();

        BidRequestSiteDTO vamBidRequestSiteDTO = vamBidRequestParentNodeDTO.getBidRequestSite();
        BidRequestAppDTO vamBidRequestAppDTO = vamBidRequestParentNodeDTO.getBidRequestApp();

        Short sitePlatform;
        String applicationId = null;

        String appOrSite = vamBidRequestParentNodeDTO.getAppOrSite();
        if (appOrSite != null || appOrSite.equals("app")) {
            sitePlatform = SITE_PLATFORM.APP.getPlatform();
            applicationId = vamBidRequestAppDTO.getApplicationIdOnExchange();
        } else {
            sitePlatform = SITE_PLATFORM.WAP.getPlatform();
        }

        String siteUrl = null != vamBidRequestSiteDTO ? vamBidRequestSiteDTO.getSitePageURL() : null;
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
                        site.getPublisherIncId(), site.getPublisherId(), siteUrl,
                        null, null, true,
                        site.getHygieneList(), site.getOptInHygieneList(), null,
                        false, sitePlatform, site.getStatus(),
                        vamBidRequestParentNodeDTO.getBlockedAdvertiserDomainsForBidRequest(),
                        false, isMarkedForDeletion, lastModifiedOn, false, null
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

        if (null != vamBidRequestSiteDTO) {
            externalSupplyUrl = vamBidRequestSiteDTO.getSitePageURL();
            externalSupplyId = vamBidRequestSiteDTO.getSiteIdOnExchange();
            externalSupplyName = vamBidRequestSiteDTO.getSiteName();
            externalSupplyDomain = vamBidRequestSiteDTO.getSiteDomain();
            externalAppPageUrl = vamBidRequestSiteDTO.getSitePageURL();
        } else if (null != vamBidRequestAppDTO) {
            externalSupplyUrl = vamBidRequestAppDTO.getApplicationStoreUrl();
            externalSupplyId = vamBidRequestAppDTO.getApplicationIdOnExchange();
            externalSupplyName = vamBidRequestAppDTO.getApplicationName();
            externalSupplyDomain = vamBidRequestAppDTO.getApplicationDomain();
            externalAppVersion = vamBidRequestAppDTO.getApplicationVersion();
            if (vamBidRequestAppDTO.getContentCategoriesApplication() != null) {
                externalCategories = vamBidRequestAppDTO.getContentCategoriesApplication();
            }
            externalAppBundle = vamBidRequestAppDTO.getApplicationBundleName();
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
     *
     * @param vamBidRequestParentNodeDTO
     * @param request
     */
    private void populateRequestObjectForExtraParameters(VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO,
                                                         Request request) {
        BidRequestUserDTO vamBidRequestUserDTO = vamBidRequestParentNodeDTO.getBidRequestUser();
        BidRequestDeviceDTO vamBidRequestDeviceDTO = vamBidRequestParentNodeDTO.getBidRequestDevice();

        EnricherUtils.populateUserIdsFromBidRequestDeviceDTO(vamBidRequestDeviceDTO, request);
        EnricherUtils.populateUserIdsFromBidRequestUserDTO(vamBidRequestUserDTO, request);
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if (externalUserIds == null || externalUserIds.size() == 0) {
            logger.debug("External user ids empty or not present");
        } else {
            logger.debug("External user ids found.");
            for (ExternalUserId externalUserId : externalUserIds) {
                logger.debug("\texternal user id = {}", externalUserId);
            }
        }

        if (null != vamBidRequestDeviceDTO) {
            request.setUserAgent(vamBidRequestDeviceDTO.getDeviceUserAgent());
            if (vamBidRequestDeviceDTO.getIpV4AddressClosestToDevice() != null) {
                request.setIpAddressUsedForDetection(vamBidRequestDeviceDTO.getIpV4AddressClosestToDevice());
            } else if (vamBidRequestDeviceDTO.getIpV6Address() != null) {
                request.setIpAddressUsedForDetection(vamBidRequestDeviceDTO.getIpV6Address());
            }
        }
    }
}
