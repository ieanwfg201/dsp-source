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
    private BidResponseEntity winnerBidResponse = null;
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static WinEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static WinEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        WinEntity entity = objectMapper.readValue(str, WinEntity.class);
        return entity;

    }
}
