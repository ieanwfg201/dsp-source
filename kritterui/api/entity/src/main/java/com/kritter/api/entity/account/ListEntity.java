package com.kritter.api.entity.account;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.AccountAPIEnum;
import com.kritter.constants.Account_Type;
import com.kritter.constants.DemandType;
import com.kritter.constants.PageConstants;
import com.kritter.constants.StatusIdEnum;

public class ListEntity {
    private int page_no = PageConstants.start_index;
    private int page_size = PageConstants.page_size;
    private String email ="";
	private Account_Type account_type = null;
    private StatusIdEnum status = StatusIdEnum.Pending; 
    private DemandType demandType = DemandType.DIRECT;
    private AccountAPIEnum accountAPIEnum = AccountAPIEnum.list_active_advertiser_by_demandtype;
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((accountAPIEnum == null) ? 0 : accountAPIEnum.hashCode());
        result = prime * result
                + ((account_type == null) ? 0 : account_type.hashCode());
        result = prime * result
                + ((demandType == null) ? 0 : demandType.hashCode());
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
        ListEntity other = (ListEntity) obj;
        if (accountAPIEnum != other.accountAPIEnum)
            return false;
        if (account_type != other.account_type)
            return false;
        if (demandType != other.demandType)
            return false;
        if (page_no != other.page_no)
            return false;
        if (page_size != other.page_size)
            return false;
        if (status != other.status)
            return false;
        return true;
    }

    public int getPage_size() {
        return page_size;
    }
    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }
    public Account_Type getAccount_type() {
        return account_type;
    }
    public void setAccount_type(Account_Type account_type) {
        this.account_type = account_type;
    }
    public int getPage_no() {
        return page_no;
    }
    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }
    public StatusIdEnum getStatus() {
        return status;
    }
    public void setStatus(StatusIdEnum status) {
        this.status = status;
    }
    public DemandType getDemandType() {
        return demandType;
    }
    public void setDemandType(DemandType demandType) {
        this.demandType = demandType;
    }
    public AccountAPIEnum getAccountAPIEnum() {
        return accountAPIEnum;
    }
    public void setAccountAPIEnum(AccountAPIEnum accountAPIEnum) {
        this.accountAPIEnum = accountAPIEnum;
    }
    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	  public JsonNode toJson(){
	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.valueToTree(this);
	        return jsonNode;
	    }

	    public static ListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
	        ObjectMapper objectMapper = new ObjectMapper();
	        ListEntity entity = objectMapper.readValue(str, ListEntity.class);
	        return entity;
	    }
}
