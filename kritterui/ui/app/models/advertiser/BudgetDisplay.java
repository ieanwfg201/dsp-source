package models.advertiser;

import java.util.ArrayList;

import models.accounts.displays.AccountDisplay;
import models.accounts.displays.AdvertiserDisplay;
import models.uiutils.Path;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account_budget.Account_budget;

public class BudgetDisplay extends EntityDisplay{

	protected Account_budget budget = null; 
	protected Account account = null;

	public BudgetDisplay(Account_budget budget, Account account){
		this.budget = budget; 
		this.account = account;
	}

	public String getAccountGuid() {
		return budget.getAccount_guid();
	}

	public double getInternal_balance() {
		if(budget != null)
			return budget.getInternal_balance();
		else
			return 0.0;
	}

	public double getInternal_burn() {
		if(budget != null)
			return budget.getInternal_burn();
		else
			return 0.0;
		
	}

	public double getAdv_balance() {
		if(budget != null)
			return budget.getAdv_balance();
		else
			return 0.0;
		
	}

	public double getAdv_burn() {
		if(budget != null)
			return budget.getAdv_burn();
		else
			return 0.0;
		
	}

	public ArrayList<Path> getBreadCrumbPaths() { 
		AccountDisplay accountDisplay = new AdvertiserDisplay(account);
		ArrayList<Path>  paths = accountDisplay.getBreadCrumbPaths();
		paths.add(new Path("budget", "#"));
		return paths;
	}

	 
	public String getAccountName() {
		return account.getName();
	}


}
