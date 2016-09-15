package com.kritter.material_upload.common.executor;

import java.sql.Connection;
import java.util.Properties;

import com.kritter.material_upload.common.adposition.AdPositionGet;
import com.kritter.material_upload.common.banner.MUBanner;
import com.kritter.material_upload.common.banner.MUBannerAudit;

public interface UploadExecutor {
	void execute(Properties properties,Connection con);
	void executeAdpositionGet(Properties properties,AdPositionGet adPositionGet,Connection con);
	void executeMaterialBannerUpload(Properties properties,MUBanner muBanner,Connection con);
	void executeMaterialBannerAudit(Properties properties,MUBannerAudit muBannerAudit,Connection con);
}
