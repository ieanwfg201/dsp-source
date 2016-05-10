package models.advertiser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import models.accounts.displays.AdvertiserDisplay;
import models.uiutils.Path;
import services.DataAPI;
import com.kritter.api.entity.account.Account;
import com.kritter.entity.retargeting_segment.RetargetingSegment;

public class RetargetingSegmentDisplay extends EntityDisplay{

    RetargetingSegment retargetingSegment = null;
	Account account = null;

	public RetargetingSegmentDisplay(RetargetingSegment retargetingSegment){ 
		this.retargetingSegment = retargetingSegment;
		if(retargetingSegment.getAccount_guid() != null){ 
			this.account = DataAPI.getAccountByGuid(retargetingSegment.getAccount_guid());
		}
	}


	public String getName(){
	    return retargetingSegment.getName();
	}

	public int getId(){
		return retargetingSegment.getRetargeting_segment_id();
	}
	
	public String getAccountGuid(){
		return retargetingSegment.getAccount_guid();
	}
	
	public String getTag(){
        return retargetingSegment.getTag();
    }
	
	public String getCreatedOn(){		
		Date date = new Date(retargetingSegment.getCreated_on());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:00:00"); 
		return dt.format(date);
	}

	/*
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
	}*/

	public ArrayList<Path> getBreadCrumbPaths() { 
        ArrayList<Path> paths = new AdvertiserDisplay(account).getBreadCrumbPaths();
       // paths.add(new Path(getName(), getPath()));
        return paths;
	}


    @Override
    public String getAccountName() {
        // TODO Auto-generated method stub
        return null;
    }






}
