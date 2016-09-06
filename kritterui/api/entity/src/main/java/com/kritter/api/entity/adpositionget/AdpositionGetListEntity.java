package com.kritter.api.entity.adpositionget;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.AdpositionGetQueryEnum;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.PageConstants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AdpositionGetListEntity {
    /** mandatory @see com.kritter.constants.PageConstants */
	@Getter@Setter
    private int page_no = PageConstants.start_index;
    /** mandatory @see com.kritter.constants.PageConstants */
	@Getter@Setter
    private int page_size = PageConstants.page_size;
    /**optional - depends on queryEnum-  comma seperated ids*/
	@Getter@Setter
    private String id_list = null;
	@Getter@Setter
    /** @see com.kritter.constants.AdpositionGetQueryEnum  mandatory - type of action available */
    private AdpositionGetQueryEnum queryEnum = AdpositionGetQueryEnum.list_adposition_get;
	@Getter@Setter
    private AdxBasedExchangesStates adxstate = AdxBasedExchangesStates.BRINGINQUEUE;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AdpositionGetListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static AdpositionGetListEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	AdpositionGetListEntity entity = objectMapper.readValue(str, AdpositionGetListEntity.class);
        return entity;

    }
    
}
