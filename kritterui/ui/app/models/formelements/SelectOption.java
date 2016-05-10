package models.formelements;

 

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class SelectOption{
		
	private ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
	public SelectOption(String label, String value) { 
		jsonNode.put("label", label);
		jsonNode.put("value", value);  
	}
	
	public SelectOption(String label, String value, String metadata) { 
		jsonNode.put("label", label);
		jsonNode.put("value", value); 
		jsonNode.put("min", "all");
		jsonNode.put("max", "all");
		jsonNode.put("metadata", metadata);
	}

	public String getLabel() {
		return jsonNode.get("label").asText();
	}

	public void setLabel(String label) {
		jsonNode.put("label", label);
	}

	public String getValue() {
		return jsonNode.get("value").asText();
	}

	public void setValue(String value) {
		jsonNode.put("value", value); 
	}
	
	public JsonNode toJson(){
		return jsonNode;
	}
	
	public void setMetadata(String value) {
		jsonNode.put("metadata", value); 
	}
	
	public String getMetadata() {
		return jsonNode.get("metadata").asText();
	}

}
