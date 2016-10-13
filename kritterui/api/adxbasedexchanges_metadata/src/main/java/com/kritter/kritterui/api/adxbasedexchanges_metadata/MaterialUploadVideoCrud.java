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

import com.kritter.api.entity.materialvideoupload.MaterialVideoUploadList;
import com.kritter.api.entity.materialvideoupload.MaterialVideoUploadListEntity;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.adxbasedexchanges_metadata.MaterialUploadVideo;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;

public class MaterialUploadVideoCrud {

	private static final Logger LOG = LoggerFactory.getLogger(MaterialUploadVideoCrud.class);

	public static MaterialUploadVideo populate( ResultSet rset) throws SQLException{
		MaterialUploadVideo entity = null;
		if(rset != null){
			entity = new MaterialUploadVideo();
			entity.setInternalid(rset.getInt("internalid"));
			entity.setPubIncId(rset.getInt("pubIncId"));
			entity.setAdxbasedexhangesstatus(rset.getInt("adxbasedexhangesstatus"));
			entity.setAdvIncId(rset.getInt("advIncId"));
			entity.setAdvName(rset.getString("advName"));
			entity.setCampaignId(rset.getInt("campaignId"));
			entity.setCampaignName(rset.getString("campaignName"));
			entity.setCampaignStatus(rset.getInt("campaignStatus"));
			entity.setAdId(rset.getInt("adId"));
			entity.setAdName(rset.getString("adName"));
			entity.setAdStatus(rset.getInt("adStatus"));
			entity.setCreativeId(rset.getInt("creativeId"));
			entity.setCreativeName(rset.getString("creativeName"));
			entity.setCreativeStatus(rset.getInt("creativeStatus"));
			entity.setVideoInfoId(rset.getInt("videoInfoId"));
			entity.setMessage(rset.getString("message"));
			entity.setInfo(rset.getString("info"));
			entity.setResource_uri_ids(rset.getString("resource_uri_ids"));
			entity.setLast_modified(rset.getTimestamp("last_modified").getTime());
		}
		return entity;
	}


	public static JsonNode various_material_video(Connection con, JsonNode jsonNode){
		if(jsonNode == null){
			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_VIDEO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_VIDEO_NULL.getName());
			returnEntity.setMsg(msg);
			return returnEntity.toJson();
		}
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			MaterialVideoUploadListEntity entity = objectMapper.treeToValue(jsonNode, MaterialVideoUploadListEntity.class);
			return various_material_video(con, entity).toJson();
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
			returnEntity.setMsg(msg);
			return returnEntity.toJson();
		}
	}
	public static MaterialVideoUploadList various_material_video(Connection con, MaterialVideoUploadListEntity entity){
		if(con == null){
			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
			msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
			returnEntity.setMsg(msg);
			return returnEntity;
		}
		if(entity == null){
			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_VIDEO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_VIDEO_NULL.getName());
			returnEntity.setMsg(msg);
			return returnEntity;
		}
		PreparedStatement pstmt = null;
		try{
			switch (entity.getQueryEnum()){
			case list_material_video:
				pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.MaterialVideoDef.list_material_video);
				break;
			case list_material_video_by_pubincids:
                if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list()) ||
                		"all".equalsIgnoreCase(entity.getId_list()) || "[all]".equalsIgnoreCase(entity.getId_list())
                		|| "".equalsIgnoreCase(entity.getId_list())){
        			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
        			Message msg = new Message();
        			msg.setError_code(ErrorEnum.NO_ERROR.getId());
        			msg.setMsg(ErrorEnum.NO_ERROR.getName());
        			returnEntity.setMsg(msg);
        			return returnEntity;
                }else{
                	pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.MaterialVideoDef.list_material_video_by_pubincids, "<id>", entity.getId_list(), 
                            ",", false));
                }
				break;
			case list_material_video_by_pubincids_state:
                if("none".equalsIgnoreCase(entity.getId_list()) || "[none]".equalsIgnoreCase(entity.getId_list()) ||
                		"all".equalsIgnoreCase(entity.getId_list()) || "[all]".equalsIgnoreCase(entity.getId_list())
                		|| "".equalsIgnoreCase(entity.getId_list())){
        			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
        			Message msg = new Message();
        			msg.setError_code(ErrorEnum.NO_ERROR.getId());
        			msg.setMsg(ErrorEnum.NO_ERROR.getName());
        			returnEntity.setMsg(msg);
        			return returnEntity;
                }else{
                	pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.MaterialVideoDef.list_material_video_by_pubincids_state, "<id>", entity.getId_list(), 
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
			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
			List<MaterialUploadVideo> entities = new LinkedList<MaterialUploadVideo>();
			while(rset.next()){
				MaterialUploadVideo outentity = populate(rset);
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
				msg.setError_code(ErrorEnum.MATERIAL_VIDEO_NF.getId());
				msg.setMsg(ErrorEnum.MATERIAL_VIDEO_NF.getName());
			}
			returnEntity.setMsg(msg);
			return returnEntity;
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			MaterialVideoUploadList returnEntity = new MaterialVideoUploadList();
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
	public static JsonNode update_material_video_status(Connection con, JsonNode jsonNode){
		if(jsonNode == null){
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_VIDEO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_VIDEO_NULL.getName());
			return msg.toJson();
		}
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			MaterialVideoUploadListEntity entity = objectMapper.treeToValue(jsonNode, MaterialVideoUploadListEntity.class);
			return update_material_video_status(con, entity, true).toJson();
		}catch(Exception e){
			LOG.error(e.getMessage(),e);
			Message msg = new Message();
			msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
			msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
			return msg.toJson();
		}
	}
	public static Message update_material_video_status(Connection con, MaterialVideoUploadListEntity entity, boolean createTransaction){
		if(con == null){
			Message msg = new Message();
			msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
			msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
			return msg;
		}
		if(entity == null){
			Message msg = new Message();
			msg.setError_code(ErrorEnum.MATERIAL_VIDEO_NULL.getId());
			msg.setMsg(ErrorEnum.MATERIAL_VIDEO_NULL.getName());
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
					com.kritter.kritterui.api.db_query_def.MaterialVideoDef.update_material_video_status, "<id>", entity.getId_list(), 
					",", false));

			pstmt.setInt(1, entity.getAdxstate().getCode());
			pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
			int returnCode = pstmt.executeUpdate();
			if(createTransaction){
				con.commit();
			}
			if(returnCode == 0){
				Message msg = new Message();
				msg.setError_code(ErrorEnum.MATERIAL_VIDEO_NOT_UPDATED.getId());
				msg.setMsg(ErrorEnum.MATERIAL_VIDEO_NOT_UPDATED.getName());
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
