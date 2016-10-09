package com.kritter.naterial_upload.youku.entity;

import java.io.IOException;
import java.util.List;

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
public class YoukuAdvInfoLocaLMaterialUploadEntity {
	@Getter@Setter
	public String name;
	@Getter@Setter
	public String brand;
	@Getter@Setter
	public String address;
	@Getter@Setter
	public String contacts;
	@Getter@Setter
	public String tel;
	@Getter@Setter
	public Integer firstindustry;
	@Getter@Setter
	public Integer secondindustry;
	@Getter@Setter
	public List<YoukuQualifications> qualifications;

	public YoukuAdvInfoLocaLMaterialUploadEntity(){
	}
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static YoukuAdvInfoLocaLMaterialUploadEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static YoukuAdvInfoLocaLMaterialUploadEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	YoukuAdvInfoLocaLMaterialUploadEntity entity = objectMapper.readValue(str, YoukuAdvInfoLocaLMaterialUploadEntity.class);
        return entity;

    }
    /*public static void main(String args[]){
    	YoukuMaterialUploadEntity h = new YoukuMaterialUploadEntity("sxcd", "wqdwq", "qwdqw", "adsqaw", "wdwe",null);
    	System.out.println(h.toJson());
    	*/
}
