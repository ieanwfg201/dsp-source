package com.kritter.naterial_upload.cloudcross.banner;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialType;
import com.kritter.material_upload.common.banner.MUBanner;
import com.kritter.naterial_upload.cloudcross.advertiser.CloudCrossAdvertiser;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossBannerEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossQueryEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class CloudCrossMUBanner implements MUBanner {
    private static final Logger LOG = LoggerFactory.getLogger(CloudCrossMUBanner.class);
    @Getter
    @Setter
    private String dspid;
    @Getter
    @Setter
    private String token;
    @Getter
    @Setter
    private Integer pubIncId;
    @Getter
    @Setter
    private boolean performTransaction = true;
    @Getter
    @Setter
    private Date dateNow;
    @Getter
    @Setter
    private Date startDate;
    @Getter
    @Setter
    private String startDateStr;
    @Getter
    @Setter
    private boolean lastRunPresent = false;
    @Getter
    @Setter
    private LinkedList<CloudCrossQueryEntity> cloudcrossQueryEntityList;

    private ObjectMapper objectMapper = new ObjectMapper();
    private CloudCrossAdvertiser cloudCrossAdvertiser;
    private CloudCrossCreative cloudCrossCreative;

    @Override
    public void init(Properties properties) {
        setDspid(properties.getProperty("cloudcross_dsp_id").toString());
        setToken(properties.getProperty("cloudcross_token").toString());
        setPubIncId(Integer.parseInt(properties.getProperty("cloudcross_pubIncId").toString()));
        dateNow = new Date();

        String creative_dspid_token = "?dspId=" + getDspid() + "&token=" + getToken();
        String cloudcross_url_prefix = properties.getProperty("cloudcross_url_prefix").toString();
        String cloudcross_prefix_banner_add = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_banner_add").toString() + creative_dspid_token;
        String cloudcross_prefix_banner_update = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_banner_update").toString() + creative_dspid_token;
        String cloudcross_prefix_banner_status = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_banner_status").toString() + creative_dspid_token;
        this.cloudCrossCreative = new CloudCrossCreative(cloudcross_prefix_banner_add, cloudcross_prefix_banner_update, null, null, cloudcross_prefix_banner_status);


    }

    @Override
    public void getLastRun(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("select * from material_upload_state where pubIncId=? and materialtype=" + MaterialType.BANNERUPLOAD.getCode());
            pstmt.setInt(1, getPubIncId());
            ResultSet rset = pstmt.executeQuery();
            if (rset.next()) {
                startDate = new Date(rset.getTimestamp("last_modified").getTime());
                lastRunPresent = true;
                LOG.debug("PubIncId {} found DB", pubIncId);
            } else {
                startDate = new Date();
                startDate = DateUtils.addDays(startDate, 0 - Integer.parseInt(properties.getProperty("cloudcross_init_date_go_back").toString()));
            }
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s = dfm.format(startDate);
            LOG.info("Start Date for Banner Upload {}", s);
            setStartDateStr(s);
        } catch (Exception e) {
            setPerformTransaction(false);
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
    public void getModifiedEntities(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(CloudCrossBannerQuery.selectQuery);
            pstmt.setString(1, getStartDateStr());
            ResultSet rset = pstmt.executeQuery();
            boolean isFirst = true;
            while (rset.next()) {
                if (isFirst) {
                    cloudcrossQueryEntityList = new LinkedList<>();
                    isFirst = false;
                }
                CloudCrossQueryEntity ccqe = new CloudCrossQueryEntity();
                ccqe.setAdvId(rset.getInt("advId"));
                ccqe.setAdvName(rset.getString("advName"));
                ccqe.setCampaignId(rset.getInt("campaignId"));
                ccqe.setCampaignName(rset.getString("campaignName"));
                ccqe.setCampaignStartDate(rset.getTimestamp("campaignStartDate").getTime());
                ccqe.setCampaignEndDate(rset.getTimestamp("campaignEndDate").getTime());
                ccqe.setCampaignStatus(rset.getInt("campaignStatus"));
                ccqe.setAdId(rset.getInt("adId"));
                ccqe.setAdName(rset.getString("adName"));
                ccqe.setAdStatus(rset.getInt("adStatus"));
                ccqe.setLanding_url(rset.getString("landing_url"));
                ccqe.setCreativeId(rset.getInt("creativeId"));
                ccqe.setCreativeName(rset.getString("creativeName"));
                ccqe.setCreativeStatus(rset.getInt("creativeStatus"));
                ccqe.setResource_uri_ids(rset.getString("resource_uri_ids"));
                ccqe.setBannerId(rset.getInt("bannerId"));
                ccqe.setResource_uri(rset.getString("resource_uri"));
                ccqe.setWidth(rset.getInt("width"));
                ccqe.setHeight(rset.getInt("height"));
                cloudcrossQueryEntityList.add(ccqe);
            }
        } catch (Exception e) {
            setPerformTransaction(false);
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
    public void insertOrUpdateBannerUpload(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        if (cloudcrossQueryEntityList == null) {
            return;
        }
        for (CloudCrossQueryEntity cqe : cloudcrossQueryEntityList) {
            if (cqe == null) {
                continue;
            }
            PreparedStatement pstmt = null;
            PreparedStatement cpstmt = null;
            try {
                pstmt = con.prepareStatement(CloudCrossBannerQuery.getBannerUpload);
                pstmt.setInt(1, getPubIncId());
                pstmt.setInt(2, cqe.getAdvId());
                pstmt.setInt(3, cqe.getCampaignId());
                pstmt.setInt(4, cqe.getAdId());
                pstmt.setInt(5, cqe.getCreativeId());
                pstmt.setInt(6, cqe.getBannerId());
                ResultSet rset = pstmt.executeQuery();
                String split[] = cqe.getResource_uri().split("/");
                String materialurl = properties.getProperty("cdn_url").toString() + split[split.length - 1];
                DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
                String campaignStartDate = dfm.format(new Date(cqe.getCampaignStartDate()));
                String campaignEndDate = dfm.format(new Date(cqe.getCampaignEndDate()));
                if (rset.next()) {
                    String info = rset.getString("info");
                    int adxbasedexhangesstatus = rset.getInt("adxbasedexhangesstatus");
//					CloudCrossMaterialUploadEntity ymue=new CloudCrossMaterialUploadEntity(materialurl, cqe.getLanding_url(), cqe.getAdvName(), campaignStartDate, campaignEndDate, null);
                    CloudCrossBannerEntity bannerEntity = new CloudCrossBannerEntity();
                    bannerEntity.setAdvertiserId(cqe.getAdvId());
                    bannerEntity.setBannerId(null);
                    bannerEntity.setHeight(cqe.getHeight());
                    bannerEntity.setWidth(cqe.getWidth());
                    bannerEntity.setPath(materialurl);
                    bannerEntity.setRheight(cqe.getHeight());
                    bannerEntity.setRwidth(cqe.getWidth());
                    String newInfoStr = objectMapper.writeValueAsString(bannerEntity);
                    cpstmt = con.prepareStatement(CloudCrossBannerQuery.updatetBannerUpload);
                    cpstmt.setInt(2, cqe.getCampaignStatus());
                    cpstmt.setInt(3, cqe.getAdStatus());
                    cpstmt.setInt(4, cqe.getCreativeStatus());
                    cpstmt.setTimestamp(5, new Timestamp(dateNow.getTime()));
                    String oldInfo = clearPublisherParamBannerId(info);
                    if (newInfoStr.equals(oldInfo)) {
                        cpstmt.setInt(1, adxbasedexhangesstatus);
                        cpstmt.setString(6, info);
                    } else {
                        cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
                        cpstmt.setString(6, newInfoStr);
                    }
                    cpstmt.setInt(7, rset.getInt("internalid"));
                    cpstmt.executeUpdate();

                } else {
                    cpstmt = con.prepareStatement(CloudCrossBannerQuery.insertBannerUpload);
                    cpstmt.setInt(1, getPubIncId());
                    cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
                    cpstmt.setInt(3, cqe.getAdvId());
                    cpstmt.setInt(4, cqe.getCampaignId());
                    cpstmt.setInt(5, cqe.getCampaignStatus());
                    cpstmt.setInt(6, cqe.getAdId());
                    cpstmt.setInt(7, cqe.getAdStatus());
                    cpstmt.setInt(8, cqe.getCreativeId());
                    cpstmt.setInt(9, cqe.getCreativeStatus());
                    cpstmt.setInt(10, cqe.getBannerId());
                    cpstmt.setTimestamp(11, new Timestamp(dateNow.getTime()));
                    CloudCrossBannerEntity bannerEntity = new CloudCrossBannerEntity();
                    bannerEntity.setAdvertiserId(cqe.getAdvId());
                    bannerEntity.setBannerId(null);
                    bannerEntity.setHeight(cqe.getHeight());
                    bannerEntity.setWidth(cqe.getWidth());
                    bannerEntity.setPath(materialurl);
                    bannerEntity.setRheight(cqe.getHeight());
                    bannerEntity.setRwidth(cqe.getWidth());
                    String newInfoStr = objectMapper.writeValueAsString(bannerEntity);
                    cpstmt.setString(12, newInfoStr);
                    cpstmt.executeUpdate();
                }
            } catch (Exception e) {
                setPerformTransaction(false);
                LOG.error(e.getMessage(), e);
            } finally {
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (SQLException e) {
                        LOG.error(e.getMessage(), e);
                    }
                    if (cpstmt != null) {
                        try {
                            cpstmt.close();
                        } catch (SQLException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
            }

        }
    }

    private String clearPublisherParamBannerId(String info) throws java.io.IOException {
        CloudCrossBannerEntity readValue = objectMapper.readValue(info, CloudCrossBannerEntity.class);
        readValue.setBannerId(null);
        return objectMapper.writeValueAsString(readValue);
    }


    @Override
    public void uploadmaterial(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        LOG.info("BEGIN UPLOAD BANNER FOR CLOUDCROSS");
        try (PreparedStatement pstmt = con.prepareStatement(CloudCrossBannerQuery.selectforUpload)) {
            pstmt.setInt(1, getPubIncId());
            ResultSet rset = pstmt.executeQuery();
            List<CloudCrossBannerEntity> materialList = new LinkedList<>();
            StringBuffer sBuff = new StringBuffer("");
            while (rset.next()) {
                //System.out.println(rset.getString("info"));
                String infoByDB = rset.getString("info");
                CloudCrossBannerEntity info = objectMapper.readValue(infoByDB, CloudCrossBannerEntity.class);
                materialList.add(info);
                LOG.info("CLOUDCROSS BANNER UPLOAD RESPONSE:" + infoByDB);

                boolean isSuccess = false;
                //[{"status":0,"success":{"message":"插入成功","index":1,"bannerId":32,"code":200}},{"status":0,"success":{"message":"插入成功","index":2,"code":200}}]
                List<CloudCrossResponse> add = cloudCrossCreative.add(materialList);
                String out = objectMapper.writeValueAsString(add);
                LOG.info(out);
                if (out != null && add != null && add.size() > 0) {
                    CloudCrossResponse cloudCrossResponse = add.get(0);
                    if (cloudCrossResponse != null && cloudCrossResponse.getSuccess() != null && cloudCrossResponse.getSuccess().getCode() == 200) {
                        info.setBannerId(cloudCrossResponse.getSuccess().getBannerId());
                        try (PreparedStatement cpstmt = con.prepareStatement(CloudCrossBannerQuery.updatetBannerStatus)) {
                            cpstmt.setInt(1, AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
                            cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
                            cpstmt.setString(3, objectMapper.writeValueAsString(info));
                            cpstmt.setInt(4,getPubIncId());
                            cpstmt.setInt(5,rset.getInt("bannerId"));
                            cpstmt.executeUpdate();
                            isSuccess = true;
                        }
                    }
                }
                if (!isSuccess) {
                    try (PreparedStatement cpstmt1 = con.prepareStatement(CloudCrossBannerQuery.updatetBannerStatus)) {
                        cpstmt1.setInt(1, AdxBasedExchangesStates.UPLOADFAIL.getCode());
                        cpstmt1.setTimestamp(2, new Timestamp(dateNow.getTime()));
                        cpstmt1.setString(3, objectMapper.writeValueAsString(info));
                        cpstmt1.setInt(4,getPubIncId());
                        cpstmt1.setInt(5,rset.getInt("bannerId"));
                        cpstmt1.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            setPerformTransaction(false);
            LOG.error(e.getMessage(), e);
        }

    }

    @Override
    public void updaterun(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        PreparedStatement pstmt = null;
        try {
            if (lastRunPresent) {
                pstmt = con.prepareStatement(CloudCrossBannerQuery.update_material_state);
                pstmt.setTimestamp(1, new Timestamp(dateNow.getTime()));
                pstmt.setInt(2, MaterialType.BANNERUPLOAD.getCode());
                pstmt.setInt(3, getPubIncId());
            } else {
                pstmt = con.prepareStatement(CloudCrossBannerQuery.insert_material_state);
                pstmt.setInt(1, getPubIncId());
                pstmt.setInt(2, MaterialType.BANNERUPLOAD.getCode());
                pstmt.setTimestamp(3, new Timestamp(dateNow.getTime()));
            }
            pstmt.executeUpdate();
        } catch (Exception e) {
            setPerformTransaction(false);
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
    public void removeDisassociatedCreative(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        try (PreparedStatement pstmt = con.prepareStatement(CloudCrossBannerQuery.removedCreativesQuery)) {
            pstmt.setString(1, getStartDateStr());
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                try (PreparedStatement updatestmt = con.prepareStatement(CloudCrossBannerQuery.updateRemovedCreatives)) {
                    updatestmt.setInt(1, rset.getInt("internalid"));
                    updatestmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            setPerformTransaction(false);
            LOG.error(e.getMessage(), e);
        }
    }

}
