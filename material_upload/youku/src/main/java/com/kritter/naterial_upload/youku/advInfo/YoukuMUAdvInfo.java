package com.kritter.naterial_upload.youku.advInfo;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialType;
import com.kritter.constants.QualificationState;
import com.kritter.material_upload.common.advInfo.MUAdvInfo;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.naterial_upload.youku.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class YoukuMUAdvInfo implements MUAdvInfo {
	private static final Logger LOG = LogManager.getLogger("material.root");
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
	private Map<Integer, YoukuAdvInfoLocaLMaterialUploadEntity> queryMap;


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
			pstmt = con.prepareStatement("select * from material_upload_state where pubIncId=? and materialtype="+MaterialType.ADVINFOUPLOAD.getCode());
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
	        LOG.info("Start Date for AdvInfo Upload {}",s);
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
		try{
			pstmt = con.prepareStatement(YoukuAdvInfoQuery.selectQuery);
			pstmt.setInt(1, getPubIncId());
			pstmt.setInt(2, getPubIncId());
			pstmt.setString(3, getStartDateStr());
			pstmt.setString(4, getStartDateStr());
			ResultSet rset = pstmt.executeQuery();
			boolean isFirst = true;
			while(rset.next()){
				if(isFirst){
					queryMap = new HashMap<Integer, YoukuAdvInfoLocaLMaterialUploadEntity>();
					isFirst=false;
				}
				Integer advIncId = rset.getInt("advId");
				YoukuAdvInfoLocaLMaterialUploadEntity yailmue = queryMap.get(advIncId);
				if(yailmue == null){
					yailmue = new YoukuAdvInfoLocaLMaterialUploadEntity();
				}
				yailmue.setAddress(rset.getString("address"));
				yailmue.setBrand(rset.getString("brand"));
				yailmue.setContacts(rset.getString("contactdetail"));
				yailmue.setName(rset.getString("advName"));
				yailmue.setTel(rset.getString("phone"));
				if(rset.getObject("firstind") != null){
					yailmue.setFirstindustry(Integer.parseInt(rset.getString("firstind")));
				}
				if(rset.getObject("secondind") != null){
					yailmue.setSecondindustry(Integer.parseInt(rset.getString("secondind")));
				}
				List<YoukuQualifications> qualifications = yailmue.getQualifications();
				if(qualifications ==null){
					qualifications = new LinkedList<YoukuQualifications>();
				}
				if(rset.getObject("qname") != null 
						&& rset.getObject("qurl") != null && rset.getObject("qmd5") != null){
					YoukuQualifications qualification = new YoukuQualifications();
					qualification.setMd5(rset.getString("qmd5"));
					qualification.setName(rset.getString("qname"));
					qualification.setOperation(QualificationState.getEnum(rset.getInt("qstate")).getName());
					String materialurl=properties.getProperty("cdn_url").toString();
			        String split[] = rset.getString("qurl").split("/");
					qualification.setUrl(materialurl+split[split.length-1]);
					qualifications.add(qualification);
				}
				yailmue.setQualifications(qualifications);
				queryMap.put(advIncId, yailmue);
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
	public void insertOrUpdateAdcInfoUpload(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		if(queryMap == null){
			return;
		}
		for(Integer advIncId:queryMap.keySet()){
			PreparedStatement pstmt = null;
			PreparedStatement cpstmt = null;
			YoukuAdvInfoLocaLMaterialUploadEntity ymue = queryMap.get(advIncId);
			try{
				pstmt = con.prepareStatement(YoukuAdvInfoQuery.getAdvInfoUpload);
				pstmt.setInt(1,getPubIncId());
				pstmt.setInt(2,advIncId);
				ResultSet rset = pstmt.executeQuery();
				if(rset.next()){
					String info=rset.getString("info");
					YoukuAdvInfoLocaLMaterialUploadEntity oldEntity= YoukuAdvInfoLocaLMaterialUploadEntity.getObject(info);
					if(oldEntity.equals(ymue)){
					}else{
						cpstmt = con.prepareStatement(YoukuAdvInfoQuery.updatetAdvInfoUpload);
						cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
						cpstmt.setString(2, ymue.toJson().toString());
						cpstmt.setTimestamp(3, new Timestamp(dateNow.getTime()));
						cpstmt.setInt(4, rset.getInt("internalid"));
						cpstmt.executeUpdate();
					}
				}else{
					cpstmt = con.prepareStatement(YoukuAdvInfoQuery.insertAdvInfoUpload);
					cpstmt.setInt(1, getPubIncId());
					cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
					cpstmt.setInt(3, advIncId);
					cpstmt.setTimestamp(4, new Timestamp(dateNow.getTime()));
					String newInfoStr = ymue.toJson().toString();
					cpstmt.setString(5, newInfoStr);
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
			pstmt = con.prepareStatement(YoukuAdvInfoQuery.selectforUpload);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			while(rset.next()){
				int internalId =  rset.getInt("internalId");
				boolean isSuccess=false;
				String out=null;
				try{
					
					YoukuAdvInfoUploadEntity ymmue = new YoukuAdvInfoUploadEntity();
					YoukuAdvInfoLocaLMaterialUploadEntity localinfo = YoukuAdvInfoLocaLMaterialUploadEntity.getObject(rset.getString("info"));
					if(localinfo.getFirstindustry() != null && localinfo.getSecondindustry()!=null){
						ymmue.setAdvertiser(localinfo);
						ymmue.setDspid(properties.getProperty("youku_dsp_id").toString());
						ymmue.setToken(properties.getProperty("youku_token").toString());
						LOG.info("MATERIAL ADVINFO UPLOAD POSTBODY");
						String postBody = ymmue.toJson().toString();
						LOG.info(postBody);

						UrlPost urlPost = new UrlPost();
						out = urlPost.urlpost(properties.getProperty("youku_url_prefix").toString()+
								properties.getProperty("youku_prefix_advinfo_upload"), postBody);
						LOG.info("MATERIAL ADVINFO UPLOAD RESPONSE");
						LOG.info(out);
						if(out != null){
							ReturnResultCode rrc = ReturnResultCode.getObject(out);
							if(rrc.getResult()==0){
								ReturnResultMessage rrm = ReturnResultMessage.getObject(out);
								if(rrm.getResult()==0 && (rrm.getMessage() == null || rrm.getMessage().size()<1 )){
									cpstmt = con.prepareStatement(YoukuAdvInfoQuery.updatetAdvinfoStatus.replaceAll("<id>", internalId+""));
									cpstmt.setInt(1,AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
									cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
									cpstmt.setString(3, out);
									cpstmt.executeUpdate();
									isSuccess=true;
								}
							}
						}
					}else{
						out="INDUSTRYCODEMISSING-DID NOT UPLOAD";
					}
				}catch(Exception e1){
					LOG.error(e1.getMessage(),e1);
				}
				if(!isSuccess){
					cpstmt1 = con.prepareStatement(YoukuAdvInfoQuery.updatetAdvinfoStatus.replaceAll("<id>", internalId+""));
					cpstmt1.setInt(1,AdxBasedExchangesStates.UPLOADFAIL.getCode());
					cpstmt1.setTimestamp(2, new Timestamp(dateNow.getTime()));
					if(out==null){
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
				pstmt = con.prepareStatement(YoukuAdvInfoQuery.update_material_state);
				pstmt.setTimestamp(1, new Timestamp(dateNow.getTime()));
				pstmt.setInt(2, MaterialType.ADVINFOUPLOAD.getCode());
				pstmt.setInt(3, getPubIncId());
			}else{
				pstmt = con.prepareStatement(YoukuAdvInfoQuery.insert_material_state);
				pstmt.setInt(1, getPubIncId());
				pstmt.setInt(2, MaterialType.ADVINFOUPLOAD.getCode());
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
