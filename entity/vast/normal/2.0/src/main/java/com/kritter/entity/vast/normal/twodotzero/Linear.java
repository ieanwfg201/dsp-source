package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Linear")
public class Linear {
    @Setter@XmlElement(name="Duration")
    private Duration duration;
    @Setter@XmlElement(name="TrackingEvents")
    private TrackingEvents trackingEvents;
    @Setter@XmlElement(name="AdParameters")
    private AdParameters adParameters;    
    @Setter@XmlElement(name="VideoClicks")
    private VideoClicks videoClicks;    
    @Setter@XmlElement(name="MediaFiles")
    private MediaFiles mediaFiles;
}
