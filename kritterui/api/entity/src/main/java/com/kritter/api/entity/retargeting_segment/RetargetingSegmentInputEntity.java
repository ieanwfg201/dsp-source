package com.kritter.api.entity.retargeting_segment;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.constants.PageConstants;
import com.kritter.constants.RetargetingSegmentEnum;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class RetargetingSegmentInputEntity {
    /** mandatory - @see com.kritter.constants.RetargetingSegmentEnum */
    @Getter@Setter
    private RetargetingSegmentEnum retargetingSegmentEnum = RetargetingSegmentEnum.get_retargeting_segments_by_ids;
    /** mandatory - @see com.kritter.constants.PageConstants */
    @Getter@Setter
    private int page_no = PageConstants.start_index;
    /** mandatory - @see com.kritter.constants.PageConstants */
    @Getter@Setter
    private int page_size = PageConstants.page_size;
    /** comma separated id of retargeting segment or guid of account*/
    @Getter@Setter
    private String id_list = null;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static RetargetingSegmentInputEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        RetargetingSegmentInputEntity entity = objectMapper.readValue(str, RetargetingSegmentInputEntity.class);
        return entity;
    }

}
