package com.kritter.kritterui.example.uiclient.reporting;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.reporting.ReportingEntity;
import com.kritter.constants.Frequency;
import com.kritter.constants.ReportingDIMTypeEnum;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetPubCreativeTrackingReport {
	/**
	 * Input 
	 * com.kritter.api.entity.reporting.ReportingEntity  (Json String in Post Body)
	 * 
	 * Output
	 * JSON String of dimension and metrics {Similar to Morris table/chart format}
	 */
    public static String getpubcreativetrackinreport(String apiUrl) throws Exception{
    	/*Available dimension and metrics listed . Rest fthe fields are not required*/;
    	ReportingEntity entity = new ReportingEntity();
    	/*Set only Start ,end date Str and optionally pubId*/
    	entity.setStart_time_str("2016-01-01 00:00:00");
    	entity.setEnd_time_str("2016-12-14 00:00:00");
    	LinkedList<Integer> ll= new LinkedList<Integer>();
    	ll.add(112);
    	entity.setPubId(ll);;
    	URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        System.out.println(entity.toJson().toString());
        mapper.writeValue(wr, entity.toJson());
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
        String str = response.toString();
        System.out.println(str);
        return str;
    }
    
    public static void main(String args[]) throws Exception{
        GetPubCreativeTrackingReport.getpubcreativetrackinreport(ExampleConstant.url_prefix+ExampleConstant.pubcreativetrackinreport);
    }
}
