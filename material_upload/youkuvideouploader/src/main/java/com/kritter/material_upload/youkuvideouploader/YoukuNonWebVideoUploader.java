package com.kritter.material_upload.youkuvideouploader;

import com.youku.uploader.YoukuUploader;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class YoukuNonWebVideoUploader {
	private static final Logger LOG = LogManager.getLogger(YoukuNonWebVideoUploader.class.getName());
	private static YoukuUploader uploader;
    @Getter@Setter
    private String videoid;
    public void upload(String client_id,String client_secret,String refresh_token,
    		String token_url, String fullFileName,String title,String tag) {
    	try{
    		String result = "";
    		HashMap<String, String> params, uploadInfo;
    		params = new HashMap<String, String>();
    		TokenNonWeb tokenNonWeb = new TokenNonWeb();
    		tokenNonWeb.refreshToken(client_id, client_secret, refresh_token, token_url);
    		params.put("access_token", tokenNonWeb.getAccessToken());
    		uploadInfo = new HashMap<String, String>();
    		uploadInfo.put("file_name", fullFileName);       // file name: full path of file, mandatory
    		uploadInfo.put("title", title);     // Title: mandatory
    		uploadInfo.put("tags", tag);          // Tags：mandatory
    		uploadInfo.put("public_type", "all");       //video visibility（all：public（default），friend：friend only，password：password required to watch the video）
    		uploader = new YoukuUploader(client_id, client_secret);
    		result = uploader.upload(params, uploadInfo, fullFileName, false); // 4th param：boolean（true：show progress false：dont show progress）
    		setVideoid(result);
    		LOG.info(result); //video id
    	}catch(Exception e){
    		LOG.error(e.getMessage(),e);
    	}
    }

    public static void main(String args[]){
    	/**YoukuNonWebVideoUploader h = new YoukuNonWebVideoUploader();
    	h.upload("10a56fb7e9c3bd9c", "b4ef90f3450b190ad3d95cc0b1c0549a", "3f2f8405a24eacc2c1c39d2c098e190a",
    			"https://openapi.youku.com/v2/oauth2/token", "/home/rohan/testvideos/300_50.mp4", "testvideowithouttag");

    	 * {"video_id":"XMTcyNjU5NDc0NA=="}
    	 * http://v.youku.com/v_show/id_XMTcyNjU5ODUzNg==.html
    	 */

    }
}
