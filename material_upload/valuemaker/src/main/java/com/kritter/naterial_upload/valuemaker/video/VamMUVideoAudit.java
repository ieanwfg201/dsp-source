package com.kritter.naterial_upload.valuemaker.video;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import com.kritter.naterial_upload.valuemaker.entity.HttpUtils;
import com.kritter.naterial_upload.valuemaker.entity.VamCreative;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.video.MUVideoAudit;
import lombok.Getter;
import lombok.Setter;

public class VamMUVideoAudit implements MUVideoAudit {
	private static final Logger LOG = LoggerFactory.getLogger(VamMUVideoAudit.class);
	@Getter@Setter
	private String dspid;
	@Getter@Setter
	private String username;
	@Getter @Setter
	private String password;
	@Getter@Setter
	private String id;//creative guid
	@Getter @Setter
	private Integer pubIncId;
	@Getter @Setter
	private Map<String,String> header;

	private VamCreative vamCreative;
	@Override
	public void init(Properties properties) {
		setDspid(properties.getProperty("vam_dsp_id").toString());
		setUsername(properties.getProperty("vam_username").toString());
		setPassword(properties.getProperty("vam_password").toString());
		setPubIncId(Integer.parseInt(properties.getProperty("vam_pubIncId").toString()));

		String vam_url_prefix = properties.getProperty("vam_url_prefix").toString();
		String vam_prefix_video_audit = vam_url_prefix + properties.getProperty("vam_prefix_video_audit").toString();
		this.vamCreative = new VamCreative(null,vam_prefix_video_audit);
		header = new HashMap<String, String>();
	}

	@Override
	public void fetchMaterialAudit(Properties properties, Connection con) {
		PreparedStatement pstmt = null;
		PreparedStatement stmt = null;
		PreparedStatement cpstmt = null;

		try{
			stmt = con.prepareStatement(VamVideoQuery.selectforAudit);
			stmt.setInt(1,getPubIncId());
			ResultSet set = stmt.executeQuery();
			pstmt = con.prepareStatement(VamVideoQuery.getVideoInfo);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			header.put("Content-Type", "application/json;charset=utf-8");
			header.put("Authorization", "Basic " + vamCreative.authStringEnc(this.username,this.password));
			while(rset.next()){
				id = rset.getString("guid");

				if(id !=  null){
					try{
						String out = vamCreative.getVideoStateByIds(id,header);
						LOG.info("MATERIAL AUDIT RETURN");
						LOG.info(out);
						if(out !=null){
							Map result = new HashMap();
							result = toMap(out);
							String status = result.get("status").toString();
							String status_name = null;
							if("1.0".equals(status)){
								status_name = "APPROVING";
							}else if ("2.0".equals(status)){
								status_name = "APPROVED";
							}else if("3.0".equals(status)){
								status_name = "REFUSED";
							}else {
								status_name = null;
							}

							if(status_name != null){
								if(set.next()){
									int internalid = set.getInt("internalid");
									cpstmt = con.prepareStatement(StringUtils.replace(VamVideoQuery.updatetVideoStatusMessage, "<id>", internalid+""));}
								if(AdxBasedExchangesStates.APPROVED.getName().equalsIgnoreCase(status_name)){
									cpstmt.setInt(1, AdxBasedExchangesStates.APPROVED.getCode());
								}else if(AdxBasedExchangesStates.REFUSED.getName().equalsIgnoreCase(status_name)){
									cpstmt.setInt(1, AdxBasedExchangesStates.REFUSED.getCode());
								}else if(AdxBasedExchangesStates.APPROVING.getName().equalsIgnoreCase(status_name)){
									cpstmt.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
								}else{
									cpstmt.setInt(1, rset.getInt("adxbasedexhangesstatus"));
								}
								cpstmt.setString(2, status_name);
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
				if(stmt != null){
					try {
						stmt.close();
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

	public Map toMap(String jsonString){
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> map = gson.fromJson(jsonString, type);
		return map;
	}

}
