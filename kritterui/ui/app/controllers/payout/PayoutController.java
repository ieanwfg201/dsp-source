package controllers.payout;

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
import com.kritter.entity.payout_threshold.DefaultPayoutThreshold;
import com.kritter.kritterui.api.def.ApiDef;

import models.entities.payout.PayoutDefaultFormEntity;

public class PayoutController extends Controller{

    static Form<PayoutDefaultFormEntity> defaultPayoutForm = Form.form(PayoutDefaultFormEntity.class);

    @SecuredAction
    public static Result defaultpayout(){
        return ok(views.html.payout.defaultpayout.render(defaultPayoutForm.fill(new PayoutDefaultFormEntity())));
    }
    @SecuredAction
    public static Result defaultpayoutdata(){
        JsonNode result = new ObjectNode(JsonNodeFactory.instance);
        Connection con = null;
        try {
            con =  DB.getConnection();
            org.codehaus.jackson.JsonNode data = ApiDef.get_default_payout_data(con);
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
    public static Result defaultpayoutupdate(){   
        Form<PayoutDefaultFormEntity> defaultPayoutForm1 = defaultPayoutForm.bindFromRequest(); 
        if (defaultPayoutForm1.hasErrors()) {
            return badRequest(views.html.payout.defaultpayout.render(defaultPayoutForm1));
        }else{
            DefaultPayoutThreshold defaultPayoutEntity = defaultPayoutForm1.get().getEntity();
            Connection con = null;
            try{
                con = DB.getConnection(); 
                if(con != null){
                    Message msg = ApiDef.update_default_payout_threshold(con, defaultPayoutEntity) ;  
                    if(msg.getError_code() ==0){ 
                        return ok(views.html.payout.defaultpayout.render(defaultPayoutForm.fill(new PayoutDefaultFormEntity())));
                    }else{
                        return badRequest(views.html.payout.defaultpayout.render(defaultPayoutForm.fill(new PayoutDefaultFormEntity())));
                    }
                }
                return ok(views.html.payout.defaultpayout.render(defaultPayoutForm.fill(new PayoutDefaultFormEntity())));
            }catch(Exception e){
                Logger.error("Error  while saving Payout Threshold Global rules:"+ e.getMessage(),e);
                return badRequest(views.html.payout.defaultpayout.render(defaultPayoutForm.fill(new PayoutDefaultFormEntity())));
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
