package com.kritter.api.entity.audience;

import com.kritter.constants.AudienceSourceEnum;
import com.kritter.constants.AudienceTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * Created by zhangyan on 2/3/17.
 */
public class Audience {

    @Getter@Setter
    private int id = -1 ;

    @Getter@Setter@Required
    private String name = null;

    @Getter@Setter
    private String source_id  ;

    @Getter@Setter
    private String tags= null;

    @Getter@Setter
    private int type = AudienceTypeEnum.MMA_TAG.getCode();//1:MMA tag, 2:audience package

    @Getter@Setter
    private String file_path = null;

    @Getter@Setter
    private int deleted = 1 ;//0:删除，1：没删除

    @Getter@Setter
    private long created_on = 0;

    @Getter@Setter
    private long last_modified = 0;

    @Getter@Setter
    private String account_guid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Audience audience = (Audience) o;

        if (id != audience.id) return false;
        if (type != audience.type) return false;
        if (deleted != audience.deleted) return false;
        if (created_on != audience.created_on) return false;
        if (last_modified != audience.last_modified) return false;
        if (name != null ? !name.equals(audience.name) : audience.name != null) return false;
        if (source_id != null ? !source_id.equals(audience.source_id) : audience.source_id != null) return false;
        if (tags != null ? !tags.equals(audience.tags) : audience.tags != null) return false;
        if (file_path != null ? !file_path.equals(audience.file_path) : audience.file_path != null) return false;
        return account_guid != null ? account_guid.equals(audience.account_guid) : audience.account_guid == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (source_id != null ? source_id.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (file_path != null ? file_path.hashCode() : 0);
        result = 31 * result + deleted;
        result = 31 * result + (int) (created_on ^ (created_on >>> 32));
        result = 31 * result + (int) (last_modified ^ (last_modified >>> 32));
        result = 31 * result + (account_guid != null ? account_guid.hashCode() : 0);
        return result;
    }

    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static Audience getObject(String str) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Audience entity = objectMapper.readValue(str, Audience.class);
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return entity;
    }

}
