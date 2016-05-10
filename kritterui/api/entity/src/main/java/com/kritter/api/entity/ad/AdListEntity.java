package com.kritter.api.entity.ad;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.constants.AdAPIEnum;
import com.kritter.constants.PageConstants;
import com.kritter.constants.StatusIdEnum;

public class AdListEntity {
    /** id of Ad @see com.kritter.api.entity.ad.Ad  - depends on adenum*/
    private int id = -1;
    /** id of Campaign @see com.kritter.api.entity.campaign.Campaign  - depends on adenum*/
    private int campaign_id = -1;
    /** mandatory - actions available - @see com.kritter.constants.AdAPIEnum */
    private AdAPIEnum adenum = AdAPIEnum.get_ad;
    /** mandatory - @see com.kritter.constants.PageConstants */
    private int page_no = PageConstants.start_index;
    /** mandatory - @see com.kritter.constants.PageConstants */
    private int page_size = PageConstants.page_size;
    /** mandatory - @see com.kritter.constants.StatusIdEnum */
    private StatusIdEnum statudIdEnum = StatusIdEnum.Active;
    /** comma separated id of ad Ad - depends on adenum*/
    private String id_list = null;
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adenum == null) ? 0 : adenum.hashCode());
        result = prime * result + campaign_id;
        result = prime * result + id;
        result = prime * result + ((id_list == null) ? 0 : id_list.hashCode());
        result = prime * result + page_no;
        result = prime * result + page_size;
        result = prime * result
                + ((statudIdEnum == null) ? 0 : statudIdEnum.hashCode());
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
        AdListEntity other = (AdListEntity) obj;
        if (adenum != other.adenum)
            return false;
        if (campaign_id != other.campaign_id)
            return false;
        if (id != other.id)
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
        if (statudIdEnum != other.statudIdEnum)
            return false;
        return true;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCampaign_id() {
        return campaign_id;
    }
    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }
    public AdAPIEnum getAdenum() {
        return adenum;
    }
    public void setAdenum(AdAPIEnum adenum) {
        this.adenum = adenum;
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
    public StatusIdEnum getStatudIdEnum() {
        return statudIdEnum;
    }
    public void setStatudIdEnum(StatusIdEnum statudIdEnum) {
        this.statudIdEnum = statudIdEnum;
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
    public static AdListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        AdListEntity entity = objectMapper.readValue(str, AdListEntity.class);
        return entity;

    }
}
