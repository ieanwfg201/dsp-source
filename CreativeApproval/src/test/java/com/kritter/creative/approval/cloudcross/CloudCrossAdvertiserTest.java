package com.kritter.creative.approval.cloudcross;

import com.kritter.creative.approval.cloudcross.entity.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by hamlin on 16-9-2.
 */
public class CloudCrossAdvertiserTest {
    private ObjectMapper mapper = new ObjectMapper();
    private CloudCrossAdvertiser cloudCrossAdvertiser = new CloudCrossAdvertiser();

    @Test
    public void testAdd() throws Exception {
        ArrayList<CloudCrossAdvertiserEntity> list = new ArrayList<>();
        CloudCrossAdvertiserEntity advertiserEntity = new CloudCrossAdvertiserEntity();
        advertiserEntity.setDspId(6);
        advertiserEntity.setAdvertiserId(123);
        advertiserEntity.setIndustryId(10);
        advertiserEntity.setName(123);
        advertiserEntity.setRegName("madhouse-test-advertise-add");
        list.add(advertiserEntity);
        List<CloudCrossResponse> response = cloudCrossAdvertiser.add(list);
        System.out.println(mapper.writeValueAsString(response));
    }

    @Test
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

    @Test
    public void testGetAllByIds() throws Exception {
        String id = "123";
        ArrayList<String> list = new ArrayList<>();
        list.add(id);
        List<CloudCrossAdvertiseResponseEntity> response = cloudCrossAdvertiser.queryByIds(list, false);
        System.out.println(mapper.writeValueAsString(response));
        response = cloudCrossAdvertiser.queryByIds(null, false);
        System.out.println(mapper.writeValueAsString(response));
    }

    @Test
    public void testGetAllState() throws Exception {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("123");
        List<CloudCrossAdvertieseStateResponseEntiry> states = cloudCrossAdvertiser.getStateByIds(ids);
        System.out.println(mapper.writeValueAsString(states));
    }
}