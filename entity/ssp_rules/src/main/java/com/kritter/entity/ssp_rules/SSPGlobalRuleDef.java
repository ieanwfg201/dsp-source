package com.kritter.entity.ssp_rules;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SSPGlobalRuleDef {
    /**
     * Version
     * */
    private int ver = 1;
    /**
     * countryMap
     */
    private Map<Integer, Def> countryMap = null;
    
    @JsonIgnore
    private int id =-1; 
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((countryMap == null) ? 0 : countryMap.hashCode());
        result = prime * result + ver;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SSPGlobalRuleDef other = (SSPGlobalRuleDef) obj;
        if (countryMap == null) {
            if (other.countryMap != null)
                return false;
        } else if (!countryMap.equals(other.countryMap))
            return false;
        if (ver != other.ver)
            return false;
        return true;
    }
    public int getVer() {
        return ver;
    }
    public void setVer(int ver) {
        this.ver = ver;
    }
    public Map<Integer, Def> getCountryMap() {
        return countryMap;
    }
    public void setCountryMap(Map<Integer, Def> countryMap) {
        this.countryMap = countryMap;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        return toJson(objectMapper);
    }
    public JsonNode toJson(ObjectMapper objectMapper){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static SSPGlobalRuleDef getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static SSPGlobalRuleDef getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        SSPGlobalRuleDef entity = objectMapper.readValue(str, SSPGlobalRuleDef.class);
        return entity;
    }
    
}
