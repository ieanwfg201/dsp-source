package com.kritter.api.entity.deal;

import com.kritter.constants.PMPAPIEnum;
import com.kritter.constants.PageConstants;
import com.kritter.constants.Payout;
import com.kritter.constants.SiteAPIEnum;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class PMPListEntity {
    /** mandatory @see com.kritter.constants.PageConstants */
    private int page_no = PageConstants.start_index;
    /** mandatory @see com.kritter.constants.PageConstants */
    private int page_size = PageConstants.page_size;
    /** optional guid of publisher account @see com.kritter.api.entity.account.Account  */
    private String deal_guid = null;
    /** optional comma separated list */
    private String id_list = null ;
    /** mandatory com.kritter.constants.PMPAPIEnum - The actions available and accordingly rest of the fields are populated */
    private PMPAPIEnum pmpApiEnum = PMPAPIEnum.none;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result;
        result = prime * result + ((id_list == null) ? 0 : id_list.hashCode());
        result = prime * result + page_no;
        result = prime * result + page_size;
        result = prime * result
                + ((deal_guid == null) ? 0 : deal_guid.hashCode());
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
        PMPListEntity other = (PMPListEntity) obj;
        if (id_list == null) {
            if (other.id_list != null)
                return false;
        } else if (!id_list.equals(other.id_list))
            return false;
        if (page_no != other.page_no)
            return false;
        if (page_size != other.page_size)
            return false;
        if (deal_guid == null) {
            if (other.deal_guid != null)
                return false;
        } else if (!deal_guid.equals(other.deal_guid))
            return false;
        if (pmpApiEnum != other.pmpApiEnum)
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

    public String getId_list() {
        return id_list;
    }
    public void setId_list(String id_list) {
        this.id_list = id_list;
    }

    public String getDeal_guid(){return this.deal_guid;}
    public void setDeal_guid(String deal_guid){this.deal_guid = deal_guid;}

    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static PMPListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        PMPListEntity entity = objectMapper.readValue(str, PMPListEntity.class);
        return entity;
    }
}
