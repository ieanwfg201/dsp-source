package models.entities.extsitereport;

import com.kritter.api.entity.extsitereport.ExtSiteReportEntity;
import com.kritter.constants.Frequency;


public class ExtSiteReportFormEntity extends ExtSiteReportBaseEntity{

	public ExtSiteReportFormEntity(ExtSiteReportEntity extsitereportEntity){
		super(extsitereportEntity);
	}
	
	public ExtSiteReportFormEntity(){
		super();
	}
	 
	public int getPagesize() {
		return extsitereportEntity.getPagesize();
	}

	public void setPagesize(int pagesize) {
		this.extsitereportEntity.setPagesize(pagesize);
	} 
	
	public int getStartindex() {
		return this.extsitereportEntity.getStartindex();
	}
 
	public void setStartindex(int startindex) {
	    if(startindex > 0){
	        this.extsitereportEntity.setStartindex( startindex -1);
	    }else{
	        this.extsitereportEntity.setStartindex(0);
	    }
	} 
    public String getExchanges() {
        return idListToString(extsitereportEntity.getExchangeId());
    }

    public void setExchanges(String exchanges) { 
        extsitereportEntity.setExchangeId(stringToIdList(exchanges));
    } 

    public String getExtsites() {
        return idListToString(extsitereportEntity.getExt_site());
    }

    public void setExtsites(String extsites) { 
        extsitereportEntity.setExt_site(stringToIdList(extsites));
    } 
	public String getAdvertisers() {
		return stringIdListToString(extsitereportEntity.getAdvertiserId());
	}

	public void setAdvertisers(String advertisers) { 
		extsitereportEntity.setAdvertiserId(stringToStringIdList(advertisers));
	}

	public String getCampaigns() {
		return idListToString(extsitereportEntity.getCampaignId());
	}

	public void setCampaigns(String campaigns) { 
		extsitereportEntity.setCampaignId(stringToIdList(campaigns));
	}

	public String getAds() {
		return idListToString(extsitereportEntity.getAdId());
	}

	public void setAds(String ads) { 
		extsitereportEntity.setAdId(stringToIdList(ads));
	}


	public ExtSiteReportEntity getReportEntity(){ 
		return extsitereportEntity;
	}

	public boolean getDate_as_dimension() {
        return extsitereportEntity.isDate_as_dimension();
    } 
	
    public void setDate_as_dimension(boolean date_as_dimension) {  
            extsitereportEntity.setDate_as_dimension(date_as_dimension); 
    }
    
    public String getFrequency(){
	    return extsitereportEntity.getFrequency().name();
	}
    
    public void setFrequency(String frequency) { 
        Frequency freq = Frequency.valueOf(frequency);
        if(freq != null){
            extsitereportEntity.setFrequency(freq);
        }
    }
    public boolean isRoundoffmetric() {
        return extsitereportEntity.isRoundoffmetric();
    }
    public void setRoundoffmetric(boolean roundoffmetric) {
        extsitereportEntity.setRoundoffmetric(roundoffmetric);
    }

    public String getCsvDelimiter() {
        return extsitereportEntity.getCsvDelimiter();
    }
    public void setCsvDelimiter(String csvDelimiter) {
        extsitereportEntity.setCsvDelimiter(csvDelimiter);
    }
    public String getCountry() {
        return idListToString(extsitereportEntity.getCountryId());
    }
    
    public void setCountry(String country) { 
        extsitereportEntity.setCountryId(stringToIdList(country));
    }
}
