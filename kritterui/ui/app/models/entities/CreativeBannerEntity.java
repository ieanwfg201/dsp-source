package models.entities;

import org.springframework.beans.BeanUtils;

import play.data.validation.Constraints.Required;

import com.kritter.api.entity.creative_banner.Creative_banner;

public class CreativeBannerEntity {
    private int id = -1;
    private String guid = null; 
    @Required
    private String account_guid = null;
    @Required
    private int slot_id = -1;
    @Required
    private String resource_uri = null;
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
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public int getSlot_id() {
        return slot_id;
    }
    public void setSlot_id(int slot_id) {
        this.slot_id = slot_id;
    }
    public String getResource_uri() {
        return resource_uri;
    }
    public void setResource_uri(String resource_uri) {
        this.resource_uri = resource_uri;
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
    
    public Creative_banner getEntity(){
    	Creative_banner cb = new Creative_banner();
    	BeanUtils.copyProperties(this, cb);
    	return cb;
    }
}
