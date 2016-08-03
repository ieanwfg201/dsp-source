package com.kritter.api.entity.video_info;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.video_props.VideoInfo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class VideoInfoList {
    @Getter@Setter
    private Message msg = null;
    @Getter@Setter
    private List<VideoInfo> cblist = null;
    
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    
    public static VideoInfoList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        VideoInfoList entity = objectMapper.readValue(str, VideoInfoList.class);
        return entity;

    }
    
}
