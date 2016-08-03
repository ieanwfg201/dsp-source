package models.advertiser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.uiutils.Path;
import services.DataAPI;
import services.MetadataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.metadata.MetaField;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.VideoDemandType;

import controllers.advertiser.routes;


public class CreativeDisplay extends EntityDisplay{

	protected Creative_container cc = null;
	protected Account account = null;
	protected MetaField format  = null;

	public CreativeDisplay(Creative_container cc){
		this.cc = cc;
		account = DataAPI.getAccountByGuid(cc.getAccount_guid());
	}

	public String getAccountName(){
		return account.getName();
	}

	public String getText(){
		return cc.getText();
	}

	public boolean isTextFormat(){
		return cc.getFormat_id()==1;
	}

	public boolean isBannerFormat(){
		return cc.getFormat_id()==2;
	}

	public boolean isNativeFormat(){
	    return cc.getFormat_id()==51;
	}
    public boolean isVideoFormat(){
        return cc.getFormat_id()==4;
    }
    public boolean isDirectVideoFormat(){
        if(cc.getFormat_id()==4){
        	if(cc.getVideoDemandType() ==2){
        		return true;
        	}
        }
        return false;
    }

	public boolean isRichMediaFormat(){
		return cc.getFormat_id()==3;
	}

	public String getFormat(){
		if(format == null)
			format = MetadataAPI.getFormat(cc.getFormat_id());
		return format.getName();
	}
	public String getHtmlDisplay(){
		return cc.getHtml_content();
	} 
	 
	public String getName(){
		return cc.getLabel();
	}

	public String getCreatedOn(){
		Date date = new Date(cc.getCreated_on());
		SimpleDateFormat dt = new SimpleDateFormat("dd.mm.yyyy"); 
		return dt.format(date);  
	}

	public String getViewUrl(){
		return routes.CreativeController.viewCreative(cc.getId()).url();
	}

	public String getEditUrl(){
		return routes.CreativeController.editCreative(cc.getId()).url();
	}

	public String getAccountUrl(){
		return routes.AdvertiserController.home(cc.getAccount_guid()).url();
	}

	public String getAccountGuid() { 
		return account.getGuid();
	}

	public String getStatus(){
	    return cc.getStatus_id().getName();
	}

    public int getId(){
        return cc.getId();
    }
    
	public List<Path> getBreadCrumbPaths() {
		List<Path> paths = new CreativeListDisplay(account).getBreadCrumbPaths();
		paths.add(new Path(getName(), getViewUrl()));
		return paths; 
	}
}
