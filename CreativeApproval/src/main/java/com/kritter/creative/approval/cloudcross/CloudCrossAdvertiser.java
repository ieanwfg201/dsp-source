package com.kritter.creative.approval.cloudcross;

import com.kritter.creative.approval.cloudcross.abstracts.CloudCrossInterface;
import com.kritter.creative.approval.cloudcross.entity.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamlin on 16-7-29.
 */
public class CloudCrossAdvertiser extends CloudCrossInterface<CloudCrossAdvertiserEntity,CloudCrossAdvertiseResponseEntity,CloudCrossAdvertieseStateResponseEntiry> {

    public static String ADVERTISER_ADD_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/add" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_UPDATE_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/update" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_GET_ALL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/getAll" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_BY_IDS = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/get" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_GET_STATE_BY_IDS = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/queryState" + CREATIVE_DSPID_TOKEN;

    static {
        MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    @Override
    public List<CloudCrossResponse> add(ArrayList<CloudCrossAdvertiserEntity> cloudCrossAdvertisers) {
        String response = null;
        try {
            response = getCloudCrossResponse(ADVERTISER_ADD_URL, "request=" + MAPPER.writeValueAsString(cloudCrossAdvertisers));

            return (List<CloudCrossResponse>) MAPPER.readValue(response, new TypeReference<List<CloudCrossResponse>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossResponse> update(ArrayList<CloudCrossAdvertiserEntity> cloudCrossAdvertiser) {
        String response = null;
        try {
            response = getCloudCrossResponse(ADVERTISER_UPDATE_URL, "request=" + MAPPER.writeValueAsString(cloudCrossAdvertiser));
//            return MAPPER.readValue(response, CloudCrossResponse.class);
            return (List<CloudCrossResponse>) MAPPER.readValue(response, new TypeReference<List<CloudCrossResponse>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    public static CloudCrossResponse creativeGetAllBannerByAdvertiserIds(List<String> ids) {
//        String idsStr = buildBody(ids, "advertiserIds");
//        try {
//            return getCloudCrossResponse(ADVERTISER_GET_ALL, idsStr);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    // TODO 这里的返回值应该是 list<banner>,但是现在不清楚返回的json结构

    /**
     *
     * @param ids  bannerids or advertiserIds
     * @param isByBannerId if true ,ids is bannerids, else ids is advertiserIds
     * @return
     */
    @Override
    public List<CloudCrossAdvertiseResponseEntity> queryByIds(List<String> ids,boolean isByBannerId) {
        String idsStr = null;
        String url;
        if (ids != null) {
            idsStr = buildBody(ids, "advertiserIds");
            url = ADVERTISER_BY_IDS;
        }else {
            url = ADVERTISER_GET_ALL;
        }
        try {
            String response = getCloudCrossResponse(url, idsStr);
            return (List<CloudCrossAdvertiseResponseEntity>) MAPPER.readValue(response, new TypeReference<List<CloudCrossAdvertiseResponseEntity>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossAdvertieseStateResponseEntiry> getStateByIds(List<String> ids) {
        String idsStr = null;
        if (ids != null)
            idsStr = buildBody(ids, "advertiserIds");
        try {
            String cloudCrossResponse = getCloudCrossResponse(ADVERTISER_GET_STATE_BY_IDS, "request="+idsStr);
            return (List<CloudCrossAdvertieseStateResponseEntiry>) MAPPER.readValue(cloudCrossResponse, new TypeReference<List<CloudCrossAdvertieseStateResponseEntiry>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
