
package models.entities.tracking_event;

import com.kritter.api.entity.tracking_event.TrackingEvent;
import com.kritter.constants.Frequency;


public class TrackingEventFormEntity extends TrackingEventBaseEntity{

	public TrackingEventFormEntity(TrackingEvent trackingEvent){
		super(trackingEvent);
	}
	
	public TrackingEventFormEntity(){
		super();
	}
	 
	public int getPagesize() {
		return trackingEvent.getPagesize();
	}

	public void setPagesize(int pagesize) {
		this.trackingEvent.setPagesize(pagesize);
	} 
	
	public int getStartindex() {
		return this.trackingEvent.getStartindex();
	}
 
	public void setStartindex(int startindex) {
	    if(startindex > 0){
	        this.trackingEvent.setStartindex( startindex -1);
	    }else{
	        this.trackingEvent.setStartindex(0);
	    }
	} 
    public String getPublishers() {
        return idListToString(trackingEvent.getPubId());
    }

    public void setPublishers(String publishers) { 
        trackingEvent.setPubId(stringToIdList(publishers));
    }
    public String getSites() {
        return idListToString(trackingEvent.getSiteId());
    }

    public void setSites(String sites) { 
        trackingEvent.setSiteId(stringToIdList(sites));
    } 
    public String getExtsites() {
        return idListToString(trackingEvent.getExtsiteId());
    }

    public void setExtsites(String extsites) { 
        trackingEvent.setExtsiteId(stringToIdList(extsites));
    } 

    public String getAdvertisers() {
        return idListToString(trackingEvent.getAdvId());
    }

    public void setAdvertisers(String advertisers) { 
        trackingEvent.setAdvId(stringToIdList(advertisers));
    } 
    public String getCampaigns() {
        return idListToString(trackingEvent.getCampaignId());
    }

    public void setCampaigns(String campaigns) { 
        trackingEvent.setCampaignId(stringToIdList(campaigns));
    }

    public String getAds() {
        return idListToString(trackingEvent.getAdId());
    }

    public void setAds(String ads) { 
        trackingEvent.setAdId(stringToIdList(ads));
    }

    public String getCountries() {
        return idListToString(trackingEvent.getCountryId());
    }

    public void setCountries(String countries) { 
        trackingEvent.setCountryId(stringToIdList(countries));
    }

    public String getCarriers() {
        return idListToString(trackingEvent.getCountryCarrierId());
    }

    public void setCarriers(String carriers) { 
        trackingEvent.setCountryCarrierId(stringToIdList(carriers));
    }
    
    public String getBrands() {
        return idListToString(trackingEvent.getDeviceManufacturerId());
    }

    public void setBrands(String brands) { 
        trackingEvent.setDeviceManufacturerId(stringToIdList(brands));
    }
    public String getOs() {
        return idListToString(trackingEvent.getDeviceOsId());
    }

    public void setOs(String os) { 
        trackingEvent.setDeviceOsId(stringToIdList(os));
    }
    
	public TrackingEvent getReportEntity(){ 
		return trackingEvent;
	}

	public boolean getDate_as_dimension() {
        return trackingEvent.isDate_as_dimension();
    } 
	
    public void setDate_as_dimension(boolean date_as_dimension) {  
            trackingEvent.setDate_as_dimension(date_as_dimension); 
    }
    
    public String getFrequency(){
	    return trackingEvent.getFrequency().name();
	}
    
    public void setFrequency(String frequency) { 
        Frequency freq = Frequency.valueOf(frequency);
        if(freq != null){
            trackingEvent.setFrequency(freq);
        }
    }
    
    public String getCsvDelimiter() {
        return trackingEvent.getCsvDelimiter();
    }
    public void setCsvDelimiter(String csvDelimiter) {
        trackingEvent.setCsvDelimiter(csvDelimiter);
    }
    public boolean isTerminationReason() {
        return trackingEvent.isTerminationReason();
    }
    public void setTerminationReason(boolean terminationReason) {
        trackingEvent.setTerminationReason(terminationReason);
    }
    public boolean isTevent() {
        return trackingEvent.isTevent();
    }
    public void setTevent(boolean tevent) {
        trackingEvent.setTevent(tevent);
    }
    public boolean isTeventtype() {
        return trackingEvent.isTeventtype();
    }
    public void setTeventtype(boolean teventtype) {
        trackingEvent.setTeventtype(teventtype);
    }

}
