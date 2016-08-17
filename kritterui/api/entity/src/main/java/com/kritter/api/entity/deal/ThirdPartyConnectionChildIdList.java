package com.kritter.api.entity.deal;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.response.msg.Message;

/**
 * Used by user interface.
 */
public class ThirdPartyConnectionChildIdList
{
    @Getter @Setter
    private List<ThirdPartyConnectionChildId> thirdPartyConnectionChildIdList = null;
    @Getter @Setter
    private Message msg = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((thirdPartyConnectionChildIdList == null) ? 0 : thirdPartyConnectionChildIdList.hashCode());
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
        ThirdPartyConnectionChildIdList other = (ThirdPartyConnectionChildIdList) obj;
        if (thirdPartyConnectionChildIdList == null) {
            if (other.thirdPartyConnectionChildIdList != null)
                return false;
        } else if (!thirdPartyConnectionChildIdList.equals(other.thirdPartyConnectionChildIdList))
            return false;
        if (msg == null) {
            if (other.msg != null)
                return false;
        } else if (!msg.equals(other.msg))
            return false;
        return true;
    }

    public JsonNode toJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

}
