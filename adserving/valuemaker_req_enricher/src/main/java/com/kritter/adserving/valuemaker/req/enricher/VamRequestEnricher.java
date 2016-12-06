package com.kritter.adserving.valuemaker.req.enricher;


import RTB.VamRealtimeBidding;
import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.*;
import com.kritter.common.caches.mma_cache.MMACache;
import com.kritter.common.caches.mma_cache.entity.MMACacheEntity;
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
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.valuemaker.reader_v20160817.entity.VamBidRequestParentNodeDTO;
import com.kritter.valuemaker.reader_v20160817.reader.VamBidRequestReader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * This class enriches valuemaker request to enable DSP for choosing right ads.
 */
public class VamRequestEnricher implements RTBExchangeRequestReader {
    private Logger logger;
    private VamBidRequestReader vamBidRequestReader;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private MMACache mMACache;

    private static final String CTRL_A = String.valueOf((char) 1);

    public VamRequestEnricher(String loggerName,
                              VamBidRequestReader vamBidRequestReader,
                              SiteCache siteCache,
                              HandsetDetectionProvider handsetDetectionProvider,
                              CountryDetectionCache countryDetectionCache,
                              MMACache mMACache

    ) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.vamBidRequestReader = vamBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.mMACache = mMACache;
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger,
                                            Logger bidRequestLogger,
                                            boolean logBidRequest,
                                            String publisherId) throws Exception {

        try {

            IBidRequest iBidRequest = this.vamBidRequestReader.convertBidRequestPayloadToBusinessObject(httpServletRequest.getInputStream());

            VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO = (VamBidRequestParentNodeDTO) iBidRequest.getBidRequestParentNodeDTO();
            convertPrice(vamBidRequestParentNodeDTO);

            Request request = new Request(requestId, INVENTORY_SOURCE.RTB_EXCHANGE);
            request.setWriteResponseInsideExchangeAdaptor(true);//response body has to be written inside
            request.setBidRequest(iBidRequest);

            //fetch site object
            String siteIdFromBidRequest = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), "/");
            logger.debug("SiteId received from bid request URL: {} ", siteIdFromBidRequest);

            //转换bcat
            Integer[] mmaIndustryCodes1 = null;
            String[] mmaIndustryCodes2 = null;
            List<Integer> battr = vamBidRequestParentNodeDTO.getBattr();
            if (battr != null && battr.size() != 0) {
                mmaIndustryCodes1 = new Integer[battr.size()];
                mmaIndustryCodes2 = new String[battr.size()];
                Site s = this.siteCache.query(siteIdFromBidRequest);
                if (s != null) {
                    for (int i = 0; i < battr.size(); i++) {
                        MMACacheEntity mmaCacheEntity = mMACache.query(s.getPublisherIncId() + CTRL_A + battr.get(i));
                        if (mmaCacheEntity != null) {
                            mmaIndustryCodes1[i] = mmaCacheEntity.getUi_id();
                            mmaIndustryCodes2[i] = String.valueOf(mmaCacheEntity.getUi_id());
                        }
                    }
                    vamBidRequestParentNodeDTO.setBlockedAdvertiserCategoriesForBidRequest(mmaIndustryCodes2);
                }
            }

            Site site = fetchSiteEntityForVamRequest(request, siteIdFromBidRequest, mmaIndustryCodes1);
            logger.debug("Site extracted inside VamRequestEnricher is null ? : {} ", (null == site));


            if (null != site)
                request.setSite(site);

            if (null == site || !(site.getStatus() == StatusIdEnum.Active.getCode())) {
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
                this.logger.error("Requesting site from Valuemaker is not fit or is not found in cache . siteid: {} ", siteIdFromBidRequest);
                return request;
            }

            if (logBidRequest) {
                StringBuffer sb = new StringBuffer();
                sb.append(publisherId);
                sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
                sb.append(requestId);
                sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
                sb.append(vamBidRequestParentNodeDTO.getExtensionObject().toString());
                bidRequestLogger.debug(sb.toString());
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
            Country country = null;
            if (null != ip) {
                request.setIpAddressUsedForDetection(ip);
                country = findCountry(ip);
                request.setCountry(country);
            }
            BidRequestGeoDTO bidRequestGeoDTO = vamBidRequestDeviceDTO.getGeoObject();

            if (vamBidRequestDeviceDTO.getConnectionType() != null) {
                ConnectionType connectionType = ConnectionType.getEnum(vamBidRequestDeviceDTO.getConnectionType().shortValue());
                if (connectionType != null) {
                    request.setConnectionType(connectionType);
                } else {
                    request.setConnectionType(ConnectionType.UNKNOWN);
                }
            } else {
                request.setConnectionType(ConnectionType.UNKNOWN);
            }

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

            return request;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

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

    /**
     * All attributes must be set at runtime except hygiene ,which
     * should be taken from the entity as present in the database.
     */
    private Site fetchSiteEntityForVamRequest(Request request, String siteIdFromBidRequest, Integer[] mmaIndustryCodes) {
        Site site = this.siteCache.query(siteIdFromBidRequest);

        if (null == site)
            return null;

        VamBidRequestParentNodeDTO vamBidRequestParentNodeDTO = (VamBidRequestParentNodeDTO) request.getBidRequest().getBidRequestParentNodeDTO();

        BidRequestSiteDTO vamBidRequestSiteDTO = vamBidRequestParentNodeDTO.getBidRequestSite();
        BidRequestAppDTO vamBidRequestAppDTO = vamBidRequestParentNodeDTO.getBidRequestApp();

        Short sitePlatform;
        String applicationId = null;

        SITE_PLATFORM sp = vamBidRequestParentNodeDTO.getSitePlatform();
        if (sp != null && sp.getPlatform() == SITE_PLATFORM.APP.getPlatform()) {
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

        Double bidFloorValue = vamBidRequestParentNodeDTO.getBidRequestImpressionArray()[0].getBidFloorPrice();
        double ecpmFloorValue = bidFloorValue == null ? 0.0 : bidFloorValue;

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

        if (sp != null && sp.getPlatform() == SITE_PLATFORM.APP.getPlatform()) { //app
            externalSupplyUrl = vamBidRequestAppDTO.getApplicationStoreUrl();
            externalSupplyId = vamBidRequestAppDTO.getApplicationBundleName();
            externalSupplyName = vamBidRequestAppDTO.getApplicationName();
            externalSupplyDomain = vamBidRequestAppDTO.getApplicationBundleName();
            externalAppVersion = vamBidRequestAppDTO.getApplicationVersion();
            if (vamBidRequestAppDTO.getContentCategoriesApplication() != null) {
                externalCategories = vamBidRequestAppDTO.getContentCategoriesApplication();
            }
            externalAppBundle = vamBidRequestAppDTO.getApplicationBundleName();
        } else {
            externalSupplyUrl = vamBidRequestSiteDTO.getSitePageURL();
            externalSupplyId = vamBidRequestSiteDTO.getSiteIdOnExchange();
            externalSupplyName = vamBidRequestSiteDTO.getSiteName();
            externalSupplyDomain = vamBidRequestSiteDTO.getSiteDomain();
            externalAppPageUrl = vamBidRequestSiteDTO.getSitePageURL();
        }

        siteToUse.setExternalSupplyDomain(externalSupplyDomain);
        siteToUse.setExternalSupplyId(externalSupplyId);
        siteToUse.setExternalSupplyName(externalSupplyName);
        siteToUse.setExternalSupplyUrl(externalSupplyUrl);
        siteToUse.setExternalAppVersion(externalAppVersion);
        siteToUse.setExternalPageUrl(externalAppPageUrl);
        siteToUse.setExternalAppBundle(externalAppBundle);
        siteToUse.setExternalCategories(externalCategories);
        siteToUse.setMmaindustrCodeExclude(true);
        siteToUse.setMmaindustryCode(mmaIndustryCodes);
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


    private void convertPrice(VamBidRequestParentNodeDTO bidRequestParentNodeDTO) {
        VamRealtimeBidding.VamRequest vamRequest = (VamRealtimeBidding.VamRequest) bidRequestParentNodeDTO.getExtensionObject();
        BidRequestImpressionDTO impressionDTO = bidRequestParentNodeDTO.getBidRequestImpressionArray()[0];

        double price = 0.0;
        if (vamRequest.getDisplayCount() > 0 && vamRequest.getDisplay(0) != null && vamRequest.getDisplay(0).hasBidfloor()) {
            price = (double) vamRequest.getDisplay(0).getBidfloor() / 100;
        } else if (vamRequest.hasVamVideo() && vamRequest.getVamVideo().hasBidfloor()) {
            price = (double) vamRequest.getVamVideo().getBidfloor() / 100;
        } else if (vamRequest.hasVamMobile() && vamRequest.getVamMobile().hasBidfloor()) {
            price = (double) vamRequest.getVamMobile().getBidfloor() / 100;
        } else if (vamRequest.hasVamMobileVideo() && vamRequest.getVamMobileVideo().hasBidfloor()) {
            price = (double) vamRequest.getVamMobileVideo().getBidfloor() / 100;
        }

        impressionDTO.setBidFloorPrice(price);
    }

}
