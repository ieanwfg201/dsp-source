package com.kritter.api.entity.native_screenshot;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.kritter.constants.NativeScreenshotAPIEnum;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class NativeScreenshotListEntity {
    @Getter@Setter
    private int id = -1;
    @Getter@Setter
    private String account_guid = null;
    @Getter@Setter
    private NativeScreenshotAPIEnum nativeenum = NativeScreenshotAPIEnum.list_native_screenshot_by_account;
    @Getter@Setter
    private String id_list = null;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static NativeScreenshotListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        NativeScreenshotListEntity entity = objectMapper.readValue(str, NativeScreenshotListEntity.class);
        return entity;

    }

}
