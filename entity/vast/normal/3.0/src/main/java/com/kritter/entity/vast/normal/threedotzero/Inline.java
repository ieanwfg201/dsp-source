package com.kritter.entity.vast.normal.threedotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="InLine")
public class Inline {
    @Setter@Getter@XmlElement(name="AdSystem")
    private AdSystem adSystem;
    @Setter@Getter@XmlElement(name="AdTitle")
    private AdTitle adTitle;
    @Setter@Getter@XmlElement(name="Description")
    private Description description;
    @Setter@Getter@XmlElement(name="Advertiser")
    private Advertiser advertiser;
    @Setter@Getter@XmlElement(name="Pricing")
    private Pricing pricing;
    @Setter@Getter@XmlElement(name="Survey")
    private Survey survey;
    @Setter@Getter@XmlElement(name="Error")
    private Error error;
    @Setter@Getter@XmlElement(name="Impression")
    private List<Impression> impression;
    @Setter@Getter@XmlElement(name="Creatives")
    private Creatives creatives;
    @Setter@Getter@XmlElement(name="Extensions")
    private Extensions extensions;
    
}
