package com.kritter.kritterui.api.ssp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.ssp.SSPEntity;
import com.kritter.constants.SSPEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.ssp_rules.Def;
import com.kritter.entity.ssp_rules.Rule;
import com.kritter.entity.ssp_rules.SSPGlobalRuleDef;
import com.kritter.kritterui.api.db_query_def.SSP;

public class SSPCrud {
    private static final Logger LOG = LoggerFactory.getLogger(SSPCrud.class);

    public static JsonNode get_global_data(Connection con){
        if(con == null){
            return null;
        }
        HashMap<String, String> guidNameMap = getApiAdvertisers(con);
        PreparedStatement pstmt = null;
        try{
            SSPHelper sspHelpher = new SSPHelper();
            String queryString = sspHelpher.createGlobalRulesQueryString();
            if(queryString == null){
                return null;
            }
            pstmt = con.prepareStatement(queryString);
            ResultSet rset = pstmt.executeQuery();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            ArrayNode dataNode = null;
            dataNode = mapper.createArrayNode();
            rootNode.put("column", sspHelpher.createGlobalColumnNode(mapper));

            while(rset.next()){
                sspHelpher.fillGlobalValues(mapper, dataNode, rset, guidNameMap);
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
    public static HashMap<String,String> getApiAdvertisers(Connection con){
        HashMap<String, String> guidNameMap = new HashMap<String, String>();
         if(con != null ){
            PreparedStatement pstmt = null;
            try{
                pstmt = con.prepareStatement(SSP.list_api_advertiser);
                ResultSet rset = pstmt.executeQuery();
                while(rset.next()){
                    guidNameMap.put(rset.getString("guid"), rset.getString("name"));
                }
                return guidNameMap;
            }catch(Exception e){
                LOG.error(e.getMessage(),e);
                return guidNameMap;
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
         return guidNameMap;
    }
    public static SSPGlobalRuleDef get_global_rule_def(Connection con){
        if(con != null ){
            PreparedStatement pstmt = null;
            try{
                pstmt = con.prepareStatement(SSP.list_ssp_global_rules);
                ResultSet rset = pstmt.executeQuery();
                while(rset.next()){
                    SSPGlobalRuleDef sspGlobalRuleDef = SSPGlobalRuleDef.getObject(rset.getString("rule_def"));
                    sspGlobalRuleDef.setId(rset.getInt("id"));
                    return sspGlobalRuleDef;
                }
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
        return null;
    }
    public static String create_rule_def(SSPGlobalRuleDef sspGlobalRuleDef, SSPEntity sspEntity){
        if(sspEntity == null && sspGlobalRuleDef == null){
            return null;
        }
        if(sspEntity == null){
            return sspGlobalRuleDef.toJson().toString();
        }
        if(sspGlobalRuleDef == null){
            SSPGlobalRuleDef newDef = new SSPGlobalRuleDef();
            Rule rule = new Rule();
            rule.setEcpm(sspEntity.getEcpm());
            Map<String, Rule> dpaMap = new HashMap<String, Rule>();
            dpaMap.put(sspEntity.getDpa(), rule);
            Def def = new Def();
            def.setDpaMap(dpaMap);
            Map<Integer,Def>  countryMap = new HashMap<Integer, Def>();
            countryMap.put(SSPEnum.COUNTRY_ALL.getCode(), def);
            newDef.setCountryMap(countryMap);
            return newDef.toJson().toString();
        }
        Map<Integer,Def>  countryMap = sspGlobalRuleDef.getCountryMap();
        Def def = countryMap.get(SSPEnum.COUNTRY_ALL.getCode());
        Map<String, Rule> dpaMap = def.getDpaMap();
        if(dpaMap.get(sspEntity.getDpa()) == null){
            Rule rule = new Rule();
            rule.setEcpm(sspEntity.getEcpm());
            dpaMap.put(sspEntity.getDpa(), rule);
        }else{
            Rule rule = dpaMap.get(sspEntity.getDpa());
            rule.setEcpm(sspEntity.getEcpm());
        }
        return sspGlobalRuleDef.toJson().toString(); 
    }
    public static JsonNode insert_update_global_rules(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SSPENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SSPENTITY_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            SSPEntity sspEntity = objectMapper.treeToValue(jsonNode, SSPEntity.class);
            return insert_update_global_rules(con, sspEntity, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message insert_update_global_rules(Connection con, SSPEntity sspEntity , boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(sspEntity == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SSPENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.SSPENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            SSPGlobalRuleDef sspGlobalRuleDef = get_global_rule_def(con);
            if(sspGlobalRuleDef == null || sspGlobalRuleDef.getId() == -1){
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.SSP.insert_ssp_global_rules);
                pstmt.setInt(1, SSPEnum.INSERT_ID.getCode());
                pstmt.setString(2, create_rule_def(sspGlobalRuleDef, sspEntity));
            }else{
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.SSP.update_ssp_global_rules);
                pstmt.setString(1, create_rule_def(sspGlobalRuleDef, sspEntity));
                pstmt.setInt(2, sspGlobalRuleDef.getId());
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.SSP_GLOBAL_RULES_NOT_UPDATED_INSERTED.getId());
                msg.setMsg(ErrorEnum.SSP_GLOBAL_RULES_NOT_UPDATED_INSERTED.getName());
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
