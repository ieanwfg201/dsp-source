package controllers.advertiser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.accounts.displays.AdvertiserDisplay;
import models.advertiser.BudgetDisplay;
import models.formbinders.MultiCampaignForm;
import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import services.DataAPI;
import services.MetadataAPI;
import views.html.advt.advertiserHome;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account_budget.Account_budget;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.CampaignQueryEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;

public class AdvertiserController extends Controller{

	static Form<MultiCampaignForm> mcfTemplate = Form.form(MultiCampaignForm.class);  

	private static List<Campaign> getCampaigns(String accountGuid, Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Campaign> campaigns = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			CampaignListEntity cle = new CampaignListEntity();
			if(status.nonEmpty()){
			    StatusIdEnum  valueEnum = StatusIdEnum.valueOf(status.get());
			    if(StatusIdEnum.Expired == valueEnum){
			        cle.setCampaignQueryEnum(CampaignQueryEnum.list_all_expired_campaign_of_account);
			    }else{
			        cle.setCampaignQueryEnum(CampaignQueryEnum.list_non_expired_campaign_by_status);
			    }
				cle.setStatusIdEnum(valueEnum);
			}else{
				cle.setCampaignQueryEnum(CampaignQueryEnum.list_all_non_expired_campaign_of_account);
			} 

			cle.setAccount_guid(accountGuid);

			if(pageNo.nonEmpty())
				cle.setPage_no(pageNo.get()-1);
			if(pageSize.nonEmpty())
				cle.setPage_size(pageSize.get());
			CampaignList cl = ApiDef.list_campaign(con,cle);
			if(cl.getMsg().getError_code()==0){
				if( cl.getCampaign_list().size()>0){
					campaigns=  cl.getCampaign_list();
				}
			}else{
				campaigns = new ArrayList<Campaign>();
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching campaign list",e);
		}
		finally{
			try {
                if(con !=null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return campaigns;
	}

	public static Result campaigns(String accountGuid, Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Campaign> clist = null; 
		clist = getCampaigns(accountGuid, status, pageNo, pageSize);


		ObjectNode result = Json.newObject();

		ArrayNode cnodes = result.putArray("list");
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode cnode = null;
		for (Campaign campaign : clist) { 
			cnode= objectMapper.valueToTree(campaign);
			cnode.put("edit_url", routes.CampaignController.edit( campaign.getId()).url());
			cnode.put("view_url", routes.CampaignController.view( campaign.getId()).url());
			cnodes.addPOJO(cnode);
		}
		result.put("size", clist.size());
		result.put("statusMap", MetadataAPI.campaignStatuses());

		return ok(result);

	}
		
	public static Result budget(String guid){
		Account_budget ab = DataAPI.getAccountBudget(guid);
		Account account = DataAPI.getAccountByGuid(guid);
		BudgetDisplay bd = new BudgetDisplay(ab, account);
		return ok(views.html.advt.budgetView.render(bd, guid));
	}
	
	public static Result campaignWorkflowForm(String accountGuid, String action, Option<String> ids){

		MultiCampaignForm mcf = new MultiCampaignForm();
		mcf.setAccountGuid(accountGuid);
		mcf.setAction(action);
		if(ids.nonEmpty())
			mcf.setIds(ids.get());

		return ok(views.html.advt.campaign.multiCampaignForm.render(mcfTemplate.fill(mcf)));
	}

	public static Result updateCampaigns(){
		Form<MultiCampaignForm> filledForm = mcfTemplate.bindFromRequest();
		if(!filledForm.hasErrors()){
			MultiCampaignForm mcf = filledForm.get();
			String selectedIds  = mcf.getIds();
			String[] ids = selectedIds.split("_");
			Connection con = null;

			Campaign campaign = null;
			Message msg = null;
			try {
			    con = DB.getConnection();
				if("start".equalsIgnoreCase(mcf.getAction())){
					for (String id : ids) {
						campaign = CampaignController.getCampaign(Integer.parseInt(id.trim()));
						if(campaign !=null)
							msg = ApiDef.activate_campaign(con, campaign);
					}
				}else{
					for (String id : ids) {
						campaign = CampaignController.getCampaign(Integer.parseInt(id.trim()));
						if(campaign !=null)
							ApiDef.pause_campaign(con, campaign);
					}
				}
			} catch (Exception e) {
				 Logger.error(e.getMessage(),e);
			}
			finally{
				try {
					con.close();
				} catch (SQLException e) {
					Logger.error("Error closing DB connection while updating campaigns in CampaignController",e);
				}
			}
		}
		return ok("started campaigns");
	}

	
	public static Result home(String accountGuid){
		Account account = DataAPI.getAccountByGuid(accountGuid);
		return ok(advertiserHome.render(new AdvertiserDisplay(account)));
	}
	public static Result info(String accountGuid){
        Account account = DataAPI.getAccountByGuid(accountGuid);
        return ok(views.html.advt.advertiserInfo.render(new AdvertiserDisplay(account)));
    }
	public static Result campaignListView(String accountGuid, String status){
        Account account = DataAPI.getAccountByGuid(accountGuid);
        return ok(views.html.advt.expiredCampaign.render(new AdvertiserDisplay(account)));
    }
}
