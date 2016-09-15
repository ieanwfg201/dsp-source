package com.kritter.material_upload.common.video;

import java.sql.Connection;
import java.util.Properties;

public interface MUVideoAudit {

	void init(Properties properties);
	/** Transaction Begin **/
	void fetchMaterialAudit(Properties properties, Connection con);
	/** Transaction End **/
}
