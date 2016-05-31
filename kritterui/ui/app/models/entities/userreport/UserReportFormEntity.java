
package models.entities.userreport;

import com.kritter.constants.Frequency;
import com.kritter.entity.userreports.UserReport;


public class UserReportFormEntity extends UserReportBaseEntity{

	public UserReportFormEntity(UserReport userReport){
		super(userReport);
	}
	
	public UserReportFormEntity(){
		super();
	}
	 
	public int getPagesize() {
		return userReport.getPagesize();
	}

	public void setPagesize(int pagesize) {
		this.userReport.setPagesize(pagesize);
	} 
	
	public int getStartindex() {
		return this.userReport.getStartindex();
	}
 
	public void setStartindex(int startindex) {
	    if(startindex > 0){
	        this.userReport.setStartindex( startindex -1);
	    }else{
	        this.userReport.setStartindex(0);
	    }
	} 
    public String getAdvertisers() {
        return idListToString(userReport.getAdvId());
    }

    public void setAdvertisers(String advertisers) { 
        userReport.setAdvId(stringToIdList(advertisers));
    } 
    public String getCampaigns() {
        return idListToString(userReport.getCampaignId());
    }

    public void setCampaigns(String campaigns) { 
        userReport.setCampaignId(stringToIdList(campaigns));
    }

    public String getAds() {
        return idListToString(userReport.getAdId());
    }

    public void setAds(String ads) { 
        userReport.setAdId(stringToIdList(ads));
    }

	public UserReport getReportEntity(){ 
		return userReport;
	}

	public boolean getDate_as_dimension() {
        return userReport.isDate_as_dimension();
    } 
	
    public void setDate_as_dimension(boolean date_as_dimension) {  
        userReport.setDate_as_dimension(date_as_dimension); 
    }
    
    public String getFrequency(){
	    return userReport.getFrequency().name();
	}
    
    public void setFrequency(String frequency) { 
        Frequency freq = Frequency.valueOf(frequency);
        if(freq != null){
            userReport.setFrequency(freq);
        }
    }
    
    public String getCsvDelimiter() {
        return userReport.getCsvDelimiter();
    }
    public void setCsvDelimiter(String csvDelimiter) {
        userReport.setCsvDelimiter(csvDelimiter);
    }
}
