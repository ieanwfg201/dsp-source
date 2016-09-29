package com.kritter.naterial_upload.cloudcross.executor;

import com.kritter.material_upload.common.executor.UploadExecutor;
import com.kritter.material_upload.common.executor.UploadExecutorImpl;
import com.kritter.naterial_upload.cloudcross.advertiser.CloudCrossMUAdvInfo;
import com.kritter.naterial_upload.cloudcross.advertiser.CloudCrossMUAdvInfoAudit;
import com.kritter.naterial_upload.cloudcross.banner.CloudCrossMUBanner;
import com.kritter.naterial_upload.cloudcross.banner.CloudCrossMUBannerAudit;

import java.sql.Connection;
import java.util.Properties;

/**
 * Created by hamlin on 16-9-19.
 */
public class CloudCrossUploadExecutor extends UploadExecutorImpl implements UploadExecutor {
    @Override
    public void execute(Properties properties, Connection con) {
        int pubInc = Integer.parseInt(properties.getProperty("cloudcross_pubIncId"));
        super.checkJobs(properties, con, pubInc);

        CloudCrossMUBanner muBanner = new CloudCrossMUBanner();
        super.executeMaterialBannerUpload(properties, muBanner, con);
        CloudCrossMUBannerAudit cloudCrossMUBannerAudit = new CloudCrossMUBannerAudit();
        super.executeMaterialBannerAudit(properties, cloudCrossMUBannerAudit, con);

        CloudCrossMUAdvInfo muAdvInfo = new CloudCrossMUAdvInfo();
        super.executeAdvInfoUpload(properties, muAdvInfo, con);
        CloudCrossMUAdvInfoAudit muAdvInfoAudit = new CloudCrossMUAdvInfoAudit();
        super.executeMaterialAdvInfoAudit(properties, muAdvInfoAudit, con);
    }
}
