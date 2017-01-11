package models.entities.audience;

import java.util.ArrayList;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import com.kritter.entity.audience_metadata.AudienceMetadata;


public class AudienceMetadataDisplay extends EntityDisplay{
    
	protected AudienceMetadata entity = null;

	public AudienceMetadataDisplay(AudienceMetadata entity){
		this.entity = entity;
	}
	public String getName() {
		return this.entity.getName();
	}
	public String getEnabled() {
		return this.entity.getEnabled()+"";
	}
	public String getDisabled() {
		return !this.entity.getEnabled()+"";
	}
	public String getBannerUrl() {
			return "";	
	}
	public String getEditUrl() { 
		return ""; 
}

	public String setDestinationUrl(){
		return "";
	}
	 
	@Override
	public ArrayList<Path> getBreadCrumbPaths() {
		ArrayList<Path> trails= new ArrayList<Path>(); 
		return trails;
	}
	@Override
	public String getAccountName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getAccountGuid() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getId(){
		return entity.getInternalid();
	}


}
