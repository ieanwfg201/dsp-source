package com.kritter.api.entity.creative_container;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.CreativeContainerAPIEnum;
import com.kritter.constants.PageConstants;
import com.kritter.constants.StatusIdEnum;

public class CreativeContainerListEntity {
    /** optional - depends on ccenum - id of creative container @see com.kritter.api.entity.creative_container.Creative_container */
    private int id = -1;
    /** optional - depends on ccenum - guid of account @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory - @see com.kritter.constants.CreativeContainerAPIEnum */
    private CreativeContainerAPIEnum ccenum = CreativeContainerAPIEnum.list_creative_container_by_account;
    /** mandatory - @see com.kritter.constants.PageConstants */
    private int page_no = PageConstants.start_index;
    /** mandatory - @see com.kritter.constants.PageConstants */
    private int page_size = PageConstants.page_size;
    /** mandatory - @see com.kritter.constants.StatusIdEnum */
    private StatusIdEnum status_id = StatusIdEnum.Pending;
    /** optional - depends on ccenum - json array of creative container ids etc */
    private String id_list = "";
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime * result + ((ccenum == null) ? 0 : ccenum.hashCode());
        result = prime * result + id;
        result = prime * result + ((id_list == null) ? 0 : id_list.hashCode());
        result = prime * result + page_no;
        result = prime * result + page_size;
        result = prime * result
                + ((status_id == null) ? 0 : status_id.hashCode());
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
        CreativeContainerListEntity other = (CreativeContainerListEntity) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (ccenum != other.ccenum)
            return false;
        if (id != other.id)
            return false;
        if (id_list == null) {
            if (other.id_list != null)
                return false;
        } else if (!id_list.equals(other.id_list))
            return false;
        if (page_no != other.page_no)
            return false;
        if (page_size != other.page_size)
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
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public CreativeContainerAPIEnum getCcenum() {
        return ccenum;
    }
    public void setCcenum(CreativeContainerAPIEnum ccenum) {
        this.ccenum = ccenum;
    }
    public int getPage_no() {
        return page_no;
    }
    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }
    public int getPage_size() {
        return page_size;
    }
    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }
    public StatusIdEnum getStatus_id() {
        return status_id;
    }
    public void setStatus_id(StatusIdEnum status_id) {
        this.status_id = status_id;
    }
    public String getId_list() {
        return id_list;
    }
    public void setId_list(String id_list) {
        this.id_list = id_list;
    }
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CreativeContainerListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        CreativeContainerListEntity entity = objectMapper.readValue(str, CreativeContainerListEntity.class);
        return entity;

    }
    
}
