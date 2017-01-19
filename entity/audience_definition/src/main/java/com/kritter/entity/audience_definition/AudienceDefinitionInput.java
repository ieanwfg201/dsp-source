package com.kritter.entity.audience_definition;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import com.kritter.constants.AudienceDefinitionQueryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AudienceDefinitionInput {
	@Getter@Setter
	private String ids;
	@Getter@Setter
	private Integer audience_type;
	@Getter@Setter
	private Integer audience_tier;
	@Getter@Setter
	private AudienceDefinitionQueryType queryType =AudienceDefinitionQueryType.list_by_type;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}
	public JsonNode toJson(){
		JsonNode jsonNode = objectMapper.valueToTree(this);
		return jsonNode;
	}
	public static AudienceDefinitionInput getObject(String str) throws JsonParseException, JsonMappingException, IOException{
		AudienceDefinitionInput entity = objectMapper.readValue(str, AudienceDefinitionInput.class);
		return entity;
	}
	public static AudienceDefinitionInput getObject(JsonNode jsonNode) throws JsonParseException, JsonMappingException, IOException{
		AudienceDefinitionInput entity = objectMapper.treeToValue(jsonNode, AudienceDefinitionInput.class);
		return entity;
	}
}
