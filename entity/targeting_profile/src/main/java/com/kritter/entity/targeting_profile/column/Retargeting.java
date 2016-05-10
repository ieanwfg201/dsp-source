package com.kritter.entity.targeting_profile.column;

import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class Retargeting
{

    @Getter@Setter
    private List<Integer> segment;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Retargeting getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static Retargeting getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        Retargeting entity = objectMapper.readValue(str, Retargeting.class);
        return entity;

    }
}
