package com.kritter.naterial_upload.valuemaker.executor;

import java.sql.Connection;
import java.util.Properties;

import com.kritter.material_upload.common.executor.UploadExecutor;
import com.kritter.material_upload.common.executor.UploadExecutorImpl;
import com.kritter.naterial_upload.valuemaker.banner.VamMUBanner;
import com.kritter.naterial_upload.valuemaker.banner.VamMUBannerAudit;
import com.kritter.naterial_upload.valuemaker.video.VamMUVideo;
import com.kritter.naterial_upload.valuemaker.video.VamMUVideoAudit;


public class VamUploadExecutor  extends UploadExecutorImpl implements UploadExecutor{

	@Override
	public void execute(Properties properties,Connection con) {
		int pubInc=Integer.parseInt(properties.getProperty("vam_pubIncId").toString());
		super.checkJobs(properties, con, pubInc);
		VamMUBanner muBanner = new VamMUBanner();
		super.executeMaterialBannerUpload(properties, muBanner, con, pubInc );
//		VamMUBannerAudit muBannerAudit = new VamMUBannerAudit();
//		super.executeMaterialBannerAudit(properties, muBannerAudit, con, pubInc);
		VamMUVideo muVideo = new VamMUVideo();
		super.executeMaterialVideoUpload(properties, muVideo, con, pubInc);
		VamMUVideoAudit muVideoAudit = new VamMUVideoAudit();
		super.executeMaterialVideoAudit(properties, muVideoAudit, con, pubInc);
	}


}
