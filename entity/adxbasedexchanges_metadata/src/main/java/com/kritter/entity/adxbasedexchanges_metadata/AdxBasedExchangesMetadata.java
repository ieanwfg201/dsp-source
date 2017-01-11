package com.kritter.entity.adxbasedexchanges_metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AdxBasedExchangesMetadata
{

    @Getter@Setter
    private int internalid = -1;
    @Getter@Setter
    private Integer pubIncId;
    @Getter@Setter
    private boolean advertiser_upload=false;
    @Getter@Setter
    private boolean adposition_get=false;
    @Getter@Setter
    private boolean banner_upload=false;
    @Getter@Setter
    private boolean video_upload=false;
    @Getter@Setter
    private long last_modified = 0;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AdxBasedExchangesMetadata getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static AdxBasedExchangesMetadata getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        AdxBasedExchangesMetadata entity = objectMapper.readValue(str, AdxBasedExchangesMetadata.class);
        return entity;

    }
}
