package models.audience;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.audience.Audience;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import controllers.audience.routes;
import models.accounts.displays.AdvertiserDisplay;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import services.DataAPI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AudienceDisplay extends EntityDisplay{

	public Audience audience = null;
	private Account account;


	public AudienceDisplay(Audience audience){
		this.audience = audience;
		this.account = DataAPI.getAccountByGuid(audience.getAccount_guid());
	}

	 
	public String getName() {
		if( audience.getName() != null)
			return audience.getName();
		else
			return "Add Audience";
	}

	public String getCreatedOn() {  	
			Date date = new Date(audience.getCreated_on());
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm"); 
			return dt.format(date); 
	}

	public long getCreated_on() {
		return audience.getCreated_on();
	}

	public List<Path> getBreadCrumbPaths() {
		List<Path> paths = new AdvertiserDisplay(account).getBreadCrumbPaths();
		paths.add(new Path(getName(), "#"));
		return paths;
	}

 
	public String getAccountName() { 
		return account.getName();
	}

	public String getEditUrl() { 
		return routes.AudienceController.edit(audience.getId(),audience.getAccount_guid()).url();
	}
	
	public String getViewUrl() { 
		return routes.AudienceController.view(audience.getId(),audience.getAccount_guid()).url();
	}
	
	public String getAccountGuid() { 
		return account.getGuid();
	}


}
