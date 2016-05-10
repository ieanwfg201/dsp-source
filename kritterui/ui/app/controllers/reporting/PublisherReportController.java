package controllers.reporting;


import models.Constants.ReportDataType;
import models.entities.reporting.ReportFormEntity;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.ReportingDataService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.reporting.ReportingEntity;


public class PublisherReportController extends Controller{

    private static Form<ReportFormEntity> reportConfigForm = Form.form(ReportFormEntity.class); 

//    @SecuredAction
    public static Result allPublishersReport(){
        return ok(views.html.reporting.staticReports.allPublishersReport.render("publisherDataTable"));
    }

//    @SecuredAction
    public static Result allPublishersReportData(){
//    	 JsonNode result = null;
//         Form<ReportFormEntity> filledFilterForm = reportConfigForm.bindFromRequest(); 
//         if(!filledFilterForm.hasErrors()){ 
//                 ReportFormEntity reportFormEntity = filledFilterForm.get();
//                 ReportingEntity reportingEntity = reportFormEntity.getReportEntity();	 
//                 addPublisherMetrics(reportingEntity);
//                 result =  ReportingDataService.getData(reportingEntity, ReportDataType.TABLE, true); 
//         } 
         return ok(dummyData());
    }

    private static JsonNode dummyData(){
    	 ObjectMapper objectMapper = new ObjectMapper();
    	 ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
        
    	String columns = "[{ \"field\":\"col1\",\"title\":\"Col 1\",\"idfield\":\"col1id\", \"drilldown\":true, \"visible\":true},"
    									+ "{ \"field\":\"col2\",\"title\":\"Col 2\",\"idfield\":\"\", \"drilldown\":false, \"visible\":true}, "
    									+ "{ \"field\":\"col3\",\"title\":\"Col 3\",\"idfield\":\"\", \"drilldown\":false, \"visible\":true}]";
    	String data="[{\"col1\":\"Record 1\", \"col2\":\"Val 1\", \"col3\":\"Val 2\", \"col1id\":\"record_1_id\"}, "
    				+ "{\"col1\":\"Record 2\", \"col2\":\"Val 1\", \"col3\":\"Val 2\", \"col1id\":\"record_2_id\"}, "
    				+ "{\"col1\":\"Record 3\", \"col2\":\"Val 1\", \"col3\":\"Val 2\",\"col1id\":\"record_3_id\"}]";
    	
    	 try {
             result.put("column", objectMapper.readTree(columns.toString()));
             result.put("data", objectMapper.readTree(data.toString()));
         } catch (Exception  e) { 
             play.Logger.error(e.getMessage(),e);
         } 
    	 return result;
    }
    private static void addPublisherMetrics(ReportingEntity re){ 
        re.setTotal_request(true);
        re.setTotal_impression(true);
        re.setTotal_click(true);
        re.setPagesize(10);
    }
    
    @SecuredAction
    public static Result publisherReport(int publisherId){
        return ok(views.html.reporting.staticReports.publisherReport.render("publisherDataTable"));
    }
    
    @SecuredAction
    public static Result publisherReportData(int publisherId){
    	JsonNode result = null;
        Form<ReportFormEntity> filledFilterForm = reportConfigForm.bindFromRequest(); 
        if(!filledFilterForm.hasErrors()){ 
                ReportFormEntity reportFormEntity = filledFilterForm.get();
                ReportingEntity reportingEntity = reportFormEntity.getReportEntity();
                
                result =  ReportingDataService.getData(reportingEntity, ReportDataType.TABLE, true); 
        } 
        return ok(result);
    }
    
}
