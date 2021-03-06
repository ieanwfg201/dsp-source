package com.kritter.entity.audience_definition;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

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
public class AudienceTargetingDef {
	@Getter@Setter
	private Boolean genderincl;
	@Getter@Setter
	private Set<Integer> gender;
	@Getter@Setter
	private Boolean agerangeinc;
	@Getter@Setter
	private Set<Integer> agerange;
	@Getter@Setter
	private Boolean catinc;
	@Getter@Setter
	private Map<Integer,Set<Integer>> tierCatSet;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}
	public JsonNode toJson(){
		JsonNode jsonNode = objectMapper.valueToTree(this);
		return jsonNode;
	}
	public static AudienceTargetingDef getObject(String str) throws JsonParseException, JsonMappingException, IOException{
		AudienceTargetingDef entity = objectMapper.readValue(str, AudienceTargetingDef.class);
		return entity;
	}


}
