package com.kritter.naterial_upload.youku.banner;

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
import com.kritter.material_upload.common.banner.MUBanner;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.naterial_upload.youku.entity.ReturnResultCode;
import com.kritter.naterial_upload.youku.entity.ReturnResultMessage;
import com.kritter.naterial_upload.youku.entity.YoukuMaterialUploadEntity;
import com.kritter.naterial_upload.youku.entity.YoukuMultipleMaterialUploadEntity;
import com.kritter.naterial_upload.youku.entity.YoukuQueryEntity;

import lombok.Getter;
import lombok.Setter;

public class YoukuMUBanner implements MUBanner {
	private static final Logger LOG = LoggerFactory.getLogger(YoukuMUBanner.class);
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
			pstmt = con.prepareStatement("select * from material_upload_state where pubIncId=? and materialtype="+MaterialType.BANNERUPLOAD.getCode());
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
	        LOG.info("Start Date for Banner Upload {}",s);
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
		PreparedStatement updatestmt = null;
		try{
			pstmt = con.prepareStatement(YoukuBannerQuery.removedCreativesQuery);
			pstmt.setString(1, getStartDateStr());
			ResultSet rset = pstmt.executeQuery();
			while(rset.next()){
				updatestmt=con.prepareStatement(YoukuBannerQuery.updateRemovedCreatives);
				updatestmt.setInt(1, rset.getInt("internalid"));
				updatestmt.executeUpdate();
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
		try{
			pstmt = con.prepareStatement(YoukuBannerQuery.selectQuery);
			pstmt.setString(1, getStartDateStr());
			ResultSet rset = pstmt.executeQuery();
			boolean isFirst = true;
			while(rset.next()){
				if(isFirst){
					youkuQueryEntityList = new LinkedList<YoukuQueryEntity>();
					isFirst=false;
				}
				YoukuQueryEntity yqe = new YoukuQueryEntity();
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
				yqe.setCreativeId(rset.getInt("creativeId"));
				yqe.setCreativeName(rset.getString("creativeName"));
				yqe.setCreativeStatus(rset.getInt("creativeStatus"));
				yqe.setResource_uri_ids(rset.getString("resource_uri_ids"));
				yqe.setBannerId(rset.getInt("bannerId"));
				yqe.setResource_uri(rset.getString("resource_uri"));
				youkuQueryEntityList.add(yqe);
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
		}
	}
	@Override
	public void insertOrUpdateBannerUpload(Properties properties, Connection con) {
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
				pstmt = con.prepareStatement(YoukuBannerQuery.getBannerUpload);
				pstmt.setInt(1,getPubIncId());
				pstmt.setInt(2,yqe.getAdvId());
				pstmt.setInt(3,yqe.getCampaignId());
				pstmt.setInt(4,yqe.getAdId());
				pstmt.setInt(5,yqe.getCreativeId());
				pstmt.setInt(6,yqe.getBannerId());
				ResultSet rset = pstmt.executeQuery();
		        String split[] = yqe.getResource_uri().split("/");
		        String materialurl=properties.getProperty("cdn_url").toString()+split[split.length-1];
		        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		        String campaignStartDate = dfm.format(new Date(yqe.getCampaignStartDate()));
		        String campaignEndDate = dfm.format(new Date(yqe.getCampaignEndDate()));
				if(rset.next()){
					String info=rset.getString("info");
					int adxbasedexhangesstatus = rset.getInt("adxbasedexhangesstatus");
					YoukuMaterialUploadEntity ymue=new YoukuMaterialUploadEntity(materialurl, yqe.getLanding_url(), yqe.getAdvName(), campaignStartDate, campaignEndDate, null);
					String newInfoStr = ymue.toJson().toString();
					cpstmt = con.prepareStatement(YoukuBannerQuery.updatetBannerUpload);
					if(newInfoStr.equals(info)){
						cpstmt.setInt(1, adxbasedexhangesstatus);
					}else{
						cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
					}
					cpstmt.setInt(2, yqe.getCampaignStatus());
					cpstmt.setInt(3, yqe.getAdStatus());
					cpstmt.setInt(4, yqe.getCreativeStatus());
					cpstmt.setTimestamp(5, new Timestamp(dateNow.getTime()));
					if(newInfoStr.equals(info)){
						cpstmt.setString(6, info);
					}else{
						cpstmt.setString(6, newInfoStr);
					}
					cpstmt.setInt(7, rset.getInt("internalid"));
					System.out.println(cpstmt);
					cpstmt.executeUpdate();
					
				}else{
					cpstmt = con.prepareStatement(YoukuBannerQuery.insertBannerUpload);
					cpstmt.setInt(1, getPubIncId());
					cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
					cpstmt.setInt(3, yqe.getAdvId());
					cpstmt.setInt(4, yqe.getCampaignId());
					cpstmt.setInt(5, yqe.getCampaignStatus());
					cpstmt.setInt(6, yqe.getAdId());
					cpstmt.setInt(7, yqe.getAdStatus());
					cpstmt.setInt(8, yqe.getCreativeId());
					cpstmt.setInt(9, yqe.getCreativeStatus());
					cpstmt.setInt(10, yqe.getBannerId());
					cpstmt.setTimestamp(11, new Timestamp(dateNow.getTime()));
					YoukuMaterialUploadEntity ymue=new YoukuMaterialUploadEntity(materialurl, yqe.getLanding_url(), yqe.getAdvName(), campaignStartDate, campaignEndDate, null);
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
		try{
			pstmt = con.prepareStatement(YoukuBannerQuery.selectforUpload);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			while(rset.next()){
				//System.out.println(rset.getString("info"));
				List<YoukuMaterialUploadEntity> materialList = new LinkedList<YoukuMaterialUploadEntity>();
				materialList.add(YoukuMaterialUploadEntity.getObject(rset.getString("info")));
				int internalId =  rset.getInt("internalId");
				boolean isSuccess=false;
				String out=null;
				try{
					YoukuMultipleMaterialUploadEntity ymmue = new YoukuMultipleMaterialUploadEntity();
					ymmue.setMaterial(materialList);
					ymmue.setDspid(properties.getProperty("youku_dsp_id").toString());
					ymmue.setToken(properties.getProperty("youku_token").toString());
					LOG.info("MATERIAL BANNER UPLOAD POSTBODY");
					String postBody = ymmue.toJson().toString();
					LOG.info(postBody);
					
					UrlPost urlPost = new UrlPost();
					out = urlPost.urlpost(properties.getProperty("youku_url_prefix").toString()+
							properties.getProperty("youku_prefix_banner_upload"), postBody);
					LOG.info("MATERIAL BANNER UPLOAD RESPONSE");
					LOG.info(out);
					if(out != null){
						ReturnResultCode rrc = ReturnResultCode.getObject(out);
						if(rrc.getResult()==0){
							ReturnResultMessage rrm = ReturnResultMessage.getObject(out);
							if(rrm.getResult()==0 && (rrm.getMessage() == null || rrm.getMessage().size()<1 )){
								cpstmt = con.prepareStatement(YoukuBannerQuery.updatetBannerStatus.replaceAll("<id>", internalId+""));
								cpstmt.setInt(1,AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
								cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
								cpstmt.setString(3, "");
								cpstmt.executeUpdate();
								isSuccess=true;
							}
						}
					}
				}catch(Exception e1){
					LOG.error(e1.getMessage(),e1);
				}
				if(!isSuccess){
					cpstmt1 = con.prepareStatement(YoukuBannerQuery.updatetBannerStatus.replaceAll("<id>", internalId+""));
					cpstmt1.setInt(1,AdxBasedExchangesStates.UPLOADFAIL.getCode());
					cpstmt1.setTimestamp(2, new Timestamp(dateNow.getTime()));
					if(out ==null){
						out="";
					}
					cpstmt1.setString(3, out);
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
				pstmt = con.prepareStatement(YoukuBannerQuery.update_material_state);
				pstmt.setTimestamp(1, new Timestamp(dateNow.getTime()));
				pstmt.setInt(2, MaterialType.BANNERUPLOAD.getCode());
				pstmt.setInt(3, getPubIncId());
			}else{
				pstmt = con.prepareStatement(YoukuBannerQuery.insert_material_state);
				pstmt.setInt(1, getPubIncId());
				pstmt.setInt(2, MaterialType.BANNERUPLOAD.getCode());
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
