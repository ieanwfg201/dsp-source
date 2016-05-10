package models.publisher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import scala.Option;
import services.DataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.ext_site.Ext_site;
import com.kritter.api.entity.site.Site;
import com.kritter.constants.StatusIdEnum;

import controllers.publisher.routes;

public class Ext_siteDisplay extends EntityDisplay{

	protected Ext_site extsite = null;

	public Ext_siteDisplay(Ext_site extsite){
		this.extsite = extsite;
	}

	public int getId(){
		return extsite.getId();
	}
	public String getExtSupplyDomain(){
        return extsite.getExt_supply_domain();
    }
    public String getExtSupplyId(){
        return extsite.getExt_supply_id();
    }
    public String getExtSupplyName(){
        return extsite.getExt_supply_name();
    }
    public String getLastModified(){
        Date date = new Date(extsite.getLast_modified());
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
        return dt.format(date);
    }
    public int getReq(){
        return extsite.getReq();
    }
    public int getOsId(){
        return extsite.getOsId();
    }
    public int getSiteInc(){
        return extsite.getSite_inc_id();
    }
    public int getSupplySourceType(){
        return extsite.getSupply_source_type();
    }
    public String getExtSupplyUrl(){
        return extsite.getExt_supply_url();
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

    @Override
    public List<Path> getBreadCrumbPaths() {
        // TODO Auto-generated method stub
        return null;
    }
	
	 
}
