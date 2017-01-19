package com.kritter.kritterui.api.audience_definition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.audience.AudienceDefinitionList;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.audience_definition.AudienceDefinition;
import com.kritter.entity.audience_definition.AudienceDefinitionInput;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class AudienceDefinitionCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(AudienceDefinitionCrud.class);
    
    public static AudienceDefinition populateDefault( ResultSet rset) throws SQLException{
    	AudienceDefinition entity = null;
        if(rset != null){
        	entity = new AudienceDefinition();
        	entity.setInternalid(rset.getInt("internalid"));
        	entity.setAudience_type(rset.getInt("audience_type"));
        	if(rset.getObject("parent_internalid") != null){
        		entity.setParent_internalid(rset.getInt("parent_internalid"));
        	}
        	if(rset.getObject("tier") != null){
        		entity.setTier(rset.getInt("tier"));
        	}
        	entity.setLast_modified(rset.getTimestamp("last_modified").getTime());
        	entity.setName(rset.getString("name"));
        }
        return entity;
    }
    
    
    


    public static JsonNode various_get_audience_definition(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
        	AudienceDefinitionList returnEntity = new AudienceDefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADINPUT_NULL.getId());
            msg.setMsg(ErrorEnum.ADINPUT_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
        try{
        	AudienceDefinitionInput entity = AudienceDefinitionInput.getObject(jsonNode);
            return various_get_audience_definition(con, entity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AudienceDefinitionList returnEntity = new AudienceDefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
    }
    public static AudienceDefinitionList various_get_audience_definition(Connection con, AudienceDefinitionInput entity){
        if(con == null){
        	AudienceDefinitionList returnEntity = new AudienceDefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        if(entity == null){
        	AudienceDefinitionList returnEntity = new AudienceDefinitionList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ADINPUT_NULL.getId());
            msg.setMsg(ErrorEnum.ADINPUT_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        PreparedStatement pstmt = null;
        try{
            switch (entity.getQueryType()){
                case list_by_ids:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.AudienceDefinitionDef.list_by_ids, "<id>", entity.getIds(), 
                            ",", false));
                    break;
                case list_by_parentids:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.AudienceDefinitionDef.list_by_parentids, "<id>", entity.getIds(), 
                            ",", false));
                    break;
                case list_by_type:
                    pstmt = con.prepareStatement(
                            com.kritter.kritterui.api.db_query_def.AudienceDefinitionDef.list_by_type);
                    pstmt.setInt(1, entity.getAudience_type());
                    break;
                case list_by_type_tier:
                    pstmt = con.prepareStatement(
                            com.kritter.kritterui.api.db_query_def.AudienceDefinitionDef.list_by_type_tier);
                    pstmt.setInt(1, entity.getAudience_type());
                    pstmt.setInt(2, entity.getAudience_tier());
                    break;
                case list_cat_by_tier:
                    pstmt = con.prepareStatement(
                            com.kritter.kritterui.api.db_query_def.AudienceDefinitionDef.list_cat_by_tier);
                    pstmt.setInt(1, entity.getAudience_tier());
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AudienceDefinitionList returnEntity = new AudienceDefinitionList();
            List<AudienceDefinition> defaultEntities = new LinkedList<AudienceDefinition>();
            while(rset.next()){
            	AudienceDefinition defaultEntity = populateDefault(rset);
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
                msg.setError_code(ErrorEnum.AD_NF.getId());
                msg.setMsg(ErrorEnum.AD_NF.getName());
            }
            returnEntity.setMsg(msg);
            return returnEntity;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AudienceDefinitionList returnEntity = new AudienceDefinitionList();
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
