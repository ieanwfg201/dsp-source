package com.kritter.naterial_upload.cloudcross.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import java.io.IOException;

@JsonSerialize(include = Inclusion.NON_NULL)
@EqualsAndHashCode
public class CloudCrossQueryEntity {
    @Getter
    @Setter
    public Integer advId;
    @Getter
    @Setter
    public String advName;
    @Getter
    @Setter
    public Integer campaignId;
    @Getter
    @Setter
    public String campaignName;
    @Getter
    @Setter
    public Long campaignStartDate;
    @Getter
    @Setter
    public Long campaignEndDate;
    @Getter
    @Setter
    public Integer campaignStatus;
    @Getter
    @Setter
    public Integer adId;
    @Getter
    @Setter
    public String adName;
    @Getter
    @Setter
    public Integer adStatus;
    @Getter
    @Setter
    public String landing_url;
    @Getter
    @Setter
    public Integer creativeId;
    @Getter
    @Setter
    public String creativeName;
    @Getter
    @Setter
    public Integer creativeStatus;
    @Getter
    @Setter
    public String resource_uri_ids;
    @Getter
    @Setter
    public Integer bannerId;
    @Getter
    @Setter
    public String resource_uri;
    @Getter
    @Setter
    public Integer videoInfoId;
    @Getter
    @Setter
    private Integer width;
    @Getter
    @Setter
    private Integer height;

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static CloudCrossQueryEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper, str);
    }

    public static CloudCrossQueryEntity getObject(ObjectMapper objectMapper, String str) throws JsonParseException, JsonMappingException, IOException {
        CloudCrossQueryEntity entity = objectMapper.readValue(str, CloudCrossQueryEntity.class);
        return entity;

    }
    /*public static void main(String args[]){
        YoukuMaterialUploadEntity h = new YoukuMaterialUploadEntity("sxcd", "wqdwq", "qwdqw", "adsqaw", "wdwe",null);
    	System.out.println(h.toJson());
    	*/
}
