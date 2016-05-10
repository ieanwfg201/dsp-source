package com.kritter.utils.databasemanager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class DatabaseManager
{
    private DataSource dataSource = null;

    public DatabaseManager(String dataSourceLookup) throws NamingException
    {
        Context ctx = (Context) new InitialContext().lookup("java:comp/env");
        if(null == ctx)
        {
            throw new RuntimeException("No Context available");
        }

        dataSource = (DataSource) ctx.lookup(dataSourceLookup);
        if(null == dataSource)
            throw new RuntimeException("No datasource, the datasource lookup did not return anything, is null");
    }

    public Connection getConnectionFromPool() throws SQLException {
        if (null == dataSource)
            throw new SQLException("Data Source not initialized properly and is null.");
        return dataSource.getConnection();
    }

    /**
     *
     * @param connection
     * @param query
     * @return
     * @throws SQLException
     * @deprecated USe DBExecutionUtils.executeQuery instead
     */
    @Deprecated
    public static ResultSet executeQuery(Connection connection, String query) throws SQLException
    {
        return DBExecutionUtils.executeQuery(connection, query);
    }

    @Deprecated
    public static void closeConnection(Connection conn)
    {
        DBExecutionUtils.closeConnection(conn);
    }

    @Deprecated
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs)
    {
        DBExecutionUtils.closeResources(conn, stmt, rs);
    }

    @Deprecated
    public static void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs)
    {
        DBExecutionUtils.closeResources(conn, pstmt, rs);
    }

    @Deprecated
    public static void closeResources(PreparedStatement pstmt, ResultSet rs)
    {
        DBExecutionUtils.closeResources(pstmt, rs);
    }
}