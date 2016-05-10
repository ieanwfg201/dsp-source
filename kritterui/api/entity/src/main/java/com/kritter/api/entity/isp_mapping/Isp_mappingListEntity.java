package com.kritter.api.entity.isp_mapping;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.Isp_mappingEnum;

public class Isp_mappingListEntity {
    private int id = -1;
    private Isp_mappingEnum isp_mappingEnum = Isp_mappingEnum.get_mappings_by_country;
    private String id_list=""; /*Comma separated ids*/
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Isp_mappingEnum getIsp_mappingEnum() {
        return isp_mappingEnum;
    }
    public void setIsp_mappingEnum(Isp_mappingEnum isp_mappingEnum) {
        this.isp_mappingEnum = isp_mappingEnum;
    }
    public String getId_list() {
        return id_list;
    }
    public void setId_list(String id_list) {
        this.id_list = id_list;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((id_list == null) ? 0 : id_list.hashCode());
        result = prime * result
                + ((isp_mappingEnum == null) ? 0 : isp_mappingEnum.hashCode());
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
        Isp_mappingListEntity other = (Isp_mappingListEntity) obj;
        if (id != other.id)
            return false;
        if (id_list == null) {
            if (other.id_list != null)
                return false;
        } else if (!id_list.equals(other.id_list))
            return false;
        if (isp_mappingEnum != other.isp_mappingEnum)
            return false;
        return true;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Isp_mappingListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Isp_mappingListEntity entity = objectMapper.readValue(str, Isp_mappingListEntity.class);
        return entity;

    }
}
