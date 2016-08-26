package models.advertiser;

import java.util.ArrayList;
import java.util.List;
import services.DataAPI;
import services.MetadataAPI;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.constants.APIFrameworks;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.CreativeMacroQuote;
import com.kritter.constants.MetadataType;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoBoxing;
import com.kritter.constants.VideoDemandType;
import com.kritter.constants.VideoLinearity;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.constants.VideoPlaybackMethods;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.entity.native_props.demand.NativeScreenshot;
import com.kritter.entity.video_props.VideoInfo;


public class CreativeDisplayFull extends CreativeDisplay{

	 
	public CreativeDisplayFull(Creative_container cc){
		super(cc);
	}

	public List<BannerDisplay> getBanners(){
		String ids = cc.getResource_uri_ids();
		List<BannerDisplay> bannerList = new ArrayList<BannerDisplay>();
		if(ids != ""){ 
			List<Creative_banner> cbList = DataAPI.getCreativeBannerList(ids);
			for (Creative_banner creative_banner : cbList) {
				bannerList.add(new BannerDisplay(creative_banner));
			}
		} 
		return bannerList;
	}

	public List<DirectvideoDisplay> getDirectvideodisplay(){
		String ids = cc.getDirect_videos();
		List<DirectvideoDisplay> viList = new ArrayList<DirectvideoDisplay>();
		if(ids != null && ids != ""){ 
			List<VideoInfo> cbList = DataAPI.getDirectVideoList(ids.replaceAll("\\[", "]").replaceAll("]", ""));
			for (VideoInfo video_info : cbList) {
				viList.add(new DirectvideoDisplay(video_info));
			}
		} 
		return viList;
	}

	public List<NativeIconDisplay> getNativeIcons(){
	    String ids = cc.getNative_icons();
	    List<NativeIconDisplay> nativeList = new ArrayList<NativeIconDisplay>();
	    if(ids != ""){ 
	        List<NativeIcon> cbList = DataAPI.getNativeIconList(ids);
	        for (NativeIcon native_icon : cbList) {
	            nativeList.add(new NativeIconDisplay(native_icon));
	        }
	    } 
	    return nativeList;
	}
	
	public List<NativeScreenshotDisplay> getNativeScreenshots(){
        String ids = cc.getNative_screenshots();
        List<NativeScreenshotDisplay> nativeList = new ArrayList<NativeScreenshotDisplay>();
        if(ids != ""){ 
            List<NativeScreenshot> cbList = DataAPI.getNativeScreenshotList(ids);
            for (NativeScreenshot native_screenshot : cbList) {
                nativeList.add(new NativeScreenshotDisplay(native_screenshot));
            }
        } 
        return nativeList;
}

	public List<String> getCreativeAttrs() { 
		String crids=  cc.getCreative_attr();
		List<String> creativeAttrList = MetadataAPI.getValues(MetadataType.CREATIVE_ATTRIBUTES_BY_ID, crids);
		return creativeAttrList;
	}
	public List<String> getTrackingEvents() { 
	    return MetadataAPI.videoTrackingEvents(cc.getTrackingStr());
	}

	public String getExtcreativeurl(){
	    return cc.getExt_resource_url();
	}
	
    public String getNativeActivePlayer(){
        if(cc.getNative_active_players() != null){
            return cc.getNative_active_players();
        }
        return "";
    }
    public String getNativeCta(){
        if(cc.getNative_cta() != null){
            return cc.getNative_cta();
        }
        return "";
    }
    public String getNativeDesc(){
        if(cc.getNative_desc() != null){
            return cc.getNative_desc();
        }
        return "";
    }
    public String getNativeDownloadcount(){
        if(cc.getNative_download_count() != null){
            return cc.getNative_download_count();
        }
        return "";
    }
    public String getNativeRating(){
        if(cc.getNative_rating()){
            return "True";
        }
        return "False";
    }
	public String getNativeTitle(){
	    if(cc.getNative_title() != null){
	        return cc.getNative_title();
	    }
	    return "";
	}
    public String getVideoDemandType(){
        return VideoDemandType.getEnum(cc.getVideoDemandType()).getName();
    }
    public String getVastTagUrl(){
        return cc.getVastTagUrl();
    }
    public String getMime(){
        return VideoMimeTypes.getEnum(cc.getMime()).getMime();
    }
    public int getDuration(){
        return cc.getDuration();
    }
    public String getProtocol(){
        return VideoBidResponseProtocols.getEnum(cc.getProtocol()).getName();
    }
    public int getStartDelay(){
        return cc.getStartdelay();
    }
    public int getWidth(){
        return cc.getWidth();
    }
    public int getHeight(){
        return cc.getHeight();
    }
    public String getLinearity(){
        return VideoLinearity.getEnum(cc.getLinearity()).getName();
    }
    public int getMaxExtended(){
        return cc.getMaxextended();
    }
    public int getBitRate(){
        return cc.getBitrate();
    }
    public String getBoxingAllowed(){
        return VideoBoxing.getEnum(cc.getBoxingallowed()).getName();
    }
    public String getPlaybackmethod(){
        return VideoPlaybackMethods.getEnum(cc.getPlaybackmethod()).getName();
    }
    public String getDelivery(){
        return ContentDeliveryMethods.getEnum(cc.getDelivery()).getName();
    }
    public String getAPI(){
        return APIFrameworks.getEnum(cc.getApi()).getName();
    }
    public String getCompanionType(){
        return VASTCompanionTypes.getEnum(cc.getCompaniontype()).getName();
    }
    public List<String> getCreativemacro() {
        return MetadataAPI.getCreativeMacroByName(cc.getCreative_macro());
    }
    public String getCreativemacroquote(){
        CreativeMacroQuote c = CreativeMacroQuote.getEnum(cc.getCreative_macro_quote());
        if(c != null){
            return c.getName();
        }
        return "";
    }
    public List<String> getVastTagmacro() {
        return MetadataAPI.getCreativeMacroByName(cc.getVastTagMacro());
    }
    public String getVastTagquote(){
        CreativeMacroQuote c = CreativeMacroQuote.getEnum(cc.getVastTagMacroQuote());
        if(c != null){
            return c.getName();
        }
        return "";
    }
/**
                        @components.formElements.selectlist2(creativeForm("companiontype"), "",services.MetadataAPI.companiontype() )

 */
}
