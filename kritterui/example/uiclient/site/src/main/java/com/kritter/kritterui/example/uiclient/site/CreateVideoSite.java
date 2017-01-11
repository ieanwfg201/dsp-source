package com.kritter.kritterui.example.uiclient.site;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;

import com.kritter.api.entity.account.Account;
import com.kritter.api.entity.account.AccountMsgPair;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.api.entity.site.Site;
import com.kritter.constants.APIFrameworks;
import com.kritter.constants.APP_STORE_ID;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.HygieneCategory;
import com.kritter.constants.SITE_PLATFORM;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.AdPositionsOpenRTB;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoLinearity;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.constants.VideoPlaybackMethods;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;
import com.kritter.kritterui.example.uiclient.pub.GetPubAccount;

public class CreateVideoSite {
	/**
	 * Input 1 
	 * com.kritter.api.entity.account.Account  (Json String in Post Body)
	 * Input 2 
	 * com.kritter.api.entity.site.Site  (Json String in Post Body)
	 * Output
	 * com.kritter.api.entity.response.msg.Message Json String
	 * {"msg":"OK","error_code":0,"id":"63"}   {Where id is the unique internal id of site }
	 */
    public static Message cerateVideoSite(String apiUrl) throws Exception{
    	/**
    	 * First Get Pub Account
    	 */
        AccountMsgPair msgPair=GetPubAccount.getPublisher(ExampleConstant.url_prefix+ExampleConstant.get_pub);
        if(msgPair != null && msgPair.getAccount() != null && msgPair.getMsg() != null
        		&& msgPair.getMsg().getError_code()==0){
        	Account pubAccount= msgPair.getAccount() ;

        	Site site = new Site();
        	/**
        	 * Required
        	 */
        	site.setPub_guid(pubAccount.getGuid());
        	site.setPub_id(pubAccount.getId());

        	site.setFloor(0.0); /*>=0*/
        	site.setAllow_house_ads(true); /*can be set to false if not alolowed*/
        	site.setSite_platform_id(SITE_PLATFORM.WAP); 
        	/* Above can be obtained from /api/v1/meta/firstlevel/site_platform */ 
        	site.setSite_url("http://kritter.in"); /*To be set if platform is web or wap*/

        	site.setApp_id("app_id");/*To be set if platform is web*/
        	site.setApp_store_id(APP_STORE_ID.GOOGLE_PLAY.getAppStoreId());/*To be set if platform is app*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/app_stores */ 
        	site.setHygiene_list("["+HygieneCategory.FAMILY_SAFE.getCode()+"]"); /*Single selection*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/hygiene */
        	site.setOpt_in_hygiene_list("["+HygieneCategory.FAMILY_SAFE.getCode()+"]"); /*Json Array*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/hygiene */
        	
        	site.setCategories_tier_1_list("[]"); /*Json Array of Integers*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/tier1categories */
        	site.setCategories_tier_2_list("[]");/*Json Array of Integers*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/tier2categories */
        	site.setIs_advertiser_excluded(true);
        	site.setAdvertiser_json_array("[17]"); 
        	/*Above to be populated selection of adv is done for inclusion of exclusion */
        	/* Above can be obtained from /api/v1/meta/firstlevel/advertisers */
        	site.setCampaign_json_array("[17|48]"); /*Json Array*/
        	/*Above to be populated selection of campaign is done for inclusion or exclusion */
        	/* Above can be obtained from /api/v1/meta/secondlevel/campaignbyadvertisers/17,25
        	 * Above 17,25 refers to advertiser ids selected before */
            /**
             * Default Selected if not Populated
            site.setBilling_rules_json(billing_rules_json);
             */
        	site.setBilling_rules_json("70");
        	
        	site.setIs_category_list_excluded(true);
        	site.setCategory_list_inc_exc_tier_1("[]"); /*Json Array of Integers*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/tier1categories */
        	site.setCategory_list_inc_exc_tier_2("[]");/*Json Array of Integers*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/tier2categories */

        	site.setIs_creative_attr_exc(true);
        	site.setCreative_attr_inc_exc("[]");/*Json Array of Integers*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/creativeattr */
        	site.setIs_richmedia_allowed(false);
        	site.setModified_by(1); /*ASk for this id*/
        	site.setName("sitename");
        	site.setUrl_exclusion("[]");/*json arry of domains like ["rediff.com"]*/
        	site.setPassback_type(-1);
        	site.setIs_video(true);
        	site.setLinearity(VideoLinearity.LINEAR_IN_STREAM.getCode());
        	/* Above can be obtained from /api/v1/meta/firstlevel/videolinearity */
        	site.setMaxDurationSec(1); /*min duration of video*/
        	site.setMinDurationSec(30); /*max duration of video*/   	
        	site.setWidthPixel(320);/* width in pixel of video*/
        	site.setHeightPixel(50);/* height in pixel of video*/
        	site.setStrapi("["+APIFrameworks.VPAID_2_0.getCode()+"]"); /*Json Array*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/vastapiframework */
        	site.setStrcompaniontype("["+VASTCompanionTypes.Unknown.getCode()+"]");
        	/* Above can be obtained from /api/v1/meta/firstlevel/vastcompaniontype */
        	site.setStrdelivery("["+ContentDeliveryMethods.progressive.getCode()+"]"); /*Json Array*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/videodelivery */
        	site.setStrmimes("["+VideoMimeTypes.MPEG4.getCode()+"]"); /*Json Array*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/videomimes */
        	site.setStrplaybackmethod("["+VideoPlaybackMethods.Unknown.getCode()+"]");
        	/* Above can be obtained from /api/v1/meta/firstlevel/videoplaybackmethod */
        	site.setStrprotocols("["+VideoBidResponseProtocols.VAST_3_0.getCode()+"]");/*Json Array*/
        	/* Above can be obtained from /api/v1/meta/firstlevel/videoprotocols */
        	site.setPos(AdPositionsOpenRTB.Unknown.getCode());
        	/* Above can be obtained from /api/v1/meta/firstlevel/videoadpos */
        	site.setStartDelay(0); 
        	/*Above alowed values -> -11=Unknown= ,PreRoll=0,GenericMidRoll=-1,GenericPostRoll=-1,
        	 * MidRoll >0 -> value indicates start delay in second for midroll
        	 *  ....as per com.kritter.constants.VideoStartDelay*/


        	/**
        	 * Not Required Auto Generated
    	site.setId(id);
    	site.setGuid(guid);
    	site.setLast_modified(last_modified);
    	site.setStatus_id(status_id); -- Pending    	
      	site.setCampaign_inc_exc_schema(campaign_inc_exc_schema);

        	 */
        	/**
        	 * Not Required
    	site.setNative_call_to_action_keyname(native_call_to_action_keyname);
    	site.setNative_description_keyname(native_description_keyname);
    	site.setNative_description_maxchars(native_description_maxchars);
    	site.setNative_icon_imagesize(native_icon_imagesize);
    	site.setNative_icon_keyname(native_icon_keyname);
    	site.setNative_landingurl_keyname(native_landingurl_keyname);
    	site.setNative_layout(native_layout);
    	site.setNative_rating_count_keyname(native_rating_count_keyname);
    	site.setNative_screenshot_imagesize(native_screenshot_imagesize);
    	site.setNative_screenshot_keyname(native_screenshot_keyname);
    	site.setNative_title_keyname(native_title_keyname);
    	site.setNative_title_maxchars(native_title_maxchars);
    	site.setIs_native(is_native);
		site.setComments(comments);
    	site.setCreated_on(created_on);
		site.setNofill_backup_content(nofill_backup_content);
        site.setPassback_content_type(passback_content_type);
        	 */
        	URL url = new URL(apiUrl);
        	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        	conn.setRequestMethod("POST");
        	conn.setRequestProperty("Content-Type", "application/json");
        	conn.setDoOutput(true);
        	ObjectMapper mapper = new ObjectMapper();
        	DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        	mapper.writeValue(wr, site.toJson());
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
        }else{
        	System.out.println("Account Not Found");
        	return null;
        }
    }
    
    public static void main(String args[]) throws Exception{
        CreateVideoSite.cerateVideoSite(ExampleConstant.url_prefix+ExampleConstant.create_site);
    }
}
