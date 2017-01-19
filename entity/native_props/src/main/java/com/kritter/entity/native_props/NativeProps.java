package com.kritter.entity.native_props;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;



import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class NativeProps {
    @Getter@Setter
    private int ver = 1;
    @Getter@Setter
    private Integer layout;
    @Getter@Setter
    private String title_keyname;
    @Getter@Setter
    private Integer title_maxchars;
    @Getter@Setter
    private String screenshot_keyname;
    @Getter@Setter
    private Integer screenshot_imagesize;
    /*in case screenshot_imagesize is not found*/
    @Getter@Setter
    private String screenshot_size;
    @Getter@Setter
    private String landingurl_keyname;
    @Getter@Setter
    private String call_to_action_keyname;
    @Getter@Setter
    private String icon_keyname;
    @Getter@Setter
    private Integer icon_imagesize;
    @Getter@Setter
    private String description_keyname;
    @Getter@Setter
    private Integer description_maxchars;
    @Getter@Setter
    private String rating_count_keyname;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static NativeProps getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static NativeProps getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        NativeProps entity = objectMapper.readValue(str, NativeProps.class);
        return entity;

    }

}
