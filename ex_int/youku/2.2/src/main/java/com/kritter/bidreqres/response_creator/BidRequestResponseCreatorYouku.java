package com.kritter.bidreqres.response_creator;

import com.kritter.bidreqres.entity.*;
import com.kritter.bidrequest.entity.IBidResponse;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.bidrequest.response_creator.IBidResponseCreator;
import com.kritter.ex_int.utils.comparator.EcpmValueComparator;
import com.kritter.ex_int.utils.picker.RandomPicker;
import com.kritter.ex_int.utils.richmedia.markuphelper.MarkUpHelper;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.ExtClickType;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ApplicationGeneralUtils;
import com.kritter.utils.common.ServerConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.*;

/**
 * This class creates and validates response to youku's bid request.
 */
public class BidRequestResponseCreatorYouku implements IBidResponseCreator
{
    private Logger logger;
    private String postImpressionBaseClickUrl;
    private String postImpressionBaseClickUrlSecure;
    private String postImpressionBaseCSCUrl;
    private String postImpressionBaseCSCUrlSecure;
    private String postImpressionBaseWinApiUrl;
    private String postImpressionBaseWinApiUrlSecure;
    private String cdnBaseImageUrl;
    private String cdnBaseImageUrlSecure;
    private String secretKey;
    private int urlVersion;
    private String notificationUrlSuffix;
    private String notificationUrlBidderBidPriceMacro;
    private AdEntityCache adEntityCache;
    private String macroPostImpressionBaseClickUrl;
    private String macroPostImpressionBaseClickUrlSecure;

    //template for formatting.
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random randomPicker = new Random();
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";  
    public BidRequestResponseCreatorYouku(
                                            String loggerName,
                                            ServerConfig serverConfig,
                                            String secretKey,
                                            int urlVersion,
                                            String notificationUrlSuffix,
                                            String notificationUrlBidderBidPriceMacro,
                                            AdEntityCache adEntityCache
                                           )
    {
        this.logger = LogManager.getLogger(loggerName);
        this.postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
        this.postImpressionBaseClickUrlSecure = StringUtils.replaceOnce(this.postImpressionBaseClickUrl, HTTP_PROTOCOL, HTTPS_PROTOCOL);
        this.postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
        this.postImpressionBaseCSCUrlSecure = StringUtils.replaceOnce(this.postImpressionBaseCSCUrl, HTTP_PROTOCOL, HTTPS_PROTOCOL);
        this.postImpressionBaseWinApiUrl = serverConfig.getValueForKey(ServerConfig.WIN_API_URL_PREFIX);
        this.postImpressionBaseWinApiUrlSecure = StringUtils.replaceOnce(this.postImpressionBaseWinApiUrl, HTTP_PROTOCOL, HTTPS_PROTOCOL);
        this.cdnBaseImageUrl = serverConfig.getValueForKey(ServerConfig.CDN_URL_PREFIX);
        this.cdnBaseImageUrlSecure = StringUtils.replaceOnce(serverConfig.getValueForKey(ServerConfig.CDN_URL_PREFIX), HTTP_PROTOCOL, HTTPS_PROTOCOL);
        this.secretKey = secretKey;
        this.urlVersion = urlVersion;
        this.notificationUrlSuffix = notificationUrlSuffix;
        this.notificationUrlBidderBidPriceMacro = notificationUrlBidderBidPriceMacro;
        this.adEntityCache = adEntityCache;
        this.macroPostImpressionBaseClickUrl= serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX);
        this.macroPostImpressionBaseClickUrlSecure = StringUtils.replaceOnce(this.macroPostImpressionBaseClickUrl, HTTP_PROTOCOL, HTTPS_PROTOCOL);
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }


    @Override
    public IBidResponse constructBidResponseForExchange(Object... dspObjects) throws BidResponseException
    {
        if(null == dspObjects || dspObjects.length != 2)
            throw new BidResponseException("Inside BidRequestResponseCreatorYouku, arguments passed " +
                                           "for formatting are null or not Request and Response objects...");

        Object requestObject = dspObjects[0];
        Object responseObject = dspObjects[1];

        if(!(requestObject instanceof Request) || !(responseObject instanceof Response))
        {
            logger.debug("Request response objects are null, case of nofill inside : {} ", getClass().getName());
            return null;
        }

        Request request = (Request)requestObject;
        Response response = (Response)responseObject;

        if(null == request.getBidRequest() || !(request.getBidRequest() instanceof BidRequestYouku))
            throw new BidResponseException("Inside BidRequestResponseCreatorYouku, IBidRequest " +
                                           "is invalid/null, cannot format response...");

        BidRequestYouku bidRequestYouku = (BidRequestYouku)request.getBidRequest();

        /*For each impression id find response ad info and format response.*/
        Set<String> impressionIdsToRespondFor = response.fetchRTBExchangeImpressionIdToRespondFor();

        if(null == impressionIdsToRespondFor)
        {
            logger.debug("There is no impression ids to respond for inside BidRequestResponseCreatorYouku");
            return null;
        }

        Comparator<ResponseAdInfo> comparator = new EcpmValueComparator();

        BidResponseYoukuDTO bidResponseYoukuDTO = new BidResponseYoukuDTO();
        bidResponseYoukuDTO.setBidderGeneratedUniqueId(bidRequestYouku.getUniqueInternalRequestId());
        bidResponseYoukuDTO.setBidRequestId(bidRequestYouku.getUniqueBidRequestIdentifierForAuctioneer());

        //only one seat bid object...
        BidResponseSeatBidYoukuEntity[] bidResponseSeatBidYoukuArray = new BidResponseSeatBidYoukuEntity[1];

        BidResponseSeatBidYoukuEntity bidResponseSeatBidYoukuDTO = new BidResponseSeatBidYoukuEntity();

        BidResponseBidYoukuEntity[] bidResponseBidYoukuArray = new
        		BidResponseBidYoukuEntity[impressionIdsToRespondFor.size()];
        int counter = 0;
        boolean atleastOneBidAsResponse = false;

        for(String impressionId : impressionIdsToRespondFor)
        {
            Set<ResponseAdInfo> responseAdInfos = response.getResponseAdInfoSetForBidRequestImpressionId(impressionId);

            //sort and pick the one with highest ecpm value.
            List<ResponseAdInfo> list = new ArrayList<ResponseAdInfo>();
            list.addAll(responseAdInfos);
            Collections.sort(list,comparator);

            ResponseAdInfo responseAdInfoToUse = list.get(0);
            responseAdInfoToUse = RandomPicker.pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues
                    (responseAdInfoToUse,list, randomPicker);

            BidResponseBidYoukuEntity bidResponseBidYoukuDTO =
                    prepareBidResponseSeatBidYouku(
                                                        responseAdInfoToUse,
                                                        request,
                                                        response,
                                                        impressionId
                                                    );

            if(null == bidResponseBidYoukuDTO)
            {
                logger.error("BidResponseBidYouku could not be prepared for bidRequestImpressionId:{}",
                             impressionId);
                continue;
            }

            bidResponseBidYoukuArray[counter] = bidResponseBidYoukuDTO;
            response.addResponseAdInfoAsFinalForImpressionId(impressionId,responseAdInfoToUse);
            counter++;
            atleastOneBidAsResponse = true;
        }

        if(!atleastOneBidAsResponse)
            return null;

        bidResponseSeatBidYoukuDTO.setBidResponseBidEntities(bidResponseBidYoukuArray);
        bidResponseSeatBidYoukuArray[0] = bidResponseSeatBidYoukuDTO;

        bidResponseYoukuDTO.setBidResponseSeatBid(bidResponseSeatBidYoukuArray);

        String payLoad = null;

        try
        {
            objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            payLoad = objectMapper.writeValueAsString(bidResponseYoukuDTO);
        }
        catch (IOException ioe)
        {
            throw new BidResponseException("IOException inside BidRequestResponseCreatorYouku", ioe);
        }

        BidResponseYouku bidResponseYoukuToReturn =
                                                   new BidResponseYouku(
                                                                            bidResponseYoukuDTO.getBidRequestId(),
                                                                            bidResponseYoukuDTO.getBidderGeneratedUniqueId(),
                                                                            payLoad
                                                                        );

        return bidResponseYoukuToReturn;
    }

    @Override
    public void validateBidResponseEntity(IBidResponse bidResponseEntity) throws BidResponseException
    {

    }

    private BidResponseBidYoukuEntity prepareBidResponseSeatBidYouku
                                                                       (
                                                                        ResponseAdInfo responseAdInfo,
                                                                        Request request,
                                                                        Response response,
                                                                        String bidRequestImpressionId
                                                                       ) throws BidResponseException
    {
        AdEntity adEntity = adEntityCache.query(responseAdInfo.getAdId());

        if(null == adEntity)
        {
            logger.error("AdEntity not found in cache inside BidRequestResponseCreatorYouku adId:{} ",
                          responseAdInfo.getAdId());
            return null;
        }


        /********************Prepare bid object and set it into bid response object.********************************/
        StringBuffer winNotificationURLBuffer = new StringBuffer();
        BidResponseBidYoukuEntity bidResponseBidYoukuDTO = new BidResponseBidYoukuEntity();
        Creative creative = responseAdInfo.getCreative();

        if(creative.getCreativeFormat().equals(CreativeFormat.BANNER))
        {
        	BidResponseBidExtYoukuEntity  bidResponseBidExtYoukuDTO = prepareBannerHTMLAdMarkup(
                                                        request,
                                                        responseAdInfo,
                                                        response, adEntity.getExtTracker(),
                                                        winNotificationURLBuffer
                                                       );
            bidResponseBidYoukuDTO.setAdMarkup((request.getSecure()==true?cdnBaseImageUrl:cdnBaseImageUrlSecure)+responseAdInfo.getCreativeBanner().getResourceURI());
            bidResponseBidYoukuDTO.setWinNotificationUrl(winNotificationURLBuffer.toString());
            bidResponseBidYoukuDTO.setExtensionObject(bidResponseBidExtYoukuDTO);
        }else if(creative.getCreativeFormat().equals(CreativeFormat.VIDEO))
        {
        	BidResponseBidExtYoukuEntity  bidResponseBidExtYoukuDTO = prepareVideoMarkup(
                                                        request,
                                                        responseAdInfo,
                                                        response, adEntity.getExtTracker(),
                                                        winNotificationURLBuffer
                                                       );

            bidResponseBidYoukuDTO.setWinNotificationUrl(winNotificationURLBuffer.toString());
            bidResponseBidYoukuDTO.setExtensionObject(bidResponseBidExtYoukuDTO);
            if(responseAdInfo.getVideoInfo() != null && responseAdInfo.getVideoInfo().getExt()!=null 
            		&& responseAdInfo.getVideoInfo().getExt().getYoukuCDNUrl() != null){
            	bidResponseBidYoukuDTO.setAdMarkup(request.getSecure()?
            					responseAdInfo.getVideoInfo().getExt().getYoukuCDNUrl():
            						StringUtils.replaceOnce(responseAdInfo.getVideoInfo().getExt().getYoukuCDNUrl(), HTTP_PROTOCOL, HTTPS_PROTOCOL));
            }

        }
        bidResponseBidYoukuDTO.setBidId(responseAdInfo.getImpressionId());
        bidResponseBidYoukuDTO.setRequestImpressionId(bidRequestImpressionId);
        if(responseAdInfo.getDealId() != null){
        	bidResponseBidYoukuDTO.setDealId(responseAdInfo.getDealId());
        }
        bidResponseBidYoukuDTO.setCreativeId(responseAdInfo.getGuid()+":"+creative.getCreativeGuid());
        bidResponseBidYoukuDTO.setPrice(responseAdInfo.getEcpmValue().floatValue()*100); /*Converting to fen*/
        /*******************************Done preparing bid response bid object.********************************/

        return bidResponseBidYoukuDTO;
    }

    private BidResponseBidExtYoukuEntity prepareBannerHTMLAdMarkup(
                                             Request request,
                                             ResponseAdInfo responseAdInfo,
                                             Response response, ExtTracker extTracker,
                                             StringBuffer winNotificationURLBuffer
                                            ) throws BidResponseException
    {


        String clickUri = CreativeFormatterUtils.prepareClickUri
                                                                (
                                                                    logger,
                                                                    request,
                                                                    responseAdInfo,
                                                                    response.getBidderModelId(),
                                                                    urlVersion,
                                                                    request.getInventorySource(),
                                                                    response.getSelectedSiteCategoryId(),
                                                                    secretKey
                                                                );

        if(null == clickUri)
            throw new BidResponseException("Click URI could not be formed using different attributes like handset,location,bids,version,etc. inside BidRequestResponseCreator banneradmarkup");

        StringBuffer clickUrl = new StringBuffer(request.getSecure()?postImpressionBaseClickUrl:postImpressionBaseClickUrlSecure);
        clickUrl.append(clickUri);
        StringBuffer macroClickUrl = new StringBuffer(request.getSecure()?macroPostImpressionBaseClickUrl:macroPostImpressionBaseClickUrlSecure);
        macroClickUrl.append(clickUri);
        /*********prepare win notification url , also include bidder price.****************/
        winNotificationURLBuffer.append(request.getSecure()?postImpressionBaseWinApiUrl:postImpressionBaseWinApiUrlSecure);
        winNotificationURLBuffer.append(clickUri);
        String suffixToAdd = notificationUrlSuffix;
        suffixToAdd = suffixToAdd.replace(
                                          notificationUrlBidderBidPriceMacro,
                                          String.valueOf(responseAdInfo.getEcpmValue())
                                         );
        winNotificationURLBuffer.append(suffixToAdd);
        /*********done preparing win notification url, included bidder price as well*******/
        //set common post impression uri to be used in any type of post impression url.
        responseAdInfo.setCommonURIForPostImpression(clickUri);

        StringBuffer cscBeaconUrl = new StringBuffer(request.getSecure()?postImpressionBaseCSCUrl:postImpressionBaseCSCUrlSecure);
        cscBeaconUrl.append(clickUri);

        /**modify csc url to have bid-switch exchangeId as a parameter for usage in post-impression server************/
        logger.debug("Going to modify CSC URL if request has user ids available, for kritterUserId:{} ",
                request.getUserId());

        Set<ExternalUserId> externalUserIdSet = request.getExternalUserIds();
        String exchangeUserId = null;
        if(null != externalUserIdSet) {
            for(ExternalUserId externalUserId : externalUserIdSet) {
                if(externalUserId.getIdType().equals(ExternalUserIdType.EXCHANGE_CONSUMER_ID))
                    exchangeUserId = externalUserId.toString();
            }
        }
        cscBeaconUrl = new StringBuffer(ApplicationGeneralUtils.modifyCSCURLForUserIds(
                                                                                       exchangeUserId,
                                                                                       request.getUserId(),
                                                                                       cscBeaconUrl.toString())
                                                                                      );
        logger.debug("CSC url is modified to contain exchange and kritter UserId, after modification url:{} ",
                     cscBeaconUrl.toString());
        /**************************modifying csc url completed********************************************************/

        Creative creative = responseAdInfo.getCreative();

        if(!creative.getCreativeFormat().equals(CreativeFormat.BANNER))
            logger.error("Creative is not banner inside BidRequestResponseCreator,adId:{} ",
                         responseAdInfo.getAdId());

        if(null == responseAdInfo.getCreativeBanner())
        {
            logger.error("CreativeBanner is null inside BannerAdMarkUp,cannot format ad, for adid: " +
                         responseAdInfo.getAdId());
            return null;
        }
        BidResponseBidExtYoukuEntity bidResponseBidExtYoukuEntity = new BidResponseBidExtYoukuEntity();
        bidResponseBidExtYoukuEntity.setLdp(clickUrl.toString());
        String impTrackerArray[] = null;
        if(extTracker != null && extTracker.getImpTracker() != null){
        	impTrackerArray = new String[1+extTracker.getImpTracker().size()];
        	int i = 1;
        	for(String str:extTracker.getImpTracker()){
        		impTrackerArray[i]=MarkUpHelper.adTagMacroReplace(str, request, responseAdInfo, response, "", 
        				macroClickUrl.toString(), extTracker.getImpMacro(), extTracker.getImpMacroQuote(), "");
        		i++;
        	}
        }else{
        	impTrackerArray = new String[1];
        }
        impTrackerArray[0] = cscBeaconUrl.toString();
        bidResponseBidExtYoukuEntity.setPm(impTrackerArray);
        if(extTracker != null && extTracker.getClickTracker() != null &&
        		extTracker.getClickTracker().size()>0 && extTracker.getClickType() != null
        		&& extTracker.getClickType()==ExtClickType.INEXCRESPONSE.getCode()){
            String clickTrackerArray[] = null;
        	clickTrackerArray = new String[extTracker.getClickTracker().size()];
        	int i = 0;
        	for(String str:extTracker.getClickTracker()){
        		clickTrackerArray[i]=MarkUpHelper.adTagMacroReplace(str, request, responseAdInfo, response, "", 
        				macroClickUrl.toString(), extTracker.getImpMacro(), extTracker.getImpMacroQuote(), "");
        		i++;
        	}
        	bidResponseBidExtYoukuEntity.setCm(clickTrackerArray);
        }
        return bidResponseBidExtYoukuEntity;
    }
    private BidResponseBidExtYoukuEntity prepareVideoMarkup(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response, ExtTracker extTracker,
            StringBuffer winNotificationURLBuffer
           ) throws BidResponseException
    {


    	String clickUri = CreativeFormatterUtils.prepareClickUri
    			(
    					logger,
    					request,
    					responseAdInfo,
    					response.getBidderModelId(),
    					urlVersion,
    					request.getInventorySource(),
    					response.getSelectedSiteCategoryId(),
    					secretKey
    					);

    	if(null == clickUri)
    		throw new BidResponseException("Click URI could not be formed using different attributes like handset,location,bids,version,etc. inside BidRequestResponseCreator banneradmarkup");

    	StringBuffer clickUrl = new StringBuffer(request.getSecure()?postImpressionBaseClickUrl:postImpressionBaseClickUrlSecure);
    	clickUrl.append(clickUri);
        StringBuffer macroClickUrl = new StringBuffer(request.getSecure()?macroPostImpressionBaseClickUrl:macroPostImpressionBaseClickUrlSecure);
        macroClickUrl.append(clickUri);
    	/*********prepare win notification url , also include bidder price.****************/
    	winNotificationURLBuffer.append(request.getSecure()?postImpressionBaseWinApiUrl:postImpressionBaseWinApiUrlSecure);
    	winNotificationURLBuffer.append(clickUri);
    	String suffixToAdd = notificationUrlSuffix;
    	suffixToAdd = suffixToAdd.replace(
    			notificationUrlBidderBidPriceMacro,
    			String.valueOf(responseAdInfo.getEcpmValue())
    			);
    	winNotificationURLBuffer.append(suffixToAdd);
    	/*********done preparing win notification url, included bidder price as well*******/
    	//set common post impression uri to be used in any type of post impression url.
    	responseAdInfo.setCommonURIForPostImpression(clickUri);

    	StringBuffer cscBeaconUrl = new StringBuffer(request.getSecure()?postImpressionBaseCSCUrl:postImpressionBaseCSCUrlSecure);
    	cscBeaconUrl.append(clickUri);

    	/**modify csc url to have bid-switch exchangeId as a parameter for usage in post-impression server************/
    	logger.debug("Going to modify CSC URL if request has user ids available, for kritterUserId:{} ",
    			request.getUserId());

    	Set<ExternalUserId> externalUserIdSet = request.getExternalUserIds();
    	String exchangeUserId = null;
    	if(null != externalUserIdSet) {
    		for(ExternalUserId externalUserId : externalUserIdSet) {
    			if(externalUserId.getIdType().equals(ExternalUserIdType.EXCHANGE_CONSUMER_ID))
    				exchangeUserId = externalUserId.toString();
    		}
    	}
    	cscBeaconUrl = new StringBuffer(ApplicationGeneralUtils.modifyCSCURLForUserIds(
    			exchangeUserId,
    			request.getUserId(),
    			cscBeaconUrl.toString())
    			);
    	logger.debug("CSC url is modified to contain exchange and kritter UserId, after modification url:{} ",
    			cscBeaconUrl.toString());
    	/**************************modifying csc url completed********************************************************/

    	Creative creative = responseAdInfo.getCreative();

    	if(!creative.getCreativeFormat().equals(CreativeFormat.VIDEO))
    		logger.error("Creative is not video inside BidRequestResponseCreator,adId:{} ",
    				responseAdInfo.getAdId());

    	if(null == responseAdInfo.getVideoInfo())
    	{
    		logger.error("Video Info is null inside VideoAdMarkUp,cannot format ad, for adid: " +
    				responseAdInfo.getAdId());
    		return null;
    	}
    	if(responseAdInfo.getVideoInfo().getExt() == null ||
    			responseAdInfo.getVideoInfo().getExt().getYoukuCDNUrl() == null){
    		logger.error("Video Info  is null inside VideoAdMarkUp,cannot format ad, for adid: " +
    				responseAdInfo.getAdId());
    		return null;
    	}
    	
    	BidResponseBidExtYoukuEntity bidResponseBidExtYoukuEntity = new BidResponseBidExtYoukuEntity();
        bidResponseBidExtYoukuEntity.setLdp(clickUrl.toString());
    	String impTrackerArray[] = null;
    	if(extTracker != null && extTracker.getImpTracker() != null){
    		impTrackerArray = new String[1+extTracker.getImpTracker().size()];
    		int i = 1;
    		for(String str:extTracker.getImpTracker()){
    			impTrackerArray[i]=MarkUpHelper.adTagMacroReplace(str, request, responseAdInfo, response, "", 
        				macroClickUrl.toString(), extTracker.getImpMacro(), extTracker.getImpMacroQuote(), "");;
    			i++;
    		}
    	}else{
    		impTrackerArray = new String[1];
    	}
    	impTrackerArray[0] = cscBeaconUrl.toString();
    	bidResponseBidExtYoukuEntity.setPm(impTrackerArray);
        if(extTracker != null && extTracker.getClickTracker() != null &&
        		extTracker.getClickTracker().size()>0 && extTracker.getClickType() != null
        		&& extTracker.getClickType()==ExtClickType.INEXCRESPONSE.getCode()){
            String clickTrackerArray[] = null;
        	clickTrackerArray = new String[extTracker.getClickTracker().size()];
        	int i = 0;
        	for(String str:extTracker.getClickTracker()){
        		clickTrackerArray[i]=MarkUpHelper.adTagMacroReplace(str, request, responseAdInfo, response, "", 
        				macroClickUrl.toString(), extTracker.getImpMacro(), extTracker.getImpMacroQuote(), "");
        		i++;
        	}
        	bidResponseBidExtYoukuEntity.setCm(clickTrackerArray);
        }
    	return bidResponseBidExtYoukuEntity;
}
}
