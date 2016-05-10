package com.kritter.api.entity.extsitereport;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.Frequency;
import com.kritter.constants.PageConstants;


public class ExtSiteReportEntity {
    /*
     *   `processing_time` datetime DEFAULT NULL,
  `impression_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

     */
    private String start_time_str = "";
    private String end_time_str = "";
    private int startindex=PageConstants.start_index;
    private int pagesize=PageConstants.page_size;

    /** mandatory - default null - null means not required, empty list means all required, integer - adid element in list signifies filters */
    private LinkedList<Integer> exchangeId = null;
    private LinkedList<Integer> siteId = null;
    private LinkedList<Integer> ext_site = null;
    private LinkedList<Integer> adId = null;
    private LinkedList<Integer> campaignId = null;
    private LinkedList<Integer> countryId = null;
    private List<String> advertiserId = null;
    private boolean total_request = false;
    private boolean total_impression = false;
    private boolean total_bidValue = false;
    private boolean total_click = false;
    private boolean total_win = false;
    private boolean total_win_bidValue = false;
    private boolean total_csc = false;
    private boolean demandCharges = false;
    private boolean supplyCost = false;
    private boolean earning = false;
    private boolean conversion = false;
    private boolean bidprice_to_exchange = false;
    private boolean cpa_goal = false;
    private boolean exchangepayout = false;
    private boolean exchangerevenue = false;
    private boolean networkpayout = false;
    private boolean networkrevenue = false;
    private boolean billedclicks = false;
    private boolean billedcsc = false;
    
    private String timezone = "UTC";
    /** mandatory - default */
    private String date_format = "yyyy-MM-dd HH:00:0";
    /** default */
    private String end_date_format = "yyyy-MM-dd 23:59:59";
    /** mandatory - true if report to be split by date*/
    private boolean date_as_dimension = false;
    /** mandatory - @see com.kritter.constants.Frequency */
    private Frequency frequency = Frequency.YESTERDAY;

    /** true of rounding required */
    private boolean roundoffmetric = true;
    /** Default round off precision */
    private int roundoffmetriclength = 3;
    /** delimiter to use for repport download */
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
        result = prime * result + (bidprice_to_exchange ? 1231 : 1237);
        result = prime * result + (billedclicks ? 1231 : 1237);
        result = prime * result + (billedcsc ? 1231 : 1237);
        result = prime * result
                + ((campaignId == null) ? 0 : campaignId.hashCode());
        result = prime * result + (conversion ? 1231 : 1237);
        result = prime * result
                + ((countryId == null) ? 0 : countryId.hashCode());
        result = prime * result + (cpa_goal ? 1231 : 1237);
        result = prime * result
                + ((csvDelimiter == null) ? 0 : csvDelimiter.hashCode());
        result = prime * result + (date_as_dimension ? 1231 : 1237);
        result = prime * result
                + ((date_format == null) ? 0 : date_format.hashCode());
        result = prime * result + (demandCharges ? 1231 : 1237);
        result = prime * result + (earning ? 1231 : 1237);
        result = prime * result
                + ((end_date_format == null) ? 0 : end_date_format.hashCode());
        result = prime * result
                + ((end_time_str == null) ? 0 : end_time_str.hashCode());
        result = prime * result
                + ((exchangeId == null) ? 0 : exchangeId.hashCode());
        result = prime * result + (exchangepayout ? 1231 : 1237);
        result = prime * result + (exchangerevenue ? 1231 : 1237);
        result = prime * result
                + ((ext_site == null) ? 0 : ext_site.hashCode());
        result = prime * result
                + ((frequency == null) ? 0 : frequency.hashCode());
        result = prime * result + (networkpayout ? 1231 : 1237);
        result = prime * result + (networkrevenue ? 1231 : 1237);
        result = prime * result + pagesize;
        result = prime * result + (rollup ? 1231 : 1237);
        result = prime * result + (roundoffmetric ? 1231 : 1237);
        result = prime * result + roundoffmetriclength;
        result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
        result = prime * result
                + ((start_time_str == null) ? 0 : start_time_str.hashCode());
        result = prime * result + startindex;
        result = prime * result + (supplyCost ? 1231 : 1237);
        result = prime * result
                + ((timezone == null) ? 0 : timezone.hashCode());
        result = prime * result + (total_bidValue ? 1231 : 1237);
        result = prime * result + (total_click ? 1231 : 1237);
        result = prime * result + (total_csc ? 1231 : 1237);
        result = prime * result + (total_impression ? 1231 : 1237);
        result = prime * result + (total_request ? 1231 : 1237);
        result = prime * result + (total_win ? 1231 : 1237);
        result = prime * result + (total_win_bidValue ? 1231 : 1237);
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
        ExtSiteReportEntity other = (ExtSiteReportEntity) obj;
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
        if (bidprice_to_exchange != other.bidprice_to_exchange)
            return false;
        if (billedclicks != other.billedclicks)
            return false;
        if (billedcsc != other.billedcsc)
            return false;
        if (campaignId == null) {
            if (other.campaignId != null)
                return false;
        } else if (!campaignId.equals(other.campaignId))
            return false;
        if (conversion != other.conversion)
            return false;
        if (countryId == null) {
            if (other.countryId != null)
                return false;
        } else if (!countryId.equals(other.countryId))
            return false;
        if (cpa_goal != other.cpa_goal)
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
        if (demandCharges != other.demandCharges)
            return false;
        if (earning != other.earning)
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
        if (exchangeId == null) {
            if (other.exchangeId != null)
                return false;
        } else if (!exchangeId.equals(other.exchangeId))
            return false;
        if (exchangepayout != other.exchangepayout)
            return false;
        if (exchangerevenue != other.exchangerevenue)
            return false;
        if (ext_site == null) {
            if (other.ext_site != null)
                return false;
        } else if (!ext_site.equals(other.ext_site))
            return false;
        if (frequency != other.frequency)
            return false;
        if (networkpayout != other.networkpayout)
            return false;
        if (networkrevenue != other.networkrevenue)
            return false;
        if (pagesize != other.pagesize)
            return false;
        if (rollup != other.rollup)
            return false;
        if (roundoffmetric != other.roundoffmetric)
            return false;
        if (roundoffmetriclength != other.roundoffmetriclength)
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
        if (supplyCost != other.supplyCost)
            return false;
        if (timezone == null) {
            if (other.timezone != null)
                return false;
        } else if (!timezone.equals(other.timezone))
            return false;
        if (total_bidValue != other.total_bidValue)
            return false;
        if (total_click != other.total_click)
            return false;
        if (total_csc != other.total_csc)
            return false;
        if (total_impression != other.total_impression)
            return false;
        if (total_request != other.total_request)
            return false;
        if (total_win != other.total_win)
            return false;
        if (total_win_bidValue != other.total_win_bidValue)
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
    public LinkedList<Integer> getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(LinkedList<Integer> exchangeId) {
        this.exchangeId = exchangeId;
    }
    public LinkedList<Integer> getSiteId() {
        return siteId;
    }
    public void setSiteId(LinkedList<Integer> siteId) {
        this.siteId = siteId;
    }
    public LinkedList<Integer> getExt_site() {
        return ext_site;
    }
    public void setExt_site(LinkedList<Integer> ext_site) {
        this.ext_site = ext_site;
    }
    public LinkedList<Integer> getAdId() {
        return adId;
    }
    public void setAdId(LinkedList<Integer> adId) {
        this.adId = adId;
    }
    public LinkedList<Integer> getCampaignId() {
        return campaignId;
    }
    public void setCampaignId(LinkedList<Integer> campaignId) {
        this.campaignId = campaignId;
    }
    public boolean isTotal_request() {
        return total_request;
    }
    public void setTotal_request(boolean total_request) {
        this.total_request = total_request;
    }
    public boolean isTotal_impression() {
        return total_impression;
    }
    public void setTotal_impression(boolean total_impression) {
        this.total_impression = total_impression;
    }
    public boolean isTotal_bidValue() {
        return total_bidValue;
    }
    public void setTotal_bidValue(boolean total_bidValue) {
        this.total_bidValue = total_bidValue;
    }
    public boolean isTotal_click() {
        return total_click;
    }
    public void setTotal_click(boolean total_click) {
        this.total_click = total_click;
    }
    public boolean isTotal_win() {
        return total_win;
    }
    public void setTotal_win(boolean total_win) {
        this.total_win = total_win;
    }
    public boolean isTotal_win_bidValue() {
        return total_win_bidValue;
    }
    public void setTotal_win_bidValue(boolean total_win_bidValue) {
        this.total_win_bidValue = total_win_bidValue;
    }
    public boolean isTotal_csc() {
        return total_csc;
    }
    public void setTotal_csc(boolean total_csc) {
        this.total_csc = total_csc;
    }
    public boolean isDemandCharges() {
        return demandCharges;
    }
    public void setDemandCharges(boolean demandCharges) {
        this.demandCharges = demandCharges;
    }
    public boolean isSupplyCost() {
        return supplyCost;
    }
    public void setSupplyCost(boolean supplyCost) {
        this.supplyCost = supplyCost;
    }
    public boolean isEarning() {
        return earning;
    }
    public void setEarning(boolean earning) {
        this.earning = earning;
    }
    public boolean isConversion() {
        return conversion;
    }
    public void setConversion(boolean conversion) {
        this.conversion = conversion;
    }
    public boolean isBidprice_to_exchange() {
        return bidprice_to_exchange;
    }
    public void setBidprice_to_exchange(boolean bidprice_to_exchange) {
        this.bidprice_to_exchange = bidprice_to_exchange;
    }
    public boolean isCpa_goal() {
        return cpa_goal;
    }
    public void setCpa_goal(boolean cpa_goal) {
        this.cpa_goal = cpa_goal;
    }
    public boolean isExchangepayout() {
        return exchangepayout;
    }
    public void setExchangepayout(boolean exchangepayout) {
        this.exchangepayout = exchangepayout;
    }
    public boolean isExchangerevenue() {
        return exchangerevenue;
    }
    public void setExchangerevenue(boolean exchangerevenue) {
        this.exchangerevenue = exchangerevenue;
    }
    public boolean isNetworkpayout() {
        return networkpayout;
    }
    public void setNetworkpayout(boolean networkpayout) {
        this.networkpayout = networkpayout;
    }
    public boolean isNetworkrevenue() {
        return networkrevenue;
    }
    public void setNetworkrevenue(boolean networkrevenue) {
        this.networkrevenue = networkrevenue;
    }
    public boolean isBilledclicks() {
        return billedclicks;
    }
    public void setBilledclicks(boolean billedclicks) {
        this.billedclicks = billedclicks;
    }
    public boolean isBilledcsc() {
        return billedcsc;
    }
    public void setBilledcsc(boolean billedcsc) {
        this.billedcsc = billedcsc;
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
    public boolean isRoundoffmetric() {
        return roundoffmetric;
    }
    public void setRoundoffmetric(boolean roundoffmetric) {
        this.roundoffmetric = roundoffmetric;
    }
    public int getRoundoffmetriclength() {
        return roundoffmetriclength;
    }
    public void setRoundoffmetriclength(int roundoffmetriclength) {
        this.roundoffmetriclength = roundoffmetriclength;
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
    public List<String> getAdvertiserId() {
        return advertiserId;
    }
    public void setAdvertiserId(List<String> advertiserId) {
        this.advertiserId = advertiserId;
    }
    public LinkedList<Integer> getCountryId() {
        return countryId;
    }
    public void setCountryId(LinkedList<Integer> countryId) {
        this.countryId = countryId;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ExtSiteReportEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ExtSiteReportEntity entity = objectMapper.readValue(str, ExtSiteReportEntity.class);
        return entity;

    }
}
