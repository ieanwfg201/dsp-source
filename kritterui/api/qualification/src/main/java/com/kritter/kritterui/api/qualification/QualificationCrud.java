package com.kritter.kritterui.api.qualification;

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

import com.kritter.api.entity.qualification.QualificationList;
import com.kritter.api.entity.qualification.QualificationListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.QualificationState;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.account.Qualification;
import com.kritter.kritterui.api.db_query_def.QualificationDef;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class QualificationCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(QualificationCrud.class);
    
    public static Qualification populateDefault( ResultSet rset) throws SQLException{
    	Qualification entity = null;
        if(rset != null){
        	entity = new Qualification();
        	entity.setAdvIncId(rset.getInt("advIncId"));
        	entity.setInternalid(rset.getInt("internalid"));
        	entity.setMd5(rset.getString("md5"));
        	entity.setQname(rset.getString("name"));
        	entity.setQurl(rset.getString("url"));
        	entity.setState(rset.getInt("state"));
        }
        return entity;
    }
    
    public static JsonNode insert_qualification(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Qualification entity = objectMapper.treeToValue(jsonNode, Qualification.class);
            return insert_qualification(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_qualification(Connection con, Qualification entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.QualificationDef.insert_qualification);
            pstmt.setString(1, entity.getQname());
            pstmt.setString(2, entity.getQurl());
            pstmt.setString(3, entity.getMd5());
            pstmt.setInt(4, QualificationState.add.getCode());
            pstmt.setInt(5, entity.getAdvIncId());
            pstmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.QUALIFICATION_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.QUALIFICATION_NOT_INSERTED.getName());
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
    
    
    public static JsonNode update_qualification(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Qualification entity = objectMapper.treeToValue(jsonNode, Qualification.class);
            return update_qualification(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_qualification(Connection con, Qualification entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.QualificationDef.update_qualification);
            pstmt.setString(1, entity.getQname());
            pstmt.setString(2, entity.getQurl());
            pstmt.setString(3, entity.getMd5());
            pstmt.setInt(4, QualificationState.update.getCode());
            pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
            pstmt.setInt(6, entity.getInternalid());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.QUALIFICATION_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.QUALIFICATION_NOT_UPDATED.getName());
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

    public static JsonNode delete_multiple_qualification(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            QualificationListEntity entity = objectMapper.treeToValue(jsonNode, QualificationListEntity.class);
            return delete_multiple_qualification(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message delete_multiple_qualification(Connection con, QualificationListEntity entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
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
                    com.kritter.kritterui.api.db_query_def.QualificationDef.delete_multiple_qualification, "<id>", entity.getId_list(), 
                    ",", false));
            pstmt.setInt(1, QualificationState.delete.getCode());
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.QUALIFICATION_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.QUALIFICATION_NOT_UPDATED.getName());
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

    public static JsonNode various_get_qualification(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
        	QualificationList returnEntity = new QualificationList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            QualificationListEntity entity = objectMapper.treeToValue(jsonNode, QualificationListEntity.class);
            return various_get_qualification(con, entity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            QualificationList returnEntity = new QualificationList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
    }
    public static QualificationList various_get_qualification(Connection con, QualificationListEntity entity){
        if(con == null){
        	QualificationList returnEntity = new QualificationList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        if(entity == null){
        	QualificationList returnEntity = new QualificationList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.QUALIFICATION_NULL.getId());
            msg.setMsg(ErrorEnum.QUALIFICATION_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        PreparedStatement pstmt = null;
        try{
            switch (entity.getQueryEnum()){
                case select_qualification_byadvertisers:
                    if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list())
                    		|| "[all]".equalsIgnoreCase(entity.getId_list()) || "all".equalsIgnoreCase(entity.getId_list())){
                    	QualificationList adlist = new QualificationList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.NO_ERROR.getId());
                        msg.setMsg(ErrorEnum.NO_ERROR.getName());
                        adlist.setMsg(msg);
                        return adlist;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.QualificationDef.select_qualification_byadvertisers, "<id>", entity.getId_list(), 
                            ",", false));
                    }
                    break;
                case select_qualification_byinternalids:
                    if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list())
                    		|| "[all]".equalsIgnoreCase(entity.getId_list()) || "all".equalsIgnoreCase(entity.getId_list())){
                    	QualificationList adlist = new QualificationList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.NO_ERROR.getId());
                        msg.setMsg(ErrorEnum.NO_ERROR.getName());
                        adlist.setMsg(msg);
                        return adlist;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.QualificationDef.select_qualification_byinternalids, "<id>", entity.getId_list(), 
                            ",", false));
                    }
                    break;
                case select_by_name_adv:
                    if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list())
                    		|| "[all]".equalsIgnoreCase(entity.getId_list()) || "all".equalsIgnoreCase(entity.getId_list())){
                    	QualificationList adlist = new QualificationList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.NO_ERROR.getId());
                        msg.setMsg(ErrorEnum.NO_ERROR.getName());
                        adlist.setMsg(msg);
                        return adlist;
                    }else{
                        pstmt = con.prepareStatement(QualificationDef.select_by_name_adv);
                        pstmt.setInt(1, Integer.parseInt(entity.getId_list()));
                        pstmt.setString(2, entity.getName());
                    }
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            QualificationList returnEntity = new QualificationList();
            List<Qualification> defaultEntities = new LinkedList<Qualification>();
            while(rset.next()){
            	Qualification defaultEntity = populateDefault(rset);
            	if(defaultEntity != null){
            		defaultEntities.add(defaultEntity);
            	}
            }
            returnEntity.setEntity_list(defaultEntities);;
            Message msg = new Message();
            if(defaultEntities.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.QUALIFICATION_NF.getId());
                msg.setMsg(ErrorEnum.QUALIFICATION_NF.getName());
            }
            returnEntity.setMsg(msg);
            return returnEntity;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            QualificationList returnEntity = new QualificationList();
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
