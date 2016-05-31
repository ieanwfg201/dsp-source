package controllers.algo;

import java.io.File;
import java.sql.Connection;

import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.MetadataAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.constants.PageConstants;
import com.kritter.entity.algomodel.AlgoModelEntity;
import com.kritter.entity.userreports.UserReport;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

import models.entities.algo.AlgoFormEntity;



public class AlgoController extends Controller{

	private static Form<AlgoFormEntity> algoConfigForm = Form.form(AlgoFormEntity.class);
	
	@SecuredAction
	public static Result getAlpha(){
	    AlgoFormEntity rfe = new AlgoFormEntity(new AlgoModelEntity());
		return ok(views.html.algo.algo.render(algoConfigForm.fill(rfe)));
	}

	public static Result reportData(){
		JsonNode result = new ObjectNode(JsonNodeFactory.instance);
		Form<AlgoFormEntity> filledFilterForm = algoConfigForm.bindFromRequest();
		Connection con = null;
		if(!filledFilterForm.hasErrors()){
			try {
			    AlgoFormEntity algoFormEntity = filledFilterForm.get();
				AlgoModelEntity algoModelEntity = algoFormEntity.getReportEntity(); 
				con =  DB.getConnection(); 
				org.codehaus.jackson.JsonNode data = ApiDef.get_data(con, algoModelEntity);
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

	public static JsonNode convert(org.codehaus.jackson.JsonNode jsonNode){
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode result = null;
		try {
			result = objectMapper.readTree(jsonNode.toString());
		} catch (Exception  e) { 
			Logger.error(e.getMessage(),e);
		} 
		return result;
	}

}
