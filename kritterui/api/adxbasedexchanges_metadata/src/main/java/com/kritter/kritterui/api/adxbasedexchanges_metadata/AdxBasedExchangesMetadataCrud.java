package com.kritter.kritterui.api.adxbasedexchanges_metadata;

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

import com.kritter.api.entity.adxbasedexchangesmetadata.AdxBasedExchangesMetadatList;
import com.kritter.api.entity.adxbasedexchangesmetadata.AdxBasedExchangesMetadataListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.adxbasedexchanges_metadata.AdxBasedExchangesMetadata;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class AdxBasedExchangesMetadataCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(AdxBasedExchangesMetadataCrud.class);
    
    public static AdxBasedExchangesMetadata populate( ResultSet rset) throws SQLException{
    	AdxBasedExchangesMetadata adxBased = null;
        if(rset != null){
        	adxBased = new AdxBasedExchangesMetadata();
        	adxBased.setInternalid(rset.getInt("internalid"));
        	adxBased.setPubIncId(rset.getInt("pubIncId"));
        	adxBased.setAdvertiser_upload(rset.getBoolean("advertiser_upload"));
        	adxBased.setAdposition_get(rset.getBoolean("adposition_get"));
        	adxBased.setBanner_upload(rset.getBoolean("banner_upload"));
        	adxBased.setVideo_upload(rset.getBoolean("video_upload"));
        	adxBased.setLast_modified(rset.getTimestamp("last_modified").getTime());
        }
        return adxBased;
    }

    
    public static JsonNode insert_adbasedexchanges_metadata(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
            msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdxBasedExchangesMetadata adxBased = objectMapper.treeToValue(jsonNode, AdxBasedExchangesMetadata.class);
            return insert_adbasedexchanges_metadata(con, adxBased, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_adbasedexchanges_metadata(Connection con, AdxBasedExchangesMetadata adxBased, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(adxBased == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
            msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AdxBasedExchangesMetadata.insert_adbasedexchanges_metadata,PreparedStatement.RETURN_GENERATED_KEYS);
            /**
             * insert into adxbasedexchanges_metadata(pubIncId,advertiser_upload,adposition_get,"
			+ "banner_upload,video_upload,last_modified) values(?,?,?,?,?,?
             */
            pstmt.setInt(1, adxBased.getPubIncId());
            pstmt.setBoolean(2, adxBased.isAdvertiser_upload());
            pstmt.setBoolean(3, adxBased.isAdposition_get());
            pstmt.setBoolean(4, adxBased.isBanner_upload());
            pstmt.setBoolean(5, adxBased.isVideo_upload());
            pstmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ADX_BASED_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.ADX_BASED_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int auto_id = -1;
            if (keyResultSet.next()) {
            	auto_id = keyResultSet.getInt(1);
            }
            adxBased.setInternalid(auto_id);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(auto_id+"");
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
    
    
    public static JsonNode update_adbasedexchanges_metadata(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
            msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdxBasedExchangesMetadata adxBased = objectMapper.treeToValue(jsonNode, AdxBasedExchangesMetadata.class);
            return update_adbasedexchanges_metadata(con, adxBased, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_adbasedexchanges_metadata(Connection con, AdxBasedExchangesMetadata adxBased, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(adxBased == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
            msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AdxBasedExchangesMetadata.update_adbasedexchanges_metadata);
            pstmt.setInt(1, adxBased.getPubIncId());
            pstmt.setBoolean(2, adxBased.isAdvertiser_upload());
            pstmt.setBoolean(3, adxBased.isAdposition_get());
            pstmt.setBoolean(4, adxBased.isBanner_upload());
            pstmt.setBoolean(5, adxBased.isVideo_upload());
            pstmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            pstmt.setInt(7, adxBased.getInternalid());

            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ADX_BASED_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.ADX_BASED_NOT_UPDATED.getName());
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
    


    public static JsonNode various_get_adbasedexchanges_metadata(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
        	AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
            msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdxBasedExchangesMetadataListEntity adxBased = objectMapper.treeToValue(jsonNode, AdxBasedExchangesMetadataListEntity.class);
            return various_get_adbasedexchanges_metadata(con, adxBased).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        	AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
    }
    public static AdxBasedExchangesMetadatList various_get_adbasedexchanges_metadata(Connection con, AdxBasedExchangesMetadataListEntity adxBased){
        if(con == null){
        	AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        if(adxBased == null){
        	AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
            msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        PreparedStatement pstmt = null;
        try{
            switch (adxBased.getQueryEnum()){
                case get_all:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AdxBasedExchangesMetadata.get_all);
                    break;
                case get_by_internalids:
                    if(adxBased.getId_list() == null || "ALL".equalsIgnoreCase(adxBased.getId_list()) 
                    || "None".equalsIgnoreCase(adxBased.getId_list()) ||"[all]".equalsIgnoreCase(adxBased.getId_list()) 
                    || "[none]".equalsIgnoreCase(adxBased.getId_list())){
                    	AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
                        msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
                        returnEntity.setMsg(msg);
                        return returnEntity;
                    }

                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                    		com.kritter.kritterui.api.db_query_def.AdxBasedExchangesMetadata.get_by_internalids, "<id>", adxBased.getId_list(), 
                            ",", false));
                    
                    break;
                case get_by_pubincids:
                    if(adxBased.getId_list() == null || "ALL".equalsIgnoreCase(adxBased.getId_list()) 
                    || "None".equalsIgnoreCase(adxBased.getId_list()) ||"[all]".equalsIgnoreCase(adxBased.getId_list()) 
                    || "[none]".equalsIgnoreCase(adxBased.getId_list())){
                    	AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.ADX_BASED_NULL.getId());
                        msg.setMsg(ErrorEnum.ADX_BASED_NULL.getName());
                        returnEntity.setMsg(msg);
                        return returnEntity;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                    		com.kritter.kritterui.api.db_query_def.AdxBasedExchangesMetadata.get_by_pubincids, "<id>", adxBased.getId_list(), 
                            ",", false));
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
            List<AdxBasedExchangesMetadata> entities = new LinkedList<AdxBasedExchangesMetadata>();
            while(rset.next()){
            	AdxBasedExchangesMetadata entity = populate(rset);
                if(entity != null){
                	entities.add(entity);
                }
            }
            returnEntity.setEntity_list(entities);
            Message msg = new Message();
            if(entities.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.CAMPAIGN_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.CAMPAIGN_NOT_FOUND.getName());
            }
            returnEntity.setMsg(msg);
            return returnEntity;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
        	AdxBasedExchangesMetadatList returnEntity = new AdxBasedExchangesMetadatList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            returnEntity.setMsg(msg);
            return returnEntity;

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
