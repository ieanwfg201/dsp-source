package com.kritter.entity.video_props;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import lombok.Getter;
import lombok.Setter;

public class VideoInfo {
    @Getter@Setter
    private int id = -1;
    @Getter@Setter
    private String guid;
    @Getter@Setter
    private String account_guid;
    @Getter@Setter
    private Integer video_size;
    @Getter@Setter
    private String resource_uri;
    @Getter@Setter
    private int modified_by = 1;
    @Getter@Setter
    private long created_on = 0;
    @Getter@Setter
    private long last_modified = 0;
    @Getter@Setter
    private VideoInfoExt ext;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    public JsonNode toJson(){

        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public JsonNode toJsonIgnoreNull(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static VideoInfo getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        VideoInfo entity = objectMapper.readValue(str, VideoInfo.class);
        return entity;
    }
    public static VideoInfo getObjectIgnoreNull(String str) throws JsonParseException, JsonMappingException, IOException{
        VideoInfo entity = objectMapper.readValue(str, VideoInfo.class);
        return entity;
    }

}
