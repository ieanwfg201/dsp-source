package com.kritter.api.entity.ad;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class AdList {
    /** @see com.kritter.api.entity.response.msg.Message */
    private Message msg = null;
    /** @see com.kritter.api.entity.ad.Ad */
    private List<Ad> adlist = null;
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adlist == null) ? 0 : adlist.hashCode());
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
        AdList other = (AdList) obj;
        if (adlist == null) {
            if (other.adlist != null)
                return false;
        } else if (!adlist.equals(other.adlist))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }
    public Message getMsg() {
        return msg;
    }
    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public List<Ad> getAdlist() {
        return adlist;
    }
    public void setAdlist(List<Ad> adlist) {
        this.adlist = adlist;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AdList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        AdList entity = objectMapper.readValue(str, AdList.class);
        return entity;

    }
}
