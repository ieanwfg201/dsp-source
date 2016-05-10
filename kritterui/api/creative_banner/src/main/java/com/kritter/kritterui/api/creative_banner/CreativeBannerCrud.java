package com.kritter.kritterui.api.creative_banner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.creative_banner.CreativeBannerList;
import com.kritter.api.entity.creative_banner.CreativeBannerListEntity;
import com.kritter.api.entity.creative_banner.Creative_banner;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class CreativeBannerCrud {
    private static final Logger LOG = LoggerFactory.getLogger(CreativeBannerCrud.class);
    
    public static void populate(Creative_banner creative_banner, ResultSet rset) throws SQLException{
        if(creative_banner != null && rset != null){
            creative_banner.setAccount_guid(rset.getString("account_guid"));
            creative_banner.setGuid(rset.getString("guid"));
            creative_banner.setId(rset.getInt("id"));
            creative_banner.setModified_by(rset.getInt("modified_by"));
            creative_banner.setResource_uri(rset.getString("resource_uri"));
            creative_banner.setSlot_id(rset.getInt("slot_id"));
        }
    }
    
    public static JsonNode insert_creative_banner(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_BANNER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_BANNER_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Creative_banner creative_banner = objectMapper.treeToValue(jsonNode, Creative_banner.class);
            return insert_creative_banner(con, creative_banner, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message insert_creative_banner(Connection con, Creative_banner creative_banner, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(creative_banner == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_BANNER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_BANNER_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_banner.insert_creative_banner,PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
            pstmt.setString(2, creative_banner.getAccount_guid());
            pstmt.setInt(3, creative_banner.getSlot_id());
            pstmt.setString(4, creative_banner.getResource_uri());
            pstmt.setInt(5, creative_banner.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(6, ts);
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CREATIVE_BANNER_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.CREATIVE_BANNER_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int cb_id = -1;
            if (keyResultSet.next()) {
                cb_id = keyResultSet.getInt(1);
            }
            creative_banner.setId(cb_id);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(cb_id+"");
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
    
    public static JsonNode update_creative_banner(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_BANNER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_BANNER_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Creative_banner creative_banner = objectMapper.treeToValue(jsonNode, Creative_banner.class);
            return update_creative_banner(con, creative_banner, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_creative_banner(Connection con, Creative_banner creative_banner, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(creative_banner == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_BANNER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_BANNER_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_banner.update_creative_banner);
            pstmt.setInt(1, creative_banner.getSlot_id());
            pstmt.setString(2, creative_banner.getResource_uri());
            pstmt.setInt(3, creative_banner.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(4, ts);
            pstmt.setInt(5, creative_banner.getId());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CREATIVE_BANNER_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.CREATIVE_BANNER_NOT_UPDATED.getName());
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
    
    public static JsonNode various_get_creative_banner(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            CreativeBannerList cblist = new CreativeBannerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_BANNERLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_BANNERLIST_ENTITY_NULL.getName());
            cblist.setMsg(msg);
            return cblist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CreativeBannerListEntity cblistEntity = objectMapper.treeToValue(jsonNode, CreativeBannerListEntity.class);
            return various_get_creative_banner(con, cblistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CreativeBannerList cblist = new CreativeBannerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            cblist.setMsg(msg);
            return cblist.toJson();
        }
    }
    
    public static CreativeBannerList various_get_creative_banner(Connection con, CreativeBannerListEntity cblistEntity){
        if(con == null){
            CreativeBannerList cblist = new CreativeBannerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            cblist.setMsg(msg);
            return cblist;
        }
        if(cblistEntity == null){
            CreativeBannerList cblist = new CreativeBannerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_BANNERLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_BANNERLIST_ENTITY_NULL.getName());
            cblist.setMsg(msg);
            return cblist;
        }
        PreparedStatement pstmt = null;
        try{
            switch (cblistEntity.getCbenum()){
                case get_creative_banner:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_banner.get_creative_banner);
                    pstmt.setInt(1, cblistEntity.getId());
                    break;
                case list_creative_banner_by_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_banner.list_creative_banner_by_account);
                    pstmt.setString(1, cblistEntity.getAccount_guid());
                    break;
                case get_creative_banner_by_ids:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Creative_banner.get_creative_banner_by_ids, "<id>", cblistEntity.getGuid_list(), 
                            ",", true));
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            CreativeBannerList  cblist = new CreativeBannerList();
            List<Creative_banner> cbs = new LinkedList<Creative_banner>();
            while(rset.next()){
                Creative_banner cb = new Creative_banner();
                populate(cb, rset);
                cbs.add(cb);
            }
            cblist.setCblist(cbs);
            Message msg = new Message();
            if(cbs.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.CREATIVE_BANNER_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.CREATIVE_BANNER_NOT_FOUND.getName());
            }
            cblist.setMsg(msg);
            return cblist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CreativeBannerList  cblist = new CreativeBannerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            cblist.setMsg(msg);
            return cblist;
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
}
