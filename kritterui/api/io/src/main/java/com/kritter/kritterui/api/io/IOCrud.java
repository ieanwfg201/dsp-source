package com.kritter.kritterui.api.io;

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

import com.kritter.api.entity.insertion_order.IOListEntity;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.insertion_order.Insertion_Order_List;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.IOStatus;
import com.kritter.constants.error.ErrorEnum;

public class IOCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(IOCrud.class);
    
    public static void populateIO(Insertion_Order io, ResultSet rset, boolean created_by_name) throws SQLException{
        if(io != null && rset != null){
            io.setAccount_guid(rset.getString("account_guid"));
            io.setModified_by(rset.getInt("modified_by"));
            io.setName(rset.getString("name"));
            io.setOrder_number(rset.getString("order_number"));
            io.setTotal_value(rset.getDouble("total_value"));
            io.setStatus(IOStatus.getEnum(rset.getInt("status")));
            io.setComment(rset.getString("comment"));
            io.setCreated_on(rset.getTimestamp("created_on").getTime());
            io.setCreated_by(rset.getInt("created_by"));
            io.setBelongs_to(rset.getInt("belongs_to"));
            if(created_by_name){
                io.setCreated_by_name(rset.getString("created_by_name"));
                io.setBelongs_to_name(rset.getString("belongs_to_name"));
            }
     }
    }
    
    public static JsonNode insert_io(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Insertion_Order io = objectMapper.treeToValue(jsonNode, Insertion_Order.class);
            return insert_io(con, io,true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message insert_io(Connection con, Insertion_Order io, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(io == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.insert_io);
            pstmt.setString(1, io.getOrder_number());
            pstmt.setString(2, io.getAccount_guid());
            pstmt.setString(3, io.getName());
            pstmt.setDouble(4, io.getTotal_value());
            pstmt.setInt(5, io.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(6, ts);
            pstmt.setInt(7, io.getStatus().getCode());
            pstmt.setString(8, null);
            pstmt.setInt(9, io.getCreated_by());
            pstmt.setInt(10, io.getBelongs_to());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.IO_NOT_CREATED.getId());
                msg.setMsg(ErrorEnum.IO_NOT_CREATED.getName());
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
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        } 
    }
    
    public static JsonNode check_io(Connection con, JsonNode jsonNode){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg.toJson();
        }
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Insertion_Order io = objectMapper.treeToValue(jsonNode, Insertion_Order.class);
            return check_io(con, io).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message check_io(Connection con, Insertion_Order io){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(io == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.check_io);
            pstmt.setString(1, io.getOrder_number());
            pstmt.setString(2, io.getAccount_guid());
            ResultSet rset = pstmt.executeQuery();
            if(rset.next()){
                populateIO(io, rset, false);
                Message msg = new Message();
                msg.setError_code(ErrorEnum.IO_ACCOUNT_EXIST.getId());
                msg.setMsg(ErrorEnum.IO_ACCOUNT_EXIST.getName());
                return msg;
            }
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
           return msg;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
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
        } 
    }
    
    public static JsonNode list_io(Connection con, JsonNode jsonNode){
        if(con == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
        if(jsonNode == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            IOListEntity iolistEntity = objectMapper.treeToValue(jsonNode, IOListEntity.class);
            return list_io(con, iolistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
    }
    
    public static Insertion_Order_List list_io(Connection con, IOListEntity iolistEntity){
        if(con == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        if(iolistEntity == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.list_io);
            pstmt.setInt(1, iolistEntity.getPage_no()*iolistEntity.getPage_size());
            pstmt.setInt(2, iolistEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            Insertion_Order_List iolist = new Insertion_Order_List();
            List<Insertion_Order> ios = new LinkedList<Insertion_Order>();
            while(rset.next()){
                Insertion_Order io = new Insertion_Order();
                populateIO(io, rset, false);
                ios.add(io);
            }
            iolist.setInsertion_order_list(ios);
            Message msg = new Message();
            if(ios.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.IO_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.IO_NOT_FOUND.getName());
            }
            iolist.setMsg(msg);
            return iolist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist;
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
    
    public static JsonNode update_io(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Insertion_Order io = objectMapper.treeToValue(jsonNode, Insertion_Order.class);
            return update_io(con, io,true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_io(Connection con, Insertion_Order io, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(io == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.update_io);
            pstmt.setString(1, io.getName());
            pstmt.setDouble(2, io.getTotal_value());
            pstmt.setInt(3, io.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(4, ts);
            pstmt.setInt(5, io.getStatus().getCode());
            pstmt.setString(6, io.getComment());
            pstmt.setString(7, io.getAccount_guid());
            pstmt.setString(8, io.getOrder_number());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.IO_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.IO_NOT_UPDATED.getName());
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
    
    
    public static JsonNode list_io_by_status(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            IOListEntity iolistEntity = objectMapper.treeToValue(jsonNode, IOListEntity.class);
            return list_io_by_status(con, iolistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
    }
    
    public static Insertion_Order_List list_io_by_status(Connection con, IOListEntity iolistEntity){
        if(con == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        if(iolistEntity == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.list_io_by_status);
            pstmt.setInt(1, iolistEntity.getStatus().getCode());
            pstmt.setInt(2, iolistEntity.getPage_no()*iolistEntity.getPage_size());
            pstmt.setInt(3, iolistEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            Insertion_Order_List iolist = new Insertion_Order_List();
            List<Insertion_Order> ios = new LinkedList<Insertion_Order>();
            while(rset.next()){
                Insertion_Order io = new Insertion_Order();
                populateIO(io, rset, true);
                ios.add(io);
            }
            iolist.setInsertion_order_list(ios);
            Message msg = new Message();
            if(ios.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.IO_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.IO_NOT_FOUND.getName());
            }
            iolist.setMsg(msg);
            return iolist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist;
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
    public static JsonNode list_io_by_account_guid(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            IOListEntity iolistEntity = objectMapper.treeToValue(jsonNode, IOListEntity.class);
            return list_io_by_account_guid(con, iolistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
    }
    
    public static Insertion_Order_List list_io_by_account_guid(Connection con, IOListEntity iolistEntity){
        if(con == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        if(iolistEntity == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.list_io_by_account_guid);
            pstmt.setString(1, iolistEntity.getAccount_guid());
            pstmt.setInt(2, iolistEntity.getPage_no()*iolistEntity.getPage_size());
            pstmt.setInt(3, iolistEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            Insertion_Order_List iolist = new Insertion_Order_List();
            List<Insertion_Order> ios = new LinkedList<Insertion_Order>();
            while(rset.next()){
                Insertion_Order io = new Insertion_Order();
                populateIO(io, rset, false);
                ios.add(io);
            }
            iolist.setInsertion_order_list(ios);
            Message msg = new Message();
            if(ios.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.IO_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.IO_NOT_FOUND.getName());
            }
            iolist.setMsg(msg);
            return iolist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist;
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
    public static JsonNode list_io_by_account_guid_by_status(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            IOListEntity iolistEntity = objectMapper.treeToValue(jsonNode, IOListEntity.class);
            return list_io_by_account_guid_by_status(con, iolistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist.toJson();
        }
    }
    
    public static Insertion_Order_List list_io_by_account_guid_by_status(Connection con, IOListEntity iolistEntity){
        if(con == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        if(iolistEntity == null){
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.IOLIST_ENTITY_NULL.getName());
            iolist.setMsg(msg);
            return iolist;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.list_io_by_account_guid_by_status);
            pstmt.setString(1, iolistEntity.getAccount_guid());
            pstmt.setInt(2, iolistEntity.getStatus().getCode());
            pstmt.setInt(3, iolistEntity.getPage_no()*iolistEntity.getPage_size());
            pstmt.setInt(4, iolistEntity.getPage_size());
            ResultSet rset = pstmt.executeQuery();
            Insertion_Order_List iolist = new Insertion_Order_List();
            List<Insertion_Order> ios = new LinkedList<Insertion_Order>();
            while(rset.next()){
                Insertion_Order io = new Insertion_Order();
                populateIO(io, rset, true);
                ios.add(io);
            }
            iolist.setInsertion_order_list(ios);
            Message msg = new Message();
            if(ios.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.IO_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.IO_NOT_FOUND.getName());
            }
            iolist.setMsg(msg);
            return iolist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Insertion_Order_List iolist = new Insertion_Order_List();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            iolist.setMsg(msg);
            return iolist;
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
    
    public static JsonNode reject_io(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Insertion_Order io = objectMapper.treeToValue(jsonNode, Insertion_Order.class);
            return reject_io(con, io,true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message reject_io(Connection con, Insertion_Order io, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(io == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.IO_NULL.getId());
            msg.setMsg(ErrorEnum.IO_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Insertion_Order.reject_io);
            pstmt.setInt(1, io.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(2, ts);
            pstmt.setInt(3, io.getStatus().getCode());
            pstmt.setString(4, io.getComment());
            pstmt.setString(5, io.getAccount_guid());
            pstmt.setString(6, io.getOrder_number());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.IO_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.IO_NOT_UPDATED.getName());
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
