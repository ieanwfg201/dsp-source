package com.kritter.kritterui.example.uiclient.ad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.ad.Ad;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.BidType;
import com.kritter.constants.HygieneCategory;
import com.kritter.constants.MarketPlace;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class CreateAd {
	/**
	 * Input 
	 * com.kritter.api.entity.ad.Ad  (Json String in Post Body)
	 * 
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"69"}
	 */
    public static Message createAd(String apiUrl) throws Exception{
    	Ad ad = new Ad();
    	ad.setName("adName");
    	ad.setLanding_url("http://kritter.in");
    	ad.setAdv_domain("http://advertiser.com");
    	ad.setAdvertiser_bid(1);
    	ad.setInternal_max_bid(1);
    	ad.setBidtype(BidType.AUTO.getCode());
    	/*Above can also be obtained by /api/v1/meta/firstlevel/bidtype  */
    	ad.setCategories_tier_1_list("[]"); /*Json Array*/
    	/*Above can also be obtained by /api/v1/meta/firstlevel/tier1categories  */
    	ad.setCategories_tier_2_list("[]"); /*Json Array*/
    	/*Above can also be obtained by /api/v1/meta/firstlevel/tier2categories  */
    	ad.setHygiene_list("["+HygieneCategory.FAMILY_SAFE.getCode()+"]"); /*Json Array - Select 1*/
    	/*Above can also be obtained by /api/v1/meta/firstlevel/hygiene  */
    	ad.setMarketplace_id(MarketPlace.CPM);
    	/*Above can also be obtained by /api/v1/meta/firstlevel/marketplace  */
    	ad.setModified_by(1); /*Ask for this*/
    	ad.setTargeting_guid("011c3e84-ed0b-cb01-580a-a46953000008"); /*Targeting profile Guid*/
    	ad.setCreative_guid("011c3e84-ed0b-cb01-580a-6213e9000007"); /*Creative Container Guid*/
    	ad.setCreative_id(83);/*Creative Container id*/
    	ad.setCampaign_guid("011c3e84-ed0b-cb01-5800-05ead1000001"); /*Campaign Guid*/
    	ad.setCampaign_id(52);/*Campaign id*/
    	/**
    	 * Optional
    	ad.setCpa_goal(cpa_goal);
    	ad.setIs_frequency_capped(is_frequency_capped);
    	ad.setClick_freq_cap(click_freq_cap);
    	ad.setClick_freq_cap_count(click_freq_cap_count);
    	ad.setClick_freq_cap_type(click_freq_cap_type);
    	ad.setClick_freq_time_window(click_freq_time_window);
    	ad.setClickMacro(clickMacro);
    	ad.setClickMacroQuote(clickMacroQuote);
    	ad.setImp_freq_cap(imp_freq_cap);
    	ad.setImp_freq_cap_count(imp_freq_cap_count);
    	ad.setImp_freq_cap_type(imp_freq_cap_type);
    	ad.setImp_freq_time_window(imp_freq_time_window);
    	ad.setImpMacro(impMacro);
    	ad.setImpMacroQuote(impMacroQuote);
    	ad.setExternal_click_tracker(external_click_tracker);
    	ad.setExternal_imp_tracker(external_imp_tracker);
    	ad.setCreated_on(created_on);
    	ad.setExtclickType(extclickType);
    	 */
    	/**
    	 * Not Required-AutoPoulated
    	ad.setStatus_id(status_id); - Default pending
    	ad.setLast_modified(last_modified);
    	ad.setGuid(guid);
    	ad.setId(id);
    	ad.setFrequency_cap(frequency_cap);
    	ad.setTime_window(time_window);
    	 */
    	/**
    	 * Not Required
    	ad.setTracking_partner(tracking_partner);
    	ad.setComment(comment);
    	ad.setMma_tier_1_list(mma_tier_1_list);
    	ad.setMma_tier_2_list(mma_tier_2_list);
    	ad.setTargeting_profile_name(targeting_profile_name);
    	ad.setAllocation_ids(allocation_ids);
    	
    	 */
    	URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, ad.toJson());
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
        CreateAd.createAd(ExampleConstant.url_prefix+ExampleConstant.create_ad);
    }
}
