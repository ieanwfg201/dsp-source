package com.kritter.api.entity.creative_container;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class CreativeContainerList {
    /** @see com.kritter.api.entity.response.msg.Message */
    private Message msg = null;
    /** @see com.kritter.api.entity.creative_container.Creative_container */
    private List<Creative_container> cclist = null;
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cclist == null) ? 0 : cclist.hashCode());
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
        CreativeContainerList other = (CreativeContainerList) obj;
        if (cclist == null) {
            if (other.cclist != null)
                return false;
        } else if (!cclist.equals(other.cclist))
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
    public List<Creative_container> getCclist() {
        return cclist;
    }
    public void setCclist(List<Creative_container> cclist) {
        this.cclist = cclist;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CreativeContainerList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        CreativeContainerList entity = objectMapper.readValue(str, CreativeContainerList.class);
        return entity;

    }
    
}
