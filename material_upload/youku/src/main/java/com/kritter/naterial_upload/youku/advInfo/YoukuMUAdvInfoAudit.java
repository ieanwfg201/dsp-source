package com.kritter.naterial_upload.youku.advInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.advInfo.MUADvInfoAudit;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.naterial_upload.youku.entity.ReturnAdvInfoMessage;
import com.kritter.naterial_upload.youku.entity.ReturnResultCode;
import com.kritter.naterial_upload.youku.entity.YoukuAdvInfoAuditEntity;
import com.kritter.naterial_upload.youku.entity.YoukuAdvInfoLocaLMaterialUploadEntity;

import lombok.Getter;
import lombok.Setter;

public class YoukuMUAdvInfoAudit implements MUADvInfoAudit {
	private static final Logger LOG = LoggerFactory.getLogger(YoukuMUAdvInfoAudit.class);
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
			pstmt = con.prepareStatement(YoukuAdvInfoQuery.selectforAudit);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			while(rset.next()){
				YoukuAdvInfoLocaLMaterialUploadEntity ymue = YoukuAdvInfoLocaLMaterialUploadEntity.getObject(rset.getString("info"));
				int internalid = rset.getInt("internalid");
				if(ymue !=  null){
					try{
						YoukuAdvInfoAuditEntity ymae = new YoukuAdvInfoAuditEntity();
						ymae.setDspid(dspid);
						ymae.setToken(token);
						ymae.setAdvertiser(ymue.getName());
						String postBody= ymae.toJson().toString();
						LOG.info("ADVINFO AUDIT POST");
						LOG.info(postBody);
						UrlPost urlPost = new UrlPost();
						String out = urlPost.urlpost(properties.getProperty("youku_url_prefix").toString()+
								properties.getProperty("youku_prefix_advinfo_get").toString(), postBody);
						LOG.info("MATERIAL AUDIT RETURN");
						LOG.info(out);
						if(out !=null){
							ReturnResultCode rrc = ReturnResultCode.getObject(out);
							if(rrc.getResult()==0){
								ReturnAdvInfoMessage rae=ReturnAdvInfoMessage.getObject(out);
								if(rae != null && rae.getMessage() != null &&
										rae.getMessage().getState() != null ){
									cpstmt = con.prepareStatement(StringUtils.replace(YoukuAdvInfoQuery.updatetAdvInfoStatusMessage, "<id>", internalid+""));
									if("通过".equalsIgnoreCase(rae.getMessage().getState())){
										cpstmt.setInt(1, AdxBasedExchangesStates.APPROVED.getCode());
									}else if("拒绝".equalsIgnoreCase(rae.getMessage().getState())){
										cpstmt.setInt(1, AdxBasedExchangesStates.REFUSED.getCode());
									}else if("待审核".equalsIgnoreCase(rae.getMessage().getState())){
										cpstmt.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
									}else{
										cpstmt.setInt(1, rset.getInt("adxbasedexhangesstatus"));
									}
									cpstmt.setString(2, rae.getMessage().getState()+"-"+rae.getMessage().getRefusereason());
									cpstmt.setTimestamp(3, ts);
									cpstmt.executeUpdate();
								}else{
									cpstmt = con.prepareStatement(StringUtils.replace(YoukuAdvInfoQuery.updatetAdvInfoStatusMessage, "<id>", internalid+""));
									cpstmt.setInt(1, AdxBasedExchangesStates.AUDITORGETFAIL.getCode());
									cpstmt.setString(2, rrc.getResult()+"--AUDIT MESSAGE NOT PRSESENT: "+out);
									cpstmt.setTimestamp(3, ts);
									cpstmt.executeUpdate();
								}
							}else{
								cpstmt = con.prepareStatement(StringUtils.replace(YoukuAdvInfoQuery.updatetAdvInfoStatusMessage, "<id>", internalid+""));
								cpstmt.setInt(1, AdxBasedExchangesStates.AUDITORGETFAIL.getCode());
								cpstmt.setString(2, rrc.getResult()+"--RETURNCODEAUDIT :"+out);
								cpstmt.setTimestamp(3, ts);
								cpstmt.executeUpdate();
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
