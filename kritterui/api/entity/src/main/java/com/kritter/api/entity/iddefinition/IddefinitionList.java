package com.kritter.api.entity.iddefinition;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class IddefinitionList {
    private List<Iddefinition> iddefinition_list = null;
    private Message msg = null;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((iddefinition_list == null) ? 0 : iddefinition_list
                        .hashCode());
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
        IddefinitionList other = (IddefinitionList) obj;
        if (iddefinition_list == null) {
            if (other.iddefinition_list != null)
                return false;
        } else if (!iddefinition_list.equals(other.iddefinition_list))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }
    public List<Iddefinition> getIddefinition_list() {
        return iddefinition_list;
    }
    public void setIddefinition_list(List<Iddefinition> iddefinition_list) {
        this.iddefinition_list = iddefinition_list;
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

    public static IddefinitionList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        IddefinitionList entity = objectMapper.readValue(str, IddefinitionList.class);
        return entity;

    }
}
