package com.kritter.api.entity.adxbasedexchangesmetadata;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.AdxBasedExchangesMetadataQueryEnum;
import com.kritter.constants.PageConstants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AdxBasedExchangesMetadataListEntity {
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
    /** mandatory - type of action available @see com.kritter.constants.AdxBasedExchangesMetadataQueryEnum */
    private AdxBasedExchangesMetadataQueryEnum queryEnum = AdxBasedExchangesMetadataQueryEnum.get_all;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static AdxBasedExchangesMetadataListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static AdxBasedExchangesMetadataListEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	AdxBasedExchangesMetadataListEntity entity = objectMapper.readValue(str, AdxBasedExchangesMetadataListEntity.class);
        return entity;

    }
    
}
