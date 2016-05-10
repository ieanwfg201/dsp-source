package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import models.Constants.ReportDataType;
import play.Logger;
import play.db.DB;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.saved_query.SavedQueryEntity;
import com.kritter.api.entity.saved_query.SavedQueryList;
import com.kritter.api.entity.saved_query.SavedQueryListEntity;
import com.kritter.constants.PageConstants;
import com.kritter.constants.SavedQueryEnum;
import com.kritter.kritterui.api.def.ApiDef;

public class ReportingDataService {


	public static SavedQueryEntity getSavedQuery(int id){
		SavedQueryEntity savedQueryEntity = null;
		Connection con = null;
		try {
			con = DB.getConnection();
			SavedQueryListEntity savedQueryListEntity = new SavedQueryListEntity();
			savedQueryListEntity.setPage_no(PageConstants.start_index);
			savedQueryListEntity.setPage_size(1);
			savedQueryListEntity.setId_list(id+"");
			savedQueryListEntity.setSaveQueryEnum(SavedQueryEnum.list_saved_query_entity_by_entity_id);

			SavedQueryList  savedQueryList = ApiDef.various_get_saved_query(con, savedQueryListEntity);
			if(savedQueryList.getMsg().getError_code() == 0){
				List<SavedQueryEntity> savedQueries = savedQueryList.getList(); 
				if(savedQueries.size() == 1){
					savedQueryEntity = savedQueries.get(0);
				}  			  
			}
		} catch (Exception e) {
			Logger.error("Error in loading saved report entity by Id");
		}finally{
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}
		return savedQueryEntity;
	}
	
	public static Message deleteSavedQuery(int id) {
		Connection con = null;  
		try {  	
			con =  DB.getConnection();
			SavedQueryListEntity savedQueryListEntity = new SavedQueryListEntity(); 
			savedQueryListEntity.setId_list(id+"");
			savedQueryListEntity.setSaveQueryEnum(SavedQueryEnum.delete_saved_query);
			return ApiDef.change_status_saved_query(con, savedQueryListEntity); 
		} catch (Exception  e) { 
			Logger.error("Error fetching reporting data");
			return null;
		}  
		finally{
			try {
				if(con != null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed to close connection in Reporting Entity",e2);
			}
		} 
		
	}

	public static JsonNode getData(ReportingEntity reportingEntity, ReportDataType dataType, boolean fetchId){
		JsonNode result = null; 
		Connection con = null; 

		try {  	
			con =  DB.getConnection();
			org.codehaus.jackson.JsonNode data = null;
			switch (dataType) {
			case TABLE:
				data = ApiDef.get_data(con, reportingEntity, fetchId, false, null);
				break;
				
			case PIE:
				data = ApiDef.get_pie(con, reportingEntity);
				break;
				
			case BAR:
				data = ApiDef.get_bar(con, reportingEntity);
				break;

			default:
				break;
			}
			

			ObjectMapper objectMapper = new ObjectMapper();

			result = objectMapper.readTree(data.toString());
		} catch (Exception  e) { 
			Logger.error("Error fetching reporting data");
		} 

		finally{
			try {
				if(con != null)
					con.close();
			} catch (Exception e2) {
				Logger.error("Failed to close connection in Reporting Entity",e2);
			}
		} 
		return result;
	}
	

	 

}
