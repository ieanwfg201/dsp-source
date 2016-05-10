package models.advertiser;

import java.util.List;

import scala.Option;
import models.accounts.displays.AdvertiserDisplay;
import models.uiutils.Path;

import com.kritter.api.entity.account.Account;

import controllers.advertiser.routes;

public class TargetingListDisplay extends ListDisplay {
 
	public String getName(){
		return "Targeting Profiles";
	}
	
	public String getPath(){
		return routes.TargetingProfileController.list(account.getGuid()).url();
	}
	public TargetingListDisplay(Account account) {
		super(account); 
	}

	public List<Path> getBreadCrumbPaths() { 
		List<Path> paths = new AdvertiserDisplay(account).getBreadCrumbPaths();
		paths.add(new Path(getName(), getPath()));
		return paths;
	}
	
	public String addTargetingUrl(){
		Option<String> none = Option.empty();
		return routes.TargetingProfileController.add(account.getGuid(), none).url();
	}

}
