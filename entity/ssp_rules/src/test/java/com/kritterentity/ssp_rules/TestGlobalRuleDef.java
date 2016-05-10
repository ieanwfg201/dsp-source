package com.kritterentity.ssp_rules;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.kritter.entity.ssp_rules.Def;
import com.kritter.entity.ssp_rules.SSPGlobalRuleDef;
import com.kritter.entity.ssp_rules.Rule;

public class TestGlobalRuleDef {

    @Test
    public void test() throws Exception{
        SSPGlobalRuleDef gdf = new SSPGlobalRuleDef();
        Rule rule = new Rule();
        Map<String, Rule> demandPartnerMap = new HashMap<String, Rule>();
        demandPartnerMap.put("415", rule);
        Def def = new Def();
        def.setDpaMap(demandPartnerMap);
        Map<Integer, Def> coutryMap = new HashMap<Integer, Def>();
        coutryMap.put(new Integer(465), def);
        gdf.setCountryMap(coutryMap);
              System.out.print(gdf.toJson());
        Assert.assertNull(null);
    }
}
