package com.kritter.material_upload.common.advInfo;

import java.sql.Connection;
import java.util.Properties;

public interface MUAdvInfo {

	void init(Properties properties);
	/** Transaction Begin **/
	void getLastRun(Properties properties, Connection con);
	void getModifiedEntities(Properties properties, Connection con);
	void insertOrUpdateAdcInfoUpload(Properties properties, Connection con);
	void uploadmaterial(Properties properties, Connection con);
	void updaterun(Properties properties, Connection con);
	/** Transaction End **/


}
