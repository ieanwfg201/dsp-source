package com.kritter.api.entity.iddefinition;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.IddefinitionEnum;
import com.kritter.constants.IddefinitionType;

public class IddefinitionInput {
    private String ids = null;
    private IddefinitionEnum iddefinitionEnum = IddefinitionEnum.NONE;
    private IddefinitionType iddefinitionType = IddefinitionType.ID;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((iddefinitionEnum == null) ? 0 : iddefinitionEnum.hashCode());
        result = prime
                * result
                + ((iddefinitionType == null) ? 0 : iddefinitionType.hashCode());
        result = prime * result + ((ids == null) ? 0 : ids.hashCode());
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
        IddefinitionInput other = (IddefinitionInput) obj;
        if (iddefinitionEnum != other.iddefinitionEnum)
            return false;
        if (iddefinitionType != other.iddefinitionType)
            return false;
        if (ids == null) {
            if (other.ids != null)
                return false;
        } else if (!ids.equals(other.ids))
            return false;
        return true;
    }
    public IddefinitionEnum getIddefinitionEnum() {
        return iddefinitionEnum;
    }
    public void setIddefinitionEnum(IddefinitionEnum iddefinitionEnum) {
        this.iddefinitionEnum = iddefinitionEnum;
    }
    public String getIds() {
        return ids;
    }
    public void setIds(String ids) {
        this.ids = ids;
    }
    public IddefinitionType getIddefinitionType() {
        return iddefinitionType;
    }
    public void setIddefinitionType(IddefinitionType iddefinitionType) {
        this.iddefinitionType = iddefinitionType;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static IddefinitionInput getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        IddefinitionInput entity = objectMapper.readValue(str, IddefinitionInput.class);
        return entity;

    }
}
