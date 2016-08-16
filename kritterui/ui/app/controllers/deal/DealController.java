package controllers.deal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import models.Constants.Actions;
import org.springframework.beans.BeanUtils;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.libs.Scala;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.DataAPI;
import services.MetadataAPI;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.DealEntity;
import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;
import com.kritter.kritterui.api.def.ApiDef;
import com.kritter.api.entity.response.msg.Message;

/**
 * This class manages deal methods invoked by views, using routes configuration.
 */
public class DealController extends Controller
{
    private static Form<DealEntity> dealFormTemplate =  Form.form(DealEntity.class);

    @SecuredAction
    public static Result getDealForm(){
        DealEntity dealEntity = new DealEntity();
        return ok(views.html.navs.pmp.render(dealFormTemplate.fill(dealEntity)));
    }

    @SecuredAction
    public static Result save()
    {
        Form<DealEntity> dealForm  = dealFormTemplate.bindFromRequest();
        PrivateMarketPlaceApiEntity pmp = null;

        if(!dealForm.hasErrors())
        {
            Connection con = null;

            try
            {
                con = DB.getConnection();
                DealEntity deal = dealForm.get();
                pmp = deal.getEntity();
                Message msg = null;
                msg = ApiDef.insert_pmp_deal(con, pmp);

                if(msg.getError_code() != 0)
                    return badRequest();
            }
            catch (Exception e)
            {
                Logger.error("Error while saving Deal inside DealController ",e);
            }
            finally
            {
                try
                {
                    if(con != null)
                    {
                        con.close();
                    }
                }
                catch (SQLException e)
                {
                    Logger.error("Error closing DB connection while saving Deal inside DealController",e);
                }
            }

        }

        return ok("{\"code\":\"success\"}");
    }
}