package com.kritter.entity.adxbasedexchanges_metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class MaterialUploadAdvInfo
{

    @Getter@Setter
    private int internalid = -1;
    @Getter@Setter
    private Integer pubIncId;
    @Getter@Setter
    private int adxbasedexhangesstatus=1;
    @Getter@Setter
    private Integer advIncId;
    @Getter@Setter
    private String advName;
    @Getter@Setter
    private String message = "";
    @Getter@Setter
    private String info = "";
    @Getter@Setter
    private long last_modified = 0;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public JsonNode toJson(){

        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static MaterialUploadAdvInfo getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static MaterialUploadAdvInfo getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        MaterialUploadAdvInfo entity = objectMapper.readValue(str, MaterialUploadAdvInfo.class);
        return entity;

    }
}
