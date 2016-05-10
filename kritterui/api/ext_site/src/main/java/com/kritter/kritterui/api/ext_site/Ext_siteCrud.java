package com.kritter.kritterui.api.ext_site;

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

import com.kritter.api.entity.ext_site.Ext_site;
import com.kritter.api.entity.ext_site.Ext_site_input;
import com.kritter.api.entity.ext_site.Ext_site_list;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class Ext_siteCrud {
    private static final Logger LOG = LoggerFactory.getLogger(Ext_siteCrud.class);
    public static void populate(Ext_site ext_site, ResultSet rset, boolean pub_inc) throws SQLException{
        if(ext_site != null && rset != null){
            ext_site.setId(rset.getInt("id"));
            ext_site.setSite_inc_id(rset.getInt("site_inc_id"));
            ext_site.setExt_supply_domain(rset.getString("ext_supply_domain"));
            ext_site.setExt_supply_id(rset.getString("ext_supply_id"));
            ext_site.setExt_supply_name(rset.getString("ext_supply_name"));
            ext_site.setLast_modified(rset.getTimestamp("last_modified").getTime());
            ext_site.setApproved(rset.getBoolean("approved"));
            ext_site.setUnapproved(rset.getBoolean("unapproved"));
            ext_site.setReq(rset.getInt("req"));
            ext_site.setSupply_source_type(rset.getInt("supply_source_type"));
            ext_site.setExt_supply_url(rset.getString("ext_supply_url"));
            ext_site.setOsId(rset.getInt("osId"));
            if(pub_inc){
                ext_site.setPub_inc_id(rset.getInt("pub_inc_id"));
            }
            
        }
    }
    
    public static JsonNode various_get_ext_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Ext_site_list ext_sitelist = new Ext_site_list();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
            ext_sitelist.setMsg(msg);
            return ext_sitelist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ext_site_input ext_site_input = objectMapper.treeToValue(jsonNode, Ext_site_input.class);
            return various_get_ext_site(con, ext_site_input).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Ext_site_list ext_sitelist = new Ext_site_list();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            ext_sitelist.setMsg(msg);
            return ext_sitelist.toJson();
        }
    }
    
    public static Ext_site_list various_get_ext_site(Connection con, Ext_site_input ext_site_input){
        if(con == null){
            Ext_site_list ext_sitelist = new Ext_site_list();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            ext_sitelist.setMsg(msg);
            return ext_sitelist;
        }
        if(ext_site_input == null){
            Ext_site_list ext_sitelist = new Ext_site_list();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
            ext_sitelist.setMsg(msg);
            return ext_sitelist;
        }
        boolean pub_inc = false;
        PreparedStatement pstmt = null;
        try{
            switch (ext_site_input.getExt_siteenum()){
                
                case get_unapproved_ext_site:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ext_site.get_unapproved_ext_site);
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    break;
                case get_approved_ext_site:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ext_site.get_approved_ext_site);
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    break;
                case get_rejected_ext_site:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Ext_site.get_rejected_ext_site);
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    break;
                case get_ext_site_by_pub:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_ext_site_by_pub, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    pub_inc=true;
                    break;
                case get_unapproved_ext_site_by_pub:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_unapproved_ext_site_by_pub, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    pub_inc=true;
                    break;
                case get_approved_ext_site_by_pub:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_approved_ext_site_by_pub, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    pub_inc=true;
                    break;
                case get_rejected_ext_site_by_pub:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_rejected_ext_site_by_pub, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    pub_inc=true;
                    break;
                case get_ext_site_by_site:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_ext_site_by_site, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(2, ext_site_input.getPage_size());
                    break;
                case get_unapproved_ext_site_by_pub_os:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_unapproved_ext_site_by_pub_os, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, Integer.parseInt(ext_site_input.getOsid_list()));
                    pstmt.setInt(2, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(3, ext_site_input.getPage_size());
                    pub_inc=true;
                    break;
                case get_approved_ext_site_by_pub_os:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_approved_ext_site_by_pub_os, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, Integer.parseInt(ext_site_input.getOsid_list()));
                    pstmt.setInt(2, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(3, ext_site_input.getPage_size());
                    pub_inc=true;
                    break;
                case get_rejected_ext_site_by_pub_os:
                    if(ext_site_input.getId_list() == null || "ALL".equalsIgnoreCase(ext_site_input.getId_list()) 
                    || "None".equalsIgnoreCase(ext_site_input.getId_list())){
                        Ext_site_list ext_sitelist = new Ext_site_list();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
                        msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
                        ext_sitelist.setMsg(msg);
                        return ext_sitelist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.Ext_site.get_rejected_ext_site_by_pub_os, "<id>", ext_site_input.getId_list(), 
                            ",", false));
                    pstmt.setInt(1, Integer.parseInt(ext_site_input.getOsid_list()));
                    pstmt.setInt(2, ext_site_input.getPage_no() * ext_site_input.getPage_size());
                    pstmt.setInt(3, ext_site_input.getPage_size());
                    pub_inc=true;
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            Ext_site_list ext_site_list = new Ext_site_list();
            List<Ext_site> ext_sites = new LinkedList<Ext_site>();
            while(rset.next()){
                Ext_site ext_site = new Ext_site();
                populate(ext_site, rset, pub_inc);
                ext_sites.add(ext_site);
            }
            ext_site_list.setExt_site_list(ext_sites);
            Message msg = new Message();
            if(ext_sites.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.EXT_SITE_NF.getId());
                msg.setMsg(ErrorEnum.EXT_SITE_NF.getName());
            }
            ext_site_list.setMsg(msg);
            return ext_site_list;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Ext_site_list ext_site_list = new Ext_site_list();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            ext_site_list.setMsg(msg);
            return ext_site_list;
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
    
    public static JsonNode update_ext_site(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Ext_site_input ext_site_input = objectMapper.treeToValue(jsonNode, Ext_site_input.class);
            return update_ext_site(con, ext_site_input, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message update_ext_site(Connection con, Ext_site_input ext_site_input, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(ext_site_input == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.EXT_SITE_INPUT_NULL.getId());
            msg.setMsg(ErrorEnum.EXT_SITE_INPUT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            switch(ext_site_input.getExt_siteenum()){
            case aprrove_ext_site_by_ids:
                String str = ext_site_input.getId_list(); 
                if(str == null || "ALL".equalsIgnoreCase(str) || "".equalsIgnoreCase(str) || "none".equalsIgnoreCase(str)){
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.EXT_SITE_ID_NP.getId());
                    msg.setMsg(ErrorEnum.EXT_SITE_ID_NP.getName());
                    return msg;
                }else{
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                        com.kritter.kritterui.api.db_query_def.Ext_site.aprrove_ext_site_by_ids, "<id>", str, 
                        ",", false));
                }
                pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                break;
            case unaprrove_ext_site_by_ids:
                String str1 = ext_site_input.getId_list(); 
                if(str1 == null || "ALL".equalsIgnoreCase(str1) || "".equalsIgnoreCase(str1) || "none".equalsIgnoreCase(str1)){
                    Message msg = new Message();
                    msg.setError_code(ErrorEnum.EXT_SITE_ID_NP.getId());
                    msg.setMsg(ErrorEnum.EXT_SITE_ID_NP.getName());
                    return msg;
                }else{
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                        com.kritter.kritterui.api.db_query_def.Ext_site.unaprrove_ext_site_by_ids, "<id>", str1, 
                        ",", false));
                }
                pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
                break;
            default:
                break;
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.EXT_SITE_NO_ROWS_TO_UPDATE.getId());
                msg.setMsg(ErrorEnum.EXT_SITE_NO_ROWS_TO_UPDATE.getName());
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
