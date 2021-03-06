package com.kritter.entity.winner;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.bidrequest.entity.common.openrtbversion2_3.BidResponseEntity;

import lombok.Getter;
import lombok.Setter;

public class WinEntity {
    @Getter@Setter
    private String advId = "-1";
    @Getter@Setter
    private float win_price = 0.0001f;
    @Getter@Setter
    private BidResponseEntity winnerBidResponse2_3 = null;
    @Getter@Setter
    private com.kritter.bidrequest.entity.common.openrtbversion2_2.BidResponseEntity winnerBidResponse2_2 = null;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static WinEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static WinEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        WinEntity entity = objectMapper.readValue(str, WinEntity.class);
        return entity;

    }
}
