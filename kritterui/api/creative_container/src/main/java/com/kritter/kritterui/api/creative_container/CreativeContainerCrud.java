package com.kritter.kritterui.api.creative_container;

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
import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.error.ErrorEnum;
import com.kritter.entity.creative_macro.CreativeMacro;
import com.kritter.entity.native_props.demand.NativeDemandProps;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.kritterui.api.utils.InQueryPrepareStmnt;
import com.kritter.utils.uuid.mac.SingletonUUIDGenerator;


public class CreativeContainerCrud {
    
    private static final Logger LOG = LoggerFactory.getLogger(CreativeContainerCrud.class);
    
    public static String convertResourceUriIdsFromUItoDB(String input){
        if(input == null){
            return input;
        }
        String tmpInput = input.trim();
        if(tmpInput.equals("")){
            return tmpInput;
        }
        if(!tmpInput.startsWith("[")){
            tmpInput = "["+tmpInput;
        }
        if(!tmpInput.endsWith("]")){
            tmpInput = tmpInput+"]";
        }
        return tmpInput;
    }
    public static String convertResourceUriIdsFromDBtoUI(String input){
        if(input == null){
            return input;
        }
        return input.replaceAll("\\[", "").replaceAll("]", "");
    }
    
    public static void populate(Creative_container cc, ResultSet rset) throws SQLException{
        if(cc != null && rset != null){
          cc.setAccount_guid(rset.getString("account_guid"));
          cc.setCreative_attr(rset.getString("creative_attr"));
          cc.setFormat_id(rset.getInt("format_id"));
          cc.setGuid(rset.getString("guid"));
          cc.setHtml_content(rset.getString("html_content"));
          cc.setId(rset.getInt("id"));
          cc.setLabel(rset.getString("label"));
          cc.setModified_by(rset.getInt("modified_by"));
          cc.setResource_uri_ids(convertResourceUriIdsFromDBtoUI(rset.getString("resource_uri_ids")));
          cc.setText(rset.getString("text"));
          cc.setStatus_id(StatusIdEnum.getEnum(rset.getInt("status_id")));
          cc.setExt_resource_url(rset.getString("ext_resource_url"));
          String native_demand_props =  rset.getString("native_demand_props");
          if(native_demand_props != null){
              String native_demand_props_trim = native_demand_props.trim();
              if(!"".equals(native_demand_props_trim)){
                  try{
                      NativeDemandProps ndp = NativeDemandProps.getObject(native_demand_props_trim);
                      if(ndp.getActive_players() != null){
                      cc.setNative_active_players(ndp.getActive_players());
                      }
                      if(ndp.getCta() != null){
                      cc.setNative_cta(ndp.getCta());
                      }
                      if(ndp.getDesc() != null){
                          cc.setNative_desc(ndp.getDesc());
                      }
                      if(ndp.getDownload_count() != null){
                          cc.setNative_download_count(ndp.getDownload_count());
                      }
                      if(ndp.getIconsStr() != null){
                          cc.setNative_icons(ndp.getIconsStr());
                      }
                      if(ndp.getRating() != null){
                          cc.setNative_rating(ndp.getRating());
                      }
                      if(ndp.getScreenshotStr() != null){
                          cc.setNative_screenshots(ndp.getScreenshotStr());
                      }
                      if(ndp.getTitle() != null){
                          cc.setNative_title(ndp.getTitle());
                      }
                  }catch(Exception e){
                      LOG.error(e.getMessage(),e);
                  }
              }
          }
          String creative_macro =  rset.getString("creative_macro");
          if(creative_macro != null){
              String creative_macro_trim = creative_macro.trim();
              if(!"".equals(creative_macro_trim)){
                  try{
                      CreativeMacro creativeMacro = CreativeMacro.getObject(creative_macro_trim);
                      if(creativeMacro != null){
                          cc.setCreative_macro(creativeMacro.getMacroIds().toString());
                          cc.setCreative_macro_quote(creativeMacro.getQuote());
                      }
                  }catch(Exception e){
                      LOG.error(e.getMessage(),e);
                  }
              }
          }
          String video_props =  rset.getString("video_props");
          if(video_props != null){
              String video_props_trim = video_props.trim();
              if(!"".equals(video_props_trim)){
                  try{
                      VideoProps videoProps = VideoProps.getObject(video_props_trim);
                      if(videoProps != null){
                          cc.setMime(videoProps.getMime());
                          cc.setDuration(videoProps.getDuration());
                          cc.setProtocol(videoProps.getProtocol());
                          cc.setStartdelay(videoProps.getStartdelay());
                          cc.setWidth(videoProps.getWidth());
                          cc.setHeight(videoProps.getHeight());
                          cc.setLinearity(videoProps.getLinearity());
                          cc.setMaxextended(videoProps.getMaxextended());
                          cc.setBitrate(videoProps.getBitrate());
                          cc.setBoxingallowed(videoProps.getBoxingallowed());
                          cc.setPlaybackmethod(videoProps.getPlaybackmethod());
                          cc.setDelivery(videoProps.getDelivery());
                          cc.setApi(videoProps.getApi());
                          cc.setCompaniontype(videoProps.getCompaniontype());
                          cc.setVastTagUrl(videoProps.getVastTagUrl());
                          cc.setVideoDemandType(videoProps.getVideoDemandType());
                          Integer[] tracking = videoProps.getTracking();
                          if(tracking != null){
                              StringBuffer sbuff = new StringBuffer("[");
                              boolean isFirst=true;
                              for(Integer i:tracking){
                                  if(isFirst){
                                      isFirst=false;
                                  }else{
                                      sbuff.append(",");
                                  }
                                  sbuff.append(i);
                              }
                              sbuff.append("]");
                              cc.setTrackingStr(sbuff.toString());
                          }
                          cc.setDirect_videos(arrayToString(videoProps.getVideo_info()));
                      }
                  }catch(Exception e){
                      LOG.error(e.getMessage(),e);
                  }
              }
          }
        }
    }

    private static String arrayToString(String[] str){
    	StringBuffer sbuff = new StringBuffer("[");
    	boolean isFirst = true;
    	if(str!=null&&str.length>0){
    		for(String strElement:str){
    			String strElememtTrim = strElement.trim();
    			if(!"".equals(strElememtTrim)){
    				if(isFirst){
    					isFirst = false;
    				}else{
    					sbuff.append(",");
    				}
    				sbuff.append(strElememtTrim);
    			}
    		}
    	}
    	sbuff.append("]");
    	return sbuff.toString();
    }
    private static List<Integer> stringtolist(String str){
        try{
            if(str==null){
                return null;
            }
            String strTrim = str.trim();
            if("".equals(strTrim)){
                return null;
            }
            String strNew = strTrim.replaceAll("\\[", "").replaceAll("]", "");
            String strSplit[] = strNew.split(",");
            if(strSplit.length>0){
                List<Integer> ll = new LinkedList<Integer>();
                for(String s:strSplit){
                    ll.add(Integer.parseInt(s));
                }
                return ll;
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
    private static String[] stringtoarray(String str){
        try{
            if(str==null){
                return null;
            }
            String strTrim = str.trim();
            if("".equals(strTrim)){
                return null;
            }
            String strNew = strTrim.replaceAll("\\[", "").replaceAll("]", "");
            String strSplit[] = strNew.split(",");
            if(strSplit.length>0){
                List<String> ll = new LinkedList<String>();
                for(String s:strSplit){
                	if(!"".equals(s)){
                		ll.add(s);
                	}
                }
                String[] array = new String[ll.size()];
                ll.toArray(array);
                return array;
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
    public static String generateVideoProps(Creative_container cc){
        VideoProps vp = new VideoProps();
        vp.setMime(cc.getMime());
        vp.setDuration(cc.getDuration());
        vp.setProtocol(cc.getProtocol());
        vp.setStartdelay(cc.getStartdelay());
        vp.setWidth(cc.getWidth());
        vp.setHeight(cc.getHeight());
        vp.setLinearity(cc.getLinearity());
        vp.setMaxextended(cc.getMaxextended());
        vp.setBitrate(cc.getBitrate());
        vp.setBoxingallowed(cc.getBoxingallowed());
        vp.setPlaybackmethod(cc.getPlaybackmethod());
        vp.setDelivery(cc.getDelivery());
        vp.setApi(cc.getApi());
        vp.setCompaniontype(cc.getCompaniontype());
        vp.setVastTagUrl(cc.getVastTagUrl());
        vp.setVideoDemandType(cc.getVideoDemandType());
        List<Integer> l = stringtolist(cc.getTrackingStr());
        if(l != null){
            vp.setTracking(l.toArray(new Integer[l.size()]));
        }
        vp.setVideo_info(stringtoarray(cc.getDirect_videos()));
        
        return vp.toJson().toString();
    }
    public static String generateCreativeMacros(Creative_container cc){
        String creative_macros = cc.getCreative_macro();
        if(creative_macros != null){
            String creative_macros_trim = creative_macros.trim().replaceAll("\\[", "").replaceAll("]", "");
            if(!"".equals(creative_macros_trim)){
                CreativeMacro cm = new CreativeMacro();
                String strSplit[] = creative_macros_trim.split(",");
                LinkedList<Integer> ll = cm.getMacroIds();
                for(String str:strSplit){
                    ll.add(Integer.parseInt(str));
                }
                cm.setQuote(cc.getCreative_macro_quote());
                return cm.toJson().toString();
            }
        }
        return "";
    }
    public static String generateNativeDemandProps(Creative_container cc){
        if(CreativeFormat.Native.getCode() == cc.getFormat_id()){
            NativeDemandProps ndp = new NativeDemandProps();
            if(cc.getNative_active_players() != null){
                ndp.setActive_players(cc.getNative_active_players());
            }
            if(cc.getNative_active_players() != null){
                ndp.setCta(cc.getNative_cta());
            }
            if(cc.getNative_desc() != null){
                ndp.setDesc(cc.getNative_desc());
            }
            if(cc.getNative_download_count() != null){
                ndp.setDownload_count(cc.getNative_download_count());
            }
            if(cc.getNative_icons() != null){
                List<Integer> ll = stringtolist(cc.getNative_icons());
                if(ll != null){
                    ndp.setIcons(ll);
                }
            }
            if(cc.getNative_rating() != null){
                ndp.setRating(cc.getNative_rating());
            }
            if(cc.getNative_screenshots() != null){
                List<Integer> ll = stringtolist(cc.getNative_screenshots());
                ndp.setScreenshots(ll);
            }
            if(cc.getNative_title() != null){
                ndp.setTitle(cc.getNative_title());
            }
            return ndp.toJson().toString();
        }
        return null;
    }
            
    public static Creative_container getCreative_container(String guid,Connection connection){

        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_container.get_creative_container_by_guid);
            pstmt.setString(1,guid);

            ResultSet rs = pstmt.executeQuery();

            Creative_container cc = null;
            while (rs.next()){
                cc = new Creative_container();
                populate(cc,rs);
                return cc;
            }
        }
        catch(Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }finally{
            if(pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }

        return null;
    }

    public static JsonNode insert_creative_container(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Creative_container cc = objectMapper.treeToValue(jsonNode, Creative_container.class);
            return insert_creative_container(con, cc, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }

    public static Message insert_creative_container(Connection con, Creative_container cc, boolean createTransaction){
        return insert_creative_container(con,cc,createTransaction,false);
    }

    public static Message insert_creative_container(Connection con, Creative_container cc, boolean createTransaction,boolean userProvidedGuid){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(cc == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_container.insert_creative_container,PreparedStatement.RETURN_GENERATED_KEYS);

            if(userProvidedGuid)
                cc.setGuid(cc.getGuid());
            else
                cc.setGuid(SingletonUUIDGenerator.getSingletonUUIDGenerator().generateUniversallyUniqueIdentifier().toString());

            pstmt.setString(1, cc.getGuid());
            pstmt.setString(2, cc.getAccount_guid());
            pstmt.setString(3, cc.getLabel());
            pstmt.setInt(4, cc.getFormat_id());
            pstmt.setString(5, cc.getCreative_attr());
            pstmt.setString(6, cc.getText());
            pstmt.setString(7, convertResourceUriIdsFromUItoDB(cc.getResource_uri_ids()));
            pstmt.setString(8, cc.getHtml_content());
            pstmt.setInt(9, cc.getModified_by());
            pstmt.setTimestamp(10, new Timestamp((new Date()).getTime()));
            pstmt.setInt(11, cc.getStatus_id().getCode());
            if(cc.getFormat_id()==CreativeFormat.RICHMEDIA.getCode()){
                pstmt.setString(12, cc.getExt_resource_url());
            }else{
                pstmt.setString(12, "");
            }
            pstmt.setString(13, generateNativeDemandProps(cc));
            pstmt.setString(14, generateCreativeMacros(cc));
            pstmt.setString(15, generateVideoProps(cc));
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NOT_INSERTED.getId());
                msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NOT_INSERTED.getName());
                return msg;
            }
            ResultSet keyResultSet = pstmt.getGeneratedKeys();
            int cc_id = -1;
            if (keyResultSet.next()) {
                cc_id = keyResultSet.getInt(1);
            }
            cc.setId(cc_id);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.NO_ERROR.getId());
            msg.setMsg(ErrorEnum.NO_ERROR.getName());
            msg.setId(cc_id+"");
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
    
    public static JsonNode update_creative_container(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Creative_container cc = objectMapper.treeToValue(jsonNode, Creative_container.class);
            return update_creative_container(con, cc, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_creative_container(Connection con, Creative_container cc, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(cc == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_container.update_creative_container);
            pstmt.setString(1, cc.getLabel());
            pstmt.setInt(2, cc.getFormat_id());
            pstmt.setString(3, cc.getCreative_attr());
            pstmt.setString(4, cc.getText());
            pstmt.setString(5, convertResourceUriIdsFromUItoDB(cc.getResource_uri_ids()));
            pstmt.setString(6, cc.getHtml_content());
            pstmt.setInt(7, cc.getModified_by());
            pstmt.setTimestamp(8, new Timestamp((new Date()).getTime()));
            pstmt.setInt(9, cc.getStatus_id().getCode());
            if(cc.getFormat_id()==CreativeFormat.RICHMEDIA.getCode()){
                pstmt.setString(10, cc.getExt_resource_url());
            }else{
                pstmt.setString(10, "");
            }
            pstmt.setString(11, generateNativeDemandProps(cc));
            pstmt.setString(12, generateCreativeMacros(cc));
            pstmt.setString(13, generateVideoProps(cc));
            pstmt.setInt(14, cc.getId());
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NOT_UPDATED.getName());
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
 
    public static JsonNode update_creative_container_status(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getName());
            return msg.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CreativeContainerListEntity cc = objectMapper.treeToValue(jsonNode, CreativeContainerListEntity.class);
            return update_creative_container_status(con, cc, true).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            return msg.toJson();
        }
    }
    
    public static Message update_creative_container_status(Connection con, CreativeContainerListEntity cc, boolean createTransaction){
        if(con == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            return msg;
        }
        if(cc == null){
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getName());
            return msg;
        }
        PreparedStatement pstmt = null;
        boolean autoCommitFlag = false;
        try{
            if(createTransaction){
                autoCommitFlag = con.getAutoCommit();
                con.setAutoCommit(false);
            }
            switch(cc.getCcenum()){
            case update_status:
                pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_container.update_status);
                pstmt.setInt(1, cc.getStatus_id().getCode());
                pstmt.setInt(2, cc.getId());
                break;
            case update_multiple_status:
                pstmt = con.prepareStatement(InQueryPrepareStmnt.createInQueryPrepareStatement(
                        com.kritter.kritterui.api.db_query_def.Creative_container.update_multiple_status, "<id>", cc.getId_list(), 
                        ",", false));
                pstmt.setInt(1, cc.getStatus_id().getCode());
               break;
            default:
                break;
            }
            int returnCode = pstmt.executeUpdate();
            if(createTransaction){
                con.commit();
            }
            if(returnCode == 0){
                Message msg = new Message();
                msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NOT_UPDATED.getId());
                msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NOT_UPDATED.getName());
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
 
    
    public static JsonNode various_get_creative_container(Connection con, JsonNode jsonNode){
        if(jsonNode == null){
            CreativeContainerList cclist = new CreativeContainerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getName());
            cclist.setMsg(msg);
            return cclist.toJson();
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            CreativeContainerListEntity cclistEntity = objectMapper.treeToValue(jsonNode, CreativeContainerListEntity.class);
            return various_get_creative_container(con, cclistEntity).toJson();
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CreativeContainerList cclist = new CreativeContainerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.JSON_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.JSON_EXCEPTION.getName());
            cclist.setMsg(msg);
            return cclist.toJson();
        }
    }
    
    public static CreativeContainerList various_get_creative_container(Connection con, CreativeContainerListEntity cclistEntity ){
        if(con == null){
            CreativeContainerList cclist = new CreativeContainerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.Internal_ERROR_1.getId());
            msg.setMsg(ErrorEnum.Internal_ERROR_1.getName());
            cclist.setMsg(msg);
            return cclist;
        }
        if(cclistEntity == null){
            CreativeContainerList cclist = new CreativeContainerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getId());
            msg.setMsg(ErrorEnum.CREATIVE_CONTAINERLIST_ENTITY_NULL.getName());
            cclist.setMsg(msg);
            return cclist;
        }
        PreparedStatement pstmt = null;
        try{
            switch (cclistEntity.getCcenum()){
                case get_creative_container:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_container.get_creative_container);
                    pstmt.setInt(1, cclistEntity.getId());
                    break;
                case list_creative_container_by_account:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_container.list_creative_container_by_account);
                    pstmt.setString(1, cclistEntity.getAccount_guid());
                    pstmt.setInt(2, cclistEntity.getPage_no()*cclistEntity.getPage_size());
                    pstmt.setInt(3, cclistEntity.getPage_size());
                    break;
                case list_creative_container_by_status:
                    pstmt = con.prepareStatement(com.kritter.kritterui.api.db_query_def.Creative_container.list_creative_container_by_status);
                    pstmt.setInt(1, cclistEntity.getStatus_id().getCode());
                    pstmt.setInt(2, cclistEntity.getPage_no()*cclistEntity.getPage_size());
                    pstmt.setInt(3, cclistEntity.getPage_size());
                    break;
                default:
                    break;
            }
            ResultSet rset = pstmt.executeQuery();
            CreativeContainerList  cclist = new CreativeContainerList();
            List<Creative_container> ccs = new LinkedList<Creative_container>();
            while(rset.next()){
                Creative_container cc = new Creative_container();
                populate(cc, rset);
                ccs.add(cc);
            }
            cclist.setCclist(ccs);
            Message msg = new Message();
            if(ccs.size()>0){
                msg.setError_code(ErrorEnum.NO_ERROR.getId());
                msg.setMsg(ErrorEnum.NO_ERROR.getName());
            }else{
                msg.setError_code(ErrorEnum.CREATIVE_CONTAINER_NOT_FOUND.getId());
                msg.setMsg(ErrorEnum.CREATIVE_CONTAINER_NOT_FOUND.getName());
            }
            cclist.setMsg(msg);
            return cclist;
        }catch(Exception e){
            LOG.error(e.getMessage(),e);
            CreativeContainerList  cclist = new CreativeContainerList();
            Message msg = new Message();
            msg.setError_code(ErrorEnum.SQL_EXCEPTION.getId());
            msg.setMsg(ErrorEnum.SQL_EXCEPTION.getName());
            cclist.setMsg(msg);
            return cclist;
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
