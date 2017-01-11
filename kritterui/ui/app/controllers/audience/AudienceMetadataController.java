package controllers.audience;

import java.sql.Connection;
import java.sql.SQLException;

import play.Play;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.entity.audience_metadata.AudienceMetadataInput;
import com.kritter.kritterui.api.def.ApiDef;

public class AudienceMetadataController extends Controller{
	
	private static String audience_targeting = Play.application().configuration().getString("audience_targeting");

	@SecuredAction
	public static Result list(){ 
		return ok(views.html.audiencemetadata.audiencemetadata.render(audience_targeting));
	}

	@SecuredAction
	public static Result updatesingle(int id,String action){
	    ObjectNode response = Json.newObject();
	    Connection con = null;
	    try{
	        if(action != null){
	            con = DB.getConnection();
	            if("enable".equalsIgnoreCase(action)){
	            	AudienceMetadataInput ami = new AudienceMetadataInput();
	            	ami.setEnabled(true);
	            	ami.setIds(id+"");
	                ApiDef.update_audience_metadata(con, ami);
	                response.put("message", "enabled");return ok(response);
	            }else if("disable".equalsIgnoreCase(action)){
	            	AudienceMetadataInput ami = new AudienceMetadataInput();
	            	ami.setEnabled(false);
	            	ami.setIds(id+"");
	                ApiDef.update_audience_metadata(con, ami);
	                response.put("message", "enabled");return ok(response);
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
