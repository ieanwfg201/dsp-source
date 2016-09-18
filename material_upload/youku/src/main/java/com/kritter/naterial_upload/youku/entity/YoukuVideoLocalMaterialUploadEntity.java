package com.kritter.naterial_upload.youku.entity;

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
public class YoukuVideoLocalMaterialUploadEntity {
	/**YoukuUrl**/
	@Getter@Setter
	public String youkuurl;
	@Getter@Setter
	public String internalurl;
	@Getter@Setter
	public String landingpage;
	@Getter@Setter
	public String advertiser;
	@Getter@Setter
	public String startdate;
	@Getter@Setter
	public String enddate;
	@Getter@Setter
	public String[] monitor;
	@Getter@Setter
	public String creativeName;
	@Getter@Setter
	public String resource_uri;
	@Getter@Setter
	public Integer videoInfoId;
	
	public YoukuVideoLocalMaterialUploadEntity(){
	}
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static YoukuVideoLocalMaterialUploadEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static YoukuVideoLocalMaterialUploadEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	YoukuVideoLocalMaterialUploadEntity entity = objectMapper.readValue(str, YoukuVideoLocalMaterialUploadEntity.class);
        return entity;

    }
	public YoukuVideoLocalMaterialUploadEntity(String youkuurl, String internalurl, String landingpage,
			String advertiser, String startdate, String enddate, String[] monitor,String creativeName,String resource_uri,Integer videoInfoId) {
		super();
		this.youkuurl = youkuurl;
		this.internalurl = internalurl;
		this.landingpage = landingpage;
		this.advertiser = advertiser;
		this.startdate = startdate;
		this.enddate = enddate;
		this.monitor = monitor;
		this.creativeName = creativeName;
		this.resource_uri=resource_uri;
		this.videoInfoId = videoInfoId;
	}
	public static boolean equalityWithoutYoukuUrl(YoukuVideoLocalMaterialUploadEntity oldentity, YoukuVideoLocalMaterialUploadEntity newentity){
		if(oldentity.getInternalurl() != null && newentity.getInternalurl() !=null 
				&& !oldentity.getInternalurl().equals(newentity.getInternalurl())){
			return false;
		}
		if(oldentity.getInternalurl() != null && newentity.getInternalurl() ==null){
			return false;
		}
		if(oldentity.getInternalurl() == null && newentity.getInternalurl() !=null){
			return false;
		}
		if(oldentity.getAdvertiser() != null && newentity.getAdvertiser() !=null 
				&& !oldentity.getAdvertiser().equals(newentity.getAdvertiser())){
			return false;
		}
		if(oldentity.getLandingpage() != null && newentity.getLandingpage() !=null 
				&& !oldentity.getLandingpage().equals(newentity.getLandingpage())){
			return false;
		}
		if(oldentity.getLandingpage() != null && newentity.getLandingpage() ==null){
			return false;
		}
		if(oldentity.getLandingpage() == null && newentity.getLandingpage() !=null){
			return false;
		}
		if(oldentity.getAdvertiser() != null && newentity.getAdvertiser() ==null){
			return false;
		}
		if(oldentity.getAdvertiser() == null && newentity.getAdvertiser() !=null){
			return false;
		}
		if(oldentity.getStartdate() != null && newentity.getStartdate() !=null 
				&& !oldentity.getStartdate().equals(newentity.getStartdate())){
			return false;
		}
		if(oldentity.getStartdate() != null && newentity.getStartdate() ==null){
			return false;
		}
		if(oldentity.getStartdate() == null && newentity.getStartdate() !=null){
			return false;
		}
		if(oldentity.getEnddate() != null && newentity.getEnddate() !=null 
				&& !oldentity.getEnddate().equals(newentity.getEnddate())){
			return false;
		}
		if(oldentity.getEnddate() != null && newentity.getEnddate() ==null){
			return false;
		}
		if(oldentity.getEnddate() == null && newentity.getEnddate() !=null){
			return false;
		}
		if(oldentity.getMonitor() != null && newentity.getMonitor() !=null 
				&& !oldentity.getMonitor().equals(newentity.getMonitor())){
			return false;
		}
		if(oldentity.getMonitor() != null && newentity.getMonitor() ==null){
			return false;
		}
		if(oldentity.getMonitor() == null && newentity.getMonitor() !=null){
			return false;
		}
			
		return true;
	}
	public static YoukuMaterialUploadEntity createEntityforUpload(YoukuVideoLocalMaterialUploadEntity entity){
		if(entity == null){
			return null;
		}
		YoukuMaterialUploadEntity newentity= new YoukuMaterialUploadEntity();
		newentity.setAdvertiser(entity.getAdvertiser());
		newentity.setEnddate(entity.getEnddate());
		newentity.setStartdate(entity.getStartdate());
		newentity.setLandingpage(entity.getLandingpage());
		newentity.setMonitor(entity.getMonitor());
		newentity.setUrl(entity.getInternalurl());
		return newentity;
	}
    /*public static void main(String args[]){
    	YoukuMaterialUploadEntity h = new YoukuMaterialUploadEntity("sxcd", "wqdwq", "qwdqw", "adsqaw", "wdwe",null);
    	System.out.println(h.toJson());
    	*/
}
