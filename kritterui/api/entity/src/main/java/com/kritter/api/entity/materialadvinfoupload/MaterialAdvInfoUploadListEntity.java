package com.kritter.api.entity.materialadvinfoupload;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialAdvInfoUploadQueryEnum;
import com.kritter.constants.PageConstants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class MaterialAdvInfoUploadListEntity {
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
    /** @see com.kritter.constants.MaterialAdvInfoUploadQueryEnum  mandatory - type of action available */
    private MaterialAdvInfoUploadQueryEnum queryEnum = MaterialAdvInfoUploadQueryEnum.list_material_advinfo;
	@Getter@Setter
    private AdxBasedExchangesStates adxstate = AdxBasedExchangesStates.BRINGINQUEUE;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static MaterialAdvInfoUploadListEntity getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static MaterialAdvInfoUploadListEntity getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	MaterialAdvInfoUploadListEntity entity = objectMapper.readValue(str, MaterialAdvInfoUploadListEntity.class);
        return entity;

    }
    
}
