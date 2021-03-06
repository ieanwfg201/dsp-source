package controllers.adxbasedexchanges;

import java.sql.Connection;
import java.sql.SQLException;

import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.materialbannerupload.MaterialBannerUploadListEntity;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.kritterui.api.def.ApiDef;

import models.entities.adxbasedexchanges.MaterialUploadExchangeFormEntity;

public class MaterialBannerUploadController extends Controller{
	
    @SecuredAction
    public static Result list(Option<String> exchange,Option<String> adxBasedExchangesStates){
        Form<MaterialUploadExchangeFormEntity> materialUploadExchangesFormEntityData = Form.form(MaterialUploadExchangeFormEntity.class);
        MaterialUploadExchangeFormEntity materialUploadExchangesFormEntity  = new MaterialUploadExchangeFormEntity();

        if(!exchange.isEmpty()){
        	materialUploadExchangesFormEntity.setExchange(exchange.get());
        }
        
        if(!adxBasedExchangesStates.isEmpty()){
        	String str = adxBasedExchangesStates.get();
        	if(!str.isEmpty()){
        		try{
        			Integer a = Integer.parseInt(str);
        			AdxBasedExchangesStates aes = AdxBasedExchangesStates.getEnum(a);
        			if(aes !=null){
        				materialUploadExchangesFormEntity.setAdxBasedExchangesStates(aes.getCode());
        			}
        		}catch(Exception e){
        			Logger.error(e.getMessage(),e);
        		}
        	}
        }
        return ok(views.html.adxbasedexchanges.bannerupload.render(materialUploadExchangesFormEntityData.fill(materialUploadExchangesFormEntity)));
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
	            	
	            	MaterialBannerUploadListEntity aListEntity = new MaterialBannerUploadListEntity();
	                aListEntity.setAdxstate(AdxBasedExchangesStates.SUBMITTED);
	                aListEntity.setId_list(idfinal);
	                ApiDef.update_material_banner_status(con, aListEntity);
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
	                MaterialBannerUploadListEntity aListEntity = new MaterialBannerUploadListEntity();
	                aListEntity.setAdxstate(AdxBasedExchangesStates.SUBMITTED);
	                aListEntity.setId_list(id+"");
	                ApiDef.update_material_banner_status(con, aListEntity);
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
