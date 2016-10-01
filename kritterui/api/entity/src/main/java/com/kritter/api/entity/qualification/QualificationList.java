package com.kritter.api.entity.qualification;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.account.Qualification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class QualificationList {
    /** @see com.kritter.api.entity.response.msg.Message */
	@Getter@Setter
    private Message msg = null;
    /** @see com.kritter.entity.account.Qualification*/
	@Getter@Setter
    private List<Qualification> entity_list = null;
	
    public JsonNode toJson(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        JsonNode jsonNode = objectMapper.valueToTree(this);
        return jsonNode;
    }
    public static QualificationList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        return getObject(objectMapper,str);
    }
    public static QualificationList getObject(ObjectMapper objectMapper,String str) throws JsonParseException, JsonMappingException, IOException{
    	QualificationList entity = objectMapper.readValue(str, QualificationList.class);
        return entity;

    }
 
}
