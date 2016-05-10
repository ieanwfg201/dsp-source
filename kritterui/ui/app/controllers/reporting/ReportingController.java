package controllers.reporting;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


import models.entities.reporting.ReportFormEntity;
import models.entities.reporting.SavedQueryFormEntity;
import models.reporting.ExternalAdvertiserReportingEntity;
import models.reporting.ExternalPublisherReportingEntity;
import models.reporting.OverallReportingEntity;
import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.MetadataAPI;
import services.ReportingDataService;
import services.TPMetadataAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.saved_query.SavedQueryEntity;
import com.kritter.api.entity.saved_query.SavedQueryList;
import com.kritter.api.entity.saved_query.SavedQueryListEntity;
import com.kritter.constants.Frequency;
import com.kritter.constants.PageConstants;
import com.kritter.constants.ReportingDIMTypeEnum;
import com.kritter.constants.SavedQueryEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;



public class ReportingController extends Controller{

	private static Form<ReportFormEntity> reportConfigForm = Form.form(ReportFormEntity.class);
	private static Form<SavedQueryFormEntity> savedReportForm = Form.form(SavedQueryFormEntity.class);
	private static String show_carrier_ui = Play.application().configuration().getString("show_carrier_ui");
	private static String allow_wifi = Play.application().configuration().getString("allow_wifi");
	private static String ext_site_report_seperate = Play.application().configuration().getString("ext_site_report_seperate");
	
	@SecuredAction
	public static Result dashboard(){
		ReportFormEntity rfe = new ReportFormEntity(new ReportingEntity());		
		return ok(views.html.dashboard.render(reportConfigForm.fill(rfe),show_carrier_ui));
	}

	@SecuredAction
	public static Result supplyReport(){
		ReportFormEntity rfe = new ReportFormEntity(new ReportingEntity());
		return ok(views.html.reporting.supplyReport.render(reportConfigForm.fill(rfe), 0, allow_wifi, ext_site_report_seperate));
	}

	@SecuredAction
    public static Result limitedReport(){
	    ReportFormEntity rfe = new ReportFormEntity(new ReportingEntity());
	        return ok(views.html.reporting.limitedReport.render(reportConfigForm.fill(rfe), 0, allow_wifi, ext_site_report_seperate));
	}

	@SecuredAction
	public static Result demandReport(){
		ReportFormEntity rfe = new ReportFormEntity(new ReportingEntity());
		return ok(views.html.reporting.demandReport.render(reportConfigForm.fill(rfe), 0, allow_wifi, ext_site_report_seperate));
	}

	@SecuredAction
	public static Result networkReport(){
		ReportFormEntity rfe = new ReportFormEntity(new ReportingEntity());
		return ok(views.html.reporting.networkReport.render(reportConfigForm.fill(rfe), 0,allow_wifi, ext_site_report_seperate));
	}

	public static Result supplyReportOptions(){
		ObjectNode defaultOptions = new ObjectNode(JsonNodeFactory.instance);
		defaultOptions.put("publisherOptions", MetadataAPI.activePublisherArray()); 
		defaultOptions.put("countryOptions", TPMetadataAPI.countryList());  
		defaultOptions.put("osOptions", TPMetadataAPI.osList()); 
		return ok(defaultOptions);
	}

	public static Result networkReportOptions(){
		ObjectNode defaultOptions = new ObjectNode(JsonNodeFactory.instance);
		defaultOptions.put("publisherOptions", MetadataAPI.activePublisherArray());
		defaultOptions.put("advertiserOptions", MetadataAPI.activeAdvertiserArray()); 
		defaultOptions.put("countryOptions", TPMetadataAPI.countryList());  
		defaultOptions.put("osOptions", TPMetadataAPI.osList()); 
		return ok(defaultOptions);
	}

	public static Result demandReportOptions(){
		ObjectNode defaultOptions = new ObjectNode(JsonNodeFactory.instance);
		defaultOptions.put("advertiserOptions", MetadataAPI.activeAdvertiserArray()); 
		defaultOptions.put("countryOptions", TPMetadataAPI.countryList()); 
		defaultOptions.put("osOptions", TPMetadataAPI.osList()); 
		return ok(defaultOptions);
	}
    public static Result limitedReportOptions(){
        ObjectNode defaultOptions = new ObjectNode(JsonNodeFactory.instance);
        defaultOptions.put("publisherOptions", MetadataAPI.activePublisherArray());
        defaultOptions.put("advertiserOptions", MetadataAPI.activeAdvertiserArray()); 
        defaultOptions.put("countryOptions", TPMetadataAPI.countryList());  
        defaultOptions.put("osOptions", TPMetadataAPI.osList()); 
        return ok(defaultOptions);
    }

	@SecuredAction
	public static Result savedReportsPage(){ 
		return ok(views.html.reporting.savedReportsPage.render());
	}

	@SecuredAction
	public static Result viewSavedReport(int reportId){ 
		ObjectNode savedReports = Json.newObject();
		Connection con = null;
		try {
			con = DB.getConnection();
			SavedQueryListEntity savedQueryListEntity = new SavedQueryListEntity();
			savedQueryListEntity.setPage_no(PageConstants.start_index);
			savedQueryListEntity.setPage_size(PageConstants.page_size);
			savedQueryListEntity.setId_list( reportId+"");
			savedQueryListEntity.setSaveQueryEnum(SavedQueryEnum.list_saved_query_entity_by_entity_id);

			SavedQueryList  savedQueryList = ApiDef.various_get_saved_query(con, savedQueryListEntity);
			if(savedQueryList.getMsg().getError_code() == 0){
				List<SavedQueryEntity> savedQueries = savedQueryList.getList(); 
				if(savedQueries.size() == 1){
					SavedQueryEntity savedQueryEntity = savedQueries.get(0);
					ReportFormEntity rfe = new ReportFormEntity(savedQueryEntity.getReporting_entity());
					
					switch (savedQueryEntity.getReportingTypeEnum()) {
					case SUPPLY:
						return ok(views.html.reporting.supplyReport.render(reportConfigForm.fill(rfe), savedQueryEntity.getId(),allow_wifi, ext_site_report_seperate));
					case DEMAND:
						return ok(views.html.reporting.demandReport.render(reportConfigForm.fill(rfe), savedQueryEntity.getId(), allow_wifi, ext_site_report_seperate));
					case NETWORK:
						return ok(views.html.reporting.networkReport.render(reportConfigForm.fill(rfe), savedQueryEntity.getId(), allow_wifi, ext_site_report_seperate));

					default:
						break;
					}
				}else
					return badRequest("Error viewed report");				  
			}
		} catch (Exception e) {
			return badRequest(savedReports);
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}
		return ok(savedReports); 
	}

	@SecuredAction
	public static Result deleteSavedReport(int reportId){ 
		ObjectNode savedReports = Json.newObject();
		
		if(reportId != 0){ 
			Message msg = ReportingDataService.deleteSavedQuery(reportId);
			if(msg != null && msg.getError_code() ==0){
				return redirect(routes.ReportingController.savedReportsPage().url());
			}  
		} 
		return redirect(routes.ReportingController.savedReportsPage().url());
	}

	@SecuredAction
	public static Result savedReportsData(){ 
		ObjectNode savedReports = Json.newObject();
		Connection con = null;
		try {
			con = DB.getConnection();
			SavedQueryListEntity savedQueryListEntity = new SavedQueryListEntity();

			Account account = new Account();
			account.setUserid(SecureSocial.currentUser().identityId().userId());

			AccountMsgPair acctMsgPair = ApiDef.get_Account(con, account); 
			acctMsgPair.getMsg();
			account = acctMsgPair.getAccount();

			savedQueryListEntity.setPage_no(PageConstants.start_index);
			savedQueryListEntity.setPage_size(PageConstants.page_size);
			savedQueryListEntity.setId_list(account.getGuid());
			savedQueryListEntity.setSaveQueryEnum(SavedQueryEnum.list_saved_query_entity_by_account_guids);



			SavedQueryList  savedQueryList = ApiDef.various_get_saved_query(con, savedQueryListEntity);
			if(savedQueryList.getMsg().getError_code() == 0){
				List<SavedQueryEntity> savedQueries = savedQueryList.getList(); 

				ArrayNode queryArray = savedReports.putArray("data");
				ObjectMapper om = new ObjectMapper();
				ObjectNode node = null;
				for (SavedQueryEntity savedQueryEntity : savedQueries) { 
					savedQueryEntity.setReporting_entity(null);
					queryArray.add(om.valueToTree(savedQueryEntity)); 
				} 
			}
		} catch (Exception e) {
			Logger.error("Error closing database connection",e); 
			return badRequest(savedReports);
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}
		return ok(savedReports);
	}

	@SecuredAction
	public static Result saveReportForm(String reportType, int savedReportId){  
		Connection con = null;
		SavedQueryFormEntity sqe = new SavedQueryFormEntity();
		try {
			con = DB.getConnection();

			String userId = SecureSocial.currentUser().identityId().userId();
			Account account = new Account();
			account.setUserid(userId);

			AccountMsgPair acctMsgPair = ApiDef.get_Account(con, account); 
			acctMsgPair.getMsg();
			account = acctMsgPair.getAccount(); 
			
			sqe.setAccount_guid(account.getGuid());
			sqe.setReportType(reportType);
			sqe.setId(savedReportId);
			if(savedReportId != 0){
				SavedQueryEntity savedQueryEntity = ReportingDataService.getSavedQuery(savedReportId);
				sqe.setName(savedQueryEntity.getName());
			}
			return ok( views.html.reporting.saved_report_form.render(savedReportForm.fill(sqe)));
		} catch (Exception e) {
			Logger.error("Error closing database connection",e); 
			return badRequest("");
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}  
	}

	@SecuredAction
	public static Result saveReport(){ 
		Form<SavedQueryFormEntity> filledForm = savedReportForm.bindFromRequest();
		ObjectNode result = Json.newObject();
		Connection con = null;
		if(!filledForm.hasErrors()){
			SavedQueryFormEntity savedQueryFormEntity = filledForm.get(); 
			try {
				con = DB.getConnection();
				SavedQueryEntity savedQueryEntity = savedQueryFormEntity.getSavedQueryEntity();
				Message msg = null;
				if(savedQueryEntity.getId() ==0){
					  msg = ApiDef.insert_saved_query(con, savedQueryEntity);
				}else{
					 SavedQueryEntity existingEntity = ReportingDataService.getSavedQuery(savedQueryEntity.getId());
					 existingEntity.setReporting_entity(savedQueryEntity.getReporting_entity());
					 existingEntity.setName(savedQueryEntity.getName());
					 msg = ApiDef.update_saved_query(con, savedQueryEntity);
				} 
				
				if(msg.getError_code() == 0){ 
					result.put("redirect_url", routes.ReportingController.savedReportsPage().url());
					return ok(result);
				}else{
					return badRequest(result);
				}
			} catch (Exception e) {
				Logger.error("parsing error", e);
			}

			finally{
				if(con != null){
					try {
						con.close();
					} catch (SQLException e) {
						Logger.error("Error in closing connection", e);
					}
				}
			}
		}
		return badRequest(result); 
	}
	public static Result monitoringAdvertiserReport(){
	    return externalAdvertiserReport(true);
	}
	public static Result externalAdvertiserReport(){
	    return externalAdvertiserReport(false);
	}
	
    public static Result monitoringOverall(){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            OverallReportingEntity entity = OverallReportingEntity.getObject(jsonNode.toString());
            Message msg = entity.validate();
            if(msg.getError_code() != ErrorEnum.NO_ERROR.getId()){
                return badRequest(msg.toJson().toString());
            }
            ReportingEntity reportingEntity = entity.getReportingEntity();
            org.codehaus.jackson.JsonNode resultNode = ApiDef.get_data(con, reportingEntity, true, false, null);
            return ok(resultNode.toString());
        }catch(Exception e){
            Logger.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return badRequest(msg.toJson().toString());
        }finally{
            if(con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                    Logger.error(e.getMessage(),e);
                }
            }
        }
    }
	public static Result externalAdvertiserReport(boolean monitoring){
		JsonNode jsonNode= request().body().asJson();
		Connection con = null;
		try{

			ExternalAdvertiserReportingEntity entity = ExternalAdvertiserReportingEntity.getObject(jsonNode.toString());
			Message msg = entity.validate();
			if(msg.getError_code() != ErrorEnum.NO_ERROR.getId()){
				return badRequest(msg.toJson().toString());
			}
			Account account = new Account();
			account.setGuid(entity.getAdvertiserId());
			account.setApi_key(entity.getApi_key());
			con = DB.getConnection();
			AccountMsgPair amPair = ApiDef.get_Account_By_Guid_Apikey(con, account);
			if(amPair.getMsg().getError_code() != ErrorEnum.NO_ERROR.getId()){
				msg.setError_code(ErrorEnum.AUTH_CREDENTIALS_INVALID.getId());
				msg.setMsg(ErrorEnum.AUTH_CREDENTIALS_INVALID.getName());
				return badRequest(msg.toJson().toString());
			}
			ReportingEntity reportingEntity = entity.getReportingEntity();
			org.codehaus.jackson.JsonNode resultNode = ApiDef.get_data(con, reportingEntity, monitoring, false, null);
			return ok(resultNode.toString());
		}catch(Exception e){
			Logger.error(e.getMessage(),e);
			Message msg = new Message();
			msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
			return badRequest(msg.toJson().toString());
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}
	}
    public static Result monitoringPublisherReport(){
        return externalPublisherReport(true);
    }
	public static Result externalPublisherReport(){
	    return externalPublisherReport(false);
	}
	public static Result externalPublisherReport(boolean monitoring){
		JsonNode jsonNode= request().body().asJson();
		Connection con = null;
		try{

			ExternalPublisherReportingEntity entity = ExternalPublisherReportingEntity.getObject(jsonNode.toString());
			Message msg = entity.validate();
			if(msg.getError_code() != ErrorEnum.NO_ERROR.getId()){
				return badRequest(msg.toJson().toString());
			}
			Account account = new Account();
			account.setGuid(entity.getPublisherId());
			account.setApi_key(entity.getApi_key());
			con = DB.getConnection();
			AccountMsgPair amPair = ApiDef.get_Account_By_Guid_Apikey(con, account);
			if(amPair.getMsg().getError_code() != ErrorEnum.NO_ERROR.getId()){
				msg.setError_code(ErrorEnum.AUTH_CREDENTIALS_INVALID.getId());
				msg.setMsg(ErrorEnum.AUTH_CREDENTIALS_INVALID.getName());
				return badRequest(msg.toJson().toString());
			}
			entity.setPubId(amPair.getAccount().getId());
			ReportingEntity reportingEntity = entity.getReportingEntity();
			org.codehaus.jackson.JsonNode resultNode = ApiDef.get_data(con, reportingEntity, monitoring, false, null);
			return ok(resultNode.toString());
		}catch(Exception e){
			Logger.error(e.getMessage(),e);
			Message msg = new Message();
			msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
			return badRequest(msg.toJson().toString());
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}
	}
	public static Result limitedreportDataCSV(){
        return reportDataCSV(null,true);
    }
	public static Result reportDataCSV(){
	    return reportDataCSV(null,false);
	}
	public static Result reportDataCSV(String hierarchyType, boolean isLimited){
        String pre  = "public/";
        String post = "reporting/download/csv/"+SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString()+".csv";
	    File file = new File(pre+post).getAbsoluteFile();
	    reportData(false, true, file.getAbsolutePath(), hierarchyType,isLimited);
        ObjectNode result = Json.newObject();
        result.put("downloadurl", controllers.routes.StaticFileController.download(Option.apply(post)).url());
        return ok(result);
	}
	public static Result reportData(){
	    return reportData(false, false, null, null,false);
	}
    public static Result limitedreportData(){
        return reportData(false, false, null, null,true);
    }
	public static Result reportData(boolean returnWithId, boolean exportAsCsv, String absoluteFileName, String hierarchyType, boolean isLimited){
		JsonNode result = new ObjectNode(JsonNodeFactory.instance);
		Form<ReportFormEntity> filledFilterForm = reportConfigForm.bindFromRequest();
		Connection con = null;
		if(!filledFilterForm.hasErrors()){
			try {
				ReportFormEntity reportFormEntity = filledFilterForm.get();
				ReportingEntity reportingEntity = reportFormEntity.getReportEntity(); 
				con =  DB.getConnection(); 
				if(exportAsCsv){
				    reportingEntity.setStartindex(PageConstants.start_index);
				    reportingEntity.setPagesize(PageConstants.csv_page_size);
				    reportingEntity.setRollup(true);
				}
				HierarchyPopulator.populate(reportingEntity, hierarchyType);
				if("GLOBAL".equals(hierarchyType)){
				    if("".equals(reportingEntity.getStart_time_str())){
				        return ok(result);
				    }
				}
				if(isLimited){
				    reportingEntity.setReportingDIMTypeEnum(ReportingDIMTypeEnum.LIMITED);
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

	public static Result sitesByPublishers(String pubList){
		ArrayNode siteOptions = MetadataAPI.sitesByPublishers(pubList); 
		return ok(siteOptions);
	}

	public static Result campaignsByAdvertiser(String advtList){
		String guids[] = advtList.split(",");
		String tmp = "";
		for (String guid : guids) {
			tmp +=  "'"+ guid +"',";
		}
		tmp = tmp.substring(0, tmp.length()-1);
		ArrayNode campaignOptions = MetadataAPI.campaignsByAccount(tmp); 
		return ok(campaignOptions);
	}

	public static Result adsByCampaign(String campaignList){
		ArrayNode adOptions = MetadataAPI.adsByCampaign(campaignList); 
		return ok(adOptions);
	}

	public static Result data(){ 
		ObjectNode defaultOptions = new ObjectNode(JsonNodeFactory.instance);
		Connection con = null;
		try {
				con = DB.getConnection();
				org.codehaus.jackson.JsonNode jsonNode = ApiDef.get_data(con, "UTC");
				if(jsonNode == null) { return ok(defaultOptions);}
				defaultOptions.put("topAdvertiserByRevenueYes",convert(jsonNode.path("topAdvertiserByRevenueYes")));
				defaultOptions.put("topCampaignByRevenueYes",convert(jsonNode.path("topCampaignByRevenueYes")));
				defaultOptions.put("topPublisherByIncomeYes",convert(jsonNode.path("topPublisherByIncomeYes")));
				defaultOptions.put("topSiteByIncomeYes",convert(jsonNode.path("topSiteByIncomeYes")));
				defaultOptions.put("topExchangeByIncomeYes",convert(jsonNode.path("topExchangeByIncomeYes")));
				defaultOptions.put("topGainerAdvertiserByRevenueYesDayBefore",convert(jsonNode.path("topGainerAdvertiserByRevenueYesDayBefore")));
				defaultOptions.put("topGainerPublisherByPubIncomeYesDayBefore",convert(jsonNode.path("topGainerPublisherByPubIncomeYesDayBefore")));
				defaultOptions.put("topGainerCampaignByRevenueYesDayBefore",convert(jsonNode.path("topGainerCampaignByRevenueYesDayBefore")));
				defaultOptions.put("topGainerSiteByIncomeYesDayBefore",convert(jsonNode.path("topGainerSiteByIncomeYesDayBefore")));
				defaultOptions.put("topLooserAdvertiserByRevenueYesDayBefore",convert(jsonNode.path("topLooserAdvertiserByRevenueYesDayBefore")));
				defaultOptions.put("topLooserPublisherByIncomeYesDayBefore",convert(jsonNode.path("topLooserPublisherByIncomeYesDayBefore")));
				defaultOptions.put("topLooserCampaignByRevenueYesDayBefore", convert(jsonNode.path("topLooserCampaignByRevenueYesDayBefore")));
				defaultOptions.put("topLooserSiteByIncomeYesDayBefore",convert(jsonNode.path("topLooserSiteByIncomeYesDayBefore")));
				defaultOptions.put("topCountryByRequestYes",convert(jsonNode.path("topCountryByRequestYes")));
				defaultOptions.put("topOsByRequestYes",convert(jsonNode.path("topOsByRequestYes")));
				defaultOptions.put("topManufacturerByRequestYes",convert(jsonNode.path("topManufacturerByRequestYes")));
				defaultOptions.put("topBrowserByRequestYes",convert(jsonNode.path("topBrowserByRequestYes")));
				defaultOptions.put("RequestImpressionRender",convert(jsonNode.path("RequestImpressionRender")));
				defaultOptions.put("ImpressionWin",convert(jsonNode.path("ImpressionWin")));
				defaultOptions.put("RevenuePubIncome",convert(jsonNode.path("RevenuePubIncome")));
				defaultOptions.put("ClickConversion",convert(jsonNode.path("ClickConversion")));
		} catch (Exception e) {
			Logger.error("Error fetching dashboadrd data", e);
		}
		finally{
			try {
				if(con != null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed to close connection in Reporting Entity",e2);
			}

		} 
		return ok(defaultOptions);
	}

	private static JsonNode getPiechartData(Connection con, int duration, String pieName){
		ReportingEntity re = new ReportingEntity();
		re.setTop_n_for_last_x_hours(duration);
		if("countryDistribution".equals(pieName)){
			re.setCountryId(new LinkedList<Integer>());
		}else if("carrierDistribution".equals(pieName)){
			re.setCountryCarrierId(new LinkedList<Integer>());
		}else if("manufacturerDistribution".equals(pieName)){
			re.setDeviceManufacturerId(new LinkedList<Integer>());
		}else if("modelDistribution".equals(pieName)){
			re.setDeviceModelId(new LinkedList<Integer>());
		}else if("osDistribution".equals(pieName)){
			re.setDeviceOsId(new LinkedList<Integer>());
		}
		re.setTotal_request(true);
		re.setTotal_request_order_sequence(1);
		re.setTop_n_for_last_x_hours(duration);
		re.setFrequency(Frequency.ADMIN_INTERNAL_HOURLY);
		re.setPagesize(PageConstants.pie_page_size);
		return  convert(ApiDef.get_pie(con, re));
	}
	private static JsonNode getBarchartData(Connection con, int duration){
		ReportingEntity re = new ReportingEntity(); 
		re.setTotal_request(true);
		re.setTotal_impression(true);
		re.setTop_n_for_last_x_hours(duration);
		re.setFrequency(Frequency.ADMIN_INTERNAL_HOURLY); 
		re.setPagesize(PageConstants.pie_page_size);
		return  convert(ApiDef.get_bar(con, re));
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
    @SecuredAction
    public static Result hierarchicalGlobal(){
        ReportFormEntity rfe = new ReportFormEntity(new ReportingEntity());
        return ok(views.html.reporting.hierarchical.global.render(reportConfigForm.fill(rfe)));
    }
    public static Result hierarchyGlobalData(){
        return reportData(false, false, null, "GLOBAL",false);
    }
    public static Result hierarchyGlobalDataCSV(){
        return reportDataCSV("GLOBAL",false);
    }
    public static Result limitedhierarchyGlobalData(){
        return reportData(false, false, null, "GLOBAL",true);
    }
    public static Result limitedhierarchyGlobalDataCSV(){
        return reportDataCSV("GLOBAL",true);
    }
}
