package com.kritter.naterial_upload.youku.adpositionget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialType;
import com.kritter.material_upload.common.adposition.AdPositionGet;
import com.kritter.material_upload.common.urlpost.UrlPost;
import com.kritter.naterial_upload.youku.entity.PostEntity;
import com.kritter.naterial_upload.youku.entity.ReturnErrorEntity;
import com.kritter.naterial_upload.youku.entity.ReturnResultCode;
import com.kritter.naterial_upload.youku.entity.adposition.APHealthyReturn;
import com.kritter.naterial_upload.youku.entity.adposition.APMessage;
import com.kritter.naterial_upload.youku.entity.adposition.APRecord;

import lombok.Getter;
import lombok.Setter;

public class YoukuAdPositionGet implements AdPositionGet{
	private static final Logger LOG = LogManager.getLogger("material.root");
	@Getter @Setter
	private String dspid;
	@Getter @Setter
	private String token;
	@Getter @Setter
	private Integer pubIncId;
	@Getter @Setter
	private String urlgetOutput;
	@Getter @Setter
	private boolean performTransaction =true;
	@Getter @Setter
	private Date dateNow;

	@Override
	public void init(Properties properties) {
		setDspid(properties.getProperty("youku_dsp_id").toString());
		setToken(properties.getProperty("youku_token").toString());
		setPubIncId(Integer.parseInt(properties.getProperty("youku_pubIncId").toString()));
		dateNow = new Date();
	}

	@Override
	public boolean checkAdpositionGet(Connection con, Properties properties) {
		PreparedStatement pstmt = null;
		try{
			pstmt = con.prepareStatement("select * from adposition_get where pubIncId=? and adxbasedexhangesstatus="+AdxBasedExchangesStates.SUBMITTED.getCode());
			pstmt.setInt(1, getPubIncId());
			ResultSet rset = pstmt.executeQuery();
			while(rset.next()){
				return true;
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
			}
		}
		setPerformTransaction(false);
		return false;
	}

	@Override
	public void getAdposition(Properties properties) {
		if(!isPerformTransaction()){
			return;
		}
		UrlPost urlPost= new UrlPost();
		PostEntity p = new PostEntity();
		p.setDspid(getDspid());
		p.setToken(getToken());
		try{
			String out = urlPost.urlpost(properties.getProperty("youku_url_prefix").toString()+properties.getProperty("youku_prefix_adposition").toString(),
					p.toJson().toString());
			if(out != null){
				LOG.info(out);
				setUrlgetOutput(out);
			}else{
				setPerformTransaction(false);
			}
		}catch(Exception e){
			setPerformTransaction(false);
		}
	}

	@Override
	public void insertAdposition(Connection con, Properties properties) {
		if(!isPerformTransaction()){
			return;
		}
		try{
			if(this.urlgetOutput != null){
				ReturnResultCode rrc = ReturnResultCode.getObject(this.urlgetOutput );
				if(rrc !=null){
					if(rrc.getResult()!=0){
						ReturnErrorEntity rre = ReturnErrorEntity.getObject(this.urlgetOutput);
						PreparedStatement pstmt = null;
						try{
							pstmt = con.prepareStatement("update adposition_get set last_modified=?,message=?,adxbasedexhangesstatus= "+AdxBasedExchangesStates.ERROR.getCode()+" where pubIncId=? ");
							pstmt.setTimestamp(1, new Timestamp(getDateNow().getTime()));
							pstmt.setString(2, rre.getMessage());
							pstmt.setInt(3, getPubIncId());
							pstmt.executeUpdate();
						}catch(Exception e){
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
					}else{
						APHealthyReturn ahr = APHealthyReturn.getObject(this.urlgetOutput);
						APMessage apmesage = ahr.getMessage();
						if(apmesage != null){
							List<APRecord> apRecords = apmesage.getRecords();
							if(apRecords!= null){
								for(APRecord apRecord:apRecords){
									boolean recordFound=false;
									if(apRecord.getAdplacementid()==null){
										continue;
									}
									PreparedStatement pstmt = null;
									try{
										pstmt = con.prepareStatement("select * from ad_position where pubIncId=? and adposid=?");
										pstmt.setInt(1, getPubIncId());
										pstmt.setString(2, apRecord.getAdplacementid()+"");
										ResultSet rset = pstmt.executeQuery();
										if(rset.next()){
											recordFound=true;
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
										}
									}
									if(recordFound){
										PreparedStatement pstmt1 = null;
										try{
											pstmt1 = con.prepareStatement("upddate ad_position set name=?,description=?,last_modified=? where pubIncId=? and adposid=?");
											pstmt1.setString(1, apRecord.getAdplacementname());
											pstmt1.setString(2, apRecord.toJson().toString());
											pstmt1.setTimestamp(3, new Timestamp(getDateNow().getTime()));
											pstmt1.setInt(4, getPubIncId());
											pstmt1.setString(5, apRecord.getAdplacementid()+"");
											pstmt1.executeUpdate();
										}catch(Exception e){
											LOG.error(e.getMessage(),e);
										}finally{
											if(pstmt1 != null){
												try {
													pstmt1.close();
												} catch (SQLException e) {
													LOG.error(e.getMessage(),e);
												}
											}
										}
									}else{
										PreparedStatement pstmt1 = null;
										try{
											pstmt1 = con.prepareStatement("insert into ad_position(name,description,last_modified,pubIncId,adposid) values(?,?,?,?,?)");
											pstmt1.setString(1, apRecord.getAdplacementname());
											pstmt1.setString(2, apRecord.toJson().toString());
											pstmt1.setTimestamp(3, new Timestamp(getDateNow().getTime()));
											pstmt1.setInt(4, getPubIncId());
											pstmt1.setString(5, apRecord.getAdplacementid()+"");
											pstmt1.executeUpdate();
										}catch(Exception e){
											LOG.error(e.getMessage(),e);
										}finally{
											if(pstmt1 != null){
												try {
													pstmt1.close();
												} catch (SQLException e) {
													LOG.error(e.getMessage(),e);
												}
											}
										}
									}
								}
								PreparedStatement pstmt = null;
								try{
									pstmt = con.prepareStatement("update adposition_get set last_modified=?,adxbasedexhangesstatus= "+AdxBasedExchangesStates.GETOBTAINED.getCode()+" where pubIncId=? ");
									pstmt.setTimestamp(1, new Timestamp(getDateNow().getTime()));
									pstmt.setInt(2, getPubIncId());
									pstmt.executeUpdate();
								}catch(Exception e){
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
					}
				}
			}
		}catch(Exception e){
			setPerformTransaction(false);
			LOG.error(e.getMessage(),e);
		}
	}

	@Override
	public void updaterun(Connection con, Properties properties) {
		if(!isPerformTransaction()){
			return;
		}
		PreparedStatement pstmt = null;
		boolean recordFound=false;
		try{
			pstmt = con.prepareStatement("select * from material_upload_state where pubIncId=? and materialtype=?");
			pstmt.setInt(1, getPubIncId());
			pstmt.setInt(2, MaterialType.ADPOSITION.getCode());
			ResultSet rset = pstmt.executeQuery();
			if(rset.next()){
				recordFound=true;
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
			}
		}
		if(recordFound){
			PreparedStatement pstmt1 = null;
			try{
				pstmt1 = con.prepareStatement("update material_upload_state set last_modified=? where pubIncId=? and materialtype=?");
				pstmt1.setTimestamp(1, new Timestamp(getDateNow().getTime()));
				pstmt1.setInt(2, getPubIncId());
				pstmt1.setInt(3, MaterialType.ADPOSITION.getCode());
				pstmt1.executeUpdate();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
			}finally{
				if(pstmt1 != null){
					try {
						pstmt1.close();
					} catch (SQLException e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}
		}else{
			PreparedStatement pstmt1 = null;
			try{
				pstmt1 = con.prepareStatement("insert material_upload_state(pubIncId,materialtype,last_modified) values(?,?,?)");
				pstmt1.setInt(1, getPubIncId());
				pstmt1.setInt(2, MaterialType.ADPOSITION.getCode());
				pstmt1.setTimestamp(3, new Timestamp(getDateNow().getTime()));
				pstmt1.executeUpdate();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
			}finally{
				if(pstmt1 != null){
					try {
						pstmt1.close();
					} catch (SQLException e) {
						LOG.error(e.getMessage(),e);
					}
				}
			}
		}
	}

}
