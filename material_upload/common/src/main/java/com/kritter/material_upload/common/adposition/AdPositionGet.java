package com.kritter.material_upload.common.adposition;

import java.sql.Connection;
import java.util.Properties;

public interface AdPositionGet {
	void init(Properties properties);
	boolean checkAdpositionGet(Connection con, Properties properties);
	void getAdposition(Properties properties);
	/** IN TRANSACTION BEGIN **/
	void insertAdposition(Connection con,Properties properties);
	void updaterun(Connection con,Properties properties);
	/** IN TRANSACTION END **/
}
