package com.kritter.material_upload.youkuvideouploader;

import com.youku.uploader.YoukuUploader;

import java.util.HashMap;

public class YoukuVideoUploader {
    private static YoukuUploader uploader;

    public static void main(String[] args) {

        YoukuConfig config = new YoukuConfig();
        String client_id = config.getClientId();
        String client_secret = config.getClientSecret();
        String result = "";
        HashMap<String, String> params, uploadInfo;
//        String filename = "/path/to/video/300_50.mp4";
        String filename = "/Users/oneal/Downloads/300_50.mp4";
        params = new HashMap<String, String>();
        params.put("access_token", TokenServer.getAccessToken());
        uploadInfo = new HashMap<String, String>();
        uploadInfo.put("file_name", filename);       // file name: full path of file, mandatory
        uploadInfo.put("title", "标准日语第43课");     // Title: mandatory
        uploadInfo.put("tags", "日语 教育");          // Tags：mandatory
        uploadInfo.put("public_type", "all");       //video visibility（all：public（default），friend：friend only，password：password required to watch the video）
        uploader = new YoukuUploader(client_id, client_secret);
        result = uploader.upload(params, uploadInfo, filename, false); // 4th param：boolean（true：show progress false：dont show progress）
        System.out.print(result); //video id

    }

}
