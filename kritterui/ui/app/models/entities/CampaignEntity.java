package models.entities;

import org.springframework.beans.BeanUtils;

import com.kritter.api.entity.campaign.Campaign;

import play.data.validation.Constraints.Required;

public class CampaignEntity {
    private int id = -1;
    private String guid = null;
    @Required
    private String name = null;
    private String account_guid = null;
    private int status_id = 5;
    @Required
    private long start_date = 0;
    @Required
    private long end_date = 0;
    private long created_on = 0;
    private int modified_by = -1;
    private long last_modified = 0;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public int getStatus_id() {
        return status_id;
    }
    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }
    public long getStart_date() {
        return start_date;
    }
    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }
    public long getEnd_date() {
        return end_date;
    }
    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }
    public long getCreated_on() {
        return created_on;
    }
    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }
    public int getModified_by() {
        return modified_by;
    }
    public void setModified_by(int modified_by) {
        this.modified_by = modified_by;
    }
    public long getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }
    
    public Campaign getEntity(){
    	Campaign campaign = new Campaign();
    	BeanUtils.copyProperties(this, campaign);
    	return campaign;
    }
    
}
