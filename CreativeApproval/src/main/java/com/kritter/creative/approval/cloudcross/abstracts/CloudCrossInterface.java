package com.kritter.creative.approval.cloudcross.abstracts;

import com.kritter.creative.approval.cloudcross.entity.CloudCrossState;
import com.kritter.creative.approval.cloudcross.entity.CloudCrossResponse;
import com.kritter.utils.http_client.SynchronousHttpClient;
import com.kritter.utils.http_client.entity.HttpRequest;
import com.kritter.utils.http_client.entity.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by hamlin on 16-7-31.
 */
@SuppressWarnings("unused")
public abstract class CloudCrossInterface<T> {
    public static ObjectMapper MAPPER = new ObjectMapper();

    public static String CREATIVE_DSPID_TOKEN = "?dspId=1&token=fed3fx6e5a2c8r";


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

    public abstract CloudCrossResponse add(T t);

    public abstract CloudCrossResponse update(T t);

    public abstract CloudCrossResponse getAllByIds(List<String> ids);

    public abstract List<CloudCrossState> getAllState(List<String> ids);
}
