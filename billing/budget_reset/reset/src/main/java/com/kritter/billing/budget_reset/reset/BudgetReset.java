package com.kritter.billing.budget_reset.reset;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.billing.budget_reset.common.BillingResetType;
import com.kritter.billing.budget_reset.common.IReset;
import com.kritter.billing.budget_reset.update_all.DBBudgetReset;
import com.kritter.utils.dbconnector.DBConnector;

public class BudgetReset {
    private static final Logger LOG = LoggerFactory.getLogger(BudgetReset.class);
    private int reset(Connection con, BillingResetType billingResetType){
        IReset ireset = null;
        switch(billingResetType){
        case UPDATEALL:
            ireset = new DBBudgetReset();
            break;
        default:
            break;
        }
        if(ireset != null){
            int retcode = ireset.reset(con);
            int retcode1 = ireset.resetDailyPayout(con);
            return retcode;
        }
        return 0;
    }
    
    public int reset(String dbtype,String dbhost, String dbport, String dbname, String dbuser, String dbpwd, BillingResetType billingResetType){
        Connection con = null;
        try {
            con  = DBConnector.getConnection( dbtype, dbhost,  dbport,  dbname,  dbuser,  dbpwd);
            return reset(con, billingResetType);
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            return -1;
        } finally {
            if(con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }
    public static void main(String args[]) throws Exception{
        if(args.length != 6){
            throw new Exception("Incorrect Usage\n" +
            		"Correct Usage: com.kritter.billing.budget_reset.reset.BudgetReset dbtype dbhost dbport dbname dbuser dbpwd ");
        }
        BudgetReset budgetReset = new BudgetReset();
        int returnCode = budgetReset.reset(args[0], args[1], args[2], args[3], args[4], args[5], BillingResetType.UPDATEALL);
        if(returnCode == -1){
            throw new Exception("Exception in Budget Reset");
        }
    }
}
