package com.kritter.api.entity.req_logging;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.req_logging.ReqLoggingEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class ReqLoggingList {
    @Getter@Setter
    private Message msg = null;
    @Getter@Setter
    private List<ReqLoggingEntity> entityList = null;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ReqLoggingList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ReqLoggingList entity = objectMapper.readValue(str, ReqLoggingList.class);
        return entity;

    }

}
