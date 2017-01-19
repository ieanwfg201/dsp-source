package com.kritter.billing.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.log4j.Logger;
import com.kritter.constants.DBCONSTANTS;

public class DBConnector
{
    private static Logger m_logger = Logger.getLogger(DBConnector.class);
    public static Connection getConnection(Properties prop) throws Exception
    {
        Connection conn = null;
        try
        {
            //IMPORTANT : use useUnicode and characterEncoding always to support all characters.

            // POSTGRES
            if(DBCONSTANTS.m_postgres.equalsIgnoreCase((String)prop.get(DBCONSTANTS.m_dbtype)))
            {
                Class.forName(DBCONSTANTS.m_posgres_driver);
                conn = DriverManager.getConnection("jdbc:postgresql://"+prop.getProperty(DBCONSTANTS.m_dbhost)+":"
                        +prop.getProperty(DBCONSTANTS.m_dbport)+"/"+prop.getProperty(DBCONSTANTS.m_dbname)
                        + "?user=" + prop.getProperty(DBCONSTANTS.m_dbuser)
                        + "&password=" + prop.getProperty(DBCONSTANTS.m_dbpwd)
                        +"&useUnicode=true&characterEncoding=UTF-8");
            }
            else
            {
                // MYSQL
                if(DBCONSTANTS.m_mysql.equalsIgnoreCase((String)prop.get(DBCONSTANTS.m_dbtype)))
                {
                    Class.forName(DBCONSTANTS.m_msql_driver);
                    conn = DriverManager.getConnection("jdbc:mysql://"+prop.getProperty(DBCONSTANTS.m_dbhost)+":"
                            +prop.getProperty(DBCONSTANTS.m_dbport)+"/"+prop.getProperty(DBCONSTANTS.m_dbname)
                            + "?user=" + prop.getProperty(DBCONSTANTS.m_dbuser)
                            + "&password=" + prop.getProperty(DBCONSTANTS.m_dbpwd)
                            +"&useUnicode=true&characterEncoding=UTF-8");
                }
            }
            return conn;
        }
        catch(Exception e)
        {
            m_logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    @Deprecated
    public static Connection getConnection(String dbtype,String dbhost, String dbport, String dbname, String dbuser, String dbpwd) throws Exception{
        Connection conn = null;
        try{
            // POSTGRES
            if(DBCONSTANTS.m_postgres.equalsIgnoreCase(dbtype)){
                Class.forName(DBCONSTANTS.m_posgres_driver);
                conn = DriverManager.getConnection("jdbc:postgresql://"+dbhost+":"+dbport+"/"+dbname,dbuser, dbpwd);
            }
            else{
                // MYSQL
                if(DBCONSTANTS.m_mysql.equalsIgnoreCase(dbtype)){
                    Class.forName(DBCONSTANTS.m_msql_driver);
                    conn = DriverManager.getConnection("jdbc:mysql://"+dbhost+":"+dbport+"/"+dbname,dbuser, dbpwd);
                }
            }
            return conn;
        }
        catch(Exception e){
            m_logger.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        }
    }

    public static void releaseConnection(Connection conn) throws Exception{
        conn.close();
    }

}
