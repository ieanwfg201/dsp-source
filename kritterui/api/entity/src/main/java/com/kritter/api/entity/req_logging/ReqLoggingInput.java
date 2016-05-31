package com.kritter.api.entity.req_logging;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.constants.PageConstants;
import com.kritter.constants.ReqLoggingEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class ReqLoggingInput {
    @Getter@Setter
    private int page_no = PageConstants.start_index;
    @Getter@Setter
    private int page_size = PageConstants.page_size;
    @Getter@Setter
    private String id_list = null ; /*comma separated list*/
    @Getter@Setter
    private ReqLoggingEnum reqLoggingEnum = ReqLoggingEnum.get_all_req_logging_entities;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ReqLoggingInput getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ReqLoggingInput entity = objectMapper.readValue(str, ReqLoggingInput.class);
        return entity;

    }
}
