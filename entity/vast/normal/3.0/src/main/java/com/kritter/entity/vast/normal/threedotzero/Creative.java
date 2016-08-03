package com.kritter.entity.vast.normal.threedotzero;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Setter;

@XmlRootElement(name="Creative")
public class Creative {
    @Setter@XmlAttribute(name="id")
    private String id;
    @Setter@XmlAttribute(name="sequence")
    private String sequence;
    @Setter@XmlAttribute(name="adID")
    private String adID;
    @Setter@XmlAttribute(name="apiFramework")
    private String apiFramework;
    @Setter@XmlElement(name="CreativeExtensions")
    private List<CreativeExtension> creativeExtensions;
    @Setter@XmlElement(name="Linear")
    private Linear linear;
    @Setter@XmlElement(name="CompanionAds")
    private List<Companion> companionAds;
    @Setter@XmlElement(name="NonLinearAds")
    private NonLinearAds nonLinearAds;
}
