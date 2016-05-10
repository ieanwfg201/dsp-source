package com.kritter.kritterui.api.native_icon;

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

import com.kritter.api.entity.native_icon.NativeIconList;
import com.kritter.api.entity.native_icon.NativeIconListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.native_props.demand.NativeIcon;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class NativeIconCrud {
    private static final Logger LOG = LoggerFactory.getLogger(NativeIconCrud.class);
    
    public static void populate(NativeIcon native_icon, ResultSet rset) throws SQLException{
        if(native_icon != null && rset != null){
            native_icon.setAccount_guid(rset.getString("account_guid"));
            native_icon.setGuid(rset.getString("guid"));
            native_icon.setId(rset.getInt("id"));
            native_icon.setModified_by(rset.getInt("modified_by"));
            native_icon.setResource_uri(rset.getString("resource_uri"));
            native_icon.setIcon_size(rset.getInt("icon_size"));
            native_icon.setCreated_on(rset.getTimestamp("created_on").getTime());
            native_icon.setLast_modified(rset.getTimestamp("last_modified").getTime());
        }
    }
    
    public static JsonNode insert_native_icon(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NATIVE_ICON_NULL.getId());
            msg.setMsg(ErrorEnum.NATIVE_ICON_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            NativeIcon native_icon = objectMapper.treeToValue(jsonNode, NativeIcon.class);
            return insert_native_icon(con, native_icon, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message insert_native_icon(Connection con, NativeIcon native_icon, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(native_icon == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NATIVE_ICON_NULL.getId());
            msg.setMsg(ErrorEnum.NATIVE_ICON_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.NativeIcon.insert_native_icon,PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
            pstmt.setString(2, native_icon.getAccount_guid());
            pstmt.setInt(3, native_icon.getIcon_size());
            pstmt.setString(4, native_icon.getResource_uri());
            pstmt.setInt(5, native_icon.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(6, ts);
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NATIVE_ICON_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.NATIVE_ICON_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int cb_id = -1;
            if (keyResultSet.next()) {
                cb_id = keyResultSet.getInt(1);
            }
            native_icon.setId(cb_id);
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
    
    public static JsonNode update_native_icon(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NATIVE_ICON_NULL.getId());
            msg.setMsg(ErrorEnum.NATIVE_ICON_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            NativeIcon native_icon = objectMapper.treeToValue(jsonNode, NativeIcon.class);
            return update_native_icon(con, native_icon, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_native_icon(Connection con, NativeIcon native_icon, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(native_icon == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NATIVE_ICON_NULL.getId());
            msg.setMsg(ErrorEnum.NATIVE_ICON_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.NativeIcon.update_native_icon);
            pstmt.setInt(1, native_icon.getIcon_size());
            pstmt.setString(2, native_icon.getResource_uri());
            pstmt.setInt(3, native_icon.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(4, ts);
            pstmt.setInt(5, native_icon.getId());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.NATIVE_ICON_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.NATIVE_ICON_NOT_UPDATED.getName());
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
    
    public static JsonNode various_get_native_icon(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            NativeIconList nativelist = new NativeIconList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NATIVE_ICONLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.NATIVE_ICONLIST_ENTITY_NULL.getName());
            nativelist.setMsg(msg);
            return nativelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            NativeIconListEntity nativelistEntity = objectMapper.treeToValue(jsonNode, NativeIconListEntity.class);
            return various_get_native_icon(con, nativelistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            NativeIconList nativelist = new NativeIconList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            nativelist.setMsg(msg);
            return nativelist.toJson();
        }
    }
    
    public static NativeIconList various_get_native_icon(Connection con, NativeIconListEntity nativelistEntity){
        if(con == null){
            NativeIconList nativelist = new NativeIconList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            nativelist.setMsg(msg);
            return nativelist;
        }
        if(nativelistEntity == null){
            NativeIconList nativelist = new NativeIconList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NATIVE_ICONLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.NATIVE_ICONLIST_ENTITY_NULL.getName());
            nativelist.setMsg(msg);
            return nativelist;
        }
        PreparedStatement pstmt = null;
        try{
            switch (nativelistEntity.getNativeenum()){
                case get_native_icon_by_id:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.NativeIcon.get_native_icon_by_id);
                    pstmt.setInt(1, nativelistEntity.getId());
                    break;
                case list_native_icon_by_account:
                    if(nativelistEntity.getAccount_guid() == null){
                        NativeIconList  cblist = new NativeIconList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.NATIVE_ICON_GUID_NULL.getId());
                        msg.setMsg(ErrorEnum.NATIVE_ICON_GUID_NULL.getName());
                        cblist.setMsg(msg);
                        return cblist;
                    }
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.NativeIcon.list_native_icon_by_account);
                    pstmt.setString(1, nativelistEntity.getAccount_guid());
                    break;
                case get_native_icon_by_ids:
                    if(nativelistEntity.getId_list() == null){
                        NativeIconList  cblist = new NativeIconList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.NATIVE_ICON_ID_LIST_NULL.getId());
                        msg.setMsg(ErrorEnum.NATIVE_ICON_ID_LIST_NULL.getName());
                        cblist.setMsg(msg);
                        return cblist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.NativeIcon.get_native_icon_by_ids, "<id>", nativelistEntity.getId_list(), 
                            ",", true));
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            NativeIconList  cblist = new NativeIconList();
            List<NativeIcon> cbs = new LinkedList<NativeIcon>();
            while(rset.next()){
                NativeIcon cb = new NativeIcon();
                populate(cb, rset);
                cbs.add(cb);
            }
            cblist.setCblist(cbs);
            Message msg = new Message();
            if(cbs.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.NATIVE_ICON_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.NATIVE_ICON_NOT_FOUND.getName());
            }
            cblist.setMsg(msg);
            return cblist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            NativeIconList  cblist = new NativeIconList();
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
