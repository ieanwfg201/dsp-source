package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import com.kritter.constants.*;
import models.formelements.SelectOption;
import play.Logger;
import play.Play;
import play.db.DB;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountList;
import com.kritter.api.entity.deal.ThirdPartyConnectionChildIdList;
import com.kritter.api.entity.deal.ThirdPartyConnectionChildId;
import com.kritter.api.entity.account.ListEntity;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.ext_site.Ext_site;
import com.kritter.api.entity.ext_site.Ext_site_input;
import com.kritter.api.entity.ext_site.Ext_site_list;
import com.kritter.api.entity.metadata.MetaField;
import com.kritter.api.entity.metadata.MetaInput;
import com.kritter.api.entity.metadata.MetaList;
import com.kritter.api.entity.payoutthreshold.PayoutThresholdList;
import com.kritter.api.entity.payoutthreshold.PayoutThresholdListEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentInputEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentList;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.constants.tracking_partner.TrackingPartner;
import com.kritter.entity.demand_props.DemandProps;
import com.kritter.entity.payout_threshold.DefaultPayoutThreshold;
import com.kritter.entity.retargeting_segment.RetargetingSegment;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.postimpression.thrift.struct.PostImpressionEvent;

public class MetadataAPI {

    private static String dp_only_mediation = Play.application().configuration().getString("dp_only_mediation");
    private static String dsp_allowed = Play.application().configuration().getString("dsp_allowed");
    private static String is_native_demand = Play.application().configuration().getString("is_native_demand");
    
	public static List<SelectOption> emptyOptions(){
		List<SelectOption> emptyOptions = new ArrayList<SelectOption>();
		return emptyOptions;
	}
	public static String getDemandUrl(DemandProps dp){
	    if(dp != null && dp.getDemand_url() != null){
	        return dp.getDemand_url();
	    }
	    return "";
	}
    public static List<SelectOption> algomodel(){
        List<SelectOption> options = emptyOptions();
        AlgoModel[] types = AlgoModel.values();
        for (AlgoModel type : types) {
            options.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return options;
    }
    public static List<SelectOption> algomodelType(){
        List<SelectOption> options = emptyOptions();
        AlgoModelType[] types = AlgoModelType.values();
        for (AlgoModelType type : types) {
            options.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return options;
    }
	public static List<SelectOption> yesNoOptions(){
		List<SelectOption> options = emptyOptions();
		options.add(new SelectOption("Yes", "true"));
		options.add(new SelectOption("No", "false"));
		return options;
	}
    public static List<SelectOption> noYesOptions(){
        List<SelectOption> options = emptyOptions();
        options.add(new SelectOption("No", "false"));
        options.add(new SelectOption("Yes", "true"));
        return options;
    }
    public static List<SelectOption> videoDemandType(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoDemandType[] types = VideoDemandType.values();
        for (VideoDemandType type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> mime(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoMimeTypes[] types = VideoMimeTypes.values();
        for (VideoMimeTypes type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> protocol(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoBidResponseProtocols[] types = VideoBidResponseProtocols.values();
        for (VideoBidResponseProtocols type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> nonwrapperprotocol(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        selectOptions.add(new SelectOption(VideoBidResponseProtocols.NONVAST.name(),  VideoBidResponseProtocols.NONVAST.getCode()+""));
        selectOptions.add(new SelectOption(VideoBidResponseProtocols.VAST_2_0.name(),  VideoBidResponseProtocols.VAST_2_0.getCode()+""));
        selectOptions.add(new SelectOption(VideoBidResponseProtocols.VAST_3_0.name(),  VideoBidResponseProtocols.VAST_3_0.getCode()+""));
        return selectOptions;
    }
    public static List<SelectOption> wrapperprotocol(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        selectOptions.add(new SelectOption(VideoBidResponseProtocols.VAST_2_0_WRAPPER.name(),  VideoBidResponseProtocols.VAST_2_0_WRAPPER.getCode()+""));
        selectOptions.add(new SelectOption(VideoBidResponseProtocols.VAST_3_0_WRAPPER.name(),  VideoBidResponseProtocols.VAST_3_0_WRAPPER.getCode()+""));
        return selectOptions;
    }
    public static List<String> videoTrackingEvents(String str){
        List<String>  selectOptions = new LinkedList<String>();
        if(str != null){
            String strTrim = str.trim().replaceAll("\\[", "").replaceAll("]", "");
            String strSplit[] = strTrim.split(",");
            for(String s:strSplit){
                if(!"".equals(s)){
                    VASTKritterTrackingEventTypes vtet = VASTKritterTrackingEventTypes.getEnum(Integer.parseInt(s));
                    if(vtet != null){
                        selectOptions.add(vtet.getName());
                    }
                }
            }
            
        }
        return selectOptions;
    }
    public static List<SelectOption> linearity(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoLinearity[] types = VideoLinearity.values();
        for (VideoLinearity type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static String linearitybyid(Integer i){
    	if(i!= null){
    		VideoLinearity v = VideoLinearity.getEnum(i);
    		if(v !=null){
    			return v.getName();
    		}
    	}
    	return "";
    }
    public static List<SelectOption> boxingallowed(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoBoxing[] types = VideoBoxing.values();
        for (VideoBoxing type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> playbackmethod(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoPlaybackMethods[] types = VideoPlaybackMethods.values();
        for (VideoPlaybackMethods type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> delivery(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        ContentDeliveryMethods[] types = ContentDeliveryMethods.values();
        for (ContentDeliveryMethods type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> api(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        APIFrameworks[] types = APIFrameworks.values();
        for (APIFrameworks type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> companiontype(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VASTCompanionTypes[] types = VASTCompanionTypes.values();
        for (VASTCompanionTypes type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> native_layout(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        NativeLayoutId[] types = NativeLayoutId.values();
        for (NativeLayoutId type : types) {
            if(!"7".equals(type.getCode())){
                selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
            }
        } 
        return selectOptions;
    }
    public static List<SelectOption> creativeMacroQuote(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        CreativeMacroQuote[] types = CreativeMacroQuote.values();
        for (CreativeMacroQuote type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> extClickType(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        ExtClickType[] types = ExtClickType.values();
        for (ExtClickType type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> latlonradiusunit(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        LatLonRadiusUnit[] types = LatLonRadiusUnit.values();
        for (LatLonRadiusUnit type : types) {
            selectOptions.add(new SelectOption(type.name(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> videopos(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoAdPos[] types = VideoAdPos.values();
        for (VideoAdPos type : types) {
            selectOptions.add(new SelectOption(type.getName(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static String videoposbyid(Integer i){
    	if(i!= null){
    		VideoAdPos v = VideoAdPos.getEnum(i);
    		if(v !=null){
    			return v.getName();
    		}
    	}
    	return "";
    }
    public static List<SelectOption> videoLinearity(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        VideoLinearity[] types = VideoLinearity.values();
        for (VideoLinearity type : types) {
            selectOptions.add(new SelectOption(type.getName(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> native_screenshot_image_size(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        NativeScreenShotImageSize[] types = NativeScreenShotImageSize.values();
        for (NativeScreenShotImageSize type : types) {
            selectOptions.add(new SelectOption(type.getName(),  type.getCode()+""));
        } 
        return selectOptions;
    }
    public static List<SelectOption> native_icon_image_size(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        NativeIconImageSize[] types = NativeIconImageSize.values();
        for (NativeIconImageSize type : types) {
            selectOptions.add(new SelectOption(type.getName(),  type.getCode()+""));
        } 
        return selectOptions;
    }

    public static List<SelectOption> logeventOptions(){
        List<SelectOption> options = emptyOptions();
        for(PostImpressionEvent event:PostImpressionEvent.values()){
            if(event.equals(PostImpressionEvent.CLICK) || event.equals(PostImpressionEvent.CONVERSION)){
                options.add(new SelectOption(event.name(), event.name()));
            }
        }
        return options;
    }
    public static List<SelectOption> idguidOption(){
        List<SelectOption> options = emptyOptions();
        for(IddefinitionType event:IddefinitionType.values()){
            options.add(new SelectOption(event.name(), event.getCode()+""));
        }
        return options;
    }
    public static List<SelectOption> getdefinitionOption(){
        List<SelectOption> options = emptyOptions();
        for(IddefinitionEnum event:IddefinitionEnum.values()){
            options.add(new SelectOption(event.name(), event.getCode()+""));
        }
        return options;
    }
    public static List<SelectOption> csvDelimiter(){
        List<SelectOption> options = emptyOptions();
        options.add(new SelectOption("COMMA", ","));
        options.add(new SelectOption("PIPE", "|"));
        return options;
    }
	
	 
	public static List<SelectOption> reportTimeRange(){
		List<SelectOption> options = emptyOptions();
		options.add(new SelectOption("12 Hours", "-12"));
		options.add(new SelectOption("24 Hours", "-24"));
		options.add(new SelectOption("3 Days", "-72"));
		options.add(new SelectOption("7 Days", (-24*7)+"" ));
		options.add(new SelectOption("14 Days", (-24*14)+"" ));
		options.add(new SelectOption("30 Days", (-24*30)+"" ));

		return options;
	}
	
	public static ArrayNode hourListOptions(){
		ArrayNode hourOptions = new ArrayNode(JsonNodeFactory.instance);
		for(int i=0; i< 24; i++){
			hourOptions.add(new SelectOption(i+"", i+"").toJson());
		}  
		return hourOptions;
	}
	
	public static List<SelectOption> geographicTargetingOptions(){
		List<SelectOption> options = emptyOptions();
		Geo_Targeting_type[] types = Geo_Targeting_type.values();
		for (Geo_Targeting_type geoTargetingType : types) {
		    String name = geoTargetingType.name();
		    if(!"ZIPCODE".equals(name)){
		        options.add(new SelectOption(name,name));
		    }
		} 
		return options;
	}
    public static ArrayNode geographicTargetingOptionsArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        Geo_Targeting_type[] types = Geo_Targeting_type.values();
        for (Geo_Targeting_type geoTargetingType : types) {
            String name = geoTargetingType.name();
            if(!"ZIPCODE".equals(name)){
                optionNodes.add(new SelectOption(name,name).toJson());
            }
        } 
        return optionNodes;
    }
	
	public static List<SelectOption> listTypeOptions(){
		List<SelectOption> options = emptyOptions();
		options.add(new SelectOption("Inclusion", "false"));
		options.add(new SelectOption("Exclusion", "true"));

		return options;
	}
	public static List<SelectOption> listIncTrue(){
		List<SelectOption> options = emptyOptions();
		options.add(new SelectOption("Inclusion", "true"));
		options.add(new SelectOption("Exclusion", "false"));

		return options;
	}
    public static List<SelectOption> freqCapOptions(){
        List<SelectOption> options = emptyOptions();
        options.add(new SelectOption("No", "false"));
        options.add(new SelectOption("Yes", "true"));

        return options;
    }
    public static List<SelectOption> passback_type(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        SITE_PASSBACK_TYPE[] types = SITE_PASSBACK_TYPE.values();
        for (SITE_PASSBACK_TYPE pt : types) {
            if(!pt.name().startsWith("SSP_WATERFALL_PASSBACK")){
                selectOptions.add(new SelectOption(pt.name(),  pt.getCode()+""));
            }
        } 
        return selectOptions;
    }
    public static List<SelectOption> freqduration(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        FreqDuration[] types = FreqDuration.values();
        for (FreqDuration pt : types) {
        	selectOptions.add(new SelectOption(pt.name(),  pt.getCode()+""));
        } 
        return selectOptions;
    }
    public static ArrayNode passback_typeArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        SITE_PASSBACK_TYPE[] types = SITE_PASSBACK_TYPE.values();
        for (SITE_PASSBACK_TYPE pt : types) {
            if(!pt.name().startsWith("SSP_WATERFALL_PASSBACK")){
                optionNodes.add(new SelectOption(pt.name(), pt.getCode()+"").toJson());
            }
        } 
        return optionNodes;
    }
    public static List<SelectOption> passback_content_type(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        SITE_PASSBACK_CONTENT_TYPE[] types = SITE_PASSBACK_CONTENT_TYPE.values();
        for (SITE_PASSBACK_CONTENT_TYPE pt : types) {
            selectOptions.add(new SelectOption(pt.name(),  pt.getCode()+""));
        } 
        return selectOptions;
    }
    public static ArrayNode passback_content_typeArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        SITE_PASSBACK_CONTENT_TYPE[] types = SITE_PASSBACK_CONTENT_TYPE.values();
        for (SITE_PASSBACK_CONTENT_TYPE pt : types) {
            optionNodes.add(new SelectOption(pt.name(), pt.getCode()+"").toJson());
        } 
        return optionNodes;
    }

	public static List<SelectOption> reportFrequencyOptions(){
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
		Frequency[] types = Frequency.values();
		for (Frequency frequency : types) {
		    if(!frequency.name().startsWith("ADMIN")){
		        selectOptions.add(new SelectOption(frequency.name(), frequency.name()));
		    }
		} 
		return selectOptions;
	}
    public static List<SelectOption> supplySourceTypeOptions(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        SupplySourceTypeEnum[] types = SupplySourceTypeEnum.values();
        selectOptions.add(new SelectOption("NONE", "NONE"));
        selectOptions.add(new SelectOption("ALL", "ALL"));
        for (SupplySourceTypeEnum t : types) {
            if("APP_WAP".equals(t.name())){
            }else {
                selectOptions.add(new SelectOption(t.name(), t.name()));
            }
        } 
        return selectOptions;
    }
    public static List<SelectOption> siteHygieneOptions(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        HygieneCategory[] types = HygieneCategory.values();
        selectOptions.add(new SelectOption("NONE", "NONE"));
        selectOptions.add(new SelectOption("ALL", "ALL"));
        for (HygieneCategory t : types) {
            selectOptions.add(new SelectOption(t.name(), t.name()));
        } 
        return selectOptions;
    }
	public static List<SelectOption> getInvSource(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        INVENTORY_SOURCE[] types = INVENTORY_SOURCE.values();
        for (INVENTORY_SOURCE invSrc : types) {
            if(!invSrc.name().startsWith("DCP")){
                selectOptions.add(new SelectOption(invSrc.name(),invSrc.getCode()+""));
            }
        } 
        return selectOptions;
    }
    public static List<SelectOption> getDemandType(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        DemandType[] types = DemandType.values();
        for (DemandType demandType : types) {
            if(DemandType.DSP == demandType){
                if("true".equals(dsp_allowed)){
                    selectOptions.add(new SelectOption(demandType.name(),demandType.getCode()+""));
                }
            }else{
                selectOptions.add(new SelectOption(demandType.name(),demandType.getCode()+""));
            }
        } 
        return selectOptions;
    }
    public static List<SelectOption> getDemandPreference(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        DemandPreference[] types = DemandPreference.values();
        for (DemandPreference demandPrefernce : types) {
            if(DemandPreference.OnlyMediation == demandPrefernce){
                if("true".equals(dp_only_mediation)){
                    selectOptions.add(new SelectOption(demandPrefernce.name(),demandPrefernce.getCode()+""));
                }
            }else if(DemandPreference.OnlyDSP == demandPrefernce || DemandPreference.DirectThenDSP == demandPrefernce){
                if("true".equals(dsp_allowed)){
                    selectOptions.add(new SelectOption(demandPrefernce.name(),demandPrefernce.getCode()+""));
                }
            }else{
                selectOptions.add(new SelectOption(demandPrefernce.name(),demandPrefernce.getCode()+""));
            }
        } 
        return selectOptions;
    }
    public static ArrayNode getInvSourceArrayNode(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        INVENTORY_SOURCE[] types = INVENTORY_SOURCE.values();
        for (INVENTORY_SOURCE invSrc : types) {
            if(!invSrc.name().startsWith("DCP")){
                optionNodes.add(new SelectOption(invSrc.name(),invSrc.getCode()+"").toJson());
            }
        } 
        return optionNodes;
    }
    public static ArrayNode videomimes(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        VideoMimeTypes[] types = VideoMimeTypes.values();
        for (VideoMimeTypes type : types) {
        	optionNodes.add(new SelectOption(type.name(),type.getCode()+"").toJson());
        } 
        return optionNodes;
    }
    public static ArrayNode videoprotocols(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        VideoBidResponseProtocols[] types = VideoBidResponseProtocols.values();
        for (VideoBidResponseProtocols type : types) {
        	optionNodes.add(new SelectOption(type.name(),type.getCode()+"").toJson());
        } 
        return optionNodes;
    }
    public static ArrayNode videoplaybackmethod(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        VideoPlaybackMethods[] types = VideoPlaybackMethods.values();
        for (VideoPlaybackMethods type : types) {
        	optionNodes.add(new SelectOption(type.name(),type.getCode()+"").toJson());
        } 
        return optionNodes;
    }
    public static ArrayNode videodelivery(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        ContentDeliveryMethods[] types = ContentDeliveryMethods.values();
        for (ContentDeliveryMethods type : types) {
        	optionNodes.add(new SelectOption(type.name(),type.getCode()+"").toJson());
        } 
        return optionNodes;
    }
    public static ArrayNode videoapi(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        APIFrameworks[] types = APIFrameworks.values();
        for (APIFrameworks type : types) {
        	optionNodes.add(new SelectOption(type.name(),type.getCode()+"").toJson());
        } 
        return optionNodes;
    }
    public static List<String> videomimesbyid(String ids){
    	List<String> ll = new LinkedList<String>();
    	if(ids != null){
    		String idTrim=ids.trim().replaceAll("\\[", "").replaceAll("]", "");
    		String split[] = idTrim.split(",");
    		for(String s:split){
    			try{
    				VideoMimeTypes v = VideoMimeTypes.getEnum(Integer.parseInt(s.trim()));
    				if(v != null){
    					ll.add(v.getMime());
    				}
    			}catch(Exception e){
    			}
    		}
    	}
        return ll;
    }
    public static List<String> videoprotocols(String ids){
    	List<String> ll = new LinkedList<String>();
    	if(ids != null){
    		String idTrim=ids.trim().replaceAll("\\[", "").replaceAll("]", "");
    		String split[] = idTrim.split(",");
    		for(String s:split){
    			try{
    				VideoBidResponseProtocols v = VideoBidResponseProtocols.getEnum(Integer.parseInt(s.trim()));
    				if(v != null){
    					ll.add(v.getName());
    				}
    			}catch(Exception e){
    			}
    		}
    	}
        return ll;
    }
    public static List<String> videoplaybackmethod(String ids){
    	List<String> ll = new LinkedList<String>();
    	if(ids != null){
    		String idTrim=ids.trim().replaceAll("\\[", "").replaceAll("]", "");
    		String split[] = idTrim.split(",");
    		for(String s:split){
    			try{
    				VideoPlaybackMethods v = VideoPlaybackMethods.getEnum(Integer.parseInt(s.trim()));
    				if(v != null){
    					ll.add(v.getName());
    				}
    			}catch(Exception e){
    			}
    		}
    	}
        return ll;
    }
    public static List<String> videodelivery(String ids){
    	List<String> ll = new LinkedList<String>();
    	if(ids != null){
    		String idTrim=ids.trim().replaceAll("\\[", "").replaceAll("]", "");
    		String split[] = idTrim.split(",");
    		for(String s:split){
    			try{
    				ContentDeliveryMethods v = ContentDeliveryMethods.getEnum(Integer.parseInt(s.trim()));
    				if(v != null){
    					ll.add(v.getName());
    				}
    			}catch(Exception e){
    			}
    		}
    	}
        return ll;
    }
    public static List<String> videoapi(String ids){
    	List<String> ll = new LinkedList<String>();
    	if(ids != null){
    		String idTrim=ids.trim().replaceAll("\\[", "").replaceAll("]", "");
    		String split[] = idTrim.split(",");
    		for(String s:split){
    			try{
    				APIFrameworks v = APIFrameworks.getEnum(Integer.parseInt(s.trim()));
    				if(v != null){
    					ll.add(v.getName());
    				}
    			}catch(Exception e){
    			}
    		}
    	}
        return ll;
    }
	
	public static <E extends Enum<E>> List<String> getValuesFromEnum(Class<E> enumClass, String ids){

		List<String> values = new ArrayList<String>();
		String[] idArray = ids.split(",");
		int[] idArr = new  int[idArray.length];
		int count = 0;
		for (String string : idArray) {
			idArr[count++] = Integer.parseInt(string);
		}
		return values;
	}

	public static  List<String> getSelectedHygienes( String ids){
		List<String> values = new ArrayList<String>();
		if(ids != null && ids.length()>2){
			ArrayNode node = (ArrayNode)Json.parse(ids);
			Short code = 0;
			for (JsonNode jsonNode : node) {
				code =  jsonNode.shortValue();
				values.add(HygieneCategory.getEnum(code).name());
			}
		}
		return values;
	}
	public static  String getSingleSelectedHygiene( String ids){
	    if(ids != null && !"".equals(ids)){
	        return HygieneCategory.getEnum(Short.parseShort(ids)).name();
	    }
	    return ids;
	}

	
	private static String jsonArrayToString(String jsonArray){
		if(jsonArray != null && jsonArray.length()>2){
			ArrayNode idArray = (ArrayNode)Json.parse(jsonArray);
			String list="";
			for (JsonNode jsonNode : idArray) {
				list += jsonNode.shortValue()+",";
			}
			if(idArray.size()>0 && list.length()>0)
				list = list.substring(0, list.length()-1);
			return list;
		}
		return "";
	}
    public static List<String> getCreativeMacroByName(String creativeMacroIds){
        List<String> ll = new LinkedList<String>();
        try{
        if(creativeMacroIds != null){
            String creativeMacroIdTrim = creativeMacroIds.trim().replaceAll("\\[", "").replaceAll("]", "");
            if(!"".equals(creativeMacroIdTrim)){
                String split[] = creativeMacroIdTrim.split(",");
                for(String s:split){
                    String strim = s.trim();
                    if(!"".equals(strim)){
                        Integer i = Integer.parseInt(strim);
                        ADTagMacros a = ADTagMacros.getEnum(i);
                        if(a!= null){
                            ll.add(a.getName());
                        }
                    }
                }
            }
        }
        }catch(Exception e){
            Logger.error(e.getMessage(),e);
        }
        return ll;
        
    }
        public static List<String> getValues(MetadataType type, String ids){
		List<String> values = new ArrayList<String>();
		List<MetaField> mfields  = new ArrayList<MetaField>();
		Connection con = null;
		try{
		    if(ids != null){
		        con = DB.getConnection(true);
			    MetaInput mi = new MetaInput();
			    mi.setQuery_id_list(jsonArrayToString(ids));
			    MetaList mlist = ApiDef.get_metalist(con, type, mi);
			    mfields = mlist.getMetaFieldList();
			    for (MetaField metaField : mfields) {
				    values.add(metaField.getName());
			    }
		    }
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return values;
	}
	public static List<SelectOption> getActiveAPIDemandPartner(){
	    List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
	    Connection con = null;
	    try{
	        con = DB.getConnection(true);
	        ListEntity listEntity = new ListEntity();
	        listEntity.setAccountAPIEnum(AccountAPIEnum.list_active_advertiser_by_demandtype);
	        listEntity.setDemandType(DemandType.API);
	        listEntity.setStatus(StatusIdEnum.Active);

	        AccountList accountList = ApiDef.various_get_account(con, listEntity);
	        for (Account account : accountList.getAccount_list()) {
	            selectOptions.add(new SelectOption(account.getName(), account.getGuid()));
	        }
	    }catch(Exception e){
	        Logger.error("Error in getActiveAPIDemandPartner", e);
	    }
	    finally{
	        try {
	            if(con !=null)
	                con.close();
	        } catch (Exception e2) {
	            Logger.error("Failed closing connection", e2);
	        }
	    }
	    return selectOptions;
	}
	public static List<SelectOption> getDefaultPayoutKey(){
	    List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
	    Connection con = null;
	    try{
	        con = DB.getConnection(true);
	        PayoutThresholdListEntity entity =  new PayoutThresholdListEntity();
	        entity.setQueryEnum(PayoutThresholdListEnum.default_payout_threshold);
	        PayoutThresholdList list = ApiDef.various_get_payout_threshold(con, entity);
	        for (DefaultPayoutThreshold threshold : list.getDefault_entity_list()) {
	            selectOptions.add(new SelectOption(threshold.getName(), threshold.getName()));
	        }
	    }catch(Exception e){
	        Logger.error("Error in getActiveAPIDemandPartner", e);
	    }
	    finally{
	        try {
	            if(con !=null)
	                con.close();
	        } catch (Exception e2) {
	            Logger.error("Failed closing connection", e2);
	        }
	    }
	    return selectOptions;
	}
	public static List<SelectOption> getAdxBasedExchangesMetadata(){
	    List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
	    Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.ADX_BASED_EXCHANGES_METATADATA, null);
			List<MetaField> mfields  = mlist.getMetaFieldList();
			for(MetaField m:mfields){
				selectOptions.add(new SelectOption(m.getName(), m.getId()+""));
			}
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
	    return selectOptions;
	}

    public static List<SelectOption> getActiveDSPDemandPartner(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        Connection con = null;
        try{
            con = DB.getConnection(true);
            ListEntity listEntity = new ListEntity();
            listEntity.setAccountAPIEnum(AccountAPIEnum.list_active_advertiser_by_demandtype);
            listEntity.setDemandType(DemandType.DSP);
            listEntity.setStatus(StatusIdEnum.Active);

            AccountList accountList = ApiDef.various_get_account(con, listEntity);
            for (Account account : accountList.getAccount_list()) {
                selectOptions.add(new SelectOption(account.getName(), account.getGuid()));
            }
        }catch(Exception e){
            Logger.error("Error in getActiveDSPDemandPartner", e);
        }
        finally{
            try {
                if(con !=null)
                    con.close();
            } catch (Exception e2) {
                Logger.error("Failed closing connection", e2);
            }
        }
        return selectOptions;
    }

	public static ArrayNode tier1mmacategory(){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.MMA_CATEGORY_TIER1_ALL, null);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	public static ArrayNode tier2mmacategory(String tier1List){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaInput mi = new MetaInput();
			if(tier1List != null){
				mi.setQuery_id_list(tier1List.trim().replaceAll("\\[", "").replaceAll("]", ""));
			}
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.MMA_CATEGORY_TIER2_BY_TIER1, mi);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	public static ArrayNode tier1mmaindustry(){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.MMA_INDUSTRY_TIER1_ALL, null);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	public static List<SelectOption> tier1mmaindustrySelectOption(){
		List<SelectOption> optionNodes = new ArrayList<SelectOption>();

		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.MMA_INDUSTRY_TIER1_ALL, null);
			mfields = mlist.getMetaFieldList();
			if(mfields != null){
				for(MetaField m: mfields){
					optionNodes.add(new SelectOption(m.getName(), m.getId()+""));
				}
			}
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return optionNodes;
	}
	public static ArrayNode tier2mmaindustry(String tier1List){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaInput mi = new MetaInput();
			if(tier1List != null){
				mi.setQuery_id_list(tier1List.trim().replaceAll("\\[", "").replaceAll("]", ""));
			}
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.MMA_INDUSTRY_TIER2_BY_TIER1, mi);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	public static List<SelectOption> tier2mmaindustrySelectOption(){
		List<SelectOption> optionNodes = new ArrayList<SelectOption>();

		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.MMA_INDUSTRY_TIER2_ALL, null);
			mfields = mlist.getMetaFieldList();
			if(mfields != null){
				for(MetaField m: mfields){
					optionNodes.add(new SelectOption(m.getName(), m.getId()+""));
				}
			}
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return optionNodes;
	}
	public static ArrayNode adposition_list(){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.ADPOS_ALL, null);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNodeWithDescription(mfields);
	}
	public static ArrayNode tier1channel(){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CHANNEL_TIER1_ALL, null);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	public static ArrayNode tier2channelBytier1(String tier1List){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaInput mi = new MetaInput();
			if(tier1List != null){
				mi.setQuery_id_list(tier1List.trim().replaceAll("\\[", "").replaceAll("]", ""));
			}
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CHANNEL_TIER2_BY_TIER1, mi);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	public static ArrayNode channelbypubids(String pubids){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaInput mi = new MetaInput();
			if(pubids != null){
				mi.setQuery_id_list(pubids.trim().replaceAll("\\[", "").replaceAll("]", ""));
			}
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CHANNEL_BY_PUBIDS, mi);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	public static ArrayNode adpositionbypubids(String pubids){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaInput mi = new MetaInput();
			if(pubids != null){
				mi.setQuery_id_list(pubids.trim().replaceAll("\\[", "").replaceAll("]", ""));
			}
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.ADPOS_BY_PUBIDS, mi);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNode(mfields);
	}
	
	public static ArrayNode tier_1_categories(){
		List<MetaField> mfields  = null;
		Connection con = null;
		try{
			con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CATEGORY_TIER_1, null);
			mfields = mlist.getMetaFieldList();
		}catch(Exception e){
			Logger.error("Failed closing connection", e);
		}
		finally{
			try {
				if(con !=null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed closing connection", e2);
			}
		}
		return metalistToArrayNodeWithDescription(mfields);
	}
    public static ArrayNode get_active_advertiser_list_as_metadata(){
        List<MetaField> mfields  = null;
        Connection con = null;
        try{
            con = DB.getConnection(true);
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.ACTIVE_ADVERTISER_LIST, null);
            mfields = mlist.getMetaFieldList();
        }catch(Exception e){
            Logger.error("Failed closing connection", e);
        }
        finally{
            try {
                if(con !=null)
                    con.close();
            } catch (Exception e2) {
                Logger.error("Failed closing connection", e2);
            }
        }
        return metalistToArrayNode(mfields);
    }
    public static ArrayNode tier_2_categories(){
        List<MetaField> mfields  = null;
        Connection con = null;
        try{
            con = DB.getConnection(true);
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.CATEGORY_TIER_2, null);
            mfields = mlist.getMetaFieldList();

        }catch(Exception e){
            Logger.error("Failed closing connection", e);
        }
        finally{
            try {
                if(con !=null)
                    con.close();
            } catch (Exception e2) {
                Logger.error("Failed closing connection", e2);
            }
        }
        return metalistToArrayNodeWithDescription(mfields);
    }



	public static ObjectNode campaignStatuses(){
		ObjectNode optionNodes = new ObjectNode(JsonNodeFactory.instance);

		StatusIdEnum[] types = StatusIdEnum.values();
		for (StatusIdEnum statusId : types) {
			optionNodes.put( statusId.getCode()+"", statusId.name());
		}
		return optionNodes; 
	}


	public static ArrayNode hygieneList(){ 
		ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);

		HygieneCategory[] types = HygieneCategory.values();
		for (HygieneCategory hygieneCategory : types) {
			optionNodes.add(new SelectOption(hygieneCategory.name(), hygieneCategory.getCode()+"").toJson());
		}
		return optionNodes; 
	}
    public static ArrayNode device_type(){ 
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);

        DeviceType[] types = DeviceType.values();
        for (DeviceType type : types) {
            if(type != DeviceType.UNKNOWN){
                optionNodes.add(new SelectOption(type.name(), type.getCode()+"").toJson());
            }
        }
        return optionNodes; 
    }
    public static List<SelectOption> hygieneListOption(){
        List<SelectOption> optionNodes = new ArrayList<SelectOption>();
        HygieneCategory[] types = HygieneCategory.values();
        for (HygieneCategory hygiene : types) {
            optionNodes.add( new SelectOption(hygiene.name(),hygiene.getCode()+""));
        }
        return optionNodes; 
    }

	public static List<SelectOption> appStores(){
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
		APP_STORE_ID[] types = APP_STORE_ID.values();
		for (APP_STORE_ID appStoreId : types) {
			selectOptions.add(new SelectOption(appStoreId.name(), appStoreId.getAppStoreId()+""));
		}
		return selectOptions;
	}
    public static ArrayNode appStoresArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        APP_STORE_ID[] types = APP_STORE_ID.values();
        for (APP_STORE_ID appStoreId : types) {
            optionNodes.add(new SelectOption(appStoreId.name(), appStoreId.getAppStoreId()+"").toJson());
        }
        return optionNodes;
    }

	public static List<SelectOption> marketPlaces(){
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
		MarketPlace[] marketPlaces = MarketPlace.values();
		for (MarketPlace marketPlace : marketPlaces) {
			selectOptions.add(new SelectOption(marketPlace.name(), marketPlace.name()));
		}
		return selectOptions;
	}
    public static ArrayNode marketPlacesArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        MarketPlace[] marketPlaces = MarketPlace.values();
        for (MarketPlace marketPlace : marketPlaces) {
            optionNodes.add(new SelectOption(marketPlace.name(), marketPlace.name()).toJson());
        }
        return optionNodes;
    }
    public static List<SelectOption> trackingPartner(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        TrackingPartner[] trackingPartners = TrackingPartner.values();
        for (TrackingPartner trackingPartner : trackingPartners) {
            selectOptions.add(new SelectOption(trackingPartner.name(), trackingPartner.name()));
        }
        return selectOptions;
    }
    public static List<SelectOption> bidtype(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        BidType[] bidtypes = BidType.values();
        for (BidType bidtype : bidtypes) {
            selectOptions.add(new SelectOption(bidtype.name(), bidtype.getCode()+""));
        }
        return selectOptions;
    }
    public static ArrayNode trackingPartnerArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        TrackingPartner[] trackingPartners = TrackingPartner.values();
        for (TrackingPartner trackingPartner : trackingPartners) {
            optionNodes.add(new SelectOption(trackingPartner.name(), trackingPartner.name()).toJson());
        }
        return optionNodes;
    }

	public static List<SelectOption> activePublisherList(){
		return activeAccountList(Account_Type.directpublisher);
	}
	
	public static ArrayNode activePublisherArray(){
		List<SelectOption> options  =  activeAccountListWithId(Account_Type.directpublisher);
		ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
		for (SelectOption option : options) {
			optionNodes.add(option.toJson());
		}
		return optionNodes;
	}
    public static ArrayNode activeDirectPublisherArray(){
        List<SelectOption> options  =  activeDirectPublisherWithId();
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }
    public static ArrayNode activeExchangeArray(){
        List<SelectOption> options  =  activeExchangeListWithId();
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }
    public static ArrayNode activeGuidExchangeArray(){
        List<SelectOption> options  =  activeExchangeListWithGuid();
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }

	   public static ArrayNode supply_source_typeArray(){
	        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
	        SupplySourceTypeEnum[] types = SupplySourceTypeEnum.values();
	        for (SupplySourceTypeEnum src : types) {
	            if(src.getCode() != 1){
	                optionNodes.add((new SelectOption(src.name(),src.getCode()+"")).toJson());
	            }
	        } 
	        return optionNodes;
	    }
       public static ArrayNode reporting_connection_type_Array(){
           ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
           ConnectionType[] types = ConnectionType.values();
           for (ConnectionType src : types) {
           			if(src.getId()<(short)50){
                   		optionNodes.add((new SelectOption(src.name(),src.getId()+"")).toJson());
                    }
           } 
           return optionNodes;
       }

       
       public static ArrayNode activeAdvIdsArray(){
           List<SelectOption> options  =  activeAdvIdsList(Account_Type.directadvertiser);
           ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
           for (SelectOption option : options) {
               optionNodes.add(option.toJson());
           }
           return optionNodes;
       }
       
	public static ArrayNode activeAdvertiserArray(){
		List<SelectOption> options  =  activeAccountList(Account_Type.directadvertiser);
		ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
		for (SelectOption option : options) {
			optionNodes.add(option.toJson());
		}
		return optionNodes;
	}
    public static ArrayNode activeAdvertiserDSPArray(){
        List<SelectOption> options  =  activeAccountList(Account_Type.directadvertiser);
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }
	public static ArrayNode advertiserforfiltering(){
        List<SelectOption> options  =  advertiserforfilteringAccountList(Account_Type.directadvertiser);
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }
	
	public static ArrayNode directadvertiserforfiltering(){
        List<SelectOption> options  =  directadvertiserforfilteringAccountList(Account_Type.directadvertiser);
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }
    public static ArrayNode campaignsByAdvIds(String accountList){
        Connection con = null;
        CampaignListEntity cle = new CampaignListEntity();  
        cle.setId_list(accountList); 
        cle.setCampaignQueryEnum(CampaignQueryEnum.list_campaign_by_account_ids);
        cle.setPage_no(PageConstants.start_index);
        cle.setPage_size(Integer.MAX_VALUE);
        CampaignList campaignList = null;
        ArrayNode campaignOptions = new ArrayNode(JsonNodeFactory.instance);
        try{ 
            con = DB.getConnection(true);
            campaignList = ApiDef.list_campaign(con, cle);
            List<Campaign> campaigns = campaignList.getCampaign_list();
            for (Campaign campaign : campaigns) {
                campaignOptions.add(new SelectOption(campaign.getName(), campaign.getId()+"").toJson());
            }
 
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return campaignOptions;
    }
    public static ArrayNode retargetingSegmentsByAccount(String account){
        Connection con = null;
        ArrayNode segmentOptions = new ArrayNode(JsonNodeFactory.instance);
        if(account==null){
            return segmentOptions;
        }
        RetargetingSegmentInputEntity rsie = new RetargetingSegmentInputEntity();
        rsie.setRetargetingSegmentEnum(RetargetingSegmentEnum.get_retargeting_segments_by_accounts);
        rsie.setId_list(account);
        RetargetingSegmentList rsList = null;
        try{ 
            con = DB.getConnection(true);
            rsList = ApiDef.various_get_retargeting_segments(con, rsie);
            List<RetargetingSegment> rsegments = rsList.getRetargeting_segment_list();
            for (RetargetingSegment rs : rsegments) {
                segmentOptions.add(new SelectOption(rs.getName(), rs.getRetargeting_segment_id()+"").toJson());
            }
 
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return segmentOptions;
    }
	public static ArrayNode campaignsByAccount(String accountList){
		Connection con = null;
		CampaignListEntity cle = new CampaignListEntity();	
		cle.setId_list(accountList); 
		cle.setCampaignQueryEnum(CampaignQueryEnum.list_campaign_of_accounts);
		cle.setPage_no(PageConstants.start_index);
		cle.setPage_size(Integer.MAX_VALUE);
		CampaignList campaignList = null;
		ArrayNode campaignOptions = new ArrayNode(JsonNodeFactory.instance);
		try{ 
		    con = DB.getConnection(true);
			campaignList = ApiDef.list_campaign(con, cle);
			List<Campaign> campaigns = campaignList.getCampaign_list();
			for (Campaign campaign : campaigns) {
				campaignOptions.add(new SelectOption(campaign.getName(), campaign.getId()+"").toJson());
			}
 
		}catch(Exception e){
			Logger.error("Error in fetching account list", e);
		}finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) { 
				Logger.error("Error in closing DB connection",e);
			}
		}
		return campaignOptions;
	}
    public static ArrayNode campaignsByAccountIds(String accountList){
        Connection con = null;
        CampaignListEntity cle = new CampaignListEntity();  
        cle.setId_list(accountList); 
        cle.setCampaignQueryEnum(CampaignQueryEnum.list_campaign_by_account_ids_with_account_id);
        cle.setPage_no(PageConstants.start_index);
        cle.setPage_size(Integer.MAX_VALUE);
        CampaignList campaignList = null;
        ArrayNode campaignOptions = new ArrayNode(JsonNodeFactory.instance);
        if(accountList == null || "none".equalsIgnoreCase(accountList) || "all".equalsIgnoreCase(accountList)
                || "[none]".equalsIgnoreCase(accountList) || "[all]".equalsIgnoreCase(accountList) || "".equals(accountList)){
            return campaignOptions;
        }
        try{ 
            con = DB.getConnection(true);
            campaignList = ApiDef.list_campaign(con, cle);
            List<Campaign> campaigns = campaignList.getCampaign_list();
            for (Campaign campaign : campaigns) {
                campaignOptions.add(new SelectOption(campaign.getName(), campaign.getAccount_id()+"|"+campaign.getId()).toJson());
            }
 
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return campaignOptions;
    }
	
	public static ArrayNode adsByCampaign(String campaignList){
		Connection con = null;
		AdListEntity ale = new AdListEntity();	
		ale.setAdenum(AdAPIEnum.list_ad_by_campaigns);
		ale.setId_list(campaignList); 
		ale.setPage_no(PageConstants.start_index);
		ale.setPage_size(Integer.MAX_VALUE);
		AdList adList = null;
		ArrayNode adOptions = new ArrayNode(JsonNodeFactory.instance);
		try{ 
		    con = DB.getConnection(true);
			adList = ApiDef.various_get_ad(con, ale);
			List<Ad> ads = adList.getAdlist();
			for (Ad ad : ads) {
				adOptions.add(new SelectOption(ad.getName(), ad.getId()+"").toJson());
			}
 
		}catch(Exception e){
			Logger.error("Error in fetching account list", e);
		}

		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) { 
				Logger.error("Error in closing DB connection",e);
			}
		}
		return adOptions;
	}
	
	public static ArrayNode sitesByPublishers(String publisherList){
		Connection con = null;
		
		ArrayNode siteOptions = new ArrayNode(JsonNodeFactory.instance);
		try{
		    if(publisherList != null && !"none".equals(publisherList)){
		        con = DB.getConnection();
		        SiteListEntity sle = new SiteListEntity();	
		        sle.setId_list(publisherList); 
		        sle.setPage_no(PageConstants.start_index);
		        sle.setPage_size(Integer.MAX_VALUE);
		        SiteList siteList = null;
		        siteList = ApiDef.list_site_by_account_ids(con, sle);
		        List<Site> sites = siteList.getSite_list();
		        for (Site site:sites) {
		            siteOptions.add(new SelectOption(site.getName(), site.getId()+"").toJson());
		        }
		    }
 
		}catch(Exception e){
			Logger.error("Error in fetching account list", e);
		}

		finally{
			try {
			    if(con != null){
			        con.close();
			    }
			} catch (SQLException e) { 
				Logger.error("Error in closing DB connection",e);
			}
		}
		return siteOptions;
	}
    public static ArrayNode targeting_sitesByPublishers(String publisherList){
        Connection con = null;
        ArrayNode siteOptions = new ArrayNode(JsonNodeFactory.instance);
        try{
            if(publisherList != null && !"none".equals(publisherList)){
                con = DB.getConnection();
                SiteListEntity sle = new SiteListEntity();  
                sle.setId_list(publisherList); 
                sle.setPage_no(PageConstants.start_index);
                sle.setPage_size(Integer.MAX_VALUE);
                SiteList siteList = null;
                siteList = ApiDef.list_site_by_account_ids(con, sle);
                List<Site> sites = siteList.getSite_list();
                for (Site site:sites) {
                    siteOptions.add(new SelectOption(site.getName(), site.getPub_id()+"|"+site.getId()).toJson());
                }
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return siteOptions;
    }
    public static ArrayNode targeting_directsitesByPublishers(String publisherList){
        Connection con = null;
        ArrayNode siteOptions = new ArrayNode(JsonNodeFactory.instance);
        try{
            if(publisherList != null && !"none".equals(publisherList) && !"".equals(publisherList)
                    && !"all".equalsIgnoreCase(publisherList) && !"[all]".equalsIgnoreCase(publisherList)){
                con = DB.getConnection();
                SiteListEntity sle = new SiteListEntity();  
                sle.setId_list(publisherList); 
                sle.setPage_no(PageConstants.start_index);
                sle.setPage_size(Integer.MAX_VALUE);
                SiteList siteList = null;
                siteList = ApiDef.list_site_by_account_ids(con, sle);
                List<Site> sites = siteList.getSite_list();
                for (Site site:sites) {
                    siteOptions.add(new SelectOption(site.getName(), site.getPub_id()+"|"+site.getId()).toJson());
                }
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return siteOptions;
    }
    public static ArrayNode ext_siteByPublishers(String publisherList){
        Connection con = null;
        
        ArrayNode siteOptions = new ArrayNode(JsonNodeFactory.instance);
        try{
            if(publisherList != null && !"none".equals(publisherList) && !"all".equalsIgnoreCase(publisherList)){
                con = DB.getConnection();
                Ext_site_input ext_site_input = new Ext_site_input();  
                ext_site_input.setId_list(publisherList); 
                ext_site_input.setPage_no(PageConstants.start_index);
                ext_site_input.setPage_size(PageConstants.csv_page_size);
                ext_site_input.setExt_siteenum(Ext_siteEnum.get_ext_site_by_pub);
                Ext_site_list ext_site_list = null;
                ext_site_list = ApiDef.various_get_ext_site(con, ext_site_input);
                List<Ext_site> ext_sites = ext_site_list.getExt_site_list();
                for (Ext_site ext_site:ext_sites) {
                    siteOptions.add(new SelectOption(ext_site.getExt_supply_name(), ext_site.getId()+"").toJson());
                }
            }
        }catch(Exception e){
            Logger.error("Error in fetching ext site list", e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return siteOptions;
    }
	
    public static ArrayNode targeting_ext_siteByPublishers(String publisherList){
        Connection con = null;
        ArrayNode siteOptions = new ArrayNode(JsonNodeFactory.instance);
        try{
            if(publisherList != null && !"none".equals(publisherList)
                    && !"all".equalsIgnoreCase(publisherList) && !"[all]".equalsIgnoreCase(publisherList)){
                con = DB.getConnection();
                Ext_site_input ext_site_input = new Ext_site_input();  
                ext_site_input.setId_list(publisherList); 
                ext_site_input.setPage_no(PageConstants.start_index);
                ext_site_input.setPage_size(Integer.MAX_VALUE);
                ext_site_input.setExt_siteenum(Ext_siteEnum.get_ext_site_by_pub);
                Ext_site_list ext_site_list = null;
                ext_site_list = ApiDef.various_get_ext_site(con, ext_site_input);
                List<Ext_site> ext_sites = ext_site_list.getExt_site_list();
                for (Ext_site ext_site:ext_sites) {
                    siteOptions.add(new SelectOption(ext_site.getExt_supply_name(), ext_site.getPub_inc_id()+"|"+ext_site.getSite_inc_id()+"|"+ext_site.getId()).toJson());
                }
            }
        }catch(Exception e){
            Logger.error("Error in fetching ext site list", e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return siteOptions;
    }

    public static List<SelectOption> activeAdvertiserList(){
		return activeAccountList(Account_Type.directadvertiser);
	}

    public static List<SelectOption> activeAdminList(){
        return activeAdminList(Account_Type.root);
    }
	
	public static List<SelectOption> activeAccountListWithId(Account_Type accountType){

		ListEntity lEntity = new ListEntity();
		Connection con = null;
		lEntity.setAccount_type(accountType);
		lEntity.setStatus(StatusIdEnum.Active);
		lEntity.setPage_no(PageConstants.start_index);
		lEntity.setPage_size(Integer.MAX_VALUE);
		AccountList accList = null;
		List<SelectOption> accountOptions = new ArrayList<SelectOption>();
		try{
		    con = DB.getConnection(true);
			accList = ApiDef.listAccountByStatus(con, lEntity);
			List<Account> accounts = accList.getAccount_list();
			for (Account account : accounts) {
					accountOptions.add(new SelectOption(account.getName(), account.getId()+""));
			}
		}catch(Exception e){
			Logger.error("Error in fetching account list", e);
		}finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) { 
				Logger.error("Error in closing DB connection",e);
			}
		}
		return accountOptions;
	}
    public static List<SelectOption> activeExchangeListWithId(){
        ListEntity lEntity = new ListEntity();
        Connection con = null;
        lEntity.setStatus(StatusIdEnum.Active);
        lEntity.setPage_no(PageConstants.start_index);
        lEntity.setPage_size(Integer.MAX_VALUE);
        AccountList accList = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        try{
            con = DB.getConnection(true);
            accList = ApiDef.listExchangesByStatus(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            for (Account account : accounts) {
                    accountOptions.add(new SelectOption(account.getName(), account.getId()+""));
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }
    public static List<SelectOption> activeExchangeListWithGuid(){
        ListEntity lEntity = new ListEntity();
        Connection con = null;
        lEntity.setStatus(StatusIdEnum.Active);
        lEntity.setPage_no(PageConstants.start_index);
        lEntity.setPage_size(Integer.MAX_VALUE);
        AccountList accList = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        accountOptions.add(new SelectOption("",""));
        try{
            con = DB.getConnection(true);
            accList = ApiDef.listExchangesByStatus(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            for (Account account : accounts) {
                    accountOptions.add(new SelectOption(account.getName(), account.getGuid()));
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }
    public static List<SelectOption> pmpExchangeListWithId(){

        ListEntity lEntity = new ListEntity();
        Connection con = null;
        lEntity.setStatus(StatusIdEnum.Active);
        lEntity.setPage_no(PageConstants.start_index);
        lEntity.setPage_size(Integer.MAX_VALUE);
        AccountList accList = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        try{
            con = DB.getConnection(true);
            accList = ApiDef.listExchangesByStatus(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            accountOptions.add(new SelectOption("",""));
            for (Account account : accounts) {
                    if(account.getName().contains("Smaato")){
                        accountOptions.add(new SelectOption(account.getName(), account.getGuid()));
                    }
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }

    public static List<SelectOption> activeDirectPublisherWithId(){

        ListEntity lEntity = new ListEntity();
        Connection con = null;
        lEntity.setStatus(StatusIdEnum.Active);
        lEntity.setPage_no(PageConstants.start_index);
        lEntity.setPage_size(Integer.MAX_VALUE);
        AccountList accList = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        try{
            con = DB.getConnection(true);
            accList = ApiDef.listDirectPublisherByStatus(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            for (Account account : accounts) {
                    accountOptions.add(new SelectOption(account.getName(), account.getId()+""));
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }
    
    public static List<SelectOption> activeAdvIdsList(Account_Type accountType){
        Connection con = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        try{
            con = DB.getConnection(true);       
            ListEntity lEntity = new ListEntity();
            lEntity.setAccount_type(accountType);
            lEntity.setStatus(StatusIdEnum.Active);
            lEntity.setPage_no(PageConstants.start_index);
            lEntity.setPage_size(Integer.MAX_VALUE);
            AccountList accList = null;
            accList = ApiDef.listAccountByStatus(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            for (Account account : accounts) {
                    accountOptions.add(new SelectOption(account.getName(), account.getId()+""));
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }

    public static List<SelectOption> activeAccountList(Account_Type accountType){
		Connection con = null;
		List<SelectOption> accountOptions = new ArrayList<SelectOption>();
		try{
		    con = DB.getConnection(true);		
			ListEntity lEntity = new ListEntity();
			lEntity.setAccount_type(accountType);
			lEntity.setStatus(StatusIdEnum.Active);
			lEntity.setPage_no(PageConstants.start_index);
			lEntity.setPage_size(Integer.MAX_VALUE);
			AccountList accList = null;
			accList = ApiDef.listAccountByStatus(con, lEntity);
			List<Account> accounts = accList.getAccount_list();
			for (Account account : accounts) {
					accountOptions.add(new SelectOption(account.getName(), account.getGuid()));
			}
		}catch(Exception e){
			Logger.error("Error in fetching account list", e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) { 
				Logger.error("Error in closing DB connection",e);
			}
		}
		return accountOptions;
	}

    public static List<SelectOption> activeAdminList(Account_Type accountType){
        
        Connection con = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        try{
            con = DB.getConnection(true);       
            ListEntity lEntity = new ListEntity();
            lEntity.setAccount_type(accountType);
            lEntity.setStatus(StatusIdEnum.Active);
            lEntity.setPage_no(PageConstants.start_index);
            lEntity.setPage_size(Integer.MAX_VALUE);
            AccountList accList = null;
            accList = ApiDef.listAccountByStatus(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            for (Account account : accounts) {
                    accountOptions.add(new SelectOption(account.getUserid(), account.getId()+""));
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }

        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }
	public static List<SelectOption> advertiserforfilteringAccountList(Account_Type accountType){
        
        Connection con = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        try{
            con = DB.getConnection(true);       
            ListEntity lEntity = new ListEntity();
            lEntity.setAccount_type(accountType);
            lEntity.setStatus(StatusIdEnum.Active);
            lEntity.setPage_no(PageConstants.start_index);
            lEntity.setPage_size(Integer.MAX_VALUE);
            AccountList accList = null;
            accList = ApiDef.listAccountByStatus(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            for (Account account : accounts) {
                    accountOptions.add(new SelectOption(account.getName(), account.getId()+""));
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }

        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }
    public static List<SelectOption> directadvertiserforfilteringAccountList(Account_Type accountType){
        Connection con = null;
        List<SelectOption> accountOptions = new ArrayList<SelectOption>();
        try{
            con = DB.getConnection(true);       
            ListEntity lEntity = new ListEntity();
            lEntity.setAccount_type(accountType);
            lEntity.setStatus(StatusIdEnum.Active);
            lEntity.setPage_no(PageConstants.start_index);
            lEntity.setPage_size(Integer.MAX_VALUE);
            lEntity.setDemandType(DemandType.DIRECT);
            lEntity.setAccountAPIEnum(AccountAPIEnum.list_active_advertiser_by_demandtype);
            AccountList accList = null;
            accList = ApiDef.various_get_account(con, lEntity);
            List<Account> accounts = accList.getAccount_list();
            for (Account account : accounts) {
                    accountOptions.add(new SelectOption(account.getName(), account.getId()+""));
            }
        }catch(Exception e){
            Logger.error("Error in fetching account list", e);
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) { 
                Logger.error("Error in closing DB connection",e);
            }
        }
        return accountOptions;
    }

	public static List<SelectOption> sitePlatforms(){
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
		SITE_PLATFORM[] types = SITE_PLATFORM.values();
		for (SITE_PLATFORM sitePlatform : types) {
		    if(SITE_PLATFORM.NO_VALID_VALUE != sitePlatform){
		        selectOptions.add(new SelectOption(sitePlatform.name(), sitePlatform.name()));
		    }
		}
		return selectOptions;
	}
    public static ArrayNode sitePlatformsArray(){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        SITE_PLATFORM[] types = SITE_PLATFORM.values();
        for (SITE_PLATFORM sitePlatform : types) {
            if(SITE_PLATFORM.NO_VALID_VALUE != sitePlatform){
                optionNodes.add(new SelectOption(sitePlatform.name(), sitePlatform.name()).toJson());
            }
        }
        return optionNodes;
    }
	public static List<SelectOption> siteRejection(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();

        SiteRejection[] types = SiteRejection.values();
        for (SiteRejection siteRejection : types) {
            selectOptions.add(new SelectOption(siteRejection.name(), siteRejection.name()));
        }
        return selectOptions;
    }
	public static List<SelectOption> accountRejection(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();

        AccountRejection[] types = AccountRejection.values();
        for (AccountRejection accountRejection : types) {
            selectOptions.add(new SelectOption(accountRejection.name(), accountRejection.name()));
        }
        return selectOptions;
    }

	public static List<MetaField> fetchCreativeAttributes(int creativeType){
		Connection con = null;
		MetaList mlist = null; 
		List<MetaField> mfields = null;
		try {
		    con = DB.getConnection();
			if(creativeType != -1){
				MetaInput metaInput = new MetaInput(); 
				metaInput.setQuery_id_list(creativeType+"");
				mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_ATTRIBUTES_BY_FORMAT_ID, metaInput); 
			}else{
				mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_ATTRIBUTES, null);
			}
			if(mlist.getMsg().getError_code() != -1)
				mfields = mlist.getMetaFieldList();
			else{
				mfields = new ArrayList<MetaField>();
				Logger.error("Error in fetching creative attributes. " + mlist.getMsg().getMsg());
			}
				
		} catch (Exception e) {
			Logger.error("Error in fetching creative attributes",e);
		}
		finally{
			try {
				if(con != null)
						con.close();
			} catch (SQLException e) { 
				Logger.error("Error in closing DB connection",e);
			}
		}
		return mfields;
	}

	public static ArrayNode creativeAttributes(int creativeType){		
		return metalistToArrayNode(fetchCreativeAttributes(creativeType));
	}

	public static ArrayNode creativeMacros(){       
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (ADTagMacros macro : ADTagMacros.values()) {
            optionNodes.add(new SelectOption(macro.getName(), macro.getCode()+"").toJson());
        }
        return optionNodes;
    }

    public static ArrayNode videoTracking(){       
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (VASTKritterTrackingEventTypes macro : VASTKritterTrackingEventTypes.values()) {
            optionNodes.add(new SelectOption(macro.getName(), macro.getCode()+"").toJson());
        }
        return optionNodes;

    }

	public static List<SelectOption> creativeAttributesList(){
		List<MetaField> mfields =fetchCreativeAttributes(-1);
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
		for (MetaField metaField : mfields) {
			selectOptions.add(new SelectOption(metaField.getName(), metaField.getId()+""));
		}
		return selectOptions;
	}

	

	public static ArrayNode appStoreOptionList(){
		ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);

		APP_STORE_ID[] types = APP_STORE_ID.values();
		for (APP_STORE_ID appStoreId : types) {
			optionNodes.add(new SelectOption(appStoreId.name(), appStoreId.ordinal()+"").toJson());
		}
		return optionNodes; 
	}


	public static ArrayNode appStoreOptions(){
		ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);

		APP_STORE_ID[] types = APP_STORE_ID.values();
		for (APP_STORE_ID appStoreId : types) {
			optionNodes.add(new SelectOption(appStoreId.name(), appStoreId.ordinal()+"").toJson());
		}
		return optionNodes; 
	}

	public static ArrayNode metalistToArrayNode(List<MetaField> mfields){
		ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
		for (MetaField metaField : mfields) {
			optionNodes.add(new SelectOption(metaField.getName(), metaField.getId()+"").toJson());
		}
		return optionNodes;
	}
	public static ArrayNode metalistToArrayNodeWithDescription(List<MetaField> mfields){
        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        for (MetaField metaField : mfields) {
            optionNodes.add(new SelectOption(metaField.getName()+" : "+metaField.getDescription(), metaField.getId()+"").toJson());
        }
        return optionNodes;
    }

	public static ArrayNode listToArrayNode(List<SelectOption> options){
		ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
		for (SelectOption selectOption : options) {
			optionNodes.add(selectOption.toJson());
		}
		return optionNodes;
	}


	public static List<SelectOption> creativeTypes(){
		Connection con = null;
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
		try {
		    con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_FORMATS, null);
			List<MetaField> mfields = mlist.getMetaFieldList();
			
			for (MetaField metaField : mfields) {
			    if(CreativeFormat.Native.getCode() == metaField.getId()){
			        if("true".equals(is_native_demand)){
			            selectOptions.add(new SelectOption(metaField.getName(), metaField.getId()+""));
			        }
			    }else{
			        selectOptions.add(new SelectOption(metaField.getName(), metaField.getId()+""));
			    }
			}
		} catch (Exception e) {
			Logger.error("Exception encountered", e);
		}
		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (Exception e2) {
				Logger.error("Failed to close DB connection", e2);
			}
		}
		return selectOptions;
	}
	
	   public static ArrayNode creativeTypesArray(){
	        Connection con = null;
	        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
	        try {
	            con = DB.getConnection(true);
	            MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_FORMATS, null);
	            List<MetaField> mfields = mlist.getMetaFieldList();
	            
	            for (MetaField metaField : mfields) {
	                optionNodes.add(new SelectOption(metaField.getName(), metaField.getId()+"").toJson());
	            }
	        } catch (Exception e) {
	            Logger.error("Exception encountered", e);
	        }
	        finally{
	            try {
	                if(con != null){
	                    con.close();
	                }
	            } catch (Exception e2) {
	                Logger.error("Failed to close DB connection", e2);
	            }
	        }
	        return optionNodes;
	    }


	public static List<SelectOption> creativeSlots(){
		Connection con = null;
		List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
		
		try {
		    con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_SLOTS, null);
			List<MetaField> mfields = mlist.getMetaFieldList();
			
			for (MetaField metaField : mfields) {
				selectOptions.add(new SelectOption(metaField.getName()+"-"+metaField.getDescription(), metaField.getId()+""));
			}
		} catch (Exception e) {
			Logger.error("Exception encountered", e);
		}

		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (Exception e2) {
				Logger.error("Failed to close DB connection", e2);
			}
		}
		
		return selectOptions;
	}

	public static List<SelectOption> iconSlots(){
	    List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
	    for(NativeIconImageSize nis : NativeIconImageSize.values()){
	        selectOptions.add(new SelectOption(nis.getName()+"- ", nis.getCode()+""));
	    }
	    return selectOptions;
	}
    public static List<SelectOption> screenshotSlots(){
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        for(NativeScreenShotImageSize nis : NativeScreenShotImageSize.values()){
            selectOptions.add(new SelectOption(nis.getName()+"- ", nis.getCode()+""));
        }
        return selectOptions;
    }

	public static ArrayNode creativeSlotsArray(){
		Connection con = null;
		ArrayNode options = null; 
		
		try {
		    con = DB.getConnection(true);
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_SLOTS, null);
			List<MetaField> mfields = mlist.getMetaFieldList();
			
			options =  metalistToArrayNodeWithDescription(mfields);
		} catch (Exception e) {
			Logger.error("Exception encountered", e);
		}

		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (Exception e2) {
				Logger.error("Failed to close DB connection", e2);
			}
		}
		if(options == null)
				options = new ArrayNode(JsonNodeFactory.instance);
		return options;
	}
	public static ArrayNode iconSlotsArray(){
	    ArrayNode options = null; 
	    List<MetaField> mfields = new LinkedList<MetaField>();
	    for(NativeIconImageSize nis: NativeIconImageSize.values()){
	        MetaField mfield = new MetaField();
	        mfield.setId(nis.getCode());
	        mfield.setName(nis.getName());
	        mfield.setDescription("");
	        mfields.add(mfield);
	    }
	    options =  metalistToArrayNodeWithDescription(mfields);
	    if(options == null)
	        options = new ArrayNode(JsonNodeFactory.instance);
	    return options;
	}
    public static ArrayNode screenshotSlotsArray(){
        ArrayNode options = null; 
        List<MetaField> mfields = new LinkedList<MetaField>();
        for(NativeScreenShotImageSize nis: NativeScreenShotImageSize.values()){
            MetaField mfield = new MetaField();
            mfield.setId(nis.getCode());
            mfield.setName(nis.getName());
            mfield.setDescription("");
            mfields.add(mfield);
        }
        options =  metalistToArrayNodeWithDescription(mfields);
        if(options == null)
            options = new ArrayNode(JsonNodeFactory.instance);
        return options;
    }
    public static String creativeSlotsStr(){
        Connection con = null;
        StringBuffer  selectOptions = new StringBuffer("");
        
        try {
            con = DB.getConnection(true);
            MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_SLOTS, null);
            List<MetaField> mfields = mlist.getMetaFieldList();
            
            for (MetaField metaField : mfields) {
                selectOptions.append(metaField.getName()+"-"+metaField.getDescription()+"\n");
            }
            return selectOptions.toString();
        } catch (Exception e) {
            Logger.error("Exception encountered", e);
            return selectOptions.toString();
        }

        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (Exception e2) {
                Logger.error("Failed to close DB connection", e2);
            }
        }
    }
    
	
	public static MetaField getSlot(int slotId) { 
		Connection con = null;
		try {
		    con = DB.getConnection();
			MetaInput metaInput = new MetaInput();
			metaInput.setQuery_id_list(slotId+"");
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_SLOTS_BY_ID, metaInput);
			List<MetaField> mfields = mlist.getMetaFieldList();
			for (MetaField metaField : mfields) {
				if(metaField.getId() == slotId)
					return metaField;
			}
		} catch (Exception e) {
			Logger.error("Exception encountered", e);
		}

		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (Exception e2) {
				Logger.error("Failed to close DB connection", e2);
			}
		}
		return null;
	}
    public static MetaField getNativeIconSlot(int slotId) {
        NativeIconImageSize ni = NativeIconImageSize.getEnum(slotId);
        if(ni != null){
            MetaField mfield = new MetaField();
            mfield.setId(slotId);
            mfield.setName(ni.getName());
            mfield.setDescription(ni.getName());
            return mfield;
        }
        return null;
    }
	
    public static MetaField getNativeScreenshotSlot(int slotId) {
        NativeScreenShotImageSize ni = NativeScreenShotImageSize.getEnum(slotId);
        if(ni != null){
            MetaField mfield = new MetaField();
            mfield.setId(slotId);
            mfield.setName(ni.getName());
            mfield.setDescription(ni.getName());
            return mfield;
        }
        return null;
    }

    public static MetaField getFormat(int formatId) { 
		Connection con = null;
		try {
		    con = DB.getConnection();
			MetaInput metaInput = new MetaInput();
			metaInput.setQuery_id_list(formatId+"");
			MetaList mlist = ApiDef.get_metalist(con, MetadataType.CREATIVE_FORMATS_BY_ID, metaInput);
			List<MetaField> mfields = mlist.getMetaFieldList();
			for (MetaField metaField : mfields) {
				if(metaField.getId() == formatId)
					return metaField;
			}
		} catch (Exception e) {
			Logger.error("Exception encountered", e);
		}

		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (Exception e2) {
				Logger.error("Failed to close DB connection", e2);
			}
		}
		return null;
	}

    public static ArrayNode thirdPartyConnectionDSPIdArray(String advertiserGuid){

        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        if(advertiserGuid == null || "none".equalsIgnoreCase(advertiserGuid) || "all".equalsIgnoreCase(advertiserGuid)
            || "[none]".equalsIgnoreCase(advertiserGuid) || "[all]".equalsIgnoreCase(advertiserGuid) || "".equals(advertiserGuid)){
            return optionNodes;
        }

        List<SelectOption> options  =  thirdPartyConnectionDSPIdArrayValues(advertiserGuid);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }

    public static List<SelectOption> thirdPartyConnectionDSPIdArrayValues(String advertiserGuid)
    {
        Logger.error("inside thirdPartyConnectionDSPIdArrayValues function ");
        Connection con = null;
        ThirdPartyConnectionChildIdList thirdPartyConnectionChildIdList = null;

        List<SelectOption> options = new ArrayList<SelectOption>();

        try
        {
            con = DB.getConnection(true);

            ThirdPartyConnectionChildId thirdPartyConnectionChildId = new ThirdPartyConnectionChildId();
            thirdPartyConnectionChildId.setFetchDSPData(Boolean.TRUE);
            thirdPartyConnectionChildId.setThirdPartyConnectionGuid(advertiserGuid);

            thirdPartyConnectionChildIdList = ApiDef.listThirdPartyConnectionDSPAdvIdList(con,thirdPartyConnectionChildId);

            List<ThirdPartyConnectionChildId> values = thirdPartyConnectionChildIdList.getThirdPartyConnectionChildIdList();

            for (ThirdPartyConnectionChildId value : values)
            {
                options.add(new SelectOption(value.getDescription(), value.getId()+""));
            }

        }
        catch(Exception e)
        {
            Logger.error("Error in fetching thirdPartyConnectionDSPIdArrayValues", e);
        }
        finally
        {
            try
            {
                if(con != null)
                {
                    con.close();
                }
            }
            catch (SQLException e)
            {
                Logger.error("Error in closing DB connection",e);
            }
        }

        return options;
    }

    public static ArrayNode thirdPartyConnectionAdvIdArray(String advertiserGuid){

        ArrayNode optionNodes = new ArrayNode(JsonNodeFactory.instance);
        if(advertiserGuid == null || "none".equalsIgnoreCase(advertiserGuid) || "all".equalsIgnoreCase(advertiserGuid)
                || "[none]".equalsIgnoreCase(advertiserGuid) || "[all]".equalsIgnoreCase(advertiserGuid) || "".equals(advertiserGuid)){
            return optionNodes;
        }

        List<SelectOption> options  =  thirdPartyConnectionAdvIdArrayValues(advertiserGuid);
        for (SelectOption option : options) {
            optionNodes.add(option.toJson());
        }
        return optionNodes;
    }

    public static List<SelectOption> thirdPartyConnectionAdvIdArrayValues(String advertiserGuid)
    {
        Connection con = null;
        ThirdPartyConnectionChildIdList thirdPartyConnectionChildIdList = null;

        List<SelectOption> options = new ArrayList<SelectOption>();

        try
        {
            con = DB.getConnection(true);

            ThirdPartyConnectionChildId thirdPartyConnectionChildId = new ThirdPartyConnectionChildId();
            thirdPartyConnectionChildId.setFetchDSPData(false);
            thirdPartyConnectionChildId.setThirdPartyConnectionGuid(advertiserGuid);

            thirdPartyConnectionChildIdList = ApiDef.listThirdPartyConnectionDSPAdvIdList(con,thirdPartyConnectionChildId);

            List<ThirdPartyConnectionChildId> values = thirdPartyConnectionChildIdList.getThirdPartyConnectionChildIdList();

            for (ThirdPartyConnectionChildId value : values)
            {
                options.add(new SelectOption(value.getDescription(), value.getId()+""));
            }

        }
        catch(Exception e)
        {
            Logger.error("Error in fetching thirdPartyConnectionAdvIdArrayValues", e);
        }
        finally
        {
            try
            {
                if(con != null)
                {
                    con.close();
                }
            }
            catch (SQLException e)
            {
                Logger.error("Error in closing DB connection",e);
            }
        }

        return options;
    }

    public static String fetchDSPsForExternalConnectionWithGivenIds(String advertiserGuid,Integer[] dspIds)
    {
        Connection con = null;

        List<Integer> dspIdList = new ArrayList<Integer>();
        if(null != dspIds)
            dspIdList = Arrays.asList(dspIds);

        Set<String> values = new HashSet<String>();

        try
        {

            ThirdPartyConnectionChildId thirdPartyConnectionChildId = new ThirdPartyConnectionChildId();
            thirdPartyConnectionChildId.setFetchDSPData(true);
            thirdPartyConnectionChildId.setThirdPartyConnectionGuid(advertiserGuid);
            con = DB.getConnection(true);
            ThirdPartyConnectionChildIdList thirdPartyConnectionChildIdList = ApiDef.listThirdPartyConnectionDSPAdvIdList(con, thirdPartyConnectionChildId);
            if(null != thirdPartyConnectionChildIdList)
            {
                List<ThirdPartyConnectionChildId> list = thirdPartyConnectionChildIdList.getThirdPartyConnectionChildIdList();
                for(ThirdPartyConnectionChildId element : list)
                {
                    if(dspIdList.size() > 0 && dspIdList.contains(element.getId()))
                        values.add(element.getDescription());
                }
            }
        }
        catch(Exception e)
        {
            Logger.error("Error in fetching fetchDSPs", e);
        }
        finally
        {
            try
            {
                if(con != null)
                {
                    con.close();
                }
            }
            catch (SQLException e)
            {
                Logger.error("Error in closing DB connection",e);
            }
        }

        return values.toString();
    }

    public static String fetchAdvsForExternalConnectionWithGivenIds(String advertiserGuid,Integer[] advIds)
    {
        Connection con = null;
        List<Integer> advIdList = new ArrayList<Integer>();
        if(null != advIds)
            advIdList = Arrays.asList(advIds);

        Set<String> values = new HashSet<String>();
        try {

            ThirdPartyConnectionChildId thirdPartyConnectionChildId = new ThirdPartyConnectionChildId();
            thirdPartyConnectionChildId.setFetchDSPData(false);
            thirdPartyConnectionChildId.setThirdPartyConnectionGuid(advertiserGuid);
            con = DB.getConnection(true);
            ThirdPartyConnectionChildIdList thirdPartyConnectionChildIdList = ApiDef.listThirdPartyConnectionDSPAdvIdList(con, thirdPartyConnectionChildId);
            if(null != thirdPartyConnectionChildIdList)
            {
                List<ThirdPartyConnectionChildId> list = thirdPartyConnectionChildIdList.getThirdPartyConnectionChildIdList();
                for(ThirdPartyConnectionChildId element : list)
                {
                    if(advIdList.size() > 0 && advIdList.contains(element.getId()))
                        values.add(element.getDescription());
                }
            }
        }
        catch(Exception e)
        {
            Logger.error("Error in fetching fetchDSPs", e);
        }
        finally
        {
            try
            {
                if(con != null)
                {
                    con.close();
                }
            }
            catch (SQLException e)
            {
                Logger.error("Error in closing DB connection",e);
            }
        }

        return values.toString();
    }

    public static String fetchIABCategoriesByIds(Integer[] ids)
    {
        Connection con = null;

        try {
            con = DB.getConnection(true);
            if(null != ids && ids.length > 0)
                return  ApiDef.getCategoriesById(con,ids).toString();
        }
        catch(Exception e)
        {
            Logger.error("Error in fetching fetchDSPs", e);
        }
        finally
        {
            try
            {
                if(con != null)
                {
                    con.close();
                }
            }
            catch (SQLException e)
            {
                Logger.error("Error in closing DB connection",e);
            }
        }

        return null;
    }

    public static List<SelectOption> fetchAuctionTypes()
    {
        List<SelectOption>  selectOptions = new ArrayList<SelectOption>();
        try
        {
            selectOptions.add(new SelectOption("FIRST_PRICE_AUCTION", String.valueOf(AuctionType.FIRST_PRICE_AUCTION)));
            selectOptions.add(new SelectOption("SECOND_PRICE_AUCTION", String.valueOf(AuctionType.SECOND_PRICE_AUCTION)));
            selectOptions.add(new SelectOption("FIXED_PRICE", String.valueOf(AuctionType.FIXED_PRICE)));
        }
        catch(Exception e){
            Logger.error("Error in fetchAuctionTypes", e);
        }

        return selectOptions;
    }

    public static List<SelectOption> openRTBVersions()
    {
        List<SelectOption> options = emptyOptions();
        options.add(new SelectOption("RTB 2.0", String.valueOf(OpenRTBVersion.VERSION_2_0.getCode())));
        options.add(new SelectOption("RTB 2.1", String.valueOf(OpenRTBVersion.VERSION_2_1.getCode())));
        options.add(new SelectOption("RTB 2.2", String.valueOf(OpenRTBVersion.VERSION_2_2.getCode())));
        options.add(new SelectOption("RTB 2.3", String.valueOf(OpenRTBVersion.VERSION_2_3.getCode())));
        return options;
    }

    public static List<SelectOption> thirdPartyDemandChannelTypes()
    {
        List<SelectOption> options = emptyOptions();
        options.add(new SelectOption("Marketplace Of DSP", String.valueOf(ThirdPartyDemandChannel.MARKETPLACE_OF_DSP.getCode())));
        options.add(new SelectOption("StandAlone DSP", String.valueOf(ThirdPartyDemandChannel.STANDALONE_DSP_BIDDER.getCode())));
        return options;
    }
}
