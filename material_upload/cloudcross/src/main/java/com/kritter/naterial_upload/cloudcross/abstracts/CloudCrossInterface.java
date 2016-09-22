package com.kritter.naterial_upload.cloudcross.abstracts;

import com.kritter.naterial_upload.cloudcross.entity.CloudCrossResponse;
import com.kritter.utils.http_client.SynchronousHttpClient;
import com.kritter.utils.http_client.entity.HttpRequest;
import com.kritter.utils.http_client.entity.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamlin on 16-7-31.
 */
@SuppressWarnings("unused")
public abstract class CloudCrossInterface<T, E, W> {
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

    public static String getCloudCrossResponse(String url, String body) throws IOException {
        HttpResponse response = doPostNoHeader(url, body);
        return response.getResponsePayload();
    }

    public static HttpResponse doPostNoHeader(String url, String body) {
        SynchronousHttpClient synchronousHttpClient = new SynchronousHttpClient(null, 1);
        HttpRequest request = new HttpRequest(url, 1000, 1000, HttpRequest.REQUEST_METHOD.POST_METHOD, null, body);
        return synchronousHttpClient.fetchResponseFromThirdPartyServer(request);
    }

    public abstract List<CloudCrossResponse> add(List<T> entity);

    public abstract List<CloudCrossResponse> update(List<T> entity);

    public abstract List<E> queryByIds(List<String> ids, boolean isByBannerId);

    public abstract List<W> getStateByIds(List<String> ids);
}
