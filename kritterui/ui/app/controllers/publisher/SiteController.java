package controllers.publisher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import models.entities.SiteEntity;
import models.formbinders.SiteWorkFlowEntity;
import models.publisher.SiteDisplay;
import models.publisher.SiteDisplayFull;

import org.springframework.beans.BeanUtils;

import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.MetadataAPI;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.constants.PageConstants;
import com.kritter.constants.SiteAPIEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;

import controllers.advertiser.IOController.IO_ACTIONS;


public class SiteController extends Controller{

	static Form<SiteEntity> siteFormModel = Form.form(SiteEntity.class);
	static Form<SiteWorkFlowEntity> siteWorkflowForm = Form.form(SiteWorkFlowEntity.class);
	private static String show_opt_in_hygiene = Play.application().configuration().getString("show_opt_in_hygiene");
	private static String allow_passback = Play.application().configuration().getString("allow_passback");
	private static String show_adcode = Play.application().configuration().getString("show_adcode");
	private static String is_native_supply = Play.application().configuration().getString("is_native_supply");
	private static String video_supply = Play.application().configuration().getString("video_supply");

	public enum CRUD{
		create, edit, delete
	}

	private static Site getSite(int id){
		Connection con = null;
		Site site = null;
		try{
			
			con = DB.getConnection();
			SiteListEntity sle = new SiteListEntity();
			sle.setSite_int_id(id);
			SiteList sl = ApiDef.get_site(con,sle);
			if(sl.getMsg().getError_code()==0){
				if( sl.getSite_list().size()>0){
					site = sl.getSite_list().get(0);
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
		return site;
	}



	@SecuredAction
	public static Result add(String  guid) { 
		Account account = DataAPI.getAccountByGuid(guid);
		Site  site =   new Site();
		site.setPub_guid(guid); 
		site.setPub_id(account.getId());
		SiteEntity siteEntity = new SiteEntity();
		BeanUtils.copyProperties(site, siteEntity);
		return ok(views.html.publisher.siteForm.render(siteFormModel.fill(siteEntity), new SiteDisplay(site), show_opt_in_hygiene, allow_passback,is_native_supply,video_supply));
	}

	@SecuredAction
	public static Result edit(int id, Option<String> destination) { 
		Site  site =   getSite(id);
		SiteEntity siteEntity = new SiteEntity();
		if(destination.nonEmpty())
			siteEntity.setDestination(destination.get());
		BeanUtils.copyProperties(site, siteEntity);
		return ok(views.html.publisher.siteForm.render( siteFormModel.fill(siteEntity), new SiteDisplay(site), show_opt_in_hygiene, allow_passback,is_native_supply,video_supply));
	}

	@SecuredAction
	public static Result view(int id,  Option<String> destination) { 
		Site  site =   getSite(id); 
		SiteDisplayFull siteDisplay = new SiteDisplayFull(site);
		if(destination.nonEmpty())
			siteDisplay.setDestination(destination.get());
		return ok(views.html.publisher.siteHome.render( siteDisplay, show_opt_in_hygiene, allow_passback, show_adcode, is_native_supply,video_supply));
	}



	@SecuredAction
	public static Result list(String status ) { 
		Connection con = null;
        try{
		    con = DB.getConnection();
		    SiteListEntity siteListEntity = new SiteListEntity();
    		siteListEntity.setPage_no(PageConstants.start_index);   
            siteListEntity.setStatus_id(StatusIdEnum.valueOf(status).getCode()); 
		    siteListEntity.setPage_size(PageConstants.page_size);
		    SiteList siteListStatus = ApiDef.list_site(con, siteListEntity);
		    if(siteListStatus.getMsg().getError_code()==0){
			    List<Site> siteList = siteListStatus.getSite_list();
			    return ok(views.html.publisher.siteList.render(StatusIdEnum.Pending.toString(),siteList));  
		    }else if(siteListStatus.getMsg().getError_code()==25){
			    return badRequest("Error loading site list");
		    }else{
			    return badRequest("Error loading site list");
		    }
        }catch(Exception e){
            play.Logger.error(e.getMessage(),e);
            return badRequest("Error loading site list");
        }
        finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing DB connection ",e);
            }
        }
	}

	@SecuredAction
	public static Result workflowForm(int id, String action  ) {   
		SiteWorkFlowEntity swe = new SiteWorkFlowEntity(id, action); 
		return ok(views.html.publisher.siteWorkflowForm.render(siteWorkflowForm.fill(swe),action));
	}

	@SecuredAction
	public static Result updateSite() { 
		Form<SiteWorkFlowEntity> siteWfEntityForm1 = siteWorkflowForm.bindFromRequest();
		Connection con = null;
        ObjectNode response = Json.newObject();
        try{
		    Message statusMsg = null;
    		if(!siteWfEntityForm1.hasErrors()){
		    	con = DB.getConnection();
	    		SiteWorkFlowEntity siteWfEntity = siteWfEntityForm1.get();
    			Site site = getSite(siteWfEntity.getSiteId()); 
			    site.setModified_by(1);
		    	if(IO_ACTIONS.valueOf(siteWfEntity.getAction())==IO_ACTIONS.approve){ 
	    			statusMsg = ApiDef.approve_site(con, site);				 
    			}else if(IO_ACTIONS.valueOf(siteWfEntity.getAction())==IO_ACTIONS.reject){  
				    site.setComments(siteWfEntity.getComment());  
			    	site.setStatus_id(StatusIdEnum.Rejected.getCode());
		    		statusMsg = ApiDef.reject_site(con, site);
	    		}else if(IO_ACTIONS.valueOf(siteWfEntity.getAction())==IO_ACTIONS.pause){  
                    site.setComments(siteWfEntity.getComment());  
                    site.setStatus_id(StatusIdEnum.Paused.getCode());
                    statusMsg = ApiDef.pause_site(con, site);
                }
    			if(statusMsg.getError_code()==0){
			    	response.put("message", "Update Successful");
		    		return ok(response);
	    		}else{
    				response.put("message", "Update Failed. Please retry");
				    return badRequest(response);
			    }
		    }
		    response.put("message", "Update Failed. Please retry");
		    return badRequest(response);
        }catch(Exception e){
            play.Logger.error(e.getMessage(),e);
            response.put("message", "Update Failed. Please retry");
            return badRequest(response);
        }
        finally{
            try {
                if(con!=null){
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing ",e);
            }
        }
	}
    @SecuredAction
    public static Result updateMultipleSite(String action, Option<String> ids){
        ObjectNode response = Json.newObject();
        Connection con = null;
        try{
            if(action != null && ids != null){
                String idlist = ids.get();
                if(idlist == null){
                    response.put("message", "ids null");return badRequest(response);
                }
                String idfinal = idlist.replaceAll("_", ",");
                con = DB.getConnection();
                if("APPROVE".equals(action)){
                    SiteListEntity siteListEntity = new SiteListEntity();
                    siteListEntity.setSiteApiEnum(SiteAPIEnum.approve_multiple_site);
                    siteListEntity.setId_list(idfinal);
                    ApiDef.change_site_status(con, siteListEntity);
                    response.put("message", "approve done");return ok(response);

                }else if("REJECT".equals(action)){
                    SiteListEntity siteListEntity = new SiteListEntity();
                    siteListEntity.setSiteApiEnum(SiteAPIEnum.reject_multiple_site);
                    siteListEntity.setId_list(idfinal);
                    ApiDef.change_site_status(con, siteListEntity);
                    response.put("message", "reject done");return ok(response);

                }else if("PAUSE".equals(action)){
                    SiteListEntity siteListEntity = new SiteListEntity();
                    siteListEntity.setSiteApiEnum(SiteAPIEnum.pause_multiple_site);
                    siteListEntity.setId_list(idfinal);
                    ApiDef.change_site_status(con, siteListEntity);
                    response.put("message", "pause done");return ok(response);

                }else{
                    response.put("message", "action : not present");return badRequest(response);
                }
            }else{
                response.put("message", "action or ids : null");return badRequest(response);
            }
        }catch(Exception e){
            play.Logger.error(e.getMessage(),e);
            response.put("message", "action or ids : null");return badRequest(response);
        }finally{
            try {
                if(con != null){
                    con.close();
                }
            } catch (SQLException e) {
                play.Logger.error(e.getMessage(),e);
            }
        }
    }
	public static Result defaultOptions(){
		ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
		result.put("tier_1_categoryOptions", MetadataAPI.tier_1_categories());
		result.put("tier_2_categoryOptions", MetadataAPI.tier_2_categories());
		result.put("hygieneOptions", MetadataAPI.hygieneList());
		result.put("creativeAttrOptions", MetadataAPI.creativeAttributes(-1));
		result.put("advertiserJsonArrayOptions", MetadataAPI.get_active_advertiser_list_as_metadata());
		return ok(result);
	}

	@SecuredAction
	public static Result save() { 	

		Form<SiteEntity> siteForm = siteFormModel.bindFromRequest(); 

		Site site = null;
		SiteEntity siteEntity;

		String guid = siteForm.field("pub_guid").value();
		if(!"cancel".equals(siteForm.data().get("action"))){
		if (siteForm.hasErrors()) {
			site = new Site();
			site.setPub_guid(guid);
			return badRequest( views.html.publisher.siteForm.render(siteForm, new SiteDisplayFull(site), show_opt_in_hygiene, allow_passback,is_native_supply,video_supply));
		}else{
			siteEntity = siteForm.get();
			site = siteEntity.getEntity();
			site.setModified_by(1);
			Connection con = null;
			try{
			con = DB.getConnection(); 
			if(con != null){
				
					Message msg = null;   
					if(site.getId() == -1){
						msg = ApiDef.insert_site(con, site);
					} else{
						msg = ApiDef.update_site(con, site);
					} 

					if(msg.getError_code() ==0){ 
						String destination = siteForm.field("destination").value();
						if(destination != null  && !"".equals(destination))
							return redirect(destination);
						else
							return redirect(routes.PublisherController.home(site.getPub_guid()));
					}else{
						return badRequest( views.html.publisher.siteForm.render(siteForm, new SiteDisplayFull(site), show_opt_in_hygiene, allow_passback,is_native_supply,video_supply));
					}
				}
			}catch(Exception e){
				Logger.error("Error  while saving Site:"+ e.getMessage(),e);
                return badRequest( views.html.publisher.siteForm.render(siteForm, new SiteDisplayFull(site), show_opt_in_hygiene, allow_passback,is_native_supply,video_supply));
			}finally{
				try {
					if(con != null)
						con.close();
				} catch (SQLException e) {
                    Logger.error(e.getMessage(),e);           
				}
			}
		}
		}
		site = new Site();
		site.setPub_guid(guid);
		return badRequest( views.html.publisher.siteForm.render(siteForm, new SiteDisplayFull(site), show_opt_in_hygiene, allow_passback,is_native_supply,video_supply));
	}
}
