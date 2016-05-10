package models.advertiser;

import java.util.List;

import models.uiutils.Path;

public abstract class EntityDisplay {
	
	protected String destinationUrl = null;
	 
	public abstract String getAccountName();
	
	public abstract String getAccountGuid();
	
	public abstract List<Path> getBreadCrumbPaths();
	
	public void setDestination(String destination){
		this.destinationUrl = destination;
	}

}
