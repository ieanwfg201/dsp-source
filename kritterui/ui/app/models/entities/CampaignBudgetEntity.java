package models.entities;

import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Required;

import com.kritter.api.entity.campaign_budget.Campaign_budget;

public class CampaignBudgetEntity {

    private int campaign_id = -1;
    private String campaign_guid = null;
    @Required
    private double internal_total_budget = 0;
    @Required
    private double adv_total_budget = 0;
    
    private double internal_total_burn = 0;
    private double adv_total_burn = 0;
    @Required
    private double internal_daily_budget = 0;
    @Required
    private double adv_daily_budget = 0;
    private double internal_daily_burn = 0;
    private double adv_daily_burn = 0;
    private int modified_by = 0;
    private long created_on = 0;
    private long last_modified = 0;

    private double add_to_total_budget = 0;
    private double add_to_daily_budget = 0;
    
     
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
    public double getAdd_to_total_budget() {
        return add_to_total_budget;
    }
    public void setAdd_to_total_budget(double add_to_total_budget) {
        this.add_to_total_budget = add_to_total_budget;
    }
    public double getAdd_to_daily_budget() {
        return add_to_daily_budget;
    }
    public void setAdd_to_daily_budget(double add_to_daily_budget) {
        this.add_to_daily_budget = add_to_daily_budget;
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
    
    public Campaign_budget getEntity(){
    	Campaign_budget campaignBudget = new Campaign_budget();
    	BeanUtils.copyProperties(this, campaignBudget);
    	return campaignBudget;
    }
    
}
