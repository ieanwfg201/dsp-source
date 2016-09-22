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
public class CloudCrossMaterialAuditEntity {
	@Getter@Setter
	public String dspid;
	@Getter@Setter
	public String token;
	@Getter@Setter
	public String materialurl;
	
	
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CloudCrossMaterialAuditEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static CloudCrossMaterialAuditEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	CloudCrossMaterialAuditEntity entity = objectMapper.readValue(str, CloudCrossMaterialAuditEntity.class);
        return entity;

    }
    /*public static void main(String args[]){
    	CloudCrossMaterialUploadEntity h = new CloudCrossMaterialUploadEntity("sxcd", "wqdwq", "qwdqw", "adsqaw", "wdwe",null);
    	System.out.println(h.toJson());
    	*/
}
