package com.kritter.creative.approval.cloudcross;

import com.kritter.creative.approval.cloudcross.abstracts.CloudCrossInterface;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossBannerEntity;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossState;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossResponse;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.List;

/**
 * Created by hamlin on 16-7-29.
 */
public class CloudCrossCreative extends CloudCrossInterface<CloudCrossBannerEntity> {
    public static String CREATIVE_ADD_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/add" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_UPDATE_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/update" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_GET_ALL_BY_ADVERTISERIDS = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/getAll" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_GET_ALL_BY_BANNERIDS = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/get" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_GET_ALL_BANNERIDS_STATE = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/queryState" + CREATIVE_DSPID_TOKEN;

    @Override
    public CloudCrossResponse add(CloudCrossBannerEntity banner) {
        try {
            return MAPPER.readValue(getCloudCrossResponse(CREATIVE_ADD_URL, MAPPER.writeValueAsString(banner)), CloudCrossResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CloudCrossResponse update(CloudCrossBannerEntity banner) {
        try {
            return MAPPER.readValue(getCloudCrossResponse(CREATIVE_UPDATE_URL, MAPPER.writeValueAsString(banner)), CloudCrossResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //    public static CloudCrossResponse creativeGetAllBannerByAdvertiserIds(List<String> ids) {
//        String idsStr = buildBody(ids, "advertiserIds");
//        try {
//            return getCloudCrossResponse(CREATIVE_GET_ALL_BY_ADVERTISERIDS, idsStr);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    // TODO 这里的返回值应该是 list<banner>,但是现在不清楚返回的json结构
    @Override
    public CloudCrossResponse getAllByIds(List<String> ids) {
        String idsStr = buildBody(ids, "bannerIds");
        try {
            return MAPPER.readValue(getCloudCrossResponse(CREATIVE_GET_ALL_BY_BANNERIDS, idsStr), CloudCrossResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossState> getAllState(List<String> ids) {
        String idsStr = buildBody(ids, "bannerIds");
        try {
            String cloudCrossResponse = getCloudCrossResponse(CREATIVE_GET_ALL_BY_BANNERIDS, idsStr);
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, CloudCrossState.class);
            return (List<CloudCrossState>) MAPPER.readValue(cloudCrossResponse, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
