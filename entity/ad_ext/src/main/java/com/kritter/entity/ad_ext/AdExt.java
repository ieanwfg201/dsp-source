package com.kritter.entity.ad_ext;

import java.io.IOException;
import java.util.HashSet;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AdExt {

	@Getter@Setter
    private HashSet<Integer> mma_tier1;
    @Getter@Setter
    private HashSet<Integer> mma_tier2;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }
    
    public JsonNode toJson(){

        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AdExt getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static AdExt getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	AdExt entity = objectMapper.readValue(str, AdExt.class);
        return entity;

    }

}
