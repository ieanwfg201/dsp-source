package com.kritter.api.entity.payoutthreshold;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.payout_threshold.CampaignPayoutThreshold;
import com.kritter.entity.payout_threshold.DefaultPayoutThreshold;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class PayoutThresholdList {
    /** @see com.kritter.api.entity.response.msg.Message */
	@Getter@Setter
    private Message msg = null;
    /** @see com.kritter.entity.payout_threshold.DefaultPayoutThreshold*/
	@Getter@Setter
    private List<DefaultPayoutThreshold> default_entity_list = null;
    /** @see com.kritter.entity.payout_threshold.CampaignPayoutThreshold*/	
	@Getter@Setter
    private List<CampaignPayoutThreshold> campaign_entity_list = null;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static PayoutThresholdList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static PayoutThresholdList getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	PayoutThresholdList entity = objectMapper.readValue(str, PayoutThresholdList.class);
        return entity;

    }
 
}
