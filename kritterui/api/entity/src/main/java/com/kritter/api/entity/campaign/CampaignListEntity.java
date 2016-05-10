package com.kritter.api.entity.campaign;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.CampaignQueryEnum;
import com.kritter.constants.PageConstants;
import com.kritter.constants.StatusIdEnum;

public class CampaignListEntity {
    /** mandatory @see com.kritter.constants.PageConstants */
    private int page_no = PageConstants.start_index;
    /** mandatory @see com.kritter.constants.PageConstants */
    private int page_size = PageConstants.page_size;
    /**optional - depends on campaignQueryEnum-  guid of advertiser account @see com.kritter.api.entity.account.Account*/
    private String account_guid = null;
    /**optional - depends on campaignQueryEnum-  id of campaign @see com.kritter.api.entity.campaign.Campaign*/
    private int campaign_id = -1;
    /** mandatory @see com.kritter.constants.StatusIdEnum */
    private StatusIdEnum statusIdEnum = StatusIdEnum.Pending;
    /**optional - depends on campaignQueryEnum-  comma seperated campaign ids*/
    private String id_list = null;
    /** mandatory - type of action available @see com.kritter.constants.CampaignQueryEnum */
    private CampaignQueryEnum campaignQueryEnum = CampaignQueryEnum.list_non_expired_campaign_of_account_by_status;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime
                * result
                + ((campaignQueryEnum == null) ? 0 : campaignQueryEnum
                        .hashCode());
        result = prime * result + campaign_id;
        result = prime * result + ((id_list == null) ? 0 : id_list.hashCode());
        result = prime * result + page_no;
        result = prime * result + page_size;
        result = prime * result
                + ((statusIdEnum == null) ? 0 : statusIdEnum.hashCode());
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
        CampaignListEntity other = (CampaignListEntity) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (campaignQueryEnum != other.campaignQueryEnum)
            return false;
        if (campaign_id != other.campaign_id)
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
        if (statusIdEnum != other.statusIdEnum)
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
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public CampaignQueryEnum getCampaignQueryEnum() {
        return campaignQueryEnum;
    }
    public void setCampaignQueryEnum(CampaignQueryEnum campaignQueryEnum) {
        this.campaignQueryEnum = campaignQueryEnum;
    }
    public int getCampaign_id() {
        return campaign_id;
    }
    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }
    public StatusIdEnum getStatusIdEnum() {
        return statusIdEnum;
    }
    public void setStatusIdEnum(StatusIdEnum statusIdEnum) {
        this.statusIdEnum = statusIdEnum;
    }
    public String getId_list() {
        return id_list;
    }
    public void setId_list(String id_list) {
        this.id_list = id_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CampaignListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        CampaignListEntity entity = objectMapper.readValue(str, CampaignListEntity.class);
        return entity;
    }

}
