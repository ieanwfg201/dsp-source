package com.kritter.api.entity.metadata;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.PageConstants;


public class MetaInput {
    private String country_id_list = null; /*a comma separated list*/
    private String handset_manufacturer_list = null;/*a comma separated list*/
    private int device_os_id = -1;
    private int device_browser_id = -1;
    
   private String query_id_list = null; /*a comma separated list*/
   private int pageno = PageConstants.start_index;
   private int pagesize = PageConstants.page_size;
   private String country_name = null;
   private String isp_name = null;
   private String isp_ui_name = null;

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((country_id_list == null) ? 0 : country_id_list.hashCode());
        result = prime * result
                + ((country_name == null) ? 0 : country_name.hashCode());
        result = prime * result + device_browser_id;
        result = prime * result + device_os_id;
        result = prime
                * result
                + ((handset_manufacturer_list == null) ? 0
                        : handset_manufacturer_list.hashCode());
        result = prime * result
                + ((isp_name == null) ? 0 : isp_name.hashCode());
        result = prime * result
                + ((isp_ui_name == null) ? 0 : isp_ui_name.hashCode());
        result = prime * result + pageno;
        result = prime * result + pagesize;
        result = prime * result
                + ((query_id_list == null) ? 0 : query_id_list.hashCode());
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
        MetaInput other = (MetaInput) obj;
        if (country_id_list == null) {
            if (other.country_id_list != null)
                return false;
        } else if (!country_id_list.equals(other.country_id_list))
            return false;
        if (country_name == null) {
            if (other.country_name != null)
                return false;
        } else if (!country_name.equals(other.country_name))
            return false;
        if (device_browser_id != other.device_browser_id)
            return false;
        if (device_os_id != other.device_os_id)
            return false;
        if (handset_manufacturer_list == null) {
            if (other.handset_manufacturer_list != null)
                return false;
        } else if (!handset_manufacturer_list
                .equals(other.handset_manufacturer_list))
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
        if (pageno != other.pageno)
            return false;
        if (pagesize != other.pagesize)
            return false;
        if (query_id_list == null) {
            if (other.query_id_list != null)
                return false;
        } else if (!query_id_list.equals(other.query_id_list))
            return false;
        return true;
    }

    public String getCountry_id_list() {
        return country_id_list;
    }

    public void setCountry_id_list(String country_id_list) {
        this.country_id_list = country_id_list;
    }

    public String getHandset_manufacturer_list() {
        return handset_manufacturer_list;
    }

    public void setHandset_manufacturer_list(String handset_manufacturer_list) {
        this.handset_manufacturer_list = handset_manufacturer_list;
    }

    public int getDevice_os_id() {
        return device_os_id;
    }

    public void setDevice_os_id(int device_os_id) {
        this.device_os_id = device_os_id;
    }

    public int getDevice_browser_id() {
        return device_browser_id;
    }

    public void setDevice_browser_id(int device_browser_id) {
        this.device_browser_id = device_browser_id;
    }

    public String getQuery_id_list() {
        return query_id_list;
    }

    public void setQuery_id_list(String query_id_list) {
        this.query_id_list = query_id_list;
    }

    public int getPageno() {
        return pageno;
    }

    public void setPageno(int pageno) {
        this.pageno = pageno;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
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
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static MetaInput getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        MetaInput entity = objectMapper.readValue(str, MetaInput.class);
        return entity;

    }

}
