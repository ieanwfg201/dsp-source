package com.kritter.material_upload.common.banner;

import java.sql.Connection;
import java.util.Properties;

public interface MUBanner {

	void init(Properties properties);
	/** Transaction Begin **/
	void getLastRun(Properties properties, Connection con);
	void removeDisassociatedCreative(Properties properties, Connection con);
	void getModifiedEntities(Properties properties, Connection con);
	void insertOrUpdateBannerUpload(Properties properties, Connection con);
	void uploadmaterial(Properties properties, Connection con);
	void updaterun(Properties properties, Connection con);
	/** Transaction End **/


}
