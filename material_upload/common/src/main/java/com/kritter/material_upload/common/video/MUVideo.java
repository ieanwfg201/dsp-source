package com.kritter.material_upload.common.video;

import java.sql.Connection;
import java.util.Properties;

public interface MUVideo {

	void init(Properties properties);
	/** Transaction Begin **/
	void getLastRun(Properties properties, Connection con);
	void removeDisassociatedCreative(Properties properties, Connection con);
	void getModifiedEntities(Properties properties, Connection con);
	void insertOrUpdateVideoUpload(Properties properties, Connection con);
	void uploadmaterial(Properties properties, Connection con);
	void updaterun(Properties properties, Connection con);
	/** Transaction End **/


}
