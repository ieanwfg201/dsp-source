package com.kritter.adexchange.youku.enricher;

import com.kritter.adserving.adexchange.request.enricher.RTBExchangeRequestReader;
import com.kritter.adserving.request.utils.EnricherUtils;
import com.kritter.bidreqres.entity.*;
import com.kritter.bidrequest.entity.IBidRequest;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestAppDTO;
import com.kritter.bidrequest.entity.common.openrtbversion2_2.BidRequestImpressionBannerObjectDTO;
import com.kritter.bidrequest.reader.IBidRequestReader;
import com.kritter.common.caches.slot_size_cache.CreativeSlotSizeCache;
import com.kritter.common.site.cache.SiteCache;
import com.kritter.common.site.entity.Site;
import com.kritter.constants.ConnectionType;
import com.kritter.constants.ExternalUserIdType;
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
import com.kritter.utils.common.ApplicationGeneralUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class reads request from youku rtb exchange.
 * The URL provided to youku contains siteid as well:
 * e.g:
 * http://rtbexchange.dsp.com/b2409484c01754ef08c0e476b796fc081/a179484c01754ef08c0e476b796fc081
 * first id is publisher id and second is the siteid.
 */
public class YoukuRequestEnricher implements RTBExchangeRequestReader
{
    private Logger logger;
    private IBidRequestReader youkuBidRequestReader;
    private SiteCache siteCache;
    private HandsetDetectionProvider handsetDetectionProvider;
    private CountryDetectionCache countryDetectionCache;
    private ISPDetectionCache ispDetectionCache;
   private static final String ENCODING = "UTF-8";
   private CreativeSlotSizeCache creativeSlotSizeCache;


    public YoukuRequestEnricher
                                (String loggerName,
                                 IBidRequestReader youkuBidRequestReader,
                                 SiteCache siteCache,
                                 HandsetDetectionProvider handsetDetectionProvider,
                                 CountryDetectionCache countryDetectionCache,
                                 ISPDetectionCache ispDetectionCache,
                                 CreativeSlotSizeCache creativeSlotSizeCache
                                 )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.youkuBidRequestReader = youkuBidRequestReader;
        this.siteCache = siteCache;
        this.handsetDetectionProvider = handsetDetectionProvider;
        this.countryDetectionCache = countryDetectionCache;
        this.ispDetectionCache = ispDetectionCache;
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

        logger.debug("Request Method inside validateAndEnrichRequest of YoukuRequestEnricher is {} ",
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
            logger.error("The bidrequest payload received from Youku RTB Exchange is null inside validateAndEnrichRequest of YoukuRequestEnricher,cannot enrich bid request...");
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
            bidRequest = youkuBidRequestReader.convertBidRequestPayloadToBusinessObject(bidRequestPayLoadReceived);
        }
        catch (Exception e)
        {
            logger.error("Exception inside YoukuRequestEnricher ",e);
            logger.error("Exception reading bid request for request id: {} ",requestId);
            return null;
        }

        youkuBidRequestReader.validateBidRequestForMandatoryParameters(bidRequest);

        logger.debug("RequestIdByDSP: {} " , bidRequest.getUniqueInternalRequestId());

        //if flow comes here this means everything is correct in the bid request.
        //now populate request object for internal working.
        /********************************DETECT SITE BY REQUEST SITEID****************************************/
        Request request = new Request(requestId, INVENTORY_SOURCE.RTB_EXCHANGE);
        request.setBidRequest(bidRequest);

        String siteIdFromBidRequest = StringUtils.substringAfterLast(httpServletRequest.getRequestURI(), "/");

        logger.debug("SiteId received from bid request URL: {} ", siteIdFromBidRequest);

        YoukuBidRequestParentNodeDTO youkuBidRequestParentNodeDTO =
                       (YoukuBidRequestParentNodeDTO)request.getBidRequest().getBidRequestParentNodeDTO();

        /******************************DETECT HANDSET BY USERAGENT**********************************************/

        String userAgent = null;
        YoukuBidRequestDeviceDTO youkuBidRequestDeviceDTO =
                                                        youkuBidRequestParentNodeDTO.getYoukuBidRequestDeviceDTO();

        if(null != youkuBidRequestDeviceDTO)
        {
            userAgent = youkuBidRequestDeviceDTO.getDeviceUserAgent();
        }


        HandsetMasterData handsetMasterData = null;
        if(userAgent != null && !userAgent.isEmpty()){        		
        	handsetMasterData = this.handsetDetectionProvider.detectHandsetForUserAgent(userAgent);
        }

        if(null == handsetMasterData)
        {
            this.logger.debug("Device detection failed inside YoukuRequestEnricher, proceeding with  undetected handset");
        }else {
        	logger.debug("The internal id for handset detection is : {}", handsetMasterData.getInternalId());
        	if(handsetMasterData.isBot())
        	{
        		this.logger.error("Device detected is BOT inside YoukuRequestEnricher, cannot proceed further");
            	request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.DEVICE_BOT);
            		return request;
        	}
        }
        request.setHandsetMasterData(handsetMasterData);

        /*******************************DETECT COUNTRY CARRIER USING MNC MCC or IP*****************************/

        //detect country and carrier and enrich request object with the same.
        //the youku format is mnc-mcc as desired by us.
        String mncMccCode = youkuBidRequestDeviceDTO.getCarrier();
        CountryIspUiDataUsingMccMnc countryIspUiDataUsingMccMnc = null;
        Country country = null;
        InternetServiceProvider internetServiceProvider = null;


        /******************************************* ip extraction and connection type detection*********************/
        String ip = youkuBidRequestDeviceDTO.getIpV4AddressClosestToDevice();
        if(null == ip || ip.isEmpty())
        {
        	ip=ip.trim();
        	ip = youkuBidRequestDeviceDTO.getIpV6Address();
        	if(ip == null || ip.isEmpty()){
        		logger.debug("Country and InternetServiceProvider could not be detected inside YoukuRequestEnricher as mnc-mcc lookup failed as well as ip address not present...");
        	}
        }

        request.setIpAddressUsedForDetection(ip);
        
        if(youkuBidRequestDeviceDTO.getConnectionType() != null)
        {
        	ConnectionType connectionType = ConnectionType.getEnum(youkuBidRequestDeviceDTO.getConnectionType().shortValue());
            // Get the connection type for this ip
        	if(connectionType != null){
        		request.setConnectionType(connectionType);
        	}
        }
        else
        {
            // Default to unknown if the cache is not present
            request.setConnectionType(ConnectionType.UNKNOWN);
        }
        /******************************************* ip extraction and  connection type detection ends***************/


        //if mnc mcc not present or location not detected use ip address to find location.
        if(null == countryIspUiDataUsingMccMnc && ip !=null && !ip.isEmpty())
        {
            logger.debug("No entry could be found for mcc-mnc combination: {} , using ip: {} , for location detection. "
                         ,mncMccCode,ip);

            country = findCountry(ip);
            internetServiceProvider = findCarrierEntity(ip);
            request.setCountry(country);
            request.setInternetServiceProvider(internetServiceProvider);
        }
        /******************************************************************************************************/

        /********************* Set strict banner size if applicable,also set text ad allowed*******************/
        YoukuBidRequestImpressionDTO[] youkuBidRequestImpressionDTOs =
                youkuBidRequestParentNodeDTO.getYoukuBidRequestImpressionDTOs();

        logger.debug("Going to set strict banner size for each impression if applicable inside YoukuRequestEnricher");
        String adpositionid=null;
        double ecpmFloorValue=0.0;
        if(null != youkuBidRequestImpressionDTOs && youkuBidRequestImpressionDTOs.length > 0)
        {
            for(YoukuBidRequestImpressionDTO youkuBidRequestImpressionDTO : youkuBidRequestImpressionDTOs)
            {
                Integer requiresSecureAssets = youkuBidRequestImpressionDTO.getRequiresSecureAssets()==null? 0 : youkuBidRequestImpressionDTO.getRequiresSecureAssets();
                if(0== requiresSecureAssets){
                    // set true, returen http
            		request.setSecure(true);
            	}
            	
            	if(youkuBidRequestImpressionDTO.getAdTagOrPlacementId() !=null && !"".equals(youkuBidRequestImpressionDTO.getAdTagOrPlacementId())){
            		adpositionid=youkuBidRequestImpressionDTO.getAdTagOrPlacementId();
            	}
            	if(youkuBidRequestImpressionDTO.getBidFloorPrice() !=null){
            		ecpmFloorValue=youkuBidRequestImpressionDTO.getBidFloorPrice()/100;
            		youkuBidRequestImpressionDTO.setBidFloorPrice(youkuBidRequestImpressionDTO.getBidFloorPrice()/100);
            	}
                BidRequestImpressionBannerObjectDTO youkuBidRequestImpressionBannerObjectDTO =
                        youkuBidRequestImpressionDTO.getBidRequestImpressionBannerObject();
                if(youkuBidRequestImpressionBannerObjectDTO != null){
                	Integer maxWidth = youkuBidRequestImpressionBannerObjectDTO.getBannerWidthInPixels();
                	Integer maxHeight = youkuBidRequestImpressionBannerObjectDTO.getBannerHeightInPixels();

                	if(null == maxWidth && null == maxHeight)
                		request.setStrictBannerSizeForImpressionIdOfRTBExchange
                		(youkuBidRequestImpressionDTO.getBidRequestImpressionId(),true);

                	/*set max width and height required and request as interstital, where variable size is needed*/
                	else
                	{
                		int widthArray[] = new int[1];
                		int heightArray[] = new int[1];
                		int minWidthArray[] = new int[1];
                		int minHeightArray[] = new int[1];

                		widthArray[0] = maxWidth;
                		heightArray[0] = maxHeight;
                		if(null != maxWidth && null != maxHeight)
                		{
                			minWidthArray[0] = maxWidth;
                			minHeightArray[0] = maxHeight;
                		}
                		else
                		{
                			minWidthArray[0] = 0;
                			minHeightArray[0] = 0;
                		}

                		request.setRequiredSizeArrayForImpressionIdOfRTBExchange
                		(youkuBidRequestImpressionDTO.getBidRequestImpressionId(),widthArray,heightArray);
                		request.setInterstitalMinimumSizeArrayForImpressionIdOfRTBExchange
                		(youkuBidRequestImpressionDTO.getBidRequestImpressionId(),minWidthArray,minHeightArray);
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

                /*set secure assets required if at all*/
                request.setSecureRequiredForImpressionIdOfRTBExchange(youkuBidRequestImpressionDTO.getBidRequestImpressionId(),Boolean.FALSE);
            }
        }
        Site site = fetchSiteEntityForYoukuRequest(request, siteIdFromBidRequest,adpositionid,ecpmFloorValue);

        if(null==site || !(site.getStatus() == StatusIdEnum.Active.getCode()))
        {
            request.setRequestEnrichmentErrorCode(Request.REQUEST_ENRICHMENT_ERROR_CODE.SITE_NOT_FIT);
            this.logger.error("Requesting site is not fit or is not found in cache . siteid: {}" , siteIdFromBidRequest);
            return request;
        }

        request.setSite(site);

        /******************************************************************************************************/


        /***************************Set extra available parameters*********************************************/
        populateRequestObjectForExtraParameters(youkuBidRequestParentNodeDTO,request);
        /******************************************************************************************************/

        return request;
    }

    /**
     * All attributes must be set at runtime except hygiene ,which
     * should be taken from the entity as present in the database.
     */
    private Site fetchSiteEntityForYoukuRequest(Request request,String siteIdFromBidRequest,String adpositionid,double ecpmFloorValue)
    {
        Site site = this.siteCache.query(siteIdFromBidRequest);

        YoukuBidRequestParentNodeDTO youkuBidRequestParentNodeDTO =
                                    (YoukuBidRequestParentNodeDTO)request.getBidRequest().getBidRequestParentNodeDTO();

        YoukuBidRequestSiteDTO youkuBidRequestSiteDTO = youkuBidRequestParentNodeDTO
                                                            .getYoukuBidRequestSiteDTO();
        BidRequestAppDTO youkuBidRequestAppDTO = youkuBidRequestParentNodeDTO
                                                             .getYoukuBidRequestAppDTO();

        String siteUrl = null != youkuBidRequestSiteDTO ? youkuBidRequestSiteDTO.getSitePageURL() : null;
        Short sitePlatform;
        String applicationId = null;
        if(null != youkuBidRequestSiteDTO)
        {
            sitePlatform = SITE_PLATFORM.WAP.getPlatform();
        }
        else if(null != youkuBidRequestAppDTO)
        {
            sitePlatform = SITE_PLATFORM.APP.getPlatform();
        }
        else
        {
            logger.error("Site/App not found in request, site/app both not present inside YoukuRequestEnricher...aborting request....");
            return null;
        }


        //creative attributes to exclude come at banner/video level for each impression required
        //in the bid response for the bid request, hence set null here and in workflow where
        //creative targeting matching is done use fields from bid request for matching up.
        boolean excludeDefinedAdDomains = true;
        boolean isMarkedForDeletion = false;
        Timestamp lastModifiedOn = new Timestamp(System.currentTimeMillis());
        Short appStoreId = 0;
        //keep floor value as 0.0, since its per impression, so for each impression
        //this value should be used and compared with ecpm value of each adunit.
        //Create a new site and set all attributes, take hygiene from the one found in cache.
        Site siteToUse = new Site.SiteEntityBuilder
                (
                 site.getSiteIncId(), site.getSiteGuid(), site.getName(),
                 site.getPublisherIncId(),site.getPublisherId(),siteUrl,
                 null,null,true,
                 site.getHygieneList(),site.getOptInHygieneList(),null,
                 false,sitePlatform, site.getStatus(),
                            youkuBidRequestParentNodeDTO.getBlockedAdvertiserDomainsForBidRequest(),
                 excludeDefinedAdDomains,isMarkedForDeletion,lastModifiedOn, false, null
                )
                .setApplicationId(applicationId).setAppStoreId(appStoreId)
                .setEcpmFloor(ecpmFloorValue)
                .setIsAdvertiserIdListExcluded(site.isAdvertiserIdListExcluded())
                .setCampaignInclusionExclusionSchemaMap(site.getCampaignInclusionExclusionSchemaMap())
                .setIsRichMediaAllowed(site.isRichMediaAllowed()).setAdPosition(adpositionid)
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

        if(null != youkuBidRequestSiteDTO)
        {
        	externalSupplyDomain = youkuBidRequestSiteDTO.getSitePageURL();
            externalSupplyId = youkuBidRequestSiteDTO.getSiteName();
            externalSupplyName = youkuBidRequestSiteDTO.getSiteName();
        }
        else if(null != youkuBidRequestAppDTO)
        {
            externalSupplyId = youkuBidRequestAppDTO.getApplicationName();
            externalSupplyName = youkuBidRequestAppDTO.getApplicationName();
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
        if(youkuBidRequestSiteDTO != null){
        	if(youkuBidRequestSiteDTO.getBidRequestContent() != null && youkuBidRequestSiteDTO.getBidRequestContent().getExtensionObject() != null){
        		YoukuBidRequestContentExtDTO  contentExt =  youkuBidRequestSiteDTO.getBidRequestContent().getExtensionObject();
        		if(contentExt != null){
        			if(contentExt.getChannel() != null){
        				siteToUse.setChannelFirstLevelCode(contentExt.getChannel());
        			}
        			if(contentExt.getCs() != null){
        				siteToUse.setChannelSecondLevelCode(contentExt.getCs());
        			}
        		}
        	}
        }
        return siteToUse;
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
            logger.error("Exception inside YoukuRequestEnricher in fetching country " , e);
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
            logger.error("Exception inside YoukuRequestEnricher in fetching isp ",e);
        }

        return internetServiceProvider;
    }

    /**
     * This function populates request object with extra parameters obtained from bid request
     * such as mcc-mnc, user-ids ,app and site attributes etc.
     * @param youkuBidRequestParentNodeDTO
     * @param request
     */
    private void populateRequestObjectForExtraParameters(YoukuBidRequestParentNodeDTO youkuBidRequestParentNodeDTO,
                                                         Request request)
    {
        YoukuBidRequestUserDTO youkuBidRequestUserDTO = youkuBidRequestParentNodeDTO
                                                            .getYoukuBidRequestUserDTO();
        YoukuBidRequestDeviceDTO youkuBidRequestDeviceDTO = youkuBidRequestParentNodeDTO
                                                                .getYoukuBidRequestDeviceDTO();

        EnricherUtils.populateUserIdsFromBidRequestDeviceDTO(youkuBidRequestDeviceDTO, request);
        EnricherUtils.populateUserIdsFromBidRequestUserDTO(youkuBidRequestUserDTO, request);
        boolean imeimd5=false;
        if(youkuBidRequestDeviceDTO.getMD5HashedDeviceId() == null ||
                youkuBidRequestDeviceDTO.getMD5HashedDeviceId().isEmpty()) {
        	imeimd5=true;
        }
        boolean androididmd5=false;
        if(youkuBidRequestDeviceDTO.getMD5HashedDevicePlatformId() == null ||
                youkuBidRequestDeviceDTO.getMD5HashedDevicePlatformId().isEmpty()) {
        	androididmd5=true;
            }

        populateUserIdsFromBidRequestYoukuDeviceDTO(youkuBidRequestDeviceDTO, request,imeimd5,androididmd5);
        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null || externalUserIds.size() == 0) {
            logger.debug("External user ids empty or not present");
        } else {
            logger.debug("External user ids found.");
            for(ExternalUserId externalUserId : externalUserIds) {
                logger.debug("\texternal user id = {}", externalUserId);
            }
        }

        if(null != youkuBidRequestDeviceDTO)
        {
            request.setMccMncCodeCouple(youkuBidRequestDeviceDTO.getCarrier());
            request.setUserAgent(youkuBidRequestDeviceDTO.getDeviceUserAgent());
        }
    }
    
    private void populateUserIdsFromBidRequestYoukuDeviceDTO(
    		YoukuBidRequestDeviceDTO bidRequestDeviceDTO,
            Request request, boolean imeimd5,boolean androididmd5) {
        if(null == bidRequestDeviceDTO)
            return;

        int siteIncId = -1;
        if(request.getSite() != null)
            siteIncId = request.getSite().getSiteIncId();

        Set<ExternalUserId> externalUserIds = request.getExternalUserIds();
        if(externalUserIds == null) {
            externalUserIds = new HashSet<ExternalUserId>();
            request.setExternalUserIds(externalUserIds);
        }

        if(bidRequestDeviceDTO.getIdfa() != null &&
                !bidRequestDeviceDTO.getIdfa().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.IFA_USER_ID, siteIncId,
                    bidRequestDeviceDTO.getIdfa()));
        }


        if(bidRequestDeviceDTO.getAndroidid() != null &&
                !bidRequestDeviceDTO.getAndroidid().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.DEVICE_PLATFORM_ID, siteIncId,
                    bidRequestDeviceDTO.getAndroidid()));
            if(androididmd5){
            	String androididmd5Str = getMD5( bidRequestDeviceDTO.getAndroidid());
            	if(androididmd5Str != null && !androididmd5Str.isEmpty()){
                    externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID, siteIncId,
                    		androididmd5Str));
            		
            	}
            }

        }

        if(bidRequestDeviceDTO.getImei() != null &&
                !bidRequestDeviceDTO.getImei().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.DEVICE_ID, siteIncId,
                    bidRequestDeviceDTO.getImei()));
            if(imeimd5){
            	String imeimd5Str = getMD5( bidRequestDeviceDTO.getImei());
            	if(imeimd5Str != null && !imeimd5Str.isEmpty()){
                    externalUserIds.add(new ExternalUserId(ExternalUserIdType.MD5_DEVICE_ID, siteIncId,
                    		imeimd5Str));
            		
            	}
            }
        }

        if(bidRequestDeviceDTO.getMac() != null && !bidRequestDeviceDTO.getMac().isEmpty()) {
            externalUserIds.add(new ExternalUserId(ExternalUserIdType.MAC, siteIncId,
                    bidRequestDeviceDTO.getMac()));
        }
    }	
    public String getMD5(String s) {

    	if(s==null){
    		return s;
    	}
    	try{
		MessageDigest md = MessageDigest.getInstance("MD5");
		String digest = getDigest(s,md);
		return digest;
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
    		return null;
    	}


	} 
	public String getDigest(String s , MessageDigest md)
			throws Exception {

		md.reset();
		byte[] digest = md.digest(s.getBytes());
		String result = new String(Hex.encodeHex(digest));
		return result;
	}

}
