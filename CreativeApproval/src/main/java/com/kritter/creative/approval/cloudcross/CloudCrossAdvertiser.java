package com.kritter.creative.approval.cloudcross;

import com.kritter.creative.approval.cloudcross.abstracts.CloudCrossInterface;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossState;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossResponse;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.List;

/**
 * Created by hamlin on 16-7-29.
 */
public class CloudCrossAdvertiser extends CloudCrossInterface<CloudCrossAdvertiser> {

    public static String ADVERTISER_ADD_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/add" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_UPDATE_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/update" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_GET_ALL_BY_ADVERTISERIDS = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/getAll" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_GET_ALL_BY_BANNERIDS = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/get" + CREATIVE_DSPID_TOKEN;
    public static String ADVERTISER_GET_ALL_BANNERIDS_STATE = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-advertiser/queryState" + CREATIVE_DSPID_TOKEN;

    @Override
    public CloudCrossResponse add(CloudCrossAdvertiser cloudCrossAdvertiser) {
        try {
            return MAPPER.readValue(getCloudCrossResponse(ADVERTISER_ADD_URL, MAPPER.writeValueAsString(cloudCrossAdvertiser)), CloudCrossResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CloudCrossResponse update(CloudCrossAdvertiser cloudCrossAdvertiser) {
        try {
            return MAPPER.readValue(getCloudCrossResponse(ADVERTISER_UPDATE_URL, MAPPER.writeValueAsString(cloudCrossAdvertiser)), CloudCrossResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    public static CloudCrossResponse creativeGetAllBannerByAdvertiserIds(List<String> ids) {
//        String idsStr = buildBody(ids, "advertiserIds");
//        try {
//            return getCloudCrossResponse(ADVERTISER_GET_ALL_BY_ADVERTISERIDS, idsStr);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    // TODO 这里的返回值应该是 list<banner>,但是现在不清楚返回的json结构
    @Override
    public CloudCrossResponse getAllByIds(List<String> ids) {
        String idsStr = buildBody(ids, "advertiserIds");
        try {
            return MAPPER.readValue(getCloudCrossResponse(ADVERTISER_GET_ALL_BY_BANNERIDS, idsStr), CloudCrossResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossState> getAllState(List<String> ids) {
        String idsStr = buildBody(ids, "advertiserIds");
        try {
            String cloudCrossResponse = getCloudCrossResponse(ADVERTISER_GET_ALL_BY_BANNERIDS, idsStr);
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, CloudCrossState.class);
            return (List<CloudCrossState>) MAPPER.readValue(cloudCrossResponse, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
