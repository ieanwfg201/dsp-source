package controllers.fraudreport;

import java.io.File;
import java.sql.Connection;

import models.entities.fraudreport.FraudReportFormEntity;
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
import com.kritter.api.entity.fraud.FraudReportEntity;
import com.kritter.constants.PageConstants;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;



public class FraudReportController extends Controller{

	private static Form<FraudReportFormEntity> fraudreportConfigForm = Form.form(FraudReportFormEntity.class);
	
	@SecuredAction
	public static Result fraudReport(){
	    FraudReportFormEntity rfe = new FraudReportFormEntity(new FraudReportEntity());
		return ok(views.html.fraudreport.fraudreport.render(fraudreportConfigForm.fill(rfe)));
	}

	public static Result reportDataCSV(){
	    return reportDataCSV(null);
	}
	public static Result reportDataCSV(String hierarchyType){
        String pre  = "public/";
        String post = "reporting/download/csv/"+SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString()+".csv";
	    File file = new File(pre+post).getAbsoluteFile();
	    reportData(false, true, file.getAbsolutePath(), hierarchyType);
        ObjectNode result = Json.newObject();
        result.put("downloadurl", controllers.routes.StaticFileController.download(Option.apply(post)).url());
        return ok(result);
	}
	public static Result reportData(){
	    return reportData(false, false, null, null);
	}
	public static Result reportData(boolean returnWithId, boolean exportAsCsv, String absoluteFileName, String hierarchyType){
		JsonNode result = new ObjectNode(JsonNodeFactory.instance);
		Form<FraudReportFormEntity> filledFilterForm = fraudreportConfigForm.bindFromRequest();
		Connection con = null;
		if(!filledFilterForm.hasErrors()){
			try {
			    FraudReportFormEntity fraudreportFormEntity = filledFilterForm.get();
				FraudReportEntity reportingEntity = fraudreportFormEntity.getReportEntity(); 
				con =  DB.getConnection(); 
				if(exportAsCsv){
				    reportingEntity.setStartindex(PageConstants.start_index);
				    reportingEntity.setPagesize(PageConstants.csv_page_size);
				    reportingEntity.setRollup(true);
				}
				org.codehaus.jackson.JsonNode data = ApiDef.get_data(con, reportingEntity, returnWithId, exportAsCsv, absoluteFileName);
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
		if(exportAsCsv){
		    return null;
		}
		return ok(result);
	}

	public static Result adsByCampaign(String campaignList){
		ArrayNode adOptions = MetadataAPI.adsByCampaign(campaignList); 
		return ok(adOptions);
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
