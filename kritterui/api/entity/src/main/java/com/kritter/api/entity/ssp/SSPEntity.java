package com.kritter.api.entity.ssp;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SSPEntity {

    private String dpa = "";
    private double ecpm = 0.0;
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dpa == null) ? 0 : dpa.hashCode());
        long temp;
        temp = Double.doubleToLongBits(ecpm);
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
        SSPEntity other = (SSPEntity) obj;
        if (dpa == null) {
            if (other.dpa != null)
                return false;
        } else if (!dpa.equals(other.dpa))
            return false;
        if (Double.doubleToLongBits(ecpm) != Double
                .doubleToLongBits(other.ecpm))
            return false;
        return true;
    }
    public String getDpa() {
        return dpa;
    }
    public void setDpa(String dpa) {
        this.dpa = dpa;
    }
    public double getEcpm() {
        return ecpm;
    }
    public void setEcpm(double ecpm) {
        this.ecpm = ecpm;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static SSPEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        SSPEntity entity = objectMapper.readValue(str, SSPEntity.class);
        return entity;
    }
}
