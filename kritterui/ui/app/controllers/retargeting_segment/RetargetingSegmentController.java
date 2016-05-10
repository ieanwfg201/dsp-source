package controllers.retargeting_segment;

import java.sql.Connection;
import java.sql.SQLException;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.retargeting_segment.RetargetingSegment;
import com.kritter.entity.retargeting_segment.RetargetingTag;
import com.kritter.kritterui.api.def.ApiDef;
import models.advertiser.RetargetingSegmentDisplay;
import models.entities.retargeting_segment.RetargetingSegmentFormEntity;
import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;

public class RetargetingSegmentController extends Controller{

    static Form<RetargetingSegmentFormEntity> retargetingSegmentFormEntityModel = Form.form(RetargetingSegmentFormEntity.class);
    private static String retargeting_url = Play.application().configuration().getString("retargeting_url");


    @SecuredAction
    public static Result create(String  guid) { 
        RetargetingSegmentFormEntity retargetingSegmentFormEntity = new RetargetingSegmentFormEntity();
        retargetingSegmentFormEntity.setAccount_guid(guid);
        return ok(views.html.retargeting_segment.retargetingSegmentForm.render(retargetingSegmentFormEntityModel.fill(retargetingSegmentFormEntity),"true",guid));
    }

    @SecuredAction
    public static Result save() {   
        Form<RetargetingSegmentFormEntity> retargetingSegmentForm = retargetingSegmentFormEntityModel.bindFromRequest();
        String guid = retargetingSegmentForm.field("account_guid").value();
        RetargetingSegmentFormEntity retargetingSegmentFormEntity = null;
        if(!"cancel".equals(retargetingSegmentForm.data().get("action"))){
            if (retargetingSegmentForm.hasErrors()) {
                retargetingSegmentFormEntity = new RetargetingSegmentFormEntity();
                retargetingSegmentFormEntity.setAccount_guid(guid);
                return badRequest( views.html.retargeting_segment.retargetingSegmentForm.render(retargetingSegmentForm,"true",guid));
            }else{
                retargetingSegmentFormEntity = retargetingSegmentForm.get();
                RetargetingSegment retargetingSegment = retargetingSegmentFormEntity.getEntity();
                retargetingSegment.setModified_by(1);
                Connection con = null;
                try{
                    con = DB.getConnection(); 
                    if(con != null){
                        Message msg = null;   
                        boolean isUpdate =false;
                        if(retargetingSegment.getRetargeting_segment_id() == -1){
                            msg = ApiDef.insert_retargeting_segment(con, retargetingSegment);
                            retargetingSegmentFormEntity.setRetargeting_segment_id(retargetingSegment.getRetargeting_segment_id());
                            String tag = RetargetingTag.getSegemntTag(retargeting_url, retargetingSegment.getRetargeting_segment_id());
                            retargetingSegment.setTag(tag);
                            retargetingSegmentFormEntity.setTag(tag);
                        } else{
                            msg = ApiDef.update_retargeting_segment(con, retargetingSegment);
                            String tag = RetargetingTag.getSegemntTag(retargeting_url, retargetingSegment.getRetargeting_segment_id());
                            retargetingSegment.setTag(tag);
                            retargetingSegmentFormEntity.setTag(tag);
                            isUpdate=true;
                            
                        } 
                        if(msg.getError_code() ==0){
                            if(isUpdate){
                                return redirect(routes.RetargetingSegmentController.list(guid).url());
                            }
                            return ok( views.html.retargeting_segment.retargetingSegmentForm.render(retargetingSegmentForm.fill(retargetingSegmentFormEntity),"false",guid));
                        }else{
                            return badRequest( views.html.retargeting_segment.retargetingSegmentForm.render(retargetingSegmentForm,"true",guid));
                        }
                    }
                }catch(Exception e){
                    Logger.error("Error  while saving Site:"+ e.getMessage(),e);
                    return badRequest( views.html.retargeting_segment.retargetingSegmentForm.render(retargetingSegmentForm,"true",guid));
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
        retargetingSegmentFormEntity = new RetargetingSegmentFormEntity();
        retargetingSegmentFormEntity.setAccount_guid(guid);
        return badRequest( views.html.retargeting_segment.retargetingSegmentForm.render(retargetingSegmentForm.fill(retargetingSegmentFormEntity),"true",guid));
    }
    @SecuredAction
    public static Result list(String accountGuid){  
        RetargetingSegment retargetingSegment = new RetargetingSegment();
        retargetingSegment.setAccount_guid(accountGuid);
        RetargetingSegmentDisplay rsd = new RetargetingSegmentDisplay(retargetingSegment);
        return ok(views.html.retargeting_segment.retargetingSegmentList.render(rsd));             
    }

}
