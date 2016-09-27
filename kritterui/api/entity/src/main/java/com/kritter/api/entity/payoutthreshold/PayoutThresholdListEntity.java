package com.kritter.api.entity.payoutthreshold;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import com.kritter.constants.PageConstants;
import com.kritter.constants.PayoutThresholdListEnum;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class PayoutThresholdListEntity {
    /** mandatory @see com.kritter.constants.PageConstants */
	@Getter@Setter
    private int page_no = PageConstants.start_index;
    /** mandatory @see com.kritter.constants.PageConstants */
	@Getter@Setter
    private int page_size = PageConstants.page_size;
    /**optional - depends on queryEnum-  comma seperated ids*/
	@Getter@Setter
    private String id_list = null;
	@Getter@Setter
    /** @see com.kritter.constants.PayoutThresholdListEnum  mandatory - type of action available */
    private PayoutThresholdListEnum queryEnum = PayoutThresholdListEnum.campaign_payout_threshold;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static PayoutThresholdListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static PayoutThresholdListEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	PayoutThresholdListEntity entity = objectMapper.readValue(str, PayoutThresholdListEntity.class);
        return entity;

    }
    
}
