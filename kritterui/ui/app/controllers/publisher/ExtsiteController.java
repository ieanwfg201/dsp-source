package controllers.publisher;

import java.sql.Connection;
import java.sql.SQLException;

import models.formbinders.EditExtSiteNameWorkFlowEntity;
import models.formbinders.ExtSiteWorkFlowEntity;
import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.ext_site.Ext_site_input;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Ext_siteEnum;
import com.kritter.kritterui.api.def.ApiDef;

import controllers.advertiser.IOController.IO_ACTIONS;


public class ExtsiteController extends Controller{

	static Form<ExtSiteWorkFlowEntity> extsiteWorkflowForm = Form.form(ExtSiteWorkFlowEntity.class);
	static Form<EditExtSiteNameWorkFlowEntity> editextsiteNameWorkflowForm = Form.form(EditExtSiteNameWorkFlowEntity.class);

	@SecuredAction
	public static Result workflowForm(int id, String action  ) {   
	    ExtSiteWorkFlowEntity swe = new ExtSiteWorkFlowEntity(id, action); 
		return ok(views.html.publisher.extsiteWorkflowForm.render(extsiteWorkflowForm.fill(swe),action));
	}

	@SecuredAction
    public static Result editworkflowForm(int id, String action  ) {   
	    EditExtSiteNameWorkFlowEntity swe = new EditExtSiteNameWorkFlowEntity(id, action,""); 
        return ok(views.html.publisher.editextsiteNameWorkflowForm.render(editextsiteNameWorkflowForm.fill(swe),action));
    }
	
    @SecuredAction
    public static Result updateextSite() { 
        Form<ExtSiteWorkFlowEntity> extsiteWfEntityForm1 = extsiteWorkflowForm.bindFromRequest();
        Connection con = null;
        ObjectNode response = Json.newObject();
        try{
            Message statusMsg = null;
            if(!extsiteWfEntityForm1.hasErrors()){
                con = DB.getConnection();
                ExtSiteWorkFlowEntity extsiteWfEntity = extsiteWfEntityForm1.get();
                Ext_site_input ext_site_input = new Ext_site_input();
                ext_site_input.setId_list(extsiteWfEntity.getId()+"");
                if(IO_ACTIONS.valueOf(extsiteWfEntity.getAction())==IO_ACTIONS.approve){ 
                    ext_site_input.setExt_siteenum(Ext_siteEnum.aprrove_ext_site_by_ids);
                    statusMsg = ApiDef.update_ext_site(con, ext_site_input);                 
                }else if(IO_ACTIONS.valueOf(extsiteWfEntity.getAction())==IO_ACTIONS.reject){  
                    ext_site_input.setExt_siteenum(Ext_siteEnum.unaprrove_ext_site_by_ids);
                    statusMsg = ApiDef.update_ext_site(con, ext_site_input);                 
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
	public static Result updateextsitename() { 
		Form<EditExtSiteNameWorkFlowEntity> editextsiteNameWorkflowForm1 = editextsiteNameWorkflowForm.bindFromRequest();
		Connection con = null;
        ObjectNode response = Json.newObject();
        try{
		    Message statusMsg = null;
    		if(!editextsiteNameWorkflowForm1.hasErrors()){
		    	con = DB.getConnection();
		    	EditExtSiteNameWorkFlowEntity extsiteWfEntity = editextsiteNameWorkflowForm1.get();
		    	Ext_site_input ext_site_input = new Ext_site_input();
		    	ext_site_input.setId_list(extsiteWfEntity.getId()+"");
		    	ext_site_input.setStr(extsiteWfEntity.getComment());
		    	ext_site_input.setExt_siteenum(Ext_siteEnum.update_ext_site_name);
		    	statusMsg = ApiDef.update_ext_site(con, ext_site_input);				 
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
    public static Result updateMultipleextSite(String action, Option<String> ids){
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
                    Ext_site_input ext_site_input = new Ext_site_input();
                    ext_site_input.setExt_siteenum(Ext_siteEnum.aprrove_ext_site_by_ids);
                    ext_site_input.setId_list(idfinal);
                    ApiDef.update_ext_site(con, ext_site_input);
                    response.put("message", "approve done");return ok(response);

                }else if("REJECT".equals(action)){
                    Ext_site_input ext_site_input = new Ext_site_input();
                    ext_site_input.setExt_siteenum(Ext_siteEnum.unaprrove_ext_site_by_ids);
                    ext_site_input.setId_list(idfinal);
                    ApiDef.update_ext_site(con, ext_site_input);
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
	
}
