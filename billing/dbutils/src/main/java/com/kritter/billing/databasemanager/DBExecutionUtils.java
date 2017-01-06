package com.kritter.billing.databasemanager;

import java.sql.*;
import java.util.Map;

public class DBExecutionUtils
{
    private static String PROCEDURE_CALL_PREFIX = "{call ";
    private static String PROCEDURE_CALL_SUFFIX = "()}";

    public static ResultSet executeQuery(Connection connection, String query) throws SQLException
    {
        PreparedStatement pstmt = connection.prepareStatement(query,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        return pstmt.executeQuery();
    }

    public static ResultSet executeQuery(Connection connection,
                                         String query,
                                         Map<Integer,Object> queryMapToSet) throws SQLException
    {
        PreparedStatement pstmt = connection.prepareStatement(query,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        //if there are parameters to set in query.
        if(null != queryMapToSet && queryMapToSet.size() > 0)
        {
            for(Map.Entry<Integer,Object> entry : queryMapToSet.entrySet())
            {
                pstmt.setObject(entry.getKey(),entry.getValue());
            }
        }

        return pstmt.executeQuery();
    }

    public static boolean executeNoArgsNoReturnProcedure(Connection conn, String procName) throws SQLException
    {
        CallableStatement statement = conn.prepareCall(PROCEDURE_CALL_PREFIX + procName + PROCEDURE_CALL_SUFFIX);
        return statement.execute();
    }

    public static void closeConnection(Connection conn)
    {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                conn = null;
            }
        }
    }

    public static void closeResources(Connection conn, Statement stmt, ResultSet rs)
    {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                conn = null;
            }
        }

        if (null != stmt) {
            try {
                stmt.close();
            } catch (SQLException e) {
                stmt = null;
            }
        }

        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                rs = null;
            }
        }
    }

    public static void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs)
    {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                conn = null;
            }
        }

        if (null != pstmt) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                pstmt = null;
            }
        }

        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                rs = null;
            }
        }
    }

    public static void closeResources(PreparedStatement pstmt, ResultSet rs)
    {
        if (null != pstmt) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                pstmt = null;
            }
        }

        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                rs = null;
            }
        }
    }
}
