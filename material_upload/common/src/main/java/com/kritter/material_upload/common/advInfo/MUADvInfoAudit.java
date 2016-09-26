package com.kritter.material_upload.common.advInfo;

import java.sql.Connection;
import java.util.Properties;

public interface MUADvInfoAudit {

	void init(Properties properties);
	/** Transaction Begin **/
	void fetchMaterialAudit(Properties properties, Connection con);
	/** Transaction End **/
}
