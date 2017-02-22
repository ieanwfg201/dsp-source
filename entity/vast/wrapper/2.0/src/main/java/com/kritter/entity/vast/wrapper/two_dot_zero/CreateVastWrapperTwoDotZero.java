package com.kritter.entity.vast.wrapper.two_dot_zero;

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

public class CreateVastWrapperTwoDotZero {
    public static VastWrapper createWrapper(String csc, String adId,String impressionId,
            String vastTagUrl,String errorUrl, String pubGuid, int linearity,
            int companionType, Integer[] tracking, String trackingEventUrl, String clickurl,
            List<String> clickTrackers, List<String> impTrackers){
        Wrapper wrapper = new Wrapper();
        VASTAdTagURI vastAdTagURI = new VASTAdTagURI();
        vastAdTagURI.setStr(vastTagUrl);
        wrapper.setVastAdTagURI(vastAdTagURI);
        AdSystem adSystem = new AdSystem();
        adSystem.setStr(pubGuid);
        wrapper.setAdSystem(adSystem);
        List<Creative> creatives = new LinkedList<Creative>();
        Creative creative = new Creative();
        creative.setId(adId);
        if(linearity == VideoLinearity.LINEAR_IN_STREAM.getCode()){
            Linear linear = new Linear();
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
                if(clickurl != null || clickTrackers != null){
                	List<ClickTracking> ctList = new LinkedList<ClickTracking>();
                	if(clickurl != null){
                		ClickTracking clickTracking = new ClickTracking();
                		clickTracking.setStr(clickurl);
                		ctList.add(clickTracking);
                	}
                	VideoClicks videoClicks = new VideoClicks();
                	if(clickTrackers != null){
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

                TrackingEvents t = new TrackingEvents();
                t.setTracking(trackingEvents);
                linear.setTrackingEvents(t);
            }
            
            creative.setLinear(linear);
        }else if(linearity == VideoLinearity.NON_LINEAR_OVERLAY.getCode()){
            if(companionType != VASTCompanionTypes.Unknown.getCode()){
                List<Companion> companionAds = new LinkedList<Companion>();
                Companion companion  = new Companion();
                List<Tracking> trackingEvents = new LinkedList<Tracking>();
                Tracking t = new Tracking();
                t.setEvent(VASTNonLinearTrackingEventTypes.creativeView.getName());
                t.setStr(trackingEventUrl+"&"+TEvent.ttype+"="+TEventType.video.getName()+"&"+TEvent.tevent+"="+VASTNonLinearTrackingEventTypes.creativeView.getName());
                trackingEvents.add(t);
                TrackingEvents tEvent = new TrackingEvents();
                tEvent.setTracking(trackingEvents);
                companion.setTrackingEvents(tEvent);
                companionAds.add(companion);
                CompanionAds c = new CompanionAds();
                c.setCompanionAd(companionAds);
                creative.setCompanionAds(c);    
            }else{
                NonLinearAds nonLinearAds = new NonLinearAds();
                if(tracking != null){
                    List<Tracking> trackingEvents = new LinkedList<Tracking>();
                    for(Integer i: tracking){
                        VASTKritterTrackingEventTypes vtet  = VASTKritterTrackingEventTypes.getEnum(i);
                        if(vtet != null){
                            Tracking t = new Tracking();
                            t.setEvent(vtet.getName());
                            t.setStr(trackingEventUrl+"&ttype="+TEventType.video.getName()+"&"+TEvent.tevent+"="+i);
                            trackingEvents.add(t);
                        }
                    }
                    TrackingEvents t = new TrackingEvents();
                    t.setTracking(trackingEvents);
                    nonLinearAds.setTrackingEvents(t);
                }
                creative.setNonLinearAds(nonLinearAds);
            }
            
            
        }
        creatives.add(creative);
        Creatives c = new Creatives();
        c.setCreative(creatives);
        wrapper.setCreatives(c);
        Error error = new Error();
        if(errorUrl != null){
            error.setStr(errorUrl+"&"+TEvent.ttype+"="+TEventType.videoerror.getName()+"&"+TEvent.tevent+"="+VideoMacros.ERRORCODE.getName());
        }
        wrapper.setError(error);
        Impression impression = new Impression();
        impression.setId(impressionId);
        impression.setStr(csc);
        List<Impression> impressions = new LinkedList<Impression>();
        impressions.add(impression);
    	if(impTrackers != null){
    		int cnt=1;
    		for(String impTracker:impTrackers){
    			if(impTracker != null && !impTracker.isEmpty()){
    				Impression imp = new Impression();
    				imp.setId(impressionId+"-"+cnt);
    				imp.setStr(impTracker);
    				impressions.add(imp);
    			}
    			cnt++;
    		}
    	}
        wrapper.setImpression(impressions);
        Ad ad = new Ad();
        ad.setId(adId);
        ad.setWrapper(wrapper);
        VastWrapper vastWrapper = new VastWrapper();
        vastWrapper.setAd(ad);
        return vastWrapper;
    }
    public static String createWrapperString(String csc, String adId,String impressionId,
            String vastTagUrl,String errorUrl, String pubGuid, int linearity,
            int companionType, Integer[] tracking, String trackingEventUrl,Logger  logger,String clickurl,
            List<String> clickTrackers, List<String> impTrackers){
        
        VastWrapper vastWrapper = createWrapper(csc, adId, impressionId, vastTagUrl, errorUrl, pubGuid, 
                linearity, companionType, tracking, trackingEventUrl, clickurl,clickTrackers, impTrackers);
        if(vastWrapper == null){
            return null;
        }
        try{
            Marshaller marshallerObj = GetJaxBContextVastTwoDotZero.getContext().createMarshaller();  
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshallerObj.marshal(vastWrapper, sw);
            return sw.toString();
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }
    
    public static VastWrapper unMarshallWrapperlVastTwoDotZeroStr(String str,Logger  logger){
    	if(str==null || str.isEmpty()){
    		return null;
    	}
    	try{
    		Unmarshaller jaxbUnmarshaller = GetJaxBContextVastTwoDotZero.getUnMarshallContext().createUnmarshaller();
    		StringReader reader = new StringReader(str);
    		VastWrapper vastWrapper = (VastWrapper)jaxbUnmarshaller.unmarshal(reader);
    		//System.out.println(jaxbUnmarshaller.unmarshal(reader));
    		return vastWrapper;
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
    	
    	VastWrapper wrapper =  unMarshallWrapperlVastTwoDotZeroStr(str,logger);
    	if(wrapper==null){
    		return null;
    	}
    	if(wrapper.getAd() != null && wrapper.getAd().getWrapper() != null){
    		List<Impression> impressions=null;
    		impressions=wrapper.getAd().getWrapper().getImpression();
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
                Marshaller marshallerObj = GetJaxBContextVastTwoDotZero.getContext().createMarshaller();  
                marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                StringWriter sw = new StringWriter();
                marshallerObj.marshal(wrapper, sw);
                return sw.toString();
            }catch(Exception e){
                logger.error(e.getMessage(),e);
                return null;
            }    		
    	}
    	return null;
    }


    /*public static void main(String args[]){
    	String s ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><VAST version=\"3.0\">   <Ad id=\"011c3e84-ed0b-cb01-5655-a0ea66000010\">      <Wrapper>         <AdSystem version=\"2.0\"><![CDATA[011c3e84-ed0b-cb01-5655-a1b4d7000011]]></AdSystem>         <VASTAdTagURI><![CDATA[https://servedbyadbutler.com/vast.spark?setID=2107&ID=167500&pid=22779]]></VASTAdTagURI>         <Error><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=videoerror&tevent=[ERRORCODE]]]></Error>         <Impression id=\"011c3e84-ed0b-cb01-5659-78bbba000002:3e\"><![CDATA[http://localhost/csc/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA]]></Impression>         <Creatives>            <Creative id=\"011c3e84-ed0b-cb01-5655-a0ea66000010\">               <Linear>                  <TrackingEvents>                     <Tracking event=\"firstQuartile\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=firstQuartile]]></Tracking>                     <Tracking event=\"midpoint\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=midpoint]]></Tracking>                     <Tracking event=\"thirdQuartile\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=thirdQuartile]]></Tracking>                     <Tracking event=\"complete\"><![CDATA[http://localhost/tevent/1/5/011c3e84-ed0b-cb01-5659-78bbba000002:3e/115617/10/871/3/108/-1/58/121/-1/aT0wLjAwNSxlPTAuMDA1/-1/-1/2/-1/1/2/3d14ccfb61de6a0d1262feb61d5a257?iid=AAAAAA&ttype=video&tevent=complete]]></Tracking>                  </TrackingEvents>               </Linear>            </Creative>         </Creatives>      </Wrapper>   </Ad></VAST>";
    	System.out.println(CreateVastWrapperTwoDotZero.unMarshallWrapperlVastTwoDotZeroStr(s, null));
    }*/

}
