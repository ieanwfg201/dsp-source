package com.kritter.ex_int.banner_admarkup.common;

import java.util.Set;

import org.slf4j.Logger;

import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.serving.demand.entity.Creative;
import com.kritter.utils.common.ApplicationGeneralUtils;

public class BannerAdMarkUp {

    public static String prepare(Logger logger, Request request, Response response,
            ResponseAdInfo responseAdInfo, int urlVersion, String secretKey,
            String postImpressionBaseClickUrl, String postImpressionBaseWinApiUrl,
            String notificationUrlSuffix, String notificationUrlBidderBidPriceMacro,
            String postImpressionBaseCSCUrl, String cdnBaseImageUrl,
            boolean templateWithWin, ExtTracker extTracker, StringBuffer winNotificationURLBuffer,
            String extraClikUrlSuffix
            ) throws BidResponseException{

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

        StringBuffer clickUrl = new StringBuffer(postImpressionBaseClickUrl);
        clickUrl.append(clickUri);
        if(extraClikUrlSuffix != null){
        	if(clickUri.contains("?")){
        		clickUrl.append("&");
        	}else{
        		clickUrl.append("?");
        	}
        	clickUrl.append(extraClikUrlSuffix);
        }
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

        StringBuffer cscBeaconUrl = new StringBuffer(postImpressionBaseCSCUrl);
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
        String HTML_BANNER_TEMPLATE =null;
        if(templateWithWin){
            HTML_BANNER_TEMPLATE = prepareHTMLBannerTemplateWithWin();
        }else{
            HTML_BANNER_TEMPLATE = prepareHTMLBannerTemplate();
        }
        String htmlBannerResponse = HTML_BANNER_TEMPLATE.replace
                (CreativeFormatterUtils.CLICK_URL_MACRO,clickUrl.toString());
        if(templateWithWin){
            htmlBannerResponse = htmlBannerResponse.replace
                (CreativeFormatterUtils.WIN_NOTIFICATION_URL,winNotificationURLBuffer.toString());
        }

        htmlBannerResponse = htmlBannerResponse.replace
                (CreativeFormatterUtils.CREATIVE_CSC_BEACON,cscBeaconUrl.toString());

        StringBuffer creativeImageUrl = new StringBuffer(cdnBaseImageUrl);
        creativeImageUrl.append(responseAdInfo.getCreativeBanner().getResourceURI());
        htmlBannerResponse = htmlBannerResponse.replace
                (CreativeFormatterUtils.CREATIVE_IMAGE_URL,creativeImageUrl.toString());

        if(null != responseAdInfo.getCreative().getText())
            htmlBannerResponse = htmlBannerResponse.replace(
                                                            CreativeFormatterUtils.CREATIVE_ALT_TEXT,
                                                            creative.getText()
                                                           );
        if(extTracker != null && extTracker.getImpTracker() != null){
            StringBuffer sBuff = new StringBuffer("");
            for(String str:extTracker.getImpTracker()){
                sBuff.append("<img src=\"");
                sBuff.append(str);
                sBuff.append("\" style=\"display: none;\"/>");
            }
            htmlBannerResponse=htmlBannerResponse+sBuff.toString();
        }
        return htmlBannerResponse;
    }
    private static String prepareHTMLBannerTemplate()
    {
        StringBuffer sb = new StringBuffer("<a href=\"");
        sb.append(CreativeFormatterUtils.CLICK_URL_MACRO);
        sb.append("\"><img src=\"");
        sb.append(CreativeFormatterUtils.CREATIVE_IMAGE_URL);
        sb.append("\" alt=\"");
        sb.append(CreativeFormatterUtils.CREATIVE_ALT_TEXT);
        sb.append("\"/></a><img src=\"");
        sb.append(CreativeFormatterUtils.CREATIVE_CSC_BEACON);
        sb.append("\" style=\"display: none;\"/>");
        return sb.toString();
    }
    private static String prepareHTMLBannerTemplateWithWin()
    {
        StringBuffer sb = new StringBuffer("<a href=\"");
        sb.append(CreativeFormatterUtils.CLICK_URL_MACRO);
        sb.append("\"><img src=\"");
        sb.append(CreativeFormatterUtils.CREATIVE_IMAGE_URL);
        sb.append("\" alt=\"");
        sb.append(CreativeFormatterUtils.CREATIVE_ALT_TEXT);
        sb.append("\"/></a><img src=\"");
        sb.append(CreativeFormatterUtils.WIN_NOTIFICATION_URL);
        sb.append("\" style=\"display: none;\"/>");
        sb.append("<img src=\"");
        sb.append(CreativeFormatterUtils.CREATIVE_CSC_BEACON);
        sb.append("\" style=\"display: none;\"/>");
        return sb.toString();
    }

}
