package com.kritter.creative.approval.cloudcross;

import com.kritter.creative.approval.cloudcross.entity.CloudCrossBannerEntity;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hamlin on 16-9-8.
 */
public class CloudCrossCreativeTest {

    @Test
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
        System.out.println(new ObjectMapper().writeValueAsString(new CloudCrossCreative().add(list)));
    }

    @Test
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
        System.out.println(new ObjectMapper().writeValueAsString(new CloudCrossCreative().update(list)));
    }

    @Test
    public void testGetAllByIds() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("68");
        list.add("69");
        System.out.println(new ObjectMapper().writeValueAsString(new CloudCrossCreative().queryByIds(list, true)));
        list.clear();
        list.add("123");
        System.out.println(new ObjectMapper().writeValueAsString(new CloudCrossCreative().queryByIds(list, false)));
    }

    @Test
    public void testGetStateByIds() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("68");
        list.add("69");
        System.out.println(new ObjectMapper().writeValueAsString(new CloudCrossCreative().getStateByIds(list)));
    }
}