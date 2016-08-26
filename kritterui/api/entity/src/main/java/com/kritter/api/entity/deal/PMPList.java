package com.kritter.api.entity.deal;

import com.kritter.api.entity.response.msg.Message;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class PMPList {
    /** @see Message */
    private Message msg = null;
    /** @see PrivateMarketPlaceApiEntity */
    private List<PrivateMarketPlaceApiEntity> pmp_list = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((msg == null) ? 0 : msg.hashCode());
        result = prime * result
                + ((pmp_list == null) ? 0 : pmp_list.hashCode());
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
        PMPList other = (PMPList) obj;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        if (pmp_list == null) {
            if (other.pmp_list != null)
                return false;
        } else if (!pmp_list.equals(other.pmp_list))
            return false;
        return true;
    }
    public Message getMsg() {
        return msg;
    }
    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public List<PrivateMarketPlaceApiEntity> getPMP_list() {
        return pmp_list;
    }
    public void setPMP_list(List<PrivateMarketPlaceApiEntity> pmp_list) {
        this.pmp_list = pmp_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static PMPList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        PMPList entity = objectMapper.readValue(str, PMPList.class);
        return entity;
    }
}
