package com.kritter.kritterui.api.deal;

import com.kritter.api.entity.deal.PrivateMarketPlaceApiEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

/**
 * This class keeps methods and data required for ui creation of a deal.
 */
public class PrivateMarketPlaceDealCrud
{
    private static final Logger LOG = LoggerFactory.getLogger(PrivateMarketPlaceDealCrud.class);

    public static Message insertPrivateMarketPlaceDeal(PrivateMarketPlaceApiEntity privateMarketPlaceDeal, Connection con, boolean createTransaction)
    {
        if(con == null)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }

        if(null == privateMarketPlaceDeal)
        {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PMP_DEAL_NULL.getId());
            msg.setMsg(ErrorEnum.PMP_DEAL_NULL.getName());
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

            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.PrivateMarketPlaceDeal.INSERT_NEW_DEAL, PreparedStatement.RETURN_GENERATED_KEYS);

            LOG.error("No error , Data for deal is: {} ",privateMarketPlaceDeal.toString());

            pstmt.setString(1,privateMarketPlaceDeal.getDealId());
            pstmt.setString(2, privateMarketPlaceDeal.getDealName());
            pstmt.setString(3,privateMarketPlaceDeal.getAdIdList());
            pstmt.setString(4,privateMarketPlaceDeal.getSiteIdList());
            pstmt.setString(5,privateMarketPlaceDeal.getBlockedIABCategories());
            pstmt.setString(6,privateMarketPlaceDeal.getThirdPartyConnectionList());
            pstmt.setString(7,privateMarketPlaceDeal.getDspIdList());
            pstmt.setString(8,privateMarketPlaceDeal.getAdvertiserIdList());
            pstmt.setString(9,privateMarketPlaceDeal.getWhitelistedAdvertiserDomainsMap());
            pstmt.setShort(10,Short.valueOf(privateMarketPlaceDeal.getAuctionType()));
            pstmt.setInt(11,Integer.valueOf(privateMarketPlaceDeal.getRequestCap()));
            pstmt.setTimestamp(12,new Timestamp(privateMarketPlaceDeal.getStartDate()));
            pstmt.setTimestamp(13,new Timestamp(privateMarketPlaceDeal.getEndDate()));
            pstmt.setDouble(14,Double.valueOf(privateMarketPlaceDeal.getDealCPM()));
            pstmt.setTimestamp(15,new Timestamp(System.currentTimeMillis()));

            int returnCode = pstmt.executeUpdate();

            if(createTransaction)
            {
                con.commit();
            }

            if(returnCode == 0)
            {
                Message msg = new Message();
                msg.setError_code(ErrorEnum.PMP_DEAL_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.PMP_DEAL_NOT_INSERTED.getName());
                return msg;
            }

            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int db_id = -1;

            if (keyResultSet.next())
            {
                db_id = keyResultSet.getInt(1);
            }

            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(db_id+"");
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
}