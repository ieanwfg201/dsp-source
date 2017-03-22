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
   private String account_guid = null;

    public String getAccount_guid() {
        return account_guid;
    }

    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaInput metaInput = (MetaInput) o;

        if (device_os_id != metaInput.device_os_id) return false;
        if (device_browser_id != metaInput.device_browser_id) return false;
        if (pageno != metaInput.pageno) return false;
        if (pagesize != metaInput.pagesize) return false;
        if (country_id_list != null ? !country_id_list.equals(metaInput.country_id_list) : metaInput.country_id_list != null)
            return false;
        if (handset_manufacturer_list != null ? !handset_manufacturer_list.equals(metaInput.handset_manufacturer_list) : metaInput.handset_manufacturer_list != null)
            return false;
        if (query_id_list != null ? !query_id_list.equals(metaInput.query_id_list) : metaInput.query_id_list != null)
            return false;
        if (country_name != null ? !country_name.equals(metaInput.country_name) : metaInput.country_name != null)
            return false;
        if (isp_name != null ? !isp_name.equals(metaInput.isp_name) : metaInput.isp_name != null) return false;
        if (isp_ui_name != null ? !isp_ui_name.equals(metaInput.isp_ui_name) : metaInput.isp_ui_name != null)
            return false;
        return account_guid != null ? account_guid.equals(metaInput.account_guid) : metaInput.account_guid == null;
    }

    @Override
    public int hashCode() {
        int result = country_id_list != null ? country_id_list.hashCode() : 0;
        result = 31 * result + (handset_manufacturer_list != null ? handset_manufacturer_list.hashCode() : 0);
        result = 31 * result + device_os_id;
        result = 31 * result + device_browser_id;
        result = 31 * result + (query_id_list != null ? query_id_list.hashCode() : 0);
        result = 31 * result + pageno;
        result = 31 * result + pagesize;
        result = 31 * result + (country_name != null ? country_name.hashCode() : 0);
        result = 31 * result + (isp_name != null ? isp_name.hashCode() : 0);
        result = 31 * result + (isp_ui_name != null ? isp_ui_name.hashCode() : 0);
        result = 31 * result + (account_guid != null ? account_guid.hashCode() : 0);
        return result;
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
