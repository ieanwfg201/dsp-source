package models.formelements;

 

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class ColumnData{
		
	private ObjectNode jsonNode = new ObjectNode(JsonNodeFactory.instance);
	public ColumnData(String field, String label, boolean sortable, boolean visible) { 
		jsonNode.put("label", label);
		jsonNode.put("value", field); 
	}
	
	public ColumnData(String field, String label) { 
		jsonNode.put("label", label);
		jsonNode.put("field", field); 
		jsonNode.put("sortable", true); 
		jsonNode.put("visible", true); 
	}

	public String getLabel() {
		return jsonNode.get("label").asText();
	}

	public void setLabel(String label) {
		jsonNode.put("label", label);
	}

	public String getField() {
		return jsonNode.get("field").asText();
	}

	public void setField(String field) {
		jsonNode.put("field", field); 
	}
	
	public String getSortable() {
		return jsonNode.get("field").asText();
	}

	public void setSortable(String field) {
		jsonNode.put("field", field); 
	}
	
	public String getVisible() {
		return jsonNode.get("field").asText();
	}

	public void setVisible(String field) {
		jsonNode.put("field", field); 
	}
	
	public JsonNode toJson(){
		return jsonNode;
	}

}
