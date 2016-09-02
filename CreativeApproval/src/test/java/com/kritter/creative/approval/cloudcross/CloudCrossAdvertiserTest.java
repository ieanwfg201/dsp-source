package com.kritter.creative.approval.cloudcross;

import com.kritter.creative.approval.cloudcross.entity.CloudCrossAdvertiserEntity;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossResponse;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossState;
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

    public void testAdd() throws Exception {
        CloudCrossAdvertiserEntity advertiserEntity = new CloudCrossAdvertiserEntity();
        CloudCrossResponse response = cloudCrossAdvertiser.add(advertiserEntity);
        System.out.println(mapper.writeValueAsString(response));
    }

    public void testUpdate() throws Exception {
        CloudCrossResponse response = cloudCrossAdvertiser.update(new CloudCrossAdvertiserEntity());
        System.out.println(mapper.writeValueAsString(response));
    }

    public void testGetAllByIds() throws Exception {
        CloudCrossResponse response = cloudCrossAdvertiser.getAllByIds(new ArrayList<String>(0));
        System.out.println(mapper.writeValueAsString(response));
    }

    public void testGetAllState() throws Exception {
        List<CloudCrossState> states = cloudCrossAdvertiser.getAllState(new ArrayList<String>(0));
        System.out.println(mapper.writeValueAsString(states));
    }
}