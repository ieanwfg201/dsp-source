package com.kritter.naterial_upload.valuemaker.entity;

/**
 * Created by zhangyan on 16-9-29.
 */

   import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class VamMaterialUploadEntity {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String landingpage ;
    @Getter
    @Setter
    private Integer width;//width 必须 Integer 创意宽度
    @Getter
    @Setter
    private Integer height;//height height 必须 Integer 创意高度
    @Getter
    @Setter
    private Integer format;
    @Getter
    @Setter
    private Integer adtype;
    @Getter
    @Setter
    private Integer category;
    @Getter
    @Setter
    private String[] adomain_list;
    @Getter
    @Setter
    private String[] pic_urls;
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String text;

    public VamMaterialUploadEntity() {
    }

    public VamMaterialUploadEntity(String id, String landingpage, Integer width, Integer height, Integer format, Integer adtype,
                                   Integer category, String[] adomain_list, String[] pic_urls, String title, String text) {
        super();
        this.id=id;
        this.landingpage=landingpage;
        this.width=width;
        this.height=height;
        this.format=format;
        this.adtype=adtype;
        this.category=category;
        if(adomain_list == null){
            this.adomain_list= new String[0];
        }else{
            this.adomain_list = adomain_list;
        }
        if(pic_urls == null){
            this.pic_urls= new String[0];
        }else{
            this.pic_urls = pic_urls;
        }
        this.title=title;
        this.text=text;
    }
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static VamMaterialUploadEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static VamMaterialUploadEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        VamMaterialUploadEntity entity = objectMapper.readValue(str, VamMaterialUploadEntity.class);
        return entity;

    }
}
