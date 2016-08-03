package models.entities.ad_stats;

import com.kritter.constants.Frequency;
import com.kritter.entity.ad_stats.AdStats;


public class AdStatsFormEntity extends AdStatsBaseEntity{

	public AdStatsFormEntity(AdStats adstatsEntity){
		super(adstatsEntity);
	}
	
	public AdStatsFormEntity(){
		super();
	}
	 
	public int getPagesize() {
		return adstatsEntity.getPagesize();
	}

	public void setPagesize(int pagesize) {
		this.adstatsEntity.setPagesize(pagesize);
	} 
	
	public int getStartindex() {
		return this.adstatsEntity.getStartindex();
	}
 
	public void setStartindex(int startindex) {
	    if(startindex > 0){
	        this.adstatsEntity.setStartindex( startindex -1);
	    }else{
	        this.adstatsEntity.setStartindex(0);
	    }
	} 
	public String getAdvertisers() {
		return idListToString(adstatsEntity.getAdvId());
	}

	public void setAdvertisers(String advertisers) { 
		adstatsEntity.setAdvId(stringToIdList(advertisers));
	}

	public String getCampaigns() {
		return idListToString(adstatsEntity.getCampaignId());
	}

	public void setCampaigns(String campaigns) { 
		adstatsEntity.setCampaignId(stringToIdList(campaigns));
	}

	public String getAds() {
		return idListToString(adstatsEntity.getAdId());
	}

	public void setAds(String ads) { 
		adstatsEntity.setAdId(stringToIdList(ads));
	}


	public AdStats getReportEntity(){ 
		return adstatsEntity;
	}

	public boolean getDate_as_dimension() {
        return adstatsEntity.isDate_as_dimension();
    } 
	
    public void setDate_as_dimension(boolean date_as_dimension) {  
            adstatsEntity.setDate_as_dimension(date_as_dimension); 
    }
    
    public String getFrequency(){
	    return adstatsEntity.getFrequency().name();
	}
    
    public void setFrequency(String frequency) { 
        Frequency freq = Frequency.valueOf(frequency);
        if(freq != null){
            adstatsEntity.setFrequency(freq);
        }
    }
    public String getCsvDelimiter() {
        return adstatsEntity.getCsvDelimiter();
    }
    public void setCsvDelimiter(String csvDelimiter) {
        adstatsEntity.setCsvDelimiter(csvDelimiter);
    }
}
