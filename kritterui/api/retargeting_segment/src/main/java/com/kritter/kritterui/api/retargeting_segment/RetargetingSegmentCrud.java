package com.kritter.kritterui.api.retargeting_segment;

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

import com.kritter.api.entity.retargeting_segment.RetargetingSegmentInputEntity;
import com.kritter.api.entity.retargeting_segment.RetargetingSegmentList;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.retargeting_segment.RetargetingSegment;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class RetargetingSegmentCrud {
    private static final Logger LOG = LoggerFactory.getLogger(RetargetingSegmentCrud.class);
    public static void populate(RetargetingSegment retargeting_segment, ResultSet rset) throws SQLException{
        if(retargeting_segment != null && rset != null){
            retargeting_segment.set_deprecated(rset.getBoolean("is_deprecated"));
            retargeting_segment.setAccount_guid(rset.getString("account_guid"));
            retargeting_segment.setCreated_on(rset.getTimestamp("created_on").getTime());
            retargeting_segment.setLast_modified(rset.getTimestamp("last_modified").getTime());
            retargeting_segment.setModified_by(rset.getInt("modified_by"));
            retargeting_segment.setName(rset.getString("name"));
            retargeting_segment.setRetargeting_segment_id(rset.getInt("id"));
            retargeting_segment.setTag(rset.getString("tag"));
        }
    }
    
    public static JsonNode insert_retargeting_segment(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.RETARGETING_SEGMENT_NULL.getId());
            msg.setMsg(ErrorEnum.RETARGETING_SEGMENT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            RetargetingSegment retargeting_segment = objectMapper.treeToValue(jsonNode, RetargetingSegment.class);
            return insert_retargeting_segment(con, retargeting_segment, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message insert_retargeting_segment(Connection con, RetargetingSegment retargeting_segment , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(retargeting_segment == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.RETARGETING_SEGMENT_NULL.getId());
            msg.setMsg(ErrorEnum.RETARGETING_SEGMENT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.RetargetingSegment.insert_retargeting_segment, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, retargeting_segment.getName());
            pstmt.setString(2, retargeting_segment.getTag());
            pstmt.setBoolean(3, retargeting_segment.is_deprecated());
            pstmt.setString(4, retargeting_segment.getAccount_guid());
            pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
            pstmt.setInt(6,retargeting_segment.getModified_by());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.RETARGETING_SEGMENT_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.RETARGETING_SEGMENT_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int retargeting_segment_id = -1;
            if (keyResultSet.next()) {
                retargeting_segment_id = keyResultSet.getInt(1);
            }
            retargeting_segment.setRetargeting_segment_id(retargeting_segment_id);
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
    
    public static JsonNode update_retargeting_segment(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.RETARGETING_SEGMENT_NULL.getId());
            msg.setMsg(ErrorEnum.RETARGETING_SEGMENT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            RetargetingSegment retargeting_segment = objectMapper.treeToValue(jsonNode, RetargetingSegment.class);
            return update_retargeting_segment(con, retargeting_segment, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_retargeting_segment(Connection con, RetargetingSegment retargeting_segment , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(retargeting_segment == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.RETARGETING_SEGMENT_NULL.getId());
            msg.setMsg(ErrorEnum.RETARGETING_SEGMENT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.RetargetingSegment.update_retargeting_segment);
            pstmt.setString(1, retargeting_segment.getName());
            pstmt.setString(2, retargeting_segment.getTag());
            pstmt.setBoolean(3, retargeting_segment.is_deprecated());
            pstmt.setString(4, retargeting_segment.getAccount_guid());
            pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
            pstmt.setInt(6,retargeting_segment.getModified_by());
            pstmt.setInt(7,retargeting_segment.getRetargeting_segment_id());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.RETARGETING_SEGMENT_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.RETARGETING_SEGMENT_NOT_INSERTED.getName());
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
    
    
    public static JsonNode various_get_retargeting_segments(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            RetargetingSegmentList retargetingSegmentList = new RetargetingSegmentList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.RetargetingSegmentInputEntity_null.getId());
            msg.setMsg(ErrorEnum.RetargetingSegmentInputEntity_null.getName());
            retargetingSegmentList.setMsg(msg);
            return retargetingSegmentList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            RetargetingSegmentInputEntity retargetingSegmentInputEntity = objectMapper.treeToValue(jsonNode, RetargetingSegmentInputEntity.class);
            return various_get_retargeting_segments(con, retargetingSegmentInputEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            RetargetingSegmentList retargetingSegmentList = new RetargetingSegmentList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            retargetingSegmentList.setMsg(msg);
            return retargetingSegmentList.toJson();
        }
    }
    
    public static RetargetingSegmentList various_get_retargeting_segments(Connection con, RetargetingSegmentInputEntity retargetingSegmentInputEntity){
        if(con == null){
            RetargetingSegmentList retargetingSegmentList = new RetargetingSegmentList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            retargetingSegmentList.setMsg(msg);
            return retargetingSegmentList;
        }
        if(retargetingSegmentInputEntity == null){
            RetargetingSegmentList retargetingSegmentList = new RetargetingSegmentList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.RetargetingSegmentInputEntity_null.getId());
            msg.setMsg(ErrorEnum.RetargetingSegmentInputEntity_null.getName());
            retargetingSegmentList.setMsg(msg);
            return retargetingSegmentList;
        }
        PreparedStatement pstmt = null;
        try{
            switch (retargetingSegmentInputEntity.getRetargetingSegmentEnum()){
                case get_retargeting_segments_by_ids:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.RetargetingSegment.get_retargeting_segments_by_ids, 
                            "<id>", retargetingSegmentInputEntity.getId_list(),",", false));
                    break;
                case get_retargeting_segments_by_accounts:
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.RetargetingSegment.get_retargeting_segments_by_accounts, 
                            "<id>", retargetingSegmentInputEntity.getId_list(),",", true));
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            RetargetingSegmentList retargetingSegmentList = new RetargetingSegmentList();
            List<RetargetingSegment> retargetingSegments = new LinkedList<RetargetingSegment>();
            while(rset.next()){
                RetargetingSegment retargetingSegment = new RetargetingSegment();
                populate(retargetingSegment, rset);
                retargetingSegments.add(retargetingSegment);
            }
            retargetingSegmentList.setRetargeting_segment_list(retargetingSegments);
            Message msg = new Message();
            if(retargetingSegments.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.AD_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.AD_NOT_FOUND.getName());
            }
            retargetingSegmentList.setMsg(msg);
            return retargetingSegmentList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            RetargetingSegmentList retargetingSegmentList = new RetargetingSegmentList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            retargetingSegmentList.setMsg(msg);
            return retargetingSegmentList;
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
