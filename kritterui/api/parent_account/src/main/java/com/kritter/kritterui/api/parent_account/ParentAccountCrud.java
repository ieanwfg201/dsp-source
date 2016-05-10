package com.kritter.kritterui.api.parent_account;

import com.kritter.api.entity.parent_account.ParentAccount;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.ParentAccountType;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * This class has methods to insert/update/view the parent account entity.
 */
public class ParentAccountCrud
{
    private static final Logger LOG = LoggerFactory.getLogger(ParentAccountCrud.class);

    public static JsonNode insertParentAccount(Connection con, JsonNode jsonNode)
    {
        if(jsonNode == null)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            ParentAccount parentAccount = objectMapper.treeToValue(jsonNode, ParentAccount.class);
            return insertParentAccount(con, parentAccount, true).toJson();
        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insertParentAccount(Connection con, ParentAccount parentAccount, boolean createTransaction)
    {
        return insertParentAccount(con, parentAccount, createTransaction, false);
    }

    public static Message insertParentAccount(
                                              Connection con,
                                              ParentAccount parentAccount,
                                              boolean createTransaction,
                                              boolean userSpecifiedGuid
                                             )
    {
        if(con == null)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(parentAccount == null)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getName());
            return msg;
        }

        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;

        try
        {
            if(createTransaction)
            {
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }

            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.ParentAccount.QUERY_INSERT_PARENT_ACCOUNT);
            if(userSpecifiedGuid)
                pstmt.setString(1, parentAccount.getGuid());
            else
                pstmt.setString(1, SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());

            pstmt.setInt(2, StatusIdEnum.Pending.getCode());
            pstmt.setInt(3, parentAccount.getType_id().getId());
            pstmt.setString(4,parentAccount.getName());
            pstmt.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
            pstmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

            int returnCode = pstmt.executeUpdate();
            if(createTransaction)
            {
                con.commit();
            }

            if(returnCode == 0)
            {
                Message msg = new Message();
                msg.setError_code(ErrorEnum.PARENT_ACCOUNT_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.PARENT_ACCOUNT_NOT_INSERTED.getName());
                return msg;
            }

            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(),e);

            if(createTransaction)
            {
                try
                {
                    con.rollback();
                }
                catch (SQLException e1)
                {
                    LOG.error(e1.getMessage(),e1);
                }
            }

            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction)
            {
                try
                {
                    con.setAutoCommit(autoCommitFlag);
                }
                catch (SQLException e1)
                {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        }
    }

    public static JsonNode updateParentAccount(Connection con, JsonNode jsonNode)
    {
        if(jsonNode == null)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            ParentAccount parentAccount = objectMapper.treeToValue(jsonNode, ParentAccount.class);
            return updateParentAccount(con, parentAccount, true).toJson();
        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message updateParentAccount(Connection con, ParentAccount parentAccount, boolean createTransaction)
    {
        if(con == null)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(parentAccount == null)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.PARENT_ACCOUNT_OBJECT_NULL.getName());
            return msg;
        }

        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;

        try
        {
            if(createTransaction)
            {
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }

            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.ParentAccount.QUERY_UPDATE_PARENT_ACCOUNT);

            pstmt.setInt(1,parentAccount.getStatus().getCode());
            pstmt.setInt(2,parentAccount.getType_id().getId());
            pstmt.setString(3,parentAccount.getName());
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(5,parentAccount.getGuid());

            int returnCode = pstmt.executeUpdate();

            if(createTransaction)
            {
                con.commit();
            }

            if(returnCode == 0)
            {
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SITE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SITE_NOT_UPDATED.getName());
                return msg;
            }

            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }
        catch(Exception e)
        {
            LOG.error(e.getMessage(),e);
            if(createTransaction)
            {
                try
                {
                    con.rollback();
                }
                catch (SQLException e1)
                {
                    LOG.error(e1.getMessage(),e1);
                }
            }

            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            return msg;
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(createTransaction)
            {
                try
                {
                    con.setAutoCommit(autoCommitFlag);
                }
                catch (SQLException e1)
                {
                    LOG.error(e1.getMessage(),e1);
                }
            }
        }
    }

    private static void populateParentAccount(ParentAccount parentAccount,ResultSet resultSet) throws SQLException
    {
        parentAccount.setId(resultSet.getInt("id"));
        parentAccount.setGuid(resultSet.getString("guid"));
        int status = resultSet.getInt("status");
        int typeId = resultSet.getInt("type_id");
        StatusIdEnum statusIdEnum = StatusIdEnum.getEnum(status);
        parentAccount.setStatus(statusIdEnum);
        ParentAccountType parentAccountType = ParentAccountType.getEnum(typeId);
        parentAccount.setType_id(parentAccountType);
        parentAccount.setName(resultSet.getString("name"));
        parentAccount.setCreatedOn(resultSet.getTimestamp("created_on"));
        parentAccount.setLastModified(resultSet.getTimestamp("last_modified"));
    }

    public static ParentAccount getParentAccount(Connection connection, String parentAccountGuid)
    {
        PreparedStatement pstmt = null;

        try
        {
            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.ParentAccount.QUERY_GET_PARENT_ACCOUNT_BY_GUID);
            pstmt.setString(1, parentAccountGuid);

            ResultSet resultSet = pstmt.executeQuery();
            ParentAccount parentAccount = null;
            while (resultSet.next())
            {
                parentAccount = new ParentAccount();
                populateParentAccount(parentAccount,resultSet);
                return parentAccount;
            }

        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(),e);
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    LOG.error(e.getMessage(),e);
                }
            }
        }

        return null;
    }
}