package com.kritter.adserving.formatting.macro;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.kritter.constants.ADTagMacros;
import com.kritter.constants.CreativeMacroQuote;
import com.kritter.constants.ExternalUserIdType;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.reqres.entity.Request;
import com.kritter.entity.reqres.entity.Response;
import com.kritter.entity.reqres.entity.ResponseAdInfo;
import com.kritter.entity.user.userid.ExternalUserId;

public class AdTagMacroReplace {
    
    private static final String defaultReplaceOrig = "";
    public static String adTagMacroReplace(String str,Request request,
            ResponseAdInfo responseAdInfo, Response response, String macroClickUrl,Set<Integer> macroSet,Integer quote){
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
    	return adTagMacroReplace(str, creativeMacro, request, responseAdInfo, response, macroClickUrl);
    }
    public static String adTagMacroReplace(String str, CreativeMacro creativeMacro, Request request,
            ResponseAdInfo responseAdInfo, Response response, String macroClickUrl){
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
                        if(request.getRequestedSlotHeights() != null && request.getRequestedSlotHeights().length> 0){
                            strTemp = strTemp.replaceAll(ADTagMacros.AD_HEIGHT.getDesc(), quote+request.getRequestedSlotHeights()[0]+quote);
                        }else{
                            strTemp = strTemp.replaceAll(ADTagMacros.AD_HEIGHT.getDesc(),defaultReplace);
                        }
                        break;
                    case AD_WIDTH:
                        if(request.getRequestedSlotWidths() != null && request.getRequestedSlotWidths().length> 0){
                            strTemp = strTemp.replaceAll(ADTagMacros.AD_HEIGHT.getDesc(), quote+request.getRequestedSlotWidths()[0]+quote);
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
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getApplicationId() != null){
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_BUNDLE.getDesc(),quote+request.getSite().getApplicationId()+quote);
                        }else{
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_BUNDLE.getDesc(),defaultReplace);
                        }
                        break;
                    case APP_CATEGORY:
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getCategoriesArray() != null){
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_CATEGORY.getDesc(),quote+request.getSite().getCategoriesArray().toString()+quote);
                        }else{
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_CATEGORY.getDesc(),defaultReplace);
                        }
                        break;
                    case APP_NAME:
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getName() != null){
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_NAME.getDesc(),quote+request.getSite().getName()+quote);
                        }else{
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_NAME.getDesc(),defaultReplace);
                        }
                        break;
                    case APP_STOREURL:
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getSiteUrl() != null){
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_STOREURL.getDesc(),quote+request.getSite().getSiteUrl()+quote);
                        }else{
                            strTemp = strTemp.replaceAll(ADTagMacros.APP_STOREURL.getDesc(),defaultReplace);
                        }
                        break;
                    case APP_VERSION:
                        strTemp = strTemp.replaceAll(ADTagMacros.APP_VERSION.getDesc(),defaultReplace);
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
                        if(request.getHandsetMasterData() != null && request.getHandsetMasterData().getDeviceOperatingSystemId()!= null){
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
                        strTemp = strTemp.replaceAll(ADTagMacros.EXCHANGE.getDesc(),defaultReplace);
                        break;
                    case PAGE_URL:
                        strTemp = strTemp.replaceAll(ADTagMacros.PAGE_URL.getDesc(),defaultReplace);
                        break;
                    case SITE_DOMAIN:
                        if(request.getSite() != null && SITE_PLATFORM.WAP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getSiteUrl() != null){
                            strTemp = strTemp.replaceAll(ADTagMacros.SITE_DOMAIN.getDesc(),quote+request.getSite().getSiteUrl()+quote);
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
