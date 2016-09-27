package com.kritter.entity.account;

import lombok.Getter;
import lombok.Setter;
import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class AdxAccountExt
{
    @Getter@Setter
    public String firstInd; /*first industry code*/
    @Getter@Setter
    public String secondInd; /*second industry*/
    @Getter@Setter
    public String qname; /*qualification name*/
    @Getter@Setter
    public String qurl; /*image cdn url*/
    @Getter@Setter
    public String md5; /*image md5*/
    
    public static AdxAccountExt getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static AdxAccountExt getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        AdxAccountExt entity = objectMapper.readValue(str, AdxAccountExt.class);
        return entity;

    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

}
