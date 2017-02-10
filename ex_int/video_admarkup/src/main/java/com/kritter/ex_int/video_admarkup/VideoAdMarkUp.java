package com.kritter.ex_int.video_admarkup;

import java.net.URLEncoder;

import org.apache.logging.log4j.Logger;

import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.vast.normal.threedotzero.CreateVastNormalThreeDotZero;
import com.kritter.entity.vast.normal.twodotzero.CreateVastNormalTwoDotZero;
import com.kritter.entity.vast.wrapper.three_dot_zero.CreateVastWrapper;
import com.kritter.entity.vast.wrapper.two_dot_zero.CreateVastWrapperTwoDotZero;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.ex_int.utils.richmedia.markuphelper.MarkUpHelper;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.serving.demand.entity.Creative;

public class VideoAdMarkUp {

    public static String prepare(
            Request request,
            ResponseAdInfo responseAdInfo,
            Response response,
            StringBuffer winNotificationURLBuffer,
            Logger logger, int urlVersion, String secretKey, String postImpressionBaseClickUrl, 
            String postImpressionBaseWinApiUrl, String notificationUrlSuffix, 
            String notificationUrlBidderBidPriceMacro, String postImpressionBaseCSCUrl, 
            String cdnBaseImageUrl, String trackingEventUrl,
            String impressionExtra, String beventBaseUrl,
            String macroPostImpressionBaseClickUrl
    )throws BidResponseException{

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
            throw new BidResponseException("Click URI could not be formed using different attributes like " +
                    "handset,location,bids,version,etc. inside BidRequestResponseCreator");

        StringBuffer clickUrl = new StringBuffer(postImpressionBaseClickUrl);
        clickUrl.append(clickUri);

        /*********prepare win notification url , also include bidder price.****************/
        winNotificationURLBuffer.append(postImpressionBaseWinApiUrl);
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

        StringBuffer cscBeaconUrl = null; 
        if(null != impressionExtra){
            cscBeaconUrl = new StringBuffer(beventBaseUrl);
            cscBeaconUrl.append(clickUri);
            cscBeaconUrl.append(impressionExtra);
        }else{
            cscBeaconUrl = new StringBuffer(postImpressionBaseCSCUrl);
            cscBeaconUrl.append(clickUri);

        }
        StringBuffer trackingUrl = new StringBuffer(trackingEventUrl);
        trackingUrl.append(clickUri);

        Creative creative = responseAdInfo.getCreative();

        if(!creative.getCreativeFormat().equals(CreativeFormat.VIDEO)){
            logger.error("Creative is not video inside BidRequestResponseCreator,adId:{} ",
                    responseAdInfo.getAdId());
            return null;
        }
        VideoProps videProps = responseAdInfo.getVideoProps();
        if(videProps==null){
            logger.error("Creative videoProps inside BidRequestResponseCreator,adId:{} ",
                    responseAdInfo.getAdId());
            return null;
        }

        StringBuffer creativeUrl = new StringBuffer(cdnBaseImageUrl);

        if(videProps.getProtocol() == VideoBidResponseProtocols.VAST_3_0_WRAPPER.getCode()){
        	String macroTagUrl = MarkUpHelper.adTagMacroReplace(videProps.getVastTagUrl(), request, responseAdInfo, response, 
        			"",macroPostImpressionBaseClickUrl, videProps.getVast_tag_macro(), videProps.getVast_tag_macro_quote(),
        			"");

            String vastStr = CreateVastWrapper.createWrapperString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                    responseAdInfo.getImpressionId(), macroTagUrl, 
                    trackingUrl.toString(), request.getSite().getPublisherId(), 
                    videProps.getLinearity(), videProps.getCompaniontype(), videProps.getTracking(), 
                    trackingUrl.toString(), logger);
            if(vastStr == null){
                logger.error("Creative vastStr inside BidRequestResponseCreator,adId:{} ",
                        responseAdInfo.getAdId());
                return null;                
            }else{
                logger.debug("VastStr Is");
                logger.debug(vastStr);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("VastStr Is");
                    request.addDebugMessageForTestRequest(vastStr);
                }
                try{
                    return URLEncoder.encode(vastStr,"UTF-8");
                }catch(Exception e){
                    logger.error(e.getMessage(),e);
                }
            }
        }else if(videProps.getProtocol() == VideoBidResponseProtocols.VAST_2_0_WRAPPER.getCode()){
        	String macroTagUrl = MarkUpHelper.adTagMacroReplace(videProps.getVastTagUrl(), request, responseAdInfo, response, 
        			"",macroPostImpressionBaseClickUrl, videProps.getVast_tag_macro(), videProps.getVast_tag_macro_quote(),"");

            String vastStr = CreateVastWrapperTwoDotZero.createWrapperString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                    responseAdInfo.getImpressionId(), macroTagUrl, 
                    trackingUrl.toString(), request.getSite().getPublisherId(), 
                    videProps.getLinearity(), videProps.getCompaniontype(), videProps.getTracking(), 
                    trackingUrl.toString(), logger);
            if(vastStr == null){
                logger.error("Creative vastStr inside BidRequestResponseCreator,adId:{} ",
                        responseAdInfo.getAdId());
                return null;                
            }else{
                logger.debug("VastStr Is");
                logger.debug(vastStr);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("VastStr Is");
                    request.addDebugMessageForTestRequest(vastStr);
                }
                try{
                    ///return URLEncoder.encode(vastStr,"UTF-8");
                    return vastStr;
                }catch(Exception e){
                    logger.error(e.getMessage(),e);
                }
            }
        }

        VideoInfo videoInfo = responseAdInfo.getVideoInfo();
        if(videoInfo == null){
            logger.error("Creative videoInfo null inside BidRequestResponseCreator,adId:{} ",
                          responseAdInfo.getAdId());
            return null;
        }

        creativeUrl.append(videoInfo.getResource_uri());

        if(videProps.getProtocol() == VideoBidResponseProtocols.VAST_2_0.getCode()){
            String bitRateStr = null;
            if(videProps.getBitrate()!=-1){
            	bitRateStr = videProps.getBitrate()+"";
            }
            String deliveryStr = null;
            ContentDeliveryMethods cdmethods = ContentDeliveryMethods.getEnum(videProps.getDelivery());
            if(cdmethods != null && cdmethods != ContentDeliveryMethods.Unknown){
            	deliveryStr = cdmethods.getName();
            }
            
            String vastStr = CreateVastNormalTwoDotZero.createVastNormalString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
            		responseAdInfo.getImpressionId(),trackingUrl.toString(), request.getSite().getPublisherId(), videProps.getLinearity(), 
            		videProps.getCompaniontype(), videProps.getTracking(), trackingUrl.toString(),logger, responseAdInfo.getAdId()+"", 
            		convertDurationStr(videProps.getDuration()),clickUrl.toString(), creativeUrl.toString(), creative.getCreativeGuid(), deliveryStr, 
            		VideoMimeTypes.getEnum(videProps.getMime()).getMime(), bitRateStr, videProps.getWidth(), videProps.getHeight());
            if(vastStr == null){
                logger.error("Creative vastStr inside BidRequestResponseCreator,adId:{} ",
                        responseAdInfo.getAdId());
                return null;                
            }else{
                logger.debug("VastStr Is");
                logger.debug(vastStr);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("VastStr Is");
                    request.addDebugMessageForTestRequest(vastStr);
                }
                try{
                    ///return URLEncoder.encode(vastStr,"UTF-8");
                    return vastStr;
                }catch(Exception e){
                    logger.error(e.getMessage(),e);
                }
            }
        }else if(videProps.getProtocol() == VideoBidResponseProtocols.VAST_3_0.getCode()){
            String bitRateStr = null;
            if(videProps.getBitrate()!=-1){
            	bitRateStr = videProps.getBitrate()+"";
            }
            String deliveryStr = null;
            ContentDeliveryMethods cdmethods = ContentDeliveryMethods.getEnum(videProps.getDelivery());
            if(cdmethods != null && cdmethods != ContentDeliveryMethods.Unknown){
            	deliveryStr = cdmethods.getName();
            }
            
            String vastStr = CreateVastNormalThreeDotZero.createVastNormalString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
            		responseAdInfo.getImpressionId(),trackingUrl.toString(), request.getSite().getPublisherId(), videProps.getLinearity(), 
            		videProps.getCompaniontype(), videProps.getTracking(), trackingUrl.toString(),logger, responseAdInfo.getAdId()+"", 
            		convertDurationStr(videProps.getDuration()),clickUrl.toString(), creativeUrl.toString(), creative.getCreativeGuid(), deliveryStr, 
            		VideoMimeTypes.getEnum(videProps.getMime()).getMime(), bitRateStr, videProps.getWidth(), videProps.getHeight(),
            		null);
            if(vastStr == null){
                logger.error("Creative vastStr inside BidRequestResponseCreator,adId:{} ",
                        responseAdInfo.getAdId());
                return null;                
            }else{
                logger.debug("VastStr Is");
                logger.debug(vastStr);
                if(request.isRequestForSystemDebugging()){
                    request.addDebugMessageForTestRequest("VastStr Is");
                    request.addDebugMessageForTestRequest(vastStr);
                }
                try{
                    ///return URLEncoder.encode(vastStr,"UTF-8");
                    return vastStr;
                }catch(Exception e){
                    logger.error(e.getMessage(),e);
                }
            }
        }
        return null;
/**
 http://adserver.com/crossdomain.xml
 <cross-domain-policy>
<allow-access-from domain=”*”>
<cross-domain-policy>

Access-Control-Allow-Origin: <origin header value>
Access-Control-Allow-Credentials: true

            
    
 */
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
