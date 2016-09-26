package controllers.adxbasedexchanges;

import java.sql.Connection;
import java.sql.SQLException;

import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.materialadvinfoupload.MaterialAdvInfoUploadListEntity;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.kritterui.api.def.ApiDef;

import models.entities.adxbasedexchanges.MaterialUploadExchangeFormEntity;

public class MaterialAdvInfoController extends Controller{
	
    @SecuredAction
    public static Result list(Option<String> exchange){
        Form<MaterialUploadExchangeFormEntity> materialUploadExchangesFormEntityData = Form.form(MaterialUploadExchangeFormEntity.class);
        MaterialUploadExchangeFormEntity materialUploadExchangesFormEntity  = new MaterialUploadExchangeFormEntity();

        if(!exchange.isEmpty()){
        	materialUploadExchangesFormEntity.setExchange(exchange.get());
        }
        return ok(views.html.adxbasedexchanges.advinfoupload.render(materialUploadExchangesFormEntityData.fill(materialUploadExchangesFormEntity)));
    }

	@SecuredAction
	public static Result updatemultiple(String action, Option<String> ids){
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
	            if(AdxBasedExchangesStates.SUBMITTED.getName().equals(action)){
	            	
	            	MaterialAdvInfoUploadListEntity aListEntity = new MaterialAdvInfoUploadListEntity();
	                aListEntity.setAdxstate(AdxBasedExchangesStates.SUBMITTED);
	                aListEntity.setId_list(idfinal);
	                ApiDef.update_material_advinfo_status(con, aListEntity);
	                response.put("message", "start dne");return ok(response);
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
	public static Result updatesingle(int id,String action){
	    ObjectNode response = Json.newObject();
	    Connection con = null;
	    try{
	        if(action != null){
	            con = DB.getConnection();
	            if(AdxBasedExchangesStates.SUBMITTED.getName().equals(action)){
	            	MaterialAdvInfoUploadListEntity aListEntity = new MaterialAdvInfoUploadListEntity();
	                aListEntity.setAdxstate(AdxBasedExchangesStates.SUBMITTED);
	                aListEntity.setId_list(id+"");
	                ApiDef.update_material_advinfo_status(con, aListEntity);
	                response.put("message", "start dne");return ok(response);
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
