package com.kritter.kritterui.api.saved_query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.saved_query.SavedQueryEntity;
import com.kritter.api.entity.saved_query.SavedQueryList;
import com.kritter.api.entity.saved_query.SavedQueryListEntity;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.Frequency;
import com.kritter.constants.ReportingTypeEnum;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.kritterui.api.utils.TimeManipulation.TimeManipulator;

public class SavedQueryCrud {
    private static final Logger LOG = LoggerFactory.getLogger(SavedQueryCrud.class);
    public static void populate(SavedQueryEntity savedQuery, ResultSet rset) throws SQLException{
        if(savedQuery != null && rset != null){
            savedQuery.setAccount_guid(rset.getString("account_guid"));
            savedQuery.setCreated_on(rset.getTimestamp("created_on").getTime());
            savedQuery.setId(rset.getInt("id"));
            savedQuery.setModified_on(rset.getTimestamp("modified_on").getTime());
            savedQuery.setName(rset.getString("name"));
            String str = rset.getString("reporting_entity");
            ObjectMapper mapper = new ObjectMapper();
            ReportingEntity reportingEntity;
            try {
                reportingEntity = mapper.readValue(str, ReportingEntity.class);
                reportingEntity.setStartindex(0);
                Frequency freq = reportingEntity.getFrequency();
                TimeZone.setDefault(TimeZone.getTimeZone(reportingEntity.getTimezone()));
                Date enddate = new Date();
                Date startdate = new Date();
                startdate.setHours(0);startdate.setMinutes(0);startdate.setSeconds(0);
                enddate.setHours(23);enddate.setMinutes(59);enddate.setSeconds(59);
                switch(freq){
                case TODAY:
                    break;
                case YESTERDAY:
                    startdate = DateUtils.addDays(startdate, -1);
                    enddate = DateUtils.addDays(enddate, -1);
                    break;
                case LAST7DAYS:
                    startdate = DateUtils.addDays(startdate, -8);
                    enddate = DateUtils.addDays(enddate, -1);
                    break;
                case CURRENTMONTH:
                    startdate.setDate(1);
                    enddate = DateUtils.addDays(enddate, -1);
                    break;
                case LASTMONTH:
                    startdate = DateUtils.addMonths(startdate, -1);
                    startdate.setDate(1);
                    enddate = DateUtils.addMonths(enddate, -1);
                    enddate = DateUtils.setDays(enddate, -1);
                    break;
                default:
                    break;
                }
                reportingEntity.setStart_time_str(TimeManipulator.convertDate(startdate, reportingEntity.getDate_format(), reportingEntity.getTimezone()));
                reportingEntity.setEnd_time_str(TimeManipulator.convertDate(enddate, reportingEntity.getEnd_date_format(), reportingEntity.getTimezone()));
                savedQuery.setReporting_entity(reportingEntity);;
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
            } 
            savedQuery.setStatus_id(StatusIdEnum.getEnum(rset.getInt("status_id")));
            savedQuery.setReportingTypeEnum(ReportingTypeEnum.getEnum(rset.getInt("reporting_type")));
        }
    }

    public static JsonNode insert_saved_query(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SavedQueryEntity savedQuery = objectMapper.treeToValue(jsonNode, SavedQueryEntity.class);
            return insert_saved_query(con, savedQuery, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message insert_saved_query(Connection con, SavedQueryEntity savedQuery, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(savedQuery == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.SavedQueryDef.insert_saved_query_entity);
            pstmt.setString(1, savedQuery.getName());
            pstmt.setString(2, savedQuery.getReporting_entity().toJson().toString());
            pstmt.setInt(3, savedQuery.getStatus_id().getCode());
            pstmt.setString(4, savedQuery.getAccount_guid());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(5, ts);
            pstmt.setInt(6, savedQuery.getReportingTypeEnum().getCode());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SAVED_QUERY_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.SAVED_QUERY_NOT_INSERTED.getName());
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
    
    public static JsonNode update_saved_query(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SavedQueryEntity savedQuery = objectMapper.treeToValue(jsonNode, SavedQueryEntity.class);
            return update_saved_query(con, savedQuery, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_saved_query(Connection con, SavedQueryEntity savedQuery, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(savedQuery == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_OBJECT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.SavedQueryDef.update_saved_query_entity);
            pstmt.setString(1, savedQuery.getName());
            pstmt.setString(2, savedQuery.getReporting_entity().toJson().toString());
            pstmt.setInt(3, savedQuery.getStatus_id().getCode());
            pstmt.setString(4, savedQuery.getAccount_guid());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(5, ts);
            pstmt.setInt(6, savedQuery.getReportingTypeEnum().getCode());
            pstmt.setInt(7, savedQuery.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SAVED_QUERY_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SAVED_QUERY_NOT_UPDATED.getName());
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
    public static JsonNode various_get_saved_query(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            SavedQueryList savedquerylist = new SavedQueryList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getName());
            savedquerylist.setMsg(msg);
            return savedquerylist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SavedQueryListEntity savedquerylistEntity = objectMapper.treeToValue(jsonNode, SavedQueryListEntity.class);
            return various_get_saved_query(con, savedquerylistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SavedQueryList savedquerylist = new SavedQueryList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            savedquerylist.setMsg(msg);
            return savedquerylist.toJson();
        }
    }
    public static SavedQueryList various_get_saved_query(Connection con, SavedQueryListEntity savedquerylistEntity){
        if(con == null){
            SavedQueryList savedquerylist = new SavedQueryList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            savedquerylist.setMsg(msg);
            return savedquerylist;
        }
        if(savedquerylistEntity == null){
            SavedQueryList savedquerylist = new SavedQueryList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getName());
            savedquerylist.setMsg(msg);
            return savedquerylist;
        }
        PreparedStatement pstmt = null;
        try{
            switch (savedquerylistEntity.getSaveQueryEnum()){
                case list_saved_query_entity_by_account_guids:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.SavedQueryDef.list_saved_query_entity_by_account_guids, "<id>", savedquerylistEntity.getId_list(), 
                            ",", true));
                    break;
                case list_saved_query_entity_by_entity_id:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.SavedQueryDef.list_saved_query_entity_by_entity_id, "<id>", savedquerylistEntity.getId_list(), 
                            ",", false));
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            SavedQueryList savequeryList = new SavedQueryList();
            List<SavedQueryEntity> savedQueryEntitys = new LinkedList<SavedQueryEntity>();
            while(rset.next()){
                SavedQueryEntity savedQueryEntity = new SavedQueryEntity();
                populate(savedQueryEntity, rset);
                savedQueryEntitys.add(savedQueryEntity);
            }
            savequeryList.setList(savedQueryEntitys);
            Message msg = new Message();
            if(savedQueryEntitys.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.SAVED_QUERY_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.SAVED_QUERY_NOT_FOUND.getName());
            }
            savequeryList.setMsg(msg);
            return savequeryList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            SavedQueryList savedquerylist = new SavedQueryList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            savedquerylist.setMsg(msg);
            return savedquerylist;
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

    public static JsonNode change_status_saved_query(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SavedQueryListEntity savedquerylistEntity = objectMapper.treeToValue(jsonNode, SavedQueryListEntity.class);
            return change_status_saved_query(con, savedquerylistEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message change_status_saved_query(Connection con, SavedQueryListEntity savedquerylistEntity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(savedquerylistEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SAVED_QUERY_LIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            switch (savedquerylistEntity.getSaveQueryEnum()){
                case delete_saved_query:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.SavedQueryDef.change_status_saved_query_entity, "<id>", savedquerylistEntity.getId_list(), 
                            ",", false));

                    pstmt.setInt(1,4);
                    break;
                default:
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.SAVED_QUERY_NOT_UPDATED.getId());
                    msg.setMsg(ErrorEnum.SAVED_QUERY_NOT_UPDATED.getName());
                    return msg;
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SAVED_QUERY_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.SAVED_QUERY_NOT_UPDATED.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            return msg;
        }catch(Exception e){
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
