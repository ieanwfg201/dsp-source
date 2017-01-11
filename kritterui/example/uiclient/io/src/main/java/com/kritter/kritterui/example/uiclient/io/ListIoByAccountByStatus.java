package com.kritter.kritterui.example.uiclient.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.insertion_order.IOListEntity;
import com.kritter.api.entity.insertion_order.Insertion_Order_List;
import com.kritter.constants.IOStatus;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class ListIoByAccountByStatus {
	/**
	 * Input 
	 * com.kritter.api.entity.insertion_order.IOListEntity  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.insertion_order.Insertion_Order_List Json String
	 */
    public static Insertion_Order_List createIO(String apiUrl) throws Exception{
    	IOListEntity iole = new IOListEntity();
    	iole.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001");
    	/*Above can also be obtained by /api/v1/meta/firstlevel/advertisersbyguid  */
    	iole.setStatus(IOStatus.NEW);
        /*Above can also be obtained by /api/v1/meta/firstlevel/iostatus  */
    	
    	/**
    	 * Optional
    	iole.setPage_no(page_no);
    	iole.setPage_size(page_size);
    	*/
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, iole.toJson());
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
        Insertion_Order_List iol = Insertion_Order_List.getObject(response.toString());
        System.out.println(iol.toJson());
        return iol;
    }
    
    public static void main(String args[]) throws Exception{
        ListIoByAccountByStatus.createIO(ExampleConstant.url_prefix+ExampleConstant.listiobyaccountbystatus);
    }
}
