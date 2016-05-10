package com.kritter.ex_int.video_admarkup;

import java.net.URLEncoder;

import org.slf4j.Logger;
import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.vast.wrapper.three_dot_zero.CreateVastWrapper;
import com.kritter.entity.vast.wrapper.two_dot_zero.CreateVastWrapperTwoDotZero;
import com.kritter.entity.video_props.VideoProps;
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
            String impressionExtra, String beventBaseUrl
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
        if(videProps.getProtocol() == VideoBidResponseProtocols.VAST_3_0_WRAPPER.getCode()){
            String vastStr = CreateVastWrapper.createWrapperString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                    responseAdInfo.getImpressionId(), videProps.getVastTagUrl(), 
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
            String vastStr = CreateVastWrapperTwoDotZero.createWrapperString(cscBeaconUrl.toString(), responseAdInfo.getGuid(), 
                    responseAdInfo.getImpressionId(), videProps.getVastTagUrl(), 
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
}
