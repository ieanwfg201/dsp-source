package com.kritter.api.entity.reporting;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.ChartType;
import com.kritter.constants.Frequency;
import com.kritter.constants.PageConstants;
import com.kritter.constants.ReportingDIMTypeEnum;
import com.kritter.constants.ReportingTableType;

public class ReportingEntity {
    
    /** mandatory */
    private ReportingDIMTypeEnum reportingDIMTypeEnum = ReportingDIMTypeEnum.EXHAUSTIVE; 
    /** mandatory */
    private ReportingTableType reportingTableType = ReportingTableType.FIRSTLEVEL; 
    /** mandatory - YYYY-MM-DD HH:00:00 format */
    private String start_time_str = null; 
    /** mandatory - YYYY-MM-DD HH:00:00 format */
    private String end_time_str = null; 
    /** mandatory - default TimeZone */
    private String timezone = "UTC";
    /** mandatory - default */
    private String date_format = "yyyy-MM-dd HH:00:0";
    /** default */
	private String end_date_format = "yyyy-MM-dd 23:59:59";
    /** mandatory - true if report to be split by date*/
    private boolean date_as_dimension = false;
    /** mandatory - @see com.kritter.constants.Frequency */
	private Frequency frequency = Frequency.YESTERDAY;
    /** mandatory - default TABLE - @see com.kritter.constants.ChartType */
	private ChartType chartType = ChartType.TABLE;
    
    /*DERIVED DIMENSION*/
    /** mandatory - default null - null means not required, empty list means all required, integer-pubid element in list signifies filters */
	private List<Integer> pubId = null; 
    /** mandatory - default false - used by hierarchical reporting */
	private boolean pubId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, String @see com.kritter.constants.HygieneCategory element in list signifies filters */
	private List<String> site_hygiene = null;
    /** mandatory - default false - used by hierarchical reporting */
	private boolean site_hygiene_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, String - advertiser guid element in list signifies filters */
	private List<String> advertiserId = null;
    /** mandatory - default false - used by Admin hierarchical reporting */
	private boolean advertiserId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, String - advertiser guid element in list signifies filters */
	private List<Integer> advId = null;
    /** mandatory - default false - used by Admin hierarchical reporting */
	private boolean advId_clickable = false;
    
    /*ACTUAL AVAILABLE DIMENSION*/
    /** mandatory - default null - null means not required, empty list means all required, integer - siteId element in list signifies filters */
	private List<Integer> siteId = null; 
    /** mandatory - default false - used by hierarchical reporting */
	private boolean siteId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - siteId element in list signifies filters */
    private List<Integer> channelId = null; 
    /** mandatory - default false - used by hierarchical reporting */
    private boolean channelId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - siteId element in list signifies filters */
    private List<Integer> adpositionId = null; 
    /** mandatory - default false - used by hierarchical reporting */
    private boolean adpositionId_clickable = false;
    /** mandatory - set true if list to be used only as filter */
    private boolean siteId_just_filter = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - external site element in list signifies filters */
    private List<Integer> ext_site = null; 
    /** mandatory - default false - used by hierarchical reporting */
    private boolean ext_site_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - @see com.kritter.constants.SupplySourceTypeEnum element in list signifies filters */
    private List<Integer> supply_source_type = null; 
    /** mandatory - default false - used by hierarchical reporting */
    private boolean supply_source_type_clickable = false;
    private List<Integer> device_type = null; 
    /** mandatory - default false - used by hierarchical reporting */
    private boolean device_type_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - campaignid element in list signifies filters */
    private List<Integer> campaignId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean campaignId_clickable = false;
    /** mandatory - set true if list to be used only as filter */
    private boolean campaignId_just_filter = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - adid element in list signifies filters */
    private List<Integer> adId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean adId_clickable = false;
    /** mandatory - set true if list to be used only as filter */
    private boolean adId_just_filter = false;
    /** mandatory - default null - null means not required, empty list means all required, String - deviceid element in list signifies filters */
    private List<String> deviceId = null; 
    /** mandatory - default null - null means not required, empty list means all required, integer - manufacturerid / brand id element in list signifies filters */
    private List<Integer> deviceManufacturerId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean deviceManufacturerId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - model id element in list signifies filters */
    private List<Integer> deviceModelId = null; 
    /** mandatory - default null - null means not required, empty list means all required, integer - os id element in list signifies filters */
    private List<Integer> deviceOsId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean deviceOsId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - country id element in list signifies filters */
    private List<Integer> countryId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean countryId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - carrier id element in list signifies filters */
    private List<Integer> countryCarrierId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean countryCarrierId_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - country id element in list signifies filters */
    private List<Integer> stateId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean stateId_clickable = false;
    private List<Integer> cityeId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean cityId_clickable = false;

    /** mandatory - default null - null means not required, empty list means all required, integer - connection typeid @see com.kritter.constants.ConnectionType element in list signifies filters */
    private List<Integer> connection_type = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean connection_type_clickable = false;
    /** mandatory - default null - null means not required, empty list means all required, integer - country region id element in list signifies filters */
    private List<Integer> countryRegionId = null; 
    /** mandatory - default null - null means not required, empty list means all required, integer - exchange id element in list signifies filters */
    private List<Integer> exchangeId = null; 
    /** mandatory - default null - placeholder */
    private List<String> extSiteId = null; 
    /** mandatory - default null - placeholder */
    private List<Integer> creativeId = null; 
    /** mandatory - default null - placeholder */
    private List<Integer> bidderModelId = null;
    /** mandatory - default null - null means not required, empty list means all required, String - NoFillReason @see com.kritter.kumbaya.libraries.data_structs.common.NoFillReason element in list signifies filters */
    private List<String> nofillReason = null; 
    /** mandatory - default null - null means not required, empty list means all required, integer - browserid element in list signifies filters */
    private List<Integer> browserId = null; 
    /** mandatory - default false - used by  hierarchical reporting */
    private boolean browserId_clickable = false;
    
    
    /** metrics set to true if required */
    private boolean selectallmetric = true; 
    /** metrics set to true if required */
    private boolean total_request = false; 
    /** metrics set to true if required */
    private boolean total_impression = false;
    /** metrics set to true if required */
    private boolean total_bidValue = false;
    /** metrics set to true if required */
    private boolean total_click = false;
    /** metrics set to true if required */
    private boolean total_win = false;
    /** metrics set to true if required */
    private boolean total_win_bidValue = false;
    /** metrics set to true if required */
    private boolean total_csc = false;
    /** metrics set to true if required */
    private boolean total_event_type = false;
    /** metrics set to true if required */
    private boolean demandCharges = false;
    /** metrics set to true if required */
    private boolean supplyCost = false;
    /** metrics set to true if required */
    private boolean earning = false;
    /** metrics set to true if required */
    private boolean conversion = false;
    /** metrics set to true if required */
    private boolean cpa_goal = false;
    /** metrics set to true if required */
    private boolean exchangepayout = false;
    /** metrics set to true if required */
    private boolean exchangerevenue = false;
    /** metrics set to true if required */
    private boolean networkpayout = false;
    /** metrics set to true if required */
    private boolean networkrevenue = false;
    /** metrics set to true if required */
    private boolean billedclicks = false;
    /** metrics set to true if required */
    private boolean billedcsc = false;
    
    /** derived metrics */
    /** metrics set to true if required */
    private boolean ctr = false;
    /** metrics set to true if required */
    private boolean ecpm = false;
    /** metrics set to true if required */
    private boolean eIPM = false;
    /** metrics set to true if required */
    private boolean fr = false;
    /** metrics set to true if required */
    private boolean eIPC = false;
    /** metrics set to true if required */
    private boolean eCPC = false;
    /** metrics set to true if required */
    private boolean clicksr = false;
    /** metrics set to true if required */
    private boolean rtr = false;
    /** metrics set to true if required */
    private boolean wtr = false;
    /** metrics set to true if required */
    private boolean eIPW = false;
    /** metrics set to true if required */
    private boolean eCPW = false;
    /** metrics set to true if required */
    private boolean profitmargin = false;
    /** metrics set to true if required */
    private boolean eIPA = false;
    /** metrics set to true if required */
    private boolean eCPA = false;
    /** metrics set to true if required */
    private boolean billedECPC = false;
    /** metrics set to true if required */
    private boolean billedECPM = false;
    /** metrics set to true if required */
    private boolean billedEIPM = false;
    /** order by sequence number */
    /** true means descending otherwise by default ascending */
    private boolean order_by_desc = true;
    /** sequence no */
    private int total_request_order_sequence = -1; 
    /** sequence no */
    private int total_impression_order_sequence = -1;
    /** sequence no */
    private int total_bidValue_order_sequence = -1;
    /** sequence no */
    private int total_click_order_sequence = -1;
    /** sequence no */
    private int total_win_order_sequence = -1;
    /** sequence no */
    private int total_win_bidValue_order_sequence = -1;
    /** sequence no */
    private int total_csc_order_sequence = -1;
    /** sequence no */
    private int total_event_type_order_sequence = -1;
    /** sequence no */
    private int demandCharges_order_sequence = -1;
    /** sequence no */
    private int supplyCost_order_sequence = -1;
    /** sequence no */
    private int earning_order_sequence = -1;
    /** sequence no */
    private int ctr_order_sequence = -1;
    /** sequence no */
    private int ecpm_order_sequence = -1;
    /** sequence no */
    private int eIPM_order_sequence = -1;
    /** sequence no */
    private int fr_order_sequence = -1;
    /** sequence no */
    private int eIPC_order_sequence = -1;
    /** sequence no */
    private int eCPC_order_sequence = -1;
    /** sequence no */
    private int conversion_order_sequence = -1;
    /** sequence no */
    private int clicksr_order_sequence = -1;
    /** sequence no */
    private int rtr_order_sequence = -1;
    /** sequence no */
    private int wtr_order_sequence = -1;
    /** sequence no */
    private int eIPW_order_sequence = -1;
    /** sequence no */
    private int eCPW_order_sequence = -1;
    /** sequence no */
    private int profitmargin_order_sequence = -1;
    /** sequence no */
    private int eIPA_order_sequence = -1;
    /** sequence no */
    private int eCPA_order_sequence = -1;
    /** sequence no */
    private int cpa_goal_order_sequence = -1;
    /** sequence no */
    private int exchangepayout_order_sequence = -1;
    /** sequence no */
    private int exchangerevenue_order_sequence = -1;
    /** sequence no */
    private int networkpayout_order_sequence = -1;
    /** sequence no */
    private int networkrevenue_order_sequence = -1;
    /** sequence no */
    private int billedclicks_order_sequence = -1;
    /** sequence no */
    private int billedECPC_order_sequence = -1;
    /** sequence no */
    private int billedcsc_order_sequence = -1;
    /** sequence no */
    private int billedECPM_order_sequence = -1;
    /** sequence no */
    private int billedEIPM_order_sequence = -1;
    /** default false - return GUID */
    private boolean returnGuid = false;
    /** default false - return AdGUID */
    private boolean returnAdGuid = false;
    /**  to be used for charts */
    private int top_n = 5;
    /** to be used for charts has to be a negative number */
    private int top_n_for_last_x_hours = -12; 
    /** Start Index of the page  @see com.kritter.constants.PageConstants */
    private int startindex = PageConstants.start_index;
    /** Number of records to return @ see com.kritter.constants.PageConstants*/
    private int pagesize = PageConstants.page_size;
    
    /** true of rounding required */
    private boolean roundoffmetric = true;
    /** Default round off precision */
    private int roundoffmetriclength = 3;
    /** delimiter to use for repport download */
    private String csvDelimiter = ",";
    /** set true when Total is required in downloads */
    private boolean rollup = false;
    
    public List<Integer> getCountryId() {
        return countryId;
    }
    public void setCountryId(List<Integer> countryId) {
        this.countryId = countryId;
    }
    public List<Integer> getCountryCarrierId() {
        return countryCarrierId;
    }
    public void setCountryCarrierId(List<Integer> countryCarrierId) {
        this.countryCarrierId = countryCarrierId;
    }
    public List<Integer> getCountryRegionId() {
        return countryRegionId;
    }
    public void setCountryRegionId(List<Integer> countryRegionId) {
        this.countryRegionId = countryRegionId;
    }
    public List<Integer> getExchangeId() {
        return exchangeId;
    }
    public void setExchangeId(List<Integer> exchangeId) {
        this.exchangeId = exchangeId;
    }
    public List<String> getExtSiteId() {
        return extSiteId;
    }
    public void setExtSiteId(List<String> extSiteId) {
        this.extSiteId = extSiteId;
    }
    public List<Integer> getCreativeId() {
        return creativeId;
    }
    public void setCreativeId(List<Integer> creativeId) {
        this.creativeId = creativeId;
    }
    public List<Integer> getBidderModelId() {
        return bidderModelId;
    }
    public void setBidderModelId(List<Integer> bidderModelId) {
        this.bidderModelId = bidderModelId;
    }
    public List<String> getNofillReason() {
        return nofillReason;
    }
    public void setNofillReason(List<String> nofillReason) {
        this.nofillReason = nofillReason;
    }
    public boolean isReturnAdGuid() {
        return returnAdGuid;
    }
    public void setReturnAdGuid(boolean returnAdGuid) {
        this.returnAdGuid = returnAdGuid;
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
    public boolean isTotal_event_type() {
        return total_event_type;
    }
    public void setTotal_event_type(boolean total_event_type) {
        this.total_event_type = total_event_type;
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
    public boolean isCtr() {
        return ctr;
    }
    public void setCtr(boolean ctr) {
        this.ctr = ctr;
    }
    public boolean isEcpm() {
        return ecpm;
    }
    public void setEcpm(boolean ecpm) {
        this.ecpm = ecpm;
    }
    public boolean isFr() {
        return fr;
    }
    public void setFr(boolean fr) {
        this.fr = fr;
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
    public int getTotal_request_order_sequence() {
        return total_request_order_sequence;
    }
    public void setTotal_request_order_sequence(int total_request_order_sequence) {
        this.total_request_order_sequence = total_request_order_sequence;
    }
    public int getTotal_impression_order_sequence() {
        return total_impression_order_sequence;
    }
    public void setTotal_impression_order_sequence(
            int total_impression_order_sequence) {
        this.total_impression_order_sequence = total_impression_order_sequence;
    }
    public int getTotal_bidValue_order_sequence() {
        return total_bidValue_order_sequence;
    }
    public void setTotal_bidValue_order_sequence(int total_bidValue_order_sequence) {
        this.total_bidValue_order_sequence = total_bidValue_order_sequence;
    }
    public int getTotal_click_order_sequence() {
        return total_click_order_sequence;
    }
    public void setTotal_click_order_sequence(int total_click_order_sequence) {
        this.total_click_order_sequence = total_click_order_sequence;
    }
    public int getTotal_win_order_sequence() {
        return total_win_order_sequence;
    }
    public void setTotal_win_order_sequence(int total_win_order_sequence) {
        this.total_win_order_sequence = total_win_order_sequence;
    }
    public int getTotal_win_bidValue_order_sequence() {
        return total_win_bidValue_order_sequence;
    }
    public void setTotal_win_bidValue_order_sequence(
            int total_win_bidValue_order_sequence) {
        this.total_win_bidValue_order_sequence = total_win_bidValue_order_sequence;
    }
    public int getTotal_csc_order_sequence() {
        return total_csc_order_sequence;
    }
    public void setTotal_csc_order_sequence(int total_csc_order_sequence) {
        this.total_csc_order_sequence = total_csc_order_sequence;
    }
    public int getTotal_event_type_order_sequence() {
        return total_event_type_order_sequence;
    }
    public void setTotal_event_type_order_sequence(
            int total_event_type_order_sequence) {
        this.total_event_type_order_sequence = total_event_type_order_sequence;
    }
    public int getDemandCharges_order_sequence() {
        return demandCharges_order_sequence;
    }
    public void setDemandCharges_order_sequence(int demandCharges_order_sequence) {
        this.demandCharges_order_sequence = demandCharges_order_sequence;
    }
    public int getSupplyCost_order_sequence() {
        return supplyCost_order_sequence;
    }
    public void setSupplyCost_order_sequence(int supplyCost_order_sequence) {
        this.supplyCost_order_sequence = supplyCost_order_sequence;
    }
    public int getEarning_order_sequence() {
        return earning_order_sequence;
    }
    public void setEarning_order_sequence(int earning_order_sequence) {
        this.earning_order_sequence = earning_order_sequence;
    }
    public int getCtr_order_sequence() {
        return ctr_order_sequence;
    }
    public void setCtr_order_sequence(int ctr_order_sequence) {
        this.ctr_order_sequence = ctr_order_sequence;
    }
    public int getEcpm_order_sequence() {
        return ecpm_order_sequence;
    }
    public void setEcpm_order_sequence(int ecpm_order_sequence) {
        this.ecpm_order_sequence = ecpm_order_sequence;
    }
    public int getFr_order_sequence() {
        return fr_order_sequence;
    }
    public void setFr_order_sequence(int fr_order_sequence) {
        this.fr_order_sequence = fr_order_sequence;
    }
    public boolean isOrder_by_desc() {
        return order_by_desc;
    }
    public void setOrder_by_desc(boolean order_by_desc) {
        this.order_by_desc = order_by_desc;
    }
    public int getTop_n() {
        return top_n;
    }
    public void setTop_n(int top_n) {
        this.top_n = top_n;
    }
    public int getTop_n_for_last_x_hours() {
        return top_n_for_last_x_hours;
    }
    public void setTop_n_for_last_x_hours(int top_n_for_last_x_hours) {
        this.top_n_for_last_x_hours = top_n_for_last_x_hours;
    }
    public boolean iseIPC() {
        return eIPC;
    }
    public void seteIPC(boolean eIPC) {
        this.eIPC = eIPC;
    }
    public boolean iseCPC() {
        return eCPC;
    }
    public void seteCPC(boolean eCPC) {
        this.eCPC = eCPC;
    }
    public int geteIPC_order_sequence() {
        return eIPC_order_sequence;
    }
    public void seteIPC_order_sequence(int eIPC_order_sequence) {
        this.eIPC_order_sequence = eIPC_order_sequence;
    }
    public int geteCPC_order_sequence() {
        return eCPC_order_sequence;
    }
    public void seteCPC_order_sequence(int eCPC_order_sequence) {
        this.eCPC_order_sequence = eCPC_order_sequence;
    }
    public boolean isConversion() {
        return conversion;
    }
    public void setConversion(boolean conversion) {
        this.conversion = conversion;
    }
    public boolean isClicksr() {
        return clicksr;
    }
    public void setClicksr(boolean clicksr) {
        this.clicksr = clicksr;
    }
    public int getConversion_order_sequence() {
        return conversion_order_sequence;
    }
    public void setConversion_order_sequence(int conversion_order_sequence) {
        this.conversion_order_sequence = conversion_order_sequence;
    }
    public int getClicksr_order_sequence() {
        return clicksr_order_sequence;
    }
    public void setClicksr_order_sequence(int clicksr_order_sequence) {
        this.clicksr_order_sequence = clicksr_order_sequence;
    }
    public boolean iseIPM() {
        return eIPM;
    }
    public void seteIPM(boolean eIPM) {
        this.eIPM = eIPM;
    }
    public int geteIPM_order_sequence() {
        return eIPM_order_sequence;
    }
    public void seteIPM_order_sequence(int eIPM_order_sequence) {
        this.eIPM_order_sequence = eIPM_order_sequence;
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
    public boolean isReturnGuid() {
        return returnGuid;
    }
    public void setReturnGuid(boolean returnGuid) {
        this.returnGuid = returnGuid;
    }
    public boolean isCountryId_clickable() {
        return countryId_clickable;
    }
    public void setCountryId_clickable(boolean countryId_clickable) {
        this.countryId_clickable = countryId_clickable;
    }
    public boolean isCountryCarrierId_clickable() {
        return countryCarrierId_clickable;
    }
    public void setCountryCarrierId_clickable(boolean countryCarrierId_clickable) {
        this.countryCarrierId_clickable = countryCarrierId_clickable;
    }
    public boolean isRtr() {
        return rtr;
    }
    public void setRtr(boolean rtr) {
        this.rtr = rtr;
    }
    public boolean isWtr() {
        return wtr;
    }
    public void setWtr(boolean wtr) {
        this.wtr = wtr;
    }
    public boolean isProfitmargin() {
        return profitmargin;
    }
    public void setProfitmargin(boolean profitmargin) {
        this.profitmargin = profitmargin;
    }
    public int getRtr_order_sequence() {
        return rtr_order_sequence;
    }
    public void setRtr_order_sequence(int rtr_order_sequence) {
        this.rtr_order_sequence = rtr_order_sequence;
    }
    public int getWtr_order_sequence() {
        return wtr_order_sequence;
    }
    public void setWtr_order_sequence(int wtr_order_sequence) {
        this.wtr_order_sequence = wtr_order_sequence;
    }
    public int geteCPW_order_sequence() {
        return eCPW_order_sequence;
    }
    public void seteCPW_order_sequence(int eCPW_order_sequence) {
        this.eCPW_order_sequence = eCPW_order_sequence;
    }
    public int getProfitmargin_order_sequence() {
        return profitmargin_order_sequence;
    }
    public void setProfitmargin_order_sequence(int profitmargin_order_sequence) {
        this.profitmargin_order_sequence = profitmargin_order_sequence;
    }
    public boolean iseIPW() {
        return eIPW;
    }
    public void seteIPW(boolean eIPW) {
        this.eIPW = eIPW;
    }
    public boolean iseCPW() {
        return eCPW;
    }
    public void seteCPW(boolean eCPW) {
        this.eCPW = eCPW;
    }
    public int geteIPW_order_sequence() {
        return eIPW_order_sequence;
    }
    public void seteIPW_order_sequence(int eIPW_order_sequence) {
        this.eIPW_order_sequence = eIPW_order_sequence;
    }
    public boolean iseIPA() {
        return eIPA;
    }
    public void seteIPA(boolean eIPA) {
        this.eIPA = eIPA;
    }
    public boolean iseCPA() {
        return eCPA;
    }
    public void seteCPA(boolean eCPA) {
        this.eCPA = eCPA;
    }
    public int geteIPA_order_sequence() {
        return eIPA_order_sequence;
    }
    public void seteIPA_order_sequence(int eIPA_order_sequence) {
        this.eIPA_order_sequence = eIPA_order_sequence;
    }
    public int geteCPA_order_sequence() {
        return eCPA_order_sequence;
    }
    public void seteCPA_order_sequence(int eCPA_order_sequence) {
        this.eCPA_order_sequence = eCPA_order_sequence;
    }
    public boolean isBrowserId_clickable() {
        return browserId_clickable;
    }
    public void setBrowserId_clickable(boolean browserId_clickable) {
        this.browserId_clickable = browserId_clickable;
    }
    public List<Integer> getBrowserId() {
        return browserId;
    }
    public void setBrowserId(List<Integer> browserId) {
        this.browserId = browserId;
    }
    public boolean isCpa_goal() {
        return cpa_goal;
    }
    public void setCpa_goal(boolean cpa_goal) {
        this.cpa_goal = cpa_goal;
    }
    public int getCpa_goal_order_sequence() {
        return cpa_goal_order_sequence;
    }
    public void setCpa_goal_order_sequence(int cpa_goal_order_sequence) {
        this.cpa_goal_order_sequence = cpa_goal_order_sequence;
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
    public int getExchangepayout_order_sequence() {
        return exchangepayout_order_sequence;
    }
    public void setExchangepayout_order_sequence(int exchangepayout_order_sequence) {
        this.exchangepayout_order_sequence = exchangepayout_order_sequence;
    }
    public int getExchangerevenue_order_sequence() {
        return exchangerevenue_order_sequence;
    }
    public void setExchangerevenue_order_sequence(int exchangerevenue_order_sequence) {
        this.exchangerevenue_order_sequence = exchangerevenue_order_sequence;
    }
    public int getNetworkpayout_order_sequence() {
        return networkpayout_order_sequence;
    }
    public void setNetworkpayout_order_sequence(int networkpayout_order_sequence) {
        this.networkpayout_order_sequence = networkpayout_order_sequence;
    }
    public int getNetworkrevenue_order_sequence() {
        return networkrevenue_order_sequence;
    }
    public void setNetworkrevenue_order_sequence(int networkrevenue_order_sequence) {
        this.networkrevenue_order_sequence = networkrevenue_order_sequence;
    }
    public boolean isBilledclicks() {
        return billedclicks;
    }
    public void setBilledclicks(boolean billedclicks) {
        this.billedclicks = billedclicks;
    }
    public boolean isBilledECPC() {
        return billedECPC;
    }
    public void setBilledECPC(boolean billedECPC) {
        this.billedECPC = billedECPC;
    }
    public int getBilledclicks_order_sequence() {
        return billedclicks_order_sequence;
    }
    public void setBilledclicks_order_sequence(int billedclicks_order_sequence) {
        this.billedclicks_order_sequence = billedclicks_order_sequence;
    }
    public int getBilledECPC_order_sequence() {
        return billedECPC_order_sequence;
    }
    public void setBilledECPC_order_sequence(int billedECPC_order_sequence) {
        this.billedECPC_order_sequence = billedECPC_order_sequence;
    }
    public boolean isBilledcsc() {
        return billedcsc;
    }
    public void setBilledcsc(boolean billedcsc) {
        this.billedcsc = billedcsc;
    }
    public boolean isBilledECPM() {
        return billedECPM;
    }
    public void setBilledECPM(boolean billedECPM) {
        this.billedECPM = billedECPM;
    }
    public boolean isBilledEIPM() {
        return billedEIPM;
    }
    public void setBilledEIPM(boolean billedEIPM) {
        this.billedEIPM = billedEIPM;
    }
    public int getBilledcsc_order_sequence() {
        return billedcsc_order_sequence;
    }
    public void setBilledcsc_order_sequence(int billedcsc_order_sequence) {
        this.billedcsc_order_sequence = billedcsc_order_sequence;
    }
    public int getBilledECPM_order_sequence() {
        return billedECPM_order_sequence;
    }
    public void setBilledECPM_order_sequence(int billedECPM_order_sequence) {
        this.billedECPM_order_sequence = billedECPM_order_sequence;
    }
    public int getBilledEIPM_order_sequence() {
        return billedEIPM_order_sequence;
    }
    public void setBilledEIPM_order_sequence(int billedEIPM_order_sequence) {
        this.billedEIPM_order_sequence = billedEIPM_order_sequence;
    }
    public List<Integer> getConnection_type() {
        return connection_type;
    }
    public void setConnection_type(List<Integer> connection_type) {
        this.connection_type = connection_type;
    }
    public boolean isConnection_type_clickable() {
        return connection_type_clickable;
    }
    public void setConnection_type_clickable(boolean connection_type_clickable) {
        this.connection_type_clickable = connection_type_clickable;
    }
    public boolean isSelectallmetric() {
        return selectallmetric;
    }
    public void setSelectallmetric(boolean selectallmetric) {
        this.selectallmetric = selectallmetric;
    }
    public ReportingDIMTypeEnum getReportingDIMTypeEnum() {
		return reportingDIMTypeEnum;
	}
	public void setReportingDIMTypeEnum(ReportingDIMTypeEnum reportingDIMTypeEnum) {
		this.reportingDIMTypeEnum = reportingDIMTypeEnum;
	}
	public ReportingTableType getReportingTableType() {
		return reportingTableType;
	}
	public void setReportingTableType(ReportingTableType reportingTableType) {
		this.reportingTableType = reportingTableType;
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
	public ChartType getChartType() {
		return chartType;
	}
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}
	public List<Integer> getPubId() {
		return pubId;
	}
	public void setPubId(List<Integer> pubId) {
		this.pubId = pubId;
	}
	public boolean isPubId_clickable() {
		return pubId_clickable;
	}
	public void setPubId_clickable(boolean pubId_clickable) {
		this.pubId_clickable = pubId_clickable;
	}
	public List<String> getSite_hygiene() {
		return site_hygiene;
	}
	public void setSite_hygiene(List<String> site_hygiene) {
		this.site_hygiene = site_hygiene;
	}
	public boolean isSite_hygiene_clickable() {
		return site_hygiene_clickable;
	}
	public void setSite_hygiene_clickable(boolean site_hygiene_clickable) {
		this.site_hygiene_clickable = site_hygiene_clickable;
	}
	public List<String> getAdvertiserId() {
		return advertiserId;
	}
	public void setAdvertiserId(List<String> advertiserId) {
		this.advertiserId = advertiserId;
	}
	public boolean isAdvertiserId_clickable() {
		return advertiserId_clickable;
	}
	public void setAdvertiserId_clickable(boolean advertiserId_clickable) {
		this.advertiserId_clickable = advertiserId_clickable;
	}
	public List<Integer> getAdvId() {
		return advId;
	}
	public void setAdvId(List<Integer> advId) {
		this.advId = advId;
	}
	public boolean isAdvId_clickable() {
		return advId_clickable;
	}
	public void setAdvId_clickable(boolean advId_clickable) {
		this.advId_clickable = advId_clickable;
	}
	public List<Integer> getSiteId() {
		return siteId;
	}
	public void setSiteId(List<Integer> siteId) {
		this.siteId = siteId;
	}
	public boolean isSiteId_clickable() {
		return siteId_clickable;
	}
	public void setSiteId_clickable(boolean siteId_clickable) {
		this.siteId_clickable = siteId_clickable;
	}
	public List<Integer> getChannelId() {
		return channelId;
	}
	public void setChannelId(List<Integer> channelId) {
		this.channelId = channelId;
	}
	public boolean isChannelId_clickable() {
		return channelId_clickable;
	}
	public void setChannelId_clickable(boolean channelId_clickable) {
		this.channelId_clickable = channelId_clickable;
	}
	public List<Integer> getAdpositionId() {
		return adpositionId;
	}
	public void setAdpositionId(List<Integer> adpositionId) {
		this.adpositionId = adpositionId;
	}
	public boolean isAdpositionId_clickable() {
		return adpositionId_clickable;
	}
	public void setAdpositionId_clickable(boolean adpositionId_clickable) {
		this.adpositionId_clickable = adpositionId_clickable;
	}
	public boolean isSiteId_just_filter() {
		return siteId_just_filter;
	}
	public void setSiteId_just_filter(boolean siteId_just_filter) {
		this.siteId_just_filter = siteId_just_filter;
	}
	public List<Integer> getExt_site() {
		return ext_site;
	}
	public void setExt_site(List<Integer> ext_site) {
		this.ext_site = ext_site;
	}
	public boolean isExt_site_clickable() {
		return ext_site_clickable;
	}
	public void setExt_site_clickable(boolean ext_site_clickable) {
		this.ext_site_clickable = ext_site_clickable;
	}
	public List<Integer> getSupply_source_type() {
		return supply_source_type;
	}
	public void setSupply_source_type(List<Integer> supply_source_type) {
		this.supply_source_type = supply_source_type;
	}
	public boolean isSupply_source_type_clickable() {
		return supply_source_type_clickable;
	}
	public void setSupply_source_type_clickable(boolean supply_source_type_clickable) {
		this.supply_source_type_clickable = supply_source_type_clickable;
	}
	public List<Integer> getDevice_type() {
		return device_type;
	}
	public void setDevice_type(List<Integer> device_type) {
		this.device_type = device_type;
	}
	public boolean isDevice_type_clickable() {
		return device_type_clickable;
	}
	public void setDevice_type_clickable(boolean device_type_clickable) {
		this.device_type_clickable = device_type_clickable;
	}
	public List<Integer> getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(List<Integer> campaignId) {
		this.campaignId = campaignId;
	}
	public boolean isCampaignId_clickable() {
		return campaignId_clickable;
	}
	public void setCampaignId_clickable(boolean campaignId_clickable) {
		this.campaignId_clickable = campaignId_clickable;
	}
	public boolean isCampaignId_just_filter() {
		return campaignId_just_filter;
	}
	public void setCampaignId_just_filter(boolean campaignId_just_filter) {
		this.campaignId_just_filter = campaignId_just_filter;
	}
	public List<Integer> getAdId() {
		return adId;
	}
	public void setAdId(List<Integer> adId) {
		this.adId = adId;
	}
	public boolean isAdId_clickable() {
		return adId_clickable;
	}
	public void setAdId_clickable(boolean adId_clickable) {
		this.adId_clickable = adId_clickable;
	}
	public boolean isAdId_just_filter() {
		return adId_just_filter;
	}
	public void setAdId_just_filter(boolean adId_just_filter) {
		this.adId_just_filter = adId_just_filter;
	}
	public List<String> getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(List<String> deviceId) {
		this.deviceId = deviceId;
	}
	public List<Integer> getDeviceManufacturerId() {
		return deviceManufacturerId;
	}
	public void setDeviceManufacturerId(List<Integer> deviceManufacturerId) {
		this.deviceManufacturerId = deviceManufacturerId;
	}
	public boolean isDeviceManufacturerId_clickable() {
		return deviceManufacturerId_clickable;
	}
	public void setDeviceManufacturerId_clickable(boolean deviceManufacturerId_clickable) {
		this.deviceManufacturerId_clickable = deviceManufacturerId_clickable;
	}
	public List<Integer> getDeviceModelId() {
		return deviceModelId;
	}
	public void setDeviceModelId(List<Integer> deviceModelId) {
		this.deviceModelId = deviceModelId;
	}
	public List<Integer> getDeviceOsId() {
		return deviceOsId;
	}
	public void setDeviceOsId(List<Integer> deviceOsId) {
		this.deviceOsId = deviceOsId;
	}
	public boolean isDeviceOsId_clickable() {
		return deviceOsId_clickable;
	}
	public void setDeviceOsId_clickable(boolean deviceOsId_clickable) {
		this.deviceOsId_clickable = deviceOsId_clickable;
	}
	public List<Integer> getStateId() {
		return stateId;
	}
	public void setStateId(List<Integer> stateId) {
		this.stateId = stateId;
	}
	public boolean isStateId_clickable() {
		return stateId_clickable;
	}
	public void setStateId_clickable(boolean stateId_clickable) {
		this.stateId_clickable = stateId_clickable;
	}
	public List<Integer> getCityeId() {
		return cityeId;
	}
	public void setCityeId(List<Integer> cityeId) {
		this.cityeId = cityeId;
	}
	public boolean isCityId_clickable() {
		return cityId_clickable;
	}
	public void setCityId_clickable(boolean cityId_clickable) {
		this.cityId_clickable = cityId_clickable;
	}
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ReportingEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        ReportingEntity entity = objectMapper.readValue(str, ReportingEntity.class);
        return entity;

    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adId == null) ? 0 : adId.hashCode());
		result = prime * result + (adId_clickable ? 1231 : 1237);
		result = prime * result + (adId_just_filter ? 1231 : 1237);
		result = prime * result + ((adpositionId == null) ? 0 : adpositionId.hashCode());
		result = prime * result + (adpositionId_clickable ? 1231 : 1237);
		result = prime * result + ((advId == null) ? 0 : advId.hashCode());
		result = prime * result + (advId_clickable ? 1231 : 1237);
		result = prime * result + ((advertiserId == null) ? 0 : advertiserId.hashCode());
		result = prime * result + (advertiserId_clickable ? 1231 : 1237);
		result = prime * result + ((bidderModelId == null) ? 0 : bidderModelId.hashCode());
		result = prime * result + (billedECPC ? 1231 : 1237);
		result = prime * result + billedECPC_order_sequence;
		result = prime * result + (billedECPM ? 1231 : 1237);
		result = prime * result + billedECPM_order_sequence;
		result = prime * result + (billedEIPM ? 1231 : 1237);
		result = prime * result + billedEIPM_order_sequence;
		result = prime * result + (billedclicks ? 1231 : 1237);
		result = prime * result + billedclicks_order_sequence;
		result = prime * result + (billedcsc ? 1231 : 1237);
		result = prime * result + billedcsc_order_sequence;
		result = prime * result + ((browserId == null) ? 0 : browserId.hashCode());
		result = prime * result + (browserId_clickable ? 1231 : 1237);
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result + (campaignId_clickable ? 1231 : 1237);
		result = prime * result + (campaignId_just_filter ? 1231 : 1237);
		result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
		result = prime * result + (channelId_clickable ? 1231 : 1237);
		result = prime * result + ((chartType == null) ? 0 : chartType.hashCode());
		result = prime * result + (cityId_clickable ? 1231 : 1237);
		result = prime * result + ((cityeId == null) ? 0 : cityeId.hashCode());
		result = prime * result + (clicksr ? 1231 : 1237);
		result = prime * result + clicksr_order_sequence;
		result = prime * result + ((connection_type == null) ? 0 : connection_type.hashCode());
		result = prime * result + (connection_type_clickable ? 1231 : 1237);
		result = prime * result + (conversion ? 1231 : 1237);
		result = prime * result + conversion_order_sequence;
		result = prime * result + ((countryCarrierId == null) ? 0 : countryCarrierId.hashCode());
		result = prime * result + (countryCarrierId_clickable ? 1231 : 1237);
		result = prime * result + ((countryId == null) ? 0 : countryId.hashCode());
		result = prime * result + (countryId_clickable ? 1231 : 1237);
		result = prime * result + ((countryRegionId == null) ? 0 : countryRegionId.hashCode());
		result = prime * result + (cpa_goal ? 1231 : 1237);
		result = prime * result + cpa_goal_order_sequence;
		result = prime * result + ((creativeId == null) ? 0 : creativeId.hashCode());
		result = prime * result + ((csvDelimiter == null) ? 0 : csvDelimiter.hashCode());
		result = prime * result + (ctr ? 1231 : 1237);
		result = prime * result + ctr_order_sequence;
		result = prime * result + (date_as_dimension ? 1231 : 1237);
		result = prime * result + ((date_format == null) ? 0 : date_format.hashCode());
		result = prime * result + (demandCharges ? 1231 : 1237);
		result = prime * result + demandCharges_order_sequence;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((deviceManufacturerId == null) ? 0 : deviceManufacturerId.hashCode());
		result = prime * result + (deviceManufacturerId_clickable ? 1231 : 1237);
		result = prime * result + ((deviceModelId == null) ? 0 : deviceModelId.hashCode());
		result = prime * result + ((deviceOsId == null) ? 0 : deviceOsId.hashCode());
		result = prime * result + (deviceOsId_clickable ? 1231 : 1237);
		result = prime * result + ((device_type == null) ? 0 : device_type.hashCode());
		result = prime * result + (device_type_clickable ? 1231 : 1237);
		result = prime * result + (eCPA ? 1231 : 1237);
		result = prime * result + eCPA_order_sequence;
		result = prime * result + (eCPC ? 1231 : 1237);
		result = prime * result + eCPC_order_sequence;
		result = prime * result + (eCPW ? 1231 : 1237);
		result = prime * result + eCPW_order_sequence;
		result = prime * result + (eIPA ? 1231 : 1237);
		result = prime * result + eIPA_order_sequence;
		result = prime * result + (eIPC ? 1231 : 1237);
		result = prime * result + eIPC_order_sequence;
		result = prime * result + (eIPM ? 1231 : 1237);
		result = prime * result + eIPM_order_sequence;
		result = prime * result + (eIPW ? 1231 : 1237);
		result = prime * result + eIPW_order_sequence;
		result = prime * result + (earning ? 1231 : 1237);
		result = prime * result + earning_order_sequence;
		result = prime * result + (ecpm ? 1231 : 1237);
		result = prime * result + ecpm_order_sequence;
		result = prime * result + ((end_date_format == null) ? 0 : end_date_format.hashCode());
		result = prime * result + ((end_time_str == null) ? 0 : end_time_str.hashCode());
		result = prime * result + ((exchangeId == null) ? 0 : exchangeId.hashCode());
		result = prime * result + (exchangepayout ? 1231 : 1237);
		result = prime * result + exchangepayout_order_sequence;
		result = prime * result + (exchangerevenue ? 1231 : 1237);
		result = prime * result + exchangerevenue_order_sequence;
		result = prime * result + ((extSiteId == null) ? 0 : extSiteId.hashCode());
		result = prime * result + ((ext_site == null) ? 0 : ext_site.hashCode());
		result = prime * result + (ext_site_clickable ? 1231 : 1237);
		result = prime * result + (fr ? 1231 : 1237);
		result = prime * result + fr_order_sequence;
		result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result + (networkpayout ? 1231 : 1237);
		result = prime * result + networkpayout_order_sequence;
		result = prime * result + (networkrevenue ? 1231 : 1237);
		result = prime * result + networkrevenue_order_sequence;
		result = prime * result + ((nofillReason == null) ? 0 : nofillReason.hashCode());
		result = prime * result + (order_by_desc ? 1231 : 1237);
		result = prime * result + pagesize;
		result = prime * result + (profitmargin ? 1231 : 1237);
		result = prime * result + profitmargin_order_sequence;
		result = prime * result + ((pubId == null) ? 0 : pubId.hashCode());
		result = prime * result + (pubId_clickable ? 1231 : 1237);
		result = prime * result + ((reportingDIMTypeEnum == null) ? 0 : reportingDIMTypeEnum.hashCode());
		result = prime * result + ((reportingTableType == null) ? 0 : reportingTableType.hashCode());
		result = prime * result + (returnAdGuid ? 1231 : 1237);
		result = prime * result + (returnGuid ? 1231 : 1237);
		result = prime * result + (rollup ? 1231 : 1237);
		result = prime * result + (roundoffmetric ? 1231 : 1237);
		result = prime * result + roundoffmetriclength;
		result = prime * result + (rtr ? 1231 : 1237);
		result = prime * result + rtr_order_sequence;
		result = prime * result + (selectallmetric ? 1231 : 1237);
		result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
		result = prime * result + (siteId_clickable ? 1231 : 1237);
		result = prime * result + (siteId_just_filter ? 1231 : 1237);
		result = prime * result + ((site_hygiene == null) ? 0 : site_hygiene.hashCode());
		result = prime * result + (site_hygiene_clickable ? 1231 : 1237);
		result = prime * result + ((start_time_str == null) ? 0 : start_time_str.hashCode());
		result = prime * result + startindex;
		result = prime * result + ((stateId == null) ? 0 : stateId.hashCode());
		result = prime * result + (stateId_clickable ? 1231 : 1237);
		result = prime * result + (supplyCost ? 1231 : 1237);
		result = prime * result + supplyCost_order_sequence;
		result = prime * result + ((supply_source_type == null) ? 0 : supply_source_type.hashCode());
		result = prime * result + (supply_source_type_clickable ? 1231 : 1237);
		result = prime * result + ((timezone == null) ? 0 : timezone.hashCode());
		result = prime * result + top_n;
		result = prime * result + top_n_for_last_x_hours;
		result = prime * result + (total_bidValue ? 1231 : 1237);
		result = prime * result + total_bidValue_order_sequence;
		result = prime * result + (total_click ? 1231 : 1237);
		result = prime * result + total_click_order_sequence;
		result = prime * result + (total_csc ? 1231 : 1237);
		result = prime * result + total_csc_order_sequence;
		result = prime * result + (total_event_type ? 1231 : 1237);
		result = prime * result + total_event_type_order_sequence;
		result = prime * result + (total_impression ? 1231 : 1237);
		result = prime * result + total_impression_order_sequence;
		result = prime * result + (total_request ? 1231 : 1237);
		result = prime * result + total_request_order_sequence;
		result = prime * result + (total_win ? 1231 : 1237);
		result = prime * result + (total_win_bidValue ? 1231 : 1237);
		result = prime * result + total_win_bidValue_order_sequence;
		result = prime * result + total_win_order_sequence;
		result = prime * result + (wtr ? 1231 : 1237);
		result = prime * result + wtr_order_sequence;
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
		ReportingEntity other = (ReportingEntity) obj;
		if (adId == null) {
			if (other.adId != null)
				return false;
		} else if (!adId.equals(other.adId))
			return false;
		if (adId_clickable != other.adId_clickable)
			return false;
		if (adId_just_filter != other.adId_just_filter)
			return false;
		if (adpositionId == null) {
			if (other.adpositionId != null)
				return false;
		} else if (!adpositionId.equals(other.adpositionId))
			return false;
		if (adpositionId_clickable != other.adpositionId_clickable)
			return false;
		if (advId == null) {
			if (other.advId != null)
				return false;
		} else if (!advId.equals(other.advId))
			return false;
		if (advId_clickable != other.advId_clickable)
			return false;
		if (advertiserId == null) {
			if (other.advertiserId != null)
				return false;
		} else if (!advertiserId.equals(other.advertiserId))
			return false;
		if (advertiserId_clickable != other.advertiserId_clickable)
			return false;
		if (bidderModelId == null) {
			if (other.bidderModelId != null)
				return false;
		} else if (!bidderModelId.equals(other.bidderModelId))
			return false;
		if (billedECPC != other.billedECPC)
			return false;
		if (billedECPC_order_sequence != other.billedECPC_order_sequence)
			return false;
		if (billedECPM != other.billedECPM)
			return false;
		if (billedECPM_order_sequence != other.billedECPM_order_sequence)
			return false;
		if (billedEIPM != other.billedEIPM)
			return false;
		if (billedEIPM_order_sequence != other.billedEIPM_order_sequence)
			return false;
		if (billedclicks != other.billedclicks)
			return false;
		if (billedclicks_order_sequence != other.billedclicks_order_sequence)
			return false;
		if (billedcsc != other.billedcsc)
			return false;
		if (billedcsc_order_sequence != other.billedcsc_order_sequence)
			return false;
		if (browserId == null) {
			if (other.browserId != null)
				return false;
		} else if (!browserId.equals(other.browserId))
			return false;
		if (browserId_clickable != other.browserId_clickable)
			return false;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		if (campaignId_clickable != other.campaignId_clickable)
			return false;
		if (campaignId_just_filter != other.campaignId_just_filter)
			return false;
		if (channelId == null) {
			if (other.channelId != null)
				return false;
		} else if (!channelId.equals(other.channelId))
			return false;
		if (channelId_clickable != other.channelId_clickable)
			return false;
		if (chartType != other.chartType)
			return false;
		if (cityId_clickable != other.cityId_clickable)
			return false;
		if (cityeId == null) {
			if (other.cityeId != null)
				return false;
		} else if (!cityeId.equals(other.cityeId))
			return false;
		if (clicksr != other.clicksr)
			return false;
		if (clicksr_order_sequence != other.clicksr_order_sequence)
			return false;
		if (connection_type == null) {
			if (other.connection_type != null)
				return false;
		} else if (!connection_type.equals(other.connection_type))
			return false;
		if (connection_type_clickable != other.connection_type_clickable)
			return false;
		if (conversion != other.conversion)
			return false;
		if (conversion_order_sequence != other.conversion_order_sequence)
			return false;
		if (countryCarrierId == null) {
			if (other.countryCarrierId != null)
				return false;
		} else if (!countryCarrierId.equals(other.countryCarrierId))
			return false;
		if (countryCarrierId_clickable != other.countryCarrierId_clickable)
			return false;
		if (countryId == null) {
			if (other.countryId != null)
				return false;
		} else if (!countryId.equals(other.countryId))
			return false;
		if (countryId_clickable != other.countryId_clickable)
			return false;
		if (countryRegionId == null) {
			if (other.countryRegionId != null)
				return false;
		} else if (!countryRegionId.equals(other.countryRegionId))
			return false;
		if (cpa_goal != other.cpa_goal)
			return false;
		if (cpa_goal_order_sequence != other.cpa_goal_order_sequence)
			return false;
		if (creativeId == null) {
			if (other.creativeId != null)
				return false;
		} else if (!creativeId.equals(other.creativeId))
			return false;
		if (csvDelimiter == null) {
			if (other.csvDelimiter != null)
				return false;
		} else if (!csvDelimiter.equals(other.csvDelimiter))
			return false;
		if (ctr != other.ctr)
			return false;
		if (ctr_order_sequence != other.ctr_order_sequence)
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
		if (demandCharges_order_sequence != other.demandCharges_order_sequence)
			return false;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (deviceManufacturerId == null) {
			if (other.deviceManufacturerId != null)
				return false;
		} else if (!deviceManufacturerId.equals(other.deviceManufacturerId))
			return false;
		if (deviceManufacturerId_clickable != other.deviceManufacturerId_clickable)
			return false;
		if (deviceModelId == null) {
			if (other.deviceModelId != null)
				return false;
		} else if (!deviceModelId.equals(other.deviceModelId))
			return false;
		if (deviceOsId == null) {
			if (other.deviceOsId != null)
				return false;
		} else if (!deviceOsId.equals(other.deviceOsId))
			return false;
		if (deviceOsId_clickable != other.deviceOsId_clickable)
			return false;
		if (device_type == null) {
			if (other.device_type != null)
				return false;
		} else if (!device_type.equals(other.device_type))
			return false;
		if (device_type_clickable != other.device_type_clickable)
			return false;
		if (eCPA != other.eCPA)
			return false;
		if (eCPA_order_sequence != other.eCPA_order_sequence)
			return false;
		if (eCPC != other.eCPC)
			return false;
		if (eCPC_order_sequence != other.eCPC_order_sequence)
			return false;
		if (eCPW != other.eCPW)
			return false;
		if (eCPW_order_sequence != other.eCPW_order_sequence)
			return false;
		if (eIPA != other.eIPA)
			return false;
		if (eIPA_order_sequence != other.eIPA_order_sequence)
			return false;
		if (eIPC != other.eIPC)
			return false;
		if (eIPC_order_sequence != other.eIPC_order_sequence)
			return false;
		if (eIPM != other.eIPM)
			return false;
		if (eIPM_order_sequence != other.eIPM_order_sequence)
			return false;
		if (eIPW != other.eIPW)
			return false;
		if (eIPW_order_sequence != other.eIPW_order_sequence)
			return false;
		if (earning != other.earning)
			return false;
		if (earning_order_sequence != other.earning_order_sequence)
			return false;
		if (ecpm != other.ecpm)
			return false;
		if (ecpm_order_sequence != other.ecpm_order_sequence)
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
		if (exchangepayout_order_sequence != other.exchangepayout_order_sequence)
			return false;
		if (exchangerevenue != other.exchangerevenue)
			return false;
		if (exchangerevenue_order_sequence != other.exchangerevenue_order_sequence)
			return false;
		if (extSiteId == null) {
			if (other.extSiteId != null)
				return false;
		} else if (!extSiteId.equals(other.extSiteId))
			return false;
		if (ext_site == null) {
			if (other.ext_site != null)
				return false;
		} else if (!ext_site.equals(other.ext_site))
			return false;
		if (ext_site_clickable != other.ext_site_clickable)
			return false;
		if (fr != other.fr)
			return false;
		if (fr_order_sequence != other.fr_order_sequence)
			return false;
		if (frequency != other.frequency)
			return false;
		if (networkpayout != other.networkpayout)
			return false;
		if (networkpayout_order_sequence != other.networkpayout_order_sequence)
			return false;
		if (networkrevenue != other.networkrevenue)
			return false;
		if (networkrevenue_order_sequence != other.networkrevenue_order_sequence)
			return false;
		if (nofillReason == null) {
			if (other.nofillReason != null)
				return false;
		} else if (!nofillReason.equals(other.nofillReason))
			return false;
		if (order_by_desc != other.order_by_desc)
			return false;
		if (pagesize != other.pagesize)
			return false;
		if (profitmargin != other.profitmargin)
			return false;
		if (profitmargin_order_sequence != other.profitmargin_order_sequence)
			return false;
		if (pubId == null) {
			if (other.pubId != null)
				return false;
		} else if (!pubId.equals(other.pubId))
			return false;
		if (pubId_clickable != other.pubId_clickable)
			return false;
		if (reportingDIMTypeEnum != other.reportingDIMTypeEnum)
			return false;
		if (reportingTableType != other.reportingTableType)
			return false;
		if (returnAdGuid != other.returnAdGuid)
			return false;
		if (returnGuid != other.returnGuid)
			return false;
		if (rollup != other.rollup)
			return false;
		if (roundoffmetric != other.roundoffmetric)
			return false;
		if (roundoffmetriclength != other.roundoffmetriclength)
			return false;
		if (rtr != other.rtr)
			return false;
		if (rtr_order_sequence != other.rtr_order_sequence)
			return false;
		if (selectallmetric != other.selectallmetric)
			return false;
		if (siteId == null) {
			if (other.siteId != null)
				return false;
		} else if (!siteId.equals(other.siteId))
			return false;
		if (siteId_clickable != other.siteId_clickable)
			return false;
		if (siteId_just_filter != other.siteId_just_filter)
			return false;
		if (site_hygiene == null) {
			if (other.site_hygiene != null)
				return false;
		} else if (!site_hygiene.equals(other.site_hygiene))
			return false;
		if (site_hygiene_clickable != other.site_hygiene_clickable)
			return false;
		if (start_time_str == null) {
			if (other.start_time_str != null)
				return false;
		} else if (!start_time_str.equals(other.start_time_str))
			return false;
		if (startindex != other.startindex)
			return false;
		if (stateId == null) {
			if (other.stateId != null)
				return false;
		} else if (!stateId.equals(other.stateId))
			return false;
		if (stateId_clickable != other.stateId_clickable)
			return false;
		if (supplyCost != other.supplyCost)
			return false;
		if (supplyCost_order_sequence != other.supplyCost_order_sequence)
			return false;
		if (supply_source_type == null) {
			if (other.supply_source_type != null)
				return false;
		} else if (!supply_source_type.equals(other.supply_source_type))
			return false;
		if (supply_source_type_clickable != other.supply_source_type_clickable)
			return false;
		if (timezone == null) {
			if (other.timezone != null)
				return false;
		} else if (!timezone.equals(other.timezone))
			return false;
		if (top_n != other.top_n)
			return false;
		if (top_n_for_last_x_hours != other.top_n_for_last_x_hours)
			return false;
		if (total_bidValue != other.total_bidValue)
			return false;
		if (total_bidValue_order_sequence != other.total_bidValue_order_sequence)
			return false;
		if (total_click != other.total_click)
			return false;
		if (total_click_order_sequence != other.total_click_order_sequence)
			return false;
		if (total_csc != other.total_csc)
			return false;
		if (total_csc_order_sequence != other.total_csc_order_sequence)
			return false;
		if (total_event_type != other.total_event_type)
			return false;
		if (total_event_type_order_sequence != other.total_event_type_order_sequence)
			return false;
		if (total_impression != other.total_impression)
			return false;
		if (total_impression_order_sequence != other.total_impression_order_sequence)
			return false;
		if (total_request != other.total_request)
			return false;
		if (total_request_order_sequence != other.total_request_order_sequence)
			return false;
		if (total_win != other.total_win)
			return false;
		if (total_win_bidValue != other.total_win_bidValue)
			return false;
		if (total_win_bidValue_order_sequence != other.total_win_bidValue_order_sequence)
			return false;
		if (total_win_order_sequence != other.total_win_order_sequence)
			return false;
		if (wtr != other.wtr)
			return false;
		if (wtr_order_sequence != other.wtr_order_sequence)
			return false;
		return true;
	}
    
 }
