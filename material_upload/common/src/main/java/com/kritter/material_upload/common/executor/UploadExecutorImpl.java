package com.kritter.material_upload.common.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.material_upload.common.adposition.AdPositionGet;
import com.kritter.material_upload.common.advInfo.MUADvInfoAudit;
import com.kritter.material_upload.common.advInfo.MUAdvInfo;
import com.kritter.material_upload.common.banner.MUBanner;
import com.kritter.material_upload.common.banner.MUBannerAudit;
import com.kritter.material_upload.common.video.MUVideo;
import com.kritter.material_upload.common.video.MUVideoAudit;

public abstract class UploadExecutorImpl implements UploadExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(UploadExecutorImpl.class);

	@Override
	public void execute(Properties properties,Connection con) {
	}
	@Override
	public void executeAdpositionGet(Properties properties, AdPositionGet adPositionGet,Connection con) {
		if(adPositionGet != null){
			boolean autoCommit = true;
			try{
				autoCommit = con.getAutoCommit();
				adPositionGet.init(properties);
				adPositionGet.checkAdpositionGet(con, properties);
				adPositionGet.getAdposition(properties);
				con.setAutoCommit(false);
				adPositionGet.insertAdposition(con, properties);
				adPositionGet.updaterun(con, properties);
				con.commit();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);				
				}
			}finally{
				try {
					con.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
	@Override
	public void executeMaterialBannerUpload(Properties properties, MUBanner muBanner,Connection con) {
		if(muBanner != null){
			boolean autoCommit = true;
			try{
				autoCommit = con.getAutoCommit();
				muBanner.init(properties);
				con.setAutoCommit(false);
				muBanner.getLastRun(properties, con);
				muBanner.getModifiedEntities(properties, con);
				muBanner.insertOrUpdateBannerUpload(properties, con);
				muBanner.uploadmaterial(properties, con);
				muBanner.updaterun(properties, con);
				con.commit();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);				
				}
			}finally{
				try {
					con.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
	@Override
	public void executeMaterialBannerAudit(Properties properties, MUBannerAudit muBannerAudit,Connection con) {
		if(muBannerAudit != null){
			boolean autoCommit = true;
			try{
				autoCommit = con.getAutoCommit();
				muBannerAudit.init(properties);
				con.setAutoCommit(false);
				muBannerAudit.fetchMaterialAudit(properties, con);
				con.commit();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);				
				}
			}finally{
				try {
					con.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
	@Override
	public void executeMaterialVideoUpload(Properties properties, MUVideo muVideo,Connection con) {
		if(muVideo != null){
			boolean autoCommit = true;
			try{
				autoCommit = con.getAutoCommit();
				muVideo.init(properties);
				con.setAutoCommit(false);
				muVideo.getLastRun(properties, con);
				muVideo.getModifiedEntities(properties, con);
				muVideo.insertOrUpdateVideoUpload(properties, con);
				muVideo.uploadmaterial(properties, con);
				muVideo.updaterun(properties, con);
				con.commit();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);				
				}
			}finally{
				try {
					con.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
	@Override
	public void executeMaterialVideoAudit(Properties properties, MUVideoAudit muVideoAudit,Connection con) {
		if(muVideoAudit != null){
			boolean autoCommit = true;
			try{
				autoCommit = con.getAutoCommit();
				muVideoAudit.init(properties);
				con.setAutoCommit(false);
				muVideoAudit.fetchMaterialAudit(properties, con);
				con.commit();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);				
				}
			}finally{
				try {
					con.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
	@Override
	public void executeAdvInfoUpload(Properties properties, MUAdvInfo advInfo,Connection con) {
		if(advInfo != null){
			boolean autoCommit = true;
			try{
				autoCommit = con.getAutoCommit();
				advInfo.init(properties);
				con.setAutoCommit(false);
				advInfo.getLastRun(properties, con);
				advInfo.getModifiedEntities(properties, con);
				advInfo.insertOrUpdateAdcInfoUpload(properties, con);
				advInfo.uploadmaterial(properties, con);
				advInfo.updaterun(properties, con);
				con.commit();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);				
				}
			}finally{
				try {
					con.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
	@Override
	public void executeMaterialAdvInfoAudit(Properties properties, MUADvInfoAudit muAdvInforAudit,Connection con) {
		if(muAdvInforAudit != null){
			boolean autoCommit = true;
			try{
				autoCommit = con.getAutoCommit();
				muAdvInforAudit.init(properties);
				con.setAutoCommit(false);
				muAdvInforAudit.fetchMaterialAudit(properties, con);
				con.commit();
			}catch(Exception e){
				LOG.error(e.getMessage(),e);
				try {
					con.rollback();
				} catch (SQLException e1) {
					LOG.error(e1.getMessage(),e1);				
				}
			}finally{
				try {
					con.setAutoCommit(autoCommit);
				} catch (SQLException e) {
					LOG.error(e.getMessage(),e);
				}
			}
		}
	}
}
