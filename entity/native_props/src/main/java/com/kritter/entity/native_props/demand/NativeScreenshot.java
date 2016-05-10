package com.kritter.entity.native_props.demand;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class NativeScreenshot {
    @Getter@Setter
    private int id = -1;
    @Getter@Setter
    private String guid;
    @Getter@Setter
    private String account_guid;
    @Getter@Setter
    private Integer ss_size;
    @Getter@Setter
    private String resource_uri;
    @Getter@Setter
    private int modified_by = 1;
    @Getter@Setter
    private long created_on = 0;
    @Getter@Setter
    private long last_modified = 0;
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

    public static NativeScreenshot getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        NativeScreenshot entity = objectMapper.readValue(str, NativeScreenshot.class);
        return entity;
    }
    public static NativeScreenshot getObjectIgnoreNull(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        NativeScreenshot entity = objectMapper.readValue(str, NativeScreenshot.class);
        return entity;
    }

}
