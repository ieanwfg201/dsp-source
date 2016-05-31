package com.kritter.kritterui.api.req_logging;

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
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.api.entity.req_logging.ReqLoggingInput;
import com.kritter.api.entity.req_logging.ReqLoggingList;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.ReqLoggingEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.req_logging.ReqLoggingEntity;

public class ReqLoggingCrud {
    private static final Logger LOG = LoggerFactory.getLogger(ReqLoggingCrud.class);
    public static void populate(ReqLoggingEntity reqLoggingEntity, ResultSet rset) throws SQLException{
        if(reqLoggingEntity != null && rset != null){
            reqLoggingEntity.setEnable(rset.getBoolean("enable"));
            reqLoggingEntity.setPubId(rset.getString("pubId"));
            int time_period = rset.getInt("time_period");
            reqLoggingEntity.setTime_period(time_period);
            reqLoggingEntity.setCreated_on(rset.getTimestamp("created_on").getTime());
            long lastModTime = rset.getTimestamp("last_modified").getTime();
            reqLoggingEntity.setLast_modified(lastModTime);
            java.util.Date now = new Date();
            long currentTime = now.getTime();
            if((currentTime-lastModTime) > (time_period*60*1000)){
                reqLoggingEntity.setEnable(false);
            }
        }
    }
    
    public static JsonNode various_get_req_logging(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            ReqLoggingList entityList = new ReqLoggingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingInput_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingInput_NULL.getName());
            entityList.setMsg(msg);
            return entityList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ReqLoggingInput reqLoggingInput = objectMapper.treeToValue(jsonNode, ReqLoggingInput.class);
            return various_get_req_logging(con, reqLoggingInput).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            ReqLoggingList entityList = new ReqLoggingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            entityList.setMsg(msg);
            return entityList.toJson();
        }
    }
    
    public static ReqLoggingList various_get_req_logging(Connection con, ReqLoggingInput reqLoggingInput){
        if(con == null){
            ReqLoggingList entityList = new ReqLoggingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            entityList.setMsg(msg);
            return entityList;
        }
        if(reqLoggingInput == null){
            ReqLoggingList entityList = new ReqLoggingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingInput_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingInput_NULL.getName());
            entityList.setMsg(msg);
            return entityList;
        }
        PreparedStatement pstmt = null;
        try{
            switch (reqLoggingInput.getReqLoggingEnum()){
                
                case get_all_req_logging_entities:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.ReqLoggingQueryDef.get_all_req_logging_entities);
                    break;
                case get_specific_req_logging_entities:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.ReqLoggingQueryDef.get_specific_req_logging_entities);
                    if(reqLoggingInput.getId_list()==null ||
                            reqLoggingInput.getId_list().trim().equals("")){
                        ReqLoggingList entityList = new ReqLoggingList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.ReqLoggingInput_NULL.getId());
                        msg.setMsg(ErrorEnum.ReqLoggingInput_NULL.getName());
                        entityList.setMsg(msg);
                        return entityList;                        
                    }
                    pstmt.setString(1, reqLoggingInput.getId_list());
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            ReqLoggingList entityList = new ReqLoggingList();
            List<ReqLoggingEntity> entities = new LinkedList<ReqLoggingEntity>();
            while(rset.next()){
                ReqLoggingEntity entity = new ReqLoggingEntity();
                populate(entity, rset);
                entities.add(entity);
            }
            entityList.setEntityList(entities);
            Message msg = new Message();
            if(entities.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.ReqLoggingEntity_NF.getId());
                msg.setMsg(ErrorEnum.ReqLoggingEntity_NF.getName());
            }
            entityList.setMsg(msg);
            return entityList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            ReqLoggingList entityList = new ReqLoggingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            entityList.setMsg(msg);
            return entityList;
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
    
    public static JsonNode get_data(Connection con, ReqLoggingInput reqLoggingInput){
        ReqLoggingList list = various_get_req_logging(con, reqLoggingInput);
        if(list != null && list.getEntityList() != null && list.getEntityList().size()>0){
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode dataNode = mapper.createArrayNode();
            for(ReqLoggingEntity entity:list.getEntityList()){
                ObjectNode dataObjNode = mapper.createObjectNode();
                dataObjNode.put("pubId",entity.getPubId());
                dataObjNode.put("enable",entity.isEnable());
                dataObjNode.put("timeperiod",entity.getTime_period());
                dataNode.add(dataObjNode);
            }
            ArrayNode columnNode = mapper.createArrayNode();
            ObjectNode columnObjNode = mapper.createObjectNode();
            columnObjNode.put("title", "pubId");
            columnObjNode.put("field", "pubId");
            columnObjNode.put("visible", true);
            columnNode.add(columnObjNode);
            ObjectNode columnObjNode1 = mapper.createObjectNode();
            columnObjNode1.put("title", "enable");
            columnObjNode1.put("field", "enable");
            columnObjNode1.put("visible", true);
            columnNode.add(columnObjNode1);
            ObjectNode columnObjNode2 = mapper.createObjectNode();
            columnObjNode2.put("title", "timeperiod in mins");
            columnObjNode2.put("field", "timeperiod");
            columnObjNode2.put("visible", true);
            columnNode.add(columnObjNode2);
            rootNode.put("column", columnNode);
            rootNode.put("data", dataNode);
            return rootNode;
        }
        return null;    
    }
    
    public static JsonNode update_req_logging(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingEntity_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingEntity_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ReqLoggingEntity reqLoggingEntity = objectMapper.treeToValue(jsonNode, ReqLoggingEntity.class);
            return update_req_logging(con, reqLoggingEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_req_logging(Connection con, ReqLoggingEntity reqLoggingEntity,
            boolean createTransaction ){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(reqLoggingEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingEntity_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingEntity_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.ReqLoggingQueryDef.update_req_logging);
            pstmt.setBoolean(1, reqLoggingEntity.isEnable());
            pstmt.setInt(2, reqLoggingEntity.getTime_period());
            pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
            pstmt.setString(4, reqLoggingEntity.getPubId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ReqLoggingEntity_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.ReqLoggingEntity_NOT_UPDATED.getName());
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
    public static JsonNode insert_req_logging(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingEntity_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingEntity_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ReqLoggingEntity reqLoggingEntity = objectMapper.treeToValue(jsonNode, ReqLoggingEntity.class);
            return insert_req_logging(con, reqLoggingEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message insert_req_logging(Connection con, ReqLoggingEntity reqLoggingEntity,
            boolean createTransaction ){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(reqLoggingEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingEntity_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingEntity_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.ReqLoggingQueryDef.insert_req_logging);
            pstmt.setString(1, reqLoggingEntity.getPubId());
            pstmt.setBoolean(2, reqLoggingEntity.isEnable());
            pstmt.setInt(3, reqLoggingEntity.getTime_period());
            pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
            pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ReqLoggingEntity_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.ReqLoggingEntity_NOT_INSERTED.getName());
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
    public static JsonNode check_update_insert_req_logging(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingEntity_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingEntity_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ReqLoggingEntity reqLoggingEntity = objectMapper.treeToValue(jsonNode, ReqLoggingEntity.class);
            return check_update_insert_req_logging(con, reqLoggingEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message check_update_insert_req_logging(Connection con, ReqLoggingEntity reqLoggingEntity,
            boolean createTransaction ){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(reqLoggingEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ReqLoggingEntity_NULL.getId());
            msg.setMsg(ErrorEnum.ReqLoggingEntity_NULL.getName());
            return msg;
        }
        ReqLoggingInput rli = new ReqLoggingInput();
        rli.setId_list(reqLoggingEntity.getPubId());
        rli.setReqLoggingEnum(ReqLoggingEnum.get_specific_req_logging_entities);
        ReqLoggingList rLL = various_get_req_logging(con, rli);
        if(rLL == null || rLL.getEntityList()==null
                || rLL.getEntityList().size() < 1){
            return insert_req_logging(con, reqLoggingEntity, createTransaction);
        }else{
            return update_req_logging(con, reqLoggingEntity, createTransaction);
        }
    }
}
