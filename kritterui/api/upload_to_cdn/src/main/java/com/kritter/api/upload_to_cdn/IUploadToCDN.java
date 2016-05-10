package com.kritter.api.upload_to_cdn;

import java.io.File;
import java.util.Map;

public interface IUploadToCDN {
    boolean upload(String url, Map<String, String> getParams, Map<String, String> postParams, File file,
            String id);
}
