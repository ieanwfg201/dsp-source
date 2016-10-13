package com.kritter.naterial_upload.valuemaker.abstracts;


import com.kritter.utils.http_client.SynchronousHttpClient;
import com.kritter.utils.http_client.entity.HttpRequest;
import com.kritter.utils.http_client.entity.HttpResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class VamInterface<T,E,W> {
    public static ObjectMapper MAPPER = new ObjectMapper();

    public static String CREATIVE_DSPID_TOKEN = "?dspId=6&token=qaw6hu8x1d7m5k";

    static {
        MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    public static String buildBody(List<String> ids, String key) {
        StringBuilder sb = new StringBuilder("[{\"" + key + "\":\"");
        for (String id : ids) {
            sb.append(id).append(",");
        }
        return sb.delete(sb.length() - 1, sb.length()).append("\"}]").toString();
    }

    public static int getVamResponse(String url, String body, Map<String,String> header) throws IOException {
        HttpResponse response = doPostHeader(url, body, header);
        return response.getResponseStatusCode();
    }
    public static String getVamResponse(String url,Map<String,String> header) throws IOException {
        HttpResponse response = doGet(url,header);
        return response.getResponsePayload();
    }

    public static HttpResponse doPostHeader(String url, String body,Map<String,String> header) {
        SynchronousHttpClient synchronousHttpClient = new SynchronousHttpClient(null, 1);
        HttpRequest request = new HttpRequest(url, 1000, 1000, HttpRequest.REQUEST_METHOD.POST_METHOD, header, body);
        return synchronousHttpClient.fetchResponseFromThirdPartyServer(request);
    }
    public static HttpResponse doGet(String url,Map<String,String> header) {
        SynchronousHttpClient synchronousHttpClient = new SynchronousHttpClient(null, 1);
        HttpRequest request = new HttpRequest(url, 1000, 1000, HttpRequest.REQUEST_METHOD.GET_METHOD, header, null);
        return synchronousHttpClient.fetchResponseFromThirdPartyServer(request);
    }


    public abstract Integer addBanner(T entity,Map<String,String> heade);
    public abstract Integer addVideo(E entity,Map<String,String> heade);

//    public abstract List<String> update(List<T> entity);
//
//    public abstract List<E> queryByIds(List<String> ids, boolean isByBannerId);
//
    public abstract W getBannerStateByIds(String id,Map<String,String> header);
    public abstract W getVideoStateByIds(String id,Map<String,String> header);

}
