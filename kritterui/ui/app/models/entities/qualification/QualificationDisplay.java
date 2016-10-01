package models.entities.qualification;

import java.util.ArrayList;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import services.DataAPI;
import com.kritter.api.entity.account.Account;
import com.kritter.constants.QualificationState;
import com.kritter.entity.account.Qualification;

import controllers.adxbasedexchanges.routes;


public class QualificationDisplay extends EntityDisplay{
    
	protected Account account=null; 
	protected Qualification entity = null;

	public QualificationDisplay(Qualification entity){
		this.entity = entity;
		this.account = DataAPI.getAccountById(entity.getAdvIncId());
	}
	public String getAdvName() {
		return this.account.getName();
	}
	public String getQName() {
		return this.entity.getQname();
	}
	public String getMd5() {
		return this.entity.getMd5();
	}
	public String getState() {
		if(this.entity.getState()!=null){
			QualificationState qs = QualificationState.getEnum(this.entity.getState());
			if(qs != null){
				return qs.getName();
			}
		}
		return "";
	}
	public String getBannerUrl() {
			return "/images/"+this.entity.getQurl();	
	}
	public String getEditUrl() { 
		return routes.QualificationController.edit(this.entity.getInternalid()).url(); 
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
