package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="NonLinearAds")
public class NonLinearAds {
    @Setter@XmlElement(name="NonLinear")
    private NonLinear nonLinear;
    @Setter@XmlElement(name="TrackingEvents")
    private TrackingEvents trackingEvents;
}
