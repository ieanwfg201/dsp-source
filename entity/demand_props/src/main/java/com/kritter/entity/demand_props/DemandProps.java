package com.kritter.entity.demand_props;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class DemandProps
{

    @Getter@Setter
    private boolean test=false;
    @Getter@Setter
    private String demand_url;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static DemandProps getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static DemandProps getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        DemandProps entity = objectMapper.readValue(str, DemandProps.class);
        return entity;

    }
}
