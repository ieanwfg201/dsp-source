package com.kritter.naterial_upload.valuemaker.entity;


import com.kritter.naterial_upload.valuemaker.abstracts.VamInterface;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class VamCreative extends VamInterface<VamMaterialUploadEntity,VamVideoMaterialUploadEntity,String> {
    public String CREATIVE_ADD_URL;
    public String CREATIVE_GET_STATE_URL;
//    public String CREATIVE_UPDATE_URL;
//    public String CREATIVE_GET_ALL;
//    public String CREATIVE_GET_ALL_BY_BANNERIDS;
//    public String CREATIVE_GET_ALL_BANNERIDS_STATE;

    public VamCreative() {

    }

    public VamCreative(String CREATIVE_ADD_URL,String CREATIVE_GET_STATE_URL) {
        this.CREATIVE_ADD_URL = CREATIVE_ADD_URL;
//        this.CREATIVE_UPDATE_URL = CREATIVE_UPDATE_URL;
//        this.CREATIVE_GET_ALL = CREATIVE_GET_ALL;
//        this.CREATIVE_GET_ALL_BY_BANNERIDS = CREATIVE_GET_ALL_BY_BANNERIDS;
//        this.CREATIVE_GET_ALL_BANNERIDS_STATE = CREATIVE_GET_ALL_BANNERIDS_STATE;
        this.CREATIVE_GET_STATE_URL=CREATIVE_GET_STATE_URL;
    }
    public static String authStringEnc(String username,String password) {
        String nameandpassword = username + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(nameandpassword.getBytes());
        String authStringEnc = new String(authEncBytes);
        System.out.println(authStringEnc);
        return authStringEnc;
    }

    @Override
    public Integer addBanner(VamMaterialUploadEntity banner, Map<String,String> header) {
        try {
            int vamResponse = getVamResponse(CREATIVE_ADD_URL, MAPPER.writeValueAsString(banner),header);
            System.out.println(vamResponse);
            return vamResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Integer addVideo(VamVideoMaterialUploadEntity video, Map<String,String> header) {
        try {
            int vamResponse = getVamResponse(CREATIVE_ADD_URL, MAPPER.writeValueAsString(video),header);
            System.out.println(vamResponse);
            return vamResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getBannerStateByIds(String id,Map<String,String> header) {
        try {
            String url = CREATIVE_GET_STATE_URL +"?id="+id;
            String vamResponse = getVamResponse(url,header);
            System.out.println(vamResponse);
            return vamResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public String getVideoStateByIds(String id,Map<String,String> header) {
        try {
            String url = CREATIVE_GET_STATE_URL +"?id="+id;
            String vamResponse = getVamResponse(url,header);
            System.out.println(vamResponse);
            return vamResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
//    @Override
//    public Integer update(VamMaterialUploadEntity banner) {
//        try {
//            Integer response = getVamResponse(CREATIVE_UPDATE_URL, "request=" + MAPPER.writeValueAsString(banner));
//            System.out.println(response);
//            return response;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    //    public static CloudCrossResponse creativeGetAllBannerByAdvertiserIds(List<String> ids) {
////        String idsStr = buildBody(ids, "advertiserIds");
////        try {
////            return getCloudCrossResponse(CREATIVE_GET_ALL_BY_ADVERTISERIDS, idsStr);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        return null;
////    }
//    // TODO 这里的返回值应该是 list<banner>,但是现在不清楚返回的json结构
//
//    /**
//     * @param ids          bannerids or advertiserIds
//     * @param isByBannerId if true ,ids is bannerids, else ids is advertiserIds
//     * @return
//     */
//    @Override
//    public List<String> queryByIds(List<String> ids, boolean isByBannerId) {
//        String idsStr = null;
//        String url = null;
//        if (ids != null) {
//            if (isByBannerId) {
//                idsStr = buildBody(ids, "bannerIds");
//                url = CREATIVE_GET_ALL_BY_BANNERIDS;
//            } else {
//                idsStr = buildBody(ids, "advertiserIds");
//                url = CREATIVE_GET_ALL;
//            }
//        }
//
//        try
//
//        {
//            String response = getVamResponse(url, "request=" + idsStr);
//            System.out.println(response);
//            return (List<String>) MAPPER.readValue(response, new TypeReference<List<String>>() {
//            });
//        } catch (
//                IOException e
//                )
//
//        {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//


}
