package com.kritter.entity.vast.normal.twodotzero;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.Logger;

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
            String bitRate, int width, int height, List<String> clickTrackers){
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
        	MediaFiles m = new MediaFiles();
        	m.setMediaFile(mediaFiles);
        	linear.setMediaFiles(m);
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
                TrackingEvents tEvents = new TrackingEvents();
                tEvents.setTrackingEvents(trackingEvents);
                linear.setTrackingEvents(tEvents);
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
            	if(clickTrackers != null){
            		List<ClickTracking> ctList = new LinkedList<ClickTracking>();
            		for(String clickTracker:clickTrackers){
            			if(clickTracker != null && !clickTracker.isEmpty()){
            				ClickTracking cT = new ClickTracking();
            				cT.setStr(clickTracker);
            				ctList.add(cT);
            			}
            		}
                	videoClicks.setClickTracking(ctList);
            	}

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
                TrackingEvents tEvents = new TrackingEvents();
                tEvents.setTrackingEvents(trackingEvents);
        		companion.setTrackingEvents(tEvents);
        		if(clickurl != null){
        			CompanionClickThrough companionClickThrough = new CompanionClickThrough();
                	companionClickThrough.setStr(clickurl);
        			companion.setCompanionClickThrough(companionClickThrough);
        		}
        		companionAds.add(companion);
        		CompanionAds cAds = new CompanionAds();
        		cAds.setCompanion(companionAds);
                creative.setCompanionAds(cAds);
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
                    TrackingEvents tEvents = new TrackingEvents();
                    tEvents.setTrackingEvents(trackingEvents);
                    nonLinear.setTrackingEvents(tEvents);
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
        		NonLinearAds nAds = new NonLinearAds();
        		nAds.setNonLinearAd(nonLinearAds);
        		creative.setNonLinearAds(nAds);
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
    	List<Impression> impressions = new LinkedList<Impression>();
    	impressions.add(impression);
    	inline.setImpression(impressions);
    	Creatives c = new Creatives();
    	c.setCreative(creatives);
    	inline.setCreatives(c);
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
            String bitRate, int width, int height, List<String> clickTrackers){
        
        VastNormal vastNormal = createVast(csc, adId, impressionId,  errorUrl, pubGuid, 
                linearity, companionType, tracking, trackingEventUrl,
                adName,
                durationinFormat, clickurl, cdnUrl,
                creativeId, delivery, mimeType,
                bitRate, width, height, clickTrackers);
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
    public static VastNormal unMarshallNormalVastTwoDotZeroStr(String str,Logger  logger){
    	if(str==null || str.isEmpty()){
    		return null;
    	}
    	try{
    		Unmarshaller jaxbUnmarshaller = GetJaxBContextVastNormalTwoDotZero.getUnMarshallContext().createUnmarshaller();
    		StringReader reader = new StringReader(str);
    		VastNormal vastNormal = (VastNormal)jaxbUnmarshaller.unmarshal(reader);
    		//System.out.println(jaxbUnmarshaller.unmarshal(reader));
    		return vastNormal;
    		//return null;
    	}catch(Exception e){
    		logger.error(e.getMessage(),e);
    		//e.printStackTrace();
    		return null;
    	}
    }
    public static String addImpressionsVastStr(String str,Logger logger, LinkedList<String> impressionsTrackers,String id){
    	if(impressionsTrackers ==null || impressionsTrackers.size()<1){
    		return str;
    	}
    	
    	VastNormal normal =  unMarshallNormalVastTwoDotZeroStr(str,logger);
    	if(normal==null){
    		return null;
    	}
    	if(normal.getAd() != null && normal.getAd().getInline() != null){
    		List<Impression> impressions=null;
    		impressions=normal.getAd().getInline().getImpression();
    		if(impressions==null){
    			impressions = new LinkedList<Impression>();
    		}
    		int count=0;
    		for(String impressionsTracker:impressionsTrackers){
    			Impression imp = new Impression();
    			imp.setId(id+":"+count);
    			imp.setStr(impressionsTracker);
    			impressions.add(imp);
    			count++;
    		}
            try{
                Marshaller marshallerObj = GetJaxBContextVastNormalTwoDotZero.getContext().createMarshaller();  
                marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                StringWriter sw = new StringWriter();
                marshallerObj.marshal(normal, sw);
                return sw.toString();
            }catch(Exception e){
                logger.error(e.getMessage(),e);
                return null;
            }    		
    	}
    	return null;
    }


   /* public static void main(String args[]){
    	String s ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><VAST version=\"2.0\">   <Ad id=\"01026424-75fb-e701-576a-13d37700000a\" sequence=\"1\">      <InLine>         <AdSystem version=\"3.0\"><![CDATA[01026424-75fb-e701-576a-0a92b2000001]]></AdSystem>         <AdTitle><![CDATA[2]]></AdTitle>         <Impression id=\"010258de-4e3e-d901-576f-ea4bc2000015:2\"><![CDATA[http://post.pokkt.com/csc/1/6/010258de-4e3e-d901-576f-ea4bc2000015:2/43187/234/1054/2/2/-1/26/172/-1/aT0wLjEsZT0wLjE=/-1/-1/2/-1/1/1/6dea835e15dcc3aa683ed16992d79d6?iid=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAjQPaWQ6MjY6VU5JUVVFIElEMg9pZDoyNjpVTklRVUUgSUQzFVVOSVFVRVNJVEVJRE9GVEhFU0lURQ&eid=id:26:UNIQUE ID&kid=id:26:UNIQUE ID]]></Impression>         <Creatives>            <Creative id=\"01026424-75fb-e701-576a-103710000008\" adID=\"01026424-75fb-e701-576a-13d37700000a\">               <Linear>                  <Duration><![CDATA[00:00:33]]></Duration>                  <MediaFiles>                     <MediaFile type=\"video/mp4\" width=\"-1\" height=\"-1\"><![CDATA[http://d1f7ey67xs6qkf.cloudfront.net/img/01026424-75fb-e701-576a-0ffecb000006.mp4]]></MediaFile>                  </MediaFiles>               </Linear>            </Creative>         </Creatives>      </InLine>   </Ad></VAST>";
    	System.out.println(CreateVastNormalTwoDotZero.unMarshallNormalVastTwoDotZeroStr(s, null));
    }*/
}
