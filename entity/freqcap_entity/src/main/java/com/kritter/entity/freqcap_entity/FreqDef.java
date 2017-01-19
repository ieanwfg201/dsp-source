package com.kritter.entity.freqcap_entity;

import java.io.IOException;

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
public class FreqDef {
	@Getter@Setter
	private FreqDuration duration;
	@Getter@Setter
	private Integer hour;
	@Getter@Setter
	private Integer count;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }
    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static FreqDef getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        return getObject(objectMapper,str);
    }

    public static FreqDef getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	FreqDef entity = objectMapper.readValue(str, FreqDef.class);
        return entity;

    }
}
