package com.kritter.entity.account;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import lombok.Getter;
import lombok.Setter;

public class Qualification {
    @Getter@Setter
    public Integer internalid=-1; 
    @Getter@Setter
    public Integer advIncId; 
    @Getter@Setter
    public String qname; /*qualification name*/
    @Getter@Setter
    public String qurl; /*image cdn url*/
    @Getter@Setter
    public String md5; /*image md5*/
    @Getter@Setter
    public Integer state; /*add,update,delete*/
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public static Qualification getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static Qualification getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	Qualification entity = objectMapper.readValue(str, Qualification.class);
        return entity;

    }
    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

}
