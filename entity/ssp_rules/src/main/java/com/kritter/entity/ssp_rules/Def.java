package com.kritter.entity.ssp_rules;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class Def {
    
    /***
     * demand partner map
     */
    private Map<String, Rule> dpaMap = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dpaMap == null) ? 0 : dpaMap.hashCode());
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
        Def other = (Def) obj;
        if (dpaMap == null) {
            if (other.dpaMap != null)
                return false;
        } else if (!dpaMap.equals(other.dpaMap))
            return false;
        return true;
    }

    public Map<String, Rule> getDpaMap() {
        return dpaMap;
    }

    public void setDpaMap(Map<String, Rule> dpaMap) {
        this.dpaMap = dpaMap;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        return toJson(objectMapper);
    }
    public JsonNode toJson(ObjectMapper objectMapper){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Def getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static Def getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        Def entity = objectMapper.readValue(str, Def.class);
        return entity;
    }
}
