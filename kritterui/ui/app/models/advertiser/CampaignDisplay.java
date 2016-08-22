package models.advertiser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import models.accounts.displays.AccountDisplay;
import models.accounts.displays.AdvertiserDisplay;
import models.uiutils.Path;
import scala.Option;
import services.DataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.constants.StatusIdEnum;

import controllers.advertiser.routes;

public class CampaignDisplay extends EntityDisplay{

	Campaign campaign = null;
	Campaign_budget budget = null;
	Account account = null;

	public CampaignDisplay(Campaign campaign){ 
		this.campaign = campaign;
		if(campaign.getId() !=-1){
			this.budget = DataAPI.getCampaignBudget(campaign.getId()); 
		}
		if(campaign.getAccount_guid() != null){ 
			this.account = DataAPI.getAccountByGuid(campaign.getAccount_guid());
		}
	}


	public String getName(){
		if(campaign.getName() != null)
			return campaign.getName();
		else
			return "New Campaign";
	}

	public int getId(){
		return campaign.getId();
	}
	
	public String getGuid(){
		return campaign.getGuid();
	}

	public String getAccountName() {
		return account.getName();
	}

	public String getAccountGuid(){
		return campaign.getAccount_guid();
	}

	public String getCreatedDate(){		
		Date date = new Date(campaign.getCreated_on());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		return dt.format(date);
	}


	public String getStatus(){
		return StatusIdEnum.getEnum(campaign.getStatus_id()).name();
	}
	public String getCampaignGuid(){  
		return campaign.getName(); 
	}


	public String getStartDate(){
		Date date = new Date(campaign.getStart_date());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm"); 
		return dt.format(date);
	}

	public String getEndDate(){
		Date date = new Date(campaign.getEnd_date());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm"); 
		return dt.format(date);
	}
	
	public boolean isStartEnabled(){ 
		if(campaign.getStatus_id() == 1  || campaign.getStatus_id() == 10  ||campaign.getStatus_id() == 11  ||campaign.getStatus_id() == 12 )
				return false;
		return true;
	}
	
	public boolean isPauseEnabled(){ 
		if(campaign.getStatus_id() == 1 )
				return true;
		return false;
	}
	
	public Campaign_budget getBudget(){
		return budget;
	}

	public String getViewUrl(){
		return routes.CampaignController.view(campaign.getId()).url();
	}

	public String getEditUrl(){
		return routes.CampaignController.edit(campaign.getId()).url();
	}
	
	public String getAddbudgetUrl(){
        return routes.CampaignController.addbudget(campaign.getId()).url();
    }

	public String getAccountUrl(){
		return routes.AdvertiserController.home(campaign.getAccount_guid()).url();
	}
	
	public String getStatUrl(){
	    Option<String> option = Option.apply("/adv_id/"+campaign.getAccount_guid()+"/campaign_id/"+campaign.getId());
	    return controllers.reporting.routes.HierarchicalReportController.reportPage("adv", option).url();
    }

	public  String adCreateUrl(){
		Option<String> none = Option.empty();
		return routes.AdController.createAd(campaign.getId(), none, none, none).url();
	}
	public String getFreqCapDisplay(){
		if(campaign.isIs_frequency_capped()){
			StringBuffer sbuff = new StringBuffer("");
			if(campaign.isClick_freq_cap()){
				sbuff.append("CLICK -> FreqPeruser: ");
				sbuff.append(campaign.getClick_freq_cap_count());
				
				if(campaign.getClick_freq_cap_type() ==1){
					sbuff.append(" FreqType: Life ");
				}else{
					sbuff.append(" FreqType: ByHour Duration:");
					sbuff.append(campaign.getClick_freq_time_window());
				}
			}
			if(campaign.isImp_freq_cap()){
				sbuff.append(" Imp -> FreqPeruser: ");
				sbuff.append(campaign.getImp_freq_cap_count());
				
				if(campaign.getImp_freq_cap_type() ==1){
					sbuff.append(" FreqType: Life ");
				}else{
					sbuff.append(" FreqType: ByHour Duration:");
					sbuff.append(campaign.getImp_freq_time_window());
				}
			}
			return sbuff.toString();
		}
		return "Not Enabled";
	}
	public String isFreqCap(){
	    if(campaign.isIs_frequency_capped()){
	        return "True";
	    }
	    return "False";
	}


	public ArrayList<Path> getBreadCrumbPaths() { 
		AccountDisplay accountDisplay = new AdvertiserDisplay(account);
		ArrayList<Path>  paths = accountDisplay.getBreadCrumbPaths();
		paths.add(new Path(getName(), getViewUrl()));
		return paths;
	}
}
