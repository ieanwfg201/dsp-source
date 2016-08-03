package com.kritter.entity.vast.normal.threedotzero;

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
    private int width;
    @Setter@XmlAttribute(name="height")
    private int height;
    @Setter@XmlAttribute(name="assetWidth")
    private String assetWidth;
    @Setter@XmlAttribute(name="assetHeight")
    private String assetHeight;
    @Setter@XmlAttribute(name="expandedWidth")
    private String expandedWidth;
    @Setter@XmlAttribute(name="expandedHeight")
    private String expandedHeight;
    @Setter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@XmlAttribute(name="adSlotID")
    private String adSlotID;
    @Setter@XmlElement(name="StaticResource")
    private StaticResource staticResource;
    @Setter@XmlElement(name="IFrameResource")
    private IFrameResource iFrameResource;
    @Setter@XmlElement(name="HTMLResource")
    private HTMLResource hTMLResource;
    @Setter@XmlElement(name="AdParameters")
    private AdParameters adParameters;
    @Setter@XmlElement(name="AltText")
    private AltText altText;
    @Setter@XmlElement(name="CompanionClickThrough")
    private CompanionClickThrough companionClickThrough;
    @Setter@XmlElement(name="CompanionClickTracking")
    private CompanionClickTracking companionClickTracking;
    @Setter@XmlElement(name="TrackingEvents")
    private List<Tracking> trackingEvents;    
}
