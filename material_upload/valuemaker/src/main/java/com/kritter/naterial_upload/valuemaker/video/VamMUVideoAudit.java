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

	@Override
	public void init(Properties properties) {
		setDspid(properties.getProperty("vam_dsp_id").toString());
		setUsername(properties.getProperty("vam_username").toString());
		setPassword(properties.getProperty("vam_password").toString());
		setPubIncId(Integer.parseInt(properties.getProperty("vam_pubIncId").toString()));
	}

	@Override
	public void fetchMaterialAudit(Properties properties, Connection con) {
		PreparedStatement pstmt = null;
		PreparedStatement cpstmt = null;
		try{
			pstmt = con.prepareStatement(VamVideoQuery.getVideoInfo);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			while(rset.next()){
				id = rset.getString("guid");
				if(id !=  null){
					try{
						String out = HttpUtils.get(properties.getProperty("vam_url_prefix").toString()+
								properties.getProperty("vam_prefix_video_audit").toString()+"?id="+id,getUsername(),getPassword());
						LOG.info("MATERIAL AUDIT RETURN");
						LOG.info(out);
						if(out !=null){
							Map result = new HashMap();
							result = toMap(out);
							String status = result.get("status").toString();
							String status_name = null;
							if("1".equals(status)){
								status_name = "APPROVING";
							}else if ("2".equals(status)){
								status_name = "APPROVED";
							}else if("3".equals(status)){
								status_name = "REFUSED";
							}else {
								status_name = null;
							}

							if(status_name != null){
								cpstmt = con.prepareStatement(VamVideoQuery.updatetVideoStatusMessage);
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

	public Map toMap(String jsonString){
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> map = gson.fromJson(jsonString, type);
		return map;
	}

}
