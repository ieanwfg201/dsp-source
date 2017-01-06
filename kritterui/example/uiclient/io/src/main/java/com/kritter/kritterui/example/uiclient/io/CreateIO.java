package com.kritter.kritterui.example.uiclient.io;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.insertion_order.Insertion_Order;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.IOStatus;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class CreateIO {
	/**
	 * Input 
	 * com.kritter.api.entity.insertion_order.Insertion_Order  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"null"}   
	 */
    public static Message createIO(String apiUrl) throws Exception{
    	Insertion_Order io = new Insertion_Order();
    	/*Required*/
    	io.setName("SomeIOName");
    	io.setOrder_number("SomeOrderNumber");
    	io.setStatus(IOStatus.NEW);
        /*Above can also be obtained by /api/v1/meta/firstlevel/iostatus  */
    	io.setTotal_value(100); /*IO Value*/
    	io.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001");
    	/*Above can also be obtained by /api/v1/meta/firstlevel/advertisersbyguid  */
    	io.setBelongs_to(1);
    	/*Above can also be obtained by /api/v1/meta/firstlevel/activeadminarray  */
    	io.setModified_by(1); /*Ask for this*/
    	io.setCreated_by(1);/*Can be set same as Modified By*/
    	/**
    	 * Auto Populated
    	io.setCreated_on(created_on);
    	io.setLast_modified(last_modified);
    	io.setComment(comment);
    	 */
    	/**
    	 * Not Required
    	io.setCreated_by_name(created_by_name);
    	io.setAccount_name(account_name);
    	io.setBelongs_to_name(belongs_to_name);
    	 */
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, io.toJson());
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
        Message msg = Message.getObject(response.toString());
        
        System.out.println(msg.toJson());
        return msg;
    }
    
    public static void main(String args[]) throws Exception{
        CreateIO.createIO(ExampleConstant.url_prefix+ExampleConstant.create_io);
    }
}
