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
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class GetTrackingReport {
	/**
	 * Input 
	 * com.kritter.api.entity.reporting.ReportingEntity  (Json String in Post Body)
	 * 
	 * Output
	 * JSON String of dimension and metrics {Similar to Morris table/chart format}
	 */
    public static String getTrackingReport(String apiUrl) throws Exception{
    	/*Available dimension and metrics listed . Reso fthe fields are not required*/;
    	ReportingEntity entity = new ReportingEntity();
    	entity.setStart_time_str("2016-01-01 00:00:00");
    	entity.setEnd_time_str("2016-05-01 00:00:00");
    	entity.setFrequency(Frequency.DATERANGE); /*Set DATERANGE for daily report or TODAY/ADMIN_INTERNAL_HOURLY for hourly. Hourly should be avoided*/
    	/*Above can also be obtained by /api/v1/meta/firstlevel/freqduration  */

    	entity.setDate_as_dimension(true); /*True if date to be received in the report*/
    	/*Dimensions*/
    	entity.setPubId(new LinkedList<Integer>());/*Integer list of ids, null 
    					if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/firstlevel/publishers  */
    	entity.setSiteId(null);/*Integer list of ids, null 
							if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/secondlevel/sitesByPublishers/<csv ids of publishers>  */
    	entity.setAdvId(new LinkedList<Integer>());/*Integer list of ids, null 
						if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/firstlevel/advertisersbyid  */
    	entity.setCampaignId(null);/*Integer list of ids, null 
					if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/secondlevel/campaignbyadvertiserid/<csv ids of advId>  */
    	entity.setAdId(null);/*Integer list of ids, null 
			if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/secondlevel/adsByCampaign/<csv ids of campaignIds>  */
    	entity.setTerminationReason(new LinkedList<String>());/*String list of entity, null 
			if not required,empty list if all required, specific string if specific entity required  */
    	/*Above can also be obtained by /api/v1/meta/firstlevel/terminationReasonPostimp  */
    	entity.setTeventtype(new LinkedList<String>());/*String list of entity, null 
				if not required,empty list if all required, specific string if specific entity required  */
    	/*Above can also be obtained by /api/v1/meta/firstlevel/teventtype  */
    	entity.setCountryId(null);/*Integer list of ids, null 
			if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/firstlevel/countries  */
    	entity.setCountryCarrierId(null);/*Integer list of ids, null 
				if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/secondlevel/carrierbycountries/<csv ids of countries>  */
    	entity.setDeviceOsId(null);/*Integer list of ids, null 
			if not required,empty list if all ids required, specific ids if specific ids required  */
    	/*Above can also be obtained by /api/v1/meta/firstlevel/os  */
    	
    	/*Metric*/
    	entity.setTotal_event(true);
    	/*
    	 * Optional
    	entity.setTevent(tevent);
    	entity.setDeviceManufacturerId(deviceManufacturerId);
    	*/
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
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
        GetTrackingReport.getTrackingReport(ExampleConstant.url_prefix+ExampleConstant.trackingreport);
    }
}
