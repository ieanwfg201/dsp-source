package com.kritter.naterial_upload.cloudcross.advertiser;

import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertiserEntity;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.util.List;

/**
 * Created by hamlin on 16-9-29.
 */
public class CloudCrossAdvInfoLocaLMaterialUploadEntity {
    @Getter
    @Setter
    public String name;
    @Getter@Setter
    public String brand;
    @Getter@Setter
    public String address;
    @Getter@Setter
    public String contacts;
    @Getter@Setter
    public String tel;
    @Getter@Setter
    public Integer firstindustry;
    @Getter@Setter
    public Integer secondindustry;
    @Getter@Setter
    public List<CloudCrossAdvertiserEntity> qualifications;

    public CloudCrossAdvInfoLocaLMaterialUploadEntity(){
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CloudCrossAdvInfoLocaLMaterialUploadEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static CloudCrossAdvInfoLocaLMaterialUploadEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        CloudCrossAdvInfoLocaLMaterialUploadEntity entity = objectMapper.readValue(str, CloudCrossAdvInfoLocaLMaterialUploadEntity.class);
        return entity;

    }
    /*public static void main(String args[]){
    	CloudCrossMaterialUploadEntity h = new CloudCrossMaterialUploadEntity("sxcd", "wqdwq", "qwdqw", "adsqaw", "wdwe",null);
    	System.out.println(h.toJson());
    	*/

}
