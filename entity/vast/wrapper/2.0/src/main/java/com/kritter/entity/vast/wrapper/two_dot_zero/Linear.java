package com.kritter.entity.vast.wrapper.two_dot_zero;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Linear")
public class Linear {
    @Setter@XmlElement(name="TrackingEvents")
    private List<Tracking> trackingEvents;
    @Setter@XmlElement(name="VideoClicks")
    private VideoClicks videoClicks;
    @Setter@XmlElement(name="AdParameters")
    private AdParameters adParameters;
    
}
