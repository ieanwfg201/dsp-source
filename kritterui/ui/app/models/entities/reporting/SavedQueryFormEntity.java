package models.entities.reporting;

import java.io.IOException;

import play.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kritter.api.entity.saved_query.SavedQueryEntity;
import com.kritter.constants.ReportingTypeEnum;

public class  SavedQueryFormEntity{

	private SavedQueryEntity savedQueryEntity;
	
	 public SavedQueryFormEntity(SavedQueryEntity savedQueryEntity){
		 this.savedQueryEntity = savedQueryEntity;
	 }
	 
	 public SavedQueryFormEntity(){
		 this.savedQueryEntity = new SavedQueryEntity();
	 }
	 
	 public int getId(){
		return savedQueryEntity.getId(); 
	 }
	 
	 public void setId(int id){
		savedQueryEntity.setId(id); 
	 }
	 public String getName(){
		 return savedQueryEntity.getName();
	 }
	 
	 public void setName(String name){
		 savedQueryEntity.setName(name); 
	 }
	 
	 public String getReportType(){ 
		 return savedQueryEntity.getReportingTypeEnum().name();
	 }
	 
	 public void setReportType(String type){ 
		 savedQueryEntity.setReportingTypeEnum(ReportingTypeEnum.valueOf(type));
	 }
	 
	 public String getQuery(){
		 ReportFormEntity rfe = new ReportFormEntity(savedQueryEntity.getReporting_entity());
		 try {
			 ObjectMapper om = new ObjectMapper();
			 return om.valueToTree(rfe).toString(); 
		} catch (Exception e) {
			return "";
		}
		
	 }
	 
	 public void setQuery(String query){
		 ObjectMapper om = new ObjectMapper();
		 ObjectNode node;
		try {
			node = (ObjectNode)om.readTree(query);
			node.remove("inputDate");
			node.remove("inputTime"); 
			ReportFormEntity rfe = om.treeToValue(node, ReportFormEntity.class);
			savedQueryEntity.setReporting_entity(rfe.getReportEntity());
		} catch (  IOException e) {
			Logger.error("Error parsing report query");
		}
			
		 savedQueryEntity.getName();
	 }

	public SavedQueryEntity getSavedQueryEntity() {
		return savedQueryEntity;
	}

	public void setAccount_guid(String account_guid) {
		savedQueryEntity.setAccount_guid(account_guid);
	} 
	
	public String getAccount_guid() { 
		return savedQueryEntity.getAccount_guid();
	}
}
