package com.kritter.ex_int.utils.richmedia;


import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.ex_int.utils.richmedia.markuphelper.MarkUpHelper;
import com.kritter.constants.ExternalUserIdType;
import org.apache.logging.log4j.Logger;

import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;

import java.util.Set;

public class RichMediaAdMarkUp {
    
    private static final String defaultReplaceOrig = ""; 

    public static String prepareRichmediaAdMarkup( Request request,
                 ResponseAdInfo responseAdInfo, Response response,
                 StringBuffer winNotificationURLBuffer,Logger logger,
                 int urlVersion, String secretKey, String postImpressionBaseClickUrl, 
                 String postImpressionBaseCSCUrl, String postImpressionBaseWinApiUrl,
                 String notificationUrlSuffix,
                 String notificationUrlBidderBidPriceMacro, String template,
                 String appCategory, CreativeMacro creativeMacro,
                 String macroPostImpressionBaseClickUrl,
                 ExtTracker extTracker
          ) throws BidResponseException {
              String clickUri = CreativeFormatterUtils.prepareClickUri(
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
              StringBuffer macroClickUrl = new StringBuffer(macroPostImpressionBaseClickUrl);
              macroClickUrl.append(clickUri);
      
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
              Set<ExternalUserId> externalUserIdSet = request.getExternalUserIds();
              String exchangeUserId = null;
              if(null != externalUserIdSet)
              {
                  for(ExternalUserId externalUserId : externalUserIdSet)
                  {
                      if(externalUserId.getIdType().equals(
                              ExternalUserIdType.EXCHANGE_CONSUMER_ID))
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


        if(null == responseAdInfo.getRichMediaPayLoadFromCreative())
              {
                  logger.error("Richmedia content is null inside BidRequestResponseCreator,cannot format ad, for adid: " +
                          responseAdInfo.getAdId());
                  return null;
              }
      
              String richmediaResponse = null;
              if(template == null){
                  richmediaResponse = ApplicationGeneralUtils.HTML_RICHMEDIA_TEMPLATE.replace
                      (ApplicationGeneralUtils.CREATIVE_CSC_BEACON,cscBeaconUrl.toString());
              }else{
                  richmediaResponse = template.replace
                          (ApplicationGeneralUtils.CREATIVE_CSC_BEACON,cscBeaconUrl.toString());
              }
      
              String str = MarkUpHelper.adTagMacroReplace(responseAdInfo.getRichMediaPayLoadFromCreative(), 
                      creativeMacro, request, responseAdInfo, response, appCategory, macroClickUrl.toString(),defaultReplaceOrig);
              richmediaResponse = richmediaResponse.replace(ApplicationGeneralUtils.RICHMEDIA_PAYLOAD,
                      str);
              if(extTracker != null && extTracker.getImpTracker() != null){
                  StringBuffer sBuff = new StringBuffer("");
                  for(String str1:extTracker.getImpTracker()){
                      sBuff.append("<img src=\"");
                      sBuff.append(MarkUpHelper.adTagMacroReplace(str1, request, responseAdInfo, response, appCategory, macroClickUrl.toString(), extTracker.getImpMacro(), extTracker.getImpMacroQuote(),defaultReplaceOrig));
                      sBuff.append("\" style=\"display: none;\"/>");
                  }
                  richmediaResponse=richmediaResponse+sBuff.toString();
              }

              return richmediaResponse;
          }
}
