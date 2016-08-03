package com.kritter.entity.vast.normal.twodotzero;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.Marshaller;

import org.slf4j.Logger;

import com.kritter.constants.TEvent;
import com.kritter.constants.TEventType;
import com.kritter.constants.VASTCompanionTypes;
import com.kritter.constants.VASTKritterTrackingEventTypes;
import com.kritter.constants.VASTNonLinearTrackingEventTypes;
import com.kritter.constants.VideoLinearity;
import com.kritter.constants.VideoMacros;

public class CreateVastNormalTwoDotZero {
	
    public static VastNormal createVast(String csc, String adId,String impressionId,
    		String errorUrl, String pubGuid, int linearity,
            int companionType, Integer[] tracking, String trackingEventUrl, String adName,
            String durationinFormat, String clickurl,String cdnUrl,
            String creativeId, String delivery, String mimeType,
            String bitRate, int width, int height){
       	AdSystem adSystem = new AdSystem();
       	adSystem.setVersion("2.0");
       	adSystem.setStr(pubGuid);
       	AdTitle adTitle = new AdTitle();
       	adTitle.setStr(adName);
       	/** Description description = new Description();
       		description.setStr(str); 
       		Survey survey = new Survey();
       		survey.setStr(str);S
       		Extension extensions = new Extension();
       		extensions.setStr(str);
       		extensions.setType(type);
       	*/
       	Error error = new Error();
        if(errorUrl != null){
            error.setStr(errorUrl+"&"+TEvent.ttype+"="+TEventType.videoerror.getName()+"&"+TEvent.tevent+"="+VideoMacros.ERRORCODE.getName());
        }
        Impression impression = new Impression();
        impression.setId(impressionId);
        impression.setStr(csc);
        List<Creative> creatives = new LinkedList<Creative>();
        Creative creative = new Creative();
        creative.setId(adId);
        /** creative.setAdID(adID);
        	creative.setSequence(sequence);
         */
        if(linearity == VideoLinearity.LINEAR_IN_STREAM.getCode()){
        	/**	AdParameters adParameters = new AdParameters();
        		adParameters.setStr(str);
        	*/
        	Linear linear = new Linear();
        	/** linear.setAdParameters(adParameters);
        	 */
        	if(durationinFormat != null){
        		Duration duration = new Duration();
        		duration.setStr(durationinFormat);
        		linear.setDuration(duration);
        	}
        	List<MediaFile> mediaFiles = new LinkedList<MediaFile>();
        	MediaFile mediaFile = new MediaFile();
        	if(bitRate != null){
        		mediaFile.setBitrate(bitRate);
        	}
        	if(delivery != null){
        		mediaFile.setDelivery(delivery);
        	}
        	mediaFile.setHeight(height);
        	if(creativeId != null){
        		mediaFile.setId(creativeId);
        	}
        	/**	mediaFile.setScalable(scalable);
        		mediaFile.setMaintainAspectRatio(maintainAspectRatio);
        		mediaFile.setApiFramework(apiFramework);
        	 */
        	mediaFile.setStr(cdnUrl);
        	mediaFile.setType(mimeType);
        	mediaFile.setWidth(width);
        	mediaFiles.add(mediaFile);
        	linear.setMediaFiles(mediaFiles);
            if(tracking != null){
                List<Tracking> trackingEvents = new LinkedList<Tracking>();
                for(Integer i: tracking){
                    VASTKritterTrackingEventTypes vtet  = VASTKritterTrackingEventTypes.getEnum(i);
                    if(vtet != null){
                        Tracking t = new Tracking();
                        t.setEvent(vtet.getName());
                        t.setStr(trackingEventUrl+"&"+TEvent.ttype+"=video&tevent="+vtet.getName());
                        trackingEvents.add(t);
                    }
                }
                linear.setTrackingEvents(trackingEvents);
            }
            if(clickurl != null){
            	/**	CustomClick customClick = new CustomClick();
            		customClick.setId(id);
            		customClick.setStr(str);
            		ClickTracking clickTracking = new ClickTracking();
            		clickTracking.setStr(str);
            	*/
            	ClickThrough clickThrough = new ClickThrough();
            	clickThrough.setStr(clickurl);
            	VideoClicks videoClicks = new VideoClicks();
            	videoClicks.setClickThrough(clickThrough);
            	/**	videoClicks.setClickTracking(clickTracking);
					videoClicks.setCustomClick(customClick);
            	 */
            	linear.setVideoClicks(videoClicks);
            }
        	creative.setLinear(linear);
        }else if(linearity == VideoLinearity.NON_LINEAR_OVERLAY.getCode()){
        	if(companionType != VASTCompanionTypes.Unknown.getCode()){
        		List<Companion> companionAds = new LinkedList<Companion>();
        		Companion companion = new Companion();
        		/**
        			companion.setExpandedWidth(expandedWidth);
        			companion.setExpandedHeight(expandedHeight);
        			companion.setApiFramework(apiFramework);
        			companion.setHTMLResource(hTMLResource);
        			companion.setIFrameResource(iFrameResource);
        			companion.setAltText(altText);
        			companion.setAdParameters(adParameters);
        		*/
        		if(creativeId != null){
        			companion.setId(creativeId);
        		}
        		companion.setWidth(width);
        		companion.setHeight(height);
        		StaticResource staticResource = new StaticResource();
        		staticResource.setCreativeType(mimeType);
        		staticResource.setStr(cdnUrl);
        		companion.setStaticResource(staticResource);
                List<Tracking> trackingEvents = new LinkedList<Tracking>();
                Tracking t = new Tracking();
                t.setEvent(VASTNonLinearTrackingEventTypes.creativeView.getName());
                t.setStr(trackingEventUrl+"&"+TEvent.ttype+"="+TEventType.video.getName()+"&"+TEvent.tevent+"="+VASTNonLinearTrackingEventTypes.creativeView.getName());
                trackingEvents.add(t);
        		companion.setTrackingEvents(trackingEvents);
        		if(clickurl != null){
        			CompanionClickThrough companionClickThrough = new CompanionClickThrough();
                	companionClickThrough.setStr(clickurl);
        			companion.setCompanionClickThrough(companionClickThrough);
        		}
        		companionAds.add(companion);
                creative.setCompanionAds(companionAds);
        	}else{
        		List<NonLinear> nonLinearAds = new LinkedList<NonLinear>();
        		NonLinear nonLinear = new NonLinear();
        		if(creativeId != null){
            		nonLinear.setId(creativeId);
        		}
        		nonLinear.setWidth(width);
        		nonLinear.setHeight(height);
        		StaticResource staticResource = new StaticResource();
        		staticResource.setCreativeType(mimeType);
        		staticResource.setStr(cdnUrl);
        		nonLinear.setStaticResource(staticResource);
                if(tracking != null){
                    List<Tracking> trackingEvents = new LinkedList<Tracking>();
                    for(Integer i: tracking){
                        VASTKritterTrackingEventTypes vtet  = VASTKritterTrackingEventTypes.getEnum(i);
                        if(vtet != null){
                            Tracking t = new Tracking();
                            t.setEvent(vtet.getName());
                            t.setStr(trackingEventUrl+"&"+TEvent.ttype+"=video&tevent="+vtet.getName());
                            trackingEvents.add(t);
                        }
                    }
                    nonLinear.setTrackingEvents(trackingEvents);
                }
                if(clickurl != null){
                	NonLinearClickThrough nonLinearClickThrough = new NonLinearClickThrough();
                	nonLinearClickThrough.setStr(clickurl);
        			nonLinear.setNonLinearClickThrough(nonLinearClickThrough);
                }
        		/**
        			nonLinear.setExpandedWidth(expandedWidth);
        			nonLinear.setExpandedHeight(expandedHeight);
        			nonLinear.setScalable(scalable);
        			nonLinear.setMaintainAspectRatio(maintainAspectRatio);
        			nonLinear.setMinSuggestedDuration(minSuggestedDuration);
        			nonLinear.setApiFramework(apiFramework);
        			nonLinear.setIFrameResource(iFrameResource);
        			nonLinear.setHTMLResource(hTMLResource);
        			nonLinear.setAdParameters(adParameters);
        		 */
        		nonLinearAds.add(nonLinear);
        		creative.setNonLinearAds(nonLinearAds);
        	}
        }
        creatives.add(creative);
        InLine inline = new InLine();
    	inline.setAdSystem(adSystem);
    	inline.setAdTitle(adTitle);
    	/** inline.setDescription(description); 
    		inline.setSurvey(survey);
    		inline.setExtensions(extensions);
    	*/
    	inline.setError(error);
    	inline.setImpression(impression);
    	inline.setCreatives(creatives);
    	Ad ad = new Ad();
    	ad.setId(adId);
    	ad.setInline(inline);
    	VastNormal vastnormal = new VastNormal();
    	vastnormal.setAd(ad);
        return vastnormal;
    }
    public static String createVastNormalString(String csc, String adId,String impressionId,
            String errorUrl, String pubGuid, int linearity,
            int companionType, Integer[] tracking, String trackingEventUrl,Logger  logger,
            String adName,
            String durationinFormat, String clickurl,String cdnUrl,
            String creativeId, String delivery, String mimeType,
            String bitRate, int width, int height){
        
        VastNormal vastNormal = createVast(csc, adId, impressionId,  errorUrl, pubGuid, 
                linearity, companionType, tracking, trackingEventUrl,
                adName,
                durationinFormat, clickurl, cdnUrl,
                creativeId, delivery, mimeType,
                bitRate, width, height);
        if(vastNormal == null){
            return null;
        }
        try{
            Marshaller marshallerObj = GetJaxBContextVastNormalTwoDotZero.getContext().createMarshaller();  
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshallerObj.marshal(vastNormal, sw);
            return sw.toString();
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }
}
