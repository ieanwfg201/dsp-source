package com.kritter.naterial_upload.cloudcross.advertiser;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.constants.MaterialType;
import com.kritter.material_upload.common.advInfo.MUAdvInfo;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertiserEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossError;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by hamlin on 16-9-28.
 */
@SuppressWarnings("SqlDialectInspection")
public class CloudCrossMUAdvInfo implements MUAdvInfo {
    private static final Logger LOG = LogManager.getLogger("material.root");
    private ObjectMapper objectMapper = new ObjectMapper();
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
    private LinkedList<CloudCrossAdvertiserEntity> cloudCrossQueryEntityList;
    private CloudCrossAdvertiser cloudCrossAdvertiser = null;

    @Override
    public void init(Properties properties) {
        setDspid(properties.getProperty("cloudcross_dsp_id"));
        setToken(properties.getProperty("cloudcross_token"));
        setPubIncId(Integer.parseInt(properties.getProperty("cloudcross_pubIncId")));
        dateNow = new Date();
        String creative_dspid_token = "?dspId=" + getDspid() + "&token=" + getToken();
        String cloudcross_url_prefix = properties.getProperty("cloudcross_url_prefix");
        String advertiser_add_url = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_add") + creative_dspid_token;
        String cloudcross_prefix_advertiser_update = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_update") + creative_dspid_token;
        String cloudcross_prefix_advertiser_status = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_status") + creative_dspid_token;
        cloudCrossAdvertiser = new CloudCrossAdvertiser(advertiser_add_url, cloudcross_prefix_advertiser_update, null, null, cloudcross_prefix_advertiser_status);
    }

    @Override
    public void getLastRun(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("select * from material_upload_state where pubIncId=? and materialtype=" + MaterialType.ADVINFOUPLOAD.getCode());
            pstmt.setInt(1, getPubIncId());
            ResultSet rset = pstmt.executeQuery();
            if (rset.next()) {
                startDate = new Date(rset.getTimestamp("last_modified").getTime());
                lastRunPresent = true;
                LOG.debug("PubIncId {} found DB", pubIncId);
            } else {
                startDate = new Date();
                startDate = DateUtils.addDays(startDate, 0 - Integer.parseInt(properties.getProperty("cloudcross_init_date_go_back")));
            }
            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s = dfm.format(startDate);
            LOG.info("Start Date for AdvInfo Upload {}", s);
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
            pstmt = con.prepareStatement(CloudCrossAdvInfoQuery.selectQuery);
            pstmt.setString(1, getStartDateStr());
            ResultSet rset = pstmt.executeQuery();
            boolean isFirst = true;
            while (rset.next()) {
                if (isFirst) {
                    cloudCrossQueryEntityList = new LinkedList<>();
                    isFirst = false;
                }
                CloudCrossAdvertiserEntity ccade = new CloudCrossAdvertiserEntity();
                ccade.setDspId(Integer.parseInt(getDspid()));
                ccade.setAdvertiserId(rset.getInt("advId"));
                ccade.setIndustryId(Integer.parseInt(getIndustryIdByUiMMACode(con, rset)));
                ccade.setRegName(rset.getString("advName"));
                ccade.setName(rset.getInt("advId"));

                cloudCrossQueryEntityList.add(ccade);
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

    private String getIndustryIdByUiMMACode(Connection con, ResultSet rset) throws java.io.IOException, SQLException {
        Integer industryId = -1;
        if (rset.getObject("firstind") != null) {
            industryId = Integer.parseInt(rset.getString("firstind"));
        }
        if (rset.getObject("secondind") != null) {
            industryId = Integer.parseInt(rset.getString("secondind"));
        }
        ResultSet resultSet;
        try (PreparedStatement statement = con.prepareStatement(CloudCrossAdvInfoQuery.selectSupplyIndustryIdByUIMMACategoriesId)) {
            statement.setInt(1, industryId);
            resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getString("supplycode") : "-1";
        }
    }

    @Override
    public void insertOrUpdateAdcInfoUpload(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        if (cloudCrossQueryEntityList == null) {
            return;
        }
        for (CloudCrossAdvertiserEntity ccae : cloudCrossQueryEntityList) {
            if (ccae == null) {
                continue;
            }
            PreparedStatement pstmt = null;
            PreparedStatement cpstmt = null;
            try {
                pstmt = con.prepareStatement(CloudCrossAdvInfoQuery.getAdvInfoUpload);
                pstmt.setInt(1, getPubIncId());
                pstmt.setInt(2, ccae.getAdvertiserId());
                ResultSet rset = pstmt.executeQuery();
                CloudCrossAdvInfoLocaLMaterialUploadEntity ccalmue = new CloudCrossAdvInfoLocaLMaterialUploadEntity();
                ccalmue.setAddress(null);
                ccalmue.setBrand(null);
                ccalmue.setContacts(null);
                ccalmue.setName(ccae.getRegName());
                ccalmue.setTel(null);
                String newInfo = objectMapper.writeValueAsString(ccae);
                if (rset.next()) {
                    String info = rset.getString("info");
                    int adxbasedexhangesstatus = rset.getInt("adxbasedexhangesstatus");
                    cpstmt = con.prepareStatement(CloudCrossAdvInfoQuery.updatetAdvInfoUpload);
                    if (info.equals(newInfo)) {
                        cpstmt.setInt(1, adxbasedexhangesstatus);
                    } else {
                        cpstmt.setInt(1, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
                    }
                    cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
                    cpstmt.setString(3, newInfo);
                    cpstmt.setInt(4, rset.getInt("internalid"));
                    cpstmt.executeUpdate();
                } else {
                    cpstmt = con.prepareStatement(CloudCrossAdvInfoQuery.insertAdvInfoUpload);
                    cpstmt.setInt(1, getPubIncId());
                    cpstmt.setInt(2, AdxBasedExchangesStates.READYTOSUBMIT.getCode());
                    cpstmt.setInt(3, ccae.getAdvertiserId());
                    cpstmt.setTimestamp(4, new Timestamp(dateNow.getTime()));
                    cpstmt.setString(5, newInfo);
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
        LOG.info("UPLOADING ADVINFO FOR CLOUDCROSS");
        PreparedStatement pstmt = null;
        PreparedStatement cpstmt = null;
        try {
            pstmt = con.prepareStatement(CloudCrossAdvInfoQuery.selectforUpload);
            pstmt.setInt(1, getPubIncId());
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                int internalId = rset.getInt("internalId");
                boolean isSuccess = false;
                String errorCode = "";
                try {
                    String postBody = rset.getString("info");
                    LOG.info("CLOUDCROSS UPLOADING ADVINFO POSTBODY:" + postBody);
                    List<CloudCrossAdvertiserEntity> list = new ArrayList<>();
                    CloudCrossAdvertiserEntity entity = objectMapper.readValue(postBody, CloudCrossAdvertiserEntity.class);
                    entity.setHomepage("null");
                    entity.setTel("null");
                    entity.setEmail("null");
                    entity.setLicencePath("null");
                    entity.setIdPath("null");
                    entity.setOrgPath("null");
                    entity.setCpiPath("null");
                    list.add(entity);
                    List<CloudCrossResponse> responses = cloudCrossAdvertiser.add(list);
                    LOG.info(objectMapper.writeValueAsString(responses));
                    if (responses != null && responses.size() > 0) {
                        // [{"status":0,"success":{"index":1,"code":200,"message":"插入成功","bannerId":null,"field":null},"error":null}]
                        CloudCrossResponse cloudCrossResponse = responses.get(0);
                        if (cloudCrossResponse != null) {
                            CloudCrossResponse.Success success = cloudCrossResponse.getSuccess();
                            CloudCrossError error = cloudCrossResponse.getError();
                            if (success != null && success.getCode() == 200 && success.getMessage().equals("插入成功")) {
                                cpstmt = con.prepareStatement(CloudCrossAdvInfoQuery.updatetAdvinfoStatus.replaceAll("<id>", internalId + ""));
                                cpstmt.setInt(1, AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
                                cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
                                cpstmt.setString(3, success.getMessage());
                                cpstmt.executeUpdate();
                                isSuccess = true;
                                LOG.info("advertiser upload success!");
                            } else if (error != null) {
                                cpstmt = con.prepareStatement(CloudCrossAdvInfoQuery.updatetAdvinfoStatus.replaceAll("<id>", internalId + ""));
                                cpstmt.setInt(1, AdxBasedExchangesStates.UPLOADFAIL.getCode());
                                cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
                                cpstmt.setString(3, error.getMessage());
                                cpstmt.executeUpdate();
                                isSuccess = false;
                            } else {

                                if (error != null && StringUtils.isNotEmpty(error.getMessage())) {
                                    errorCode = error.getMessage();
                                } else {
                                    errorCode = objectMapper.writeValueAsString(cloudCrossResponse);
                                }
                                LOG.info("advertiser upload failed! message : " + errorCode);
                            }
                        }
                    }
                } catch (Exception e1) {
                    LOG.error(e1.getMessage(), e1);
                }
                if (!isSuccess) {
                    try (PreparedStatement cpstmt1 = con.prepareStatement(CloudCrossAdvInfoQuery.updatetAdvinfoStatus.replaceAll("<id>", internalId + ""))) {
                        cpstmt1.setInt(1, AdxBasedExchangesStates.UPLOADFAIL.getCode());
                        cpstmt1.setTimestamp(2, new Timestamp(dateNow.getTime()));
                        cpstmt1.setString(3, errorCode);
                        cpstmt1.executeUpdate();
                    }
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

    private PreparedStatement updatetAdvSuccess(Connection con, PreparedStatement cpstmt, int internalId, String errorCode) throws SQLException {
        cpstmt = con.prepareStatement(CloudCrossAdvInfoQuery.updatetAdvinfoStatus.replaceAll("<id>", internalId + ""));
        cpstmt.setInt(1, AdxBasedExchangesStates.UPLOADSUCCESS.getCode());
        cpstmt.setTimestamp(2, new Timestamp(dateNow.getTime()));
        cpstmt.setString(3, errorCode);
        cpstmt.executeUpdate();
        return cpstmt;
    }

    @Override
    public void updaterun(Properties properties, Connection con) {
        if (!isPerformTransaction()) {
            return;
        }
        PreparedStatement pstmt = null;
        try {
            if (lastRunPresent) {
                pstmt = con.prepareStatement(CloudCrossAdvInfoQuery.update_material_state);
                pstmt.setTimestamp(1, new Timestamp(dateNow.getTime()));
                pstmt.setInt(2, MaterialType.ADVINFOUPLOAD.getCode());
                pstmt.setInt(3, getPubIncId());
            } else {
                pstmt = con.prepareStatement(CloudCrossAdvInfoQuery.insert_material_state);
                pstmt.setInt(1, getPubIncId());
                pstmt.setInt(2, MaterialType.ADVINFOUPLOAD.getCode());
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
