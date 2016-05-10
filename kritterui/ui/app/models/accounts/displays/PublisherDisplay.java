package models.accounts.displays;

import java.util.ArrayList;

import models.uiutils.Path;
import scala.Option;

import com.kritter.api.entity.account.Account;
import com.kritter.constants.Account_Type;
import com.kritter.constants.StatusIdEnum;

import controllers.publisher.routes;

public class PublisherDisplay extends AccountDisplay{

	public PublisherDisplay(Account account) {
		super(account); 
	}

	@Override
	public String getViewUrl() { 
		return routes.PublisherController.home(account.getGuid()).url();
	}

	@Override
	public String getEditUrl() { 
		return controllers.routes.AccountsController.edit(account.getGuid()).url();
	}

	public String addSiteUrl(){
		return  routes.SiteController.add(getAccountGuid()).url();
	}
	
	public String getStatUrl(){
           Option<String> option = Option.apply("/pub_id/"+account.getId());
           return controllers.reporting.routes.HierarchicalReportController.reportPage("pub", option).url();
	}

	
	public ArrayList<Path> getBreadCrumbPaths() {
		ArrayList<Path> trails= new ArrayList<Path>(); 
		trails.add(new Path("All Publishers", 
				controllers.routes.AccountsController.accountListView(
							Account_Type.directpublisher.getName(), 
							Option.apply(StatusIdEnum.Active.getName())).url()));
		trails.add(new Path(getAccountName(), getViewUrl()));
		
		return trails;
	}


}

