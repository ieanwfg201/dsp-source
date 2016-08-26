package com.kritter.adserving.formatting;

import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.entity.vast.normal.threedotzero.CreateVastNormalThreeDotZero;
import com.kritter.entity.vast.normal.twodotzero.CreateVastNormalTwoDotZero;
import com.kritter.entity.vast.wrapper.three_dot_zero.CreateVastWrapper;
import com.kritter.entity.vast.wrapper.two_dot_zero.CreateVastWrapperTwoDotZero;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.utils.common.ApplicationGeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.adserving.formatting.macro.AdTagMacroReplace;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.CreativeFormat;
import com.kritter.serving.demand.cache.AdEntityCache;
import com.kritter.serving.demand.cache.CreativeCache;
import com.kritter.serving.demand.entity.AdEntity;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ServerConfig;
import com.kritter.formatterutil.CreativeFormatterUtils;

import java.util.List;
import java.util.Set;

/**
 * This class formats creatives in vast format.
 */
public class VASTFormatter implements CreativesFormatter{
	private Logger logger;
	private String postImpressionBaseClickUrl;
	private String postImpressionBaseCSCUrl;
	private String cdnBaseImageUrl;
	private String secretKey;
	private int urlVersion;
	private CreativeCache creativeCache;
	private AdEntityCache adEntityCache;
	private String loggerName;
	private String trackingEventUrl;
	private String macroPostImpressionBaseClickUrl;
	public VASTFormatter(
                         String loggerName,
                         String secretKey,
                         int urlVersion,
                         CreativeCache creativeCache,
                         AdEntityCache adEntityCache,
                         ServerConfig serverConfig)
    {
		this.logger = LoggerFactory.getLogger(loggerName);
		this.loggerName = loggerName;
		this.postImpressionBaseClickUrl = serverConfig.getValueForKey(ServerConfig.CLICK_URL_PREFIX);
		this.postImpressionBaseCSCUrl = serverConfig.getValueForKey(ServerConfig.CSC_URL_PREFIX);
		this.cdnBaseImageUrl = serverConfig.getValueForKey(ServerConfig.CDN_URL_PREFIX);
		this.secretKey = secretKey;
		this.urlVersion = urlVersion;
		this.creativeCache = creativeCache;
		this.adEntityCache = adEntityCache;
		this.trackingEventUrl = serverConfig.getValueForKey(ServerConfig.trackingEventUrl_PREFIX);
		this.macroPostImpressionBaseClickUrl=serverConfig.getValueForKey(ServerConfig.MACRO_CLICK_URL_PREFIX);
	}

	@Override
	public String formatCreatives(Request request, Response response) throws Exception
    {
		if(null == response || null == response.getResponseAdInfo())
        {
			logger.error("No Ads shortlisted for this request,nothing to format in formatCreatives of VASTFormatter");
			return null;
		}

		logger.debug("Bidder model id inside formatCreatives of VASTFormatter is : {}", response.getBidderModelId());
		for(ResponseAdInfo responseAdInfo : response.getResponseAdInfo())
        {
			if(null == responseAdInfo)
            {
				continue;
			}
			AdEntity adEntity = this.adEntityCache.query(responseAdInfo.getAdId());

            if(null== adEntity)
            {
				this.logger.error("The ad entity not found in cache inside formatCreatives() of VASTFormatter,id being: {}", adEntity.getId());
				continue;
			}

            Creative creative = this.creativeCache.query(adEntity.getCreativeId());

            if(null == creative)
            {
				this.logger.error("The creative entity not found inside formatCreatives() VASTFormatter,id being: {}", adEntity.getCreativeId());
				continue;
			}

            String clickUri = CreativeFormatterUtils.prepareClickUri(
                                                                     this.logger,
                                                                     request,
                                                                     responseAdInfo,
                                                                     response.getBidderModelId(),
                                                                     this.urlVersion,
                                                                     request.getInventorySource(),
                                                                     response.getSelectedSiteCategoryId(),
                                                                     this.secretKey
                                                                    );
			if(null == clickUri){
				throw new Exception("Click URI could not be formed using different attributes like handset,location,bids,version,etc. inside formatCreatives of VASTFormatter");
			}
			StringBuffer clickUrl = new StringBuffer();
            clickUrl.append(this.postImpressionBaseClickUrl);
            clickUrl.append(clickUri);

			StringBuffer cscBeaconUrl = new StringBuffer();
            cscBeaconUrl.append(this.postImpressionBaseCSCUrl);
            cscBeaconUrl.append(clickUri);

            /**modify csc url to have bid-switch exchangeId as a parameter for usage in post-impression server************/
            logger.debug("Going to modify CSC URL if request has user ids available, for kritterUserId:{} ",
                    request.getUserId());

            Set<ExternalUserId> externalUserIdSet = request.getExternalUserIds();
            String exchangeUserId = null;
            if(null != externalUserIdSet)
            {
                for(ExternalUserId externalUserId : externalUserIdSet)
                {
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

            //set common post impression uri to be used in any type of post impression url.
			responseAdInfo.setCommonURIForPostImpression(clickUri);
			
			if(creative.getCreativeFormat().equals(CreativeFormat.VIDEO))
            {
                if(null == responseAdInfo.getVideoProps())
                {
                    logger.error("VideoProps is null inside VASTFormatter,cannot format ad, for adid: {}", responseAdInfo.getAdId());
                    continue;
                }
                List<String> extImpTracker = null;
                if(adEntity.getExtTracker() != null && adEntity.getExtTracker().getImpTracker() != null){
                    extImpTracker = adEntity.getExtTracker().getImpTracker();
                }
                VideoProps videoProps = responseAdInfo.getVideoProps();
                String bitRateStr = null;
                if(videoProps.getBitrate()!=-1){
                	bitRateStr = videoProps.getBitrate()+"";
                }
                String deliveryStr = null;
                ContentDeliveryMethods cdmethods = ContentDeliveryMethods.getEnum(videoProps.getDelivery());
                if(cdmethods != null && cdmethods != ContentDeliveryMethods.Unknown){
                	deliveryStr = cdmethods.getName();
                }
                StringBuffer trackingUrl = new StringBuffer(trackingEventUrl);
                trackingUrl.append(clickUri);


                if(videoProps.getProtocol() == VideoBidResponseProtocols.VAST_2_0.getCode()){
                	if(responseAdInfo.getVideoInfo() != null){
                	StringBuffer creativeUrl = new StringBuffer(cdnBaseImageUrl);
                	if(cdnBaseImageUrl != null && !cdnBaseImageUrl.endsWith("/")){
                		creativeUrl.append("/");
                	}
                    creativeUrl.append(responseAdInfo.getVideoInfo().getResource_uri());
                	return CreateVastNormalTwoDotZero.createVastNormalString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                    		responseAdInfo.getImpressionId(),trackingUrl.toString(), request.getSite().getPublisherId(), videoProps.getLinearity(), 
                    		videoProps.getCompaniontype(), videoProps.getTracking(), trackingUrl.toString(),logger, responseAdInfo.getAdId()+"", 
                    		convertDurationStr(videoProps.getDuration()),null, creativeUrl.toString(), creative.getCreativeGuid(), deliveryStr, 
                    		VideoMimeTypes.getEnum(videoProps.getMime()).getMime(), bitRateStr, videoProps.getWidth(), videoProps.getHeight());
                	}
                }
                if(videoProps.getProtocol() == VideoBidResponseProtocols.VAST_3_0.getCode()){
                	if(responseAdInfo.getVideoInfo() != null){
                	StringBuffer creativeUrl = new StringBuffer(cdnBaseImageUrl);
                	if(cdnBaseImageUrl != null && !cdnBaseImageUrl.endsWith("/")){
                		creativeUrl.append("/");
                	}
                    creativeUrl.append(responseAdInfo.getVideoInfo().getResource_uri());
	
                	return CreateVastNormalThreeDotZero.createVastNormalString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                    		responseAdInfo.getImpressionId(),trackingUrl.toString(), request.getSite().getPublisherId(), videoProps.getLinearity(), 
                    		videoProps.getCompaniontype(), videoProps.getTracking(), trackingUrl.toString(),logger, responseAdInfo.getAdId()+"", 
                    		convertDurationStr(videoProps.getDuration()),null, creativeUrl.toString(), creative.getCreativeGuid(), deliveryStr, 
                    		VideoMimeTypes.getEnum(videoProps.getMime()).getMime(), bitRateStr, videoProps.getWidth(), videoProps.getHeight(),
                    		null);
                	}
                }
                if(videoProps.getProtocol() == VideoBidResponseProtocols.VAST_3_0_WRAPPER.getCode()){
                	String macroTagUrl = AdTagMacroReplace.adTagMacroReplace(videoProps.getVastTagUrl(), request, responseAdInfo, response, 
                			macroPostImpressionBaseClickUrl, videoProps.getVast_tag_macro(), videoProps.getVast_tag_macro_quote());
                	return CreateVastWrapper.createWrapperString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                            responseAdInfo.getImpressionId(), macroTagUrl, 
                            trackingUrl.toString(), request.getSite().getPublisherId(), 
                            videoProps.getLinearity(), videoProps.getCompaniontype(), videoProps.getTracking(), 
                            trackingUrl.toString(), logger);
                }
                if(videoProps.getProtocol() == VideoBidResponseProtocols.VAST_2_0_WRAPPER.getCode()){
                	String macroTagUrl = AdTagMacroReplace.adTagMacroReplace(videoProps.getVastTagUrl(), request, responseAdInfo, response, 
                			macroPostImpressionBaseClickUrl, videoProps.getVast_tag_macro(), videoProps.getVast_tag_macro_quote());
                	return CreateVastWrapperTwoDotZero.createWrapperString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                            responseAdInfo.getImpressionId(), macroTagUrl, 
                            trackingUrl.toString(), request.getSite().getPublisherId(), 
                            videoProps.getLinearity(), videoProps.getCompaniontype(), videoProps.getTracking(), 
                            trackingUrl.toString(), logger);
                }
			}
		}

		return "";
	}
    private static String convertDurationStr(int duration){
    	if(duration < 1){
    		return null;
    	}
    	int sec = duration % 60;
    	int min = (duration/60) %60;
    	int hr = (duration/60) /60;
    	StringBuffer sbuff = new StringBuffer("");
    	if(hr < 10){
    		sbuff.append("0");
    	}
    	sbuff.append(hr);
    	sbuff.append(":");
    	if(min < 10){
    		sbuff.append("0");
    	}
    	sbuff.append(min);
    	sbuff.append(":");
    	if(sec < 10){
    		sbuff.append("0");
    	}
    	sbuff.append(sec);
    	return sbuff.toString();
    }

}
