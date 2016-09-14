package com.kritter.material_upload.common.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kritter.material_upload.common.adposition.AdPositionGet;

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

}
