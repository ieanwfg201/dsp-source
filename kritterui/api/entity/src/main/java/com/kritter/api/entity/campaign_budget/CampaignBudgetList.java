package com.kritter.api.entity.campaign_budget;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.response.msg.Message;

public class CampaignBudgetList {
    /** @see com.kritter.api.entity.response.msg.Message */
    private Message msg = null;
    /** @see com.kritter.api.entity.campaign_budget.Campaign_budget */
    List<Campaign_budget> campaig_budget_list = null;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((campaig_budget_list == null) ? 0 : campaig_budget_list
                        .hashCode());
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
        CampaignBudgetList other = (CampaignBudgetList) obj;
        if (campaig_budget_list == null) {
            if (other.campaig_budget_list != null)
                return false;
        } else if (!campaig_budget_list.equals(other.campaig_budget_list))
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
    public List<Campaign_budget> getCampaig_budget_list() {
        return campaig_budget_list;
    }
    public void setCampaig_budget_list(List<Campaign_budget> campaig_budget_list) {
        this.campaig_budget_list = campaig_budget_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CampaignBudgetList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        CampaignBudgetList entity = objectMapper.readValue(str, CampaignBudgetList.class);
        return entity;

    }
}
