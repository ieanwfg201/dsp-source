package com.kritter.api.entity.account;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class AccountList {
    private List<Account> account_list = null;
    private Message msg = null;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_list == null) ? 0 : account_list.hashCode());
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
        AccountList other = (AccountList) obj;
        if (account_list == null) {
            if (other.account_list != null)
                return false;
        } else if (!account_list.equals(other.account_list))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }

    public List<Account> getAccount_list() {
        return account_list;
    }

    public void setAccount_list(List<Account> account_list) {
        this.account_list = account_list;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    
}
