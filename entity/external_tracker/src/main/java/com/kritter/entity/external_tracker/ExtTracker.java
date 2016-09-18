package com.kritter.entity.external_tracker;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
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
public class ExtTracker {

    @Getter@Setter
    private List<String> impTracker;
    @Getter@Setter
    private Set<Integer> impMacro;
    @Getter@Setter
    private Integer impMacroQuote;
    @Getter@Setter
    private List<String> clickTracker;
    @Getter@Setter
    private Set<Integer> clickMacro;
    @Getter@Setter
    private Integer clickMacroQuote;
    @Getter@Setter
    private Integer clickType=1;
    
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static ExtTracker getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return getObject(objectMapper,str);
    }
    public static ExtTracker getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
        ExtTracker entity = objectMapper.readValue(str, ExtTracker.class);
        objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return entity;

    }

}
