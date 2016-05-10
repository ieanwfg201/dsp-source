package com.kritter.api.entity.isp_mapping;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class Isp_mappingList {
    private Message msg = null;
    private List<Isp_mapping> isp_mappinglist = null;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((isp_mappinglist == null) ? 0 : isp_mappinglist.hashCode());
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
        Isp_mappingList other = (Isp_mappingList) obj;
        if (isp_mappinglist == null) {
            if (other.isp_mappinglist != null)
                return false;
        } else if (!isp_mappinglist.equals(other.isp_mappinglist))
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
    
    public List<Isp_mapping> getIsp_mappinglist() {
        return isp_mappinglist;
    }
    public void setIsp_mappinglist(List<Isp_mapping> isp_mappinglist) {
        this.isp_mappinglist = isp_mappinglist;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Isp_mappingList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Isp_mappingList entity = objectMapper.readValue(str, Isp_mappingList.class);
        return entity;

    }
}
