package models.advertiser;

import services.MetadataAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritter.api.entity.metadata.MetaField;
import com.kritter.entity.video_props.VideoInfo;
 

public class DirectvideoDisplay{
	
	private VideoInfo videoInfo = null;
	private MetaField slot = null;
	
	public DirectvideoDisplay(VideoInfo videoInfo){
		this.videoInfo = videoInfo; 
		this.slot = MetadataAPI.getSlot(videoInfo.getVideo_size());
	}

	 
	public int getId(){
		return videoInfo.getId();
	} 
	
	public String getBannerUrl(){
		return "/images/banners/original"+videoInfo.getResource_uri();
	}
	public String getDownloadUrl(){
		return "/download?file=/banners/original"+videoInfo.getResource_uri();
	}
	
	public String getBannerThumbUrl(){
		return  "/images/banners/thumbnails"+videoInfo.getResource_uri();
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
