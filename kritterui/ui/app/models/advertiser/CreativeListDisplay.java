package models.advertiser;

import java.util.List;

import scala.Option;
import models.accounts.displays.AdvertiserDisplay;
import models.uiutils.Path;

import com.kritter.api.entity.account.Account;

import controllers.advertiser.routes;

public class CreativeListDisplay extends ListDisplay {

	public CreativeListDisplay(Account account) {
		super(account); 
	}
  
 
	public String getName(){
		return "Creatives";
	}
	
	public String getPath(){
		return routes.CreativeController.listView(account.getGuid()).url();
	}
	 

	public List<Path> getBreadCrumbPaths() { 
		List<Path> paths = new AdvertiserDisplay(account).getBreadCrumbPaths();
		paths.add(new Path(getName(), getPath()));
		return paths;
	}
	
	public String addCreativeUrl(){
		Option<String> none = Option.empty();
		return routes.CreativeController.addCreative(account.getGuid(), none).url();
	}

}
