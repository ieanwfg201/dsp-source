package models.advertiser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import play.libs.Scala;
import models.uiutils.Path;
import scala.Option;
import services.DataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.constants.StatusIdEnum;

import controllers.advertiser.CampaignController;
import controllers.advertiser.routes;

public class AdDisplay extends EntityDisplay{
	
	protected Ad ad = null;
	protected Campaign campaign = null;
	protected Account account = null;
	
	public AdDisplay(Ad ad){
		this.ad = ad;
		campaign =CampaignController.getCampaign(ad.getCampaign_id());		
	}
	
	public Account getAccount(){
		if(account == null )
			account = DataAPI.getAccountByGuid(campaign.getAccount_guid());
		return account;
	}
	public int getId(){
		return ad.getId();
	}
	public String getGuid(){
        return ad.getGuid();
    }
	public String getName(){
		if(ad.getName() != null)
			return ad.getName();
		else
			return "Untitled Ad";
	}
	
	public String getAccountName(){ 
		return getAccount().getName();
	}
	
	public String getAccountGuid(){ 
		return getAccount().getGuid();
	}
	
	
	public String getCampaign(){
		return campaign.getName();
	}
	
	public String getComment(){
		return ad.getComment();
	}
	public String isFreqCap(){
	    if(ad.isIs_frequency_capped()){
	        return "True";
	    }
	    return "False";
	}
	
	public int getFreqCap(){
	    return ad.getFrequency_cap();
	}
	
	public int getTimeWindow(){
	    return ad.getTime_window();
	}
	public String getCreatedOn(){		
		Date date = new Date(ad.getCreated_on());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		return dt.format(date);
	}
	
	
	public String getStatus(){
		return ad.getStatus_id().getName();
	}
	public String getTpname(){
        return ad.getTargeting_profile_name();
    }
	 
	public boolean isActive(){
		if(ad.getStatus_id()== StatusIdEnum.Active)
			return true;
		else
			return false;			
	}
	
	public boolean isPaused(){
		if(ad.getStatus_id()== StatusIdEnum.Paused || ad.getStatus_id()== StatusIdEnum.Approved)
			return true;
		else
			return false;			
	}
	public String getViewUrl(){
		return routes.AdController.view(ad.getId(), Scala.Option(destinationUrl)).url();
	}
	
	public String getEditUrl(){ 
			return routes.AdController.edit(ad.getId(), Scala.Option(destinationUrl)).url(); 
	}
    public String getStatUrl(){ 
        Option<String> option = Option.apply("/adv_id/"+account.getGuid()+"/ad_id/"+ad.getId());
        return controllers.reporting.routes.HierarchicalReportController.reportPage("adv", option).url();
}
	
	public String getCloneUrl(){ 
		return routes.AdController.cloneAd(ad.getId()).url(); 
}
	
	public String getCampaignUrl(){
		return routes.CampaignController.view(campaign.getId()).url();
	}
	
	public String getAccountUrl(){
		return routes.AdvertiserController.info(campaign.getAccount_guid()).url();
	}
	
	public String getCreativePreviewUrl(){
		return routes.CreativeController.viewCreative(ad.getCreative_id()).url();
	}
	
	public String getTargetingPreviewUrl(){
		return routes.TargetingProfileController.view(ad.getTargeting_guid(), getAccountGuid()).url();
	}
	public String getLandingUrl(){
        return ad.getLanding_url();
    }
//	
//	public String getNewCreativeUrl(){
//		return routes.CreativeController.addCreative(getAccount().getGuid()).url();
//	}
//	
//	public String getNewTargetingUrl(){
//		return routes.TargetingProfileController.add(getAccount().getGuid()).url();
//	}
 
	public List<Path> getBreadCrumbPaths() {
		List<Path> paths = new CampaignDisplay(campaign).getBreadCrumbPaths();
		paths.add(new Path(getName(), getCreativePreviewUrl()));
		return paths;
	} 	
	
	
 	 	 
}
