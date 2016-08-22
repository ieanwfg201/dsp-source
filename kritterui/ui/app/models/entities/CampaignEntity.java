package models.entities;

import org.springframework.beans.BeanUtils;

import com.kritter.api.entity.campaign.Campaign;
import com.kritter.constants.FreqDuration;
import com.kritter.constants.UserConstant;

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
    private boolean is_frequency_capped = false;
    private boolean click_freq_cap = false;
    private int click_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int click_freq_cap_count = UserConstant.frequency_cap_default;
    private int click_freq_time_window = UserConstant.frequency_cap_time_window_default;
    private boolean imp_freq_cap = false;
    private int imp_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int imp_freq_cap_count = UserConstant.frequency_cap_default;
    private int imp_freq_time_window = UserConstant.frequency_cap_time_window_default;

    
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
    public boolean isIs_frequency_capped() {
        return is_frequency_capped;
    }
    public boolean isClick_freq_cap() {
		return click_freq_cap;
	}
	public void setClick_freq_cap(boolean click_freq_cap) {
		this.click_freq_cap = click_freq_cap;
	}
	public int getClick_freq_cap_type() {
		return click_freq_cap_type;
	}
	public void setClick_freq_cap_type(int click_freq_cap_type) {
		this.click_freq_cap_type = click_freq_cap_type;
	}
	public int getClick_freq_cap_count() {
		return click_freq_cap_count;
	}
	public void setClick_freq_cap_count(int click_freq_cap_count) {
		this.click_freq_cap_count = click_freq_cap_count;
	}
	public int getClick_freq_time_window() {
		return click_freq_time_window;
	}
	public void setClick_freq_time_window(int click_freq_time_window) {
		this.click_freq_time_window = click_freq_time_window;
	}
	public boolean isImp_freq_cap() {
		return imp_freq_cap;
	}
	public void setImp_freq_cap(boolean imp_freq_cap) {
		this.imp_freq_cap = imp_freq_cap;
	}
	public int getImp_freq_cap_type() {
		return imp_freq_cap_type;
	}
	public void setImp_freq_cap_type(int imp_freq_cap_type) {
		this.imp_freq_cap_type = imp_freq_cap_type;
	}
	public int getImp_freq_cap_count() {
		return imp_freq_cap_count;
	}
	public void setImp_freq_cap_count(int imp_freq_cap_count) {
		this.imp_freq_cap_count = imp_freq_cap_count;
	}
	public int getImp_freq_time_window() {
		return imp_freq_time_window;
	}
	public void setImp_freq_time_window(int imp_freq_time_window) {
		this.imp_freq_time_window = imp_freq_time_window;
	}

    public void setIs_frequency_capped(boolean is_frequency_capped) {
        this.is_frequency_capped = is_frequency_capped;
    }
    
    public Campaign getEntity(){
    	Campaign campaign = new Campaign();
    	BeanUtils.copyProperties(this, campaign);
    	return campaign;
    }
    
}
