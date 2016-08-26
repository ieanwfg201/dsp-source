package com.kritter.kritterui.api.deal;

import com.kritter.api.entity.deal.*;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.db_query_def.PrivateMarketPlaceDeal;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

            String domainsWhitelisted = null;

            if(null != privateMarketPlaceDeal.getWhitelistedAdvertiserDomains())
            {
                String[] domains = null;
                String values[] =  privateMarketPlaceDeal.getWhitelistedAdvertiserDomains().split(",");
                if(null != values && values.length > 0)
                    domains = new String[values.length];

                int i = 0;
                for(String value: values)
                {
                    domains[i++] = value;
                }
                ObjectMapper o = new ObjectMapper();
                domainsWhitelisted = o.writeValueAsString(domains);
            }

            pstmt.setString(1,privateMarketPlaceDeal.getDealId());
            pstmt.setString(2, privateMarketPlaceDeal.getDealName());
            pstmt.setString(3,privateMarketPlaceDeal.getAdIdList());
            pstmt.setString(4,privateMarketPlaceDeal.getSiteIdList());
            pstmt.setString(5,privateMarketPlaceDeal.getBlockedIABCategories());
            pstmt.setString(6,privateMarketPlaceDeal.getThirdPartyConnectionGuid());
            pstmt.setString(7,privateMarketPlaceDeal.getDspIdList());
            pstmt.setString(8,privateMarketPlaceDeal.getAdvertiserIdList());
            pstmt.setString(9,domainsWhitelisted);
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

    public static ThirdPartyConnectionChildIdList getDSPAdvIdListForAdvertiserGuid(Connection con, ThirdPartyConnectionChildId thirdPartyConnectionChildId)
    {
        if(null == thirdPartyConnectionChildId.getFetchDSPData())
            return null;

        ThirdPartyConnectionChildIdList thirdPartyConnectionChildIdList = new ThirdPartyConnectionChildIdList();
        List<ThirdPartyConnectionChildId> list = new ArrayList<ThirdPartyConnectionChildId>();

        PreparedStatement pstmt = null;

        try
        {
            if (thirdPartyConnectionChildId.getFetchDSPData().booleanValue())
            {
                pstmt = con.prepareStatement(ThirdPartyConnectionChildId.FETCH_DSP_DATA);
                pstmt.setString(1,thirdPartyConnectionChildId.getThirdPartyConnectionGuid());
                ResultSet rs = pstmt.executeQuery();
                while(rs.next())
                {
                    Integer id = rs.getInt("id");
                    String desc = rs.getString("description");
                    ThirdPartyConnectionChildId element = new ThirdPartyConnectionChildId();
                    element.setDescription(desc);
                    element.setId(id);
                    list.add(element);
                }
            }
            else
            {
                pstmt = con.prepareStatement(ThirdPartyConnectionChildId.FETCH_ADV_DATA);
                pstmt.setString(1,thirdPartyConnectionChildId.getThirdPartyConnectionGuid());
                ResultSet rs = pstmt.executeQuery();
                while(rs.next())
                {
                    Integer id = rs.getInt("id");
                    String desc = rs.getString("description");
                    ThirdPartyConnectionChildId element = new ThirdPartyConnectionChildId();
                    element.setDescription(desc);
                    element.setId(id);
                    list.add(element);
                }
            }

            Message message = new Message();
            message.setError_code(ErrorEnum.NO_ERROR.getId());

            thirdPartyConnectionChildIdList.setThirdPartyConnectionChildIdList(list);
            thirdPartyConnectionChildIdList.setMsg(message);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(),e);
            Message message = new Message();
            message.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            thirdPartyConnectionChildIdList.setMsg(message);
        }
        finally
        {
            if (pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch (SQLException e)
                {
                    LOG.error(e.getMessage(), e);
                    Message message = new Message();
                    message.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
                    thirdPartyConnectionChildIdList.setMsg(message);
                }
            }
        }

        return thirdPartyConnectionChildIdList;
    }

    public static PMPMessagePair get_PMP_Deal_By_Guid(Connection con, PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity){
        if(con == null){
            PMPMessagePair amp = new PMPMessagePair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            return amp;
        }
        if(privateMarketPlaceApiEntity == null){
            PMPMessagePair amp = new PMPMessagePair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_2.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_2.getName());
            amp.setMsg(msg);
            return amp;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(PrivateMarketPlaceDeal.GET_DEAL_BY_GUID);
            pstmt.setString(1, privateMarketPlaceApiEntity.getDealId());

            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                PMPMessagePair amp = new PMPMessagePair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populatePMP(privateMarketPlaceApiEntity, rset);
                amp.setPrivateMarketPlaceApiEntity(privateMarketPlaceApiEntity);
                amp.setMsg(msg);
                return amp;
            }
            PMPMessagePair amp = new PMPMessagePair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.USERIDINCORRECT.getId());
            msg.setMsg(ErrorEnum.USERIDINCORRECT.getName());
            amp.setMsg(msg);
            return amp;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            PMPMessagePair amp = new PMPMessagePair();
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

    public static List<PMPMessagePair> get_PMP_Deals(Connection con){

        List<PMPMessagePair> list = new ArrayList<PMPMessagePair>();
        if(con == null){
            PMPMessagePair amp = new PMPMessagePair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            amp.setMsg(msg);
            list.add(amp);
            return list;
        }

        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(PrivateMarketPlaceDeal.GET_DEALS);
            PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity = new PrivateMarketPlaceApiEntity();
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                PMPMessagePair amp = new PMPMessagePair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
                populatePMP(privateMarketPlaceApiEntity, rset);
                amp.setPrivateMarketPlaceApiEntity(privateMarketPlaceApiEntity);
                amp.setMsg(msg);
                list.add(amp);
            }

            if(list.size() <= 0) {
                PMPMessagePair amp = new PMPMessagePair();
                Message msg = new Message();
                msg.setError_code(ErrorEnum.USERIDINCORRECT.getId());
                msg.setMsg(ErrorEnum.USERIDINCORRECT.getName());
                amp.setMsg(msg);
                list.add(amp);
            }

            return list;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            PMPMessagePair amp = new PMPMessagePair();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            amp.setMsg(msg);
            list.add(amp);
            return list;
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

    public static PMPList get_PMPList(Connection con,PMPListEntity pmpListEntity){

        if(con == null){
            PMPList pmplist = new PMPList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            pmplist.setMsg(msg);
            return pmplist;
        }
        if(pmpListEntity == null){
            PMPList pmplist = new PMPList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PMP_DEAL_LIST_NULL.getId());
            msg.setMsg(ErrorEnum.PMP_DEAL_LIST_NULL.getName());
            pmplist.setMsg(msg);
            return pmplist;
        }
        PreparedStatement pstmt = null;
        try{

            pstmt = con.prepareStatement(PrivateMarketPlaceDeal.GET_DEALS);

            ResultSet rset = pstmt.executeQuery();
            PMPList pmplist = new PMPList();
            List<PrivateMarketPlaceApiEntity> deals = new LinkedList<PrivateMarketPlaceApiEntity>();
            while(rset.next()){
                PrivateMarketPlaceApiEntity deal = new PrivateMarketPlaceApiEntity();
                populatePMP(deal, rset);
                deals.add(deal);
            }
            pmplist.setPMP_list(deals);
            Message msg = new Message();
            if(deals.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }

            pmplist.setMsg(msg);
            return pmplist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            PMPList pmplist = new PMPList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            pmplist.setMsg(msg);
            return pmplist;
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

    private static void populatePMP(PrivateMarketPlaceApiEntity privateMarketPlaceApiEntity,ResultSet resultSet) throws Exception
    {
        if(null != privateMarketPlaceApiEntity && null != resultSet)
        {
            privateMarketPlaceApiEntity.setDealId(resultSet.getString("deal_id"));
            privateMarketPlaceApiEntity.setDealName(resultSet.getString("deal_name"));
            privateMarketPlaceApiEntity.setAdIdList(resultSet.getString("ad_id_list"));
            privateMarketPlaceApiEntity.setSiteIdList(resultSet.getString("site_id_list"));
            privateMarketPlaceApiEntity.setBlockedIABCategories(resultSet.getString("bcat"));
            privateMarketPlaceApiEntity.setThirdPartyConnectionGuid(resultSet.getString("third_party_conn_list"));
            privateMarketPlaceApiEntity.setDspIdList(resultSet.getString("dsp_id_list"));
            privateMarketPlaceApiEntity.setAdvertiserIdList(resultSet.getString("adv_id_list"));
            privateMarketPlaceApiEntity.setWhitelistedAdvertiserDomains(resultSet.getString("wadomain"));
            privateMarketPlaceApiEntity.setAuctionType(String.valueOf(resultSet.getShort("auction_type")));
            privateMarketPlaceApiEntity.setRequestCap(String.valueOf(resultSet.getInt("request_cap")));
            privateMarketPlaceApiEntity.setStartDate((resultSet.getTimestamp("start_date")).getTime());
            privateMarketPlaceApiEntity.setStartDate((resultSet.getTimestamp("end_date")).getTime());
            privateMarketPlaceApiEntity.setDealCPM(String.valueOf(resultSet.getDouble("deal_cpm")));
        }
    }

}