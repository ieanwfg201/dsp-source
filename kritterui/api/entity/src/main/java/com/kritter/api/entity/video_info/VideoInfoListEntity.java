package com.kritter.api.entity.video_info;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.kritter.constants.VideoInfoAPIEnum;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class VideoInfoListEntity {
    @Getter@Setter
    private int id = -1;
    @Getter@Setter
    private String account_guid = null;
    @Getter@Setter
    private VideoInfoAPIEnum videoenum = VideoInfoAPIEnum.list_video_info_by_account;
    @Getter@Setter
    private String id_list = null;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static VideoInfoListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        VideoInfoListEntity entity = objectMapper.readValue(str, VideoInfoListEntity.class);
        return entity;

    }

}
