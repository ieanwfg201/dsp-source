package com.kritter.entity.formatter_entity.creative;

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
public class CreativeFormatEntity {
    private static final String CDATA_PREFIX                = "<![CDATA[";
    private static final String CDATA_SUFFIX                = "]]>";

    @Getter@Setter
    private String img; /** For Banner */
    @Getter@Setter
    private String type; /** For Banner|Text */
    @Getter@Setter
    private String alt; /** For Banner */
    @Getter@Setter
    private String csc; /** For Banner|Text */
    @Getter@Setter
    private String curl; /** For Banner|Text */
    @Getter@Setter
    private Short w; /** For Banner */
    @Getter@Setter
    private Short  h; /** For Banner */
    @Getter@Setter
    private String text; /** For Text */
    @Getter@Setter
    private String adm; /** For RichMedia */
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static CreativeFormatEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static CreativeFormatEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        CreativeFormatEntity entity = objectMapper.readValue(str, CreativeFormatEntity.class);
        return entity;
    }
    public String toXml(){
        StringBuffer sb = new StringBuffer("<ad>");
        if(this.type != null){
            sb.append("<type>");
            sb.append(this.type);
            sb.append("</type>");
        }
        if(this.text != null){
            sb.append("<text>");
            sb.append(CDATA_PREFIX);
            sb.append(this.text) ;
            sb.append(CDATA_SUFFIX);
            sb.append("</text>");
        }
        if(this.img != null){
            sb.append("<img>");
            sb.append(CDATA_PREFIX);    
            sb.append(this.img);
            sb.append(CDATA_SUFFIX);
            sb.append("</img>");
        }
        if(this.w != null){
            sb.append("<w>");
            sb.append(this.w);
            sb.append("</w>");
        }
        if(this.h != null){
            sb.append("<h>");
            sb.append(this.h);
            sb.append("</h>");
        }
        if(this.alt != null){
            sb.append("<alt>");
            sb.append(CDATA_PREFIX);
            sb.append(this.alt);
            sb.append(CDATA_SUFFIX);
            sb.append("</alt>");
        }
        if(this.adm != null){
            sb.append("<adm>");
            sb.append(CDATA_PREFIX);
            sb.append(this.adm);
            sb.append(CDATA_SUFFIX);
            sb.append("</adm>");
        }
        if(this.curl != null){
            sb.append("<curl>");
            sb.append(CDATA_PREFIX);
            sb.append(curl);
            sb.append(CDATA_SUFFIX);
            sb.append("</curl>");
        }
        if(this.csc != null){
            sb.append("<csc>");
            sb.append(CDATA_PREFIX);
            sb.append(this.csc);
            sb.append(CDATA_SUFFIX);
            sb.append("</csc>");
        }
        sb.append("</ad>");
        
        return sb.toString();
    }
    public String toXHTML(){
        StringBuffer sb = new StringBuffer("");
        boolean iscurl = false;
        if(this.curl != null){
            sb.append("<a href=\"");
            sb.append(this.curl);
            sb.append("\">");
            iscurl = true;
        }
        if(this.text != null){
            sb.append(this.text);
        }
        if(this.img != null){
            sb.append("<img src=\"");
            sb.append(this.img);
            sb.append("\" alt=\"");
            if(this.alt != null){
                sb.append(this.alt);
            }
            sb.append("\"/>");
        }
        if(iscurl){
            sb.append("</a>");
        }
        if(this.adm != null){
            sb.append(this.adm);
        }
        if(this.csc != null){
            sb.append("<img src=\"");
            sb.append(this.csc);
            sb.append("\" style=\"display: none;\"/>");
        }
        return sb.toString();
    }
}
