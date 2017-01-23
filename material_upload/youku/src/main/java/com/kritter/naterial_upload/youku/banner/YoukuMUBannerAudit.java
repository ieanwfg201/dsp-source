package com.kritter.naterial_upload.youku.banner;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.banner.MUBannerAudit;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.naterial_upload.youku.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Date;
import java.util.Properties;

public class YoukuMUBannerAudit implements MUBannerAudit {
	private static final Logger LOG = LogManager.getLogger(YoukuMUBannerAudit.class.getName());
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
			pstmt = con.prepareStatement(YoukuBannerQuery.selectforAudit);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			while(rset.next()){
				YoukuMaterialUploadEntity ymue = YoukuMaterialUploadEntity.getObject(rset.getString("info"));
				int internalid = rset.getInt("internalid");
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
								properties.getProperty("youku_prefix_banner_audit").toString(), postBody);
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
										cpstmt = con.prepareStatement(StringUtils.replace(YoukuBannerQuery.updatetBannerStatusMessage, "<id>", internalid+""));
										if("通过".equalsIgnoreCase(rar.getResult())){
											cpstmt.setInt(1, AdxBasedExchangesStates.APPROVED.getCode());
										}else if("不通过".equalsIgnoreCase(rar.getResult())){
											cpstmt.setInt(1, AdxBasedExchangesStates.REFUSED.getCode());
										}else if("待审核".equalsIgnoreCase(rar.getResult())){
											cpstmt.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
										}else{
											cpstmt.setInt(1, rset.getInt("adxbasedexhangesstatus"));
										}
										cpstmt.setString(2, rar.getResult()+"-"+rar.getReason());
										cpstmt.setTimestamp(3, ts);
										cpstmt.executeUpdate();
									}else{
										cpstmt = con.prepareStatement(StringUtils.replace(YoukuBannerQuery.updatetBannerStatusMessage, "<id>", internalid+""));
										cpstmt.setInt(1, AdxBasedExchangesStates.AUDITORGETFAIL.getCode());
										cpstmt.setString(2, rrc.getResult()+"--AUDIT MESSAGE NOT PRSESENT: "+out);
										cpstmt.setTimestamp(3, ts);
										cpstmt.executeUpdate();
									}
								}else{
										cpstmt = con.prepareStatement(StringUtils.replace(YoukuBannerQuery.updatetBannerStatusMessage, "<id>", internalid+""));
										cpstmt.setInt(1, AdxBasedExchangesStates.AUDITORGETFAIL.getCode());
										cpstmt.setString(2, rrc.getResult()+"--material URL doesn't exist: "+out);
										cpstmt.setTimestamp(3, ts);
										cpstmt.executeUpdate();
									
								}
							}else{
								cpstmt = con.prepareStatement(StringUtils.replace(YoukuBannerQuery.updatetBannerStatusMessage, "<id>", internalid+""));
								cpstmt.setInt(1, AdxBasedExchangesStates.AUDITORGETFAIL.getCode());
								cpstmt.setString(2, rrc.getResult()+"--RETURNCODEAUDIT: "+out);
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
