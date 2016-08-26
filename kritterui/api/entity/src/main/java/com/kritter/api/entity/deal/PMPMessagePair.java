package com.kritter.api.entity.deal;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class PMPMessagePair
{
    private PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity = null;
    /** @see com.kritter.api.entity.response.msg.Message */
    private Message msg = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((privateMarketPlaceApiEntity == null) ? 0 : privateMarketPlaceApiEntity.hashCode());
        result = prime * result + ((msg == null) ? 0 : msg.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PMPMessagePair other = (PMPMessagePair) obj;
        if (privateMarketPlaceApiEntity == null) {
            if (other.privateMarketPlaceApiEntity != null)
                return false;
        } else if (!privateMarketPlaceApiEntity.equals(other.privateMarketPlaceApiEntity))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }
    public PrivateMarketPlaceApiEntity getPrivateMarketPlaceApiEntity() {
        return privateMarketPlaceApiEntity;
    }
    public void setPrivateMarketPlaceApiEntity(PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity) {
        this.privateMarketPlaceApiEntity = privateMarketPlaceApiEntity;
    }
    public Message getMsg() {
        return msg;
    }
    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static PMPMessagePair getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        PMPMessagePair entity = objectMapper.readValue(str, PMPMessagePair.class);
        return entity;

    }

}
