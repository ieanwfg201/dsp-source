package com.kritter.api.entity.targeting_profile;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.TargetingProfileAPIEnum;

public class TargetingProfileListEntity {
    /** optional - depends on tpEnum - id of Targeting_profile @see com.kritter.api.entity.targeting_profile.Targeting_profile */
    private Integer id = null;
    /** optional - depends on tpEnum - guid of Targeting_profile @see com.kritter.api.entity.targeting_profile.Targeting_profile */
    private String guid = null;
    /** optional - depends on tpEnum - guid of Account @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory - @see  com.kritter.constants.TargetingProfileAPIEnum */
    private TargetingProfileAPIEnum tpEnum = TargetingProfileAPIEnum.list_active_targeting_profile_by_account;
    
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account_guid == null) ? 0 : account_guid.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((tpEnum == null) ? 0 : tpEnum.hashCode());
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
		TargetingProfileListEntity other = (TargetingProfileListEntity) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (tpEnum != other.tpEnum)
			return false;
		return true;
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
    public TargetingProfileAPIEnum getTpEnum() {
        return tpEnum;
    }
    public void setTpEnum(TargetingProfileAPIEnum tpEnum) {
        this.tpEnum = tpEnum;
    }
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static TargetingProfileListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        TargetingProfileListEntity entity = objectMapper.readValue(str, TargetingProfileListEntity.class);
        return entity;

    }
    
}
