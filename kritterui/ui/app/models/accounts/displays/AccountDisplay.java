package models.accounts.displays;

import java.util.ArrayList;

import models.Constants.Actions;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;

import com.kritter.api.entity.account.Account;
import com.kritter.constants.DemandPreference;
import com.kritter.constants.DemandType;
import com.kritter.constants.INVENTORY_SOURCE;
import com.kritter.constants.StatusIdEnum;

import controllers.routes;

public class AccountDisplay extends EntityDisplay{
	
	protected Account account; 
	
	public AccountDisplay(Account account){
		this.account = account;
	}
	public Account getAccount(){
	    return account;
	}
	public String getComment(){
        return account.getComment();
    }
	public String getAccountGuid(){
		return account.getGuid();
	}
	public String getAccountName(){
		if(account.getName() != null)
			return account.getName();
		else
			return "Untitled Account";
	}
	
	public String getCompanyName(){
		return account.getCompany_name();
	}
	
	public String getStatus(){
        return account.getStatus().getName();
	}
	public String getAction(){
		if(account.getStatus() == StatusIdEnum.Active)
			return   Actions.Deactivate.name() ;
		else
			return Actions.Activate.name() ;
	}
	public String getInvSrc(){
	    if(account.getInventory_source() == 0){
	        return "";
	    }
        return INVENTORY_SOURCE.getEnum((short)account.getInventory_source()).name();
    }
	public String getDemandtype(){
        return DemandType.getEnum(account.getDemandtype()).getName();
    }
	public String getDemandpreference(){
        return DemandPreference.getEnum(account.getDemandpreference()).getName();
    }
	public String statusUpdateUrl(){
		if(account.getStatus() == StatusIdEnum.Rejected)
			return routes.AccountsController.updateAccountStatus(account.getGuid(), "deactivate").url();
		else
			return routes.AccountsController.updateAccountStatus(account.getGuid(), "activate").url();
	}
	
	public String getViewUrl(){
		return "#";
	}
	
	public String getEditUrl(){
		return "#";
	}
	
	public ArrayList<Path> getBreadCrumbPaths() {
		ArrayList<Path> trails= new ArrayList<Path>(); 
		trails.add(new Path(getAccountName(), getViewUrl()));
		return trails;
	}

}
