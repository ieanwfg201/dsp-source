package com.kritter.material_upload.common.executor;

import com.kritter.material_upload.common.adposition.AdPositionGet;
import com.kritter.material_upload.common.advInfo.MUADvInfoAudit;
import com.kritter.material_upload.common.advInfo.MUAdvInfo;
import com.kritter.material_upload.common.banner.MUBanner;
import com.kritter.material_upload.common.banner.MUBannerAudit;
import com.kritter.material_upload.common.video.MUVideo;
import com.kritter.material_upload.common.video.MUVideoAudit;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public abstract class UploadExecutorImpl implements UploadExecutor {
    private static final Logger LOG = LogManager.getLogger(UploadExecutorImpl.class);
    @Getter
    @Setter
    private boolean advertiser_upload = false;
    @Getter
    @Setter
    private boolean adposition_get = false;
    @Getter
    @Setter
    private boolean banner_upload = false;
    @Getter
    @Setter
    private boolean video_upload = false;

    @Override
    public void execute(Properties properties, Connection con) {
    }

    @Override
    public void checkJobs(Properties properties, Connection con, int pubIncId) {
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("select * from adxbasedexchanges_metadata where pubIncId=?");
            pstmt.setInt(1, pubIncId);
            ResultSet rset = pstmt.executeQuery();
            if (rset.next()) {
                advertiser_upload = rset.getBoolean("advertiser_upload");
                adposition_get = rset.getBoolean("adposition_get");
                banner_upload = rset.getBoolean("banner_upload");
                video_upload = rset.getBoolean("video_upload");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void executeAdpositionGet(Properties properties, AdPositionGet adPositionGet, Connection con, int pubincId) {
        if (!this.adposition_get) {
            LOG.debug("PubIncId {} - ADPOSITION is not set in METADATA", pubincId);
            return;
        }
        if (adPositionGet != null) {
            boolean autoCommit = true;
            try {
                autoCommit = con.getAutoCommit();
                adPositionGet.init(properties);
                adPositionGet.checkAdpositionGet(con, properties);
                adPositionGet.getAdposition(properties);
                con.setAutoCommit(false);
                adPositionGet.insertAdposition(con, properties);
                adPositionGet.updaterun(con, properties);
                con.commit();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            } finally {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void executeMaterialBannerUpload(Properties properties, MUBanner muBanner, Connection con, int pubincId) {
        if (!this.banner_upload) {
            LOG.debug("PubIncId {} - BANNER UPLOAD is not set in METADATA", pubincId);
            return;
        }

        if (muBanner != null) {
            boolean autoCommit = true;
            try {
                autoCommit = con.getAutoCommit();
                muBanner.init(properties);
                con.setAutoCommit(false);
                muBanner.getLastRun(properties, con);
                muBanner.removeDisassociatedCreative(properties, con);
                muBanner.getModifiedEntities(properties, con);
                muBanner.insertOrUpdateBannerUpload(properties, con);
                muBanner.uploadmaterial(properties, con);
                muBanner.updaterun(properties, con);
                con.commit();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            } finally {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void executeMaterialBannerAudit(Properties properties, MUBannerAudit muBannerAudit, Connection con, int pubincId) {
        if (!this.banner_upload) {
            LOG.debug("PubIncId {} - BANNERUPLOAD is not set in METADATA", pubincId);
            return;
        }

        if (muBannerAudit != null) {
            boolean autoCommit = true;
            try {
                autoCommit = con.getAutoCommit();
                muBannerAudit.init(properties);
                con.setAutoCommit(false);
                muBannerAudit.fetchMaterialAudit(properties, con);
                con.commit();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            } finally {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void executeMaterialVideoUpload(Properties properties, MUVideo muVideo, Connection con, int pubincId) {
        if (!this.video_upload) {
            LOG.debug("PubIncId {} - VIDEOUPLOAD is not set in METADATA", pubincId);
            return;
        }

        if (muVideo != null) {
            boolean autoCommit = true;
            try {
                autoCommit = con.getAutoCommit();
                muVideo.init(properties);
                con.setAutoCommit(false);
                muVideo.getLastRun(properties, con);
                muVideo.removeDisassociatedCreative(properties, con);
                muVideo.getModifiedEntities(properties, con);
                muVideo.insertOrUpdateVideoUpload(properties, con);
                muVideo.uploadmaterial(properties, con);
                muVideo.updaterun(properties, con);
                con.commit();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            } finally {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void executeMaterialVideoAudit(Properties properties, MUVideoAudit muVideoAudit, Connection con, int pubincId) {
        if (!this.video_upload) {
            LOG.debug("PubIncId {} - VIDEOUPLOAD is not set in METADATA", pubincId);
            return;
        }

        if (muVideoAudit != null) {
            boolean autoCommit = true;
            try {
                autoCommit = con.getAutoCommit();
                muVideoAudit.init(properties);
                con.setAutoCommit(false);
                muVideoAudit.fetchMaterialAudit(properties, con);
                con.commit();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            } finally {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void executeAdvInfoUpload(Properties properties, MUAdvInfo advInfo, Connection con, int pubincId) {
        if (!this.advertiser_upload) {
            LOG.debug("PubIncId {} - ADVINFOUPLOAD is not set in METADATA", pubincId);
            return;
        }

        if (advInfo != null) {
            boolean autoCommit = true;
            try {
                autoCommit = con.getAutoCommit();
                advInfo.init(properties);
                con.setAutoCommit(false);
                advInfo.getLastRun(properties, con);
                advInfo.getModifiedEntities(properties, con);
                advInfo.insertOrUpdateAdcInfoUpload(properties, con);
                advInfo.uploadmaterial(properties, con);
                advInfo.updaterun(properties, con);
                con.commit();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            } finally {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void executeMaterialAdvInfoAudit(Properties properties, MUADvInfoAudit muAdvInforAudit, Connection con, int pubincId) {
        if (!this.advertiser_upload) {
            LOG.debug("PubIncId {} - ADVINFOUPLOAD is not set in METADATA", pubincId);
            return;
        }

        if (muAdvInforAudit != null) {
            boolean autoCommit = true;
            try {
                autoCommit = con.getAutoCommit();
                muAdvInforAudit.init(properties);
                con.setAutoCommit(false);
                muAdvInforAudit.fetchMaterialAudit(properties, con);
                con.commit();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOG.error(e1.getMessage(), e1);
                }
            } finally {
                try {
                    con.setAutoCommit(autoCommit);
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }
}
