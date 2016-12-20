package com.kritter.adexchange.cloudcross.enricher;

import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.bidreqres.entity.cloudcross4_4_4.*;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestGeoDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.common.caches.mma_cache.MMACache;
import com.kritter.common.caches.mma_cache.entity.MMACacheEntity;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ExternalUserIdType;
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
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


/**
 * 将云联的请求参数信息转成我们自己的request对象
 * Created by hamlin on 7/5/16.
 */
public class CloudCrossRequestEnricher4_4_4 implements RTBExchangeRequestReader {
    private static final String CTRL_A = String.valueOf((char) 1);
    private Logger logger;
    private IBidRequestReader cloudCrossBidRequestReader;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private MMACache mmaCache;
    private static final String ENCODING = "UTF-8";

    public CloudCrossRequestEnricher4_4_4(String loggerName,
                                          IBidRequestReader cloudCrossBidRequestReader,
                                          SiteCache siteCache,
                                          HandsetDetectionProvider handsetDetectionProvider,
                                          CountryDetectionCache countryDetectionCache,
                                          MMACache mmaCache
    ) {
        this.logger = LoggerFactory.getLogger(loggerName);
        this.cloudCrossBidRequestReader = cloudCrossBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.mmaCache = mmaCache;
    }


    @Override
    public Request validateAndEnrichRequest(String requestId, HttpServletRequest httpServletRequest, Logger logger, Logger bidRequestLogger, boolean logBidRequest, String publisherId) throws Exception {
        {
            StringWriter stringWriter = new StringWriter();

            logger.debug("Request Method inside validateAndEnrichRequest of CloudCrossRequestEnricher is {} ",
                    httpServletRequest.getMethod());

            String encodingToUse = httpServletRequest.getCharacterEncoding();
            if (null == encodingToUse)
                encodingToUse = ENCODING;

            logger.debug("ContentLength: {}, ContentType: {} , RequestCharset: {}, UsedCharset: {} ",
                    httpServletRequest.getContentLength(),
                    httpServletRequest.getContentType(),
                    httpServletRequest.getCharacterEncoding(),
                    encodingToUse);


            IOUtils.copy(httpServletRequest.getInputStream(), stringWriter, encodingToUse);

            String bidRequestPayLoadReceived = stringWriter.toString();

            if (StringUtils.isEmpty(bidRequestPayLoadReceived)) {
                logger.error("The bidrequest payload received from CloudCross RTB Exchange is null inside validateAndEnrichRequest of CloudCrossRequestEnricher,cannot enrich bid request...");
                return null;
            }

            if (logBidRequest) {
                //noinspection StringBufferReplaceableByString
                StringBuilder sb = new StringBuilder();
                sb.append(publisherId);
                sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
                sb.append(requestId);
                sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
                sb.append(bidRequestPayLoadReceived);
                bidRequestLogger.debug(sb.toString());
            }

            IBidRequest bidRequest;

            try {
                bidRequest = cloudCrossBidRequestReader.
                        convertBidRequestPayloadToBusinessObject(bidRequestPayLoadReceived);
            } catch (Exception e) {
                logger.error("Exception in reading cloudCross bid request ", e);
                logger.error("Exception reading bid request for request id: {} ", requestId);
                return null;
            }

            //if bid request is null throw exception
            if (null == bidRequest)
                throw new Exception("BidRequest could not be read from cloudCross ad-exchange...Payload being: " +
                        bidRequestPayLoadReceived);

            cloudCrossBidRequestReader.validateBidRequestForMandatoryParameters(bidRequest);

            logger.debug("RequestIdByDSP: {} ", bidRequest.getUniqueInternalRequestId());

            //if flow comes here this means everything is correct in the bid request.
            //now populate request object for internal working.
            /********************************DETECT SITE BY REQUEST SITEID****************************************/
            Request request = new Request(requestId, INVENTORY_SOURCE.RTB_EXCHANGE);
            request.setBidRequest(bidRequest);

            String siteIdFromBidRequest = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), "/");

            logger.debug("SiteId received from bid request URL: {} ", siteIdFromBidRequest);

            Site site = fetchSiteEntityForCloudCrossRequest(request, siteIdFromBidRequest);

            if (null == site || !(site.getStatus() == StatusIdEnum.Active.getCode())) {
                request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
                this.logger.error("Requesting site is not fit or is not found in cache . siteid: {}", siteIdFromBidRequest);
                return request;
            }

            request.setSite(site);

            CloudCrossBidRequestParentNodeDTO cloudCrossBidRequestParentNodeDTO =
                    (CloudCrossBidRequestParentNodeDTO) request.getBidRequest().getBidRequestParentNodeDTO();


            /******************************DETECT HANDSET BY USERAGENT**********************************************/
            String userAgent = null;
            CloudCrossBidRequestDeviceDTO cloudCrossBidRequestDeviceDTO =
                    cloudCrossBidRequestParentNodeDTO.getCloudCrossBidRequestDeviceDTO();

            if (null != cloudCrossBidRequestDeviceDTO) {
                userAgent = cloudCrossBidRequestDeviceDTO.getDeviceUserAgent();
            }

            HandsetMasterData handsetMasterData = null;
            if (userAgent != null && !userAgent.isEmpty()) {
                handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);
            }

            if (null == handsetMasterData) {
                this.logger.debug("Device detection failed inside CloudCrossRequestEnricher, proceeding with  undetected handset");
            } else {
                logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());
                if (handsetMasterData.isBot()) {
                    this.logger.error("Device detected is BOT inside CloudCrossRequestEnricher, cannot proceed further");
                    request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
                    return request;
                }
            }
            request.setHandsetMasterData(handsetMasterData);


            String ip = cloudCrossBidRequestDeviceDTO != null ? cloudCrossBidRequestDeviceDTO.getIpV4AddressClosestToDevice() : null;
            if (StringUtils.isEmpty(ip)) {
                ip = cloudCrossBidRequestDeviceDTO != null ? cloudCrossBidRequestDeviceDTO.getIpV6Address() : null;
                if (StringUtils.isEmpty(ip)) {
                    logger.debug("Country and InternetServiceProvider could not be detected inside YoukuRequestEnricher as mnc-mcc lookup failed as well as ip address not present...");
                }
            }
            if (StringUtils.isNotEmpty(ip)) {
                request.setIpAddressUsedForDetection(ip);
                request.setCountry(findCountry(ip));
            }

            Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
            if (externalUserIds == null) {
                externalUserIds = new HashSet<>();
                request.setExternalUserIds(externalUserIds);
            }

            Integer siteIncId = request.getSite().getSiteIncId();
            String uuidType = cloudCrossBidRequestDeviceDTO.getUuidType();
            String uuid = cloudCrossBidRequestDeviceDTO.getUuid();
            if (!StringUtils.isEmpty(uuidType)) {
                switch (uuidType) {
                    case "mac":
                        if (!StringUtils.isEmpty(uuid)) {
                            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MAC, siteIncId, uuid));
                            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MAC_MD5_DEVICE_ID, siteIncId, getMD5(uuid)));
                        }
                        break;
                    case "idfa":
                        if (!StringUtils.isEmpty(uuid))
                            externalUserIds.add(new ExternalUserId(ExternalUserIdType.IFA_USER_ID, siteIncId, uuid));
                        break;
                    case "imei":
                        if (!StringUtils.isEmpty(uuid))
                            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, siteIncId, getMD5(uuid)));
                        externalUserIds.add(new ExternalUserId(ExternalUserIdType.DEVICE_ID, siteIncId, uuid));
                        break;
                }
            }


            /******************************************* ip extraction and  connection type detection ends***************/

            BidRequestImpressionDTO[] impressionArray = cloudCrossBidRequestParentNodeDTO.getBidRequestImpressionArray();
            if (impressionArray != null && impressionArray.length > 0) {
                for (int i = 0; i < impressionArray.length; i++) {
                    Double bidFloorPrice = impressionArray[i].getBidFloorPrice();
                    impressionArray[i].setBidFloorPrice(bidFloorPrice == null ? 0 : bidFloorPrice.doubleValue() / 100);
                }

            }

            /************************** Set lat-long if available in the request **********************************/
            BidRequestGeoDTO bidRequestGeoDTO = cloudCrossBidRequestDeviceDTO != null ? cloudCrossBidRequestDeviceDTO.getGeoObject() : null;
            if (null != bidRequestGeoDTO &&
                    null != bidRequestGeoDTO.getGeoLongitude() &&
                    null != bidRequestGeoDTO.getGeoLatitude()) {
                request.setRequestingLongitudeValue(bidRequestGeoDTO.getGeoLongitude().doubleValue());
                request.setRequestingLatitudeValue(bidRequestGeoDTO.getGeoLatitude().doubleValue());
            }
            /******************************************************************************************************/

            /***************************Set extra available parameters*********************************************/
            populateRequestObjectForExtraParameters(cloudCrossBidRequestParentNodeDTO, request);
            /******************************************************************************************************/

            return request;
        }
    }

    private void populateRequestObjectForExtraParameters(CloudCrossBidRequestParentNodeDTO cloudCrossBidRequestParentNodeDTO, Request request) {
        CloudCrossBidRequestUserDTO cloudCrossBidRequestUserDTO = cloudCrossBidRequestParentNodeDTO.getCloudCrossBidRequestUserDTO();
        CloudCrossBidRequestDeviceDTO cloudCrossBidRequestDeviceDTO = cloudCrossBidRequestParentNodeDTO.getCloudCrossBidRequestDeviceDTO();

        EnricherUtils.populateUserIdsFromBidRequestDeviceDTO(cloudCrossBidRequestDeviceDTO, request);
        EnricherUtils.populateUserIdsFromBidRequestUserDTO(cloudCrossBidRequestUserDTO, request);
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if (externalUserIds == null || externalUserIds.isEmpty()) {
            logger.debug("External user ids empty or not present");
        } else {
            logger.debug("External user ids found.");
            for (ExternalUserId externalUserId : externalUserIds) {
                logger.debug("\texternal user id = {}", externalUserId);
            }
        }

        if (null != cloudCrossBidRequestDeviceDTO) {
            request.setMccMncCodeCouple(cloudCrossBidRequestDeviceDTO.getCarrier());
            request.setUserAgent(cloudCrossBidRequestDeviceDTO.getDeviceUserAgent());
            request.setIpAddressUsedForDetection(cloudCrossBidRequestDeviceDTO.getIpV4AddressClosestToDevice());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Site fetchSiteEntityForCloudCrossRequest(Request request, String siteIdFromBidRequest) {
        Site site = this.siteCache.query(siteIdFromBidRequest);

        CloudCrossBidRequestParentNodeDTO cloudCrossBidRequestParentNodeDTO =
                (CloudCrossBidRequestParentNodeDTO) request.getBidRequest().getBidRequestParentNodeDTO();

        CloudCrossBidRequestSiteDTO cloudCrossBidRequestSiteDTO = cloudCrossBidRequestParentNodeDTO.getCloudCrossBidRequestSiteDTO();
        CloudCrossBidRequestAppDTO cloudCrossBidRequestAppDTO = cloudCrossBidRequestParentNodeDTO.getCloudCrossBidRequestAppDTO();

        String siteUrl = null != cloudCrossBidRequestSiteDTO ? cloudCrossBidRequestSiteDTO.getSitePageURL() : null;
        Short[] categoriesArray = null;
        Short sitePlatform;
        String applicationId = null;
        if (null != cloudCrossBidRequestSiteDTO) {
            sitePlatform = SITE_PLATFORM.WAP.getPlatform();
        } else if (null != cloudCrossBidRequestAppDTO) {
            sitePlatform = SITE_PLATFORM.APP.getPlatform();
            applicationId = cloudCrossBidRequestAppDTO.getApplicationIdOnExchange();
        } else {
            logger.error("Site/App not found in request, site/app both not present inside CloudCrossRequestEnricher...aborting request....");
            return null;
        }
        Integer[] mmaCatgories = null;
        // cat, app category
        if (cloudCrossBidRequestAppDTO != null) {
            String[] cat = cloudCrossBidRequestAppDTO.getContentCategoriesApplication();
            mmaCatgories = buildMMAFromMMACache(site.getPublisherIncId(), mmaCatgories, cat);
        }

        // bcat
        Integer[] mmaIndustryCodes = null;
        if (cloudCrossBidRequestParentNodeDTO != null) {
            String[] bcat = cloudCrossBidRequestParentNodeDTO.getBlockedAdvertiserCategoriesForBidRequest();
            mmaIndustryCodes = buildMMAFromMMACache(site.getPublisherIncId(), mmaIndustryCodes, bcat);
        }


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
        if (cloudCrossBidRequestParentNodeDTO != null
                && cloudCrossBidRequestParentNodeDTO.getBidRequestImpressionArray() != null
                && cloudCrossBidRequestParentNodeDTO.getBidRequestImpressionArray().length > 0
                && cloudCrossBidRequestParentNodeDTO.getBidRequestImpressionArray()[0] != null) {
            ecpmFloorValue = cloudCrossBidRequestParentNodeDTO.getBidRequestImpressionArray()[0].getBidFloorPrice();
        }

        //Create a new site and set all attributes, take hygiene from the one found in cache.
        Site siteToUse = new Site.SiteEntityBuilder
                (
                        site.getSiteIncId(), site.getSiteGuid(), site.getName(),
                        site.getPublisherIncId(), site.getPublisherId(), siteUrl,
                        categoriesArray, null, true,
                        site.getHygieneList(), site.getOptInHygieneList(), creativeAttributesIncExc,
                        isCreativeAttributesForExclusion, sitePlatform, site.getStatus(),
                        cloudCrossBidRequestParentNodeDTO.getBlockedAdvertiserDomainsForBidRequest(),
                        excludeDefinedAdDomains, isMarkedForDeletion, lastModifiedOn, false, null
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

        if (null != cloudCrossBidRequestSiteDTO) {
            externalSupplyUrl = cloudCrossBidRequestSiteDTO.getSitePageURL();
            externalSupplyId = cloudCrossBidRequestSiteDTO.getSiteIdOnExchange();
            externalSupplyName = cloudCrossBidRequestSiteDTO.getSiteName();
            externalSupplyDomain = cloudCrossBidRequestSiteDTO.getSiteDomain();
            externalAppPageUrl = cloudCrossBidRequestSiteDTO.getSitePageURL();
        } else if (null != cloudCrossBidRequestAppDTO) {
            externalSupplyUrl = cloudCrossBidRequestAppDTO.getApplicationStoreUrl();
            externalSupplyId = cloudCrossBidRequestAppDTO.getApplicationIdOnExchange();
            externalSupplyName = cloudCrossBidRequestAppDTO.getApplicationName();
            externalSupplyDomain = cloudCrossBidRequestAppDTO.getApplicationDomain();
            externalAppVersion = cloudCrossBidRequestAppDTO.getApplicationVersion();
            if (cloudCrossBidRequestAppDTO.getContentCategoriesApplication() != null) {
                externalCategories = cloudCrossBidRequestAppDTO.getContentCategoriesApplication();
            }
            externalAppBundle = cloudCrossBidRequestAppDTO.getApplicationBundleName();
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
        siteToUse.setMmaCatgories(mmaCatgories);
        /******************************************************************************************************/

        return siteToUse;
    }

    private Integer[] buildMMAFromMMACache(Integer publisherId, Integer[] mmaCatgories, String[] cat) {
        if (cat != null && cat.length > 0) {
            int length = cat.length;
            mmaCatgories = new Integer[length];
            for (int i = 0; i < length; i++) {
                MMACacheEntity mmaCacheEntity = mmaCache.query(publisherId + CTRL_A + Integer.valueOf(cat[i]));
                mmaCatgories[i] = mmaCacheEntity == null ? 0 : mmaCacheEntity.getUi_id();
            }
        }
        return mmaCatgories;
    }

    public String getMD5(String s) {

        if (s == null) {
            return s;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String digest = getDigest(s, md);
            return digest;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }


    }

    public String getDigest(String s, MessageDigest md)
            throws Exception {

        md.reset();
        byte[] digest = md.digest(s.getBytes());
        String result = new String(Hex.encodeHex(digest));
        return result;
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
}
