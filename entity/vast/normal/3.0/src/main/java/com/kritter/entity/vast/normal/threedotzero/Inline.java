package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Inline")
public class Inline {
    @Setter@XmlElement(name="AdSystem")
    private AdSystem adSystem;
    @Setter@XmlElement(name="AdTitle")
    private AdTitle adTitle;
    @Setter@XmlElement(name="Description")
    private Description description;
    @Setter@XmlElement(name="Advertiser")
    private Advertiser advertiser;
    @Setter@XmlElement(name="Pricing")
    private Pricing pricing;
    @Setter@XmlElement(name="Survey")
    private Survey survey;
    @Setter@XmlElement(name="Error")
    private Error error;
    @Setter@XmlElement(name="Impression")
    private Impression impression;
    @Setter@XmlElement(name="Creatives")
    private Creatives creatives;
    @Setter@XmlElement(name="Extensions")
    private Extensions extensions;
    
}
