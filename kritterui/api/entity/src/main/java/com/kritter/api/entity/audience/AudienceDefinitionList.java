package com.kritter.api.entity.audience;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.api.entity.response.msg.Message;
import com.kritter.entity.audience_definition.AudienceDefinition;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AudienceDefinitionList {
	@Getter@Setter
	private List<AudienceDefinition> list;
	@Getter@Setter
	private Message msg;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}
	public JsonNode toJson(){
		JsonNode jsonNode = objectMapper.valueToTree(this);
		return jsonNode;
	}
	public static AudienceDefinitionList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
		AudienceDefinitionList entity = objectMapper.readValue(str, AudienceDefinitionList.class);
		return entity;
	}
	public static AudienceDefinitionList getObject(JsonNode jsonNode) throws JsonParseException, JsonMappingException, IOException{
		AudienceDefinitionList entity = objectMapper.treeToValue(jsonNode, AudienceDefinitionList.class);
		return entity;
	}
}
