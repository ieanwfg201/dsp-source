package com.kritter.kritterui.api.account_budget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.account_budget.Account_Budget_Msg;
import com.kritter.api.entity.account_budget.Account_budget;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;

public class Account_Budget_Crud {
    private static final Logger LOG = LoggerFactory.getLogger(Account_Budget_Crud.class);
    
    public static void populate_account_budget(Account_budget ab, ResultSet rset) throws SQLException{
        if(ab != null && rset != null){
            ab.setAdv_balance(rset.getDouble("adv_balance"));
            ab.setAdv_burn(rset.getDouble("adv_burn"));
            ab.setAccount_guid(rset.getString("account_guid"));
            ab.setInternal_balance(rset.getDouble("internal_balance"));
            ab.setInternal_burn(rset.getDouble("internal_burn"));
            ab.setModified_by(rset.getInt("modified_by"));
     }
    }
    
    public static JsonNode insert_account_budget(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account_budget ab = objectMapper.treeToValue(jsonNode, Account_budget.class);
            return insert_account_budget(con, ab, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message insert_account_budget(Connection con, Account_budget ab, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ab == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account_Budget.insert_account_budget);
            pstmt.setString(1, ab.getAccount_guid());
            pstmt.setDouble(2, ab.getInternal_balance());
            pstmt.setDouble(3, ab.getInternal_burn());
            pstmt.setDouble(4, ab.getAdv_balance());
            pstmt.setDouble(5, ab.getAdv_burn());
            pstmt.setInt(6, ab.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(7, ts);
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NOT_INSERTED.getName());
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
    
    public static JsonNode get_Account_Budget(Connection con, JsonNode jsonNode){
        if(con == null){
            Account_Budget_Msg amp = new Account_Budget_Msg();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
        if(jsonNode == null){
            Account_Budget_Msg amp = new Account_Budget_Msg();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NULL.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account_budget ab = objectMapper.treeToValue(jsonNode, Account_budget.class);
            return get_Account_Budget(con, ab).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Account_Budget_Msg amp = new Account_Budget_Msg();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp.toJson();
        }
    }
    public static Account_Budget_Msg get_Account_Budget(Connection con,Account_budget ab){
        if(con == null){
            Account_Budget_Msg amp = new Account_Budget_Msg();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp;
        }
        if(ab == null){
            Account_Budget_Msg amp = new Account_Budget_Msg();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NULL.getName());
            amp.setMsg(msg);
            return amp;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account_Budget.get_account_budget);
            pstmt.setString(1,ab.getAccount_guid());
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                Account_Budget_Msg amp = new Account_Budget_Msg();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populate_account_budget(ab, rset);
                amp.setAccount_budget(ab);
                amp.setMsg(msg);
                return amp;
            }
            Account_Budget_Msg amp = new Account_Budget_Msg();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NOT_FOUND.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NOT_FOUND.getName());
            amp.setMsg(msg);
            return amp;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Account_Budget_Msg amp = new Account_Budget_Msg();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            amp.setMsg(msg);
            return amp;
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

    public static Account_budget get_Account_Budget(String guid,Connection con){
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account_Budget.get_account_budget);
            pstmt.setString(1,guid);
            ResultSet rset = pstmt.executeQuery();
            Account_budget ab = null;
            if(rset.next()){
                ab = new Account_budget();
                populate_account_budget(ab, rset);
                return ab;
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
    
    public static JsonNode update_account_budget(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Account_budget ab = objectMapper.treeToValue(jsonNode, Account_budget.class);
            return update_account_budget(con, ab, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_account_budget(Connection con, Account_budget ab, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ab == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NULL.getId());
            msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Account_Budget.update_account_budget);
            pstmt.setDouble(1, ab.getInternal_balance());
            pstmt.setDouble(2, ab.getInternal_burn());
            pstmt.setDouble(3, ab.getAdv_balance());
            pstmt.setDouble(4, ab.getAdv_burn());
            pstmt.setInt(5, ab.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(6, ts);
            pstmt.setString(7, ab.getAccount_guid());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ACCOUNT_BUDGET_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.ACCOUNT_BUDGET_NOT_UPDATED.getName());
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

}
