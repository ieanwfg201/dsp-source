package com.kritter.api.entity.fraud;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.Frequency;
import com.kritter.constants.PageConstants;


public class FraudReportEntity {
    private String start_time_str = "";
    private String end_time_str = "";
    private int startindex=PageConstants.start_index;
    private int pagesize=PageConstants.page_size;

    /** mandatory - default null - null means not required, empty list means all required, integer - adid element in list signifies filters */
    private LinkedList<Integer> pubId = null; 
    private LinkedList<Integer> siteId = null;
    private List<String> advertiserId = null;
    private LinkedList<Integer> campaignId = null;
    private LinkedList<Integer> adId = null;
    private List<String> event = null;
    private LinkedList<String> terminationReason = new LinkedList<String>();
    
    private boolean count = false;
    
    private String timezone = "UTC";
    /** mandatory - default */
    private String date_format = "yyyy-MM-dd HH:00:0";
    /** default */
    private String end_date_format = "yyyy-MM-dd 23:59:59";
    /** mandatory - true if report to be split by date*/
    private boolean date_as_dimension = false;
    /** mandatory - @see com.kritter.constants.Frequency */
    private Frequency frequency = Frequency.YESTERDAY;

    private String csvDelimiter = ",";
    /** set true when Total is required in downloads */
    private boolean rollup = false;

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adId == null) ? 0 : adId.hashCode());
        result = prime * result
                + ((advertiserId == null) ? 0 : advertiserId.hashCode());
        result = prime * result
                + ((campaignId == null) ? 0 : campaignId.hashCode());
        result = prime * result + (count ? 1231 : 1237);
        result = prime * result
                + ((csvDelimiter == null) ? 0 : csvDelimiter.hashCode());
        result = prime * result + (date_as_dimension ? 1231 : 1237);
        result = prime * result
                + ((date_format == null) ? 0 : date_format.hashCode());
        result = prime * result
                + ((end_date_format == null) ? 0 : end_date_format.hashCode());
        result = prime * result
                + ((end_time_str == null) ? 0 : end_time_str.hashCode());
        result = prime * result + ((event == null) ? 0 : event.hashCode());
        result = prime * result
                + ((frequency == null) ? 0 : frequency.hashCode());
        result = prime * result + pagesize;
        result = prime * result + ((pubId == null) ? 0 : pubId.hashCode());
        result = prime * result + (rollup ? 1231 : 1237);
        result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
        result = prime * result
                + ((start_time_str == null) ? 0 : start_time_str.hashCode());
        result = prime * result + startindex;
        result = prime
                * result
                + ((terminationReason == null) ? 0 : terminationReason
                        .hashCode());
        result = prime * result
                + ((timezone == null) ? 0 : timezone.hashCode());
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
        FraudReportEntity other = (FraudReportEntity) obj;
        if (adId == null) {
            if (other.adId != null)
                return false;
        } else if (!adId.equals(other.adId))
            return false;
        if (advertiserId == null) {
            if (other.advertiserId != null)
                return false;
        } else if (!advertiserId.equals(other.advertiserId))
            return false;
        if (campaignId == null) {
            if (other.campaignId != null)
                return false;
        } else if (!campaignId.equals(other.campaignId))
            return false;
        if (count != other.count)
            return false;
        if (csvDelimiter == null) {
            if (other.csvDelimiter != null)
                return false;
        } else if (!csvDelimiter.equals(other.csvDelimiter))
            return false;
        if (date_as_dimension != other.date_as_dimension)
            return false;
        if (date_format == null) {
            if (other.date_format != null)
                return false;
        } else if (!date_format.equals(other.date_format))
            return false;
        if (end_date_format == null) {
            if (other.end_date_format != null)
                return false;
        } else if (!end_date_format.equals(other.end_date_format))
            return false;
        if (end_time_str == null) {
            if (other.end_time_str != null)
                return false;
        } else if (!end_time_str.equals(other.end_time_str))
            return false;
        if (event == null) {
            if (other.event != null)
                return false;
        } else if (!event.equals(other.event))
            return false;
        if (frequency != other.frequency)
            return false;
        if (pagesize != other.pagesize)
            return false;
        if (pubId == null) {
            if (other.pubId != null)
                return false;
        } else if (!pubId.equals(other.pubId))
            return false;
        if (rollup != other.rollup)
            return false;
        if (siteId == null) {
            if (other.siteId != null)
                return false;
        } else if (!siteId.equals(other.siteId))
            return false;
        if (start_time_str == null) {
            if (other.start_time_str != null)
                return false;
        } else if (!start_time_str.equals(other.start_time_str))
            return false;
        if (startindex != other.startindex)
            return false;
        if (terminationReason == null) {
            if (other.terminationReason != null)
                return false;
        } else if (!terminationReason.equals(other.terminationReason))
            return false;
        if (timezone == null) {
            if (other.timezone != null)
                return false;
        } else if (!timezone.equals(other.timezone))
            return false;
        return true;
    }
    public String getStart_time_str() {
        return start_time_str;
    }
    public void setStart_time_str(String start_time_str) {
        this.start_time_str = start_time_str;
    }
    public String getEnd_time_str() {
        return end_time_str;
    }
    public void setEnd_time_str(String end_time_str) {
        this.end_time_str = end_time_str;
    }
    public int getStartindex() {
        return startindex;
    }
    public void setStartindex(int startindex) {
        this.startindex = startindex;
    }
    public int getPagesize() {
        return pagesize;
    }
    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }
    public LinkedList<Integer> getSiteId() {
        return siteId;
    }
    public void setSiteId(LinkedList<Integer> siteId) {
        this.siteId = siteId;
    }
    public List<String> getAdvertiserId() {
        return advertiserId;
    }
    public void setAdvertiserId(List<String> advertiserId) {
        this.advertiserId = advertiserId;
    }
    public LinkedList<Integer> getCampaignId() {
        return campaignId;
    }
    public void setCampaignId(LinkedList<Integer> campaignId) {
        this.campaignId = campaignId;
    }
    public LinkedList<Integer> getAdId() {
        return adId;
    }
    public void setAdId(LinkedList<Integer> adId) {
        this.adId = adId;
    }
    public boolean isCount() {
        return count;
    }
    public void setCount(boolean count) {
        this.count = count;
    }
    public String getTimezone() {
        return timezone;
    }
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    public String getDate_format() {
        return date_format;
    }
    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }
    public String getEnd_date_format() {
        return end_date_format;
    }
    public void setEnd_date_format(String end_date_format) {
        this.end_date_format = end_date_format;
    }
    public boolean isDate_as_dimension() {
        return date_as_dimension;
    }
    public void setDate_as_dimension(boolean date_as_dimension) {
        this.date_as_dimension = date_as_dimension;
    }
    public Frequency getFrequency() {
        return frequency;
    }
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }
    public String getCsvDelimiter() {
        return csvDelimiter;
    }
    public void setCsvDelimiter(String csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
    }
    public boolean isRollup() {
        return rollup;
    }
    public void setRollup(boolean rollup) {
        this.rollup = rollup;
    }
    public List<String> getEvent() {
        return event;
    }
    public void setEvent(List<String> event) {
        this.event = event;
    }
    public LinkedList<String> getTerminationReason() {
        return terminationReason;
    }
    public void setTerminationReason(LinkedList<String> terminationReason) {
        this.terminationReason = terminationReason;
    }
    public LinkedList<Integer> getPubId() {
        return pubId;
    }
    public void setPubId(LinkedList<Integer> pubId) {
        this.pubId = pubId;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static FraudReportEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        FraudReportEntity entity = objectMapper.readValue(str, FraudReportEntity.class);
        return entity;

    }
}
