package com.kritter.api.entity.isp_mapping;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Isp_mapping {
    private int id = -1;
    private String country_name = "";
    private String data_source_name = "";
    private String isp_name = "";
    private String isp_ui_name = "";
    private boolean is_marked_for_deletion = false;
    private int isp_mapping_id = -1;
    private long modified_on = 0;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((country_name == null) ? 0 : country_name.hashCode());
        result = prime
                * result
                + ((data_source_name == null) ? 0 : data_source_name.hashCode());
        result = prime * result + id;
        result = prime * result + (is_marked_for_deletion ? 1231 : 1237);
        result = prime * result + isp_mapping_id;
        result = prime * result
                + ((isp_name == null) ? 0 : isp_name.hashCode());
        result = prime * result
                + ((isp_ui_name == null) ? 0 : isp_ui_name.hashCode());
        result = prime * result + (int) (modified_on ^ (modified_on >>> 32));
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
        Isp_mapping other = (Isp_mapping) obj;
        if (country_name == null) {
            if (other.country_name != null)
                return false;
        } else if (!country_name.equals(other.country_name))
            return false;
        if (data_source_name == null) {
            if (other.data_source_name != null)
                return false;
        } else if (!data_source_name.equals(other.data_source_name))
            return false;
        if (id != other.id)
            return false;
        if (is_marked_for_deletion != other.is_marked_for_deletion)
            return false;
        if (isp_mapping_id != other.isp_mapping_id)
            return false;
        if (isp_name == null) {
            if (other.isp_name != null)
                return false;
        } else if (!isp_name.equals(other.isp_name))
            return false;
        if (isp_ui_name == null) {
            if (other.isp_ui_name != null)
                return false;
        } else if (!isp_ui_name.equals(other.isp_ui_name))
            return false;
        if (modified_on != other.modified_on)
            return false;
        return true;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCountry_name() {
        return country_name;
    }
    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
    public String getData_source_name() {
        return data_source_name;
    }
    public void setData_source_name(String data_source_name) {
        this.data_source_name = data_source_name;
    }
    public String getIsp_name() {
        return isp_name;
    }
    public void setIsp_name(String isp_name) {
        this.isp_name = isp_name;
    }
    public String getIsp_ui_name() {
        return isp_ui_name;
    }
    public void setIsp_ui_name(String isp_ui_name) {
        this.isp_ui_name = isp_ui_name;
    }
    public boolean isIs_marked_for_deletion() {
        return is_marked_for_deletion;
    }
    public void setIs_marked_for_deletion(boolean is_marked_for_deletion) {
        this.is_marked_for_deletion = is_marked_for_deletion;
    }
    public int getIsp_mapping_id() {
        return isp_mapping_id;
    }
    public void setIsp_mapping_id(int isp_mapping_id) {
        this.isp_mapping_id = isp_mapping_id;
    }
    public long getModified_on() {
        return modified_on;
    }
    public void setModified_on(long modified_on) {
        this.modified_on = modified_on;
    }
    public static Isp_mapping getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Isp_mapping entity = objectMapper.readValue(str, Isp_mapping.class);
        return entity;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
}
