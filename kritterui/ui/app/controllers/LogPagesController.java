package controllers;


import java.io.File;
import java.sql.Connection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.log.LogEntity;
import com.kritter.constants.PageConstants;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

import models.entities.logpages.LogPagesFormEntity;

import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;


public class LogPagesController extends Controller{

	static Form<LogPagesFormEntity> logPagesFormEntity = Form.form(LogPagesFormEntity.class);

    @SecuredAction
    public static Result logs() {
        return ok(views.html.logpages.logs.render(logPagesFormEntity.fill(new LogPagesFormEntity())));
    }
    
    public static Result dataCSV(){
        String pre  = "public/";
        String post = "logs/download/csv/"+SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString()+".csv";
        File file = new File(pre+post).getAbsoluteFile();
        data(true, file.getAbsolutePath());
        ObjectNode result = Json.newObject();
        result.put("downloadurl", controllers.routes.StaticFileController.download(Option.apply(post)).url());
        return ok(result);
    }
    @SecuredAction
    public static Result data(){
        return data(false, null);
    }
    @SecuredAction
    public static Result data(boolean exportAsCsv, String absoluteFileName ){
        JsonNode result = new ObjectNode(JsonNodeFactory.instance);
        Form<LogPagesFormEntity> filledFilterForm = logPagesFormEntity.bindFromRequest();
        Connection con = null;
        if(!filledFilterForm.hasErrors()){
            try {
                LogPagesFormEntity logFormEntity = filledFilterForm.get();
                LogEntity logEntity = logFormEntity.getLogEntity();
                con =  DB.getConnection();
                if(exportAsCsv){
                    logEntity.setStartindex(PageConstants.start_index);
                    logEntity.setPagesize(PageConstants.csv_page_size);
                }
                org.codehaus.jackson.JsonNode data = ApiDef.get_log(con, logEntity, exportAsCsv, absoluteFileName);
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
