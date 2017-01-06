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
import com.kritter.constants.AudienceMetadataQueryType;
import com.kritter.entity.audience_metadata.AudienceMetadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@EqualsAndHashCode
public class AudienceMetadataList {
	@Getter@Setter
	private List<AudienceMetadata> list;
	@Getter@Setter
	private Message msg;
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
	public static AudienceMetadataList getObject(String str) throws JsonParseException, JsonMappingException, IOException{
		AudienceMetadataList entity = objectMapper.readValue(str, AudienceMetadataList.class);
		return entity;
	}
	public static AudienceMetadataList getObject(JsonNode jsonNode) throws JsonParseException, JsonMappingException, IOException{
		AudienceMetadataList entity = objectMapper.treeToValue(jsonNode, AudienceMetadataList.class);
		return entity;
	}
}
