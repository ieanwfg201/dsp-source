package com.kritter.naterial_upload.cloudcross.executor;

import com.kritter.material_upload.common.executor.UploadExecutor;
import com.kritter.material_upload.common.executor.UploadExecutorImpl;
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
        CloudCrossMUBanner muBanner = new CloudCrossMUBanner();
        super.executeMaterialBannerUpload(properties, muBanner, con);
        // 物料审核状态查询
        CloudCrossMUBannerAudit cloudCrossMUBannerAudit = new CloudCrossMUBannerAudit();
        super.executeMaterialBannerAudit(properties, cloudCrossMUBannerAudit, con);

    }
}
