package com.kritter.naterial_upload.cloudcross.banner;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class CloudCrossMaterialUploadEntity {
	@Getter@Setter
	public String url;
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

	public CloudCrossMaterialUploadEntity(){
	}
    public CloudCrossMaterialUploadEntity(String url, String landingpage, String advertiser, String startdate,
										  String enddate, String[] monitor) {
		super();
		this.url = url;
		this.landingpage = landingpage;
		this.advertiser = advertiser;
		this.startdate = startdate;
		this.enddate = enddate;
		if(monitor == null){
			this.monitor= new String[0];
		}else{
			this.monitor = monitor;
		}
	}
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CloudCrossMaterialUploadEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static CloudCrossMaterialUploadEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	CloudCrossMaterialUploadEntity entity = objectMapper.readValue(str, CloudCrossMaterialUploadEntity.class);
        return entity;

    }
    /*public static void main(String args[]){
    	CloudCrossMaterialUploadEntity h = new CloudCrossMaterialUploadEntity("sxcd", "wqdwq", "qwdqw", "adsqaw", "wdwe",null);
    	System.out.println(h.toJson());
    	*/
}
