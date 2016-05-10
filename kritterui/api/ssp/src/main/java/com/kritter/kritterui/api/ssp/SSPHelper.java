package com.kritter.kritterui.api.ssp;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import com.kritter.entity.ssp_rules.Def;
import com.kritter.entity.ssp_rules.Rule;
import com.kritter.entity.ssp_rules.SSPGlobalRuleDef;
import com.kritter.kritterui.api.db_query_def.SSP;

public class SSPHelper {
    

    
    public String createGlobalRulesQueryString(){
        return SSP.list_ssp_global_rules;
    }
    
    public void fillGlobalValues(ObjectMapper mapper, ArrayNode dataNode, ResultSet rset, HashMap<String, String> guidNameMap) throws Exception{
        SSPGlobalRuleDef ruleDef = SSPGlobalRuleDef.getObject(rset.getString("rule_def"));
        Map<Integer, Def> countryMap = ruleDef.getCountryMap();
        for(Integer countryid: countryMap.keySet()){
            Def def = countryMap.get(countryid);
            Map<String,Rule> dpaMap = def.getDpaMap();
            for(String dpaid:dpaMap.keySet()){
                Rule rule = dpaMap.get(dpaid);
                ObjectNode dataObjNode = mapper.createObjectNode();
                dataObjNode.put("demandpartner", guidNameMap.get(dpaid));
                dataObjNode.put("externalEcpmRanking", rule.getEcpm());
                dataNode.add(dataObjNode);
            }
        }
    }
    public ArrayNode createGlobalColumnNode(ObjectMapper mapper){
        ArrayNode columnNode = mapper.createArrayNode();
        ObjectNode demandpartnerObjNode = mapper.createObjectNode();
        demandpartnerObjNode.put("title", "demandpartner");
        demandpartnerObjNode.put("field", "demandpartner");
        demandpartnerObjNode.put("visible", true);
        columnNode.add(demandpartnerObjNode);
        ObjectNode ecpmObjNode = mapper.createObjectNode();
        ecpmObjNode.put("title", "externalEcpmRanking");
        ecpmObjNode.put("field", "externalEcpmRanking");
        ecpmObjNode.put("visible", true);
        columnNode.add(ecpmObjNode);
        return columnNode;
    }
}
