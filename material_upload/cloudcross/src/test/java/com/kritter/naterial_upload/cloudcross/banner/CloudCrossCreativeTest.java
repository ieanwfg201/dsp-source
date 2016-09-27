package com.kritter.naterial_upload.cloudcross.banner;

import com.kritter.naterial_upload.cloudcross.entity.CloudCrossBannerEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Created by hamlin on 16-9-22.
 */
public class CloudCrossCreativeTest {
    private String dspid;
    private String token;
    private Integer pubIncId;
    private boolean performTransaction = true;
    private Date dateNow;
    private Date startDate;
    private String startDateStr;
    private boolean lastRunPresent = false;
    private CloudCrossCreative cloudCrossCreative;

//    @Before
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

//    @Test
    public void testAdd() throws Exception {
        ArrayList<CloudCrossBannerEntity> list = new ArrayList<>();
        CloudCrossBannerEntity cloudCrossBannerEntity = new CloudCrossBannerEntity();
        cloudCrossBannerEntity.setAdvertiserId(123);
        cloudCrossBannerEntity.setHeight(50);
        cloudCrossBannerEntity.setWidth(320);
        cloudCrossBannerEntity.setPath("http://qa.admin.optimad.cn/img/01525400-0b35-d401-5703-e5df20000007.jpg");
        cloudCrossBannerEntity.setRheight(5);
        cloudCrossBannerEntity.setRwidth(32);
        list.add(cloudCrossBannerEntity);
        CloudCrossBannerEntity cloudCrossBannerEntity2 = new CloudCrossBannerEntity();
        cloudCrossBannerEntity2.setAdvertiserId(123);
        cloudCrossBannerEntity2.setHeight(50);
        cloudCrossBannerEntity2.setWidth(320);
        cloudCrossBannerEntity2.setPath("http://qa.admin.optimad.cn/img/01525400-0b35-d401-5703-e5df20000007.jpg");
        cloudCrossBannerEntity2.setRheight(5);
        cloudCrossBannerEntity2.setRwidth(32);
        list.add(cloudCrossBannerEntity2);
        System.out.println(new ObjectMapper().writeValueAsString(cloudCrossCreative.add(list)));
    }

//    @Test
    public void testUpdate() throws Exception {
        ArrayList<CloudCrossBannerEntity> list = new ArrayList<>();
        CloudCrossBannerEntity cloudCrossBannerEntity = new CloudCrossBannerEntity();
        cloudCrossBannerEntity.setBannerId(68);
        cloudCrossBannerEntity.setAdvertiserId(123);
        cloudCrossBannerEntity.setHeight(60);
        cloudCrossBannerEntity.setWidth(320);
        cloudCrossBannerEntity.setPath("http://qa.admin.optimad.cn/img/01525400-0b35-d401-5703-e5df20000007.jpg");
        cloudCrossBannerEntity.setRheight(6);
        cloudCrossBannerEntity.setRwidth(32);
        list.add(cloudCrossBannerEntity);
        System.out.println(new ObjectMapper().writeValueAsString(cloudCrossCreative.update(list)));
    }

//    @Test
    public void testGetAllByIds() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("68");
        list.add("69");
        System.out.println(new ObjectMapper().writeValueAsString(cloudCrossCreative.queryByIds(list, true)));
        list.clear();
        list.add("123");
        System.out.println(new ObjectMapper().writeValueAsString(cloudCrossCreative.queryByIds(list, false)));
    }

//    @Test
    public void testGetStateByIds() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("33");
        list.add("34");
        System.out.println(new ObjectMapper().writeValueAsString(cloudCrossCreative.getStateByIds(list)));
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

    public boolean isPerformTransaction() {
        return performTransaction;
    }

    public void setPerformTransaction(boolean performTransaction) {
        this.performTransaction = performTransaction;
    }

    public Date getDateNow() {
        return dateNow;
    }

    public void setDateNow(Date dateNow) {
        this.dateNow = dateNow;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public boolean isLastRunPresent() {
        return lastRunPresent;
    }

    public void setLastRunPresent(boolean lastRunPresent) {
        this.lastRunPresent = lastRunPresent;
    }

    public CloudCrossCreative getCloudCrossCreative() {
        return cloudCrossCreative;
    }

    public void setCloudCrossCreative(CloudCrossCreative cloudCrossCreative) {
        this.cloudCrossCreative = cloudCrossCreative;
    }
}