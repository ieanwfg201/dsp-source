package controllers.advertiser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.advertiser.AdDisplay;
import models.advertiser.CampaignDisplay;
import models.entities.CampaignBudgetEntity;
import models.entities.CampaignEntity;
import models.formbinders.MultiAdForm;

import org.springframework.beans.BeanUtils;

import play.Logger;
import play.data.Form;
import play.data.Form.Field;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.MetadataAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.CampaignQueryEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;

public class CampaignController extends Controller{

	static Form<CampaignEntity> campaignFormTemplate = Form.form(CampaignEntity.class); 
	static Form<MultiAdForm> mafTemplate = Form.form(MultiAdForm.class); 
	static Form<CampaignBudgetEntity> campaignBudgetFormTemplate = Form.form(CampaignBudgetEntity.class);

	public static List<Campaign> getCampaigns(String accountGuid, StatusIdEnum status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Campaign> campaigns = null;
		Connection con = null;
		try{
		    con = DB.getConnection(); 
			CampaignListEntity cle = new CampaignListEntity(); 
			cle.setCampaignQueryEnum(CampaignQueryEnum.list_non_expired_campaign_by_status);
			cle.setStatusIdEnum(status);
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
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection while fetching list of campaigns in CampaignController",e);
			}
		}
		return campaigns;
	}

	@SecuredAction
	public static Result campaignList(String accountGuid, Option<String> status, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Campaign> clist = null;
		if(status.nonEmpty()){
			clist = getCampaigns(accountGuid, StatusIdEnum.valueOf(status.get()), pageNo, pageSize);
		}else{
			clist = getCampaigns(accountGuid, StatusIdEnum.Active, pageNo, pageSize);
		}

		ObjectNode result = Json.newObject();

		ArrayNode cnodes = result.arrayNode();
		for (Campaign campaign : clist) {
			cnodes.addPOJO(campaign);
		}
		result.put("size", clist.size());
		return ok(result);

	}

	public static Campaign getCampaign(int id ){
		Connection con = null;
		Campaign campaign = null;
		try{
		    con = DB.getConnection();
			CampaignListEntity cle = new CampaignListEntity();
			cle.setCampaignQueryEnum(CampaignQueryEnum.get_campaign_by_id);
			cle.setCampaign_id(id);  
			CampaignList cl = ApiDef.list_campaign(con,cle);
			if(cl.getMsg().getError_code()==0){
				if( cl.getCampaign_list().size()>0){
					campaign = cl.getCampaign_list().get(0);
				}
			}
		}catch(Exception e){
			play.Logger.error(e.getMessage()+".Error fetching campaign with id = "+id,e);
		}
		finally{
			try {
                if(con != null){
				    con.close();
                }
			} catch (SQLException e) {
				Logger.error("Error closing DB connection in getCampaign in CampaignCOntroller",e);
			}
		}
		return campaign;
	}

	@SecuredAction
	public static Result ads(int campaign_id, Option<Integer> pageNo, Option<Integer> pageSize){
		List<Ad> adList = null; 
		adList = DataAPI.getAds(campaign_id, pageNo, pageSize) ;
		ObjectNode result = Json.newObject();
		if(adList != null){
		    ArrayNode cnodes = result.putArray("list");
		    ObjectMapper objectMapper = new ObjectMapper();
		    ObjectNode cnode = null;
		    for (Ad ad : adList) { 
			    cnode= objectMapper.valueToTree(new AdDisplay(ad)); 
			    cnodes.addPOJO(cnode);
		    }
		    result.put("size", adList.size());
		    result.put("statusMap", MetadataAPI.campaignStatuses());
		}
		return ok(result);

	}

	@SecuredAction
	public static Result add( String accountGuid){ 
		CampaignEntity campaign = new CampaignEntity(); 
		campaign.setAccount_guid(accountGuid);
		Date date = new Date();
		campaign.setStart_date(date.getTime());
		campaign.setEnd_date(date.getTime());
		CampaignBudgetEntity campaignBudget = new CampaignBudgetEntity(); 
		campaignBudget.setCampaign_guid("");
		campaignBudget.setCampaign_id(0);

		return ok(views.html.advt.campaign.campaignForm.render(campaignFormTemplate.fill(campaign), 
				campaignBudgetFormTemplate.fill(campaignBudget), new CampaignDisplay(campaign.getEntity())));
	}

	@SecuredAction
	public static Result edit( int campaignId){ 
		Campaign campaign = getCampaign(campaignId);;
		Campaign_budget campaignBudget = DataAPI.getCampaignBudget(campaign.getId());;

		CampaignEntity campaignEntity = new CampaignEntity(); 
		CampaignBudgetEntity campaignBudgetEntity = new CampaignBudgetEntity(); 
		
		BeanUtils.copyProperties(campaign, campaignEntity);
		BeanUtils.copyProperties(campaignBudget, campaignBudgetEntity);
		
		return ok(views.html.advt.campaign.campaignForm.render(campaignFormTemplate.fill(campaignEntity), 
				campaignBudgetFormTemplate.fill(campaignBudgetEntity),  new CampaignDisplay(campaign)));
	}

    @SecuredAction
    public static Result addbudget( int campaignId){ 
        Campaign campaign = getCampaign(campaignId);;
        Campaign_budget campaignBudget = DataAPI.getCampaignBudget(campaign.getId());;

        CampaignEntity campaignEntity = new CampaignEntity(); 
        CampaignBudgetEntity campaignBudgetEntity = new CampaignBudgetEntity(); 
        
        BeanUtils.copyProperties(campaign, campaignEntity);
        BeanUtils.copyProperties(campaignBudget, campaignBudgetEntity);
        
        return ok(views.html.advt.campaign.campaignaddbudgetForm.render(campaignFormTemplate.fill(campaignEntity), 
                campaignBudgetFormTemplate.fill(campaignBudgetEntity),  new CampaignDisplay(campaign)));
    }

	
	
	@SecuredAction
	public static Result view(int campaignId){ 
		Campaign campaign   = getCampaign(campaignId); 
		return ok(views.html.advt.campaign.campaignHome.render(new CampaignDisplay(campaign)));
	}

    @SecuredAction
    public static Result save(){
        return save(false);
    }
    
    @SecuredAction
    public static Result saveadbudget(){
        return save(true);
    }

	@SecuredAction
	public static Result save(boolean addbudget){

		Form<CampaignEntity> campaignForm = campaignFormTemplate.bindFromRequest();
		Form<CampaignBudgetEntity> campaignBudgetForm = campaignBudgetFormTemplate.bindFromRequest(); 


		Campaign campaign = new Campaign();
		Campaign_budget campaignBudget = null;
		CampaignEntity campaignEntity = new CampaignEntity();
		CampaignBudgetEntity campaignBudgetEntity = null;

		if(!campaignForm.hasErrors()){
			campaignEntity = campaignForm.get();
			campaignEntity.setModified_by(1);


			Message msg = null; 
			Connection con = null;
			
			try {
				con = DB.getConnection();
				campaign = campaignEntity.getEntity();
				if(campaign.getId()  == -1){
				    campaign.setStatus_id(StatusIdEnum.Paused.getCode());
					msg = ApiDef.insert_campaign(con, campaign);
				}else{
					msg = ApiDef.update_campaign(con, campaign);
				}
				campaign = getCampaign(campaign.getId());
				if(!campaignBudgetForm.hasErrors() && campaign !=null){
					campaignBudgetEntity = campaignBudgetForm.get();
					if(campaignBudgetEntity.getCampaign_id()<1){			
						campaignBudgetEntity.setCampaign_id(campaign.getId());
						campaignBudgetEntity.setCampaign_guid(campaign.getGuid());			
						campaignBudgetEntity.setModified_by(1);
						msg = ApiDef.insert_campaign_budget(con, campaignBudgetEntity.getEntity());
					}else{
						campaignBudget = campaignBudgetEntity.getEntity();
						Campaign_budget originalCampaignBudget  = DataAPI.getCampaignBudget(campaign.getId());
						if(addbudget){
						    originalCampaignBudget.setAdv_daily_budget(campaignBudget.getAdv_daily_budget()+campaignBudgetEntity.getAdd_to_daily_budget());
						    originalCampaignBudget.setAdv_total_budget(campaignBudget.getAdv_total_budget()+campaignBudgetEntity.getAdd_to_total_budget());
						    originalCampaignBudget.setInternal_daily_budget(campaignBudget.getInternal_daily_budget()+campaignBudgetEntity.getAdd_to_daily_budget());
						    originalCampaignBudget.setInternal_total_budget(campaignBudget.getInternal_total_budget()+campaignBudgetEntity.getAdd_to_total_budget());
						}else{
						    originalCampaignBudget.setAdv_daily_budget(campaignBudget.getAdv_daily_budget());
						    originalCampaignBudget.setAdv_total_budget(campaignBudget.getAdv_total_budget());
						    originalCampaignBudget.setInternal_daily_budget(campaignBudget.getInternal_daily_budget());
						    originalCampaignBudget.setInternal_total_budget(campaignBudget.getInternal_total_budget());
						}
						msg = ApiDef.update_campaign_budget(con, originalCampaignBudget);
					}

					if(msg.getError_code()==0){ 
						return redirect(controllers.advertiser.routes.AdvertiserController.home(campaign.getAccount_guid()));
					}
				}
			} catch (Exception e) {
				Logger.error("Error while updating campaign", e );
			}
			
			finally{
				try{
					if(con!=null){
						con.close();
					}
				}catch(Exception e){
					Logger.error("Error while closing DB connection", e );
				}
			}

		
		}
		Field field = campaignForm.field("id"); 
		if(field.value() != ""){
			campaign = new Campaign();
			campaign.setAccount_guid(campaignForm.field("account_guid").value());
		}else{
			campaign = getCampaign(Integer.parseInt(field.value()));
		}		
		return badRequest(views.html.advt.campaign.campaignForm.render(campaignForm, campaignBudgetForm,  new CampaignDisplay(campaign)));
	}


	@SecuredAction
	public static Result adWorkflowForm(String accountGuid, String action, Option<String> ids){

		MultiAdForm maf = new MultiAdForm();
		maf.setAccountGuid(accountGuid);
		maf.setAction(action);
		if(ids.nonEmpty())
			maf.setIds(ids.get());

		return ok(views.html.advt.campaign.multiAdForm.render(mafTemplate.fill(maf)));
	}

	@SecuredAction
	public static Result updateAds(){
		ObjectNode response = Json.newObject();
		Message msg = null;
		Form<MultiAdForm> filledForm = mafTemplate.bindFromRequest();
		
		if(!filledForm.hasErrors()){
			MultiAdForm maf = filledForm.get();
			String selectedIds  = maf.getIds();
			String[] ids = selectedIds.split("_");
			Connection con = null;

			Ad ad = null; 
			try {
				con = DB.getConnection();
				
				if("approve".equalsIgnoreCase(maf.getAction())){
					for (String id : ids) {
						ad = DataAPI.getAd(Integer.parseInt(id));
						if(ad !=null)
							msg = ApiDef.approve_ad(con, ad);
					}
				}else{
					for (String id : ids) {
						ad = DataAPI.getAd(Integer.parseInt(id));
						if(ad !=null)
							msg = ApiDef.reject_ad(con, ad);
					}
				}
			} catch (Exception e) {
					Logger.error(e.getMessage(),e);
			}
			finally{
				try {
					if(con != null)
						con.close();
				} catch (SQLException e) {
					Logger.error("Error closing DB connection in CampaignCOntroller",e);
				}
			} 
		}
		if(msg != null && msg.getError_code() == 0){
			return ok(response);
		}
		return badRequest(response);
	}
	
}
