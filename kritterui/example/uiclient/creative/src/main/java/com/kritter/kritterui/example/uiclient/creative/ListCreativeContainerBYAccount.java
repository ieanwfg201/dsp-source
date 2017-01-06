package com.kritter.kritterui.example.uiclient.creative;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.creative_container.CreativeContainerList;
import com.kritter.api.entity.creative_container.CreativeContainerListEntity;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class ListCreativeContainerBYAccount {
	/**
	 * Input 
	 * com.kritter.api.entity.creative_container.CreativeContainerListEntity (Jsont String)
	 * Output
	 * com.kritter.api.entity.creative_container..CreativeContainerList  (Json String)
	 * 
	 * NOTE: Video should be uploaded and video info should be populated before calling this 
	 * 
	 */
    public static CreativeContainerList getDirectVideoCC(String apiUrl) throws Exception{
    	
    	CreativeContainerListEntity ccle = new CreativeContainerListEntity();
    	ccle.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001");/*GUID of account - can be obtained by GetAdvAccount*/
    	/**
    	 * Not Required
    	ccle.setId(83);
    	ccle.setCcenum(ccenum);
    	ccle.setComment(comment);
    	ccle.setPage_no(page_no);
    	ccle.setPage_size(page_size);
    	ccle.setId_list(id_list);
    	ccle.setStatus_id(status_id);
    	 */
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, ccle.toJson());
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
        CreativeContainerList ccl = CreativeContainerList.getObject(response.toString());
        System.out.println(ccl.toJson());
        return ccl;
    }
    
    public static void main(String args[]) throws Exception{
        ListCreativeContainerBYAccount.getDirectVideoCC(ExampleConstant.url_prefix+ExampleConstant.list_creativecontainer);
    }
}
