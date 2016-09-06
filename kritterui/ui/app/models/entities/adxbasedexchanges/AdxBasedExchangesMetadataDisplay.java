package models.entities.adxbasedexchanges;

import java.util.ArrayList;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import services.DataAPI;
import services.MetadataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;

import controllers.adxbasedexchanges.routes;

public class AdxBasedExchangesMetadataDisplay extends EntityDisplay{
    
	protected Account account=null; 
	protected AdxBasedExchangesMetadata adxBasedMetadata = null;

	public AdxBasedExchangesMetadataDisplay(AdxBasedExchangesMetadata adxBasedMetadata){
		this.adxBasedMetadata = adxBasedMetadata;
		this.account = DataAPI.getAccountById(adxBasedMetadata.getPubIncId());
	}
	/*'exchangeName',  '', 'adpositionGet','bannerUpload','videoUpload',  'action'*/
	public String getAdvertiserUpload(){
		if(adxBasedMetadata.isAdvertiser_upload()){
			return "True";
		}
		return "False";
	}
	public String getAdpositionGet(){
		if(adxBasedMetadata.isAdposition_get()){
			return "True";
		}
		return "False";
	}
	public String getBannerUpload(){
		if(adxBasedMetadata.isBanner_upload()){
			return "True";
		}
		return "False";
	}
	public String getVideoUpload(){
		if(adxBasedMetadata.isVideo_upload()){
			return "True";
		}
		return "False";
	}
	public String getExchangeName() {
		return this.account.getName();
	}
	
	
	public String getEditUrl() { 
			return routes.CreateMetadataController.edit(adxBasedMetadata.getInternalid()).url(); 
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
	

}
