package com.kritter.api.entity.audience;

import com.kritter.constants.AudienceAPIEnum;
import com.kritter.constants.PageConstants;
import com.kritter.constants.StatusIdEnum;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by zhangyan on 2/3/17.
 */
public class AudienceListEntity {

    /** mandatory @see com.kritter.constants.PageConstants */
    @Getter@Setter
    private int page_no = PageConstants.start_index;
    /** mandatory @see com.kritter.constants.PageConstants */
    @Getter@Setter
    private int page_size = PageConstants.page_size;
    /**optional - depends on campaignQueryEnum-  guid of advertiser account @see com.kritter.api.entity.account.Account*/
    @Getter@Setter
    private String account_guid = null;
    /**optional - depends on campaignQueryEnum-  id of campaign @see com.kritter.api.entity.campaign.Campaign*/
    @Getter@Setter
    private int audience_id = -1;
    /** mandatory @see com.kritter.constants.StatusIdEnum */
    @Getter@Setter
    private StatusIdEnum statusIdEnum = StatusIdEnum.Pending;
    /**optional - depends on campaignQueryEnum-  comma seperated campaign ids*/
    @Getter@Setter
    private String id_list = null;
    /** mandatory - type of action available @see com.kritter.constants.CampaignQueryEnum */
    @Getter@Setter
    private AudienceAPIEnum audienceAPIEnum = AudienceAPIEnum.list_non_expired_audience_of_account_by_status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AudienceListEntity that = (AudienceListEntity) o;

        if (page_no != that.page_no) return false;
        if (page_size != that.page_size) return false;
        if (audience_id != that.audience_id) return false;
        if (account_guid != null ? !account_guid.equals(that.account_guid) : that.account_guid != null) return false;
        if (statusIdEnum != that.statusIdEnum) return false;
        if (id_list != null ? !id_list.equals(that.id_list) : that.id_list != null) return false;
        return audienceAPIEnum == that.audienceAPIEnum;
    }

    @Override
    public int hashCode() {
        int result = page_no;
        result = 31 * result + page_size;
        result = 31 * result + (account_guid != null ? account_guid.hashCode() : 0);
        result = 31 * result + audience_id;
        result = 31 * result + (statusIdEnum != null ? statusIdEnum.hashCode() : 0);
        result = 31 * result + (id_list != null ? id_list.hashCode() : 0);
        result = 31 * result + (audienceAPIEnum != null ? audienceAPIEnum.hashCode() : 0);
        return result;
    }

    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AudienceListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AudienceListEntity entity = objectMapper.readValue(str, AudienceListEntity.class);
        return entity;

    }

}
