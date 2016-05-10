package com.kritter.kumbaya.libraries.ext_site_app_merger.entity;

public class SummaryEvent {
    private int siteId = 0;
    private int supply_source_type = 0;
    private String  ext_supply_url = "";
    private String  ext_supply_id = "";
    private String  ext_supply_name = "";
    private String  ext_supply_domain = "";
    private int ext_supply_attr_internal_id = -1;
    private int req = 0;
    private DBActivityEnum dbActivity = DBActivityEnum.INSERT;
    private int id = -1;
    private boolean approved = false;
    private boolean unapproved = false;
    private int osId = 2;
    
    public int getOsId() {
        return osId;
    }
    public void setOsId(int osId) {
        this.osId = osId;
    }
    public int getSiteId() {
        return siteId;
    }
    public void setSiteId(int siteId) {
        this.siteId = siteId;
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
    public int getExt_supply_attr_internal_id() {
        return ext_supply_attr_internal_id;
    }
    public void setExt_supply_attr_internal_id(int ext_supply_attr_internal_id) {
        this.ext_supply_attr_internal_id = ext_supply_attr_internal_id;
    }
    public int getReq() {
        return req;
    }
    public void setReq(int req) {
        this.req = req;
    }
    public DBActivityEnum getDbActivity() {
        return dbActivity;
    }
    public void setDbActivity(DBActivityEnum dbActivity) {
        this.dbActivity = dbActivity;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
    
}
