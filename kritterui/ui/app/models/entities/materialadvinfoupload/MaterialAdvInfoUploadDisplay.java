package models.entities.materialadvinfoupload;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import services.DataAPI;
import com.kritter.api.entity.account.Account;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadAdvInfo;

public class MaterialAdvInfoUploadDisplay extends EntityDisplay{
    
	protected Account account=null; 
	protected MaterialUploadAdvInfo entity = null;

	public MaterialAdvInfoUploadDisplay(MaterialUploadAdvInfo entity){
		this.entity = entity;
		this.account = DataAPI.getAccountById(entity.getPubIncId());
	}
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
	public String getExchangeName() {
		return this.account.getName();
	}
	public String getAdvName() {
		return this.entity.getAdvName();
	}
	public String getInfo() {
		return this.entity.getInfo();
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
		return entity.getInternalid();
	}


}
