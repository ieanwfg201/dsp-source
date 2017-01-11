package com.kritter.entity.retargeting_segment;

import lombok.Getter;
import lombok.Setter;
import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class RetargetingSegment
{
    @Getter@Setter
    public int retargeting_segment_id;
    @Getter@Setter
    public String name;
    @Getter@Setter
    public String tag;
    @Getter@Setter
    public boolean is_deprecated = false;
    @Getter@Setter
    public String account_guid;
    @Getter@Setter
    private long created_on = 0;
    @Getter@Setter
    private long last_modified = 0;
    @Getter@Setter
    private int modified_by = 1;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    
    public static RetargetingSegment getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static RetargetingSegment getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        RetargetingSegment entity = objectMapper.readValue(str, RetargetingSegment.class);
        return entity;

    }
}
