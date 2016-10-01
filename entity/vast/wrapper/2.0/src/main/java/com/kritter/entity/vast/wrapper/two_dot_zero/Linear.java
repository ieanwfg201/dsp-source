package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Linear")
public class Linear {
    @Setter@Getter@XmlElement(name="TrackingEvents")
    private TrackingEvents trackingEvents;
    @Setter@Getter@XmlElement(name="VideoClicks")
    private VideoClicks videoClicks;
    @Setter@Getter@XmlElement(name="AdParameters")
    private AdParameters adParameters;
    
}
