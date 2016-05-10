package models.advertiser;

import java.util.List;

import models.uiutils.Path;

import com.kritter.api.entity.account.Account;

public abstract class ListDisplay extends EntityDisplay{
 
	protected Account account; 
	
	public ListDisplay(Account account){
		this.account = account;
	}
	public abstract List<Path> getBreadCrumbPaths();
 
	public String getAccountName() { 
		return account.getName();
	}
 
	public String getAccountGuid() { 
		return account.getGuid();
	}

}
