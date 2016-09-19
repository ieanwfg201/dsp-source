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
public class APHealthyReturn {
	@Getter@Setter
	private Integer result;
	@Getter@Setter
	private APMessage message;
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static APHealthyReturn getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return getObject(objectMapper,str);
    }
    public static APHealthyReturn getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	APHealthyReturn entity = objectMapper.readValue(str, APHealthyReturn.class);
        return entity;
    }
    /*public static void main(String args[]) throws Exception{
    	String a ="{\"result\":0,\"message\":{\"total\" :2,\"count\" :2,\"records\" : [{\"adplacementid\": 101,\"adplacementname\": \"Auto-Home page-banner\" ,\"size\": \"300*250\" ,\"bidfloor\": 20 ,\"blockcategory\": [\"1\", \"2\"] ,\"allowmaterial\": [\"swf\", \"png\"]},{\"adplacementid\": 102,\"adplacementname\": \"News-Video\",\"size\": \"400*300\",\"bidfloor\": 15 ,\"blockcategory\": [],\"allowmaterial\": [\"flv\"]}]}}";
    	System.out.println(APHealthyReturn.getObject(a).toJson());
    }*/
}
