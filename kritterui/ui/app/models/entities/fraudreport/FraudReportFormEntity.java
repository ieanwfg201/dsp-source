
package models.entities.fraudreport;

import com.kritter.api.entity.fraud.FraudReportEntity;
import com.kritter.constants.Frequency;


public class FraudReportFormEntity extends FraudReportBaseEntity{

	public FraudReportFormEntity(FraudReportEntity fraudReportEntity){
		super(fraudReportEntity);
	}
	
	public FraudReportFormEntity(){
		super();
	}
	 
	public int getPagesize() {
		return fraudReportEntity.getPagesize();
	}

	public void setPagesize(int pagesize) {
		this.fraudReportEntity.setPagesize(pagesize);
	} 
	
	public int getStartindex() {
		return this.fraudReportEntity.getStartindex();
	}
 
	public void setStartindex(int startindex) {
	    if(startindex > 0){
	        this.fraudReportEntity.setStartindex( startindex -1);
	    }else{
	        this.fraudReportEntity.setStartindex(0);
	    }
	} 
    public String getPublishers() {
        return idListToString(fraudReportEntity.getPubId());
    }

    public void setPublishers(String publishers) { 
        fraudReportEntity.setPubId(stringToIdList(publishers));
    }
    public String getSites() {
        return idListToString(fraudReportEntity.getSiteId());
    }

    public void setSites(String sites) { 
        fraudReportEntity.setSiteId(stringToIdList(sites));
    } 

	public String getAdvertisers() {
		return stringIdListToString(fraudReportEntity.getAdvertiserId());
	}

	public void setAdvertisers(String advertisers) { 
		fraudReportEntity.setAdvertiserId(stringToStringIdList(advertisers));
	}
    public String getEvent() {
        return stringIdListToString(fraudReportEntity.getEvent());
    }

    public void setEvent(String event) { 
        fraudReportEntity.setEvent(stringToStringIdList(event));
    }

	public String getCampaigns() {
		return idListToString(fraudReportEntity.getCampaignId());
	}

	public void setCampaigns(String campaigns) { 
		fraudReportEntity.setCampaignId(stringToIdList(campaigns));
	}

	public String getAds() {
		return idListToString(fraudReportEntity.getAdId());
	}

	public void setAds(String ads) { 
		fraudReportEntity.setAdId(stringToIdList(ads));
	}


	public FraudReportEntity getReportEntity(){ 
		return fraudReportEntity;
	}

	public boolean getDate_as_dimension() {
        return fraudReportEntity.isDate_as_dimension();
    } 
	
    public void setDate_as_dimension(boolean date_as_dimension) {  
            fraudReportEntity.setDate_as_dimension(date_as_dimension); 
    }
    
    public String getFrequency(){
	    return fraudReportEntity.getFrequency().name();
	}
    
    public void setFrequency(String frequency) { 
        Frequency freq = Frequency.valueOf(frequency);
        if(freq != null){
            fraudReportEntity.setFrequency(freq);
        }
    }
    
    public String getCsvDelimiter() {
        return fraudReportEntity.getCsvDelimiter();
    }
    public void setCsvDelimiter(String csvDelimiter) {
        fraudReportEntity.setCsvDelimiter(csvDelimiter);
    }


}
