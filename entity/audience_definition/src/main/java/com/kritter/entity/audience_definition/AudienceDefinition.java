package com.kritter.entity.audience_definition;

import java.io.IOException;

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
public class AudienceDefinition {
	@Getter@Setter
	private Integer internalid;
	@Getter@Setter
	private String name;
	@Getter@Setter
	private Integer audience_type;
	@Getter@Setter
	private Integer tier;
	@Getter@Setter
	private Integer parent_internalid;
	@Getter@Setter
	private Long last_modified;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}
	public JsonNode toJson(){
		JsonNode jsonNode = objectMapper.valueToTree(this);
		return jsonNode;
	}
	public static AudienceDefinition getObject(String str) throws JsonParseException, JsonMappingException, IOException{
		AudienceDefinition entity = objectMapper.readValue(str, AudienceDefinition.class);
		return entity;
	}
}
