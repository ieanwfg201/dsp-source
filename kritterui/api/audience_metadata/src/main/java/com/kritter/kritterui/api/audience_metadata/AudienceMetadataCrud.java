package com.kritter.kritterui.api.audience_metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.audience.AudienceMetadataList;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.audience_metadata.AudienceMetadata;
import com.kritter.entity.audience_metadata.AudienceMetadataInput;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class AudienceMetadataCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(AudienceMetadataCrud.class);
    
    public static AudienceMetadata populateDefault( ResultSet rset) throws SQLException{
    	AudienceMetadata entity = null;
        if(rset != null){
        	entity = new AudienceMetadata();
        	entity.setInternalid(rset.getInt("internalid"));
        	entity.setEnabled(rset.getBoolean("enabled"));
        	entity.setLast_modified(rset.getTimestamp("last_modified").getTime());
        	entity.setName(rset.getString("name"));
        }
        return entity;
    }
    
    
    
    public static JsonNode update_audience_metadata(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AMINPUT_NULL.getId());
            msg.setMsg(ErrorEnum.AMINPUT_NULL.getName());
            return msg.toJson();
        }
        try{
        	AudienceMetadataInput entity = AudienceMetadataInput.getObject(jsonNode);
            return update_audience_metadata(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_audience_metadata(Connection con, AudienceMetadataInput entity , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AMINPUT_NULL.getId());
            msg.setMsg(ErrorEnum.AMINPUT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            if(entity.getIds()== null || entity.getIds().isEmpty() ||entity.getEnabled()==null){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AMINPUT_INCORRECT.getId());
                msg.setMsg(ErrorEnum.AMINPUT_INCORRECT.getName());
                return msg;
            	
            }
            pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                    com.kritter.kritterui.api.db_query_def.AudienceMetadataDef.update_audience_metadata, "<id>", entity.getIds(), 
                    ",", false));
            
            pstmt.setBoolean(1, entity.getEnabled());
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AM_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AM_NOT_UPDATED.getName());
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

    public static JsonNode various_get_audience_metadata(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
        	AudienceMetadataList returnEntity = new AudienceMetadataList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AMINPUT_NULL.getId());
            msg.setMsg(ErrorEnum.AMINPUT_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
        try{
        	AudienceMetadataInput entity = AudienceMetadataInput.getObject(jsonNode);
            return various_get_audience_metadata(con, entity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AudienceMetadataList returnEntity = new AudienceMetadataList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
    }
    public static AudienceMetadataList various_get_audience_metadata(Connection con, AudienceMetadataInput entity){
        if(con == null){
        	AudienceMetadataList returnEntity = new AudienceMetadataList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        if(entity == null){
        	AudienceMetadataList returnEntity = new AudienceMetadataList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AMINPUT_NULL.getId());
            msg.setMsg(ErrorEnum.AMINPUT_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        PreparedStatement pstmt = null;
        try{
            switch (entity.getQueryType()){
                case list_all:
                	pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceMetadataDef.select__audience_metadata);
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AudienceMetadataList returnEntity = new AudienceMetadataList();
            List<AudienceMetadata> defaultEntities = new LinkedList<AudienceMetadata>();
            while(rset.next()){
            	AudienceMetadata defaultEntity = populateDefault(rset);
            	if(defaultEntity != null){
            		defaultEntities.add(defaultEntity);
            	}
            }
            returnEntity.setList(defaultEntities);
            Message msg = new Message();
            if(defaultEntities.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.AM_NF.getId());
                msg.setMsg(ErrorEnum.AM_NF.getName());
            }
            returnEntity.setMsg(msg);
            return returnEntity;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AudienceMetadataList returnEntity = new AudienceMetadataList();
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
