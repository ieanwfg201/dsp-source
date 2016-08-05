package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Setter;

@XmlRootElement(name="NonLinear")
public class NonLinear {
    @Setter@XmlAttribute(name="id")
    private String id;
    @Setter@XmlAttribute(name="width")
    private int width;
    @Setter@XmlAttribute(name="height")
    private int height;
    @Setter@XmlAttribute(name="expandedWidth")
    private String expandedWidth;
    @Setter@XmlAttribute(name="expandedHeight")
    private String expandedHeight;
    @Setter@XmlAttribute(name="scalable")
    private String scalable;
    @Setter@XmlAttribute(name="maintainAspectRatio")
    private String maintainAspectRatio;
    @Setter@XmlAttribute(name="minSuggestedDuration")
    private String minSuggestedDuration;
    @Setter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@XmlElement(name="StaticResource")
    private StaticResource staticResource;
    @Setter@XmlElement(name="IFrameResource")
    private IFrameResource iFrameResource;
    @Setter@XmlElement(name="HTMLResource")
    private HTMLResource hTMLResource;
    @Setter@XmlElement(name="TrackingEvents")
    private TrackingEvents trackingEvents;
    @Setter@XmlElement(name="NonLinearClickThrough")
    private NonLinearClickThrough nonLinearClickThrough;
    @Setter@XmlElement(name="AdParameters")
    private AdParameters adParameters;

    
}
