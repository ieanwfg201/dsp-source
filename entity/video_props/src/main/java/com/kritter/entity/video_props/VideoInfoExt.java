package com.kritter.entity.video_props;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import lombok.Getter;
import lombok.Setter;

public class VideoInfoExt {
	@Getter@Setter
	private String extCDNUrl;
	@Getter@Setter
	private String youkuCDNUrl;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public JsonNode toJsonIgnoreNull(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static VideoInfoExt getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        VideoInfoExt entity = objectMapper.readValue(str, VideoInfoExt.class);
        return entity;
    }
    public static VideoInfoExt getObjectIgnoreNull(String str) throws JsonParseException, JsonMappingException, IOException{
        VideoInfoExt entity = objectMapper.readValue(str, VideoInfoExt.class);
        return entity;
    }

}
