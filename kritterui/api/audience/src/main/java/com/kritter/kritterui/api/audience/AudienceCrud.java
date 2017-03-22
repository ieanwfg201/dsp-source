package com.kritter.kritterui.api.audience;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kritter.api.entity.audience.Audience;
import com.kritter.api.entity.audience.AudienceList;
import com.kritter.api.entity.audience.AudienceListEntity;


import com.kritter.kritterui.api.db_query_def.AudienceSql;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.targeting_profile.TargetingProfileList;
import com.kritter.api.entity.targeting_profile.TargetingProfileListEntity;
import com.kritter.api.entity.targeting_profile.Targeting_profile;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.audience_definition.AudienceTargetingDef;
import com.kritter.entity.targeting_profile.column.Retargeting;
import com.kritter.entity.targeting_profile.column.TPExt;
import com.kritter.kritterui.api.db_query_def.Targeting_Profile;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class AudienceCrud {

    private static final Logger LOG = LoggerFactory.getLogger(AudienceCrud.class);

    private static String new_format_direct_supply_source = "{}";
    private static String new_format_exchange_supply_source = "{}";

    public static AudienceList getAudience(Connection con, AudienceListEntity aulistEntity) {
        if (con == null) {
            AudienceList aulist = new AudienceList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            aulist.setMsg(msg);
            return aulist;
        }
        if (aulistEntity == null) {
            AudienceList aulist = new AudienceList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AUDIENCE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.AUDIENCE_LIST_ENTITY_NULL.getName());
            aulist.setMsg(msg);
            return aulist;
        }
        PreparedStatement pstmt = null;
        try {
            switch (aulistEntity.getAudienceAPIEnum()) {
                case get_audience_by_id_and_deleted:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.get_audience_by_id_and_deleted);
                    pstmt.setInt(1, aulistEntity.getAudience_id());
                    break;
                case get_audience_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.get_audience_of_account);
                    pstmt.setInt(1, aulistEntity.getAudience_id());
                    pstmt.setString(2, aulistEntity.getAccount_guid());
                    break;
                    case get_audience_list_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.get_audience_list_of_account);
                    pstmt.setString(1, aulistEntity.getAccount_guid());
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AudienceList aulist = new AudienceList();
            List<Audience> aus = new LinkedList<Audience>();
            while (rset.next()) {
                Audience au = new Audience();
                au.setId(rset.getInt("id"));
                au.setAccount_guid(rset.getString("account_guid"));
                au.setType(rset.getInt("type"));
                au.setTags(rset.getString("tags"));
                au.setSource_id(rset.getString("source_id"));
                au.setName(rset.getString("name"));
                au.setDeleted(rset.getInt("deleted"));
                aus.add(au);
            }
            aulist.setAudience_list(aus);
            Message msg = new Message();
            if (aus.size() > 0) {
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            } else {
                msg.setError_code(ErrorEnum.AUDIENCE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.AUDIENCE_NOT_FOUND.getName());
            }
            aulist.setMsg(msg);
            return aulist;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            AudienceList aulist = new AudienceList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            aulist.setMsg(msg);
            return aulist;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static AudienceList list_audience(Connection con, AudienceListEntity audiencelistEntity){
        if(con == null){
            AudienceList audiencelist = new AudienceList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            audiencelist.setMsg(msg);
            return audiencelist;
        }
        if(audiencelistEntity == null){
            AudienceList audiencelist = new AudienceList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CAMPAIGNLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CAMPAIGNLIST_ENTITY_NULL.getName());
            audiencelist.setMsg(msg);
            return audiencelist;
        }
        Float defaultAbsolute=null;
        Float defaultPercentage=null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt = null;
        boolean showExpiry=false;
        boolean budgetStatus=false;
        boolean account_id=false;
        try{
//
//            pstmt1=con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.payout_threshold_metadata);
//            ResultSet rset1=pstmt1.executeQuery();
//            while(rset1.next()){
//                String name =rset1.getString("name");
//                if(PayoutThreshold.audience_absolute_payout_threshold.getName().equals(name) ){
//                    defaultAbsolute = rset1.getFloat("value");
//                }else if(PayoutThreshold.audience_percentage_payout_threshold.getName().equals(name) ){
//                    defaultPercentage = rset1.getFloat("value");
//                }
//            }

            switch (audiencelistEntity.getAudienceAPIEnum()){
                case get_audience_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.get_audience_of_account);
                    pstmt.setInt(1, audiencelistEntity.getAudience_id());
                    pstmt.setString(2, audiencelistEntity.getAccount_guid());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case get_audience_by_id_and_deleted:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.get_audience_by_id);
                    pstmt.setInt(1, audiencelistEntity.getAudience_id());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_audience_of_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.list_audience_of_account);
                    pstmt.setString(1, audiencelistEntity.getAccount_guid());
                    pstmt.setInt(2, audiencelistEntity.getPage_no()*audiencelistEntity.getPage_size());
                    pstmt.setInt(3, audiencelistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_audience_of_accounts:
                    if("ALL".equalsIgnoreCase(audiencelistEntity.getId_list())){
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.list_audience_of_all_accounts);
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.AudienceSql.list_audience_of_accounts, "<id>", audiencelistEntity.getId_list(),
                                ",", false));
                    }
                    pstmt.setInt(1, audiencelistEntity.getPage_no()*audiencelistEntity.getPage_size());
                    pstmt.setInt(2, audiencelistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_audience_by_account_ids:
                    if("ALL".equalsIgnoreCase(audiencelistEntity.getId_list())){
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.list_audience_of_all_accounts);
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.AudienceSql.list_audience_by_account_ids, "<id>", audiencelistEntity.getId_list(),
                                ",", false));
                    }
                    pstmt.setInt(1, audiencelistEntity.getPage_no()*audiencelistEntity.getPage_size());
                    pstmt.setInt(2, audiencelistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                case list_audience_by_account_ids_with_account_id:

                    if("ALL".equalsIgnoreCase(audiencelistEntity.getId_list())){
                        pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.list_audience_of_all_accounts);
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                                com.kritter.kritterui.api.db_query_def.AudienceSql.list_audience_by_account_ids_with_account_id, "<id>", audiencelistEntity.getId_list(),
                                ",", false));
                        account_id=true;
                    }
                    pstmt.setInt(1, audiencelistEntity.getPage_no()*audiencelistEntity.getPage_size());
                    pstmt.setInt(2, audiencelistEntity.getPage_size());
                    showExpiry=true;
                    budgetStatus=false;
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            AudienceList audiencelist = new AudienceList();
            List<Audience> audiences = new LinkedList<Audience>();
            while(rset.next()){
                Audience audience = new Audience();
                audience.setAccount_guid(rset.getString("account_guid"));
                audience.setId(rset.getInt("id"));
                audience.setSource_id(rset.getString("source_id"));
                audience.setName(rset.getString("name"));
                audience.setTags(rset.getString("tags"));
                audience.setType(rset.getInt("type"));
                if(audience != null){
                    audiences.add(audience);
                }
            }
            audiencelist.setAudience_list(audiences);
            Message msg = new Message();
            if(audiences.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.AUDIENCE_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.AUDIENCE_NOT_FOUND.getName());
            }
            audiencelist.setMsg(msg);
            return audiencelist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            AudienceList audiencelist = new AudienceList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            audiencelist.setMsg(msg);
            return audiencelist;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
            if(pstmt1 != null){
                try {
                    pstmt1.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }


    public static Message insertAudienceTags(Connection con, Audience audience) {
        if (con == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if (audience == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AUDIENCE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.AUDIENCE_LIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        try {
            //name,source_id,tags,type,created_on,last_modified
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.AudienceSql.insert_audience_tags);
            pstmt.setString(1, audience.getName());
            pstmt.setString(2, audience.getSource_id());
            pstmt.setString(3, audience.getTags());
            pstmt.setInt(4, audience.getType());
            pstmt.setString(5, audience.getAccount_guid());
            int returnCode = pstmt.executeUpdate();
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AUDIENCE_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.AUDIENCE_NOT_INSERTED.getName());
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
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static Message updata_audience_tags(Connection con, Audience audience) {
        if (con == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if (audience == null) {
            Message msg = new Message();
            msg.setError_code(ErrorEnum.AUDIENCE_LIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.AUDIENCE_LIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        try {
            //name,source_id,tags,type,created_on,last_modified
            pstmt = con.prepareStatement(AudienceSql.update_audience);
            pstmt.setString(1, audience.getName());
            pstmt.setString(2, audience.getSource_id());
            pstmt.setString(3, audience.getTags());
            pstmt.setInt(4, audience.getType());
            pstmt.setInt(5, audience.getId());
            pstmt.setString(6, audience.getAccount_guid());
            int returnCode = pstmt.executeUpdate();
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.AUDIENCE_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.AUDIENCE_NOT_UPDATED.getName());
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
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String args[]) {
        Audience tp = new Audience();
        String str = "{\"34\":[\"kwjdbq\",\"akjkqs\"]}";

    }
}
