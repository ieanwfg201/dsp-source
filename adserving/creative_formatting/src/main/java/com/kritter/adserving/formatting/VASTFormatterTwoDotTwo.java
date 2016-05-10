package com.kritter.adserving.formatting;

import com.kritter.entity.reqres.entity.ResponseAdInfo;
import java.util.Set;

/**
 * This formatter is used for creating video ad response as per the IAB VAST 2.0
 * protocol. This formatter is used by an ad-exchange's response creator to
 * enable video ad serving by means of sending out a valid VAST 2.0 ad payload.
 */
public class VASTFormatterTwoDotTwo
{
    private static StringBuffer VAST_INLINE_PAYLOAD_TEMPLATE = new StringBuffer();
    private static StringBuffer VAST_INLINE_AD_PAYLOAD_TEMPLATE = new StringBuffer();
    private static StringBuffer VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE = new StringBuffer();

    public enum VIDEO_TRACKING_EVENT
    {

        CREATIVE_VIEW("creativeView"),
        START("start"),
        MIDPOINT("midpoint"),
        FIRST_QUARTILE("firstQuartile"),
        THIRD_QUARTILE("thirdQuartile"),
        COMPLETE("complete"),
        MUTE("mute"),
        UNMUTE("unmute"),
        PAUSE("pause"),
        REWIND("rewind"),
        RESUME("resume"),
        FULLSCREEN("fullscreen"),
        EXPAND("expand"),
        COLLAPSE("collapse"),
        ACCEPT_INVITATION("acceptInvitation"),
        CLOSE("close");

        private String event;

        public String getEvent()
        {
            return event;
        }

        VIDEO_TRACKING_EVENT(String event)
        {
            this.event = event;
        }
    }

    static
    {
        VAST_INLINE_PAYLOAD_TEMPLATE.append("<VAST version=\"2.0\" xmlns:xsi=\"http://www.w3" +
                                                   ".org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"oxml.xsd\">");

        VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE.append("<Creative id=\"$VAST_CREATIVE_ID\" AdID=\"$VAST_AD_ID\"");
        VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE.append("<Linear>");
        VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE.append("<Duration>$VAST_CREATIVE_DURATION</Duration>");
        VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE.append("<TrackingEvents>" +
                "<Tracking event=\"creativeView\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=creativeView]]>" +
                "</Tracking>" +
                "<Tracking event=\"start\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=start]]>" +
                "</Tracking>" +
                "<Tracking event=\"midpoint\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=midpoint]]>" +
                "</Tracking>" +
                "<Tracking event=\"firstQuartile\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=firstQuartile]]>" +
                "</Tracking>" +
                "<Tracking event=\"thirdQuartile\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=thirdQuartile]]>" +
                "</Tracking>" +
                "<Tracking event=\"complete\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=complete]]>" +
                "</Tracking>" +
                "<Tracking event=\"mute\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=mute]]>" +
                "</Tracking>" +
                "<Tracking event=\"unmute\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=unmute]]>" +
                "</Tracking>" +
                "<Tracking event=\"pause\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=pause]]>" +
                "</Tracking>" +
                "<Tracking event=\"rewind\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=rewind]]>" +
                "</Tracking>" +
                "<Tracking event=\"resume\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=resume]]>" +
                "</Tracking>" +
                "<Tracking event=\"fullscreen\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=fullscreen]]>" +
                "</Tracking>" +
                "<Tracking event=\"expand\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=expand]]>" +
                "</Tracking>" +
                "<Tracking event=\"collapse\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=collapse]]>" +
                "</Tracking>" +
                "<Tracking event=\"acceptInvitation\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=acceptInvitation]]>" +
                "</Tracking>" +
                "<Tracking event=\"close\">" +
                "<![CDATA[$VIDEO_EVENT_TRACKING_URI?event=close]]>" +
                "</Tracking>" +
                "</TrackingEvents>");
        VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE.append("<VideoClicks><ClickThrough><![CDATA[$VAST_CLICK_URL]]>" +
                                                            "</ClickThrough></VideoClicks>");
        VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE.append("<MediaFiles>" +
                                                        "<MediaFile delivery=\"$VAST_MEDIA_FILE_DELIVERY\" " +
                                                        "bitrate=\"$VAST_MEDIA_FILE_BITRATE\" " +
                                                        "width=\"$VAST_MEDIA_FILE_WIDTH\" " +
                                                        "height=\"$VAST_MEDIA_FILE_HEIGHT\" " +
                                                        "type=\"$VAST_MEDIA_FILE_TYPE\"> " +
                                                        "<![CDATA[$VAST_MEDIA_FILE_SOURCE_URL]]> " +
                                                        "</MediaFile> " +
                                                        "<MediaFiles>" +
                                                        "</Linear>" +
                                                        "</Creative>");

        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<Ad id=\"$VAST_AD_ID\"");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<InLine>");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<AdSystem version=\"$VAST_AD_SERVER_VERSION\">");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("$VAST_AD_SERVER_NAME</AdSystem>");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<AdTitle>");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("$VAST_AD_TITLE</AdTitle>");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<Description>$VAST_AD_DESCRIPTION</Description>");
        //VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<Survey>$VAST_AD_SURVEY_URL</Survey>");
        //VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<Error>$VAST_AD_ERROR_URL</Error>");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<Impression><![CDATA[$VAST_AD_IMPRESSION_URL]]></Impression>");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("<Creatives>");
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append(VAST_INLINE_AD_CREATIVE_LINEAR_PAYLOAD_TEMPLATE);
        VAST_INLINE_AD_PAYLOAD_TEMPLATE.append("</Creatives>");



    }

    public String prepareVASTPayLoad(Set<ResponseAdInfo> responseAdInfoSet,String videoEventTrackingURI,String adServerName)
    {


        return null;
    }

}
