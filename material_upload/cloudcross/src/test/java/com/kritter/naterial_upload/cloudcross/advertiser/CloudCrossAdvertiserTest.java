package com.kritter.naterial_upload.cloudcross.advertiser;

import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertieseStateResponseEntiry;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertiseResponseEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertiserEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossResponse;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *
 * Created by hamlin on 16-9-22.
 */
@SuppressWarnings("unused")
public class CloudCrossAdvertiserTest {
    private ObjectMapper mapper = new ObjectMapper();
    private CloudCrossAdvertiser cloudCrossAdvertiser;
    private String dspid;
    private String token;
    private Integer pubIncId;
    private Date dateNow;

    public Date getDateNow() {
        return dateNow;
    }

    public void setDateNow(Date dateNow) {
        this.dateNow = dateNow;
    }

    public String getDspid() {
        return dspid;
    }

    public void setDspid(String dspid) {
        this.dspid = dspid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getPubIncId() {
        return pubIncId;
    }

    public void setPubIncId(Integer pubIncId) {
        this.pubIncId = pubIncId;
    }

    public void init() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(new File("/home/hamlin/workspace/optimad/material_upload/uploader/conf/kritter/material.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setDspid(properties.getProperty("cloudcross_dsp_id"));
        setToken(properties.getProperty("cloudcross_token"));
        setPubIncId(Integer.parseInt(properties.getProperty("cloudcross_pubIncId")));
        dateNow = new Date();

        String creative_dspid_token = "?dspId=" + getDspid() + "&token=" + getToken();
        String cloudcross_url_prefix = properties.getProperty("cloudcross_url_prefix");
        String cloudcross_prefix_advertiser_add = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_add") + creative_dspid_token;
        String cloudcross_prefix_advertiser_update = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_update") + creative_dspid_token;
        String cloudcross_prefix_advertiser_status = cloudcross_url_prefix + properties.getProperty("cloudcross_prefix_advertiser_status") + creative_dspid_token;
        this.cloudCrossAdvertiser = new CloudCrossAdvertiser(cloudcross_prefix_advertiser_add, cloudcross_prefix_advertiser_update, null, null, cloudcross_prefix_advertiser_status);


    }

    public void testAdd() throws Exception {
        ArrayList<CloudCrossAdvertiserEntity> list = new ArrayList<>();
        CloudCrossAdvertiserEntity advertiserEntity = new CloudCrossAdvertiserEntity();
        advertiserEntity.setDspId(6);
        advertiserEntity.setAdvertiserId(23);
        advertiserEntity.setIndustryId(10);
        advertiserEntity.setName(123);
        advertiserEntity.setRegName("madhouse-test-advertise-add2");
        list.add(advertiserEntity);
        List<CloudCrossResponse> response = cloudCrossAdvertiser.add(list);
        System.out.println(mapper.writeValueAsString(response));
    }

    public void testUpdate() throws Exception {
        ArrayList<CloudCrossAdvertiserEntity> list = new ArrayList<>();
        CloudCrossAdvertiserEntity advertiserEntity = new CloudCrossAdvertiserEntity();
        advertiserEntity.setDspId(6);
        advertiserEntity.setAdvertiserId(123);
        advertiserEntity.setIndustryId(10);
        advertiserEntity.setName(123);
        advertiserEntity.setRegName("madhouse-test-advertise-add");
        list.add(advertiserEntity);
        List<CloudCrossResponse> response = cloudCrossAdvertiser.update(list);
        System.out.println(mapper.writeValueAsString(response));
    }

    public void testGetAllByIds() throws Exception {
        String id = "123";
        ArrayList<String> list = new ArrayList<>();
        list.add(id);
        List<CloudCrossAdvertiseResponseEntity> response = cloudCrossAdvertiser.queryByIds(list, false);
        System.out.println(mapper.writeValueAsString(response));
        response = cloudCrossAdvertiser.queryByIds(null, false);
        System.out.println(mapper.writeValueAsString(response));
    }

    public void testGetAllState() throws Exception {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("223");
        List<CloudCrossAdvertieseStateResponseEntiry> states = cloudCrossAdvertiser.getStateByIds(ids);
        System.out.println(mapper.writeValueAsString(states));
    }

}