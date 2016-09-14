package com.kritter.naterial_upload.youku.entity.adposition;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class APRecord {
	@Getter@Setter
	private Integer adplacementid;
	@Getter@Setter
	private String adplacementname;
	@Getter@Setter
	private String size;
	@Getter@Setter
	private Double bidfloor;
	@Getter@Setter
	private String[] blockcategory;
	@Getter@Setter
	private String[] allowmaterial;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static APRecord getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return getObject(objectMapper,str);
    }
    public static APRecord getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	APRecord entity = objectMapper.readValue(str, APRecord.class);
        return entity;
    }
}
