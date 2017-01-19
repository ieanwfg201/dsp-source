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
public class MaterialUploadVideo
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
    private Integer campaignId;
    @Getter@Setter
    private String campaignName;
    @Getter@Setter
    private Integer campaignStatus;
    @Getter@Setter
    private Integer adId;
    @Getter@Setter
    private String adName;
    @Getter@Setter
    private Integer adStatus;
    @Getter@Setter
    private Integer creativeId;
    @Getter@Setter
    private String creativeName;
    @Getter@Setter
    private Integer creativeStatus;
    @Getter@Setter
    private Integer videoInfoId;
    @Getter@Setter
    private String message = "";
    @Getter@Setter
    private String info = "";
    @Getter@Setter
    private long last_modified = 0;
    @Getter@Setter
    private String resource_uri_ids;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    public JsonNode toJson(){

        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static MaterialUploadVideo getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static MaterialUploadVideo getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        MaterialUploadVideo entity = objectMapper.readValue(str, MaterialUploadVideo.class);
        return entity;

    }
}
