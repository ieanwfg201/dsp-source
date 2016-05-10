package com.kritter.kumbaya.libraries.report_delete.daily;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.kritter.kumbaya.libraries.report_delete.common.IReportDelete;
import com.kritter.utils.dbconnector.DBConnector;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

public class DailyDelete implements IReportDelete {
    private static Logger m_logger = Logger.getLogger(DailyDelete.class);
    
    @Override
    public void delete(String dbhost, String dbuser, String dbpwd,
            String dbname, String dbport, String dbtype, String tablename, int days) throws Exception{
        Date date = new Date();
        date = DateUtils.addHours(date, -days*24);
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String impression_time = dfm.format(date);
        delete(dbhost, dbuser, dbpwd, dbname, dbport, dbtype, tablename, impression_time);
    }

    @Override
    public void delete(String dbhost, String dbuser, String dbpwd,
            String dbname, String dbport, String dbtype, String tablename,
            String impression_time) throws Exception{
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = DBConnector.getConnection(dbtype, dbhost,  dbport, dbname,  dbuser,  dbpwd);
            boolean autoCommit  = conn.getAutoCommit();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            if((null != impression_time) && (!"".equals(impression_time))){
                System.out.println("delete from "+tablename+" where impression_time <= '"+impression_time+"'");
                stmt.execute("delete from "+tablename+" where impression_time <= '"+impression_time+"'");
            }
            conn.commit();
            conn.setAutoCommit(autoCommit);
        }catch(Exception e){
            e.printStackTrace();
            m_logger.error(e.getMessage(), e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                m_logger.error(e.getMessage(), e);
            }
            throw e;
        }finally{
            if(stmt != null){
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    m_logger.error(e.getMessage(), e);
                    throw e;
                }
            }
            if(conn != null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    m_logger.error(e.getMessage(), e);
                    throw e;
                }
            }
        }            
    }
    public static void main(String args[]) throws Exception{
        /**
         * String dbhost, String dbuser, String dbpwd,
            String dbname, String dbport, String dbtype, String tablename, int days
         */
        if(args.length == 8){
            DailyDelete hd  = new DailyDelete();
            hd.delete(args[0], args[1], args[2], args[3], args[4], args[5], args[6], Integer.parseInt(args[7]));
        }else{
            //DailyDelete hd  = new DailyDelete();
            //hd.delete("localhost", "root", "password", "kritter", "3306", "MYSQL", "first_level", Integer.parseInt("720"));
            System.out.println("############## ERROR in USAGE #############");
            System.out.println(args.length);
            System.exit(1);
            
        }
    }
}
