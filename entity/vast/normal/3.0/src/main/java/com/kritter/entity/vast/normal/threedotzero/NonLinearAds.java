package com.kritter.entity.vast.normal.threedotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="NonLinearAds")
public class NonLinearAds {
    @Setter@XmlElement(name="NonLinear")
    private List<NonLinear> nonLinearAds;
    @Setter@XmlElement(name="TrackingEvents")
    private List<Tracking> trackingEvents;    
}
