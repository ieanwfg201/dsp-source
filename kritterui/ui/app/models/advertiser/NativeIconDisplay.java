package models.advertiser;

import services.MetadataAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritter.api.entity.metadata.MetaField;
import com.kritter.entity.native_props.demand.NativeIcon;
 

public class NativeIconDisplay{
	
	private NativeIcon nativeIcon = null;
	private MetaField slot = null;
	
	public NativeIconDisplay(NativeIcon nativeIcon){
		this.nativeIcon = nativeIcon; 
		this.slot = MetadataAPI.getNativeIconSlot(nativeIcon.getIcon_size());
	}

	 
	public int getId(){
		return nativeIcon.getId();
	} 
	
	public String getBannerUrl(){
		return "/images/banners/original"+nativeIcon.getResource_uri();
	}
	
	public String getBannerThumbUrl(){
		return  "/images/banners/thumbnails"+nativeIcon.getResource_uri();
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
