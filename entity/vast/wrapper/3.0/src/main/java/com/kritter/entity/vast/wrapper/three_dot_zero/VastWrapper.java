package com.kritter.entity.vast.wrapper.three_dot_zero;

import java.io.StringWriter;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="VAST")
public class VastWrapper {
    
    @Setter@Getter@XmlAttribute(name="version")
    private String version="3.0";
    @Setter@Getter@XmlElement(name="Ad")
    private Ad ad;
    
/**
 * VAST version="3.0"
    Ad
        Wrapper
            AdSystem version"3.0"
            Impression
            VASTAdTagURI
            Creatives
                Creative
                    Linear
                        <VideoClicks>
                            ClickTracking
                            CustomClicks
                        <TrackingEvents>
                            Tracking
                            Tracking
                        Icons
                            Icon
                                IconClicks
                                    IconClickThrough
                                    IconClickTracking
                                IconViewTracking
                    NonLinearAds
                        NonLinear
                            NonLinearClickTracking>
                        <TrackingEvents>
                            Tracking
                            Tracking                        
                    CompanionAds
                        Companion
                            CompanionClickThrough
                            CompanionClickTracking
                            <TrackingEvents>
                                Tracking
                                Tracking
            Error
            Extensions

 */
    public static void main(String args[]) throws Exception{
        long a = System.currentTimeMillis();
        VastWrapper vw = new VastWrapper();
        //vw.setVASTAdTagURI("knw");
        VASTAdTagURI vASTAdTagURI= new VASTAdTagURI();
        vASTAdTagURI.setStr("wlqhfqwd");
        Wrapper w = new Wrapper();
        w.setVastAdTagURI(vASTAdTagURI);
        w.setVastAdTagURI(vASTAdTagURI);
        Ad ad = new Ad();
        ad.setWrapper(w);
        vw.setAd(ad);
        Marshaller marshallerObj = GetJaxBContext.getContext().createMarshaller();  
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        long a1 = System.currentTimeMillis();
        marshallerObj.marshal(vw, sw);
        long b = System.currentTimeMillis();
        long c = b-a;
        long c1 = b-a1;
        System.out.println(sw.toString());
        System.out.println(c);
        System.out.println(c1);
        VastWrapper v1 = new VastWrapper();
        //v1.setVASTAdTagURI("knw");
        Marshaller marshallerObj1 = GetJaxBContext.getContext().createMarshaller();
        marshallerObj1.marshal(v1, sw);
        System.out.println(sw.toString());
        long d = System.currentTimeMillis()-b;
        System.out.println(d);
    }
}
