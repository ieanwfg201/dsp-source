package com.kritter.entity.external_tracker;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class ExtTracker {

    @Getter@Setter
    private List<String> impTracker;
    @Getter@Setter
    private List<String> clickTracker;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ExtTracker getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static ExtTracker getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        ExtTracker entity = objectMapper.readValue(str, ExtTracker.class);
        return entity;

    }

}
