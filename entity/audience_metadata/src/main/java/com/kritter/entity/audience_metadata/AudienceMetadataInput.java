package com.kritter.entity.audience_metadata;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.kritter.constants.AudienceMetadataQueryType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AudienceMetadataInput {
	@Getter@Setter
	private String ids;
	@Getter@Setter
	private Boolean enabled;
	@Getter@Setter
	private AudienceMetadataQueryType queryType =AudienceMetadataQueryType.list_all;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}
	public JsonNode toJson(){
		JsonNode jsonNode = objectMapper.valueToTree(this);
		return jsonNode;
	}
	public static AudienceMetadataInput getObject(String str) throws JsonParseException, JsonMappingException, IOException{
		AudienceMetadataInput entity = objectMapper.readValue(str, AudienceMetadataInput.class);
		return entity;
	}
	public static AudienceMetadataInput getObject(JsonNode jsonNode) throws JsonParseException, JsonMappingException, IOException{
		AudienceMetadataInput entity = objectMapper.treeToValue(jsonNode, AudienceMetadataInput.class);
		return entity;
	}
}
