package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Setter;

@XmlRootElement(name="Linear")
public class Linear {
    @Setter@XmlAttribute(name="skipoffset")
    private String skipoffset;
    @Setter@XmlElement(name="AdParameters")
    private AdParameters adParameters;
    @Setter@XmlElement(name="Duration")
    private Duration duration;
    @Setter@XmlElement(name="MediaFiles")
    private MediaFiles mediaFiles;
    @Setter@XmlElement(name="TrackingEvents")
    private TrackingEvents trackingEvents;
    @Setter@XmlElement(name="VideoClicks")
    private VideoClicks videoClicks;
    @Setter@XmlElement(name="Icons")
    private Icons icons;
}
