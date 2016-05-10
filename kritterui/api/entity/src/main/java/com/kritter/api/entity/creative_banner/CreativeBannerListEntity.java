package com.kritter.api.entity.creative_banner;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.CreativeBannerAPIEnum;

public class CreativeBannerListEntity {
    /** optional - depends on cbenum - id of creative banner @see com.kritter.api.entity.creative_banner.Creative_banner */
    private int id = -1;
    /** optional - depends on cbenum-  guid of account - @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory - @see com.kritter.constants.CreativeBannerAPIEnum */
    private CreativeBannerAPIEnum cbenum = CreativeBannerAPIEnum.list_creative_banner_by_account;
    /** optional - depends on cbenum-   comma separated creative banner ids etc*/
    private String guid_list = null;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime * result + ((cbenum == null) ? 0 : cbenum.hashCode());
        result = prime * result
                + ((guid_list == null) ? 0 : guid_list.hashCode());
        result = prime * result + id;
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
        CreativeBannerListEntity other = (CreativeBannerListEntity) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (cbenum != other.cbenum)
            return false;
        if (guid_list == null) {
            if (other.guid_list != null)
                return false;
        } else if (!guid_list.equals(other.guid_list))
            return false;
        if (id != other.id)
            return false;
        return true;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public CreativeBannerAPIEnum getCbenum() {
        return cbenum;
    }
    public void setCbenum(CreativeBannerAPIEnum cbenum) {
        this.cbenum = cbenum;
    }
 
    public String getGuid_list() {
        return guid_list;
    }
    public void setGuid_list(String guid_list) {
        this.guid_list = guid_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CreativeBannerListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        CreativeBannerListEntity entity = objectMapper.readValue(str, CreativeBannerListEntity.class);
        return entity;

    }

}
