package models.advertiser;

import services.MetadataAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritter.api.entity.metadata.MetaField;
import com.kritter.entity.native_props.demand.NativeScreenshot;
 

public class NativeScreenshotDisplay{
	
	private NativeScreenshot nativeScreenshot = null;
	private MetaField slot = null;
	
	public NativeScreenshotDisplay(NativeScreenshot nativeScreenshot){
		this.nativeScreenshot = nativeScreenshot; 
		this.slot = MetadataAPI.getNativeScreenshotSlot(nativeScreenshot.getSs_size());
	}

	 
	public int getId(){
		return nativeScreenshot.getId();
	} 
	
	public String getBannerUrl(){
		return "/images/banners/original"+nativeScreenshot.getResource_uri();
	}
	
	public String getBannerThumbUrl(){
		return  "/images/banners/thumbnails"+nativeScreenshot.getResource_uri();
	}
	
	public String getSlotName(){
		return slot.getName();
	}
	
	public String getSlotDescription(){
		return slot.getDescription();
	}
	
	public JsonNode toJson(){
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.valueToTree(this);  
	}
	 
}
