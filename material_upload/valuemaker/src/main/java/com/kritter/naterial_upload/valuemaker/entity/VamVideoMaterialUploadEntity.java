package com.kritter.naterial_upload.valuemaker.entity;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class VamVideoMaterialUploadEntity {
	@Getter@Setter
	public String id;//DSP 自己系统中的视频创意 id。
	@Getter@Setter
	public Integer category;
	@Getter@Setter
	public String landingpage;//点击跳转地址 URL,需包含双方 点击检测宏{!vam_click_url}(或{!vam_click_url_esc})和 {!dsp_click_url}。
	@Getter@Setter
	public String[] adomain_list;//落地页主域名。
	@Getter@Setter
	public Integer width;//素材分辨率宽。
	@Getter@Setter
	public Integer height;//素材分辨率高。
	@Getter@Setter
	public Integer format;//创意素材格式:1-STATIC_PIC, 2-DYNAMIC_PIC,3-SWF,5-FLV,6-MP4,7-OGG,8-WebM
	@Getter@Setter
	public Integer duration;//视频创意时长
	@Getter@Setter
	public String fileurl;//存放于 DSP 的 CDN 上的视频素材 地址 URL。
	@Getter@Setter
	public String advertiser;//选填,广告主的中文名称
	@Getter@Setter
	public Integer creative_type;//选填,创意类型:0-贴片(默认),1- 角标,2-overlay
	
	public VamVideoMaterialUploadEntity(){
	}

	public VamVideoMaterialUploadEntity(String id, Integer category, String landingpage,String[] adomain_list,Integer width,Integer height,
											   Integer format,Integer duration,String fileurl,String advertiser,Integer creative_type) {
		super();
		this.id=id;
		this.category=category;
		this.landingpage=landingpage;
		if(adomain_list == null){
			this.adomain_list= new String[0];
		}else{
			this.adomain_list = adomain_list;
		}
		this.width=width;
		this.height=height;
		this.format=format;
		this.duration=duration;
		this.fileurl = fileurl;
		this.advertiser=advertiser;
		this.creative_type=creative_type;
	}

	public JsonNode toJson(){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		JsonNode jsonNode = objectMapper.valueToTree(this);
		return jsonNode;
	}
	public static VamVideoMaterialUploadEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return getObject(objectMapper,str);
	}
	public static VamVideoMaterialUploadEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
		VamVideoMaterialUploadEntity entity = objectMapper.readValue(str,VamVideoMaterialUploadEntity.class);
		return entity;

	}

}
