package controllers.isp_mapping;

import java.sql.Connection;
import java.sql.SQLException;

import models.entities.isp_mapping.IspMappingFormEntity;
import models.formbinders.IspMappingWorkFlowEntity;


import play.Logger;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.isp_mapping.Isp_mapping;
import com.kritter.api.entity.isp_mapping.Isp_mappingListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Isp_mappingEnum;
import com.kritter.kritterui.api.def.ApiDef;



public class IspMappingController extends Controller{

    static Form<IspMappingFormEntity> ispMappingFormEntityData = Form.form(IspMappingFormEntity.class);
    static Form<IspMappingWorkFlowEntity> ispMappingWorkflowForm = Form.form(IspMappingWorkFlowEntity.class);

    @SecuredAction
    public static Result ispMappingQueue(Option<String> country){
        Form<IspMappingFormEntity> ispMappingFormEntityData = Form.form(IspMappingFormEntity.class);
        IspMappingFormEntity ispMappingFormEntity  = new IspMappingFormEntity();

        if(!country.isEmpty()){
            ispMappingFormEntity.setCountry(country.get());
        }
        return ok(views.html.operations.ispMappingQueueView.render(ispMappingFormEntityData.fill(ispMappingFormEntity)));
    }

    @SecuredAction
    public static Result deleteMapping(Option<String> country){
        Form<IspMappingFormEntity> ispMappingFormEntityData = Form.form(IspMappingFormEntity.class);
        IspMappingFormEntity ispMappingFormEntity  = new IspMappingFormEntity();

        if(!country.isEmpty()){
            ispMappingFormEntity.setCountry(country.get());
        }
        return ok(views.html.operations.ispMappingDeleteView.render(ispMappingFormEntityData.fill(ispMappingFormEntity)));
    }
    @SecuredAction
    public static Result rejectedMapping(Option<String> country){
        Form<IspMappingFormEntity> ispMappingFormEntityData = Form.form(IspMappingFormEntity.class);
        IspMappingFormEntity ispMappingFormEntity  = new IspMappingFormEntity();

        if(!country.isEmpty()){
            ispMappingFormEntity.setCountry(country.get());
        }
        return ok(views.html.operations.ispMappingRejectedView.render(ispMappingFormEntityData.fill(ispMappingFormEntity)));
    }

    
    @SecuredAction
    public static Result deleteIspMapping(int id){
        Connection con = null;
        ObjectNode response = Json.newObject();
        try{
                con = DB.getConnection(); 
                Isp_mappingListEntity isp_mappingListEntity = new Isp_mappingListEntity();
                isp_mappingListEntity.setIsp_mappingEnum(Isp_mappingEnum.mark_delete);
                isp_mappingListEntity.setId(id);
                ApiDef.update_isp_mapping(con, isp_mappingListEntity);
            return ok(response);
        }catch(Exception e){
            Logger.error("Error  while saving Isp Mapping:"+ e.getMessage(),e);
            return ok(response);
        }
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) {
                Logger.error(e.getMessage(),e);
            }
        }
    }
    
    @SecuredAction
    public static Result insertMappings(){
        Form<IspMappingWorkFlowEntity> ispMappingWorkFlowEntity = ispMappingWorkflowForm.bindFromRequest();
        Connection con = null;
        ObjectNode response = Json.newObject();
        try{
            if(!ispMappingWorkFlowEntity.hasErrors()){
                con = DB.getConnection(); 
                IspMappingWorkFlowEntity ismwe = ispMappingWorkFlowEntity.get();
                Isp_mapping isp_mapping = new Isp_mapping();
                isp_mapping.setCountry_name(ismwe.getCountry_name());
                isp_mapping.setIsp_name(ismwe.getIsp_name());
                isp_mapping.setIsp_ui_name(ismwe.getIsp_ui_name());
                ApiDef.insert_isp_mapping(con, isp_mapping, true);
            }
            return ok(response);
        }catch(Exception e){
            Logger.error("Error  while saving Isp Mapping:"+ e.getMessage(),e);
            return ok(response);
        }
        finally{
            try {
                if(con != null)
                    con.close();
            } catch (SQLException e) {
                Logger.error(e.getMessage(),e);
            }
        }
    }

    @SecuredAction
    public static Result ispMappingWorkflowForm(String countryName, String ispName, String action) {   
        IspMappingWorkFlowEntity ispMappingWorkFlowEntity = new IspMappingWorkFlowEntity(); 
        ispMappingWorkFlowEntity.setCountry_name(countryName);
        ispMappingWorkFlowEntity.setIsp_name(ispName);
        return ok(views.html.operations.isp_mapping.ispMappingWorkflowForm.render(ispMappingWorkflowForm.fill(ispMappingWorkFlowEntity),action));
    }

}
