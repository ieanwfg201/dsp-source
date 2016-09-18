package com.kritter.naterial_upload.youku.entity;

import java.io.IOException;

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
public class YoukuVideoId {
	@Getter@Setter
	private String video_id;
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static YoukuVideoId getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static YoukuVideoId getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	YoukuVideoId entity = objectMapper.readValue(str, YoukuVideoId.class);
        return entity;
    }
    public static void main(String args[]) throws Exception{
    	String a = "{\"result\":7,\"result1\":7}";
    	System.out.println(YoukuVideoId.getObject(a).toJson());
    }
}
