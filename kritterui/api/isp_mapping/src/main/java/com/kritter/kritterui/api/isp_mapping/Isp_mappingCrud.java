package com.kritter.kritterui.api.isp_mapping;

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

import com.kritter.api.entity.isp_mapping.Isp_mapping;
import com.kritter.api.entity.isp_mapping.Isp_mappingList;
import com.kritter.api.entity.isp_mapping.Isp_mappingListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class Isp_mappingCrud {
    private static final Logger LOG = LoggerFactory.getLogger(Isp_mappingCrud.class);
    public static void populate(Isp_mapping isp_mapping, ResultSet rset) throws SQLException{
        if(isp_mapping != null && rset != null){
            isp_mapping.setId(rset.getInt("id"));
            isp_mapping.setCountry_name(rset.getString("country_name"));
            isp_mapping.setData_source_name(rset.getString("data_source_name"));
            isp_mapping.setIsp_name(rset.getString("isp_name"));
            String isp_ui_name = rset.getString("isp_ui_name");
            if(isp_ui_name != null){
                isp_mapping.setIsp_ui_name(isp_ui_name);
            }
            isp_mapping.setIs_marked_for_deletion(rset.getBoolean("is_marked_for_deletion"));
            isp_mapping.setIsp_mapping_id(rset.getInt("isp_mapping_id"));
            java.sql.Timestamp ts = rset.getTimestamp("modified_on");
            if(ts != null){
                isp_mapping.setModified_on(rset.getTimestamp("modified_on").getTime());
            }
        }
    }
    
    public static JsonNode insert_isp_mapping(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ISP_MAPPING_NULL.getId());
            msg.setMsg(ErrorEnum.ISP_MAPPING_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Isp_mapping isp_mapping = objectMapper.treeToValue(jsonNode, Isp_mapping.class);
            return insert_isp_mapping(con, isp_mapping, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message insert_isp_mapping(Connection con, Isp_mapping isp_mapping , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(isp_mapping == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ISP_MAPPING_NULL.getId());
            msg.setMsg(ErrorEnum.ISP_MAPPING_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Isp_mapping.insert_isp_mapping);
            pstmt.setString(1, isp_mapping.getCountry_name());
            pstmt.setString(2, isp_mapping.getIsp_ui_name().trim());
            pstmt.setString(3, isp_mapping.getIsp_name());
            pstmt.setBoolean(4, false);
            pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ISP_MAPPING_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.ISP_MAPPING_NOT_INSERTED.getName());
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
    
    
    
    public static JsonNode various_get_isp_mapping(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Isp_mappingList isp_mappingList = new Isp_mappingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getName());
            isp_mappingList.setMsg(msg);
            return isp_mappingList.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Isp_mappingListEntity isp_mappingListEntity = objectMapper.treeToValue(jsonNode, Isp_mappingListEntity.class);
            return various_get_isp_mapping(con, isp_mappingListEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Isp_mappingList isp_mappingList = new Isp_mappingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            isp_mappingList.setMsg(msg);
            return isp_mappingList.toJson();
        }
    }
    
    public static Isp_mappingList various_get_isp_mapping(Connection con, Isp_mappingListEntity isp_mappingListEntity){
        if(con == null){
            Isp_mappingList isp_mappingList = new Isp_mappingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            isp_mappingList.setMsg(msg);
            return isp_mappingList;
        }
        if(isp_mappingListEntity == null){
            Isp_mappingList isp_mappingList = new Isp_mappingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getName());
            isp_mappingList.setMsg(msg);
            return isp_mappingList;
        }
        PreparedStatement pstmt = null;
        try{
            Isp_mappingList isp_mappingList = new Isp_mappingList();
            switch (isp_mappingListEntity.getIsp_mappingEnum()){
                case get_mappings_by_country:
                    if("".equals(isp_mappingListEntity.getId_list()) || "ALL".equalsIgnoreCase(isp_mappingListEntity.getId_list())
                            || "none".equalsIgnoreCase(isp_mappingListEntity.getId_list())){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.ISP_MAPPING_COUNTRY_ABSENT.getId());
                        msg.setMsg(ErrorEnum.ISP_MAPPING_COUNTRY_ABSENT.getName());
                        isp_mappingList.setMsg(msg);
                        return isp_mappingList;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Isp_mapping.get_mappings_by_country, "<id>", isp_mappingListEntity.getId_list(), 
                            ",", false));
                    }
                    break;
                case get_mapped_isp_by_country:
                    if("".equals(isp_mappingListEntity.getId_list()) || "ALL".equalsIgnoreCase(isp_mappingListEntity.getId_list())
                            || "none".equalsIgnoreCase(isp_mappingListEntity.getId_list())){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.ISP_MAPPING_COUNTRY_ABSENT.getId());
                        msg.setMsg(ErrorEnum.ISP_MAPPING_COUNTRY_ABSENT.getName());
                        isp_mappingList.setMsg(msg);
                        return isp_mappingList;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Isp_mapping.get_mapped_isp_by_country, "<id>", isp_mappingListEntity.getId_list(), 
                            ",", false));
                    }
                    break;
                case get_rejected_isp_mapping_by_country:
                    if("".equals(isp_mappingListEntity.getId_list()) || "ALL".equalsIgnoreCase(isp_mappingListEntity.getId_list())
                            || "none".equalsIgnoreCase(isp_mappingListEntity.getId_list())){
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.ISP_MAPPING_COUNTRY_ABSENT.getId());
                        msg.setMsg(ErrorEnum.ISP_MAPPING_COUNTRY_ABSENT.getName());
                        isp_mappingList.setMsg(msg);
                        return isp_mappingList;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Isp_mapping.get_rejected_isp_mapping_by_country, "<id>", isp_mappingListEntity.getId_list(), 
                            ",", false));
                    }
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            List<Isp_mapping> isp_mappings = new LinkedList<Isp_mapping>();
            while(rset.next()){
                Isp_mapping isp_mapping = new Isp_mapping();
                populate(isp_mapping, rset);
                isp_mappings.add(isp_mapping);
            }
            isp_mappingList.setIsp_mappinglist(isp_mappings);
            Message msg = new Message();
            if(isp_mappings.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.ISP_MAPPING_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.ISP_MAPPING_NOT_FOUND.getName());
            }
            isp_mappingList.setMsg(msg);
            return isp_mappingList;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Isp_mappingList isp_mappingList = new Isp_mappingList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            isp_mappingList.setMsg(msg);
            return isp_mappingList;
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
    public static JsonNode update_isp_mapping(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Isp_mappingListEntity isp_mappingListEntity = objectMapper.treeToValue(jsonNode, Isp_mappingListEntity.class);
            return update_isp_mapping(con, isp_mappingListEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_isp_mapping(Connection con, Isp_mappingListEntity isp_mappingListEntity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(isp_mappingListEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.ISP_MAPPING_LIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            boolean success_when_zero_rows_affected=false;
            switch (isp_mappingListEntity.getIsp_mappingEnum()){
                case mark_delete:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Isp_mapping.mark_delete);
                    pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                    pstmt.setInt(2, isp_mappingListEntity.getId());
                    break;
                default:
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.ISP_MAPPING_ENUM_NF.getId());
                    msg.setMsg(ErrorEnum.ISP_MAPPING_ENUM_NF.getName());
                    return msg;
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0 && !success_when_zero_rows_affected){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.ISP_MAPPING_NF_FOR_UPDATE.getId());
                msg.setMsg(ErrorEnum.ISP_MAPPING_NF_FOR_UPDATE.getName());
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
