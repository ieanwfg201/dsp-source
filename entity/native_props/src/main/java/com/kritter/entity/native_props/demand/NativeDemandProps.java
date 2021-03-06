package com.kritter.entity.native_props.demand;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class NativeDemandProps {
    @Getter@Setter
    private List<Integer> icons;
    @Getter@Setter
    private List<Integer> screenshots;
    @Getter@Setter
    private String title;
    @Getter@Setter
    private String desc;
    @Getter@Setter
    private String cta;
    @Getter@Setter
    private Boolean rating;
    @Getter@Setter
    private String download_count;
    @Getter@Setter
    private String active_players;
    @Getter@Setter
    private Integer titleId;
    @Getter@Setter
    private Integer descId;
    @Getter@Setter
    private Integer iconId;
    @Getter@Setter
    private Integer screenshotId;
    @JsonIgnore
    public String getIconsStr(){
        if(this.icons != null){
            if(this.icons.size()>0){
                StringBuffer sBuff = new StringBuffer("[");
                boolean isFirst = true;
                for(Integer i:this.icons){
                    if(isFirst){
                        isFirst = false;
                    }else{
                        sBuff.append(",");
                    }
                    sBuff.append(i);
                }
                sBuff.append("]");
                return sBuff.toString();
            }
        }
        return "[]";
    }
    @JsonIgnore
    public String getScreenshotStr(){
        if(this.screenshots != null){
            if(this.screenshots.size()>0){
                StringBuffer sBuff = new StringBuffer("[");
                boolean isFirst = true;
                for(Integer i:this.screenshots){
                    if(isFirst){
                        isFirst = false;
                    }else{
                        sBuff.append(",");
                    }
                    sBuff.append(i);
                }
                sBuff.append("]");
                return sBuff.toString();
            }
        }
        return "[]";
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }
    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static NativeDemandProps getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }
    public static NativeDemandProps getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        NativeDemandProps entity = objectMapper.readValue(str, NativeDemandProps.class);
        return entity;
    }
}
