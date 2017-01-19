package com.kritter.kritterui.api.payout_threshold;

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
import com.kritter.api.entity.payoutthreshold.PayoutThresholdList;
import com.kritter.api.entity.payoutthreshold.PayoutThresholdListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.payout_threshold.CampaignPayoutThreshold;
import com.kritter.entity.payout_threshold.DefaultPayoutThreshold;
import com.kritter.kritterui.api.db_query_def.PayoutThresholdDef;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class PayoutThresholdCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(PayoutThresholdCrud.class);
    
    public static DefaultPayoutThreshold populateDefault( ResultSet rset) throws SQLException{
    	DefaultPayoutThreshold entity = null;
        if(rset != null){
        	entity = new DefaultPayoutThreshold();
        	entity.setName(rset.getString("name"));
        	entity.setValue(rset.getFloat("value"));
        	entity.setLast_modified(rset.getTimestamp("last_modified").getTime());
        }
        return entity;
    }
    public static CampaignPayoutThreshold populateCampaignThreshold( ResultSet rset) throws SQLException{
    	CampaignPayoutThreshold entity = null;
        if(rset != null){
        	entity = new CampaignPayoutThreshold();
        	entity.setCampaign_id(rset.getInt("campaign_id"));
        	entity.setAbsolute_threshold(rset.getFloat("absolute_threshold"));
        	entity.setPercentage_threshold(rset.getFloat("percentage_threshold"));
        	entity.setLast_modified(rset.getTimestamp("last_modified").getTime());
        }
        return entity;
    }

    
    public static JsonNode insert_campaign_payout_threshold(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CampaignPayoutThreshold entity = objectMapper.treeToValue(jsonNode, CampaignPayoutThreshold.class);
            return insert_campaign_payout_threshold(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_campaign_payout_threshold(Connection con, CampaignPayoutThreshold entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            return msg;
        }
        if(entity.getAbsolute_threshold()==-1 || entity.getPercentage_threshold()==-1){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NOT_INSERTED.getId());
            msg.setMsg(ErrorEnum.PT_NOT_INSERTED.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.PayoutThresholdDef.insert_campaign_payout_threshold);
            pstmt.setInt(1, entity.getCampaign_id());
            pstmt.setFloat(2, entity.getAbsolute_threshold());
            pstmt.setFloat(3, entity.getPercentage_threshold());
            pstmt.setTimestamp(4, new Timestamp(new Date().getTime()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.PT_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.PT_NOT_INSERTED.getName());
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
    
    
    public static JsonNode update_campaign_payout_threshold(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CampaignPayoutThreshold entity = objectMapper.treeToValue(jsonNode, CampaignPayoutThreshold.class);
            return update_campaign_payout_threshold(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_campaign_payout_threshold(Connection con, CampaignPayoutThreshold entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            return msg;
        }
        if(entity.getAbsolute_threshold()==-1 || entity.getPercentage_threshold()==-1){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NOT_UPDATED.getId());
            msg.setMsg(ErrorEnum.PT_NOT_UPDATED.getName());
            return msg;
        }

        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.PayoutThresholdDef.update_campaign_payout_threshold);
            pstmt.setFloat(1, entity.getAbsolute_threshold());
            pstmt.setFloat(2, entity.getPercentage_threshold());
            pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
            pstmt.setInt(4, entity.getCampaign_id());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.PT_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.PT_NOT_UPDATED.getName());
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
    public static JsonNode update_default_payout_threshold(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            DefaultPayoutThreshold entity = objectMapper.treeToValue(jsonNode, DefaultPayoutThreshold.class);
            return update_default_payout_threshold(con, entity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_default_payout_threshold(Connection con, DefaultPayoutThreshold entity, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(entity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.PayoutThresholdDef.update_default_payout_threshold);
            pstmt.setFloat(1, entity.getValue());
            pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmt.setString(3, entity.getName());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.PT_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.PT_NOT_UPDATED.getName());
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
    
    public static JsonNode various_get_payout_threshold(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
        	PayoutThresholdList returnEntity = new PayoutThresholdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            PayoutThresholdListEntity entity = objectMapper.treeToValue(jsonNode, PayoutThresholdListEntity.class);
            return various_get_payout_threshold(con, entity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            PayoutThresholdList returnEntity = new PayoutThresholdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            returnEntity.setMsg(msg);
            return returnEntity.toJson();
        }
    }
    public static PayoutThresholdList various_get_payout_threshold(Connection con, PayoutThresholdListEntity entity){
        if(con == null){
        	PayoutThresholdList returnEntity = new PayoutThresholdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        if(entity == null){
        	PayoutThresholdList returnEntity = new PayoutThresholdList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.PT_NULL.getId());
            msg.setMsg(ErrorEnum.PT_NULL.getName());
            returnEntity.setMsg(msg);
            return returnEntity;
        }
        PreparedStatement pstmt = null;
        try{
        	boolean defaultResult = true;
            switch (entity.getQueryEnum()){
                case default_payout_threshold:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.PayoutThresholdDef.select_default_payout_threshold);
                    break;
                case campaign_payout_threshold:
                    if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list())
                    		|| "[all]".equalsIgnoreCase(entity.getId_list()) || "all".equalsIgnoreCase(entity.getId_list())){
                    	PayoutThresholdList adlist = new PayoutThresholdList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.NO_ERROR.getId());
                        msg.setMsg(ErrorEnum.NO_ERROR.getName());
                        adlist.setMsg(msg);
                        return adlist;
                    }else{
                        pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.PayoutThresholdDef.select_campaign_payout_threshold, "<id>", entity.getId_list(), 
                            ",", false));
                        defaultResult=false;
                    }
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            PayoutThresholdList returnEntity = new PayoutThresholdList();
            List<DefaultPayoutThreshold> defaultEntities = new LinkedList<DefaultPayoutThreshold>();
            List<CampaignPayoutThreshold> campaignEntities = new LinkedList<CampaignPayoutThreshold>();
            while(rset.next()){
            	if(defaultResult){
            		DefaultPayoutThreshold defaultEntity = populateDefault(rset);
            		if(defaultEntity != null){
            			defaultEntities.add(defaultEntity);
            		}
            	}else{
            		CampaignPayoutThreshold campaignEntity = populateCampaignThreshold(rset);
            		if(campaignEntity != null){
            			campaignEntities.add(campaignEntity);
            		}
            		
            	}
            }
            if(defaultResult){
            	returnEntity.setDefault_entity_list(defaultEntities);;
            }else{
            	returnEntity.setCampaign_entity_list(campaignEntities);;
            }
            Message msg = new Message();
            if(defaultEntities.size()>0 || campaignEntities.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.PT_NF.getId());
                msg.setMsg(ErrorEnum.PT_NF.getName());
            }
            returnEntity.setMsg(msg);
            return returnEntity;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            PayoutThresholdList returnEntity = new PayoutThresholdList();
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
    public static JsonNode get_default_payout_data(Connection con){
        if(con == null){
            return null;
        }
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(PayoutThresholdDef.select_default_payout_threshold);
            ResultSet rset = pstmt.executeQuery();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode dataNode = null;
            dataNode = mapper.createArrayNode();
            rootNode.put("column", PayoutCrudHelper.createGlobalColumnNode(mapper));

            while(rset.next()){
            	PayoutCrudHelper.fillGlobalValues(mapper, dataNode, rset);
            }
            rootNode.put("data", dataNode);
            return rootNode;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
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
