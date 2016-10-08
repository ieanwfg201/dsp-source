package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Companion")
public class Companion {
    @Setter@Getter@XmlAttribute(name="id")
    private String id;
    @Setter@Getter@XmlAttribute(name="width")
    private int width;
    @Setter@Getter@XmlAttribute(name="height")
    private int height;
    @Setter@Getter@XmlAttribute(name="assetWidth")
    private String assetWidth;
    @Setter@Getter@XmlAttribute(name="assetHeight")
    private String assetHeight;
    @Setter@Getter@XmlAttribute(name="expandedWidth")
    private String expandedWidth;
    @Setter@Getter@XmlAttribute(name="expandedHeight")
    private String expandedHeight;
    @Setter@Getter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@Getter@XmlAttribute(name="adSlotID")
    private String adSlotID;
    @Setter@Getter@XmlElement(name="StaticResource")
    private StaticResource staticResource;
    @Setter@Getter@XmlElement(name="IFrameResource")
    private IFrameResource iFrameResource;
    @Setter@Getter@XmlElement(name="HTMLResource")
    private HTMLResource hTMLResource;
    @Setter@Getter@XmlElement(name="AdParameters")
    private AdParameters adParameters;
    @Setter@Getter@XmlElement(name="AltText")
    private AltText altText;
    @Setter@Getter@XmlElement(name="CompanionClickThrough")
    private CompanionClickThrough companionClickThrough;
    @Setter@Getter@XmlElement(name="CompanionClickTracking")
    private CompanionClickTracking companionClickTracking;
    @Setter@Getter@XmlElement(name="TrackingEvents")
    private TrackingEvents trackingEvents;    
}
