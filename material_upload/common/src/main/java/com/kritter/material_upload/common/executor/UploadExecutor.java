package com.kritter.material_upload.common.executor;

import java.sql.Connection;
import java.util.Properties;

import com.kritter.material_upload.common.adposition.AdPositionGet;
import com.kritter.material_upload.common.advInfo.MUADvInfoAudit;
import com.kritter.material_upload.common.advInfo.MUAdvInfo;
import com.kritter.material_upload.common.banner.MUBanner;
import com.kritter.material_upload.common.banner.MUBannerAudit;
import com.kritter.material_upload.common.video.MUVideo;
import com.kritter.material_upload.common.video.MUVideoAudit;

public interface UploadExecutor {
	void checkJobs(Properties properties,Connection con,int pubincId);
	void execute(Properties properties,Connection con);
	void executeAdpositionGet(Properties properties,AdPositionGet adPositionGet,Connection con,int pubincId);
	void executeMaterialBannerUpload(Properties properties,MUBanner muBanner,Connection con,int pubincId);
	void executeMaterialBannerAudit(Properties properties,MUBannerAudit muBannerAudit,Connection con,int pubincId);
	void executeMaterialVideoUpload(Properties properties,MUVideo muVideo,Connection con,int pubincId);
	void executeMaterialVideoAudit(Properties properties,MUVideoAudit muVideoAudit,Connection con,int pubincId);
	void executeAdvInfoUpload(Properties properties,MUAdvInfo advInfo,Connection con,int pubincId);
	void executeMaterialAdvInfoAudit(Properties properties,MUADvInfoAudit muAdvInforAudit,Connection con,int pubincId);
}
