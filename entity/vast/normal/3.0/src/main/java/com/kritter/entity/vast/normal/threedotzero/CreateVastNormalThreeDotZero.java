package com.kritter.entity.vast.normal.threedotzero;

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

public class CreateVastNormalThreeDotZero {
	
    public static VastNormal createVast(String csc, String adId,String impressionId,
    		String errorUrl, String pubGuid, int linearity,
            int companionType, Integer[] tracking, String trackingEventUrl, String adName,
            String durationinFormat, String clickurl,String cdnUrl,
            String creativeId, String delivery, String mimeType,
            String bitRate, int width, int height, String skipOffset){
    	Error error = new Error();
        if(errorUrl != null){
            error.setStr(errorUrl+"&"+TEvent.ttype+"="+TEventType.videoerror.getName()+"&"+TEvent.tevent+"="+VideoMacros.ERRORCODE.getName());
        }
        AdSystem adSystem = new AdSystem();
        adSystem.setStr(pubGuid);
        AdTitle adTitle = new AdTitle();
        adTitle.setStr(adName);
        Impression impression = new Impression();
        impression.setId(impressionId);
        impression.setStr(csc);
        Inline inline = new Inline();
        inline.setAdSystem(adSystem);
        inline.setAdTitle(adTitle);
        /**
        	inline.setDescription(description);
        	inline.setAdvertiser(advertiser);
        	inline.setPricing(pricing);
        	inline.setSurvey(survey);
        	inline.setError(error);
        	inline.setExtensions(extensions);
         */
        List<Creative> creatives = new LinkedList<Creative>();
        Creative creative = new Creative();
        creative.setAdID(adId);
        creative.setId(creativeId);
        if(linearity == VideoLinearity.LINEAR_IN_STREAM.getCode()){
        	Linear linear = new Linear();
        	/**
        		linear.setAdParameters(adParameters);
        		linear.setIcons(icons);
        	 */
        	if(durationinFormat != null){
        		Duration duration = new Duration();
        		duration.setStr(durationinFormat);
        		linear.setDuration(duration);
        	}
        	List<MediaFile> mediaFiles = new LinkedList<MediaFile>();
        	MediaFile mediaFile = new MediaFile();
        	if(delivery != null){
        		mediaFile.setDelivery(delivery);
        	}
        	mediaFile.setHeight(height);
        	mediaFile.setStr(cdnUrl);
        	if(mimeType != null){
        		mediaFile.setType(mimeType);
        	}
        	mediaFile.setWidth(width);
        	/**
        		mediaFile.setCodec(codec);
        		mediaFile.setId(id);
        		mediaFile.setBitrate(bitrate);
        		mediaFile.setMinBitrate(minBitrate);
        		mediaFile.setMaxBitrate(maxBitrate);
        		mediaFile.setScalable(scalable);
        		mediaFile.setMantainAspectRatio(mantainAspectRatio);
        		mediaFile.setApiFramework(apiFramework);
        	 */
        	mediaFiles.add(mediaFile);
        	linear.setMediaFiles(mediaFiles);
        	if(skipOffset != null){
        		linear.setSkipoffset(skipOffset);
        	}
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
            	ClickThrough clickThrough = new ClickThrough();
            	/**
					clickThrough.setId(id);
            	 */
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
        		if(creativeId != null){
        			companion.setId(creativeId);
        		}
        		companion.setWidth(width);
        		companion.setHeight(height);
        		StaticResource staticResource = new StaticResource();
        		if(mimeType != null){
        			staticResource.setCreativeType(mimeType);
        		}
        		staticResource.setStr(cdnUrl);
        		companion.setStaticResource(staticResource);
        		if(clickurl != null){
        			CompanionClickThrough companionClickThrough = new CompanionClickThrough();
        			companionClickThrough.setStr(clickurl);
        			companion.setCompanionClickThrough(companionClickThrough);
        		}
                List<Tracking> trackingEvents = new LinkedList<Tracking>();
                Tracking t = new Tracking();
                t.setEvent(VASTNonLinearTrackingEventTypes.creativeView.getName());
                t.setStr(trackingEventUrl+"&"+TEvent.ttype+"="+TEventType.video.getName()+"&"+TEvent.tevent+"="+VASTNonLinearTrackingEventTypes.creativeView.getName());
                trackingEvents.add(t);
        		companion.setTrackingEvents(trackingEvents);
        		/**
        			companion.setAdSlotID(adSlotID);
        			companion.setApiFramework(apiFramework);
        			companion.setAssetHeight(assetHeight);
        			companion.setAssetWidth(assetWidth);
        			companion.setExpandedHeight(expandedHeight);
        			companion.setExpandedWidth(expandedWidth);
        			companion.setAdParameters(adParameters);
        			companion.setAltText(altText);
        			companion.setHTMLResource(hTMLResource);
        			companion.setIFrameResource(iFrameResource);
        			companion.setCompanionClickTracking(companionClickTracking);
        		 */
        		companionAds.add(companion);
        		creative.setCompanionAds(companionAds);
        	}else{
        		NonLinearAds nla= new NonLinearAds();
        		List<NonLinear> nonLinearAds = new LinkedList<NonLinear>(); 
        		NonLinear nonLinearAd = new NonLinear();
        		if(creativeId != null){
        			nonLinearAd.setId(creativeId);
        		}
        		nonLinearAd.setWidth(width);
        		nonLinearAd.setHeight(height);
        		if(clickurl != null){
        			NonLinearClickThrough nonLinearClickThrough = new NonLinearClickThrough();
                	nonLinearClickThrough.setStr(clickurl);
                	nonLinearAd.setNonLinearClickThrough(nonLinearClickThrough);
        		}
        		StaticResource staticResource = new StaticResource();
        		if(mimeType != null){
        			staticResource.setCreativeType(mimeType);
        		}
        		staticResource.setStr(cdnUrl);
        		nonLinearAd.setStaticResource(staticResource);
        		/**
        			nonLinearAd.setApiFramework(apiFramework);
        			nonLinearAd.setExpandedHeight(expandedHeight);
        			nonLinearAd.setExpandedWidth(expandedWidth);
        			nonLinearAd.setScalable(scalable);
        			nonLinearAd.setMaintainAspectRatio(maintainAspectRatio);
        			nonLinearAd.setMinSuggestedDuration(minSuggestedDuration);
        			nonLinearAd.setNonLinearClickTracking(nonLinearClickTracking);
        			nonLinearAd.setHTMLResource(hTMLResource);
        			nonLinearAd.setIFrameResource(iFrameResource);
        			nonLinearAd.setAdParameters(adParameters);
        		 */
        		nonLinearAds.add(nonLinearAd);
        		nla.setNonLinearAds(nonLinearAds);
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
            		nla.setTrackingEvents(trackingEvents);;
                }

        		creative.setNonLinearAds(nla);
        	}
        }
        /**
        	creative.setSequence(sequence);
        	creative.setApiFramework(apiFramework);
        	creative.setCreativeExtensions(creativeExtensions);
         */
        creatives.add(creative);
        inline.setCreatives(creatives);
        inline.setImpression(impression);
    	Ad ad = new Ad();
    	/**
			ad.setSequence(sequence);
    	 */
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
            String bitRate, int width, int height, String skipOffset){
        
        VastNormal vastNormal = createVast(csc, adId, impressionId,  errorUrl, pubGuid, 
                linearity, companionType, tracking, trackingEventUrl,
                adName,
                durationinFormat, clickurl, cdnUrl,
                creativeId, delivery, mimeType,
                bitRate, width, height, skipOffset);
        if(vastNormal == null){
            return null;
        }
        try{
            Marshaller marshallerObj = GetJaxBContextVastNormalThreeDotZero.getContext().createMarshaller();  
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
