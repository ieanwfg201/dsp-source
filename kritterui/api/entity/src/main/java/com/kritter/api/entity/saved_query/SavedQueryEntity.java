package com.kritter.api.entity.saved_query;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.ReportingTypeEnum;
import com.kritter.constants.StatusIdEnum;

public class SavedQueryEntity {
    private int id = -1;
    private String name = null;
    private ReportingEntity reporting_entity = null;
    private StatusIdEnum status_id = StatusIdEnum.Active;
    private String account_guid = null;
    private long created_on = 0;
    private long modified_on = 0;
    private ReportingTypeEnum reportingTypeEnum = ReportingTypeEnum.SUPPLY;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((account_guid == null) ? 0 : account_guid.hashCode());
        result = prime * result + (int) (created_on ^ (created_on >>> 32));
        result = prime * result + id;
        result = prime * result + (int) (modified_on ^ (modified_on >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime
                * result
                + ((reportingTypeEnum == null) ? 0 : reportingTypeEnum
                        .hashCode());
        result = prime
                * result
                + ((reporting_entity == null) ? 0 : reporting_entity.hashCode());
        result = prime * result
                + ((status_id == null) ? 0 : status_id.hashCode());
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
        SavedQueryEntity other = (SavedQueryEntity) obj;
        if (account_guid == null) {
            if (other.account_guid != null)
                return false;
        } else if (!account_guid.equals(other.account_guid))
            return false;
        if (created_on != other.created_on)
            return false;
        if (id != other.id)
            return false;
        if (modified_on != other.modified_on)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (reportingTypeEnum != other.reportingTypeEnum)
            return false;
        if (reporting_entity == null) {
            if (other.reporting_entity != null)
                return false;
        } else if (!reporting_entity.equals(other.reporting_entity))
            return false;
        if (status_id != other.status_id)
            return false;
        return true;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public StatusIdEnum getStatus_id() {
        return status_id;
    }
    public void setStatus_id(StatusIdEnum status_id) {
        this.status_id = status_id;
    }
    public String getAccount_guid() {
        return account_guid;
    }
    public void setAccount_guid(String account_guid) {
        this.account_guid = account_guid;
    }
    public long getCreated_on() {
        return created_on;
    }
    public void setCreated_on(long created_on) {
        this.created_on = created_on;
    }
    public long getModified_on() {
        return modified_on;
    }
    public void setModified_on(long modified_on) {
        this.modified_on = modified_on;
    }
    public ReportingTypeEnum getReportingTypeEnum() {
        return reportingTypeEnum;
    }
    public void setReportingTypeEnum(ReportingTypeEnum reportingTypeEnum) {
        this.reportingTypeEnum = reportingTypeEnum;
    }
    public ReportingEntity getReporting_entity() {
        return reporting_entity;
    }
    public void setReporting_entity(ReportingEntity reporting_entity) {
        this.reporting_entity = reporting_entity;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static SavedQueryEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        SavedQueryEntity entity = objectMapper.readValue(str, SavedQueryEntity.class);
        return entity;
    }
}
