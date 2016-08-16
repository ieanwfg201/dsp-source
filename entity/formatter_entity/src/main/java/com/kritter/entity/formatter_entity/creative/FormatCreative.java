package com.kritter.entity.formatter_entity.creative;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.constants.CreativeFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class FormatCreative {
    private Logger logger;
    private static final String HTML_LINE_BREAK = "<br>";
    
    @Getter@Setter
    private List<CreativeFormatEntity> creativeArray = new LinkedList<CreativeFormatEntity>();
    
    public FormatCreative(String loggerName){
        this.logger = LoggerFactory.getLogger(loggerName);
    }
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static FormatCreative getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return getObject(objectMapper,str);
    }
    public static FormatCreative getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        FormatCreative entity = objectMapper.readValue(str, FormatCreative.class);
        return entity;
    }
    public void addTextEntity(String text, String clickUrl, String cscBeaconUrl){
        CreativeFormatEntity cfe = new CreativeFormatEntity();
        cfe.setType(CreativeFormat.TEXT.getLabel());
        cfe.setText(text);
        cfe.setCurl(clickUrl);
        cfe.setCsc(cscBeaconUrl);
        this.creativeArray.add(cfe);
    }
    public void addBannerEntity(String img, Short w, Short h, String alt, String curl, String csc, List<String> extImpTracker){
        CreativeFormatEntity cfe = new CreativeFormatEntity();
        cfe.setType(CreativeFormat.BANNER.getLabel());
        cfe.setImg(img);
        if(w != null){
            cfe.setW(w);
        }
        if(h != null){
            cfe.setH(h);
        }
        if(alt != null){
            cfe.setAlt(alt);
        }
        cfe.setCurl(curl);
        cfe.setCsc(csc);
        if(extImpTracker != null){
            cfe.setExtImpTracker(extImpTracker);
        }
        this.creativeArray.add(cfe);
    }
    /*Function to include multiple banner images.*/
    public void addBannerEntity(Map<String,String> images, String alt, String curl, String csc, List<String> extImpTracker){
        CreativeFormatEntity cfe = new CreativeFormatEntity();
        cfe.setType(CreativeFormat.BANNER.getLabel());
        cfe.setImages(images);
        if(alt != null){
            cfe.setAlt(alt);
        }
        cfe.setCurl(curl);
        cfe.setCsc(csc);
        if(extImpTracker != null){
            cfe.setExtImpTracker(extImpTracker);
        }
        this.creativeArray.add(cfe);
    }
    public void addRichMediaEntity(String adm, String cscBeaconUrl){
        CreativeFormatEntity cfe = new CreativeFormatEntity();
        cfe.setType(CreativeFormat.RICHMEDIA.getLabel());
        cfe.setAdm(adm);
        cfe.setCsc(cscBeaconUrl);
        this.creativeArray.add(cfe);
    }

    /************************Below functions are overloaded,used to capture extra information**************************/
    public void addTextEntity(String text, String clickUrl, String cscBeaconUrl,Double ecpmValue){
        CreativeFormatEntity cfe = new CreativeFormatEntity();
        cfe.setType(CreativeFormat.TEXT.getLabel());
        cfe.setText(text);
        cfe.setCurl(clickUrl);
        cfe.setCsc(cscBeaconUrl);
        cfe.setEcpmValue(ecpmValue);
        this.creativeArray.add(cfe);
    }
    public void addBannerEntity(String img, Short w, Short h, String alt, String curl, String csc, List<String> extImpTracker,Double ecpmValue){
        CreativeFormatEntity cfe = new CreativeFormatEntity();
        cfe.setType(CreativeFormat.BANNER.getLabel());
        cfe.setImg(img);
        if(w != null){
            cfe.setW(w);
        }
        if(h != null){
            cfe.setH(h);
        }
        if(alt != null){
            cfe.setAlt(alt);
        }
        cfe.setCurl(curl);
        cfe.setCsc(csc);
        cfe.setEcpmValue(ecpmValue);

        if(extImpTracker != null){
            cfe.setExtImpTracker(extImpTracker);
        }
        this.creativeArray.add(cfe);
    }
    public void addRichMediaEntity(String adm, String cscBeaconUrl,Double ecpmValue){
        CreativeFormatEntity cfe = new CreativeFormatEntity();
        cfe.setType(CreativeFormat.RICHMEDIA.getLabel());
        cfe.setAdm(adm);
        cfe.setCsc(cscBeaconUrl);
        cfe.setEcpmValue(ecpmValue);

        this.creativeArray.add(cfe);
    }
    /******************************************************************************************************************/

    public String writeJson(){
        String str = "[]";
        OutputStream out = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Inclusion.NON_NULL);
        try {
            mapper.writeValue(out, this.creativeArray);
            return out.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return str;
            //e.printStackTrace();
        }finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
//                    /e.printStackTrace();
                }
            }
        }
    }
    public String writeXml(){
        StringBuffer sBuff = new StringBuffer("");
        boolean isFirst = true;
        for(CreativeFormatEntity cfe: this.creativeArray){
            if(isFirst){
                isFirst = false;
            }
            sBuff.append(cfe.toXml());
        }
        return sBuff.toString();
    }
    public String writeXHTML(){
        StringBuffer sBuff = new StringBuffer("");
        boolean isFirst = true;
        for(CreativeFormatEntity cfe: this.creativeArray){
            if(isFirst){
                isFirst = false;
            }else{
                sBuff.append(HTML_LINE_BREAK);
            }
            sBuff.append(cfe.toXHTML());
        }
        return sBuff.toString();
    }
    public static void main (String args[]) throws Exception{
        /*FormatCreative t = new FormatCreative("dnwe");
        System.out.println(t.writeJson());
        CreativeFormatEntity tce = new CreativeFormatEntity();
        t.getCreativeArray().add(tce);
        System.out.println(t.writeJson());
        tce = new CreativeFormatEntity();
        tce.setCsc("skdjqw");
        short i =78;
        tce.setH(i);
        t.getCreativeArray().add(tce);
        System.out.println(t.writeJson());
        */ 
    }
    
}
