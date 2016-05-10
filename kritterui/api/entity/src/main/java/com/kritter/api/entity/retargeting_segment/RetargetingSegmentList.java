package com.kritter.api.entity.retargeting_segment;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.retargeting_segment.RetargetingSegment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class RetargetingSegmentList {
    /** @see com.kritter.api.entity.response.msg.Message */
    @Getter@Setter
    private Message msg = null;
    /** @see com.kritter.entity.retargeting_segment.RetargetingSegment */
    @Getter@Setter
    private List<RetargetingSegment> retargeting_segment_list = null;
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static RetargetingSegmentList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        RetargetingSegmentList entity = objectMapper.readValue(str, RetargetingSegmentList.class);
        return entity;
    }
}
