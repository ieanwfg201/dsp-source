package com.kritter.naterial_upload.youku.entity;

import java.io.IOException;
import java.util.List;

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
public class YoukuAdvInfoLocaLMaterialUploadEntity {
	@Getter@Setter
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
	public List<YoukuQualifications> qualifications;

	public YoukuAdvInfoLocaLMaterialUploadEntity(){
	}
	public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static YoukuAdvInfoLocaLMaterialUploadEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static YoukuAdvInfoLocaLMaterialUploadEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	YoukuAdvInfoLocaLMaterialUploadEntity entity = objectMapper.readValue(str, YoukuAdvInfoLocaLMaterialUploadEntity.class);
        return entity;

    }
    public static void main(String args[]) throws Exception{
    	String s="{\"name\":\"亿动广告传媒\",\"brand\":\"广告传媒\",\"address\":\"黄埔区成都北路500号38楼\",\"contacts\":\"亿动广告\",\"tel\":\"13912345678\",\"firstindustry\":36,\"secondindustry\":133,\"qualifications\":[{\"name\":\"亿动广告传媒\",\"url\":\"http://madhouse.ufile.ucloud.com.cn/01525400-0b35-d401-576f-57d66e000002.jpg\",\"md5\":\"bd207f18123dfc05c1381a26c6cb6ad1\",\"operation\":\"add\"}]}";
    	System.out.println(YoukuAdvInfoLocaLMaterialUploadEntity.getObject(s));
    }
}
