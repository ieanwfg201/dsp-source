package controllers.adstats;

import java.io.File;
import java.sql.Connection;

import models.entities.ad_stats.AdStatsFormEntity;
import play.Logger;
import play.Play;
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
import com.kritter.entity.ad_stats.AdStats;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;



public class AdStatsController extends Controller{
	private static String timezoneid = Play.application().configuration().getString("timezoneid");
	private static Form<AdStatsFormEntity> adstatsConfigForm = Form.form(AdStatsFormEntity.class);
	
	@SecuredAction
	public static Result adstatsReport(){
	    AdStatsFormEntity rfe = new AdStatsFormEntity(new AdStats());
		return ok(views.html.ad_stats.ad_stats.render(adstatsConfigForm.fill(rfe)));
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
		Form<AdStatsFormEntity> filledFilterForm = adstatsConfigForm.bindFromRequest();
		Connection con = null;
		if(!filledFilterForm.hasErrors()){
			try {
			    AdStatsFormEntity adstatsFormEntity = filledFilterForm.get();
				AdStats reportingEntity = adstatsFormEntity.getReportEntity(); 
				con =  DB.getConnection(); 
				if(exportAsCsv){
				    reportingEntity.setStartindex(PageConstants.start_index);
				    reportingEntity.setPagesize(PageConstants.csv_page_size);
				}
				reportingEntity.setTimezone(timezoneid);
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
