package controllers.ssp;

import java.sql.Connection;
import java.sql.SQLException;

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
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.ssp.SSPEntity;
import com.kritter.kritterui.api.def.ApiDef;
import models.entities.ssp.SSPFormEntity;

public class SSPController extends Controller{

    static Form<SSPFormEntity> sspForm = Form.form(SSPFormEntity.class);

    @SecuredAction
    public static Result globalwaterfall(){
        return ok(views.html.ssp.sspglobal.render(sspForm.fill(new SSPFormEntity())));
    }
    @SecuredAction
    public static Result global_data(){
        JsonNode result = new ObjectNode(JsonNodeFactory.instance);
        Connection con = null;
        try {
            con =  DB.getConnection();
            org.codehaus.jackson.JsonNode data = ApiDef.ssp_global_data(con);
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
                Logger.error("Failed to close connection in SSPController global_data",e2);
            }
        }

        return ok(result);
    }

    @SecuredAction
    public static Result update_insert(){   
        Form<SSPFormEntity> sspForm1 = sspForm.bindFromRequest(); 
        if (sspForm1.hasErrors()) {
            return badRequest(views.html.ssp.sspglobal.render(sspForm1));
        }else{
            SSPEntity sspEntity = sspForm1.get().getEntity();
            Connection con = null;
            try{
                con = DB.getConnection(); 
                if(con != null){
                    Message msg = ApiDef.insert_update_global_rules(con, sspEntity);   
                    if(msg.getError_code() ==0){ 
                        return ok(views.html.ssp.sspglobal.render(sspForm.fill(new SSPFormEntity())));
                    }else{
                        return badRequest(views.html.ssp.sspglobal.render(sspForm.fill(new SSPFormEntity())));
                    }
                }
                return ok(views.html.ssp.sspglobal.render(sspForm.fill(new SSPFormEntity())));
            }catch(Exception e){
                Logger.error("Error  while saving SSP Global rules:"+ e.getMessage(),e);
                return badRequest(views.html.ssp.sspglobal.render(sspForm.fill(new SSPFormEntity())));
            }finally{
                try {
                    if(con != null)
                        con.close();
                } catch (SQLException e) {
                    Logger.error(e.getMessage(),e);           
                }
            }
        }
    }
}
