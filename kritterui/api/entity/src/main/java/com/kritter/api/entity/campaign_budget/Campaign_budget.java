package com.kritter.api.entity.campaign_budget;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Campaign_budget {
    
    /** mandatory - id of campaign - @see com.kritter.api.entity.campaign.Campaign */
    private int campaign_id = -1;
    /** mandatory - guid of campaign - @see com.kritter.api.entity.campaign.Campaign */
    private String campaign_guid = null;
    /** mandatory - should be set equal to adv_total_budget by at the time of creation if not manually specified  */
    private double internal_total_budget = 0;
    /** mandatory */
    private double adv_total_budget = 0;
    /** automatic - mandatory - 0 by default */
    private double internal_total_burn = 0;
    /** automatic - mandatory - 0 by default */
    private double adv_total_burn = 0;
    /** mandatory - should be set equal to adv_daily_budget by at the time of creation if not manually specified  */
    private double internal_daily_budget = 0;
    /** mandatory */
    private double adv_daily_budget = 0;
    /** automatic - mandatory - 0 by default */
    private double internal_daily_burn = 0;
    /** automatic - mandatory - 0 by default */
    private double adv_daily_burn = 0;
    /** id of account modifying the entity @see com.kritter.api.entity.account.Account */
    private int modified_by = 0;
    /** automatic */
    private long created_on = 0;
    /** automatic */
    private long last_modified = 0;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(adv_daily_budget);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(adv_daily_burn);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(adv_total_budget);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(adv_total_burn);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + ((campaign_guid == null) ? 0 : campaign_guid.hashCode());
        result = prime * result + campaign_id;
        result = prime * result + (int) (created_on ^ (created_on >>> 32));
        temp = Double.doubleToLongBits(internal_daily_budget);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(internal_daily_burn);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(internal_total_budget);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(internal_total_burn);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + (int) (last_modified ^ (last_modified >>> 32));
        result = prime * result + modified_by;
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
        Campaign_budget other = (Campaign_budget) obj;
        if (Double.doubleToLongBits(adv_daily_budget) != Double
                .doubleToLongBits(other.adv_daily_budget))
            return false;
        if (Double.doubleToLongBits(adv_daily_burn) != Double
                .doubleToLongBits(other.adv_daily_burn))
            return false;
        if (Double.doubleToLongBits(adv_total_budget) != Double
                .doubleToLongBits(other.adv_total_budget))
            return false;
        if (Double.doubleToLongBits(adv_total_burn) != Double
                .doubleToLongBits(other.adv_total_burn))
            return false;
        if (campaign_guid == null) {
            if (other.campaign_guid != null)
                return false;
        } else if (!campaign_guid.equals(other.campaign_guid))
            return false;
        if (campaign_id != other.campaign_id)
            return false;
        if (created_on != other.created_on)
            return false;
        if (Double.doubleToLongBits(internal_daily_budget) != Double
                .doubleToLongBits(other.internal_daily_budget))
            return false;
        if (Double.doubleToLongBits(internal_daily_burn) != Double
                .doubleToLongBits(other.internal_daily_burn))
            return false;
        if (Double.doubleToLongBits(internal_total_budget) != Double
                .doubleToLongBits(other.internal_total_budget))
            return false;
        if (Double.doubleToLongBits(internal_total_burn) != Double
                .doubleToLongBits(other.internal_total_burn))
            return false;
        if (last_modified != other.last_modified)
            return false;
        if (modified_by != other.modified_by)
            return false;
        return true;
    }
    public int getCampaign_id() {
        return campaign_id;
    }
    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }
    public String getCampaign_guid() {
        return campaign_guid;
    }
    public void setCampaign_guid(String campaign_guid) {
        this.campaign_guid = campaign_guid;
    }
    public double getInternal_total_budget() {
        return internal_total_budget;
    }
    public void setInternal_total_budget(double internal_total_budget) {
        this.internal_total_budget = internal_total_budget;
    }
    public double getAdv_total_budget() {
        return adv_total_budget;
    }
    public void setAdv_total_budget(double adv_total_budget) {
        this.adv_total_budget = adv_total_budget;
    }
    public double getInternal_total_burn() {
        return internal_total_burn;
    }
    public void setInternal_total_burn(double internal_total_burn) {
        this.internal_total_burn = internal_total_burn;
    }
    public double getAdv_total_burn() {
        return adv_total_burn;
    }
    public void setAdv_total_burn(double adv_total_burn) {
        this.adv_total_burn = adv_total_burn;
    }
    public double getInternal_daily_budget() {
        return internal_daily_budget;
    }
    public void setInternal_daily_budget(double internal_daily_budget) {
        this.internal_daily_budget = internal_daily_budget;
    }
    public double getAdv_daily_budget() {
        return adv_daily_budget;
    }
    public void setAdv_daily_budget(double adv_daily_budget) {
        this.adv_daily_budget = adv_daily_budget;
    }
    public double getInternal_daily_burn() {
        return internal_daily_burn;
    }
    public void setInternal_daily_burn(double internal_daily_burn) {
        this.internal_daily_burn = internal_daily_burn;
    }
    public double getAdv_daily_burn() {
        return adv_daily_burn;
    }
    public void setAdv_daily_burn(double adv_daily_burn) {
        this.adv_daily_burn = adv_daily_burn;
    }
    public int getModified_by() {
        return modified_by;
    }
    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }
    public long getCreated_on() {
        return created_on;
    }
    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }
    public long getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Campaign_budget getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Campaign_budget entity = objectMapper.readValue(str, Campaign_budget.class);
        return entity;
    }

}
