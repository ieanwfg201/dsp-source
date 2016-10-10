package com.kritter.entity.vast.wrapper.two_dot_zero;

import java.io.StringWriter;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * NOTE 2.0 VAST entity is not complete and correct
 * It caters to Wrapper requirement
 *
 */
@XmlRootElement(name="VAST")
public class VastWrapper {
    
    @Setter@Getter@XmlAttribute(name="version")
    private String version="2.0";
    @Setter@Getter@XmlElement(name="Ad")
    private Ad ad;
    
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
        Marshaller marshallerObj = GetJaxBContextVastTwoDotZero.getContext().createMarshaller();  
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
        Marshaller marshallerObj1 = GetJaxBContextVastTwoDotZero.getContext().createMarshaller();
        marshallerObj1.marshal(v1, sw);
        System.out.println(sw.toString());
        long d = System.currentTimeMillis()-b;
        System.out.println(d);
    }
}
