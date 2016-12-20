package com.kritter.naterial_upload.valuemaker.video;

import com.alibaba.fastjson.JSON;
import com.kritter.constants.AdxBasedExchangesStates;
import com.kritter.material_upload.common.video.MUVideoAudit;
import com.kritter.naterial_upload.valuemaker.entity.HttpUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class VamMUVideoAudit implements MUVideoAudit {
    private static final Logger LOG = LoggerFactory.getLogger(VamMUVideoAudit.class);
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
    private Map<String, String> header;
    @Getter
    @Setter
    private String vam_video_audit_url;

    @Override
    public void init(Properties properties) {
        setDspid(properties.getProperty("vam_dsp_id").toString());
        setUsername(properties.getProperty("vam_username").toString());
        setPassword(properties.getProperty("vam_password").toString());
        setPubIncId(Integer.parseInt(properties.getProperty("vam_pubIncId").toString()));
        vam_video_audit_url = properties.getProperty("vam_url_prefix").toString() + properties.getProperty("vam_prefix_video_audit").toString();
    }

    @Override
    public void fetchMaterialAudit(Properties properties, Connection con) {
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;

        try {
            ps = con.prepareStatement(VamVideoQuery.selectforAudit);
            ps.setInt(1, getPubIncId());
            ResultSet st = ps.executeQuery();

            while (st.next()) {
                try {

                    String info = st.getString("info");
                    if (info == null) {
                        continue;
                    }

                    String video_guid = JSON.parseObject(info).getString("id");
                    if (video_guid == null) {
                        continue;
                    }


                    String url = vam_video_audit_url + "?id=" + video_guid;
                    Map<String, String> result = HttpUtils.get(url, username, password);
                    LOG.debug(video_guid);
                    LOG.debug(JSON.toJSONString(result));

                    if (result.get("StatusCode") != null && result.get("StatusCode").equals("200")) {

                        ps1 = con.prepareStatement(VamVideoQuery.updatetVideoStatusMessage);

                        ps1.setString(2, result.get("ResponseStr") == null ? "" : result.get("ResponseStr"));
                        ps1.setTimestamp(3, new Timestamp(new Date().getTime()));
                        ps1.setInt(4, getPubIncId());
                        ps1.setInt(5, st.getInt("videoInfoId"));
                        ps1.setInt(6, st.getInt("adxbasedexhangesstatus"));

                        String responseStr = result.get("ResponseStr");
                        if (responseStr != null) {
                            int status = JSON.parseObject(responseStr).getInteger("status");
                            switch (status) {
                                case 1:
                                    ps1.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
                                    break;
                                case 2:
                                    ps1.setInt(1, AdxBasedExchangesStates.APPROVED.getCode());
                                    break;
                                case 3:
                                    ps1.setInt(1, AdxBasedExchangesStates.REFUSED.getCode());
                                    break;
                                default:
                                    ps1.setInt(1, AdxBasedExchangesStates.APPROVING.getCode());
                                    break;
                            }
                        }
                        ps1.executeUpdate();
                    }
                } catch (Exception e) {
                    LOG.error(e.toString());
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
                if (ps1 != null) {
                    try {
                        ps1.close();
                    } catch (SQLException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

}
