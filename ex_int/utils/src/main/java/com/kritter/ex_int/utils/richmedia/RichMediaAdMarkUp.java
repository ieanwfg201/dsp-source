package com.kritter.ex_int.utils.richmedia;


import com.kritter.entity.user.userid.ExternalUserId;
import com.kritter.constants.ExternalUserIdType;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;

import com.kritter.bidrequest.exception.BidResponseException;
import com.kritter.constants.ADTagMacros;
import com.kritter.constants.CreativeMacroQuote;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.external_tracker.ExtTracker;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.formatterutil.CreativeFormatterUtils;
import com.kritter.utils.common.ApplicationGeneralUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
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
      
              String str = adTagMacroReplace(responseAdInfo.getRichMediaPayLoadFromCreative(), 
                      creativeMacro, request, responseAdInfo, response, appCategory, macroClickUrl.toString());
              richmediaResponse = richmediaResponse.replace(ApplicationGeneralUtils.RICHMEDIA_PAYLOAD,
                      str);
              if(extTracker != null && extTracker.getImpTracker() != null){
                  StringBuffer sBuff = new StringBuffer("");
                  for(String str1:extTracker.getImpTracker()){
                      sBuff.append("<img src=\"");
                      sBuff.append(str1);
                      sBuff.append("\" style=\"display: none;\"/>");
                  }
                  richmediaResponse=richmediaResponse+sBuff.toString();
              }

              return richmediaResponse;
          }
    public static String adTagMacroReplace(String str,Request request,
            ResponseAdInfo responseAdInfo, Response response, String appCategory, String macroClickUrl,Set<Integer> macroSet,Integer quote){
    	CreativeMacro creativeMacro = new CreativeMacro();
    	if(macroSet != null){
    		if(quote != null){
    		creativeMacro.setQuote(quote);
    		}
    		if(macroSet != null){
    			LinkedList<Integer> l = new LinkedList<Integer>();
    			l.addAll(macroSet);
    			creativeMacro.setMacroIds(l);
    		}
    	}
    	return adTagMacroReplace(str, creativeMacro, request, responseAdInfo, response,appCategory, macroClickUrl);

    	}      
      public static String adTagMacroReplace(String str, CreativeMacro creativeMacro, Request request,
              ResponseAdInfo responseAdInfo, Response response, String appCategory, String macroClickUrl){
          if(creativeMacro == null || creativeMacro.getMacroIds() == null || creativeMacro.getMacroIds().size() < 1){
              return str;
          }
          String quote = CreativeMacroQuote.getEnum(creativeMacro.getQuote()).getName();
          String defaultReplace = quote+defaultReplaceOrig+quote;
          String strTemp = str;
          Map<ExternalUserIdType, String> useridMap =null;
          boolean userIdPopulated = false;
          for(Integer i: creativeMacro.getMacroIds()){
              ADTagMacros adTagMacro = ADTagMacros.getEnum(i);
              if(adTagMacro != null){
                  switch(adTagMacro){
                      case AD_HEIGHT:
                          if(request.getRequestedSlotHeights()!= null && request.getRequestedSlotHeights().length> 0){
                              strTemp = strTemp.replaceAll(ADTagMacros.AD_HEIGHT.getDesc(), quote+request.getRequestedSlotHeights()[0]+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.AD_HEIGHT.getDesc(),defaultReplace);
                          }
                          break;
                      case AD_WIDTH:
                          if(request.getRequestedSlotWidths() != null && request.getRequestedSlotWidths().length> 0){
                              strTemp = strTemp.replaceAll(ADTagMacros.AD_WIDTH.getDesc(), quote+request.getRequestedSlotWidths()[0]+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.AD_WIDTH.getDesc(),defaultReplace);
                          }
                          break;
                      case AD_ID:
                          if(responseAdInfo.getGuid() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.AD_ID.getDesc(),quote+responseAdInfo.getGuid()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.AD_ID.getDesc(),defaultReplace);
                          }
                          break;
                      case APP_BUNDLE:
                          if(request.getSite() != null && request.getSite().getSitePlatform() == SITE_PLATFORM.APP.getPlatform() && request.getSite().getExternalAppBundle() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_BUNDLE.getDesc(),quote+request.getSite().getExternalAppBundle()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_BUNDLE.getDesc(),defaultReplace);
                          }
                          break;
                      case APP_CATEGORY:
                          if(request.getSite() != null && request.getSite().getSitePlatform() == SITE_PLATFORM.APP.getPlatform() && request.getSite().getExternalCategories() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_CATEGORY.getDesc(),quote+ArrayUtils.toString(request.getSite().getExternalCategories())+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_CATEGORY.getDesc(),defaultReplace);
                          }
                          break;
                      case APP_NAME:
                          if(request.getSite() != null && request.getSite().getSitePlatform() == SITE_PLATFORM.APP.getPlatform() && request.getSite().getExternalSupplyName() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_NAME.getDesc(),quote+request.getSite().getExternalSupplyName()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_NAME.getDesc(),defaultReplace);
                          }
                          break;
                      case APP_STOREURL:
                          if(request.getSite() != null && request.getSite().getSitePlatform() == SITE_PLATFORM.APP.getPlatform() && request.getSite().getExternalSupplyUrl() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_STOREURL.getDesc(),quote+request.getSite().getExternalSupplyUrl()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_STOREURL.getDesc(),defaultReplace);
                          }
                          break;
                      case APP_VERSION:
                          if(request.getSite() != null && request.getSite().getSitePlatform() == SITE_PLATFORM.APP.getPlatform() && request.getSite().getExternalAppVersion() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_VERSION.getDesc(),quote+request.getSite().getExternalAppVersion()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.APP_VERSION.getDesc(),defaultReplace);
                          }
                          break;
                      case CAMPAIGN_ID:
                          if(responseAdInfo.getCampaignId() != null ){
                              strTemp = strTemp.replaceAll(ADTagMacros.CAMPAIGN_ID.getDesc(),quote+responseAdInfo.getCampaignId()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.CAMPAIGN_ID.getDesc(),defaultReplace);
                          }
                          break;
                      case CLICK_ID:
                          if(responseAdInfo.getImpressionId() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.CLICK_ID.getDesc(),quote+responseAdInfo.getImpressionId()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.CLICK_ID.getDesc(),defaultReplace);
                          }
                          break;
                      case DEVICE_IP:
                          if(request.getIpAddressUsedForDetection() != null ){
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_IP.getDesc(),quote+request.getIpAddressUsedForDetection()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_IP.getDesc(),defaultReplace);
                          }
                          break;
                      case DEVICE_OS:
                          if(request.getHandsetMasterData() != null && request.getHandsetMasterData().getDeviceOperatingSystemId() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_OS.getDesc(),quote+request.getHandsetMasterData().getDeviceOperatingSystemId()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_OS.getDesc(),defaultReplace);
                          }
                          break;
                      case DEVICE_UA:
                          if(request.getUserAgent() != null ){
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_UA.getDesc(),quote+request.getUserAgent()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_UA.getDesc(),defaultReplace);
                          }
                          break;
                      case EXCHANGE:
                          if(request.getSite() != null && request.getSite().getPublisherId() != null ){
                              strTemp = strTemp.replaceAll(ADTagMacros.EXCHANGE.getDesc(),quote+request.getSite().getPublisherId()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.EXCHANGE.getDesc(),defaultReplace);
                          }
                          break;
                      case PAGE_URL:
                          if(request.getSite() != null && request.getSite().getExternalPageUrl() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.PAGE_URL.getDesc(),quote+request.getSite().getExternalPageUrl()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.PAGE_URL.getDesc(),defaultReplace);
                          }
                          break;
                      case SITE_DOMAIN:
                          if(request.getSite() != null && request.getSite().getSitePlatform() == SITE_PLATFORM.WAP.getPlatform() && request.getSite().getExternalSupplyDomain() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.SITE_DOMAIN.getDesc(),quote+request.getSite().getExternalSupplyDomain()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.SITE_DOMAIN.getDesc(),defaultReplace);
                          }
                          break;
                      case TIMESTAMP:
                          strTemp = strTemp.replaceAll(ADTagMacros.TIMESTAMP.getDesc(),quote+System.currentTimeMillis()/1000+quote);
                          break;
                      case USER_COUNTRY:
                          if(request.getCountryUserInterfaceId() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.USER_COUNTRY.getDesc(),quote+request.getCountryUserInterfaceId()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.USER_COUNTRY.getDesc(),defaultReplace);
                          }
                          break;
                      case USER_GEO_LAT:
                          if(request.getRequestingLatitudeValue() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.USER_GEO_LAT.getDesc(),quote+request.getRequestingLatitudeValue()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.USER_GEO_LAT.getDesc(),defaultReplace);
                          }
                          break;
                      case USER_GEO_LNG:
                          if(request.getRequestingLongitudeValue() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.USER_GEO_LNG.getDesc(),quote+request.getRequestingLongitudeValue()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.USER_GEO_LNG.getDesc(),defaultReplace);
                          }
                          break;
                      case DEVICE_ID:
                          if(request.getUserId() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_ID.getDesc(),quote+request.getUserId()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.DEVICE_ID.getDesc(),defaultReplace);
                          }
                          break;
                      case CLICK_URL:
                          if(macroClickUrl != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.CLICK_URL.getDesc(),quote+macroClickUrl+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.CLICK_URL.getDesc(),defaultReplace);
                          }
                          break;
                      case SECURE_CLICK_URL:
                          if(macroClickUrl != null){
                              String secureMacroClickUrl = macroClickUrl.replaceFirst("http", "https");
                              strTemp = strTemp.replaceAll(ADTagMacros.SECURE_CLICK_URL.getDesc(),quote+secureMacroClickUrl+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.SECURE_CLICK_URL.getDesc(),defaultReplace);
                          }
                          break;
                      case RANDOM:
                          String a = quote+System.nanoTime()+quote;
                          strTemp = strTemp.replaceAll(ADTagMacros.RANDOM.getDesc(),a);
                          break;
                      case CACHEBUSTER:
                          String b = quote+System.nanoTime()+quote;
                          strTemp = strTemp.replaceAll(ADTagMacros.CACHEBUSTER.getDesc(),b);
                          break;
                      case PUB_GUID:
                          if(request.getSite() != null && request.getSite().getPublisherId() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.PUB_GUID.getDesc(),quote+request.getSite().getPublisherId()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.PUB_GUID.getDesc(),defaultReplace);
                          }
                      	break;
                      case DO_NOT_TRACK:
                          if(request.getDoNotTrack() != null && request.getDoNotTrack()){
                              strTemp = strTemp.replaceAll(ADTagMacros.DO_NOT_TRACK.getDesc(),quote+"True"+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.DO_NOT_TRACK.getDesc(),defaultReplace);
                          }
                      	break;
                      case REFERER:
                          if(request.getSite() != null && request.getSite().getReferer() != null){
                              strTemp = strTemp.replaceAll(ADTagMacros.REFERER.getDesc(),quote+request.getSite().getReferer()+quote);
                          }else{
                              strTemp = strTemp.replaceAll(ADTagMacros.REFERER.getDesc(),defaultReplace);
                          }
                      	break;
                      case IMEI:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.DEVICE_ID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.IMEI.getDesc(),quote+useridMap.get(ExternalUserIdType.DEVICE_ID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.IMEI.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.IMEI.getDesc(),defaultReplace);
                      	}
                      	break;
                      case IMEIMD5:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.MD5_DEVICE_ID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.IMEIMD5.getDesc(),quote+useridMap.get(ExternalUserIdType.MD5_DEVICE_ID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.IMEIMD5.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.IMEIMD5.getDesc(),defaultReplace);
                      	}
                      	break;
                      case MAC:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.MAC) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.MAC.getDesc(),quote+useridMap.get(ExternalUserIdType.MAC)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.MAC.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.MAC.getDesc(),defaultReplace);
                      	}
                      	break;
                      case IDFA:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.IFA_USER_ID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.IDFA.getDesc(),quote+useridMap.get(ExternalUserIdType.IFA_USER_ID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.IDFA.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.IDFA.getDesc(),defaultReplace);
                      	}
                      	break;
                      case AAID:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.AAID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.AAID.getDesc(),quote+useridMap.get(ExternalUserIdType.AAID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.AAID.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.AAID.getDesc(),defaultReplace);
                      	}
                      	break;
                      case OpenUDID:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.OPENUDID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.OpenUDID.getDesc(),quote+useridMap.get(ExternalUserIdType.OPENUDID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.OpenUDID.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.OpenUDID.getDesc(),defaultReplace);
                      	}
                      	break;
                      case AndroidID:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.DEVICE_PLATFORM_ID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.AndroidID.getDesc(),quote+useridMap.get(ExternalUserIdType.DEVICE_PLATFORM_ID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.AndroidID.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.AndroidID.getDesc(),defaultReplace);
                      	}
                      	break;
                      case AndroidIDMD5:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.AndroidIDMD5.getDesc(),quote+useridMap.get(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.AndroidIDMD5.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.AndroidIDMD5.getDesc(),defaultReplace);
                      	}
                      	break;
                      case UDID:
                      	if(!userIdPopulated){
                      		useridMap=getUserId(request.getExternalUserIds());
                      		userIdPopulated=true;
                      	}
                      	if(useridMap != null){
                      		if(useridMap.get(ExternalUserIdType.UDID) != null){
                      			strTemp = strTemp.replaceAll(ADTagMacros.UDID.getDesc(),quote+useridMap.get(ExternalUserIdType.UDID)+quote);
                      		}else{
                      			strTemp = strTemp.replaceAll(ADTagMacros.UDID.getDesc(),defaultReplace);
                      		}
                      	}else{
                      		strTemp = strTemp.replaceAll(ADTagMacros.UDID.getDesc(),defaultReplace);
                      	}
                      	break;
                      default:
                          break;
                  }
              }
          }
          return strTemp;
      }
      private static Map<ExternalUserIdType, String> getUserId(Set<ExternalUserId> userIds){
      	if(userIds != null && userIds.size()>0){
      		Map<ExternalUserIdType, String>  map= new HashMap<ExternalUserIdType, String>();
      		for(ExternalUserId user:userIds){
      			if(user != null && user.getIdType() != null){
      				map.put(user.getIdType(), user.getUserId());
      			}
      		}
      		return  map;
      	}
      	return null;
      }
}
