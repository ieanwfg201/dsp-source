package com.kritter.naterial_upload.cloudcross.banner;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.banner.MUBannerAudit;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossBannerStateResponseEntiry;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class CloudCrossMUBannerAudit implements MUBannerAudit {
    private static final Logger LOG = LoggerFactory.getLogger(CloudCrossMUBannerAudit.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Getter
    @Setter
    private String dspid;
    @Getter
    @Setter
    private String token;
    @Getter
    @Setter
    private Integer pubIncId;
    private CloudCrossCreative cloudCrossCreative;

    @Override
    public void init(Properties properties) {
        setDspid(properties.getProperty("cloudcross_dsp_id"));
        setToken(properties.getProperty("cloudcross_token"));
        setPubIncId(Integer.parseInt(properties.getProperty("cloudcross_pubIncId")));

        String creative_dspid_token = "?dspId=" + getDspid() + "&token=" + getToken();
        String cloudcross_url_prefix = properties.getProperty("cloudcross_url_prefix");
        String cloudcross_prefix_banner_add = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_banner_add") + creative_dspid_token;
        String cloudcross_prefix_banner_update = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_banner_update") + creative_dspid_token;
        String cloudcross_prefix_banner_status = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_banner_status") + creative_dspid_token;
        this.cloudCrossCreative = new CloudCrossCreative(cloudcross_prefix_banner_add, cloudcross_prefix_banner_update, null, null, cloudcross_prefix_banner_status);


    }


    @Override
    public void fetchMaterialAudit(Properties properties, Connection con) {
        try (PreparedStatement pstmt = con.prepareStatement(CloudCrossBannerQuery.selectforAudit)) {
            pstmt.setInt(1, getPubIncId());
            ResultSet rset = pstmt.executeQuery();
            Timestamp ts = new Timestamp(new Date().getTime());
            while (rset.next()) {
                CloudCrossResponse ccbe = objectMapper.readValue(rset.getString("message"), CloudCrossResponse.class);
                Integer bannerId;

                if (ccbe == null || ccbe.getSuccess() == null || (bannerId = ccbe.getSuccess().getBannerId()) == 0) {
                    LOG.warn("banner id is null or zero!");
                    continue;
                }
                //[{"refuseReason":"","bannerId":33,"state":1,"stateValue":"待检查","advertiserId":123,"dspId":6},{"refuseReason":"","bannerId":34,"state":1,"stateValue":"待检查","advertiserId":123,"dspId":6}]
                // 状态（0通过，1待检查，2检查未通过）
                List<CloudCrossBannerStateResponseEntiry> responseEntirys = cloudCrossCreative.getStateById(bannerId);
                if (responseEntirys != null && !responseEntirys.isEmpty()) {
                    CloudCrossBannerStateResponseEntiry responseEntiry = responseEntirys.get(0);
                    if (responseEntiry != null && StringUtils.isNotEmpty(responseEntiry.getStateValue())) {
                        String updatetBannerStatusMessage = CloudCrossBannerQuery.updatetBannerStatusMessage;
                        try (PreparedStatement cpstmt = con.prepareStatement(updatetBannerStatusMessage)) {
                            switch (responseEntiry.getState()) {
                                case 0:
                                    cpstmt.setInt(1, AdxBasedExchangesStates.APPROVED.getCode());
                                    break;
                                case 1:
                                    cpstmt.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
                                    break;
                                case 2:
                                    cpstmt.setInt(1, AdxBasedExchangesStates.REFUSED.getCode());
                                    break;
                                default:
                                    cpstmt.setInt(1, rset.getInt("adxbasedexhangesstatus"));
                            }
                            cpstmt.setString(2, objectMapper.writeValueAsString(responseEntiry));
                            cpstmt.setTimestamp(3, ts);
                            cpstmt.setInt(4, getPubIncId());
                            cpstmt.setInt(5, rset.getInt("bannerId"));
                            cpstmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
