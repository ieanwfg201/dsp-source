package com.kritter.billing.budget_reset.update_all;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.billing.budget_reset.common.IReset;

public class DBBudgetReset implements IReset{
    
    private static final Logger LOG = LoggerFactory.getLogger(DBBudgetReset.class);
    private static final String updateString = "update campaign_budget set internal_daily_burn=0,adv_daily_burn=0,last_modified=?";
    private static final String updatePayoutString = "update payout set daily_payout=0,last_modified=?";
    
    public int reset(Connection con){
        if(con == null){
            return 0;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            autoCommitFlag = con.getAutoCommit();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(updateString);
            pstmt.setTimestamp(1, new Timestamp((new Date()).getTime()));
            int returnCode = pstmt.executeUpdate();
            con.commit();
            return returnCode;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return -1;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            try {
                con.setAutoCommit(autoCommitFlag);
            } catch (SQLException e1) {
                LOG.error(e1.getMessage(),e1);
            }
        }
    }
    public int resetDailyPayout(Connection con){
        if(con == null){
            return 0;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            autoCommitFlag = con.getAutoCommit();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement(updatePayoutString);
            pstmt.setTimestamp(1, new Timestamp((new Date()).getTime()));
            int returnCode = pstmt.executeUpdate();
            con.commit();
            return returnCode;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return -1;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            try {
                con.setAutoCommit(autoCommitFlag);
            } catch (SQLException e1) {
                LOG.error(e1.getMessage(),e1);
            }
        }
    }

}
