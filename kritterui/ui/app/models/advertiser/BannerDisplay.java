package models.advertiser;

import services.MetadataAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.metadata.MetaField;
 

public class BannerDisplay{
	
	private Creative_banner creativeBanner = null;
	private MetaField slot = null;
	
	public BannerDisplay(Creative_banner creativeBanner){
		this.creativeBanner = creativeBanner; 
		this.slot = MetadataAPI.getSlot(creativeBanner.getSlot_id());
	}

	 
	public int getId(){
		return creativeBanner.getId();
	} 
	
	public String getBannerUrl(){
		return "/images/banners/original"+creativeBanner.getResource_uri();
	}
	
	public String getBannerThumbUrl(){
		return  "/images/banners/thumbnails"+creativeBanner.getResource_uri();
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
