package com.kritter.api.entity.ext_site;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Ext_site {
    private int id = -1;
    private int site_inc_id = -1;
    private int pub_inc_id = -1;
    private String ext_supply_id = null;
    private String ext_supply_name = null;
    private String ext_supply_domain = null;
    private long last_modified = 0;
    private int req = 0;
    private boolean approved = false;
    private boolean unapproved = false;
    private int supply_source_type = 1;
    private String ext_supply_url = "";
    private int osId = 1;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (approved ? 1231 : 1237);
        result = prime
                * result
                + ((ext_supply_domain == null) ? 0 : ext_supply_domain
                        .hashCode());
        result = prime * result
                + ((ext_supply_id == null) ? 0 : ext_supply_id.hashCode());
        result = prime * result
                + ((ext_supply_name == null) ? 0 : ext_supply_name.hashCode());
        result = prime * result
                + ((ext_supply_url == null) ? 0 : ext_supply_url.hashCode());
        result = prime * result + id;
        result = prime * result
                + (int) (last_modified ^ (last_modified >>> 32));
        result = prime * result + pub_inc_id;
        result = prime * result + req;
        result = prime * result + site_inc_id;
        result = prime * result + supply_source_type;
        result = prime * result + (unapproved ? 1231 : 1237);
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
        Ext_site other = (Ext_site) obj;
        if (approved != other.approved)
            return false;
        if (ext_supply_domain == null) {
            if (other.ext_supply_domain != null)
                return false;
        } else if (!ext_supply_domain.equals(other.ext_supply_domain))
            return false;
        if (ext_supply_id == null) {
            if (other.ext_supply_id != null)
                return false;
        } else if (!ext_supply_id.equals(other.ext_supply_id))
            return false;
        if (ext_supply_name == null) {
            if (other.ext_supply_name != null)
                return false;
        } else if (!ext_supply_name.equals(other.ext_supply_name))
            return false;
        if (ext_supply_url == null) {
            if (other.ext_supply_url != null)
                return false;
        } else if (!ext_supply_url.equals(other.ext_supply_url))
            return false;
        if (id != other.id)
            return false;
        if (last_modified != other.last_modified)
            return false;
        if (pub_inc_id != other.pub_inc_id)
            return false;
        if (req != other.req)
            return false;
        if (site_inc_id != other.site_inc_id)
            return false;
        if (supply_source_type != other.supply_source_type)
            return false;
        if (unapproved != other.unapproved)
            return false;
        return true;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getSite_inc_id() {
        return site_inc_id;
    }
    public void setSite_inc_id(int site_inc_id) {
        this.site_inc_id = site_inc_id;
    }
    public String getExt_supply_id() {
        return ext_supply_id;
    }
    public void setExt_supply_id(String ext_supply_id) {
        this.ext_supply_id = ext_supply_id;
    }
    public String getExt_supply_name() {
        return ext_supply_name;
    }
    public void setExt_supply_name(String ext_supply_name) {
        this.ext_supply_name = ext_supply_name;
    }
    public String getExt_supply_domain() {
        return ext_supply_domain;
    }
    public void setExt_supply_domain(String ext_supply_domain) {
        this.ext_supply_domain = ext_supply_domain;
    }
    public long getLast_modified() {
        return last_modified;
    }
    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }
    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public boolean isUnapproved() {
        return unapproved;
    }
    public void setUnapproved(boolean unapproved) {
        this.unapproved = unapproved;
    }
    public int getReq() {
        return req;
    }
    public void setReq(int req) {
        this.req = req;
    }
    public int getSupply_source_type() {
        return supply_source_type;
    }
    public void setSupply_source_type(int supply_source_type) {
        this.supply_source_type = supply_source_type;
    }
    public String getExt_supply_url() {
        return ext_supply_url;
    }
    public void setExt_supply_url(String ext_supply_url) {
        this.ext_supply_url = ext_supply_url;
    }
    public int getPub_inc_id() {
        return pub_inc_id;
    }
    public void setPub_inc_id(int pub_inc_id) {
        this.pub_inc_id = pub_inc_id;
    }
    public int getOsId() {
        return osId;
    }
    public void setOsId(int osId) {
        this.osId = osId;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static Ext_site getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        Ext_site entity = objectMapper.readValue(str, Ext_site.class);
        return entity;
    }
}
