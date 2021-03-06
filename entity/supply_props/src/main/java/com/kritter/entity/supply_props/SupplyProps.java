package com.kritter.entity.supply_props;

import lombok.Getter;
import lombok.Setter;
import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;



public class SupplyProps
{

    @Getter@Setter
    private Short[] btype=null;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static SupplyProps getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static SupplyProps getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        SupplyProps entity = objectMapper.readValue(str, SupplyProps.class);
        return entity;

    }
}
