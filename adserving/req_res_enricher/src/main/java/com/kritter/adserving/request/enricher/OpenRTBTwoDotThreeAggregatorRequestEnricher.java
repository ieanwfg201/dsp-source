package com.kritter.adserving.request.enricher;

import com.kritter.entity.reqres.entity.Request;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestAppDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestDeviceDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestImpressionVideoObjectDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestParentNodeDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestSiteDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidRequestUserDTO;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.DefaultCurrency;
import com.kritter.constants.FormatterIds;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.OpenRTBVersion;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.device.common.HandsetDetectionProvider;
import com.kritter.device.common.entity.HandsetMasterData;
import com.kritter.geo.common.entity.Country;
import com.kritter.geo.common.entity.InternetServiceProvider;
import com.kritter.geo.common.entity.reader.IConnectionTypeDetectionCache;
import com.kritter.geo.common.entity.reader.CountryDetectionCache;
import com.kritter.geo.common.entity.reader.ISPDetectionCache;
import com.kritter.req_logging.ReqLoggingCache;
import com.kritter.req_logging.ReqLoggingCacheEntity;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.entity.video_supply_props.VideoSupplyProps;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is used to enrich aggregator requests who are integrated via
 * the server to server api calls hand follows OpenRTB 2.3 protocol
 */
public class OpenRTBTwoDotThreeAggregatorRequestEnricher implements RequestEnricher
{
    private Logger logger;
    private Logger bidRequestLogger;
    private ReqLoggingCache reqLoggingCache;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
    private IConnectionTypeDetectionCache connectionTypeDetectionCache;
    private static final String ENCODING = "UTF-8";
    private ObjectMapper objectMapper;

    public OpenRTBTwoDotThreeAggregatorRequestEnricher(
                                     String loggerName,
                                     String bidRequestLoggerName,
                                     ReqLoggingCache reqLoggingCache,
                                     SiteCache siteCache,
                                     HandsetDetectionProvider handsetDetectionProvider,
                                     CountryDetectionCache countryDetectionCache,
                                     ISPDetectionCache ispDetectionCache,
                                     IConnectionTypeDetectionCache connectionTypeDetectionCache
                                     )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.bidRequestLogger = LogManager.getLogger(bidRequestLoggerName);
        this.reqLoggingCache = reqLoggingCache;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
        this.connectionTypeDetectionCache = connectionTypeDetectionCache;
        this.objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    @Override
    public Request validateAndEnrichRequest(String requestId,
                                            HttpServletRequest httpServletRequest,
                                            Logger logger) throws Exception
    {
        this.logger.info("Inside validateAndEnrichRequest() of OpenRTBTwoDotThreeAggregatorRequestEnricher");
        String requestURI = httpServletRequest.getRequestURI();
        this.logger.debug("Request URI inside OpenRTBTwoDotThreeAggregatorRequestEnricher is: {} ", requestURI);

        String requestURIParts[] = requestURI.split(ApplicationGeneralUtils.URI_PATH_SEPARATOR);
        Request request = new Request(requestId, INVENTORY_SOURCE.OPENRTB_AGGREGATOR);


        if(requestURIParts.length < 2){
        	logger.error("Invalid Exchange Request, requestURI being: {} , should have " +
                                "been e.g: /b209484c01754ef08c0e476b796fc081/a179484c01754ef08c0e476b796fc081 , " +
                                "where b209484c01754ef08c0e476b796fc081 is exchange " +
                                "identifier(publisher id) and last param is its site id.",requestURI );
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            return request;
        }

        //pick second last as the exchange identifier.
        String pubGuid = requestURIParts[requestURIParts.length-2];
        String siteGuid = requestURIParts[requestURIParts.length-1];
        if(pubGuid==null || siteGuid==null || pubGuid.isEmpty() ||siteGuid.isEmpty()){
        	logger.error("Site{} or Pub {} Incorrect",siteGuid,pubGuid);
        	request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            return request;
        }
        StringWriter stringWriter = new StringWriter();

        logger.debug("Request Method inside validateAndEnrichRequest of OpenRTBTwoDotThreeAggregatorRequestEnricher is {} ",
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

        String adRequestPayLoadReceived = stringWriter.toString();
        
        ReqLoggingCacheEntity reqLoggingCacheEntity = reqLoggingCache.query(pubGuid);
        boolean logRequest = false;
        if(null != reqLoggingCacheEntity){
        	logRequest = reqLoggingCacheEntity.isEnable();
        }
        if(logRequest)
        {
            StringBuffer sb = new StringBuffer();
            sb.append(pubGuid);
            sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
            sb.append(requestId);
            sb.append(ApplicationGeneralUtils.EXCHANGE_BID_REQUEST_DELIM);
            sb.append(adRequestPayLoadReceived);
            bidRequestLogger.debug(sb.toString());
        }
        try{

        	BidRequestParentNodeDTO entity = objectMapper.readValue(adRequestPayLoadReceived, BidRequestParentNodeDTO.class);
        	if(entity == null){
            	logger.error("Site {} - Unable to deserialise post body-> {} ",siteGuid,adRequestPayLoadReceived);
            	request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
                return request;
        	}
        	entity.setBidRequestId(requestId);
        	String[] allowedCurrencies = new String[1];
        	allowedCurrencies[0] = DefaultCurrency.defaultCurrency.getName();
        	entity.setAuctionType(2);
        	entity.setAllowedCurrencies(allowedCurrencies);
        	Site site =fetchSiteEntity(siteGuid);
        	if(site==null || !(site.getStatus() == StatusIdEnum.Active.getCode())){
            	logger.error("Site {} not found in cache or inactive ",siteGuid);
        		request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
                return request;
        	}
        	String ip=null;
        	String userAgent=null;
        	if(entity.getBidRequestDevice() != null){
        		if(entity.getBidRequestDevice().getDeviceUserAgent() != null && !entity.getBidRequestDevice().getDeviceUserAgent().isEmpty()){
        			userAgent=entity.getBidRequestDevice().getDeviceUserAgent();
        		}else{
                	logger.error("Site {} UserAgentNotFound ",siteGuid);
            		request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_UNDETECTED);
                    return request;
        		}
        		if(entity.getBidRequestDevice().getIpV4AddressClosestToDevice() != null && !entity.getBidRequestDevice().getIpV4AddressClosestToDevice().isEmpty()){
        			ip=entity.getBidRequestDevice().getIpV4AddressClosestToDevice();
        		}else if(entity.getBidRequestDevice().getIpV6Address() != null && !entity.getBidRequestDevice().getIpV6Address().isEmpty()){
        			ip=entity.getBidRequestDevice().getIpV6Address() ;
        		}
        		if(entity.getBidRequestDevice().getGeoObject() != null &&
        				null != entity.getBidRequestDevice().getGeoObject().getGeoLatitude() &&
        				null != entity.getBidRequestDevice().getGeoObject().getGeoLongitude()){
                    request.setRequestingLongitudeValue(entity.getBidRequestDevice().getGeoObject().getGeoLongitude().doubleValue());
                    request.setRequestingLatitudeValue(entity.getBidRequestDevice().getGeoObject().getGeoLatitude().doubleValue());
        		}
        	}
        	request.setIpAddressUsedForDetection(ip);
        	request.setUserAgent(userAgent);
            HandsetMasterData handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);
            
            if(handsetMasterData != null)
            {
                logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());
                if(handsetMasterData.isBot()){
                	this.logger.error("Device detected is BOT inside OpenRTBTwoDotThreeAggregatorRequestEnricher, can not proceed further");
                	request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
                	return request;
                }
                int[] requiredWidths = new int[1];
                int[] requiredHeights = new int[1];
                requiredWidths[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionWidth();
                requiredHeights[0] = handsetMasterData.getHandsetCapabilityObject().getResolutionHeight();

                request.setRequestedSlotWidths(requiredWidths);
                request.setRequestedSlotHeights(requiredHeights);
           }
            request.setHandsetMasterData(handsetMasterData);
            if(connectionTypeDetectionCache != null && ip != null){
                request.setConnectionType(this.connectionTypeDetectionCache.getConnectionType(ip));
            }else {
                request.setConnectionType(ConnectionType.UNKNOWN);
            }
            Country country = findCountry(ip);
            InternetServiceProvider internetServiceProvider = findCarrierEntity(ip);
            request.setCountry(country);
            request.setInternetServiceProvider(internetServiceProvider);
            BidRequestUserDTO userEntity = entity.getBidRequestUser();
            BidRequestDeviceDTO deviceEntity = entity.getBidRequestDevice();
            Double bidFloor = 0.0;
            boolean isVideo=false;
            VideoSupplyProps videosupplyProps=null;
            if(null != entity.getBidRequestImpressionArray() && entity.getBidRequestImpressionArray().length > 0){
            	request.setRequestedNumberOfAds(1);/*NOTE only one ad allowed*/
            	BidRequestImpressionDTO impEntity = entity.getBidRequestImpressionArray()[0];
            	if(impEntity.getBidRequestImpressionId() == null || impEntity.getBidRequestImpressionId().isEmpty()){
            		impEntity.setBidRequestImpressionId(requestId+":1");
            	}
            	if(impEntity.getBidFloorPrice()!=null){
            		bidFloor=impEntity.getBidFloorPrice();
            		if(impEntity.getBidRequestImpressionVideoObject() != null){
            			isVideo=true;
            			videosupplyProps = getVideoSupplyProps(impEntity.getBidRequestImpressionVideoObject());
            			request.setResponseFormat(FormatterIds.VAST_FORMATTER_ID);
            		}
            	}
            }
            Site siteToUse = getSiteToUse(entity, site, bidFloor, isVideo,videosupplyProps);
            if(siteToUse ==null){
            	logger.error("Site {} Site to use is Null ",siteGuid);
        		request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
                return request;
            }
        	request.setSite(siteToUse);

            EnricherUtils.populateUserIdsFromBidRequestDeviceDTO(deviceEntity, request);
            EnricherUtils.populateUserIdsFromBidRequestUserDTO(userEntity, request);
            Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
            if(externalUserIds == null || externalUserIds.size() == 0) {
                logger.debug("External user ids empty or not present");
            } else {
                logger.debug("External user ids found.");
                for(ExternalUserId externalUserId : externalUserIds) {
                    logger.debug("\texternal user id = {}", externalUserId);
                }
            }
            request.setAggregatorOpenRTB(true);
            request.setAggregatorversion(OpenRTBVersion.VERSION_2_3);
            request.setOpenrtbObjTwoDotThree(entity);
        }catch(Exception e){
        	logger.error(e.getMessage(),e);
        	request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.REQUEST_MALFORMED);
            return request;
        }
        return request;
    }
    
    private Site getSiteToUse(BidRequestParentNodeDTO entity,Site siteIn,double ecpmFloorValue,
    		boolean isVideo, VideoSupplyProps videoSupplyProps)
    {
    	if(siteIn == null){
    		return null;
    	}
    	
        BidRequestSiteDTO siteEntity = entity.getBidRequestSite();
        BidRequestAppDTO appEntity = entity.getBidRequestApp();
        String siteUrl = null != siteEntity ? siteEntity.getSitePageURL() : null;
        Short sitePlatform;
        String applicationId = null;
        if(null != siteEntity){
            sitePlatform = SITE_PLATFORM.WAP.getPlatform();
        }else if(null != appEntity){
            sitePlatform = SITE_PLATFORM.APP.getPlatform();
        }else{
            logger.error("Site/App not found in request, site/app both not present inside YoukuRequestEnricher...aborting request....");
            return null;
        }
        boolean excludeDefinedAdDomains = true;
        boolean isMarkedForDeletion = false;
        Timestamp lastModifiedOn = new Timestamp(System.currentTimeMillis());
        Short appStoreId = 0;

        Site site = new Site.SiteEntityBuilder
                (
                		siteIn.getSiteIncId(), siteIn.getSiteGuid(), siteIn.getName(),
                		siteIn.getPublisherIncId(),siteIn.getPublisherId(),siteUrl,
                 null,null,true,
                 siteIn.getHygieneList(),siteIn.getOptInHygieneList(),null,
                 true,sitePlatform, siteIn.getStatus(),
                 entity.getBlockedAdvertiserDomainsForBidRequest(),
                 excludeDefinedAdDomains,isMarkedForDeletion,lastModifiedOn, false, null
                )
                .setApplicationId(applicationId).setAppStoreId(appStoreId)
                .setEcpmFloor(ecpmFloorValue)
                .setIsAdvertiserIdListExcluded(siteIn.isAdvertiserIdListExcluded())
                .setCampaignInclusionExclusionSchemaMap(siteIn.getCampaignInclusionExclusionSchemaMap())
                .setIsRichMediaAllowed(siteIn.isRichMediaAllowed())
                .setIsVideo(isVideo)
                .setVideoSupplyProps(videoSupplyProps)
                .build();

        String externalSupplyUrl = null;
        String externalSupplyId = null;
        String externalSupplyName = null;
        String externalSupplyDomain = null;
        String externalAppVersion = null;
        String externalAppPageUrl = null;
        String[] externalCategories = null;
        String externalAppBundle = null;

        if(null != siteEntity)
        {
            externalSupplyUrl = siteEntity.getSitePageURL();
            externalSupplyId = siteEntity.getSiteIdOnExchange();
            externalSupplyName = siteEntity.getSiteName();
            externalSupplyDomain = siteEntity.getSiteDomain();
        }
        else if(null != appEntity)
        {
            externalSupplyUrl = appEntity.getApplicationStoreUrl();
            externalSupplyId = appEntity.getApplicationIdOnExchange();
            externalSupplyName = appEntity.getApplicationName();
            externalSupplyDomain = appEntity.getApplicationDomain();
            externalAppVersion = appEntity.getApplicationVersion();
            externalAppBundle = appEntity.getApplicationBundleName();
        }

        site.setExternalSupplyDomain(externalSupplyDomain);
        site.setExternalSupplyId(externalSupplyId);
        site.setExternalSupplyName(externalSupplyName);
        site.setExternalSupplyUrl(externalSupplyUrl);
        site.setExternalAppVersion(externalAppVersion);
        site.setExternalPageUrl(externalAppPageUrl);
        site.setExternalAppBundle(externalAppBundle);
        site.setExternalCategories(externalCategories);
        return site;
    }

    
    private Site fetchSiteEntity(String siteId){
        return this.siteCache.query(siteId);
    }
    private Country findCountry(String ip){
        Country country = null;
        try{
            country = this.countryDetectionCache.findCountryForIpAddress(ip);
        }catch (Exception e){
            logger.error("Exception inside OpenRTBTwoDotThreeAggregatorRequestEnricher in fetching country " , e);
        }
        return country;
    }
    private InternetServiceProvider findCarrierEntity(String ip){
        InternetServiceProvider internetServiceProvider = null;
        try{
            internetServiceProvider = this.ispDetectionCache.fetchISPForIpAddress(ip);
        }catch (Exception e){
            logger.error("Exception inside OpenRTBTwoDotThreeAggregatorRequestEnricher in fetching isp ",e);
        }
        return internetServiceProvider;
    }
    private HashSet<Integer> getIntArrayToHashSet(Integer[] intArray){
    	if(intArray==null) {return null;}
    	HashSet<Integer> set = new HashSet<Integer>();
    	for(Integer i:intArray){
    		set.add(i);
    	}
    	return set;
    }
    private VideoSupplyProps getVideoSupplyProps(BidRequestImpressionVideoObjectDTO video){
    	if(video == null){
    		return null;
    	}
    	VideoSupplyProps vsp = new VideoSupplyProps();
    	if(video.getWidthVideoPlayerInPixels() != null){
    		vsp.setWidthPixel(video.getWidthVideoPlayerInPixels());
    	}
    	if(video.getHeightVideoPlayerInPixels() != null){
    		vsp.setHeightPixel(video.getHeightVideoPlayerInPixels());
    	}
    	if(video.getVideoBidResponseProtocol()!= null){
        	vsp.setProtocols(getIntArrayToHashSet(video.getVideoBidResponseProtocol()));
    	}
    	if(video.getMimeTypesSupported() != null){
    		String mimes[] = video.getMimeTypesSupported() ;
    		HashSet<Integer>  set = new HashSet<Integer>();
    		for(String s:mimes){
    			Integer i = VideoMimeTypes.getCodeFromName(s);
    			if(i!=null){
    				set.add(i);
    			}
    		}
         	vsp.setMimes(set);
    	}
    	if(video.getStartDelayInSeconds() != null){
        	vsp.setStartDelay(video.getStartDelayInSeconds());
    	}
    	if(video.getIsVideoLinear() != null){
        	vsp.setLinearity(video.getIsVideoLinear());
    	}
    	if(video.getContentBoxingAllowed() != null){
        	vsp.setBoxingallowed(video.getContentBoxingAllowed());
    	}
    	if(video.getMaximumBitRateVideoInKbps() != null){
    		vsp.setMaxBitrate(video.getMaximumBitRateVideoInKbps());
    	}
    	if(video.getMinimumBitRateVideoInKbps() != null){
    		vsp.setMinBitrate(video.getMinimumBitRateVideoInKbps());
    	}
    	if(video.getMinimumDurationOfVideo() != null){
    		vsp.setMinDurationSec(video.getMinimumDurationOfVideo());
    	}
    	if(video.getMaxDurationOfVideo()!= null){
    		vsp.setMaxDurationSec(video.getMaxDurationOfVideo());
    	}
    	if(video.getSupportedAPIFrameworkList() != null){
    		vsp.setApi(getIntArrayToHashSet(video.getSupportedAPIFrameworkList() ));
    	}
    	if(video.getSupportedDeliveryMethods() != null){
    		vsp.setDelivery(getIntArrayToHashSet(video.getSupportedDeliveryMethods()));
    	}
    	if(video.getMaximumExtendedVideoAdDuration() != null){
    		vsp.setMaxextended(video.getMaximumExtendedVideoAdDuration() );
    	}
    	if(video.getPlaybackMethods() != null){
    		vsp.setPlaybackmethod(getIntArrayToHashSet(video.getPlaybackMethods()));
    	}
    	if(video.getAdPosition() != null){
    		vsp.setPos(video.getAdPosition());
    	}
    	if(video.getCompanionTypes() != null){
    		vsp.setCompaniontype(getIntArrayToHashSet(video.getCompanionTypes()));
    	}
    	return vsp;
    }

}
