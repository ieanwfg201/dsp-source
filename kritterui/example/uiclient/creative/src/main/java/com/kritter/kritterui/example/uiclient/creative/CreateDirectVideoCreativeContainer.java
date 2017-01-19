package com.kritter.kritterui.example.uiclient.creative;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.codehaus.jackson.map.ObjectMapper;
import com.kritter.api.entity.creative_container.Creative_container;
import com.kritter.api.entity.response.msg.Message;
import com.kritter.constants.APIFrameworks;
import com.kritter.constants.ContentDeliveryMethods;
import com.kritter.constants.CreativeFormat;
import com.kritter.constants.StatusIdEnum;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.VASTKritterTrackingEventTypes;
import com.kritter.constants.VideoBidResponseProtocols;
import com.kritter.constants.VideoBoxing;
import com.kritter.constants.VideoDemandType;
import com.kritter.constants.VideoLinearity;
import com.kritter.constants.VideoMaxExtended;
import com.kritter.constants.VideoMimeTypes;
import com.kritter.constants.VideoPlaybackMethods;
import com.kritter.kritterui.example.uiclient.common.ExampleConstant;

public class CreateDirectVideoCreativeContainer {
	/**
	 * Input 
	 * com.kritter.api.entity.creative_container.Creative_container  (Json String)
	 * Output
	 * com.kritter.api.entity.response.msg.Message (Json String)
	 * 
	 * NOTE: Video should be uploaded and video info should be populated before calling this 
	 * 
	 */
    public static Message createDirectVideoCC(String apiUrl) throws Exception{

        Creative_container cc = new Creative_container();
        cc.setAccount_guid("011c3e84-ed0b-cb01-57fa-c1f271000001");/*GUID of account - can be obtained by GetAdvAccount*/
        cc.setFormat_id(CreativeFormat.VIDEO.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/creativetypes  */
        cc.setModified_by(1);/*Ask For this*/
        cc.setStatus_id(StatusIdEnum.Active);
        /*Above can also be obtained by /api/v1/meta/firstlevel/statusids  */
        cc.setLabel("mylabel");
        cc.setCreative_attr("["+72+"]"); /*Json Array - Set Video Creative attribute*/
        /*Above can also be obtained by /api/v1/meta/firstlevel/creativeattr  */

        cc.setApi(APIFrameworks.Unknown.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/vastapiframework  */
        cc.setBitrate(-1); /*in Kbps, -1 if not known*/
        cc.setDuration(10);/*Suration in Seconds*/
        cc.setWidth(300);/*width in pixels*/
        cc.setHeight(50);/*width in pixels*/
        cc.setBoxingallowed(VideoBoxing.Unknown.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/videoboxing  */
        cc.setLinearity(VideoLinearity.LINEAR_IN_STREAM.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/videolinearity  */
        cc.setDelivery(ContentDeliveryMethods.Unknown.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/videodelivery  */
        cc.setMaxextended(VideoMaxExtended.Unknown.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/videomaxextended  */
        cc.setPlaybackmethod(VideoPlaybackMethods.Unknown.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/videoplaybackmethod  */
        cc.setCompaniontype(VASTCompanionTypes.Unknown.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/vastcompaniontype  */
        cc.setMime(VideoMimeTypes.MPEG4.getCode()); /*This should in 1-1 relationship between the video format uploaded*/
        /*Above can also be obtained by /api/v1/meta/firstlevel/videomimes  */
        cc.setProtocol(VideoBidResponseProtocols.VAST_3_0.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/videoprotocols  */
        cc.setVideoDemandType(VideoDemandType.DirectVideo.getCode());
        /*Above can also be obtained by /api/v1/meta/firstlevel/videodemandtype  */
        cc.setTrackingStr("["+VASTKritterTrackingEventTypes.firstQuartile.getCode()+"]"); /*Json Array*/
        /*Above can also be obtained by /api/v1/meta/firstlevel/videotrackingevents  */
        cc.setStartdelay(0);
    	/*Above alowed values -> -11=Unknown= ,PreRoll=0,GenericMidRoll=-1,GenericPostRoll=-1,
    	 * MidRoll >0 -> value indicates start delay in second for midroll
    	 *  ....as per com.kritter.constants.VideoStartDelay*/
        cc.setDirect_videos("[27]"); /*Id from videoinfo get,list or create Json Array - Only one video info per creative container*/
        
        /**
         * Not Required - Auto Populated
		cc.setLast_modified(last_modified);
        cc.setCreated_on(created_on);
        cc.setId(id);
        cc.setGuid(guid);

         */
        /**
         * Not Required
        cc.setNative_active_players(native_active_players);
        cc.setNative_cta(native_cta);
        cc.setNative_desc(native_desc);
        cc.setNative_download_count(native_download_count);
        cc.setNative_icons(native_icons);
        cc.setNative_rating(native_rating);
        cc.setNative_screenshots(native_screenshots);
        cc.setNative_title(native_title);
        cc.setVastTagMacro(vastTagMacro);
        cc.setVastTagMacroQuote(vastTagMacroQuote);
        cc.setVastTagUrl(vastTagUrl);
		cc.setComment(comment);
        cc.setExt_resource_url(ext_resource_url);
        cc.setCreative_macro(creative_macro);
        cc.setCreative_macro_quote(creative_macro_quote);
        cc.setHtml_content(html_content);
        cc.setResource_uri_ids(resource_uri_ids);
        cc.setText(text);

         */
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        ObjectMapper mapper = new ObjectMapper();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        mapper.writeValue(wr, cc.toJson());
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
        CreateDirectVideoCreativeContainer.createDirectVideoCC(ExampleConstant.url_prefix+ExampleConstant.create_creativecontainer);
    }
}
