package com.kritter.api.entity.ext_site;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class Ext_site_list {
    private Message msg = null;
    private List<Ext_site> ext_site_list = null;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((ext_site_list == null) ? 0 : ext_site_list.hashCode());
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
        Ext_site_list other = (Ext_site_list) obj;
        if (ext_site_list == null) {
            if (other.ext_site_list != null)
                return false;
        } else if (!ext_site_list.equals(other.ext_site_list))
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
    public List<Ext_site> getExt_site_list() {
        return ext_site_list;
    }
    public void setExt_site_list(List<Ext_site> ext_site_list) {
        this.ext_site_list = ext_site_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Ext_site_list getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Ext_site_list entity = objectMapper.readValue(str, Ext_site_list.class);
        return entity;

    }

}
