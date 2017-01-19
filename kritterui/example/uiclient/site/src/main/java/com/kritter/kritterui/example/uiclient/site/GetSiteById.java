package com.kritter.kritterui.example.uiclient.site;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.site.Site;
import com.kritter.api.entity.site.SiteList;
import com.kritter.api.entity.site.SiteListEntity;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetSiteById {
	/**
	 * Input 1 
	 * com.kritter.api.entity.site.SiteListEntity  (Json String in Post Body)
	 * Output
	 * com.kritter.api.entity.site.SiteList  (Json String in Post Body)
	 */
	public static Site getSiteById(String apiUrl) throws Exception{
		SiteListEntity slE = new SiteListEntity();
		slE.setSite_int_id(63);

		URL url = new URL(apiUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);
		ObjectMapper mapper = new ObjectMapper();
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		mapper.writeValue(wr, slE.toJson());
		wr.flush();
		wr.close();
		int responseCode = conn.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		SiteList siteList = SiteList.getObject(response.toString());
		if(siteList.getSite_list() != null && siteList.getSite_list().size()>0
				&& siteList.getMsg()!= null && siteList.getMsg().getError_code() ==0){
			System.out.println(siteList.getSite_list().get(0).toJson().toString());
			return siteList.getSite_list().get(0);
			
		}else{
			System.out.println("SiteList Null");
		}
		return null;
	}
    
    public static void main(String args[]) throws Exception{
        GetSiteById.getSiteById(ExampleConstant.url_prefix+ExampleConstant.get_site_by_id);
    }
}
