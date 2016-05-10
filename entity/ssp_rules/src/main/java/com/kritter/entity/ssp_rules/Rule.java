package com.kritter.entity.ssp_rules;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Rule {
    private double percent=0;
    private double ecpm=0;

    public double getPercent() {
        return percent;
    }
    public void setPercent(double percent) {
        this.percent = percent;
    }
    public double getEcpm() {
        return ecpm;
    }
    public void setEcpm(double ecpm) {
        this.ecpm = ecpm;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(ecpm);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(percent);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Rule other = (Rule) obj;
        if (Double.doubleToLongBits(ecpm) != Double
                .doubleToLongBits(other.ecpm))
            return false;
        if (Double.doubleToLongBits(percent) != Double
                .doubleToLongBits(other.percent))
            return false;
        return true;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        return toJson(objectMapper);
    }
    public JsonNode toJson(ObjectMapper objectMapper){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Rule getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static Rule getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        Rule entity = objectMapper.readValue(str, Rule.class);
        return entity;
    }
}
