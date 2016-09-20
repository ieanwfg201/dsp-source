package com.kritter.naterial_upload.youku.video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.material_upload.common.video.MUVideoAudit;
import com.kritter.naterial_upload.youku.entity.ReturnAuditEntity;
import com.kritter.naterial_upload.youku.entity.ReturnAuditRecord;
import com.kritter.naterial_upload.youku.entity.ReturnResultCode;
import com.kritter.naterial_upload.youku.entity.YoukuMaterialAuditEntity;
import com.kritter.naterial_upload.youku.entity.YoukuMaterialUploadEntity;

import lombok.Getter;
import lombok.Setter;

public class YoukuMUVideoAudit implements MUVideoAudit {
	private static final Logger LOG = LoggerFactory.getLogger(YoukuMUVideoAudit.class);
	@Getter@Setter
	private String dspid;
	@Getter @Setter
	private String token;
	@Getter @Setter
	private Integer pubIncId;

	@Override
	public void init(Properties properties) {
		setDspid(properties.getProperty("youku_dsp_id").toString());
		setToken(properties.getProperty("youku_token").toString());
		setPubIncId(Integer.parseInt(properties.getProperty("youku_pubIncId").toString()));
	}


	@Override
	public void fetchMaterialAudit(Properties properties, Connection con) {
		PreparedStatement pstmt = null;
		PreparedStatement cpstmt = null;
		try{
			pstmt = con.prepareStatement(YoukuVideoQuery.selectforAudit);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			while(rset.next()){
				YoukuMaterialUploadEntity ymue = YoukuMaterialUploadEntity.getObject(rset.getString("info"));
				if(ymue !=  null){
					try{
						YoukuMaterialAuditEntity ymae = new YoukuMaterialAuditEntity();
						ymae.setDspid(dspid);
						ymae.setToken(token);
						String urlArray[] = new String[1];
						urlArray[0]=ymue.getUrl();
						ymae.setMaterialurl(urlArray);
						String postBody= ymae.toJson().toString();
						LOG.info("MATERIAL AUDIT POST");
						LOG.info(postBody);
						UrlPost urlPost = new UrlPost();
						String out = urlPost.urlpost(properties.getProperty("youku_url_prefix").toString()+
								properties.getProperty("youku_prefix_video_audit").toString(), postBody);
						LOG.info("MATERIAL AUDIT RETURN");
						LOG.info(out);
						if(out !=null){
							ReturnResultCode rrc = ReturnResultCode.getObject(out);
							if(rrc.getResult()==0){
								ReturnAuditEntity rae=ReturnAuditEntity.getObject(out);
								if(rae != null && rae.getMessage() != null &&
										rae.getMessage().getRecords() != null && rae.getMessage().getRecords().size()>0){
									ReturnAuditRecord rar = rae.getMessage().getRecords().get(0);
									if(rar != null){
										cpstmt = con.prepareStatement(YoukuVideoQuery.updatetVideoStatusMessage);
										if(AdxBasedExchangesStates.APPROVED.getName().equalsIgnoreCase(rar.getReason())){
											cpstmt.setInt(1, AdxBasedExchangesStates.APPROVED.getCode());
										}else if(AdxBasedExchangesStates.REFUSED.getName().equalsIgnoreCase(rar.getReason())){
											cpstmt.setInt(1, AdxBasedExchangesStates.REFUSED.getCode());
										}else if(AdxBasedExchangesStates.APPROVING.getName().equalsIgnoreCase(rar.getReason())){
											cpstmt.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
										}else{
											cpstmt.setInt(1, rset.getInt("adxbasedexhangesstatus"));
										}
										cpstmt.setString(2, rar.getReason());
										cpstmt.setTimestamp(3, ts);
										cpstmt.executeUpdate();
									}
								}

							}
						}
					}catch(Exception e1){
						LOG.error(e1.getMessage(),e1);
					}
				}
			}
		}catch(Exception e){
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
