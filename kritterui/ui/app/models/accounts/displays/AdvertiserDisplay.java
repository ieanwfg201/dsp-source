package models.accounts.displays;

import java.util.ArrayList;

import scala.Option;
import models.uiutils.Path;

import com.kritter.api.entity.account.Account;
import com.kritter.constants.Account_Type;
import com.kritter.constants.StatusIdEnum;

import controllers.advertiser.routes;

public class AdvertiserDisplay extends AccountDisplay{
	
 
	public AdvertiserDisplay(Account account){
		super(account); 
	}
	
	public String getViewUrl(){
		return routes.AdvertiserController.home(account.getGuid()).url();
	}
	
	public String getEditUrl(){
		return controllers.routes.AccountsController.edit(account.getGuid()).url();
	}
	
	public String addCampaignUrl(){
		return routes.CampaignController.add(getAccountGuid()).url();
	}
	
	public String getStatUrl(){
           Option<String> option = Option.apply("/adv_id/"+account.getGuid());
           return controllers.reporting.routes.HierarchicalReportController.reportPage("adv", option).url();
    }

	
	public ArrayList<Path> getBreadCrumbPaths() {
		ArrayList<Path> trails= new ArrayList<Path>(); 
		trails.add(new Path("All Advertisers", 
				controllers.routes.AccountsController.accountListView(
							Account_Type.directadvertiser.getName(), 
							Option.apply(StatusIdEnum.Active.getName())).url()));
		trails.add(new Path(getAccountName(), getViewUrl()));
		
		return trails;
	}

}
