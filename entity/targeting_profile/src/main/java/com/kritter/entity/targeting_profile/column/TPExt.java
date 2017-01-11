package com.kritter.entity.targeting_profile.column;

import java.io.IOException;
import java.util.HashSet;

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
public class TPExt {

    @Getter@Setter
    private boolean inc=true; //false mean exclusion
    @Getter@Setter
    private HashSet<Integer> mma_tier1;
    @Getter@Setter
    private HashSet<Integer> mma_tier2;
    @Getter@Setter
    private boolean adposids_inc=true; //false mean exclusion
    @Getter@Setter
    private HashSet<Integer> adposids;
    @Getter@Setter
    private boolean channel_inc=true; //false mean exclusion
    @Getter@Setter
    private HashSet<Integer> channel_tier1;
    @Getter@Setter
    private HashSet<Integer> channel_tier2;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static TPExt getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static TPExt getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	TPExt entity = objectMapper.readValue(str, TPExt.class);
        return entity;

    }

}
