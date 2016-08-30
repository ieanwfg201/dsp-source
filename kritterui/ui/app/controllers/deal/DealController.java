package controllers.deal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.Constants.Actions;
import models.entities.DealFormEntity;
import models.pmp.display.PMPDisplay;
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
    static Form<DealFormEntity> dealFormTemplate =  Form.form(DealFormEntity.class);

    @SecuredAction
    public static Result getDealForm(){
        DealFormEntity dealEntity = new DealFormEntity();
        return ok(views.html.navs.pmp.render(dealFormTemplate.fill(dealEntity)));
    }

    @SecuredAction
    public static Result save()
    {
        Form<DealFormEntity> dealForm  = dealFormTemplate.bindFromRequest();
        PrivateMarketPlaceApiEntity pmp = null;

        if(!"cancel".equals(dealForm.data().get("action")) && !dealForm.hasErrors())
        {
            Connection con = null;

            try
            {
                con = DB.getConnection();
                DealFormEntity deal = dealForm.get();
                pmp = deal.getEntity();
                Message msg = null;

                if(deal.isEdit == 1)
                    msg = ApiDef.update_pmp_deal(con,pmp);
                else
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

        return PMPListView();
    }

    @SecuredAction
    public static Result edit(   String pmpId )
    {
        PrivateMarketPlaceApiEntity deal = DataAPI.getPMPDealByGuid(pmpId);
        deal.setIsEdit(1);
        DealFormEntity dealEntity = new DealFormEntity();
        BeanUtils.copyProperties(deal, dealEntity);

        if(deal != null)
            return ok(views.html.navs.pmp.render(dealFormTemplate.fill(dealEntity)));
        else
            return ok("Invalid Deal Id supplied");
    }

    @SecuredAction
    public static Result PMPListView() {
        return ok(views.html.navs.pmpListPage.render());
    }

    @SecuredAction
    public static Result deals()
    {
        return PMPListView();
    }

    @SecuredAction
    public static Result info(String dealId)
    {
        PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity = DataAPI.getPMPDealByGuid(dealId);
        return ok(views.html.navs.pmpInfo.render(new PMPDisplay(privateMarketPlaceApiEntity)));
    }

    @SecuredAction
    public static Result dealList(Option<String> destination)
    {
        List<PrivateMarketPlaceApiEntity> privateMarketPlaceApiEntityList = getDeals();

        ObjectNode result = Json.newObject();
        ArrayNode cnodes = result.putArray("list");
        ObjectNode snode = null;
        ObjectMapper objectMapper = new ObjectMapper();
        PMPDisplay pmpDisplay = null;

        String destinationUrl = null;

        if(destination.nonEmpty())
            destinationUrl = destination.get();

        for (PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity : privateMarketPlaceApiEntityList)
        {
            pmpDisplay = new PMPDisplay(privateMarketPlaceApiEntity);
            if(destinationUrl != null)
                pmpDisplay.setDestination(destinationUrl);
            snode= objectMapper.valueToTree(pmpDisplay);
            cnodes.addPOJO(snode);
        }

        result.put("size", privateMarketPlaceApiEntityList.size());
        return ok(result);
    }

    private static List<PrivateMarketPlaceApiEntity> getDeals()
    {
        List<PrivateMarketPlaceApiEntity> deals = null;
        Connection con = null;

        try
        {
            con = DB.getConnection();
            return DataAPI.getPMPDeals();
        }
        catch(Exception e)
        {
            play.Logger.error(e.getMessage()+".Error fetching campaign list",e);
        }
        finally
        {
            try
            {
                if(con != null)
                    con.close();
            }
            catch (SQLException e)
            {
                Logger.error("Error closing DB connection while fetching list of sites in PublisherController",e);
            }
        }

        return deals;
    }

    @SecuredAction
    public static Result add()
    {
        PrivateMarketPlaceApiEntity deal = new PrivateMarketPlaceApiEntity();
        DealFormEntity dealEntity = new DealFormEntity();

        BeanUtils.copyProperties(deal, dealEntity);

        return ok(views.html.navs.pmp.render(dealFormTemplate.fill(dealEntity)));
    }
}