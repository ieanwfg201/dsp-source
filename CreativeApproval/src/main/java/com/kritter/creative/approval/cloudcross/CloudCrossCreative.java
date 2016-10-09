package com.kritter.creative.approval.cloudcross;

import com.kritter.creative.approval.cloudcross.abstracts.CloudCrossInterface;
import com.kritter.creative.approval.cloudcross.entity.*;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamlin on 16-7-29.
 */
public class CloudCrossCreative extends CloudCrossInterface<CloudCrossBannerEntity, CloudCrossBannerResponseEntity, CloudCrossBannerStateResponseEntiry> {
    public static String CREATIVE_ADD_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/add" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_UPDATE_URL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/update" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_GET_ALL = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/getAll" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_GET_ALL_BY_BANNERIDS = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/get" + CREATIVE_DSPID_TOKEN;
    public static String CREATIVE_GET_ALL_BANNERIDS_STATE = "http://test.datacross.cn:8080/ssp_web/dsp/main/dsp-banner/queryState" + CREATIVE_DSPID_TOKEN;

    @Override
    public List<CloudCrossResponse> add(ArrayList<CloudCrossBannerEntity> banner) {
        try {
            String body = "request=" + MAPPER.writeValueAsString(banner);
            String cloudCrossResponse = getCloudCrossResponse(CREATIVE_ADD_URL, body);
            System.out.println("====================");
            System.out.println("url: " + CREATIVE_ADD_URL);
            System.out.println("body: " + body);
            System.out.println("response: " + cloudCrossResponse);
            System.out.println("====================");
            return MAPPER.readValue(cloudCrossResponse, new TypeReference<List<CloudCrossResponse>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossResponse> update(ArrayList<CloudCrossBannerEntity> banner) {
        try {
            String response = getCloudCrossResponse(CREATIVE_UPDATE_URL, "request=" + MAPPER.writeValueAsString(banner));
            System.out.println(response);
            return (List<CloudCrossResponse>) MAPPER.readValue(response, new TypeReference<List<CloudCrossResponse>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param ids          bannerids or advertiserIds
     * @param isByBannerId if true ,ids is bannerids, else ids is advertiserIds
     * @return
     */
    @Override
    public List<CloudCrossBannerResponseEntity> queryByIds(List<String> ids, boolean isByBannerId) {
        String idsStr = null;
        String url = null;
        if (ids != null) {
            if (isByBannerId) {
                idsStr = buildBody(ids, "bannerIds");
                url = CREATIVE_GET_ALL_BY_BANNERIDS;
            } else {
                idsStr = buildBody(ids, "advertiserIds");
                url = CREATIVE_GET_ALL;
            }
        }

        try

        {
            String response = getCloudCrossResponse(url, "request=" + idsStr);
            System.out.println(response);
            return (List<CloudCrossBannerResponseEntity>) MAPPER.readValue(response, new TypeReference<List<CloudCrossBannerResponseEntity>>() {
            });
        } catch (
                IOException e
                )

        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<CloudCrossBannerStateResponseEntiry> getStateByIds(List<String> ids) {
        String idsStr = buildBody(ids, "bannerIds");
        try {
            String cloudCrossResponse = getCloudCrossResponse(CREATIVE_GET_ALL_BANNERIDS_STATE, "request=" + idsStr);
            System.out.println(cloudCrossResponse);
            return (List<CloudCrossBannerStateResponseEntiry>) MAPPER.readValue(cloudCrossResponse, new TypeReference<List<CloudCrossBannerStateResponseEntiry>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
