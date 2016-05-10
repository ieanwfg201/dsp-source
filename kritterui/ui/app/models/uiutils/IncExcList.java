package models.uiutils;

import java.util.List;

public class IncExcList {

	private boolean isIncList = false;
	
	private List<String> values;
	
	public IncExcList(boolean isIncList, List<String> values){
		this.isIncList = isIncList;
		this.values = values;
	}

	public boolean isIncList() {
		return isIncList;
	}

	public void setIncList(boolean isIncList) {
		this.isIncList = isIncList;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}
	
	
}
