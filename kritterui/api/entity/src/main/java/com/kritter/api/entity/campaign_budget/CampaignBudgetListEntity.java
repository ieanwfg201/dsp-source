package com.kritter.api.entity.campaign_budget;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class CampaignBudgetListEntity {
    /** mandatory - id of campaign - com.kritter.api.entity.campaign.Campaign */
    private int campaign_id = -1 ;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + campaign_id;
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
        CampaignBudgetListEntity other = (CampaignBudgetListEntity) obj;
        if (campaign_id != other.campaign_id)
            return false;
        return true;
    }

    public int getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CampaignBudgetListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        CampaignBudgetListEntity entity = objectMapper.readValue(str, CampaignBudgetListEntity.class);
        return entity;
    }

    
}
