package com.kritter.naterial_upload.youku.video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialType;
import com.kritter.entity.video_props.VideoInfoExt;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.material_upload.common.video.MUVideo;
import com.kritter.material_upload.youkuvideouploader.YoukuNonWebVideoUploader;
import com.kritter.naterial_upload.youku.entity.ReturnResultCode;
import com.kritter.naterial_upload.youku.entity.YoukuMaterialUploadEntity;
import com.kritter.naterial_upload.youku.entity.YoukuMultipleMaterialUploadEntity;
import com.kritter.naterial_upload.youku.entity.YoukuQueryEntity;
import com.kritter.naterial_upload.youku.entity.YoukuVideoLocalMaterialUploadEntity;

import lombok.Getter;
import lombok.Setter;

public class YoukuMUVideo implements MUVideo {
	private static final Logger LOG = LoggerFactory.getLogger(YoukuMUVideo.class);
	@Getter@Setter
	private String dspid;
	@Getter @Setter
	private String token;
	@Getter @Setter
	private Integer pubIncId;
	@Getter @Setter
	private boolean performTransaction =true;
	@Getter @Setter
	private Date dateNow;
	@Getter @Setter
	private Date startDate;
	@Getter @Setter
	private String startDateStr;
	@Getter @Setter
	private boolean lastRunPresent=false;
	@Getter @Setter
	private LinkedList<YoukuQueryEntity> youkuQueryEntityList;


	@Override
	public void init(Properties properties) {
		setDspid(properties.getProperty("youku_dsp_id").toString());
		setToken(properties.getProperty("youku_token").toString());
		setPubIncId(Integer.parseInt(properties.getProperty("youku_pubIncId").toString()));
		dateNow = new Date();
	}

	@Override
	public void getLastRun(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		PreparedStatement pstmt = null;
		try{
			pstmt = con.prepareStatement("select * from material_upload_state where pubIncId=? and materialtype="+MaterialType.VIDEOUPLOAD.getCode());
			pstmt.setInt(1, getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			if(rset.next()){
				startDate = new Date(rset.getTimestamp("last_modified").getTime());
				lastRunPresent=true;
				LOG.debug("PubIncId {} found DB", pubIncId );
			}else{
				startDate = new Date();
				startDate=DateUtils.addDays(startDate, 0-Integer.parseInt(properties.getProperty("youku_init_date_go_back").toString()));
			}
	        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String s = dfm.format(startDate);
	        LOG.info("Start Date for Video Upload {}",s);
	        setStartDateStr(s);
		}catch(Exception e){
			setPerformTransaction(false);
			LOG.error(e.getMessage(),e);
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

	@Override
	public void getModifiedEntities(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		PreparedStatement pstmt = null;
		PreparedStatement cpstmt = null;
		try{
			pstmt = con.prepareStatement(YoukuVideoQuery.selectQuery);
			pstmt.setString(1, getStartDateStr());
			ResultSet rset = pstmt.executeQuery();
			boolean isFirst = true;
			while(rset.next()){
				if(isFirst){
					youkuQueryEntityList = new LinkedList<YoukuQueryEntity>();
					isFirst=false;
				}
				String video_props=rset.getString("video_props");
				int creativeId=rset.getInt("creativeId");
				try{
					VideoProps videoProps=VideoProps.getObject(video_props);
					if(videoProps != null && videoProps.getVideo_info() != null && videoProps.getVideo_info().length==1){
						String split[] = videoProps.getVideo_info()[0].replaceAll("\\[", "").replaceAll("]", "").split(",");
						int videoInfoId= Integer.parseInt(split[0]);
						cpstmt=con.prepareStatement(YoukuVideoQuery.getVideoInfo);
						cpstmt.setInt(1, videoInfoId);
						ResultSet cpRset = cpstmt.executeQuery();
						if(cpRset.next()){
							String resource_uri = cpRset.getString("resource_uri");
							if(resource_uri !=null){
								YoukuQueryEntity yqe = new YoukuQueryEntity();
								yqe.setVideoInfoId(videoInfoId);

								yqe.setAdvId(rset.getInt("advId"));
								yqe.setAdvName(rset.getString("advName"));
								yqe.setCampaignId(rset.getInt("campaignId"));
								yqe.setCampaignName(rset.getString("campaignName"));
								yqe.setCampaignStartDate(rset.getTimestamp("campaignStartDate").getTime());
								yqe.setCampaignEndDate(rset.getTimestamp("campaignEndDate").getTime());
								yqe.setCampaignStatus(rset.getInt("campaignStatus"));
								yqe.setAdId(rset.getInt("adId"));
								yqe.setAdName(rset.getString("adName"));
								yqe.setAdStatus(rset.getInt("adStatus"));
								yqe.setLanding_url(rset.getString("landing_url"));
								yqe.setCreativeId(creativeId);
								yqe.setCreativeName(rset.getString("creativeName"));
								yqe.setCreativeStatus(rset.getInt("creativeStatus"));
								yqe.setResource_uri(resource_uri);;
								youkuQueryEntityList.add(yqe);
							}
						}
					}else{
						LOG.info("video info video_props is of incorrect size: creative container {}",creativeId);
					}
				}catch(Exception e1){
					LOG.error(e1.getMessage(),e1);
				}
			}
		}catch(Exception e){
			setPerformTransaction(false);
			LOG.error(e.getMessage(),e);
		}finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
			if(cpstmt != null){
				try {
					cpstmt.close();
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
	@Override
	public void insertOrUpdateVideoUpload(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		if(youkuQueryEntityList == null){
			return;
		}
		for(YoukuQueryEntity yqe:youkuQueryEntityList){
			if(yqe == null){
				continue;
			}
			PreparedStatement pstmt = null;
			PreparedStatement cpstmt = null;
			try{
				pstmt = con.prepareStatement(YoukuVideoQuery.getVideoUpload);
				pstmt.setInt(1,getPubIncId());
				pstmt.setInt(2,yqe.getAdvId());
				pstmt.setInt(3,yqe.getCampaignId());
				pstmt.setInt(4,yqe.getAdId());
				pstmt.setInt(5,yqe.getCreativeId());
				pstmt.setInt(6,yqe.getVideoInfoId());
				ResultSet rset = pstmt.executeQuery();
		        String split[] = yqe.getResource_uri().split("/");
		        String materialurl=properties.getProperty("cdn_url").toString()+split[split.length-1];
		        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		        String campaignStartDate = dfm.format(new Date(yqe.getCampaignStartDate()));
		        String campaignEndDate = dfm.format(new Date(yqe.getCampaignEndDate()));
				if(rset.next()){
					String info=rset.getString("info");
					YoukuVideoLocalMaterialUploadEntity oldymue=YoukuVideoLocalMaterialUploadEntity.getObject(info);
					int adxbasedexhangesstatus = rset.getInt("adxbasedexhangesstatus");
					YoukuVideoLocalMaterialUploadEntity ymue=new YoukuVideoLocalMaterialUploadEntity(null,materialurl, yqe.getLanding_url(), 
							yqe.getAdvName(), campaignStartDate, campaignEndDate, null,yqe.getCreativeName(),yqe.getResource_uri(),
							yqe.getVideoInfoId());
					String newInfoStr = ymue.toJson().toString();
					cpstmt = con.prepareStatement(YoukuVideoQuery.updatetVideoUpload);
					cpstmt.setInt(2, yqe.getCampaignStatus());
					cpstmt.setInt(3, yqe.getAdStatus());
					cpstmt.setInt(4, yqe.getCreativeStatus());
					pstmt.setTimestamp(5, new Timestamp(dateNow.getTime()));
					if(YoukuVideoLocalMaterialUploadEntity.equalityWithoutYoukuUrl(oldymue, ymue)){
						cpstmt.setInt(1, adxbasedexhangesstatus);
						cpstmt.setString(6, info);
					}else{
						cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
						cpstmt.setString(6, newInfoStr);
					}
					cpstmt.setInt(7, rset.getInt("internalid"));
					cpstmt.executeUpdate();
					
				}else{
					cpstmt = con.prepareStatement(YoukuVideoQuery.insertVideoUpload);
					cpstmt.setInt(1, getPubIncId());
					cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
					cpstmt.setInt(3, yqe.getAdvId());
					cpstmt.setInt(4, yqe.getCampaignId());
					cpstmt.setInt(5, yqe.getCampaignStatus());
					cpstmt.setInt(6, yqe.getAdId());
					cpstmt.setInt(7, yqe.getAdStatus());
					cpstmt.setInt(8, yqe.getCreativeId());
					cpstmt.setInt(9, yqe.getCreativeStatus());
					cpstmt.setInt(10, yqe.getVideoInfoId());
					cpstmt.setTimestamp(11, new Timestamp(dateNow.getTime()));
					YoukuVideoLocalMaterialUploadEntity ymue=new YoukuVideoLocalMaterialUploadEntity(null,materialurl, yqe.getLanding_url(), 
							yqe.getAdvName(), campaignStartDate, campaignEndDate, null,yqe.getCreativeName(),yqe.getResource_uri(),
							yqe.getVideoInfoId());
					String newInfoStr = ymue.toJson().toString();
					cpstmt.setString(12, newInfoStr);
					cpstmt.executeUpdate();
				}
			}catch(Exception e){
				setPerformTransaction(false);
				LOG.error(e.getMessage(),e);
			}finally{
				if(pstmt != null){
					try {
						pstmt.close();
					} catch (SQLException e) {
						LOG.error(e.getMessage(),e);
					}
					if(cpstmt != null){
						try {
							cpstmt.close();
						} catch (SQLException e) {
							LOG.error(e.getMessage(),e);
						}
					}
				}
			}

		}
	}



	@Override
	public void uploadmaterial(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		LOG.info("UPLOADING MATERIAL");
		PreparedStatement pstmt = null;
		PreparedStatement cpstmt = null;
		PreparedStatement cpstmt1 = null;
		PreparedStatement cpstmt2 = null;
		PreparedStatement cpstmt3 = null;
		try{
			pstmt = con.prepareStatement(YoukuVideoQuery.selectforUpload);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			List<YoukuMaterialUploadEntity> materialList = new LinkedList<YoukuMaterialUploadEntity>();
			StringBuffer sBuff = new StringBuffer("");
			boolean isFirst=true;
			while(rset.next()){
				//System.out.println(rset.getString("info"));
				YoukuVideoLocalMaterialUploadEntity localEntity = YoukuVideoLocalMaterialUploadEntity.getObject(rset.getString("info"));
				if(localEntity.getYoukuurl()==null || localEntity.getYoukuurl().equals("")){
					YoukuNonWebVideoUploader ynvu = new YoukuNonWebVideoUploader();
					ynvu.upload(properties.getProperty("youku_clientId").toString(), 
							properties.getProperty("youku_clientSecret").toString(), 
							properties.getProperty("youku_refreshToken").toString(), 
							properties.getProperty("youku_token_url").toString(), 
							properties.getProperty("file_path_prefix").toString()+localEntity.getResource_uri(), 
							localEntity.getCreativeName(),localEntity.getCreativeName());
					if(ynvu.getVideoid()!=null && !ynvu.getVideoid().equals("")){
						String youkuUrl = properties.getProperty("youku_video_url").toString().replaceAll("<videoid>", ynvu.getVideoid());
						localEntity.setYoukuurl(youkuUrl);
						cpstmt2 = con.prepareStatement(YoukuVideoQuery.getVideoInfo);
						cpstmt2.setInt(1, localEntity.getVideoInfoId());
						ResultSet cpstmt2Rset = cpstmt2.executeQuery();
						if(cpstmt2Rset.next()){
							VideoInfoExt viext = VideoInfoExt.getObject(rset.getString("ext"));
							if(viext ==null){
								viext=new VideoInfoExt();
							}
							viext.setYoukuCDNUrl(youkuUrl);
							cpstmt3 = con.prepareStatement(YoukuVideoQuery.update_video_info);
							cpstmt3.setTimestamp(1, new Timestamp(dateNow.getTime()));
							cpstmt3.setString(2, viext.toJson().toString());
							cpstmt3.setInt(3, localEntity.getVideoInfoId());
							cpstmt3.executeUpdate();
						}
					}else{
						LOG.info("NOTUPLOADED TO YOUKU CDN {} ",localEntity.getCreativeName());
					}
				}
				if(localEntity.getYoukuurl()!=null && localEntity.getYoukuurl().equals("")){
					materialList.add(YoukuVideoLocalMaterialUploadEntity.createEntityforUpload(localEntity));
				}
				if(isFirst){
					isFirst=false;
				}else{
					sBuff.append(",");
				}
				sBuff.append(rset.getInt("internalId"));
			}
			if(materialList.size()>0){
				boolean isSuccess=false;
				try{
					
					YoukuMultipleMaterialUploadEntity ymmue = new YoukuMultipleMaterialUploadEntity();
					ymmue.setMaterial(materialList);
					ymmue.setDspid(properties.getProperty("youku_dsp_id").toString());
					ymmue.setToken(properties.getProperty("youku_token").toString());
					LOG.info("MATERIAL VIDEO UPLOAD POSTBODY");
					String postBody = ymmue.toJson().toString();
					LOG.info(postBody);
					
					UrlPost urlPost = new UrlPost();
					String out = urlPost.urlpost(properties.getProperty("youku_url_prefix").toString()+
							properties.getProperty("youku_prefix_video_upload"), postBody);
					LOG.info("MATERIAL VIDEO UPLOAD RESPONSE");
					LOG.info(out);
					if(out != null){
						ReturnResultCode rrc = ReturnResultCode.getObject(out);
						if(rrc.getResult()==0){
							cpstmt = con.prepareStatement(YoukuVideoQuery.updatetVideoStatus.replaceAll("<id>", sBuff.toString()));
							cpstmt.setInt(1,AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
							cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
							cpstmt.executeUpdate();
							isSuccess=true;
						}
					}
				}catch(Exception e1){
					LOG.error(e1.getMessage(),e1);
				}
				if(!isSuccess){
					cpstmt1 = con.prepareStatement(YoukuVideoQuery.updatetVideoStatus.replaceAll("<id>", sBuff.toString()));
					cpstmt1.setInt(1,AdxBasedExchangesStates.UPLOADFAIL.getCode());
					cpstmt1.setTimestamp(2, new Timestamp(dateNow.getTime()));
					cpstmt1.executeUpdate();
				}
			}
		}catch(Exception e){
			setPerformTransaction(false);
			LOG.error(e.getMessage(),e);
		}finally{
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
				if(cpstmt != null){
					try {
						cpstmt.close();
					} catch (SQLException e) {
						LOG.error(e.getMessage(),e);
					}
				}
				if(cpstmt1 != null){
					try {
						cpstmt1.close();
					} catch (SQLException e) {
						LOG.error(e.getMessage(),e);
					}
				}
				if(cpstmt2 != null){
					try {
						cpstmt2.close();
					} catch (SQLException e) {
						LOG.error(e.getMessage(),e);
					}
				}
				if(cpstmt3 != null){
					try {
						cpstmt3.close();
					} catch (SQLException e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}
		}

	}
	@Override
	public void updaterun(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		PreparedStatement pstmt = null;
		try{
			if(lastRunPresent){
				pstmt = con.prepareStatement(YoukuVideoQuery.update_material_state);
				pstmt.setTimestamp(1, new Timestamp(dateNow.getTime()));
				pstmt.setInt(2, MaterialType.VIDEOUPLOAD.getCode());
				pstmt.setInt(3, getPubIncId());
			}else{
				pstmt = con.prepareStatement(YoukuVideoQuery.insert_material_state);
				pstmt.setInt(1, getPubIncId());
				pstmt.setInt(2, MaterialType.VIDEOUPLOAD.getCode());
				pstmt.setTimestamp(3, new Timestamp(dateNow.getTime()));
			}
			pstmt.executeUpdate();
		}catch(Exception e){
			setPerformTransaction(false);
			LOG.error(e.getMessage(),e);
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
