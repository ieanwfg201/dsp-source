package com.kritter.kritterui.api.adxbasedexchanges_metadata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kritter.api.entity.materialadvinfoupload.MaterialAdvInfoUploadList;
import com.kritter.api.entity.materialadvinfoupload.MaterialAdvInfoUploadListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadAdvInfo;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class MaterialAdvInfoCrud {

	private static final Logger LOG = LoggerFactory.getLogger(MaterialAdvInfoCrud.class);

	public static MaterialUploadAdvInfo populate( ResultSet rset) throws SQLException{
		MaterialUploadAdvInfo entity = null;
		if(rset != null){
			entity = new MaterialUploadAdvInfo();
			entity.setInternalid(rset.getInt("internalid"));
			entity.setPubIncId(rset.getInt("pubIncId"));
			entity.setAdxbasedexhangesstatus(rset.getInt("adxbasedexhangesstatus"));
			entity.setAdvIncId(rset.getInt("advIncId"));
			entity.setAdvName(rset.getString("advName"));
			entity.setMessage(rset.getString("message"));
			entity.setInfo(rset.getString("info"));
			entity.setLast_modified(rset.getTimestamp("last_modified").getTime());
		}
		return entity;
	}


	public static JsonNode various_material_advinfo(Connection con, JsonNode jsonNode){
		if(jsonNode == null){
			MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_ADVINFO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_ADVINFO_NULL.getName());
			returnEntity.setMsg(msg);
			return returnEntity.toJson();
		}
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			MaterialAdvInfoUploadListEntity entity = objectMapper.treeToValue(jsonNode, MaterialAdvInfoUploadListEntity.class);
			return various_material_advinfo(con, entity).toJson();
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
			returnEntity.setMsg(msg);
			return returnEntity.toJson();
		}
	}
	public static MaterialAdvInfoUploadList various_material_advinfo(Connection con, MaterialAdvInfoUploadListEntity entity){
		if(con == null){
			MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
			msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
			returnEntity.setMsg(msg);
			return returnEntity;
		}
		if(entity == null){
			MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_ADVINFO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_ADVINFO_NULL.getName());
			returnEntity.setMsg(msg);
			return returnEntity;
		}
		PreparedStatement pstmt = null;
		try{
			switch (entity.getQueryEnum()){
			case list_material_advinfo:
				pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.MaterialAdvInfoDef.list_material_advinfo);
				break;
			case list_material_advinfo_by_pubincids:
                if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list()) ||
                		"all".equalsIgnoreCase(entity.getId_list()) || "[all]".equalsIgnoreCase(entity.getId_list())
                		|| "".equalsIgnoreCase(entity.getId_list())){
                	MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
        			Message msg = new Message();
        			msg.setError_code(ErrorEnum.NO_ERROR.getId());
        			msg.setMsg(ErrorEnum.NO_ERROR.getName());
        			returnEntity.setMsg(msg);
        			return returnEntity;
                }else{
                	pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.MaterialAdvInfoDef.list_material_advinfo_by_pubincids, "<id>", entity.getId_list(), 
                            ",", false));
                }
				break;
			case list_material_advinfo_by_pubincids_status:
                if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list()) ||
                		"all".equalsIgnoreCase(entity.getId_list()) || "[all]".equalsIgnoreCase(entity.getId_list())
                		|| "".equalsIgnoreCase(entity.getId_list())){
                	MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
        			Message msg = new Message();
        			msg.setError_code(ErrorEnum.NO_ERROR.getId());
        			msg.setMsg(ErrorEnum.NO_ERROR.getName());
        			returnEntity.setMsg(msg);
        			return returnEntity;
                }else{
                	pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.MaterialAdvInfoDef.list_material_advinfo_by_pubincids_status, "<id>", entity.getId_list(), 
                            ",", false));
                	if(entity.getAdxstate()==null){
                		pstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
                	}else{
                		pstmt.setInt(1, entity.getAdxstate().getCode());
                	}
                }
				break;
			default:
				break;
			}
			ResultSet rset = pstmt.executeQuery();
			MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
			List<MaterialUploadAdvInfo> entities = new LinkedList<MaterialUploadAdvInfo>();
			while(rset.next()){
				MaterialUploadAdvInfo outentity = populate(rset);
				if(outentity != null){
					entities.add(outentity);
				}
			}
			returnEntity.setEntity_list(entities);
			Message msg = new Message();
			if(entities.size()>0){
				msg.setError_code(ErrorEnum.NO_ERROR.getId());
				msg.setMsg(ErrorEnum.NO_ERROR.getName());
			}else{
				msg.setError_code(ErrorEnum.MATERIAL_ADVINFO_NF.getId());
				msg.setMsg(ErrorEnum.MATERIAL_ADVINFO_NF.getName());
			}
			returnEntity.setMsg(msg);
			return returnEntity;
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			MaterialAdvInfoUploadList returnEntity = new MaterialAdvInfoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
			returnEntity.setMsg(msg);
			return returnEntity;

		}finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		} 
	}
	public static JsonNode update_material_advinfo_status(Connection con, JsonNode jsonNode){
		if(jsonNode == null){
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_ADVINFO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_ADVINFO_NULL.getName());
			return msg.toJson();
		}
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			MaterialAdvInfoUploadListEntity entity = objectMapper.treeToValue(jsonNode, MaterialAdvInfoUploadListEntity.class);
			return update_material_advinfo_status(con, entity, true).toJson();
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			Message msg = new Message();
			msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
			return msg.toJson();
		}
	}
	public static Message update_material_advinfo_status(Connection con, MaterialAdvInfoUploadListEntity entity, boolean createTransaction){
		if(con == null){
			Message msg = new Message();
			msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
			msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
			return msg;
		}
		if(entity == null){
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_ADVINFO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_ADVINFO_NULL.getName());
			return msg;
		}
		PreparedStatement pstmt = null;
		boolean autoCommitFlag = false;
		try{
			if(createTransaction){
				autoCommitFlag = con.getAutoCommit();
				con.setAutoCommit(false);
			}
			pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
					com.kritter.kritterui.api.db_query_def.MaterialAdvInfoDef.update_material_advinfo_status, "<id>", entity.getId_list(), 
					",", false));

			pstmt.setInt(1, entity.getAdxstate().getCode());
			pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
			int returnCode = pstmt.executeUpdate();
			if(createTransaction){
				con.commit();
			}
			if(returnCode == 0){
				Message msg = new Message();
				msg.setError_code(ErrorEnum.MATERIAL_ADVINFO_NOT_UPDATED.getId());
				msg.setMsg(ErrorEnum.MATERIAL_ADVINFO_NOT_UPDATED.getName());
				return msg;
			}
			Message msg = new Message();
			msg.setError_code(ErrorEnum.NO_ERROR.getId());
			msg.setMsg(ErrorEnum.NO_ERROR.getName());
			return msg;
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			if(createTransaction){
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);
				}
			}
			Message msg = new Message();
			msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
			return msg;
		}finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
			if(createTransaction){
				try {
					con.setAutoCommit(autoCommitFlag);
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);
				}
			}
		} 
	}

}
