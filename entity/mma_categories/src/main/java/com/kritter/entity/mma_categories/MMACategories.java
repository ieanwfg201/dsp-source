package com.kritter.entity.mma_categories;

import java.io.IOException;

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
public class MMACategories {

    @Getter@Setter
    private int id;
	@Getter@Setter
    private String code;
    @Getter@Setter
    private String name;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static MMACategories getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static MMACategories getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	MMACategories entity = objectMapper.readValue(str, MMACategories.class);
        return entity;

    }

}
