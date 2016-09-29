package com.kritter.naterial_upload.cloudcross.advertiser;

import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.advInfo.MUADvInfoAudit;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertieseStateResponseEntiry;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertiserEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by hamlin on 16-9-29.
 */
public class CloudCrossMUAdvInfoAudit implements MUADvInfoAudit {
    private static final Logger LOG = LoggerFactory.getLogger(CloudCrossMUAdvInfoAudit.class);
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
    private CloudCrossAdvertiser cloudCrossAdvertiser = null;

    @Override
    public void init(Properties properties) {
        setDspid(properties.getProperty("cloudCross_dsp_id"));
        setToken(properties.getProperty("cloudCross_token"));
        setPubIncId(Integer.parseInt(properties.getProperty("cloudCross_pubIncId")));

        String cloudcross_url_prefix = properties.getProperty("cloudcross_url_prefix");
        String advertiser_add_url = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_add");
        String cloudcross_prefix_advertiser_update = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_update");
        String cloudcross_prefix_advertiser_status = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_status");
        cloudCrossAdvertiser = new CloudCrossAdvertiser(advertiser_add_url, cloudcross_prefix_advertiser_update, null, null, cloudcross_prefix_advertiser_status);
    }

    @Override
    public void fetchMaterialAudit(Properties properties, Connection con) {
        PreparedStatement pstmt = null;
        PreparedStatement cpstmt = null;
        try {
            pstmt = con.prepareStatement(CloudCrossAdvInfoQuery.selectforAudit);
            pstmt.setInt(1, getPubIncId());
            ResultSet rset = pstmt.executeQuery();
            Timestamp ts = new Timestamp(new Date().getTime());
            while (rset.next()) {
                CloudCrossAdvertiserEntity advertiserEntity = objectMapper.readValue(rset.getString("info"), CloudCrossAdvertiserEntity.class);
                if (advertiserEntity != null) {
                    try {
                        List<String> ids = new ArrayList<>();
                        ids.add(Integer.toString(advertiserEntity.getAdvertiserId()));
                        List<CloudCrossAdvertieseStateResponseEntiry> stateByIds = cloudCrossAdvertiser.getStateByIds(ids);
                        LOG.info("MATERIAL AUDIT RETURN");
                        String response = objectMapper.writeValueAsString(stateByIds);
                        LOG.info(response);
                        if (stateByIds != null && stateByIds.size() > 0) {
                            // [{"refuseReason":"","state":1,"stateValue":"待检查","advertiserId":23,"dspId":6}]
                            CloudCrossAdvertieseStateResponseEntiry advertieseStateResponseEntiry = stateByIds.get(0);
                            if (advertieseStateResponseEntiry != null && StringUtils.isNotEmpty(advertieseStateResponseEntiry.getStateValue())) {
                                cpstmt = con.prepareStatement(CloudCrossAdvInfoQuery.updatetAdvInfoStatusMessage);
                                // 状态（0通过，1待检查，2检查未通过）
                                switch (advertieseStateResponseEntiry.getState()) {
                                    case 0:
                                        cpstmt.setInt(1, AdxBasedExchangesStates.APPROVED.getCode());
                                        break;
                                    case 2:
                                        cpstmt.setInt(1, AdxBasedExchangesStates.REFUSED.getCode());
                                        break;
                                    case 1:
                                        cpstmt.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
                                        break;
                                    default:
                                        cpstmt.setInt(1, rset.getInt("adxbasedexhangesstatus"));
                                        break;
                                }
                                cpstmt.setString(2, advertieseStateResponseEntiry.getStateValue());
                                cpstmt.setTimestamp(3, ts);
                                cpstmt.executeUpdate();
                            } else {
                                cpstmt = con.prepareStatement(CloudCrossAdvInfoQuery.updatetAdvInfoStatusMessage);
                                cpstmt.setInt(1, AdxBasedExchangesStates.AUGITORGETFAIL.getCode());
                                cpstmt.setString(2, response + "--AUDIT MESSAGE NOT PRSESENT");
                                cpstmt.setTimestamp(3, ts);
                                cpstmt.executeUpdate();
                            }

                        }
                    } catch (Exception e1) {
                        LOG.error(e1.getMessage(), e1);
                    }
                }
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
