package com.kritter.api.entity.creative_banner;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class Creative_banner {
    /** mandatory - auto generated at creation */
    private int id = -1;
    /** mandatory - auto generated at creation */
    private String guid = null; 
    /** mandatory - guid of account - @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory - id of Creative slot*/
    private int slot_id = -1;
    /** mandatory - path to banner image*/
    private String resource_uri = null;
    /** mandatory - id of Account modifying this entity*/
    private int modified_by = -1;
    /** auto populated */
    private long last_modified = 0;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + id;
        result = prime * result
                + (int) (last_modified ^ (last_modified >>> 32));
        result = prime * result + modified_by;
        result = prime * result
                + ((resource_uri == null) ? 0 : resource_uri.hashCode());
        result = prime * result + slot_id;
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
        Creative_banner other = (Creative_banner) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (guid == null) {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        if (id != other.id)
            return false;
        if (last_modified != other.last_modified)
            return false;
        if (modified_by != other.modified_by)
            return false;
        if (resource_uri == null) {
            if (other.resource_uri != null)
                return false;
        } else if (!resource_uri.equals(other.resource_uri))
            return false;
        if (slot_id != other.slot_id)
            return false;
        return true;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public int getSlot_id() {
        return slot_id;
    }
    public void setSlot_id(int slot_id) {
        this.slot_id = slot_id;
    }
    public String getResource_uri() {
        return resource_uri;
    }
    public void setResource_uri(String resource_uri) {
        this.resource_uri = resource_uri;
    }
    public int getModified_by() {
        return modified_by;
    }
    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }
    public long getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    } 
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Creative_banner getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Creative_banner entity = objectMapper.readValue(str, Creative_banner.class);
        return entity;

    }

}
