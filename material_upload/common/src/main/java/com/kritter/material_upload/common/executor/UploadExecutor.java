package com.kritter.material_upload.common.executor;

import java.sql.Connection;
import java.util.Properties;

import com.kritter.material_upload.common.adposition.AdPositionGet;

public interface UploadExecutor {
	void execute(Properties properties,Connection con);
	void executeAdpositionGet(Properties properties,AdPositionGet adPositionGet,Connection con);
}
