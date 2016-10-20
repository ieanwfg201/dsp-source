package com.kritter.naterial_upload.valuemaker.banner;

import com.alibaba.fastjson.JSON;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialType;
import com.kritter.material_upload.common.banner.MUBanner;
import com.kritter.naterial_upload.valuemaker.entity.HttpUtils;
import com.kritter.naterial_upload.valuemaker.entity.VamMaterialUploadEntity;
import com.kritter.naterial_upload.valuemaker.entity.VamQueryEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class VamMUBanner implements MUBanner {
    private static final Logger LOG = LoggerFactory.getLogger(VamMUBanner.class);
    @Getter
    @Setter
    private String dspid;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
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
    private LinkedList<VamQueryEntity> vamQueryEntityList;
    @Getter
    @Setter
    private String vam_material_add_url;

    @Override
    public void init(Properties properties) {
        setDspid(properties.getProperty("vam_dsp_id").toString());
        setPubIncId(Integer.parseInt(properties.getProperty("vam_pubIncId").toString()));
        setDateNow(new Date());
        setUsername(properties.getProperty("vam_username").toString());
        setPassword(properties.getProperty("vam_password").toString());
        setVam_material_add_url(properties.getProperty("vam_url_prefix").toString() + properties.getProperty("vam_prefix_banner_add").toString());
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
                startDate = DateUtils.addDays(startDate, 0 - Integer.parseInt(properties.getProperty("vam_init_date_go_back").toString()));
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
    public void removeDisassociatedCreative(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        PreparedStatement pstmt = null;
        PreparedStatement updatestmt = null;
        try {
            pstmt = con.prepareStatement(VamBannerQuery.removedCreativesQuery);
            pstmt.setString(1, getStartDateStr());
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                updatestmt = con.prepareStatement(VamBannerQuery.updateRemovedCreatives);
                updatestmt.setInt(1, rset.getInt("internalid"));
                updatestmt.executeUpdate();
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
            if (updatestmt != null) {
                try {
                    updatestmt.close();
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
            pstmt = con.prepareStatement(VamBannerQuery.selectQuery);
            pstmt.setString(1, getStartDateStr());
            ResultSet rset = pstmt.executeQuery();
            boolean isFirst = true;
            while (rset.next()) {
                if (isFirst) {
                    vamQueryEntityList = new LinkedList<VamQueryEntity>();
                    isFirst = false;
                }
                VamQueryEntity vqe = new VamQueryEntity();
                vqe.setAdvId(rset.getInt("advId"));
                vqe.setAdvName(rset.getString("advName"));
                vqe.setCampaignId(rset.getInt("campaignId"));
                vqe.setCampaignName(rset.getString("campaignName"));
                vqe.setCampaignStartDate(rset.getTimestamp("campaignStartDate").getTime());
                vqe.setCampaignEndDate(rset.getTimestamp("campaignEndDate").getTime());
                vqe.setCampaignStatus(rset.getInt("campaignStatus"));
                vqe.setAdId(rset.getInt("adId"));
                vqe.setAdName(rset.getString("adName"));
                vqe.setAdStatus(rset.getInt("adStatus"));
                vqe.setLanding_url(rset.getString("landing_url"));
                vqe.setCreativeId(rset.getInt("creativeId"));
                vqe.setCreativeName(rset.getString("creativeName"));
                vqe.setCreativeStatus(rset.getInt("creativeStatus"));
                vqe.setResource_uri_ids(rset.getString("resource_uri_ids"));
                vqe.setBannerId(rset.getInt("bannerId"));
                vqe.setResource_uri(rset.getString("resource_uri"));
                vqe.setWidth(rset.getInt("width"));
                vqe.setHeight(rset.getInt("height"));
                vqe.setCreativeGuid(rset.getString("creativeGuid"));
                vqe.setCategory(rset.getInt("category"));
                vamQueryEntityList.add(vqe);
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
        if (vamQueryEntityList == null) {
            return;
        }
        for (VamQueryEntity vqe : vamQueryEntityList) {
            if (vqe == null) {
                continue;
            }
            PreparedStatement pstmt = null;
            PreparedStatement cpstmt = null;
            try {
                pstmt = con.prepareStatement(VamBannerQuery.getBannerUpload);
                pstmt.setInt(1, getPubIncId());
                pstmt.setInt(2, vqe.getAdvId());
                pstmt.setInt(3, vqe.getCampaignId());
                pstmt.setInt(4, vqe.getAdId());
                pstmt.setInt(5, vqe.getCreativeId());
                pstmt.setInt(6, vqe.getBannerId());
                ResultSet rset = pstmt.executeQuery();
                String split[] = vqe.getResource_uri().split("/");
                String[] materialurl = {properties.getProperty("cdn_url").toString() + split[split.length - 1]};
                DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");

                java.net.URL url = new java.net.URL(vqe.getLanding_url());
                String host = url.getHost();
                String[] Adomain_list = {host};
                VamMaterialUploadEntity bannerEntity = new VamMaterialUploadEntity(vqe.getCreativeGuid(),
                        "{!vam_click_url}{!dsp_click_url}" + vqe.getLanding_url(), vqe.getWidth(),
                        vqe.getHeight(), 1, 1, vqe.getCategory(), Adomain_list, materialurl, "title", "test");

                String newInfoStr = JSON.toJSONString(bannerEntity);
                if (rset.next()) {
                    String info = rset.getString("info");
                    int adxbasedexhangesstatus = rset.getInt("adxbasedexhangesstatus");

                    cpstmt = con.prepareStatement(VamBannerQuery.updatetBannerUpload);
                    cpstmt.setInt(2, vqe.getCampaignStatus());
                    cpstmt.setInt(3, vqe.getAdStatus());
                    cpstmt.setInt(4, vqe.getCreativeStatus());
                    cpstmt.setTimestamp(5, new Timestamp(dateNow.getTime()));
                    if (newInfoStr.equals(info)) {
                        cpstmt.setInt(1, adxbasedexhangesstatus);
                        cpstmt.setString(6, info);
                    } else {
                        cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
                        cpstmt.setString(6, newInfoStr);
                    }
                    cpstmt.setInt(7, rset.getInt("internalid"));
                    System.out.println(cpstmt);
                    cpstmt.executeUpdate();

                } else {
                    cpstmt = con.prepareStatement(VamBannerQuery.insertBannerUpload);
                    cpstmt.setInt(1, getPubIncId());
                    cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
                    cpstmt.setInt(3, vqe.getAdvId());
                    cpstmt.setInt(4, vqe.getCampaignId());
                    cpstmt.setInt(5, vqe.getCampaignStatus());
                    cpstmt.setInt(6, vqe.getAdId());
                    cpstmt.setInt(7, vqe.getAdStatus());
                    cpstmt.setInt(8, vqe.getCreativeId());
                    cpstmt.setInt(9, vqe.getCreativeStatus());
                    cpstmt.setInt(10, vqe.getBannerId());
                    cpstmt.setTimestamp(11, new Timestamp(dateNow.getTime()));

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


    @Override
    public void uploadmaterial(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        LOG.info("UPLOADING MATERIAL");
        PreparedStatement pstmt = null;
        PreparedStatement cpstmt = null;
        try {
            pstmt = con.prepareStatement(VamBannerQuery.selectforUpload);
            pstmt.setInt(1, getPubIncId());
            ResultSet rset = pstmt.executeQuery();

            while (rset.next()) {
                try {
                    String info = rset.getString("info");
                    Map<String, String> result = HttpUtils.post(vam_material_add_url, info, username, password);
                    LOG.debug(info);
                    LOG.debug(JSON.toJSONString(result));

                    if (result.get("StatusCode") != null && result.get("StatusCode").equals("200")) {
                        cpstmt = con.prepareStatement(VamBannerQuery.updatetBannerStatusMessage);
                        cpstmt.setInt(1, AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
                        cpstmt.setString(2, JSON.toJSONString(result));
                        cpstmt.setTimestamp(3, new Timestamp(dateNow.getTime()));
                        cpstmt.setInt(4, rset.getInt("internalId"));
                        cpstmt.executeUpdate();
                    } else {
                        cpstmt = con.prepareStatement(VamBannerQuery.updatetBannerStatusMessage);
                        cpstmt.setInt(1, AdxBasedExchangesStates.ERROR.getCode());
                        cpstmt.setString(2, JSON.toJSONString(result));
                        cpstmt.setTimestamp(3, new Timestamp(dateNow.getTime()));
                        cpstmt.setInt(4, rset.getInt("internalId"));
                        cpstmt.executeUpdate();
                    }
                } catch (Exception e) {
                    LOG.error(e.toString());
                }
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

    @Override
    public void updaterun(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        PreparedStatement pstmt = null;
        try {
            if (lastRunPresent) {
                pstmt = con.prepareStatement(VamBannerQuery.update_material_state);
                pstmt.setTimestamp(1, new Timestamp(dateNow.getTime()));
                pstmt.setInt(2, MaterialType.BANNERUPLOAD.getCode());
                pstmt.setInt(3, getPubIncId());
            } else {
                pstmt = con.prepareStatement(VamBannerQuery.insert_material_state);
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

}
