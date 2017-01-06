package com.kritter.entity.creative_macro;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import java.util.LinkedList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class CreativeMacro
{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Getter@Setter
    private LinkedList<Integer> macroIds = new LinkedList<Integer>();
    @Getter@Setter
    private int quote = 0;
    
    public JsonNode toJson(){

        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CreativeMacro getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static CreativeMacro getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        CreativeMacro entity = objectMapper.readValue(str, CreativeMacro.class);
        return entity;

    }
}
