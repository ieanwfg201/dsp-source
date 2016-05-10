package com.kritter.api.entity.metadata;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class MetaList {
    private String listName = null;
    private List<MetaField> metaFieldList = null;
    private Message msg = null;
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((listName == null) ? 0 : listName.hashCode());
        result = prime * result
                + ((metaFieldList == null) ? 0 : metaFieldList.hashCode());
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
        MetaList other = (MetaList) obj;
        if (listName == null) {
            if (other.listName != null)
                return false;
        } else if (!listName.equals(other.listName))
            return false;
        if (metaFieldList == null) {
            if (other.metaFieldList != null)
                return false;
        } else if (!metaFieldList.equals(other.metaFieldList))
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
    public String getListName() {
        return listName;
    }
    public void setListName(String listName) {
        this.listName = listName;
    }
    public List<MetaField> getMetaFieldList() {
        return metaFieldList;
    }
    public void setMetaFieldList(List<MetaField> metaFieldList) {
        this.metaFieldList = metaFieldList;
    }
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    
}
