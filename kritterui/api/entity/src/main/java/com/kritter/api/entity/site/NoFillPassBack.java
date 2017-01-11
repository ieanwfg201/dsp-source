package com.kritter.api.entity.site;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class NoFillPassBack {

	@Getter@Setter
	private Integer priority;
	@Getter@Setter
	private String content;
	@Getter@Setter
	private Integer type;
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public JsonNode toJsonIgnoreNull(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static NoFillPassBack getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        NoFillPassBack entity = objectMapper.readValue(str, NoFillPassBack.class);
        return entity;
    }
    public static NoFillPassBack getObjectIgnoreNull(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        NoFillPassBack entity = objectMapper.readValue(str, NoFillPassBack.class);
        return entity;
    }

}
