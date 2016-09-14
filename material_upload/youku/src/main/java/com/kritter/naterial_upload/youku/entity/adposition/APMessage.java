package com.kritter.naterial_upload.youku.entity.adposition;

import java.io.IOException;
import java.util.List;

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
public class APMessage {
	@Getter@Setter
	private Integer total;
	@Getter@Setter
	private Integer count;
	@Getter@Setter
	private List<APRecord> records;
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static APMessage getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return getObject(objectMapper,str);
    }
    public static APMessage getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	APMessage entity = objectMapper.readValue(str, APMessage.class);
        return entity;
    }
    public static void main(String args[]) throws Exception{
    	String a = "{\"result\":7,\"message\":\"lwdhls\"}";
    	System.out.println(APMessage.getObject(a).toJson());
    }
}
