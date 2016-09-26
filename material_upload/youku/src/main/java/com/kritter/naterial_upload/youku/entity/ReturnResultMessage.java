package com.kritter.naterial_upload.youku.entity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class ReturnResultMessage {
	@Getter@Setter
	private Integer result;
	@Getter@Setter
	private Map<String, List<String>> message;
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ReturnResultMessage getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static ReturnResultMessage getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	ReturnResultMessage entity = objectMapper.readValue(str, ReturnResultMessage.class);
        return entity;
    }
    public static void main(String args[]) throws Exception{
    	String a = "{\"result\":7,\"result1\":7}";
    	System.out.println(ReturnResultMessage.getObject(a).toJson());
    }
}
