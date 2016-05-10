package controllers.reporting;


import java.io.File;
import java.sql.Connection;
import java.util.Map;

import play.Logger;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.PageConstants;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;


public class HierarchicalReportController extends Controller{

	public static  ObjectMapper  om  = new ObjectMapper();
	public static Result reportPage( String reportType, Option<String> filter){ 
		if(filter.nonEmpty()) 
			return ok(views.html.reporting.hierarchical.hierarchical_report.render(reportType, filter.get(), "", "")); 
		else
			return ok(views.html.reporting.hierarchical.hierarchical_report.render(reportType, "", "", "")); 
 
	}
	
	@SuppressWarnings("unused")
	public static Result filteredReportPage( String reportType, Option<String> filter){ 
		String breadcrumbs = "";
		Map<String, String[]>  filterData  = request().body().asFormUrlEncoded();
		ObjectNode paramObj = Json.newObject(); 
		
//		if(filterData.containsKey("breadcrumbs")){
//			paramObj.put("breadcrumbs", filterData.get("breadcrumbs")[0]);
//		}
		if(filterData.containsKey("startDate")){
			paramObj.put("startDate", filterData.get("startDate")[0]);
		}
		if(filterData.containsKey("endDate")){
			paramObj.put("endDate", filterData.get("endDate")[0]);
		}
		if(filterData.containsKey("frequency")){
            paramObj.put("frequency", filterData.get("frequency")[0]);
        }
		if(filterData.containsKey("supplysourcetype")){
            paramObj.put("supplysourcetype", filterData.get("supplysourcetype")[0]);
        }
        if(filterData.containsKey("site_hygiene")){
            paramObj.put("site_hygiene", filterData.get("site_hygiene")[0]);
        }
			
		String params = "";
		try {
			params = om.writeValueAsString(paramObj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(filter.nonEmpty()) 
			return ok(views.html.reporting.hierarchical.hierarchical_report.render(reportType, filter.get(), filterData.get("breadcrumbs")[0], params)); 
		else
			return ok(views.html.reporting.hierarchical.hierarchical_report.render(reportType, "", filterData.get("breadcrumbs")[0], params)); 
 
	}
	/**
	 * 
	 * public static Result reportDataCSV(String hierarchyType){
        String pre  = "public/";
        String post = "reporting/download/csv/"+SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString()+".csv";
        File file = new File(pre+post).getAbsoluteFile();
        reportData(false, true, file.getAbsolutePath(), hierarchyType);
        ObjectNode result = Json.newObject();
        result.put("downloadurl", controllers.routes.StaticFileController.download(Option.apply(post)).url());
        return ok(result);
    }
	 */
	public static Result reportData(){
		ObjectNode response = Json.newObject();
		Connection con = null;
		try{
			JsonNode  filterData  = request().body().asJson();

			//JsonNode breadCrumbs = filterData.get("breadcrumbs");
			String crumbs = filterData.get("crumbs").asText();
			String reportType  = filterData.get("reportType").asText();
			String reportpath  = filterData.get("reportpath").asText();
			String supplysourcetype  = filterData.get("supplysourcetype").asText();
			String site_hygiene  = filterData.get("site_hygiene").asText();
			if(filterData.get("loadDimData") != null){
				boolean getTabData = filterData.get("loadDimData").asBoolean();
				if(getTabData){
					String dimData = HierarchyPopulator.getTabs(reportType,crumbs);
					JsonNode dimDataObj = om.readTree(dimData);
					response.put("dimensions", dimDataObj);
				}
			}
			String orderBy = null;
			if(filterData.get("orderBy") != null){
			    orderBy = filterData.get("orderBy").asText();
			}
			boolean orderByReverse = false;
			if(filterData.get("orderByReverse") != null){
                orderByReverse = filterData.get("orderByReverse").asBoolean();
            }
            boolean getCSV = false;
            if(filterData.get("getCSV") != null){
                getCSV = filterData.get("getCSV").asBoolean();
            }

			con =  DB.getConnection();
			ReportingEntity re = new ReportingEntity();
			HierarchyPopulator.populateSupplySourceType(re, supplysourcetype);
			HierarchyPopulator.populateSiteHygiene(re, site_hygiene);
			HierarchyPopulator.populate(re,reportType, crumbs);
			HierarchyPopulator.populateOrderBy(re, orderBy, orderByReverse, crumbs);
			re.setStartindex(filterData.get("pageNo").asInt());
			re.setPagesize(PageConstants.page_size);
			String startDate = filterData.get("startDate").asText();
			re.setStart_time_str(startDate);
			String endDate = filterData.get("endDate").asText();
			re.setEnd_time_str(endDate);
			HierarchyPopulator.populateFreq(re, startDate, re.getTimezone());
			org.codehaus.jackson.JsonNode data = null;
			String post = null;
			if(getCSV){
                re.setStartindex(PageConstants.start_index);
                re.setPagesize(PageConstants.csv_page_size);
                re.setRollup(true);
                String pre  = "public/";
                post = "reporting/download/csv/"+reportpath+"_"+startDate+"_to_"+endDate+"_"+SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString()+".csv";
                File file = new File(pre+post).getAbsoluteFile();
                data = ApiDef.get_data(con, re, true, false, null);
                ApiDef.get_data(con, re, false, true, file.getAbsolutePath());
            }else{
                data = ApiDef.get_data(con, re, true, false, null);
            }
			if(data != null){ 
				JsonNode tableData = om.readTree(data.toString());
				response.put("tableData", tableData);
				if(getCSV){
				    response.put("downloadurl", controllers.routes.StaticFileController.download(Option.apply(post)).url());
				}
			}

		}catch (Exception  e) { 
			Logger.error(e.getMessage(),e);
		}finally{
			try {
				if(con != null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed to close connection in Reporting Entity",e2);
			}
		}
		return ok(response);
	}
	
}
