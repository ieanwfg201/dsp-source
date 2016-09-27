package com.kritter.naterial_upload.youku.advInfo;

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
import com.kritter.entity.account.AdxAccountExt;
import com.kritter.material_upload.common.advInfo.MUAdvInfo;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.naterial_upload.youku.advInfo.YoukuAdvInfoQuery;
import com.kritter.naterial_upload.youku.entity.ReturnResultCode;
import com.kritter.naterial_upload.youku.entity.ReturnResultMessage;
import com.kritter.naterial_upload.youku.entity.YoukuAdvInfoLocaLMaterialUploadEntity;
import com.kritter.naterial_upload.youku.entity.YoukuAdvInfoQueryEntity;
import com.kritter.naterial_upload.youku.entity.YoukuAdvInfoUploadEntity;
import com.kritter.naterial_upload.youku.entity.YoukuQualifications;

import lombok.Getter;
import lombok.Setter;

public class YoukuMUAdvInfo implements MUAdvInfo {
	private static final Logger LOG = LoggerFactory.getLogger(YoukuMUAdvInfo.class);
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
	private LinkedList<YoukuAdvInfoQueryEntity> youkuQueryEntityList;


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
			pstmt.setString(1, getStartDateStr());
			ResultSet rset = pstmt.executeQuery();
			boolean isFirst = true;
			while(rset.next()){
				if(isFirst){
					youkuQueryEntityList = new LinkedList<YoukuAdvInfoQueryEntity>();
					isFirst=false;
				}
				YoukuAdvInfoQueryEntity yqe = new YoukuAdvInfoQueryEntity();
				yqe.setAdvId(rset.getInt("advId"));
				yqe.setAdvName(rset.getString("advName"));
				yqe.setAddress(rset.getString("address"));
				try{
					AdxAccountExt adExt = AdxAccountExt.getObject(rset.getString("adxext"));
					if(adExt==null){
						continue;
					}
					yqe.setAdxext(adExt);
				}catch(Exception e1){
					LOG.error(e1.getMessage(),e1);
					continue;
				}
				yqe.setBrand(rset.getString("brand"));
				yqe.setContactdetail(rset.getString("contactdetail"));
				yqe.setPhone(rset.getString("phone"));
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
	public void insertOrUpdateAdcInfoUpload(Properties properties, Connection con) {
		if(!isPerformTransaction()){
			return;
		}
		if(youkuQueryEntityList == null){
			return;
		}
		for(YoukuAdvInfoQueryEntity yqe:youkuQueryEntityList){
			if(yqe == null){
				continue;
			}
			PreparedStatement pstmt = null;
			PreparedStatement cpstmt = null;
			try{
				pstmt = con.prepareStatement(YoukuAdvInfoQuery.getAdvInfoUpload);
				pstmt.setInt(1,getPubIncId());
				pstmt.setInt(2,yqe.getAdvId());
				ResultSet rset = pstmt.executeQuery();
				YoukuAdvInfoLocaLMaterialUploadEntity ymue= new YoukuAdvInfoLocaLMaterialUploadEntity();
				ymue.setAddress(yqe.getAddress());ymue.setBrand(yqe.getBrand());
				ymue.setContacts(yqe.getContactdetail());
				ymue.setName(yqe.getAdvName());
				ymue.setTel(yqe.getPhone());
				String materialurl=properties.getProperty("cdn_url").toString();
		        
				YoukuQualifications yQ = null;
				if(yqe.getAdxext()!=null){
					yQ= new YoukuQualifications();
					if(yqe.getAdxext().getMd5() != null){
						yQ.setMd5(yqe.getAdxext().getMd5());
					}
					if(yqe.getAdxext().getQname() != null){
						yQ.setName(yqe.getAdxext().getQname());
					}
					yQ.setOperation("add");
					if(yqe.getAdxext().getQurl() != null && !yqe.getAdxext().getQurl().isEmpty()){
				        String split[] = yqe.getAdxext().getQurl() .split("/");
						yQ.setUrl(materialurl+split[split.length-1]);
					}
					if(yqe.getAdxext().getFirstInd() != null){
						ymue.setFirstindustry(Integer.parseInt(yqe.getAdxext().getFirstInd() ));
					}
					if(yqe.getAdxext().getSecondInd() != null){
						ymue.setSecondindustry(Integer.parseInt(yqe.getAdxext().getSecondInd() ));
					}
				}
				List<YoukuQualifications> qual = new LinkedList<YoukuQualifications>();
				if(yQ != null){
					qual.add(yQ);
				}
				ymue.setQualifications(qual);

				if(rset.next()){
					String info=rset.getString("info");
					int adxbasedexhangesstatus = rset.getInt("adxbasedexhangesstatus");
					YoukuAdvInfoLocaLMaterialUploadEntity oldEntity= YoukuAdvInfoLocaLMaterialUploadEntity.getObject("info");
					cpstmt = con.prepareStatement(YoukuAdvInfoQuery.updatetAdvInfoUpload);
					if(oldEntity.equals(ymue)){
						cpstmt.setInt(1, adxbasedexhangesstatus);
					}else{
						if(ymue.getQualifications() != null && ymue.getQualifications().size()>0){
							ymue.getQualifications().get(0).setOperation("update");
						}
						cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
					}
					cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
					if(oldEntity.equals(ymue)){
						cpstmt.setString(3, info);
					}else{
						cpstmt.setString(3, ymue.toJson().toString());
					}
					cpstmt.setInt(4, rset.getInt("internalid"));
					cpstmt.executeUpdate();
					
				}else{
					cpstmt = con.prepareStatement(YoukuAdvInfoQuery.insertAdvInfoUpload);
					cpstmt.setInt(1, getPubIncId());
					cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
					cpstmt.setInt(3, yqe.getAdvId());
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
				String errorCode="";
				try{
					
					YoukuAdvInfoUploadEntity ymmue = new YoukuAdvInfoUploadEntity();
					ymmue.setAdvertiser(YoukuAdvInfoLocaLMaterialUploadEntity.getObject(rset.getString("info")));
					ymmue.setDspid(properties.getProperty("youku_dsp_id").toString());
					ymmue.setToken(properties.getProperty("youku_token").toString());
					LOG.info("MATERIAL ADVINFO UPLOAD POSTBODY");
					String postBody = ymmue.toJson().toString();
					LOG.info(postBody);
					
					UrlPost urlPost = new UrlPost();
					String out = urlPost.urlpost(properties.getProperty("youku_url_prefix").toString()+
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
								cpstmt.setString(3, errorCode);
								cpstmt.executeUpdate();
								isSuccess=true;
							}else{
								if(rrm.getMessage() != null && rrm.getMessage().size()>0 ){
									errorCode=rrm.getMessage().keySet().iterator().next();
								}else{
									errorCode = rrm.getResult()+" -- ReturnCode";
								}
							}
						}else{
							errorCode = rrc.getResult()+" -- ReturnCode";
						}
					}
				}catch(Exception e1){
					LOG.error(e1.getMessage(),e1);
				}
				if(!isSuccess){
					cpstmt1 = con.prepareStatement(YoukuAdvInfoQuery.updatetAdvinfoStatus.replaceAll("<id>", internalId+""));
					cpstmt1.setInt(1,AdxBasedExchangesStates.UPLOADFAIL.getCode());
					cpstmt1.setTimestamp(2, new Timestamp(dateNow.getTime()));
					cpstmt1.setString(3, errorCode);
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
