package controllers.advertiser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import models.Constants.Actions;
import models.advertiser.AdDisplay;
import models.advertiser.AdDisplayFull;
import models.entities.AdEntity;
import models.formbinders.AdWorkFlowEntity;
import org.springframework.beans.BeanUtils;

import play.Play;
import play.cache.Cache;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.MetadataAPI;
import views.html.advt.campaign.adForm;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class AdController extends Controller{

	static Form<AdEntity> adFormTemplate = Form.form(AdEntity.class);
	static Form<AdWorkFlowEntity> adWFForm = Form.form(AdWorkFlowEntity.class);
	private static String allow_adomain = Play.application().configuration().getString("allow_adomain");
	private static String user_flow_enabled = Play.application().configuration().getString("user_flow_enabled");
	private static String retargeting_flow_enabled = Play.application().configuration().getString("retargeting_flow_enabled");
	private static String mma_required = Play.application().configuration().getString("mma_required");
	private static String adposition_required = Play.application().configuration().getString("adposition_required");
	private static String channel_required = Play.application().configuration().getString("channel_required");
	private static String lat_lon_file = Play.application().configuration().getString("lat_lon_file");
	private static String deviceid_targeting = Play.application().configuration().getString("deviceid_targeting");

	@SecuredAction
	public static Result createAd(  int campaignId, Option<String> formId,
									Option<String> targetingGuid, Option<String> creativeGuid){  
		AdEntity ad = new AdEntity(); 
		Campaign campaign = CampaignController.getCampaign(campaignId);
		ad.setCampaign_id(campaignId);
		ad.setCampaign_guid(campaign.getGuid());
		ad.setAccount_guid(campaign.getAccount_guid());
		if(!formId.nonEmpty()){	 
			return ok(adForm.render( adFormTemplate.fill(ad), new AdDisplay(ad.getEntity()), "false", allow_adomain, user_flow_enabled, mma_required));
		}else{
			@SuppressWarnings("unchecked")
			Form<AdEntity> adForm1 = (Form<AdEntity>) Cache.get(formId.get());
			Map<String, String> formData = adForm1.data();
			if(targetingGuid.nonEmpty())
				formData.put("targeting_guid", targetingGuid.get());
			if(creativeGuid.nonEmpty())
				formData.put("creative", creativeGuid.get());
			return ok(adForm.render( adForm1.bind(formData), new AdDisplay(ad.getEntity()), "false", allow_adomain, user_flow_enabled,mma_required));
		}
	}

	@SecuredAction
	public static Result edit( int id, Option<String> destination){
		Ad ad = DataAPI.getAd(id);
		AdEntity adEntity = new AdEntity();
		BeanUtils.copyProperties(ad,  adEntity);
		if(destination.nonEmpty())
			adEntity.setDestination(destination.get()); 
		AdDisplay	adDisplay =	new AdDisplay(ad);
		adEntity.setAccount_guid(adDisplay.getAccountGuid());
		return ok(adForm.render(  adFormTemplate.fill(adEntity), adDisplay, "false", allow_adomain, user_flow_enabled,mma_required));
	}
	
	@SecuredAction
	public static Result cloneAd( int id){
		Ad ad = DataAPI.getAd(id);
		AdEntity adEntity = new AdEntity();
		ad.setId(0);
		ad.setName(null);
		ad.setStatus_id(StatusIdEnum.Pending);
		BeanUtils.copyProperties(ad,  adEntity); 
        AdDisplay   adDisplay = new AdDisplay(ad);
        adEntity.setAccount_guid(adDisplay.getAccountGuid());
        return ok(adForm.render(  adFormTemplate.fill(adEntity), adDisplay, "true", allow_adomain, user_flow_enabled,mma_required));
	}

	@SecuredAction
	public static Result view( int id, Option<String> destination){
		Ad ad = DataAPI.getAd(id);
		AdDisplayFull adDisplay = new AdDisplayFull(ad);
		if(destination.nonEmpty())
			adDisplay.setDestination(destination.get());
		return ok(views.html.advt.campaign.adHome.render(adDisplay,retargeting_flow_enabled,mma_required,adposition_required,channel_required,lat_lon_file,deviceid_targeting));
	}

	@SecuredAction
	public static Result defaultOptions(){
		ObjectNode defaultOptions = new ObjectNode(JsonNodeFactory.instance);
		defaultOptions.put("tier1categoryOptions", MetadataAPI.tier_1_categories());
		defaultOptions.put("tier2categoryOptions", MetadataAPI.tier_2_categories());
		defaultOptions.put("hygieneOptions", MetadataAPI.hygieneList());
		return ok(defaultOptions);
	}
	
	@SecuredAction
	public static Result updateMultipleAds(String action, Option<String> ids){
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
	            if("START".equals(action)){
	                AdListEntity adListEntity = new AdListEntity();
	                adListEntity.setAdenum(AdAPIEnum.activate_ad_by_ids);
	                adListEntity.setId_list(idfinal);
	                ApiDef.change_status_ad(con, adListEntity);
	                response.put("message", "start dne");return ok(response);
	            }else if("PAUSE".equals(action)){
	                AdListEntity adListEntity = new AdListEntity();
	                adListEntity.setAdenum(AdAPIEnum.pause_ad_by_ids);
	                adListEntity.setId_list(idfinal);
	                ApiDef.change_status_ad(con, adListEntity);
	                response.put("message", "pause done");return ok(response);

	            }else if("APPROVE".equals(action)){
                    AdListEntity adListEntity = new AdListEntity();
                    adListEntity.setAdenum(AdAPIEnum.approve_multiple_ads);
                    adListEntity.setId_list(idfinal);
                    ApiDef.change_status_ad(con, adListEntity);
                    response.put("message", "approve done");return ok(response);

                }else if("REJECT".equals(action)){
                    AdListEntity adListEntity = new AdListEntity();
                    adListEntity.setAdenum(AdAPIEnum.reject_multiple_ads);
                    adListEntity.setId_list(idfinal);
                    ApiDef.change_status_ad(con, adListEntity);
                    response.put("message", "reject done");return ok(response);

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

	@SecuredAction
    public static Result saveAd(){
	    return saveAd(false);
	}

    @SecuredAction
    public static Result saveAdClone(){
        return saveAd(true);
    }
    
	@SecuredAction
	public static Result saveAd(boolean isClone){
		String[] postAction = request().body().asFormUrlEncoded().get("action");
		String action = "";
		if (postAction == null || postAction.length == 0) {
			return badRequest("You must provide a valid action");
		} 
		action = postAction[0];
		if("cancel".equals(action)){

		}
		Form<AdEntity> filledForm = adFormTemplate.bindFromRequest(); 
		String formUuid = SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString();
		Cache.set(formUuid, filledForm);

		String accountGuid = filledForm.field("account_guid").value();
		int campaignId =-1;
		String campaignGuid=null;
		if(isClone){
		    campaignId = Integer.parseInt(filledForm.field("campaign_guid").value().split("\\|")[0]);
		    campaignGuid = filledForm.field("campaign_guid").value().split("\\|")[1];
		}else{
		    campaignId = Integer.parseInt(filledForm.field("campaign_id").value());
		}
		Option<String> none = Option.empty();
		String destUrl = routes.AdController.createAd(campaignId, Scala.Option(formUuid), none, none).url();

		if("new_targeting".equals(action)){			
			return redirect(routes.TargetingProfileController.add(accountGuid, Scala.Option(destUrl)).url());
		}

		if("new_creative".equals(action)){
			return redirect(routes.CreativeController.addCreative(accountGuid, Scala.Option(destUrl)).url());
		}




		AdEntity ad = null;

		if(!filledForm.hasErrors()){
			ad = filledForm.get();
			ad.setModified_by(1); 
			Connection con = null;
			Message msg = null;
			if(isClone){
			    ad.setCampaign_guid(campaignGuid);
			    ad.setCampaign_id(campaignId);
			}
			try{
			    con = DB.getConnection();
				if(ad.getId()>0){
					msg = ApiDef.update_ad(con, ad.getEntity());
				}else{
					msg = ApiDef.insert_ad(con, ad.getEntity());
				}

				if(msg.getError_code()==0){
					String destination = filledForm.field("destination").value();
					if(destination != null  && !"".equals(destination))
						return redirect(destination);
					else{  
						return redirect(controllers.advertiser.routes.CampaignController.view(ad.getCampaign_id()));
					}

				}else{
					return badRequest(adForm.render( filledForm, new AdDisplay(ad.getEntity()), "false", allow_adomain, user_flow_enabled,mma_required));
				}
			}catch(Exception e){
                play.Logger.error(e.getMessage(),e);
				return badRequest();
			}finally{   
                try {
                    if(con != null){
                        con.close();
                    }
                } catch (SQLException e) {
                    play.Logger.error(e.getMessage(),e);
                }            
            }
		}else{
			if(filledForm.error("creative_guid")!= null || filledForm.error("creative_id")!= null){
				filledForm.reject("creative", "Please select a creative");
			}
		}
		Ad ad1  = new Ad();
		if(isClone){
		    ad1.setCampaign_id(Integer.parseInt(filledForm.field("campaign_guid").value().split("\\|")[0]));
		    ad1.setCampaign_guid(filledForm.field("campaign_guid").value().split("\\|")[1]);

		}else{
		    ad1.setCampaign_guid(filledForm.field("campaign_guid").value());
		    ad1.setCampaign_id(Integer.parseInt(filledForm.field("campaign_id").value()));
		}
		return badRequest(adForm.render( filledForm, new AdDisplay(ad1), "false", allow_adomain, user_flow_enabled,mma_required));
	}

	@SecuredAction
	public static Result updateStatusForm(int adId,   String action) {
		AdWorkFlowEntity adWfEntity = new AdWorkFlowEntity( adId, action); 
		return ok(views.html.advt.campaign.adWorkflowForm.render(adWFForm.fill(adWfEntity), action));
	}

	@SecuredAction
	public static Result updateStatus(){
		Form<AdWorkFlowEntity> filledForm = adWFForm.bindFromRequest();
		Connection con = null;
        ObjectNode response = Json.newObject();
        try{
    		Message statusMsg = null;
		    if(!filledForm.hasErrors()){
    			con = DB.getConnection();
	    		AdWorkFlowEntity adWfEntity = filledForm.get();
		    	Ad ad = DataAPI.getAd(adWfEntity.getAdId());
			    ad.setModified_by(1);
    			switch(Actions.valueOf(adWfEntity.getAction())){		
	    		case Approve: 
		    		statusMsg = ApiDef.approve_ad(con, ad);			
			    	break;
    			case Reject: 
	    			ad.setComment(adWfEntity.getComment()); 
		    		statusMsg = ApiDef.reject_ad(con, ad); 
			    	break;
    			case Start: 
	    			ad.setComment(adWfEntity.getComment()); 
		    		statusMsg = ApiDef.activate_ad(con, ad); 
			    	break;
    			case Pause: 
	    			ad.setComment(adWfEntity.getComment()); 
		    		statusMsg = ApiDef.pause_ad(con, ad); 
			    	break;
    			default:
	    			break;
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
                    if(con != null){
                        con.close();
                    }
                } catch (SQLException e) {
                        play.Logger.error(e.getMessage(),e);
                }
        }

	}
}
