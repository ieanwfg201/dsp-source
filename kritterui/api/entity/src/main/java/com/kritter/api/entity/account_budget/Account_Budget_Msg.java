package com.kritter.api.entity.account_budget;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class Account_Budget_Msg {
    private Account_budget account_budget = null;
    private Message msg = null;
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_budget == null) ? 0 : account_budget.hashCode());
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
        Account_Budget_Msg other = (Account_Budget_Msg) obj;
        if (account_budget == null) {
            if (other.account_budget != null)
                return false;
        } else if (!account_budget.equals(other.account_budget))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }
    public Account_budget getAccount_budget() {
        return account_budget;
    }
    public void setAccount_budget(Account_budget account_budget) {
        this.account_budget = account_budget;
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
