package com.kritter.naterial_upload.valuemaker.video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.kritter.naterial_upload.valuemaker.entity.VamCreative;
import com.kritter.naterial_upload.valuemaker.entity.VamQueryEntity;
import com.kritter.naterial_upload.valuemaker.entity.VamVideoMaterialUploadEntity;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialType;
import com.kritter.entity.video_props.VideoProps;
import com.kritter.material_upload.common.video.MUVideo;



import lombok.Getter;
import lombok.Setter;

public class VamMUVideo implements MUVideo {
	private static final Logger LOG = LoggerFactory.getLogger(VamMUVideo.class);
	@Getter@Setter
	private String dspid;
	@Getter @Setter
	private String username;
	@Getter @Setter
	private String password;
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
	private LinkedList<VamQueryEntity> vamQueryEntityList;
	@Getter @Setter
	private Map<String,String> header;
	private VamCreative vamCreative;


	@Override
	public void init(Properties properties) {
		setDspid(properties.getProperty("vam_dsp_id").toString());
		setPubIncId(Integer.parseInt(properties.getProperty("vam_pubIncId").toString()));
		this.dateNow = new Date();
		this.header = new HashMap<String,String>();
		setUsername(properties.getProperty("vam_username").toString());
		setPassword(properties.getProperty("vam_password").toString());
		String vam_url_prefix = properties.getProperty("vam_url_prefix").toString();
		String vam_prefix_video_add = vam_url_prefix + properties.getProperty("vam_prefix_video_add").toString();
		this.vamCreative = new VamCreative(vam_prefix_video_add,null);

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
				startDate=DateUtils.addDays(startDate, 0-Integer.parseInt(properties.getProperty("vam_init_date_go_back").toString()));
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
	public void removeDisassociatedCreative(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		PreparedStatement pstmt = null;
		PreparedStatement secondSelectstmt = null;
		PreparedStatement updatestmt = null;
		try{
			pstmt = con.prepareStatement(VamVideoQuery.removedCreatives);
			//pstmt.setString(1, getStartDateStr());
			ResultSet rset = pstmt.executeQuery();
			while(rset.next()){
				int internalid=rset.getInt("internalid");
				int creativeId=rset.getInt("creativeId");
				int videoInfoId=rset.getInt("videoInfoId");
				secondSelectstmt = con.prepareStatement(VamVideoQuery.getCreativeContainer);
				secondSelectstmt.setInt(1,creativeId);
				ResultSet secondRset = secondSelectstmt.executeQuery();
				if(secondRset.next()){
					boolean found =false;
					String video_props = secondRset.getString("video_props");
					try{
						if(video_props != null && !video_props.isEmpty()){
							VideoProps vProps = VideoProps.getObject(video_props);
							if(vProps.getVideo_info() != null && vProps.getVideo_info().length>0){
								for(String s:vProps.getVideo_info() ){
									if(s.equals(videoInfoId+"")){
										found=true;
										break;
									}
								}
							}
						}
					}catch(Exception e1){
						LOG.error(e1.getMessage(),e1);
					}
					if(!found){
						updatestmt = con.prepareStatement(VamVideoQuery.updateRemovedCreatives);
						updatestmt.setInt(1, internalid);
						updatestmt.executeUpdate();
					}

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
			if(secondSelectstmt != null){
				try {
					secondSelectstmt.close();
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
			if(updatestmt != null){
				try {
					updatestmt.close();
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
			pstmt = con.prepareStatement(VamVideoQuery.selectQuery);
			pstmt.setString(1, getStartDateStr());
			ResultSet rset = pstmt.executeQuery();
			boolean isFirst = true;
			while(rset.next()){
				if(isFirst){
					vamQueryEntityList = new LinkedList<VamQueryEntity>();
					isFirst=false;
				}
				String video_props=rset.getString("video_props");
				int creativeId=rset.getInt("creativeId");
				try{
					VideoProps videoProps=VideoProps.getObject(video_props);
					if(videoProps != null && videoProps.getVideo_info() != null && videoProps.getVideo_info().length==1){
						String split[] = videoProps.getVideo_info()[0].replaceAll("\\[", "").replaceAll("]", "").split(",");

						Integer width = videoProps.getWidth();
						Integer height = videoProps.getHeight();
						Integer duration = videoProps.getDuration();
						
						int videoInfoId= Integer.parseInt(split[0]);
						
						cpstmt=con.prepareStatement(VamVideoQuery.getVideoInfo);
						cpstmt.setInt(1, videoInfoId);
						ResultSet cpRset = cpstmt.executeQuery();
						if(cpRset.next()){
							String resource_uri = cpRset.getString("resource_uri");
							String creativeGuid = cpRset.getString("guid");
							if(resource_uri !=null){
								VamQueryEntity vqe = new VamQueryEntity();
								vqe.setVideoInfoId(videoInfoId);

								vqe.setAdvId(rset.getInt("advId"));
								vqe.setAdvName(rset.getString("advName"));
								vqe.setCampaignId(rset.getInt("campaignId"));
								vqe.setCampaignName(rset.getString("campaignName"));
								vqe.setCampaignStartDate(rset.getTimestamp("campaignStartDate").getTime());
								vqe.setCampaignEndDate(rset.getTimestamp("campaignEndDate").getTime());
								vqe.setCampaignStatus(rset.getInt("campaignStatus"));
								vqe.setAdId(rset.getInt("adId"));
								vqe.setAdName(rset.getString("adName"));
								vqe.setAdStatus(rset.getInt("adStatus"));
								vqe.setLanding_url(rset.getString("landing_url"));
								vqe.setCreativeId(creativeId);
								vqe.setCreativeName(rset.getString("creativeName"));
								vqe.setCreativeStatus(rset.getInt("creativeStatus"));
								vqe.setResource_uri(resource_uri);
								vqe.setWidth(width);
								vqe.setHeight(height);
								vqe.setDuration(duration);
								vqe.setCreativeGuid(creativeGuid);
								vamQueryEntityList.add(vqe);
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
		if(vamQueryEntityList == null){
			return;
		}
		for(VamQueryEntity vqe:vamQueryEntityList){
			if(vqe == null){
				continue;
			}
			PreparedStatement pstmt = null;
			PreparedStatement cpstmt = null;
			try{
				pstmt = con.prepareStatement(VamVideoQuery.getVideoUpload);
				pstmt.setInt(1,getPubIncId());
				pstmt.setInt(2,vqe.getAdvId());
				pstmt.setInt(3,vqe.getCampaignId());
				pstmt.setInt(4,vqe.getAdId());
				pstmt.setInt(5,vqe.getCreativeId());
				pstmt.setInt(6,vqe.getVideoInfoId());
				ResultSet rset = pstmt.executeQuery();
		        String split[] = vqe.getResource_uri().split("/");
		        String materialurl=properties.getProperty("cdn_url").toString()+split[split.length-1];
		        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		        String campaignStartDate = dfm.format(new Date(vqe.getCampaignStartDate()));
		        String campaignEndDate = dfm.format(new Date(vqe.getCampaignEndDate()));

				java.net.URL url = new java.net.URL(vqe.getLanding_url());
				String host = url.getHost();
				String[] Adomain_list = {host};

				VamVideoMaterialUploadEntity videoEntity = new VamVideoMaterialUploadEntity(vqe.getCreativeGuid(),1,
						"{!vam_click_url}{!dsp_click_url}"+vqe.getLanding_url(),Adomain_list,vqe.getWidth(),
						vqe.getHeight(),1,vqe.getDuration(),materialurl,null,0);
				String newInfoStr = videoEntity.toJson().toString();

				if(rset.next()){
					String info=rset.getString("info");
					int adxbasedexhangesstatus = rset.getInt("adxbasedexhangesstatus");
//							String id, Integer category, String landingpage,String[] adomain_list,Integer width,Integer height,
//							Integer format,Integet duration,String fileurl,String advertiser,Integer creative_type)
					cpstmt = con.prepareStatement(VamVideoQuery.updatetVideoUpload);
					cpstmt.setInt(2, vqe.getCampaignStatus());
					cpstmt.setInt(3, vqe.getAdStatus());
					cpstmt.setInt(4, vqe.getCreativeStatus());
					cpstmt.setTimestamp(5, new Timestamp(dateNow.getTime()));
					if(newInfoStr.equals(info)){
						cpstmt.setInt(1, adxbasedexhangesstatus);
						cpstmt.setString(6, info);
					}else{
						cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
						cpstmt.setString(6, newInfoStr);
					}
					cpstmt.setInt(7, rset.getInt("internalid"));
					System.out.println(cpstmt);
					cpstmt.executeUpdate();

				}else{
					cpstmt = con.prepareStatement(VamVideoQuery.insertVideoUpload);
					cpstmt.setInt(1, getPubIncId());
					cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
					cpstmt.setInt(3, vqe.getAdvId());
					cpstmt.setInt(4, vqe.getCampaignId());
					cpstmt.setInt(5, vqe.getCampaignStatus());
					cpstmt.setInt(6, vqe.getAdId());
					cpstmt.setInt(7, vqe.getAdStatus());
					cpstmt.setInt(8, vqe.getCreativeId());
					cpstmt.setInt(9, vqe.getCreativeStatus());
					cpstmt.setInt(10, vqe.getVideoInfoId());
					cpstmt.setTimestamp(11, new Timestamp(dateNow.getTime()));
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
		try{
			pstmt = con.prepareStatement(VamVideoQuery.selectforUpload);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			List<VamVideoMaterialUploadEntity> materialList = new LinkedList<VamVideoMaterialUploadEntity>();
			StringBuffer sBuff = new StringBuffer("");
			boolean isFirst=true;
			header.put("Content-Type", "application/json;charset=utf-8");
			header.put("Authorization", "Basic " + vamCreative.authStringEnc(this.username,this.password));
			while(rset.next()){
				//System.out.println(rset.getString("info"));
				materialList.add(VamVideoMaterialUploadEntity.getObject(rset.getString("info")));
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
					for(VamVideoMaterialUploadEntity vamVideoMaterialUploadEntity:materialList){
						Integer out = vamCreative.addVideo(vamVideoMaterialUploadEntity,header);
						LOG.info("MATERIAL BANNER UPLOAD RESPONSE");
						LOG.info(String.valueOf(out));
						if(out != null && out == 200){
							cpstmt = con.prepareStatement(VamVideoQuery.updatetVideoStatus.replaceAll("<id>", sBuff.toString()));
							cpstmt.setInt(1,AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
							cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
							cpstmt.executeUpdate();
							isSuccess=true;
						}
						if(!isSuccess){
							cpstmt1 = con.prepareStatement(VamVideoQuery.updatetVideoStatus.replaceAll("<id>", sBuff.toString()));
							cpstmt1.setInt(1,AdxBasedExchangesStates.UPLOADFAIL.getCode());
							cpstmt1.setTimestamp(2, new Timestamp(dateNow.getTime()));
							cpstmt1.executeUpdate();
						}
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
				pstmt = con.prepareStatement(VamVideoQuery.update_material_state);
				pstmt.setTimestamp(1, new Timestamp(dateNow.getTime()));
				pstmt.setInt(2, MaterialType.VIDEOUPLOAD.getCode());
				pstmt.setInt(3, getPubIncId());
			}else{
				pstmt = con.prepareStatement(VamVideoQuery.insert_material_state);
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
