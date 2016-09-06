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

import com.kritter.api.entity.adpositionget.AdpositionGetList;
import com.kritter.api.entity.adpositionget.AdpositionGetListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.adxbasedexchanges_metadata.AdPositionGet;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class AdpositionGetCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(AdpositionGetCrud.class);
    
    public static AdPositionGet populate( ResultSet rset) throws SQLException{
    	AdPositionGet entity = null;
        if(rset != null){
        	entity = new AdPositionGet();
        	entity.setInternalid(rset.getInt("internalid"));
        	entity.setPubIncId(rset.getInt("pubIncId"));
        	entity.setAdxbasedexhangesstatus(rset.getInt("adxbasedexhangesstatus"));
        	entity.setMessage(rset.getString("message"));
        	entity.setLast_modified(rset.getTimestamp("last_modified").getTime());
        }
        return entity;
    }

    
    public static JsonNode insert_adposition_get(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdPositionGet entity = objectMapper.treeToValue(jsonNode, AdPositionGet.class);
            return insert_adposition_get(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_adposition_get(Connection con, AdPositionGet entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AdpositionGetDef.insert_adposition_get,PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, entity.getPubIncId());
            pstmt.setInt(2, entity.getAdxbasedexhangesstatus());
            pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ADPOSITION_GET_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.ADPOSITION_GET_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int auto_id = -1;
            if (keyResultSet.next()) {
            	auto_id = keyResultSet.getInt(1);
            }
            entity.setInternalid(auto_id);
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
    
    
    public static JsonNode update_adposition_get(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdPositionGet entity = objectMapper.treeToValue(jsonNode, AdPositionGet.class);
            return update_adposition_get(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_adposition_get(Connection con, AdPositionGet entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AdpositionGetDef.update_insert_adposition_status);
            pstmt.setInt(1, entity.getAdxbasedexhangesstatus());
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmt.setInt(3, entity.getInternalid());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ADPOSITION_GET_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.ADPOSITION_GET_NOT_UPDATED.getName());
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
    
    public static JsonNode various_adposition_get(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
        	AdpositionGetList returnEntity = new AdpositionGetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdpositionGetListEntity entity = objectMapper.treeToValue(jsonNode, AdpositionGetListEntity.class);
            return various_adposition_get(con, entity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AdpositionGetList returnEntity = new AdpositionGetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
    }
    public static AdpositionGetList various_adposition_get(Connection con, AdpositionGetListEntity entity){
        if(con == null){
        	AdpositionGetList returnEntity = new AdpositionGetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        if(entity == null){
        	AdpositionGetList returnEntity = new AdpositionGetList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        PreparedStatement pstmt = null;
        try{
            switch (entity.getQueryEnum()){
                case list_adposition_get:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AdpositionGetDef.list_adposition_get);
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AdpositionGetList returnEntity = new AdpositionGetList();
            List<AdPositionGet> entities = new LinkedList<AdPositionGet>();
            while(rset.next()){
            	AdPositionGet outentity = populate(rset);
                if(outentity != null){
                	entities.add(outentity);
                }
            }
            returnEntity.setEntity_list(entities);
            Message msg = new Message();
            if(entities.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.ADPOSITION_GET_NF.getId());
                msg.setMsg(ErrorEnum.ADPOSITION_GET_NF.getName());
            }
            returnEntity.setMsg(msg);
            return returnEntity;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AdpositionGetList returnEntity = new AdpositionGetList();
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
    public static JsonNode update_adposition_get_status_by_pubincids(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            AdpositionGetListEntity entity = objectMapper.treeToValue(jsonNode, AdpositionGetListEntity.class);
            return update_adposition_get_status_by_pubincids(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_adposition_get_status_by_pubincids(Connection con, AdpositionGetListEntity entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADPOSITION_GET_NULL.getId());
            msg.setMsg(ErrorEnum.ADPOSITION_GET_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                    com.kritter.kritterui.api.db_query_def.AdpositionGetDef.update_status_by_pubincids, "<id>", entity.getId_list(), 
                    ",", false));

            pstmt.setInt(1, entity.getAdxstate().getCode());
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ADPOSITION_GET_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.ADPOSITION_GET_NOT_UPDATED.getName());
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

}
