package com.kritter.entity.vast.wrapper.three_dot_zero;

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

public class CreateVastWrapper {
    public static VastWrapper createWrapper(String csc, String adId,String impressionId,
            String vastTagUrl,String errorUrl, String pubGuid, int linearity,
            int companionType, Integer[] tracking, String trackingEventUrl){
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
                        t.setStr(trackingEventUrl+"&"+TEvent.ttype+"="+TEventType.video.getName()+"&"+TEvent.tevent+"="+vtet.getName());
                        trackingEvents.add(t);
                    }
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
                TrackingEvents tEvents = new TrackingEvents();
                tEvents.setTracking(trackingEvents);
                companion.setTrackingEvents(tEvents);
                companionAds.add(companion);
                CompanionAds c = new CompanionAds();
                c.setCompanion(companionAds);
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
                            t.setStr(trackingEventUrl+"&"+TEvent.ttype+"="+TEventType.video.getName()+"&"+TEvent.tevent+"="+i);
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
        wrapper.setImpression(impression);
        Ad ad = new Ad();
        ad.setId(adId);
        ad.setWrapper(wrapper);
        VastWrapper vastWrapper = new VastWrapper();
        vastWrapper.setAd(ad);
        return vastWrapper;
    }
    public static String createWrapperString(String csc, String adId,String impressionId,
            String vastTagUrl,String errorUrl, String pubGuid, int linearity,
            int companionType, Integer[] tracking, String trackingEventUrl,Logger  logger){
        
        VastWrapper vastWrapper = createWrapper(csc, adId, impressionId, vastTagUrl, errorUrl, pubGuid, 
                linearity, companionType, tracking, trackingEventUrl);
        if(vastWrapper == null){
            return null;
        }
        try{
            Marshaller marshallerObj = GetJaxBContext.getContext().createMarshaller();  
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();
            marshallerObj.marshal(vastWrapper, sw);
            return sw.toString();
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return null;
        }
    }
}
