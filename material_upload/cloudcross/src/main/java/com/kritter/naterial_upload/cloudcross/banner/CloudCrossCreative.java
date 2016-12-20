package com.kritter.naterial_upload.cloudcross.banner;

import com.kritter.naterial_upload.cloudcross.abstracts.CloudCrossInterface;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossBannerEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossBannerResponseEntity;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossBannerStateResponseEntiry;
import com.kritter.naterial_upload.cloudcross.entity.CloudCrossResponse;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hamlin on 16-7-29.
 */
public class CloudCrossCreative extends CloudCrossInterface<CloudCrossBannerEntity, CloudCrossBannerResponseEntity, CloudCrossBannerStateResponseEntiry> {
    public String CREATIVE_ADD_URL;
    public String CREATIVE_UPDATE_URL;
    public String CREATIVE_GET_ALL;
    public String CREATIVE_GET_ALL_BY_BANNERIDS;
    public String CREATIVE_GET_ALL_BANNERIDS_STATE;

    public CloudCrossCreative(String CREATIVE_ADD_URL, String CREATIVE_UPDATE_URL, String CREATIVE_GET_ALL, String CREATIVE_GET_ALL_BY_BANNERIDS, String CREATIVE_GET_ALL_BANNERIDS_STATE) {
        this.CREATIVE_ADD_URL = CREATIVE_ADD_URL;
        this.CREATIVE_UPDATE_URL = CREATIVE_UPDATE_URL;
        this.CREATIVE_GET_ALL = CREATIVE_GET_ALL;
        this.CREATIVE_GET_ALL_BY_BANNERIDS = CREATIVE_GET_ALL_BY_BANNERIDS;
        this.CREATIVE_GET_ALL_BANNERIDS_STATE = CREATIVE_GET_ALL_BANNERIDS_STATE;
    }

    @Override
    public List<CloudCrossResponse> add(List<CloudCrossBannerEntity> banner) {
        try {
            String cloudCrossResponse = getCloudCrossResponse(CREATIVE_ADD_URL, "request=" + MAPPER.writeValueAsString(banner));
            return MAPPER.readValue(cloudCrossResponse, new TypeReference<List<CloudCrossResponse>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossResponse> add(CloudCrossBannerEntity entity) {
        List<CloudCrossBannerEntity> materialList = new LinkedList<>();
        materialList.add(entity);
        return add(materialList);
    }

    @Override
    public List<CloudCrossResponse> update(List<CloudCrossBannerEntity> banner) {
        try {
            String response = getCloudCrossResponse(CREATIVE_UPDATE_URL, "request=" + MAPPER.writeValueAsString(banner));
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
//            return getCloudCrossResponse(CREATIVE_GET_ALL_BY_ADVERTISERIDS, idsStr);
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
            return (List<CloudCrossBannerStateResponseEntiry>) MAPPER.readValue(cloudCrossResponse, new TypeReference<List<CloudCrossBannerStateResponseEntiry>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<CloudCrossBannerStateResponseEntiry> getStateById(Integer id) {
        List<String> ids = new ArrayList<>();
        ids.add(Integer.toString(id));
        return getStateByIds(ids);
    }

}
