package com.kritter.api.entity.audience;

import com.kritter.api.entity.campaign.Campaign;
import com.kritter.api.entity.campaign.CampaignList;
import com.kritter.api.entity.response.msg.Message;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhangyan on 2/3/17.
 */
public class AudienceList {
    /** @see com.kritter.api.entity.response.msg.Message */
    private Message msg = null;
    /** @see com.kritter.api.entity.campaign.Campaign */
    private List<Audience> audience_list = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((audience_list == null) ? 0 : audience_list.hashCode());
        result = prime * result + ((msg == null) ? 0 : msg.hashCode());
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
        AudienceList other = (AudienceList) obj;
        if (audience_list == null) {
            if (other.audience_list != null)
                return false;
        } else if (!audience_list.equals(other.audience_list))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }
    public Message getMsg() {
        return msg;
    }
    public void setMsg(Message msg) {
        this.msg = msg;
    }
    public List<Audience> getAudience_list() {
        return audience_list;
    }
    public void setAudience_list(List<Audience> audience_list) {
        this.audience_list = audience_list;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CampaignList getObject(String str) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CampaignList entity = objectMapper.readValue(str, CampaignList.class);
        return entity;
    }
}
