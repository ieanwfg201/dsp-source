package com.kritter.kritterui.api.campaign_budget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.kritter.kritterui.api.db_query_def.Campaign_Budget;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.campaign_budget.CampaignBudgetList;
import com.kritter.api.entity.campaign_budget.CampaignBudgetListEntity;
import com.kritter.api.entity.campaign_budget.Campaign_budget;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;

public class CampaignBudgetCrud {
    private static final Logger LOG = LoggerFactory.getLogger(CampaignBudgetCrud.class);
    public static void populate(Campaign_budget campaign_budget, ResultSet rset) throws SQLException{
        if(campaign_budget != null && rset != null){
            campaign_budget.setAdv_daily_budget(rset.getDouble("adv_daily_budget"));
            campaign_budget.setAdv_daily_burn(rset.getDouble("adv_daily_burn"));
            campaign_budget.setAdv_total_budget(rset.getDouble("adv_total_budget"));
            campaign_budget.setAdv_total_burn(rset.getDouble("adv_total_burn"));
            campaign_budget.setCampaign_guid(rset.getString("campaign_guid"));
            campaign_budget.setCampaign_id(rset.getInt("campaign_id"));
            campaign_budget.setInternal_daily_budget(rset.getDouble("internal_daily_budget"));
            campaign_budget.setInternal_daily_burn(rset.getDouble("internal_daily_burn"));
            campaign_budget.setInternal_total_budget(rset.getDouble("internal_total_budget"));
            campaign_budget.setInternal_total_burn(rset.getDouble("internal_total_burn"));
            campaign_budget.setModified_by(rset.getInt("modified_by"));
            
        }
    }
    
    public static JsonNode insert_campaign_budget(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Campaign_budget campaign_budget = objectMapper.treeToValue(jsonNode, Campaign_budget.class);
            return insert_campaign_budget(con, campaign_budget, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message insert_campaign_budget(Connection con, Campaign_budget campaign_budget, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(campaign_budget == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign_Budget.insert_campaign_budget);
            pstmt.setInt(1, campaign_budget.getCampaign_id());
            pstmt.setString(2, campaign_budget.getCampaign_guid());
            pstmt.setDouble(3, campaign_budget.getInternal_total_budget());
            pstmt.setDouble(4, campaign_budget.getAdv_total_budget());
            pstmt.setDouble(5, campaign_budget.getInternal_total_burn());
            pstmt.setDouble(6, campaign_budget.getAdv_total_burn());
            pstmt.setDouble(7, campaign_budget.getInternal_daily_budget());
            pstmt.setDouble(8, campaign_budget.getAdv_daily_budget());
            pstmt.setDouble(9, campaign_budget.getInternal_daily_burn());
            pstmt.setDouble(10, campaign_budget.getAdv_daily_burn());
            pstmt.setInt(11, campaign_budget.getModified_by());
            pstmt.setTimestamp(12, new Timestamp((new Date()).getTime()));
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NOT_INSERTED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
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
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        } 
    }
    
    public static JsonNode update_campaign_budget(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Campaign_budget campaign_budget = objectMapper.treeToValue(jsonNode, Campaign_budget.class);
            return update_campaign_budget(con, campaign_budget, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    } 
    public static Message update_campaign_budget(Connection con, Campaign_budget campaign_budget, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(campaign_budget == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign_Budget.update_campaign_budget);
            pstmt.setDouble(1, campaign_budget.getInternal_total_budget());
            pstmt.setDouble(2, campaign_budget.getAdv_total_budget());
            pstmt.setDouble(3, campaign_budget.getInternal_total_burn());
            pstmt.setDouble(4, campaign_budget.getAdv_total_burn());
            pstmt.setDouble(5, campaign_budget.getInternal_daily_budget());
            pstmt.setDouble(6, campaign_budget.getAdv_daily_budget());
            pstmt.setDouble(7, campaign_budget.getInternal_daily_burn());
            pstmt.setDouble(8, campaign_budget.getAdv_daily_burn());
            pstmt.setInt(9, campaign_budget.getModified_by());
            pstmt.setTimestamp(10, new Timestamp((new Date()).getTime()));
            pstmt.setInt(11, campaign_budget.getCampaign_id());
                       
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NOT_UPDATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
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
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction){
                try {
                    con.setAutoCommit(autoCommitFlag);
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        } 
    }
    
    public static JsonNode get_campaign_budget(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            CampaignBudgetList campaignBudgetlist = new CampaignBudgetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NULL.getName());
            campaignBudgetlist.setMsg(msg);
            return campaignBudgetlist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CampaignBudgetListEntity campaignBudgetlistEntity = objectMapper.treeToValue(jsonNode, CampaignBudgetListEntity.class);
            return get_campaign_budget(con, campaignBudgetlistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CampaignBudgetList campaignBudgetlist = new CampaignBudgetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            campaignBudgetlist.setMsg(msg);
            return campaignBudgetlist.toJson();
        }
    }
    public static CampaignBudgetList get_campaign_budget(Connection con, CampaignBudgetListEntity campaignBudgetlistEntity){
        if(con == null){
            CampaignBudgetList campaignBudgetlist = new CampaignBudgetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            campaignBudgetlist.setMsg(msg);
            return campaignBudgetlist;
        }
        if(campaignBudgetlistEntity == null){
            CampaignBudgetList campaignBudgetlist = new CampaignBudgetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGNBUDGETLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGNBUDGETLIST_ENTITY_NULL.getName());
            campaignBudgetlist.setMsg(msg);
            return campaignBudgetlist;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Campaign_Budget.get_campaign_budget);
            pstmt.setInt(1, campaignBudgetlistEntity.getCampaign_id());
            ResultSet rset = pstmt.executeQuery();
            CampaignBudgetList campaignBudgetlist = new CampaignBudgetList();
            List<Campaign_budget> campaign_budgets = new LinkedList<Campaign_budget>();
            while(rset.next()){
                Campaign_budget campaignBudget = new Campaign_budget();
                populate(campaignBudget, rset);
                campaign_budgets.add(campaignBudget);
            }
            campaignBudgetlist.setCampaig_budget_list(campaign_budgets);
            Message msg = new Message();
            if(campaign_budgets.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.CAMPAIGN_BUDGET_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_BUDGET_NOT_FOUND.getName());
            }
            campaignBudgetlist.setMsg(msg);
            return campaignBudgetlist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CampaignBudgetList campaignBudgetlist = new CampaignBudgetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            campaignBudgetlist.setMsg(msg);
            return campaignBudgetlist;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }
    public static Campaign_budget get_campaign_budget(String guid,Connection con){
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(Campaign_Budget.get_campaign_budget_by_guid);
            pstmt.setString(1,guid);
            ResultSet rset = pstmt.executeQuery();

            while(rset.next()){
                Campaign_budget campaignBudget = new Campaign_budget();
                populate(campaignBudget, rset);
                return campaignBudget;
            }
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
        return null;
    }
}
