package com.kritter.entity.freqcap_entity;

import java.io.IOException;

import lombok.ToString;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.FreqDuration;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
@ToString
public class FreqDef {
	@Getter@Setter
	private FreqDuration duration;
	@Getter@Setter
	private Integer hour;
	@Getter@Setter
	private Integer count;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static FreqDef getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static FreqDef getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	FreqDef entity = objectMapper.readValue(str, FreqDef.class);
        return entity;

    }

}
