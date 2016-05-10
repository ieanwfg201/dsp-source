package com.kritter.api.entity.site;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.PageConstants;
import com.kritter.constants.Payout;
import com.kritter.constants.SiteAPIEnum;

public class SiteListEntity {
    /** mandatory @see com.kritter.constants.PageConstants */
    private int page_no = PageConstants.start_index;
    /** mandatory @see com.kritter.constants.PageConstants */
    private int page_size = PageConstants.page_size;
    /** 
     * only one of status_id or site_int_id will be populated
     * mandatory @see com.kritter.constants.StatusIdEnum */
    private int status_id = -1;
    /** id of site @see com.kritter.api.entity.site.Site */
    private int site_int_id = -1;
    /** optional guid of publisher account @see com.kritter.api.entity.account.Account  */
    private String pub_guid = null;
    /** optional id of publisher account @see com.kritter.api.entity.account.Account  */
    private int pub_id = -1;
    /** optional comma separated list */
    private String id_list = null ;
    /** mandatory com.kritter.constants.SiteAPIEnum - The actions available and accordingly rest of the fields are populated */
    private SiteAPIEnum siteApiEnum = SiteAPIEnum.none;
    /** optional */
    private String billing_rules_json = "{\"payout\":"+Payout.default_payout_percent_str+"}";

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((billing_rules_json == null) ? 0 : billing_rules_json
                        .hashCode());
        result = prime * result + ((id_list == null) ? 0 : id_list.hashCode());
        result = prime * result + page_no;
        result = prime * result + page_size;
        result = prime * result
                + ((pub_guid == null) ? 0 : pub_guid.hashCode());
        result = prime * result + pub_id;
        result = prime * result
                + ((siteApiEnum == null) ? 0 : siteApiEnum.hashCode());
        result = prime * result + site_int_id;
        result = prime * result + status_id;
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
        SiteListEntity other = (SiteListEntity) obj;
        if (billing_rules_json == null) {
            if (other.billing_rules_json != null)
                return false;
        } else if (!billing_rules_json.equals(other.billing_rules_json))
            return false;
        if (id_list == null) {
            if (other.id_list != null)
                return false;
        } else if (!id_list.equals(other.id_list))
            return false;
        if (page_no != other.page_no)
            return false;
        if (page_size != other.page_size)
            return false;
        if (pub_guid == null) {
            if (other.pub_guid != null)
                return false;
        } else if (!pub_guid.equals(other.pub_guid))
            return false;
        if (pub_id != other.pub_id)
            return false;
        if (siteApiEnum != other.siteApiEnum)
            return false;
        if (site_int_id != other.site_int_id)
            return false;
        if (status_id != other.status_id)
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
    public int getStatus_id() {
        return status_id;
    }
    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }
    public int getSite_int_id() {
        return site_int_id;
    }
    public void setSite_int_id(int site_int_id) {
        this.site_int_id = site_int_id;
    }
    public int getPub_id() {
        return pub_id;
    }
    public void setPub_id(int pub_id) {
        this.pub_id = pub_id;
    }
    
    
    public String getPub_guid() {
        return pub_guid;
    }
    public void setPub_guid(String pub_guid) {
        this.pub_guid = pub_guid;
    }
    public String getId_list() {
        return id_list;
    }
    public void setId_list(String id_list) {
        this.id_list = id_list;
    }
    public SiteAPIEnum getSiteApiEnum() {
        return siteApiEnum;
    }
    public void setSiteApiEnum(SiteAPIEnum siteApiEnum) {
        this.siteApiEnum = siteApiEnum;
    }
    public String getBilling_rules_json() {
        return billing_rules_json;
    }
    public void setBilling_rules_json(String billing_rules_json) {
        this.billing_rules_json = billing_rules_json;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static SiteListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        SiteListEntity entity = objectMapper.readValue(str, SiteListEntity.class);
        return entity;
    }
}
