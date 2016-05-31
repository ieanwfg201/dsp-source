package controllers.req_logging;

import java.sql.Connection;

import play.Logger;
import play.data.Form;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.req_logging.ReqLoggingInput;
import com.kritter.constants.ReqLoggingEnum;
import com.kritter.entity.req_logging.ReqLoggingEntity;
import com.kritter.kritterui.api.def.ApiDef;
import models.entities.req_logging.ReqLoggingFormEntity;



public class ReqLoggingController extends Controller{

	private static Form<ReqLoggingFormEntity> algoConfigForm = Form.form(ReqLoggingFormEntity.class);
	
	@SecuredAction
	public static Result getReqLogging(){
	    ReqLoggingFormEntity rfe = new ReqLoggingFormEntity(new ReqLoggingEntity());
		return ok(views.html.reqlogging.reqlogging.render(algoConfigForm.fill(rfe)));
	}

	public static Result reportData(){
		JsonNode result = new ObjectNode(JsonNodeFactory.instance);
		Form<ReqLoggingFormEntity> filledFilterForm = algoConfigForm.bindFromRequest();
		Connection con = null;
		if(!filledFilterForm.hasErrors()){
			try {
			    ReqLoggingFormEntity reqLoggingFormEntity = filledFilterForm.get();
			    ReqLoggingEntity reqLoggingEntity = reqLoggingFormEntity.getEntity(); 
				con =  DB.getConnection(); 
				if(reqLoggingEntity.getPubId() !=null && !reqLoggingEntity.getPubId().trim().equals("")){
				    ApiDef.check_update_insert_req_logging(con, reqLoggingEntity);
				}
				ReqLoggingInput reqLoggingInput = new ReqLoggingInput();
				reqLoggingInput.setReqLoggingEnum(ReqLoggingEnum.get_all_req_logging_entities);
				org.codehaus.jackson.JsonNode data = ApiDef.get_data(con, reqLoggingInput);
            	if(data != null){
            	    ObjectMapper objectMapper = new ObjectMapper(); 
            	    result = objectMapper.readTree(data.toString());
				} 
			} catch (Exception  e) { 
				Logger.error(e.getMessage(),e);
			} 
			finally{
				try {
					if(con != null)
						con.close();
				} catch (Exception e2) {
					Logger.error("Failed to close connection in Reporting Entity",e2);
				}
			}
		}
		return ok(result);
	}

}
