package controllers.publisher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.accounts.displays.PublisherDisplay;
import models.formbinders.MultiCampaignForm;
import models.publisher.SiteDisplay;
import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import services.DataAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;

import controllers.routes;

public class PublisherController extends Controller{

	static Form<MultiCampaignForm> mcfTemplate = Form.form(MultiCampaignForm.class);  
	
	private static List<Site> getSites(String accountGuid, Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Site> sites = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			SiteListEntity siteListEntity = new SiteListEntity();
			siteListEntity.setPub_guid(accountGuid);
			if(status.nonEmpty()){ 
				siteListEntity.setStatus_id(StatusIdEnum.valueOf(status.get()).getCode());
			}  

			if(pageNo.nonEmpty())
				siteListEntity.setPage_no(pageNo.get()-1);
			if(pageSize.nonEmpty())
				siteListEntity.setPage_size(pageSize.get());
			SiteList siteList = ApiDef.list_site_by_account_guid(con,siteListEntity);
			if(siteList.getMsg().getError_code()==0){
				if( siteList.getSite_list().size()>0){
					sites=  siteList.getSite_list();
				}
			}else{
				sites = new ArrayList<Site>();
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of sites in PublisherController",e);
			}
		}
		return sites;
	}

	public static Result siteList(String accountGuid, Option<String> destination, Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Site> siteList = getSites(accountGuid, status, pageNo, pageSize);


		ObjectNode result = Json.newObject();
		ArrayNode cnodes = result.putArray("list");
		ObjectNode snode = null;
		ObjectMapper objectMapper = new ObjectMapper();
		SiteDisplay siteDisplay = null;
		
		String destinationUrl = null;
				
		if(destination.nonEmpty())
			destinationUrl = destination.get(); 
		
		for (Site site : siteList) { 
			siteDisplay = new SiteDisplay(site);
			if(destinationUrl != null)
				siteDisplay.setDestination(destinationUrl);
			snode= objectMapper.valueToTree(siteDisplay); 
			cnodes.addPOJO(snode);
		}
		result.put("size", siteList.size()); 

		return ok(result);

	}
	
	public static Result home(String accountGuid){
		Account account = DataAPI.getAccountByGuid(accountGuid);
		return ok(views.html.publisher.publisherHome.render(new PublisherDisplay(account)));
	}
    public static Result info(String accountGuid){
        Account account = DataAPI.getAccountByGuid(accountGuid);
        return ok(views.html.publisher.publisherInfo.render(new PublisherDisplay(account)));
    }
}
