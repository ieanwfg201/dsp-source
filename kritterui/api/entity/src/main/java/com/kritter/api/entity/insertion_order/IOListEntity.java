package com.kritter.api.entity.insertion_order;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.IOStatus;
import com.kritter.constants.PageConstants;

public class IOListEntity {
    /** mandatory index number of page */
    private int page_no = PageConstants.start_index;
    /** mandatory size of page */
    private int page_size = PageConstants.page_size;
    /** mandatory io status  @see  com.kritter.constants.IoStatus */
    private IOStatus status = IOStatus.NEW;
    /** mandatory guid of account @see com.kritter.api.entity.account.Account  */
    private String account_guid = null;
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime * result + page_no;
        result = prime * result + page_size;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        IOListEntity other = (IOListEntity) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (page_no != other.page_no)
            return false;
        if (page_size != other.page_size)
            return false;
        if (status != other.status)
            return false;
        return true;
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
    public IOStatus getStatus() {
        return status;
    }
    public void setStatus(IOStatus status) {
        this.status = status;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static IOListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        IOListEntity entity = objectMapper.readValue(str, IOListEntity.class);
        return entity;
    }
}
