package models.entities.materialbannerupload;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import services.DataAPI;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.StatusIdEnum;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadBanner;

public class MaterialBannerUploadDisplay extends EntityDisplay{
    
	protected Account account=null; 
	protected MaterialUploadBanner entity = null;

	public MaterialBannerUploadDisplay(MaterialUploadBanner entity){
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
	public String getExchangeName() {
		return this.account.getName();
	}
	public String getAdvName() {
		return this.entity.getAdvName();
	}
	public String getCampaignName() {
		return this.entity.getCampaignName();
	}
	public String getCampaignStatus() {
		if(this.entity.getCampaignStatus() != null && this.entity.getCampaignStatus() != -1){
			return StatusIdEnum.getEnum(this.entity.getCampaignStatus()).getName();
		}
		return "";
	}
	public String getAdName() {
		return this.entity.getAdName();
	}
	public String getAdStatus() {
		if(this.entity.getAdStatus() != null && this.entity.getAdStatus() != -1){
			return StatusIdEnum.getEnum(this.entity.getAdStatus()).getName();
		}
		return "";
	}
	public String getCreativeName() {
		return this.entity.getCreativeName();
	}
	public String getCreativeStatus() {
		if(this.entity.getCreativeStatus() != null && this.entity.getCreativeStatus() != -1){
			return StatusIdEnum.getEnum(this.entity.getCreativeStatus()).getName();
		}
		return "";
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

	public String getBannerUrl() {
		List<Creative_banner> cbList = DataAPI.getCreativeBannerList(this.entity.getBannerId()+"");
		if(cbList != null && cbList.size()==1){
			return "/images/banners/original"+cbList.get(0).getResource_uri();	
		}
		return "NF";
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
