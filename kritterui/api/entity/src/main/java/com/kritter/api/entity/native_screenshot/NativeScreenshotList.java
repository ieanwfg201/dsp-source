package com.kritter.api.entity.native_screenshot;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.native_props.demand.NativeScreenshot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class NativeScreenshotList {
    @Getter@Setter
    private Message msg = null;
    @Getter@Setter
    private List<NativeScreenshot> cblist = null;
    
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    
    public static NativeScreenshotList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        NativeScreenshotList entity = objectMapper.readValue(str, NativeScreenshotList.class);
        return entity;

    }
    
}
