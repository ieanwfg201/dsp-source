package com.kritter.api.entity.adpositionget;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.adxbasedexchanges_metadata.AdPositionGet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AdpositionGetList {
    /** @see com.kritter.api.entity.response.msg.Message */
	@Getter@Setter
    private Message msg = null;
    /** @see com.kritter.entity.adxbasedexchanges_metadata.AdPositionGet */
	@Getter@Setter
    private List<AdPositionGet> entity_list = null;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AdpositionGetList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static AdpositionGetList getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	AdpositionGetList entity = objectMapper.readValue(str, AdpositionGetList.class);
        return entity;

    }
 
}
