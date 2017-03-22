package controllers.audience;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kritter.api.entity.audience.Audience;
import com.kritter.api.entity.audience.AudienceList;
import com.kritter.api.entity.audience.AudienceListEntity;

import com.kritter.constants.*;
import com.kritter.kritterui.api.def.ApiDef;

import models.audience.AudienceDisplay;
import models.audience.AudienceDisplayFull;
import models.audience.AudienceListDisplay;


import models.entities.audience.AudienceEntity;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.Play;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import views.html.audience.audienceForm;
import views.html.audience.audienceList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.response.msg.Message;


public class AudienceController extends Controller {

    private static Form<AudienceEntity> audienceFormTemplate = Form.form(AudienceEntity.class);
    private static String file_prefix_path = Play.application().configuration().getString("file_prefix_path");

    private static Audience getAudience(int audienceId, String accountGuid) {
        Connection con = null;
        Audience audience = null;
        try {
            con = DB.getConnection();
            AudienceListEntity ale = new AudienceListEntity();
            ale.setAudienceAPIEnum(AudienceAPIEnum.get_audience_of_account);
            ale.setAudience_id(audienceId);
            ale.setAccount_guid(accountGuid);
            AudienceList alist = ApiDef.various_get_audience(con, ale);
            if (alist.getMsg().getError_code() == 0) {
                if (alist.getAudience_list().size() > 0) {
                    audience = alist.getAudience_list().get(0);
                }
            }
        } catch (Exception e) {
            play.Logger.error(e.getMessage() + ".Error fetching aduience with id=" + audienceId, e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                Logger.error("Error closing DB connection in getAudience", e);
            }
        }
        return audience;
    }

    @SecuredAction
    public static Result add(String guid) {
        Audience audience = new Audience();
        AudienceEntity audienceEntity = new AudienceEntity();
        audienceEntity.setAccount_guid(guid);
        BeanUtils.copyProperties( audienceEntity,audience);
        //return ok(audienceForm.render( audienceFormTemplate.fill(audienceEntity) , new AudienceDisplay(audience), rhs));
        return ok(audienceForm.render(audienceFormTemplate.fill(audienceEntity), new AudienceDisplay(audience),guid));
    }

    @SecuredAction
    public static Result save() {
        Form<AudienceEntity> audienceFormEntity = audienceFormTemplate.bindFromRequest();
        Audience audience = new Audience();
        String guid = audienceFormEntity.field("account_guid").value();
        AudienceEntity audienceEntity = null;
        if (audienceFormEntity.hasErrors()) {
            audienceEntity = new AudienceEntity();
            audienceEntity.setAccount_guid(guid);
            return badRequest(audienceForm.render(audienceFormEntity, new AudienceDisplay(audience),guid));
        } else {
            audienceEntity = audienceFormEntity.get();
            audienceEntity.setSource_id(audienceFormEntity.field("Fruit").value());
            audienceEntity.setTags(audienceFormEntity.field("selected_tag_codes").value());
            if (audienceEntity.getTags() == "" || audienceEntity.getTags() == null) {
                audienceEntity = new AudienceEntity();
                audienceEntity.setAccount_guid(guid);
                return badRequest(audienceForm.render(audienceFormEntity.fill(audienceEntity), new AudienceDisplay(audience),guid));
            }
            audienceEntity.setType(1);
            audience = audienceEntity.getEntity();
            Connection con = null;
            try {
                con = DB.getConnection();
                if (con != null) {
                    Message msg = null;
                    if(audienceEntity.getDeleted()==1){
                        msg = ApiDef.insert_audience_tags(con, audience);
                    }else{
                        msg = ApiDef.updata_audience_tags(con, audience);
                    }

                    //    audienceFormEntity.setId(audience.getId());
                    if (msg.getError_code() == 0) {
                        return ok(audienceForm.render(audienceFormEntity.fill(audienceEntity),new AudienceDisplay(audience), guid));
                    }
                } else {
                    return badRequest(audienceForm.render(audienceFormEntity, new AudienceDisplay(audience),guid));
                }
            } catch (
                    Exception e)

            {
                Logger.error("Error  while saving Site:" + e.getMessage(), e);
                return badRequest(audienceForm.render(audienceFormEntity, new AudienceDisplay(audience),guid));
            } finally

            {
                try {
                    if (con != null)
                        con.close();
                } catch (SQLException e) {
                    Logger.error(e.getMessage(), e);
                }
            }
        }
        audienceEntity = new AudienceEntity();
        audienceEntity.setAccount_guid(guid);
        return badRequest(audienceForm.render(audienceFormEntity.fill(audienceEntity), new AudienceDisplay(audience),guid));
    }

    @SecuredAction
    public static Result edit(int audienceId,String accountGuid){
        Audience audience = getAudience(audienceId , accountGuid);
        AudienceEntity audienceEntity = new AudienceEntity();
        BeanUtils.copyProperties(audience, audienceEntity);
        if(audience!= null){
            return ok(audienceForm.render(audienceFormTemplate.fill(audienceEntity),new AudienceDisplay(audience),accountGuid));
        }else
            return badRequest();
    }

    @SecuredAction
    public static Result view(int audienceId,String accountGuid){
        Audience audience = getAudience(audienceId , accountGuid);
        AudienceEntity audienceEntity = new AudienceEntity();
        BeanUtils.copyProperties(audience, audienceEntity);
        if(audience!= null){
            return ok(views.html.audience.audienceHome.render(new AudienceDisplayFull(audience)));
        }else
            return badRequest();
    }

//    public static List<Audience> getAudienceList(String accountGuid) {
//        List<Audience> audiences = null;
//        Connection con = null;
//        try {
//            con = DB.getConnection();
//            AudienceListEntity cle = new AudienceListEntity();
//
//            cle.setAudienceAPIEnum(AudienceAPIEnum.get_audience_of_account);
//
//            cle.setAccount_guid(accountGuid);
//
//
//            AudienceList cl = ApiDef.list_audience(con, cle);
//            if (cl.getMsg().getError_code() == 0) {
//                if (cl.getAudience_list().size() > 0) {
//                    audiences = cl.getAudience_list();
//                }
//            } else {
//                audiences = new ArrayList<Audience>();
//            }
//        } catch (Exception e) {
//            play.Logger.error(e.getMessage() + ".Error fetching audience list", e);
//        } finally {
//            try {
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException e) {
//                Logger.error("Error closing DB connection while fetching list of audiences in AudienceController", e);
//            }
//        }
//        return ok(audienceList.render(audiences));
//
//    }

//    public static Result audience(String accountGuid, Option<Integer> pageNo, Option<Integer> pageSize) {
//        List<Audience> clist = null;
//        clist = getAudienceList(accountGuid);
//
//
//        ObjectNode result = Json.newObject();
//
//        ArrayNode cnodes = result.putArray("list");
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode cnode = null;
//        for (Audience audience : clist) {
//            cnode = objectMapper.valueToTree(audience);
//            cnode.put("edit_url", routes.AudienceController.edit(audience.getId(),accountGuid).url());
//            cnode.put("view_url", routes.AudienceController.view(audience.getId(),accountGuid).url());
//            cnodes.addPOJO(cnode);
//        }
//        result.put("size", clist.size());
//
//        return ok(result);
//
//    }
    public static Result list(String accountGuid) {
        Account account = DataAPI.getAccountByGuid(accountGuid);
        AudienceListDisplay audienceDisplay = new AudienceListDisplay(account);
        return ok(audienceList.render(audienceDisplay));
    }
}