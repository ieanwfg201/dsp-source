package com.kritter.entity.native_props.demand;

import java.io.IOException;

import lombok.ToString;
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
@ToString
public class NativeIcon {
    @Getter@Setter
    private int id = -1;
    @Getter@Setter
    private String guid;
    @Getter@Setter
    private String account_guid;
    @Getter@Setter
    private Integer icon_size;
    @Getter@Setter
    private String resource_uri;
    @Getter@Setter
    private int modified_by = 1;
    @Getter@Setter
    private long created_on = 0;
    @Getter@Setter
    private long last_modified = 0;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
    }

    public JsonNode toJson(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public JsonNode toJsonIgnoreNull(){
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }

    public static NativeIcon getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        NativeIcon entity = objectMapper.readValue(str, NativeIcon.class);
        return entity;
    }
    public static NativeIcon getObjectIgnoreNull(String str) throws JsonParseException, JsonMappingException, IOException{
        NativeIcon entity = objectMapper.readValue(str, NativeIcon.class);
        return entity;
    }

}
