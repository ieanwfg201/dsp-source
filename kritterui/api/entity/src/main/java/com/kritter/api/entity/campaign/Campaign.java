package com.kritter.api.entity.campaign;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.StatusIdEnum;

public class Campaign {
    /** mandatory - id of campaign - auto created at the time of creation */
    private int id = -1;
    /** mandatory - guid of campaign - auto created at the time of creation */
    private String guid = null;
    /** mandatory */
    private String name = null;
    /** mandatory guid of advertiser account @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory id of advertiser account @see com.kritter.api.entity.account.Account */
    private int account_id = -1;
    /** mandatory status id of campaign @see  com.kritter.constants.StatusIdEnum */
    private int status_id = StatusIdEnum.Paused.getCode();
    /** mandatory start date of campaign */
    private long start_date = 0;
    /** mandatory end date of campaign */
    private long end_date = 0;
    /** optional auto populated */
    private long created_on = 0;
    /** mandatory id of account changing this entity */
    private int modified_by = -1;
    /** optional auto populated */
    private long last_modified = 0;
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime * result + account_id;
        result = prime * result + (int) (created_on ^ (created_on >>> 32));
        result = prime * result + (int) (end_date ^ (end_date >>> 32));
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + id;
        result = prime * result
                + (int) (last_modified ^ (last_modified >>> 32));
        result = prime * result + modified_by;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (start_date ^ (start_date >>> 32));
        result = prime * result + status_id;
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
        Campaign other = (Campaign) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (account_id != other.account_id)
            return false;
        if (created_on != other.created_on)
            return false;
        if (end_date != other.end_date)
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
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (start_date != other.start_date)
            return false;
        if (status_id != other.status_id)
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public int getStatus_id() {
        return status_id;
    }
    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }
    public long getStart_date() {
        return start_date;
    }
    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }
    public long getEnd_date() {
        return end_date;
    }
    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }
    public long getCreated_on() {
        return created_on;
    }
    public void setCreated_on(long created_on) {
        this.created_on = created_on;
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
    public int getAccount_id() {
        return account_id;
    }
    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static Campaign getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Campaign entity = objectMapper.readValue(str, Campaign.class);
        return entity;
    }

}
