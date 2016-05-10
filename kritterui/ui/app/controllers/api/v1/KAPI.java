package controllers.api.v1;

import java.sql.Connection;
import java.sql.SQLException;

import models.StaticUtils;


import play.Logger;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import services.MetadataAPI;
import services.TPMetadataAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.account_budget.Account_Budget_Msg;
import com.kritter.api.entity.account_budget.Account_budget;
import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.ad.AdList;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.campaign.CampaignListEntity;
import com.kritter.api.entity.campaign_budget.CampaignBudgetList;
import com.kritter.api.entity.campaign_budget.CampaignBudgetListEntity;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.api.entity.creative_banner.CreativeBannerList;
import com.kritter.api.entity.creative_banner.CreativeBannerListEntity;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.creative_banner.ImageUploadResponse;
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.insertion_order.IOListEntity;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.insertion_order.Insertion_Order_List;
import com.kritter.api.entity.metadata.MetaInput;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.saved_query.SavedQueryEntity;
import com.kritter.api.entity.saved_query.SavedQueryList;
import com.kritter.api.entity.saved_query.SavedQueryListEntity;
import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.api.entity.targeting_profile.FileUploadResponse;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.Account_Type;
import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.CampaignQueryEnum;
import com.kritter.constants.CreativeBannerAPIEnum;
import com.kritter.constants.CreativeContainerAPIEnum;
import com.kritter.constants.SavedQueryEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.TargetingProfileAPIEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.def.ApiDef;

import controllers.advertiser.CreativeController;
import controllers.advertiser.TargetingProfileController;

public class KAPI extends Controller{
    public static Result reporting(){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            ReportingEntity entity = ReportingEntity.getObject(jsonNode.toString());
            org.codehaus.jackson.JsonNode resultNode = ApiDef.get_data(con, entity, false, false, null);
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
    public static Result reportingwithId(){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            ReportingEntity entity = ReportingEntity.getObject(jsonNode.toString());
            org.codehaus.jackson.JsonNode resultNode = ApiDef.get_data(con, entity, true, false, null);
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
    public static Result account(String accountType,String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            if(!("pub".equals(accountType) ||"adv".equals(accountType))){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ACCOUNT_TYPE_ABSENT.getId());
                msg.setMsg(ErrorEnum.ACCOUNT_TYPE_ABSENT.getName());
                return ok(msg.toJson().toString());
            }
            con = DB.getConnection();
            Account entity = Account.getObject(jsonNode.toString());
            if("pub".equals(accountType)){
               entity.setType_id(Account_Type.directpublisher);
            }else if("adv".equals(accountType)){
                entity.setType_id(Account_Type.directadvertiser);
             } 
            if("create".equals(actionType)){
                Message msg  = ApiDef.createAccount(con, entity);
                entity.setStatus(StatusIdEnum.Pending);
                return ok(msg.toJson().toString());
            }else if("verify".equals(actionType)){
                Message msg  = ApiDef.verifyAccount(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                AccountMsgPair msg  = ApiDef.get_Account_By_UserId_Pwd(con, entity);
                return ok(msg.toJson().toString());
            }else if("getbyuserid".equals(actionType)){
                AccountMsgPair msg  = ApiDef.get_Account(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Message msg  = ApiDef.updateAccount(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result account_budget(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Account_budget entity = Account_budget.getObject(jsonNode.toString());
                Message msg  = ApiDef.insert_account_budget(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                Account_budget entity = Account_budget.getObject(jsonNode.toString());
                Account_Budget_Msg msg  = ApiDef.get_Account_Budget(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Account_budget entity = Account_budget.getObject(jsonNode.toString());
                Message msg  = ApiDef.update_account_budget(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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

    public static Result dashboard(String type,String guid){
        Connection con = null;
        try{
            con = DB.getConnection();
            if("adv".equals(type)){
                return ok(ApiDef.get_adv_dashboard(con, "UTC",guid).toString());
            }else if("pub".equals(type)){
                return ok(ApiDef.get_pub_dashboard(con, "UTC",guid).toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());
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
    public static Result io(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Insertion_Order entity = Insertion_Order.getObject(jsonNode.toString());
                Message msg  = ApiDef.insert_io(con, entity);
                return ok(msg.toJson().toString());
            }else if("check".equals(actionType)){
                Insertion_Order entity = Insertion_Order.getObject(jsonNode.toString());
                Message msg  = ApiDef.check_io(con, entity);
                return ok(msg.toJson().toString());
            }else if("listiobyaccountbystatus".equals(actionType)){
                IOListEntity entity = IOListEntity.getObject(jsonNode.toString());
                Insertion_Order_List msg  = ApiDef.list_io_by_account_guid_by_status(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result site(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Site entity = Site.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Pending.getCode());
                Message msg  = ApiDef.insert_site(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Site entity = Site.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Pending.getCode());
                Message msg  = ApiDef.update_site(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                SiteListEntity entity = SiteListEntity.getObject(jsonNode.toString());
                SiteList msg  = ApiDef.get_site(con, entity);
                return ok(msg.toJson().toString());
            }else if("list".equals(actionType)){
                SiteListEntity entity = SiteListEntity.getObject(jsonNode.toString());
                SiteList msg  = ApiDef.list_site_by_account_id(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result campaign(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Campaign entity = Campaign.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Paused.getCode());
                Message msg  = ApiDef.insert_campaign(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Campaign entity = Campaign.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Paused.getCode());
                Message msg  = ApiDef.update_campaign(con, entity);
                return ok(msg.toJson().toString());
            }else if("pause".equals(actionType)){
                Campaign entity = Campaign.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Paused.getCode());
                Message msg  = ApiDef.pause_campaign(con, entity);
                return ok(msg.toJson().toString());
            }else if("activate".equals(actionType)){
                Campaign entity = Campaign.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Active.getCode());
                Message msg  = ApiDef.activate_campaign(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                CampaignListEntity entity = CampaignListEntity.getObject(jsonNode.toString());
                entity.setCampaignQueryEnum(CampaignQueryEnum.get_campaign_by_id);
                CampaignList msg  = ApiDef.list_campaign(con, entity);
                return ok(msg.toJson().toString());
            }else if("list".equals(actionType)){
                CampaignListEntity entity = CampaignListEntity.getObject(jsonNode.toString());
                entity.setCampaignQueryEnum(CampaignQueryEnum.list_campaign_of_account);
                CampaignList msg  = ApiDef.list_campaign(con, entity);
                return ok(msg.toJson().toString());
            }else if("expiredlist".equals(actionType)){
                CampaignListEntity entity = CampaignListEntity.getObject(jsonNode.toString());
                entity.setCampaignQueryEnum(CampaignQueryEnum.list_all_expired_campaign_of_account);
                CampaignList msg  = ApiDef.list_campaign(con, entity);
                return ok(msg.toJson().toString());
            }else if("nonexpiredlist".equals(actionType)){
                CampaignListEntity entity = CampaignListEntity.getObject(jsonNode.toString());
                entity.setCampaignQueryEnum(CampaignQueryEnum.list_all_non_expired_campaign_of_account);
                CampaignList msg  = ApiDef.list_campaign(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result campaignbudget(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Campaign_budget entity = Campaign_budget.getObject(jsonNode.toString());
                Message msg  = ApiDef.insert_campaign_budget(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Campaign_budget entity = Campaign_budget.getObject(jsonNode.toString());
                Message msg  = ApiDef.update_campaign_budget(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                CampaignBudgetListEntity entity = CampaignBudgetListEntity.getObject(jsonNode.toString());
                CampaignBudgetList msg  = ApiDef.get_campaign_budget(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result ad(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Ad entity = Ad.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Pending);
                Message msg  = ApiDef.insert_ad(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Ad entity = Ad.getObject(jsonNode.toString());
                entity.setStatus_id(StatusIdEnum.Pending);
                Message msg  = ApiDef.update_ad(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                AdListEntity entity = AdListEntity.getObject(jsonNode.toString());
                entity.setAdenum(AdAPIEnum.get_ad);
                AdList msg  = ApiDef.various_get_ad(con, entity);
                return ok(msg.toJson().toString());
            }else if("list".equals(actionType)){
                AdListEntity entity = AdListEntity.getObject(jsonNode.toString());
                entity.setAdenum(AdAPIEnum.list_ad_by_campaign);
                AdList msg  = ApiDef.various_get_ad(con, entity);
                return ok(msg.toJson().toString());
            }else if("listbymultiplecampaigns".equals(actionType)){
                AdListEntity entity = AdListEntity.getObject(jsonNode.toString());
                entity.setAdenum(AdAPIEnum.list_ad_by_campaigns);
                AdList msg  = ApiDef.various_get_ad(con, entity);
                return ok(msg.toJson().toString());
            }else if("pause".equals(actionType)){
                AdListEntity entity = AdListEntity.getObject(jsonNode.toString());
                entity.setAdenum(AdAPIEnum.pause_ad_by_ids);
                Message msg  = ApiDef.change_status_ad(con, entity);
                return ok(msg.toJson().toString());
            }else if("activate".equals(actionType)){
                AdListEntity entity = AdListEntity.getObject(jsonNode.toString());
                entity.setAdenum(AdAPIEnum.activate_ad_by_ids);
                Message msg  = ApiDef.change_status_ad(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result targetingprofile(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Targeting_profile entity = Targeting_profile.getObject(jsonNode.toString());
                Message msg  = ApiDef.insert_targeting_profile(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Targeting_profile entity = Targeting_profile.getObject(jsonNode.toString());
                Message msg  = ApiDef.update_targeting_profile(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                TargetingProfileListEntity entity = TargetingProfileListEntity.getObject(jsonNode.toString());
                entity.setTpEnum(TargetingProfileAPIEnum.get_targeting_profile);
                TargetingProfileList msg  = ApiDef.various_get_targeting_profile(con, entity);
                return ok(msg.toJson().toString());
            }else if("list".equals(actionType)){
                TargetingProfileListEntity entity = TargetingProfileListEntity.getObject(jsonNode.toString());
                entity.setTpEnum(TargetingProfileAPIEnum.list_active_targeting_profile_by_account);
                TargetingProfileList msg  = ApiDef.various_get_targeting_profile(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result creativecontainer(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Creative_container entity = Creative_container.getObject(jsonNode.toString());
                Message msg  = ApiDef.insert_creative_container(con, entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Creative_container entity = Creative_container.getObject(jsonNode.toString());
                Message msg  = ApiDef.update_creative_container(con, entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                CreativeContainerListEntity entity = CreativeContainerListEntity.getObject(jsonNode.toString());
                entity.setCcenum(CreativeContainerAPIEnum.get_creative_container);
                CreativeContainerList msg  = ApiDef.various_get_creative_container(con, entity);
                return ok(msg.toJson().toString());
            }else if("list".equals(actionType)){
                CreativeContainerListEntity entity = CreativeContainerListEntity.getObject(jsonNode.toString());
                entity.setCcenum(CreativeContainerAPIEnum.list_creative_container_by_account);
                CreativeContainerList msg  = ApiDef.various_get_creative_container(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result creativebanner(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                Creative_banner entity = Creative_banner.getObject(jsonNode.toString());
                Message msg  = ApiDef.insert_creative_banner(con,  entity);
                return ok(msg.toJson().toString());
            }else if("edit".equals(actionType)){
                Creative_banner entity = Creative_banner.getObject(jsonNode.toString());
                Message msg  = ApiDef.update_creative_banner(con,  entity);
                return ok(msg.toJson().toString());
            }else if("get".equals(actionType)){
                CreativeBannerListEntity entity = CreativeBannerListEntity.getObject(jsonNode.toString());
                entity.setCbenum(CreativeBannerAPIEnum.get_creative_banner);
                CreativeBannerList msg  = ApiDef.various_get_creative_banner(con, entity);
                return ok(msg.toJson().toString());
            }else if("list".equals(actionType)){
                CreativeBannerListEntity entity = CreativeBannerListEntity.getObject(jsonNode.toString());
                entity.setCbenum(CreativeBannerAPIEnum.get_creative_banner_by_ids);
                CreativeBannerList msg  = ApiDef.various_get_creative_banner(con, entity);
                return ok(msg.toJson().toString());
            }else if("listbyaccount".equals(actionType)){
                CreativeBannerListEntity entity = CreativeBannerListEntity.getObject(jsonNode.toString());
                entity.setCbenum(CreativeBannerAPIEnum.list_creative_banner_by_account);
                CreativeBannerList msg  = ApiDef.various_get_creative_banner(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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
    public static Result imageupload(String accountGuid){
        MultipartFormData multipartFormData = request().body().asMultipartFormData();
        try{
            if(accountGuid == null){
                ImageUploadResponse iur = new ImageUploadResponse();
                iur.setErrorCode(ErrorEnum.ACCOUNT_GUID_ABSENT.getId());
                iur.setMessage(ErrorEnum.ACCOUNT_GUID_ABSENT.getName());
                return ok(iur.toJson().toString());
            }
            return CreativeController.uploadBanner(accountGuid, true);
        }catch(Exception e){
            Logger.error(e.getMessage(),e);
            ImageUploadResponse iur = new ImageUploadResponse();
            iur.setErrorCode(ErrorEnum.INTERNAL_ERROR_CONTACT_ADMINISTRATOR.getId());
            iur.setMessage(ErrorEnum.INTERNAL_ERROR_CONTACT_ADMINISTRATOR.getName());
            return ok(iur.toJson().toString());
        }finally{
        }
    }
    public static Result ipfileupload(String accountGuid){
        try{
            if(accountGuid == null){
                FileUploadResponse iur = new FileUploadResponse();
                iur.setErrorCode(ErrorEnum.ACCOUNT_GUID_ABSENT.getId());
                iur.setMessage(ErrorEnum.ACCOUNT_GUID_ABSENT.getName());
                return ok(iur.toJson().toString());
            }
            return TargetingProfileController.uploadGeoTargetingData(true, "custom_ip_file_id_set", accountGuid);
        }catch(Exception e){
            Logger.error(e.getMessage(),e);
            FileUploadResponse iur = new FileUploadResponse();
            iur.setErrorCode(ErrorEnum.INTERNAL_ERROR_CONTACT_ADMINISTRATOR.getId());
            iur.setMessage(ErrorEnum.INTERNAL_ERROR_CONTACT_ADMINISTRATOR.getName());
            return ok(iur.toJson().toString());
        }finally{
        }
    }
    public static Result metafirstlevel(String metatype){
        try{
            if("countries".equals(metatype)){
                ArrayNode countryOptions = TPMetadataAPI.countryList(); 
                return ok(countryOptions);
            }else if("browsers".equals(metatype)){
                ArrayNode browserOptions = TPMetadataAPI.browserList(); 
                return ok(browserOptions);
            }else if("tier1categories".equals(metatype)){
                ArrayNode tier_1_categoryOptions = MetadataAPI.tier_1_categories(); 
                return ok(tier_1_categoryOptions);
            }else if("tier2categories".equals(metatype)){
                ArrayNode tier_2_categoryOptions = MetadataAPI.tier_2_categories(); 
                return ok(tier_2_categoryOptions);
            }else if("hours".equals(metatype)){
                ArrayNode adOptions = MetadataAPI.hourListOptions(); 
                return ok(adOptions);
            }else if("inventory_source".equals(metatype)){
                return ok(MetadataAPI.getInvSourceArrayNode());
            }else if("payment_type".equals(metatype)){
                return ok(StaticUtils.getPaymentOptionsArray());
            }else if("site_platform".equals(metatype)){
                return ok(MetadataAPI.sitePlatformsArray());
            }else if("appstores".equals(metatype)){
                return ok(MetadataAPI.appStoresArray());
            }else if("hygiene".equals(metatype)){
                return ok(MetadataAPI.hygieneList());
            }else if("creativeattr".equals(metatype)){
                return ok(MetadataAPI.creativeAttributes(-1));
            }else if("creativetypes".equals(metatype)){
                return ok(MetadataAPI.creativeTypesArray());
            }else if("slotoptions".equals(metatype)){
                return ok(MetadataAPI.creativeSlotsArray());
            }else if("marketplace".equals(metatype)){
                return ok(MetadataAPI.marketPlacesArray());
            }else if("trackingpartner".equals(metatype)){
                return ok(MetadataAPI.trackingPartnerArray());
            }else if("connection_type".equals(metatype)){
                return ok(MetadataAPI.reporting_connection_type_Array());
            }else if("geo_targeting_type".equals(metatype)){
                return ok(MetadataAPI.geographicTargetingOptionsArray());
            }else if("os".equals(metatype)){
                return ok(TPMetadataAPI.osList());
            }else if("supply_source".equals(metatype)){
                return ok(TPMetadataAPI.supplySourceOptionsArray());
            }else if("supply_source_type".equals(metatype)){
                return ok(TPMetadataAPI.supplySourceTypeOptionsArray());
            }else if("advertisers".equals(metatype)){
                return ok(MetadataAPI.advertiserforfiltering());
            }else if("directpublishers".equals(metatype)){
                return ok(MetadataAPI.activeDirectPublisherArray());
            }else if("exchanges".equals(metatype)){
                return ok(MetadataAPI.activeExchangeArray());
            }else if("passback_type".equals(metatype)){
                return ok(MetadataAPI.passback_typeArray());
            }else if("passback_content_type".equals(metatype)){
                return ok(MetadataAPI.passback_content_typeArray());
            }else if("advertisersbyid".equals(metatype)){
                return ok(MetadataAPI.activeAdvIdsArray());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

        }catch(Exception e){
            Logger.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return badRequest(msg.toJson().toString());
        }
    }
    public static Result metasecondlevel(String metatype, String ids){
        try{
            if("carrierbycountries".equals(metatype)){
                return ok(TPMetadataAPI.carrierList(ids));
            }else if("campaignbyadvertisers".equals(metatype)){
                return ok(MetadataAPI.campaignsByAccountIds(ids));
            }else if("creativeattrbycreativetype".equals(metatype)){
                return ok(MetadataAPI.creativeAttributes(Integer.parseInt(ids)));
            }else if("brandbyos".equals(metatype)){
                return ok(TPMetadataAPI.brandList(ids));
            }else if("modelbybrand".equals(metatype)){
                return ok(TPMetadataAPI.modelList(ids));
            }else if("sitesbydirectpublishers".equals(metatype)){
                return ok(MetadataAPI.targeting_sitesByPublishers(ids));
            }else if("extsitesbyexchanges".equals(metatype)){
                return ok(MetadataAPI.targeting_ext_siteByPublishers(ids));
            }else if("campaignbyadvertiserid".equals(metatype)){
                return ok(MetadataAPI.campaignsByAdvIds(ids));
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

        }catch(Exception e){
            Logger.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return badRequest(msg.toJson().toString());
        }
    }
    public static Result metasecondlevelpost(String metatype){
        JsonNode jsonNode= request().body().asJson();
        try{
            MetaInput entity = MetaInput.getObject(jsonNode.toString());
            if(entity != null){
                String ids  = entity.getQuery_id_list();
                if("carrierbycountries".equals(metatype)){
                    return ok(TPMetadataAPI.carrierList(ids));
                }else if("campaignbyadvertisers".equals(metatype)){
                    return ok(MetadataAPI.campaignsByAccountIds(ids));
                }else if("creativeattrbycreativetype".equals(metatype)){
                    return ok(MetadataAPI.creativeAttributes(Integer.parseInt(ids)));
                }else if("brandbyos".equals(metatype)){
                    return ok(TPMetadataAPI.brandList(ids));
                }else if("modelbybrand".equals(metatype)){
                    return ok(TPMetadataAPI.modelList(ids));
                }else if("sitesbydirectpublishers".equals(metatype)){
                    return ok(MetadataAPI.targeting_sitesByPublishers(ids));
                }else if("extsitesbyexchanges".equals(metatype)){
                    return ok(MetadataAPI.targeting_ext_siteByPublishers(ids));
                }else if("campaignbyadvertiserid".equals(metatype)){
                    return ok(MetadataAPI.campaignsByAdvIds(ids));
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

        }catch(Exception e){
            Logger.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return badRequest(msg.toJson().toString());
        }
    }
    public static Result savedquery(String actionType){
        JsonNode jsonNode= request().body().asJson();
        Connection con = null;
        try{
            con = DB.getConnection();
            if("create".equals(actionType)){
                SavedQueryEntity entity = SavedQueryEntity.getObject(jsonNode.toString());
                Message msg  = ApiDef.insert_saved_query(con, entity);
                return ok(msg.toJson().toString());
            }else if("update".equals(actionType)){
                SavedQueryEntity entity = SavedQueryEntity.getObject(jsonNode.toString());
                Message msg  = ApiDef.update_saved_query(con, entity);
                return ok(msg.toJson().toString());
            }else if("getbyids".equals(actionType)){
                SavedQueryListEntity entity = SavedQueryListEntity.getObject(jsonNode.toString());
                entity.setSaveQueryEnum(SavedQueryEnum.list_saved_query_entity_by_entity_id);
                SavedQueryList msg  = ApiDef.various_get_saved_query(con, entity);
                return ok(msg.toJson().toString());
            }else if("getbyaccountguids".equals(actionType)){
                SavedQueryListEntity entity = SavedQueryListEntity.getObject(jsonNode.toString());
                entity.setSaveQueryEnum(SavedQueryEnum.list_saved_query_entity_by_account_guids);
                SavedQueryList msg  = ApiDef.various_get_saved_query(con, entity);
                return ok(msg.toJson().toString());
            }else if("deletesavedquery".equals(actionType)){
                SavedQueryListEntity entity = SavedQueryListEntity.getObject(jsonNode.toString());
                entity.setSaveQueryEnum(SavedQueryEnum.delete_saved_query);
                Message msg  = ApiDef.change_status_saved_query(con, entity);
                return ok(msg.toJson().toString());
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACTION_TYPE_ABSENT.getId());
            msg.setMsg(ErrorEnum.ACTION_TYPE_ABSENT.getName());
            return ok(msg.toJson().toString());

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

}
