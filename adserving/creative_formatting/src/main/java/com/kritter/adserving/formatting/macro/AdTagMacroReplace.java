package com.kritter.adserving.formatting.macro;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

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
    public static List<String> adTagMacroReplace(List<String> list,Request request,
            ResponseAdInfo responseAdInfo, Response response, String macroClickUrl,Set<Integer> macroSet,Integer quote){
    	if(list==null || list.size()<1){
    		return null;
    	}
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
    	LinkedList<String> newList = new LinkedList<String>();
    	for(String str:list){
    		String s = adTagMacroReplace(str, creativeMacro, request, responseAdInfo, response, macroClickUrl);
    		if(s !=null){
    			newList.add(s);
    		}
    	}
    	return newList;
    }

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
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.AD_HEIGHT.getName(), quote+request.getRequestedSlotHeights()[0]+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.AD_HEIGHT.getName(),defaultReplace);
                        }
                        break;
                    case AD_WIDTH:
                        if(request.getRequestedSlotWidths() != null && request.getRequestedSlotWidths().length> 0){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.AD_HEIGHT.getName(), quote+request.getRequestedSlotWidths()[0]+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.AD_WIDTH.getName(),defaultReplace);
                        }
                        break;
                    case AD_ID:
                        if(responseAdInfo.getGuid() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.AD_ID.getName(),quote+responseAdInfo.getGuid()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.AD_ID.getName(),defaultReplace);
                        }
                        break;
                    case APP_BUNDLE:
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getApplicationId() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_BUNDLE.getName(),quote+request.getSite().getApplicationId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_BUNDLE.getName(),defaultReplace);
                        }
                        break;
                    case APP_CATEGORY:
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getCategoriesArray() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_CATEGORY.getName(),quote+request.getSite().getCategoriesArray().toString()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_CATEGORY.getName(),defaultReplace);
                        }
                        break;
                    case APP_NAME:
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getName() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_NAME.getName(),quote+request.getSite().getName()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_NAME.getName(),defaultReplace);
                        }
                        break;
                    case APP_STOREURL:
                        if(request.getSite() != null && SITE_PLATFORM.APP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getSiteUrl() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_STOREURL.getName(),quote+request.getSite().getSiteUrl()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_STOREURL.getName(),defaultReplace);
                        }
                        break;
                    case APP_VERSION:
                        strTemp = StringUtils.replace(strTemp,ADTagMacros.APP_VERSION.getName(),defaultReplace);
                        break;
                    case CAMPAIGN_ID:
                        if(responseAdInfo.getCampaignId() != null ){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.CAMPAIGN_ID.getName(),quote+responseAdInfo.getCampaignId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.CAMPAIGN_ID.getName(),defaultReplace);
                        }
                        break;
                    case CLICK_ID:
                        if(responseAdInfo.getImpressionId() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.CLICK_ID.getName(),quote+responseAdInfo.getImpressionId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.CLICK_ID.getName(),defaultReplace);
                        }
                        break;
                    case DEVICE_IP:
                        if(request.getIpAddressUsedForDetection() != null ){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_IP.getName(),quote+request.getIpAddressUsedForDetection()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_IP.getName(),defaultReplace);
                        }
                        break;
                    case DEVICE_OS:
                        if(request.getHandsetMasterData() != null && request.getHandsetMasterData().getDeviceOperatingSystemId()!= null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_OS.getName(),quote+request.getHandsetMasterData().getDeviceOperatingSystemId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_OS.getName(),defaultReplace);
                        }
                        break;
                    case DEVICE_UA:
                        if(request.getUserAgent() != null ){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_UA.getName(),quote+request.getUserAgent()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_UA.getName(),defaultReplace);
                        }
                        break;
                    case EXCHANGE:
                        strTemp = StringUtils.replace(strTemp,ADTagMacros.EXCHANGE.getName(),defaultReplace);
                        break;
                    case PAGE_URL:
                        strTemp = StringUtils.replace(strTemp,ADTagMacros.PAGE_URL.getName(),defaultReplace);
                        break;
                    case SITE_DOMAIN:
                        if(request.getSite() != null && SITE_PLATFORM.WAP.getPlatform() ==  request.getSite().getSitePlatform() && request.getSite().getSiteUrl() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.SITE_DOMAIN.getName(),quote+request.getSite().getSiteUrl()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.SITE_DOMAIN.getName(),defaultReplace);
                        }
                        break;
                    case TIMESTAMP:
                        strTemp = StringUtils.replace(strTemp,ADTagMacros.TIMESTAMP.getName(),quote+System.currentTimeMillis()/1000+quote);
                        break;
                    case USER_COUNTRY:
                        if(request.getCountryUserInterfaceId() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.USER_COUNTRY.getName(),quote+request.getCountryUserInterfaceId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.USER_COUNTRY.getName(),defaultReplace);
                        }
                        break;
                    case USER_GEO_LAT:
                        if(request.getRequestingLatitudeValue() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.USER_GEO_LAT.getName(),quote+request.getRequestingLatitudeValue()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.USER_GEO_LAT.getName(),defaultReplace);
                        }
                        break;
                    case USER_GEO_LNG:
                        if(request.getRequestingLongitudeValue() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.USER_GEO_LNG.getName(),quote+request.getRequestingLongitudeValue()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.USER_GEO_LNG.getName(),defaultReplace);
                        }
                        break;
                    case DEVICE_ID:
                        if(request.getUserId() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_ID.getName(),quote+request.getUserId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DEVICE_ID.getName(),defaultReplace);
                        }
                        break;
                    case CLICK_URL:
                        if(macroClickUrl != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.CLICK_URL.getName(),quote+macroClickUrl+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.CLICK_URL.getName(),defaultReplace);
                        }
                        break;
                    case SECURE_CLICK_URL:
                        if(macroClickUrl != null){
                            String secureMacroClickUrl = macroClickUrl.replaceFirst("http", "https");
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.SECURE_CLICK_URL.getName(),quote+secureMacroClickUrl+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.SECURE_CLICK_URL.getName(),defaultReplace);
                        }
                        break;
                    case RANDOM:
                        String a = quote+System.nanoTime()+quote;
                        strTemp = StringUtils.replace(strTemp,ADTagMacros.RANDOM.getName(),a);
                        break;
                    case CACHEBUSTER:
                        String b = quote+System.nanoTime()+quote;
                        strTemp = StringUtils.replace(strTemp,ADTagMacros.CACHEBUSTER.getName(),b);
                        break;
                    case PUB_GUID:
                        if(request.getSite() != null && request.getSite().getPublisherId() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.PUB_GUID.getName(),quote+request.getSite().getPublisherId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.PUB_GUID.getName(),defaultReplace);
                        }
                    	break;
                    case DO_NOT_TRACK:
                        if(request.getDoNotTrack() != null && request.getDoNotTrack()){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DO_NOT_TRACK.getName(),quote+"True"+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.DO_NOT_TRACK.getName(),defaultReplace);
                        }
                    	break;
                    case REFERER:
                        if(request.getSite() != null && request.getSite().getReferer() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.REFERER.getName(),quote+request.getSite().getReferer()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.REFERER.getName(),defaultReplace);
                        }
                    	break;
                    case EXTAPPID:
                        if(request.getSite() != null && request.getSite().getExternalSupplyId() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.EXTAPPID.getName(),quote+request.getSite().getExternalSupplyId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.EXTAPPID.getName(),defaultReplace);
                        }
                        break;
                    case EXTSITEID:
                        if(request.getSite() != null && request.getSite().getExternalSupplyId() != null){
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.EXTSITEID.getName(),quote+request.getSite().getExternalSupplyId()+quote);
                        }else{
                            strTemp = StringUtils.replace(strTemp,ADTagMacros.EXTSITEID.getName(),defaultReplace);
                        }
                        break;
                    case IMEI:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.DEVICE_ID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.IMEI.getName(),quote+useridMap.get(ExternalUserIdType.DEVICE_ID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.IMEI.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.IMEI.getName(),defaultReplace);
                    	}
                    	break;
                    case IMEIMD5:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.MD5_DEVICE_ID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.IMEIMD5.getName(),quote+useridMap.get(ExternalUserIdType.MD5_DEVICE_ID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.IMEIMD5.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.IMEIMD5.getName(),defaultReplace);
                    	}
                    	break;
                    case MAC:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.MAC) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.MAC.getName(),quote+useridMap.get(ExternalUserIdType.MAC)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.MAC.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.MAC.getName(),defaultReplace);
                    	}
                    	break;
                    case IDFA:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.IFA_USER_ID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.IDFA.getName(),quote+useridMap.get(ExternalUserIdType.IFA_USER_ID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.IDFA.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.IDFA.getName(),defaultReplace);
                    	}
                    	break;
                    case AAID:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.AAID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.AAID.getName(),quote+useridMap.get(ExternalUserIdType.AAID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.AAID.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.AAID.getName(),defaultReplace);
                    	}
                    	break;
                    case OpenUDID:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.OPENUDID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.OpenUDID.getName(),quote+useridMap.get(ExternalUserIdType.OPENUDID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.OpenUDID.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.OpenUDID.getName(),defaultReplace);
                    	}
                    	break;
                    case AndroidID:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.DEVICE_PLATFORM_ID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.AndroidID.getName(),quote+useridMap.get(ExternalUserIdType.DEVICE_PLATFORM_ID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.AndroidID.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.AndroidID.getName(),defaultReplace);
                    	}
                    	break;
                    case AndroidIDMD5:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.AndroidIDMD5.getName(),quote+useridMap.get(ExternalUserIdType.MD5_DEVICE_PLATFORM_ID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.AndroidIDMD5.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.AndroidIDMD5.getName(),defaultReplace);
                    	}
                    	break;
                    case UDID:
                    	if(!userIdPopulated){
                    		useridMap=getUserId(request.getExternalUserIds());
                    		userIdPopulated=true;
                    	}
                    	if(useridMap != null){
                    		if(useridMap.get(ExternalUserIdType.UDID) != null){
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.UDID.getName(),quote+useridMap.get(ExternalUserIdType.UDID)+quote);
                    		}else{
                    			strTemp = StringUtils.replace(strTemp,ADTagMacros.UDID.getName(),defaultReplace);
                    		}
                    	}else{
                    		strTemp = StringUtils.replace(strTemp,ADTagMacros.UDID.getName(),defaultReplace);
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
