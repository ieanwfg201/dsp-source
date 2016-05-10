package models.publisher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import play.Play;
import play.libs.Scala;
import models.accounts.displays.PublisherDisplay;
import models.advertiser.EntityDisplay;
import models.uiutils.Path;
import scala.Option;
import services.DataAPI;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.site.Site;
import com.kritter.constants.SITE_PASSBACK_CONTENT_TYPE;
import com.kritter.constants.SITE_PASSBACK_TYPE;
import com.kritter.constants.StatusIdEnum;

import controllers.publisher.routes;

public class SiteDisplay extends EntityDisplay{
    private static String adcode_url_domain = Play.application().configuration().getString("adcode_url_domain");
    private static String adcode_id = Play.application().configuration().getString("adcode_id");
    
	protected Site site = null;
	protected Account account  = null;

	public SiteDisplay(Site site){
		this.site = site;
		this.account  = DataAPI.getAccountByGuid(site.getPub_guid());
	}

	public int getId(){
		return site.getId();
	}
	
	public String getSiteUrl(){
		return site.getSite_url();
	}

	public String getName() {
		if(site != null && site.getName() !=null)
			return site.getName();
		else
			return "New Site";
	}

	public String getComment(){
		return site.getComments();
	}
	
	public String getAccountName(){		
		return account.getName();
	}
	
	public String getAccountGuid(){		
		return account.getGuid();
	}

	public String getStatus() {
		return StatusIdEnum.getEnum(site.getStatus_id()).name();
	}
	
	public String getCreatedOn(){		
		Date date = new Date(site.getCreated_on());
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		return dt.format(date);
	}
	
	public String getViewUrl(){
		return routes.SiteController.view(site.getId(), Scala.Option(destinationUrl)).url();
	}
	
	public String getEditUrl() { 
			return routes.SiteController.edit(site.getId(), Scala.Option(destinationUrl)).url(); 
	}
	public String getStatUrl(){
	        Option<String> option = Option.apply("/pub_id/"+site.getPub_id()+"/site_id/"+site.getId());
	        return controllers.reporting.routes.HierarchicalReportController.reportPage("pub", option).url();
	}

	public String getAccountUrl(){
		return routes.PublisherController.info(site.getPub_guid()).url();
	}
		
	public String setDestinationUrl(){
		return "";
	}
	public String getFloor(){
        return ""+site.getFloor();
    }
    public String getPassbackType() {
        return SITE_PASSBACK_TYPE.getEnum((short)site.getPassback_type()).name();
    }
    public String getPassbackContentType() {
        return SITE_PASSBACK_CONTENT_TYPE.getEnum((short)site.getPassback_content_type()).name();
    }
    public String getPassbackContent() {
        return site.getNofill_backup_content();
    }
    
    public String getRichMediaAllowed() {
        if(site.isIs_richmedia_allowed()){
            return "Yes";
        }
        return "No";
    }
    public String getAdCode(){
        String str = "<script type=\"text/javascript\" id=\""+adcode_id+"\"" +  
                " src=\""+adcode_url_domain+"/impjs/0/1/"+site.getGuid()+"\">"+
             "</script>";
             
        return str;

    }
	 
	@Override
	public ArrayList<Path> getBreadCrumbPaths() {
		PublisherDisplay publisherDisplay = new PublisherDisplay(account);
		ArrayList<Path> trails= new ArrayList<Path>(); 
		trails.add(new Path(account.getName(), publisherDisplay.getViewUrl()));
		trails.add(new Path(getName(), getViewUrl()));
		return trails;
	}
	

}
