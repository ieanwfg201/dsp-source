package com.kritter.api.entity.ext_site;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.Ext_siteEnum;
import com.kritter.constants.PageConstants;

public class Ext_site_input {
    private int page_no = PageConstants.start_index;
    private int page_size = PageConstants.page_size;
    private String id_list = null ; /*comma separated list*/
    private String osid_list = null ; /*comma separated list*/
    private Ext_siteEnum ext_siteenum = Ext_siteEnum.get_ext_site_by_pub;
    private String str = null;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((ext_siteenum == null) ? 0 : ext_siteenum.hashCode());
        result = prime * result + ((id_list == null) ? 0 : id_list.hashCode());
        result = prime * result
                + ((osid_list == null) ? 0 : osid_list.hashCode());
        result = prime * result + page_no;
        result = prime * result + page_size;
        result = prime * result + ((str == null) ? 0 : str.hashCode());
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
        Ext_site_input other = (Ext_site_input) obj;
        if (ext_siteenum != other.ext_siteenum)
            return false;
        if (id_list == null) {
            if (other.id_list != null)
                return false;
        } else if (!id_list.equals(other.id_list))
            return false;
        if (osid_list == null) {
            if (other.osid_list != null)
                return false;
        } else if (!osid_list.equals(other.osid_list))
            return false;
        if (page_no != other.page_no)
            return false;
        if (page_size != other.page_size)
            return false;
        if (str == null) {
            if (other.str != null)
                return false;
        } else if (!str.equals(other.str))
            return false;
        return true;
    }
    public int getPage_no() {
        return page_no;
    }
    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }
    public int getPage_size() {
        return page_size;
    }
    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }
    public String getId_list() {
        return id_list;
    }
    public void setId_list(String id_list) {
        this.id_list = id_list;
    }
    public Ext_siteEnum getExt_siteenum() {
        return ext_siteenum;
    }
    public void setExt_siteenum(Ext_siteEnum ext_siteenum) {
        this.ext_siteenum = ext_siteenum;
    }
    public String getOsid_list() {
        return osid_list;
    }
    public void setOsid_list(String osid_list) {
        this.osid_list = osid_list;
    }
    public String getStr() {
        return str;
    }
    public void setStr(String str) {
        this.str = str;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Ext_site_input getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Ext_site_input entity = objectMapper.readValue(str, Ext_site_input.class);
        return entity;

    }
}
