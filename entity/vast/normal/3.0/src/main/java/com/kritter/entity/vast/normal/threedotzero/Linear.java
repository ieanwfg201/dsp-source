package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Linear")
public class Linear {
    @Setter@Getter@XmlAttribute(name="skipoffset")
    private String skipoffset;
    @Setter@Getter@XmlElement(name="AdParameters")
    private AdParameters adParameters;
    @Setter@Getter@XmlElement(name="Duration")
    private Duration duration;
    @Setter@Getter@XmlElement(name="MediaFiles")
    private MediaFiles mediaFiles;
    @Setter@Getter@XmlElement(name="TrackingEvents")
    private TrackingEvents trackingEvents;
    @Setter@Getter@XmlElement(name="VideoClicks")
    private VideoClicks videoClicks;
    @Setter@Getter@XmlElement(name="Icons")
    private Icons icons;
}
