package com.kritter.material_upload.common.banner;

import java.sql.Connection;
import java.util.Properties;

public interface MUBannerAudit {

	void init(Properties properties);
	/** Transaction Begin **/
	void fetchMaterialAudit(Properties properties, Connection con);
	/** Transaction End **/
}
