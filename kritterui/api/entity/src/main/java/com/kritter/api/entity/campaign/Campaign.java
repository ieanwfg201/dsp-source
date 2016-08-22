package com.kritter.api.entity.campaign;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.FreqDuration;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.UserConstant;

public class Campaign {
    /** mandatory - id of campaign - auto created at the time of creation */
    private int id = -1;
    /** mandatory - guid of campaign - auto created at the time of creation */
    private String guid = null;
    /** mandatory */
    private String name = null;
    /** mandatory guid of advertiser account @see com.kritter.api.entity.account.Account */
    private String account_guid = null;
    /** mandatory id of advertiser account @see com.kritter.api.entity.account.Account */
    private int account_id = -1;
    /** mandatory status id of campaign @see  com.kritter.constants.StatusIdEnum */
    private int status_id = StatusIdEnum.Paused.getCode();
    /** mandatory start date of campaign */
    private long start_date = 0;
    /** mandatory end date of campaign */
    private long end_date = 0;
    /** optional auto populated */
    private long created_on = 0;
    /** mandatory id of account changing this entity */
    private int modified_by = -1;
    /** optional auto populated */
    private long last_modified = 0;
    /** flag to signify whether user is frequency capped or not */
    private boolean is_frequency_capped = false;
    private boolean click_freq_cap = false;
    private int click_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int click_freq_cap_count = UserConstant.frequency_cap_default;
    private int click_freq_time_window = UserConstant.frequency_cap_time_window_default;
    private boolean imp_freq_cap = false;
    private int imp_freq_cap_type = FreqDuration.BYHOUR.getCode();
    private int imp_freq_cap_count = UserConstant.frequency_cap_default;
    private int imp_freq_time_window = UserConstant.frequency_cap_time_window_default;
    
    
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account_guid == null) ? 0 : account_guid.hashCode());
		result = prime * result + account_id;
		result = prime * result + (click_freq_cap ? 1231 : 1237);
		result = prime * result + click_freq_cap_count;
		result = prime * result + click_freq_cap_type;
		result = prime * result + click_freq_time_window;
		result = prime * result + (int) (created_on ^ (created_on >>> 32));
		result = prime * result + (int) (end_date ^ (end_date >>> 32));
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result + id;
		result = prime * result + (imp_freq_cap ? 1231 : 1237);
		result = prime * result + imp_freq_cap_count;
		result = prime * result + imp_freq_cap_type;
		result = prime * result + imp_freq_time_window;
		result = prime * result + (is_frequency_capped ? 1231 : 1237);
		result = prime * result + (int) (last_modified ^ (last_modified >>> 32));
		result = prime * result + modified_by;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (start_date ^ (start_date >>> 32));
		result = prime * result + status_id;
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
		Campaign other = (Campaign) obj;
		if (account_guid == null) {
			if (other.account_guid != null)
				return false;
		} else if (!account_guid.equals(other.account_guid))
			return false;
		if (account_id != other.account_id)
			return false;
		if (click_freq_cap != other.click_freq_cap)
			return false;
		if (click_freq_cap_count != other.click_freq_cap_count)
			return false;
		if (click_freq_cap_type != other.click_freq_cap_type)
			return false;
		if (click_freq_time_window != other.click_freq_time_window)
			return false;
		if (created_on != other.created_on)
			return false;
		if (end_date != other.end_date)
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (id != other.id)
			return false;
		if (imp_freq_cap != other.imp_freq_cap)
			return false;
		if (imp_freq_cap_count != other.imp_freq_cap_count)
			return false;
		if (imp_freq_cap_type != other.imp_freq_cap_type)
			return false;
		if (imp_freq_time_window != other.imp_freq_time_window)
			return false;
		if (is_frequency_capped != other.is_frequency_capped)
			return false;
		if (last_modified != other.last_modified)
			return false;
		if (modified_by != other.modified_by)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (start_date != other.start_date)
			return false;
		if (status_id != other.status_id)
			return false;
		return true;
	}
    public boolean isIs_frequency_capped() {
		return is_frequency_capped;
	}
	public void setIs_frequency_capped(boolean is_frequency_capped) {
		this.is_frequency_capped = is_frequency_capped;
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
    public int getAccount_id() {
        return account_id;
    }
    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static Campaign getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Campaign entity = objectMapper.readValue(str, Campaign.class);
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return entity;
    }

}
