package com.kritter.entity.payout_threshold;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class CampaignPayoutThreshold
{

    @Getter@Setter
    private Integer campaign_id;
    @Getter@Setter
    private Float absolute_threshold=-1.0f;
    @Getter@Setter
    private Float percentage_threshold=-1.0f;
    @Getter@Setter
    private long last_modified = 0;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CampaignPayoutThreshold getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static CampaignPayoutThreshold getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        CampaignPayoutThreshold entity = objectMapper.readValue(str, CampaignPayoutThreshold.class);
        return entity;

    }
}
