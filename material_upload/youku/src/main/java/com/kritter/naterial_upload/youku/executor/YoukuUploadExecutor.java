package com.kritter.naterial_upload.youku.executor;

import java.sql.Connection;
import java.util.Properties;

import com.kritter.material_upload.common.adposition.AdPositionGet;
import com.kritter.material_upload.common.executor.UploadExecutor;
import com.kritter.material_upload.common.executor.UploadExecutorImpl;
import com.kritter.naterial_upload.youku.adpositionget.YoukuAdPositionGet;
import com.kritter.naterial_upload.youku.advInfo.YoukuMUAdvInfo;
import com.kritter.naterial_upload.youku.advInfo.YoukuMUAdvInfoAudit;
import com.kritter.naterial_upload.youku.banner.YoukuMUBanner;
import com.kritter.naterial_upload.youku.banner.YoukuMUBannerAudit;
import com.kritter.naterial_upload.youku.video.YoukuMUVideo;
import com.kritter.naterial_upload.youku.video.YoukuMUVideoAudit;

public class YoukuUploadExecutor  extends UploadExecutorImpl implements UploadExecutor{

	@Override
	public void execute(Properties properties,Connection con) {
		int pubInc=Integer.parseInt(properties.getProperty("youku_pubIncId").toString());
		super.checkJobs(properties, con, pubInc);
		AdPositionGet yag = new YoukuAdPositionGet();
		super.executeAdpositionGet(properties,yag,con);
		YoukuMUBanner muBanner = new YoukuMUBanner();
		super.executeMaterialBannerUpload(properties, muBanner, con);
		YoukuMUBannerAudit muBannerAudit = new YoukuMUBannerAudit();
		super.executeMaterialBannerAudit(properties, muBannerAudit, con);
		YoukuMUVideo muVideo = new YoukuMUVideo();
		super.executeMaterialVideoUpload(properties, muVideo, con);
		YoukuMUVideoAudit muVideoAudit = new YoukuMUVideoAudit();
		super.executeMaterialVideoAudit(properties, muVideoAudit, con);
		YoukuMUAdvInfo muAdvInfo = new YoukuMUAdvInfo();
		super.executeAdvInfoUpload(properties, muAdvInfo, con);
		YoukuMUAdvInfoAudit muAdvInfoAudit = new YoukuMUAdvInfoAudit();
		super.executeMaterialAdvInfoAudit(properties, muAdvInfoAudit, con);
	}


}
