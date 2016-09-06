package models.entities.adpositionget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import services.DataAPI;
import com.kritter.api.entity.account.Account;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.entity.adxbasedexchanges_metadata.AdPositionGet;

public class AdpositionGetDisplay extends EntityDisplay{
    
	protected Account account=null; 
	protected AdPositionGet entity = null;

	public AdpositionGetDisplay(AdPositionGet entity){
		this.entity = entity;
		this.account = DataAPI.getAccountById(entity.getPubIncId());
	}
	/*'exchangeName',  '', 'adpositionGet','bannerUpload','videoUpload',  'action'*/
	public String getAdxstatus(){
		AdxBasedExchangesStates state = AdxBasedExchangesStates.getEnum(entity.getAdxbasedexhangesstatus());
		if(state != null){
			return state.getName();
		}
		return AdxBasedExchangesStates.READYTOSUBMIT.getName();
	}
	public String getMessage(){
		if(entity.getMessage() != null){
			return entity.getMessage();
		}
		return "";
	}
	public boolean isSubmit(){
		AdxBasedExchangesStates state = AdxBasedExchangesStates.getEnum(entity.getAdxbasedexhangesstatus());
		if(state != null  ){
			if(state==AdxBasedExchangesStates.READYTOSUBMIT){
				return true;
			}else{
				return false;
			}
		}
		return true;
		
	}
	public boolean isNew(){
		AdxBasedExchangesStates state = AdxBasedExchangesStates.getEnum(entity.getAdxbasedexhangesstatus());
		if(state != null  ){
			if(state==AdxBasedExchangesStates.BRINGINQUEUE){
				return true;
			}else{
				return false;
			}
		}
		return false;
		
	}
	public String getExchangeName() {
		return this.account.getName();
	}
	public String getLastmodified(){
		if(entity.getLast_modified()>0){
		Date date = new Date(entity.getLast_modified());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
		return dt.format(date);
		}
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
		return entity.getPubIncId();
	}


}
