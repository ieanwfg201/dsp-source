package com.kritter.naterial_upload.cloudcross.advertiser;

import com.kritter.naterial_upload.cloudcross.abstracts.CloudCrossInterface;
import com.kritter.naterial_upload.cloudcross.banner.CloudCrossMUBanner;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertieseStateResponseEntiry;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertiseResponseEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossAdvertiserEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossResponse;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamlin on 16-7-29.
 */
public class CloudCrossAdvertiser extends CloudCrossInterface<CloudCrossAdvertiserEntity, CloudCrossAdvertiseResponseEntity, CloudCrossAdvertieseStateResponseEntiry> {
    private static final Logger LOG = LoggerFactory.getLogger(CloudCrossMUBanner.class);
    public String ADVERTISER_ADD_URL;
    public String ADVERTISER_UPDATE_URL;
    public String ADVERTISER_GET_ALL;
    public String ADVERTISER_BY_IDS;
    public String ADVERTISER_GET_STATE_BY_IDS;


    public CloudCrossAdvertiser(String ADVERTISER_ADD_URL, String ADVERTISER_UPDATE_URL, String ADVERTISER_GET_ALL, String ADVERTISER_BY_IDS, String ADVERTISER_GET_STATE_BY_IDS) {
        this.ADVERTISER_ADD_URL = ADVERTISER_ADD_URL;
        this.ADVERTISER_UPDATE_URL = ADVERTISER_UPDATE_URL;
        this.ADVERTISER_GET_ALL = ADVERTISER_GET_ALL;
        this.ADVERTISER_BY_IDS = ADVERTISER_BY_IDS;
        this.ADVERTISER_GET_STATE_BY_IDS = ADVERTISER_GET_STATE_BY_IDS;
    }

    @Override
    public List<CloudCrossResponse> add(List<CloudCrossAdvertiserEntity> cloudCrossAdvertisers) {
        String response = null;
        try {
            LOG.info("MATERIAL BANNER UPLOAD POSTBODY");
            String body = "request=" + MAPPER.writeValueAsString(cloudCrossAdvertisers);
            LOG.info(body);
            response = getCloudCrossResponse(ADVERTISER_ADD_URL, body);

            return (List<CloudCrossResponse>) MAPPER.readValue(response, new TypeReference<List<CloudCrossResponse>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossResponse> update(List<CloudCrossAdvertiserEntity> cloudCrossAdvertiser) {
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
     * @param ids          bannerids or advertiserIds
     * @param isByBannerId if true ,ids is bannerids, else ids is advertiserIds
     * @return
     */
    @Override
    public List<CloudCrossAdvertiseResponseEntity> queryByIds(List<String> ids, boolean isByBannerId) {
        String idsStr = null;
        String url;
        if (ids != null) {
            idsStr = buildBody(ids, "advertiserIds");
            url = ADVERTISER_BY_IDS;
        } else {
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
            String cloudCrossResponse = getCloudCrossResponse(ADVERTISER_GET_STATE_BY_IDS, "request=" + idsStr);
            return (List<CloudCrossAdvertieseStateResponseEntiry>) MAPPER.readValue(cloudCrossResponse, new TypeReference<List<CloudCrossAdvertieseStateResponseEntiry>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
