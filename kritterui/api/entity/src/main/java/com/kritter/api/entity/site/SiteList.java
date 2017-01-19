package com.kritter.api.entity.site;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.api.entity.response.msg.Message;

public class SiteList {
    /** @see com.kritter.api.entity.response.msg.Message */
    private Message msg = null;
    /** @see com.kritter.api.entity.site.Site */
    private List<Site> site_list = null;
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((msg == null) ? 0 : msg.hashCode());
        result = prime * result
                + ((site_list == null) ? 0 : site_list.hashCode());
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
        SiteList other = (SiteList) obj;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        if (site_list == null) {
            if (other.site_list != null)
                return false;
        } else if (!site_list.equals(other.site_list))
            return false;
        return true;
    }
    public Message getMsg() {
        return msg;
    }
    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public List<Site> getSite_list() {
        return site_list;
    }
    public void setSite_list(List<Site> site_list) {
        this.site_list = site_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static SiteList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        SiteList entity = objectMapper.readValue(str, SiteList.class);
        return entity;

    }

}
