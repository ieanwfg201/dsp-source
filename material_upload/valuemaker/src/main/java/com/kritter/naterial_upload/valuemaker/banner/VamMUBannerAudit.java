package com.kritter.naterial_upload.valuemaker.banner;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kritter.naterial_upload.valuemaker.entity.HttpUtils;

import com.kritter.naterial_upload.valuemaker.entity.VamCreative;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.banner.MUBannerAudit;
import lombok.Getter;
import lombok.Setter;

public class VamMUBannerAudit implements MUBannerAudit {
	private static final Logger LOG = LoggerFactory.getLogger(VamMUBannerAudit.class);
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
		header = new HashMap<String,String>();
		String vam_url_prefix = properties.getProperty("vam_url_prefix").toString();
		String vam_prefix_banner_audit = vam_url_prefix + properties.getProperty("vam_prefix_banner_audit").toString();
		this.vamCreative = new VamCreative(null,vam_prefix_banner_audit);
	}

	@Override
	public void fetchMaterialAudit(Properties properties, Connection con) {
		PreparedStatement pstmt = null;
		PreparedStatement stmt = null;
		PreparedStatement cpstmt = null;

		try{
			pstmt = con.prepareStatement(VamBannerQuery.selectforAudit);
			pstmt.setInt(1,getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			stmt=con.prepareStatement(VamBannerQuery.selectCreativeGuid);
			stmt.setInt(1,getPubIncId());
			ResultSet set = stmt.executeQuery();
			Timestamp ts = new Timestamp(new Date().getTime());
			header.put("Content-Type", "application/json;charset=utf-8");
			header.put("Authorization", "Basic " + vamCreative.authStringEnc(this.username,this.password));
			while(rset.next()){
				if(set.next()){id = set.getString("guid");}
				int internalid = rset.getInt("internalid");
				if(id !=  null){
					try{
						String out = vamCreative.getBannerStateByIds(id,header);
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
								cpstmt = con.prepareStatement(StringUtils.replace(VamBannerQuery.updatetBannerStatusMessage, "<id>", internalid+""));
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
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> map = gson.fromJson(jsonString, type);
		return map;
	}


}
