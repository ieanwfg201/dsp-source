package com.kritter.kritterui.api.mixed;

import java.sql.Connection;
import java.sql.SQLException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account_budget.Account_Budget_Msg;
import com.kritter.api.entity.account_budget.Account_budget;
import com.kritter.api.entity.ad.AdListEntity;
import com.kritter.api.entity.creative_banner.CreativeBannerList;
import com.kritter.api.entity.creative_banner.CreativeBannerListEntity;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.native_icon.NativeIconList;
import com.kritter.api.entity.native_icon.NativeIconListEntity;
import com.kritter.api.entity.native_screenshot.NativeScreenshotList;
import com.kritter.api.entity.native_screenshot.NativeScreenshotListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.constants.Account_Type;
import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.CreativeBannerAPIEnum;
import com.kritter.constants.IOStatus;
import com.kritter.constants.NativeIconAPIEnum;
import com.kritter.constants.NativeScreenshotAPIEnum;
import com.kritter.constants.Payout;
import com.kritter.constants.SiteAPIEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.account.AccountCrud;
import com.kritter.kritterui.api.account_budget.Account_Budget_Crud;
import com.kritter.kritterui.api.ad.AdCrud;
import com.kritter.kritterui.api.creative_banner.CreativeBannerCrud;
import com.kritter.kritterui.api.creative_container.CreativeContainerCrud;
import com.kritter.kritterui.api.io.IOCrud;
import com.kritter.kritterui.api.native_icon.NativeIconCrud;
import com.kritter.kritterui.api.native_screenshot.NativeScreenshotCrud;
import com.kritter.kritterui.api.site.SiteCrud;

public class MixedCrud {

    private static final Logger LOG = LoggerFactory.getLogger(MixedCrud.class);

    public static JsonNode approve_io(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Insertion_Order io = objectMapper.treeToValue(jsonNode, Insertion_Order.class);
            return approve_io(con, io, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message approve_io(Connection con, Insertion_Order io, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(io == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg;
        }
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            int modified_by = io.getModified_by();
            Message checkMsg = IOCrud.check_io(con, io);
            if(checkMsg.getError_code() != ErrorEnum.IO_ACCOUNT_EXIST.getId()){
                checkMsg.setError_code(ErrorEnum.IO_POPULATION_FAIL.getId());
                checkMsg.setMsg(ErrorEnum.IO_POPULATION_FAIL.getName());
                return checkMsg;
            }
            IOStatus dbIOSttaus  = io.getStatus();
            if(!dbIOSttaus.equals(IOStatus.NEW)){
                Message statusMsg =  new Message();
                statusMsg.setError_code(ErrorEnum.IO_STATUS_NOT_NEW.getId());
                statusMsg.setMsg(ErrorEnum.IO_STATUS_NOT_NEW.getName());
                return statusMsg;
            }
            io.setStatus(IOStatus.Approved);
            io.setModified_by(modified_by);
            Message msg = IOCrud.update_io(con, io, false);
            if(msg.getError_code() == 0){
                Account_budget ab = new Account_budget();
                ab.setAccount_guid(io.getAccount_guid());
                Account_Budget_Msg accountBudgetMsg = Account_Budget_Crud.get_Account_Budget(con, ab);
                Message abm = accountBudgetMsg.getMsg();
                if(abm.getError_code() == 0){
                    ab.setAdv_balance(ab.getAdv_balance()+io.getTotal_value());
                    ab.setInternal_balance(ab.getInternal_balance()+io.getTotal_value());
                    ab.setModified_by(io.getModified_by());
                    Message updatemsg = Account_Budget_Crud.update_account_budget(con, ab, false);
                    if(createTransaction){
                        con.commit();
                    }
                    return updatemsg;
                }else{
                    ab.setAdv_balance(io.getTotal_value());
                    ab.setInternal_balance(io.getTotal_value());
                    ab.setModified_by(io.getModified_by());
                    Message insertmsg = Account_Budget_Crud.insert_account_budget(con, ab, false);
                    if(createTransaction){
                        con.commit();
                    }
                    return insertmsg;
                }
            }
            if(createTransaction){
                con.commit();
            }
            return msg;
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
            if(createTransaction){
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }finally{
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        }
    }
    
    public static JsonNode get_creative_banner_from_container(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Creative_container cc = objectMapper.treeToValue(jsonNode, Creative_container.class);
            return get_creative_banner_from_container(con, cc).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static CreativeBannerList get_creative_banner_from_container(Connection con, Creative_container cc){
        if(con == null){
            CreativeBannerList cblist = new CreativeBannerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            cblist.setMsg(msg);
            return cblist;
        }
        if(cc == null){
            CreativeBannerList cblist = new CreativeBannerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            cblist.setMsg(msg);
            return cblist;
        }
        CreativeBannerListEntity cblistEntity = new CreativeBannerListEntity();
        cblistEntity.setGuid_list(cc.getResource_uri_ids().replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\[", "").replaceAll("\\]", ""));
        cblistEntity.setCbenum(CreativeBannerAPIEnum.get_creative_banner_by_ids);
        return CreativeBannerCrud.various_get_creative_banner(con, cblistEntity);
    }
    public static Message update_creative_container(Connection con, Creative_container cc, boolean createTransaction){
        Message msg = CreativeContainerCrud.update_creative_container(con, cc, createTransaction);
        if(msg.getError_code() != ErrorEnum.NO_ERROR.getId()){
            return msg;
        }
        AdListEntity adlistEntity = new AdListEntity();
        adlistEntity.setAdenum(AdAPIEnum.approve_ad_again_on_creative_update);
        adlistEntity.setId(cc.getId());
        msg = AdCrud.change_status_ad(con, adlistEntity, createTransaction);
        return msg;
    }
    public static String payout_percentage_to_billing_rule_json(String payout_percentage){
        if(payout_percentage != null){
            String tmp_payout_percentage = payout_percentage.trim();
            if(!tmp_payout_percentage.equals("")){
                return "{\"payout\":"+payout_percentage+"}";
            }
        }
        return "{\"payout\":"+Payout.default_payout_percent+"}";
    }
    public static Message updateAccount(Connection con,Account account, boolean createTransaction){
        Message msg = AccountCrud.updateAccount(con, account, createTransaction);
        if(account != null && Account_Type.directpublisher == account.getType_id()){
            SiteListEntity siteListEntity = new SiteListEntity();
            siteListEntity.setPub_id(account.getId());
            siteListEntity.setBilling_rules_json( payout_percentage_to_billing_rule_json(account.getBilling_rules_json()));
            siteListEntity.setSiteApiEnum(SiteAPIEnum.change_site_payout);
            SiteCrud.change_site_payout(con, siteListEntity, createTransaction);
        }
        return msg;
    }
    
    public static JsonNode get_native_icon_from_container(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Creative_container cc = objectMapper.treeToValue(jsonNode, Creative_container.class);
            return get_native_icon_from_container(con, cc).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static NativeIconList get_native_icon_from_container(Connection con, Creative_container cc){
        if(con == null){
            NativeIconList nativeList = new NativeIconList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            nativeList.setMsg(msg);
            return nativeList;
        }
        if(cc == null){
            NativeIconList nativeList = new NativeIconList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            nativeList.setMsg(msg);
            return nativeList;
        }
        NativeIconListEntity nativelistEntity = new NativeIconListEntity();
        nativelistEntity.setId_list(cc.getNative_icons().replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\[", "").replaceAll("\\]", ""));
        nativelistEntity.setNativeenum(NativeIconAPIEnum.get_native_icon_by_ids);
        return NativeIconCrud.various_get_native_icon(con, nativelistEntity);
    }

    public static JsonNode get_native_screenshot_from_container(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Creative_container cc = objectMapper.treeToValue(jsonNode, Creative_container.class);
            return get_native_screenshot_from_container(con, cc).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static NativeScreenshotList get_native_screenshot_from_container(Connection con, Creative_container cc){
        if(con == null){
            NativeScreenshotList nativeList = new NativeScreenshotList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            nativeList.setMsg(msg);
            return nativeList;
        }
        if(cc == null){
            NativeScreenshotList nativeList = new NativeScreenshotList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            nativeList.setMsg(msg);
            return nativeList;
        }
        NativeScreenshotListEntity nativelistEntity = new NativeScreenshotListEntity();
        nativelistEntity.setId_list(cc.getNative_icons().replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\[", "").replaceAll("\\]", ""));
        nativelistEntity.setNativeenum(NativeScreenshotAPIEnum.get_native_screenshot_by_ids);
        return NativeScreenshotCrud.various_get_native_screenshot(con, nativelistEntity);
    }

}
