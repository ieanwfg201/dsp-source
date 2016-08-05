package com.kritter.entity.vast.wrapper.two_dot_zero;

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
    @Setter@XmlAttribute(name="AdID")
    private String adID;
    @Setter@XmlElement(name="Linear")
    private Linear linear;
    @Setter@XmlElement(name="NonLinearAds")
    private NonLinearAds nonLinearAds;
    @Setter@XmlElement(name="CompanionAds")
    private CompanionAds companionAds;
}
