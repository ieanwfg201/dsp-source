package com.kritter.kritterui.api.video_info;

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
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.video_info.VideoInfoList;
import com.kritter.api.entity.video_info.VideoInfoListEntity;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.video_props.VideoInfo;
import com.kritter.entity.video_props.VideoInfoExt;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;

public class VideoInfoCrud {
    private static final Logger LOG = LoggerFactory.getLogger(VideoInfoCrud.class);
    
    public static void populate(VideoInfo video_info, ResultSet rset) throws SQLException{
        if(video_info != null && rset != null){
            video_info.setAccount_guid(rset.getString("account_guid"));
            video_info.setGuid(rset.getString("guid"));
            video_info.setId(rset.getInt("id"));
            video_info.setModified_by(rset.getInt("modified_by"));
            video_info.setResource_uri(rset.getString("resource_uri"));
            video_info.setVideo_size(rset.getInt("video_size"));
            video_info.setCreated_on(rset.getTimestamp("created_on").getTime());
            video_info.setLast_modified(rset.getTimestamp("last_modified").getTime());
            String ext = rset.getString("ext");
            video_info.setExt(populateExt(ext));
        }
    }
    private static VideoInfoExt populateExt(String ext){
        if(ext != null){
        	String extStr = ext.trim();
        	if(!"".equals(extStr)){
        		try {
					VideoInfoExt vie = VideoInfoExt.getObject(extStr);
					return vie;
				} catch (Exception e) {
					LOG.error(e.getMessage(),e);
					return null;
				}
        	}
        }
    	return null;
    }
    private static String generateExt(VideoInfoExt vie){
    	if(vie==null){
    		return "";
    	}
    	return vie.toJson().toString();
    }
    public static JsonNode insert_video_info(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.VIDEO_INFO_NULL.getId());
            msg.setMsg(ErrorEnum.VIDEO_INFO_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            VideoInfo video_info = objectMapper.treeToValue(jsonNode, VideoInfo.class);
            return insert_video_info(con, video_info, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message insert_video_info(Connection con, VideoInfo video_info, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(video_info == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.VIDEO_INFO_NULL.getId());
            msg.setMsg(ErrorEnum.VIDEO_INFO_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.VideoInfo.insert_video_info,PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());
            pstmt.setString(2, video_info.getAccount_guid());
            pstmt.setInt(3, video_info.getVideo_size());
            pstmt.setString(4, video_info.getResource_uri());
            pstmt.setInt(5, video_info.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(6, ts);
            pstmt.setString(7, generateExt(video_info.getExt()));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.VIDEO_INFO_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.VIDEO_INFO_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int cb_id = -1;
            if (keyResultSet.next()) {
                cb_id = keyResultSet.getInt(1);
            }
            video_info.setId(cb_id);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(cb_id+"");
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
    
    public static JsonNode update_video_info(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.VIDEO_INFO_NULL.getId());
            msg.setMsg(ErrorEnum.VIDEO_INFO_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            VideoInfo video_info = objectMapper.treeToValue(jsonNode, VideoInfo.class);
            return update_video_info(con, video_info, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    public static Message update_video_info(Connection con, VideoInfo video_info, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(video_info == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.VIDEO_INFO_NULL.getId());
            msg.setMsg(ErrorEnum.VIDEO_INFO_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.VideoInfo.update_video_info);
            pstmt.setInt(1, video_info.getVideo_size());
            pstmt.setString(2, video_info.getResource_uri());
            pstmt.setInt(3, video_info.getModified_by());
            Timestamp ts = new Timestamp((new Date()).getTime());
            pstmt.setTimestamp(4, ts);
            pstmt.setString(5, generateExt(video_info.getExt()));
            pstmt.setInt(6, video_info.getId());
            
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.VIDEO_INFO_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.VIDEO_INFO_NOT_UPDATED.getName());
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
    
    public static JsonNode various_get_video_info(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            VideoInfoList videoinfolist = new VideoInfoList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.VIDEO_INFOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.VIDEO_INFOLIST_ENTITY_NULL.getName());
            videoinfolist.setMsg(msg);
            return videoinfolist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            VideoInfoListEntity videoinfolistEntity = objectMapper.treeToValue(jsonNode, VideoInfoListEntity.class);
            return various_get_video_info(con, videoinfolistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            VideoInfoList videoinfolist = new VideoInfoList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            videoinfolist.setMsg(msg);
            return videoinfolist.toJson();
        }
    }
    
    public static VideoInfoList various_get_video_info(Connection con, VideoInfoListEntity videoinfolistEntity){
        if(con == null){
            VideoInfoList videoinfolist = new VideoInfoList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            videoinfolist.setMsg(msg);
            return videoinfolist;
        }
        if(videoinfolistEntity == null){
            VideoInfoList videoinfolist = new VideoInfoList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.VIDEO_INFOLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.VIDEO_INFOLIST_ENTITY_NULL.getName());
            videoinfolist.setMsg(msg);
            return videoinfolist;
        }
        PreparedStatement pstmt = null;
        try{
            switch (videoinfolistEntity.getVideoenum()){
                case get_video_info_by_id:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.VideoInfo.get_video_info_by_id);
                    pstmt.setInt(1, videoinfolistEntity.getId());
                    break;
                case list_video_info_by_account:
                    if(videoinfolistEntity.getAccount_guid() == null){
                        VideoInfoList  cblist = new VideoInfoList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.VIDEO_INFO_GUID_NULL.getId());
                        msg.setMsg(ErrorEnum.VIDEO_INFO_GUID_NULL.getName());
                        cblist.setMsg(msg);
                        return cblist;
                    }
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.VideoInfo.list_video_info_by_account);
                    pstmt.setString(1, videoinfolistEntity.getAccount_guid());
                    break;
                case get_video_info_by_ids:
                    if(videoinfolistEntity.getId_list() == null){
                        VideoInfoList  cblist = new VideoInfoList();
                        Message msg = new Message();
                        msg.setError_code(ErrorEnum.VIDEO_INFO_ID_LIST_NULL.getId());
                        msg.setMsg(ErrorEnum.VIDEO_INFO_ID_LIST_NULL.getName());
                        cblist.setMsg(msg);
                        return cblist;
                    }
                    pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                            com.kritter.kritterui.api.db_query_def.VideoInfo.get_video_info_by_ids, "<id>", videoinfolistEntity.getId_list(), 
                            ",", true));
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            VideoInfoList  cblist = new VideoInfoList();
            List<VideoInfo> cbs = new LinkedList<VideoInfo>();
            while(rset.next()){
                VideoInfo cb = new VideoInfo();
                populate(cb, rset);
                cbs.add(cb);
            }
            cblist.setCblist(cbs);
            Message msg = new Message();
            if(cbs.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.VIDEO_INFO_NF.getId());
                msg.setMsg(ErrorEnum.VIDEO_INFO_NF.getName());
            }
            cblist.setMsg(msg);
            return cblist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            VideoInfoList  cblist = new VideoInfoList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            cblist.setMsg(msg);
            return cblist;
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
}
