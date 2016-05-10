package com.kritter.entity.vast.wrapper.two_dot_zero;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Companion")
public class Companion {
    @Setter@XmlAttribute(name="id")
    private String id;
    @Setter@XmlAttribute(name="width")
    private String width;
    @Setter@XmlAttribute(name="height")
    private String height;
    @Setter@XmlAttribute(name="expandedWidth")
    private String expandedWidth;
    @Setter@XmlAttribute(name="expandedHeight")
    private String expandedHeight;
    @Setter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@XmlElement(name="StaticResource")
    private StaticResource staticResource;
    @Setter@XmlElement(name="IFrameResource")
    private IFrameResource iframeResource;
    @Setter@XmlElement(name="HTMLResource")
    private HTMLResource htmlResource;
    @Setter@XmlElement(name="AdParameters")
    private AdParameters adParameters;
    @Setter@XmlElement(name="AltText")
    private AltText altText;
    @Setter@XmlElement(name="CompanionClickThrough")
    private CompanionClickThrough companionClickThrough;
    @Setter@XmlElement(name="TrackingEvents")
    private List<Tracking> trackingEvents;

}
