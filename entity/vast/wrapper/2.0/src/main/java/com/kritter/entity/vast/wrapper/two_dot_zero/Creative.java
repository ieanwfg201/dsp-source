package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Creative")
public class Creative {
    @Setter@Getter@XmlAttribute(name="id")
    private String id;
    @Setter@Getter@XmlAttribute(name="sequence")
    private String sequence;
    @Setter@Getter@XmlAttribute(name="AdID")
    private String adID;
    @Setter@Getter@XmlElement(name="Linear")
    private Linear linear;
    @Setter@Getter@XmlElement(name="NonLinearAds")
    private NonLinearAds nonLinearAds;
    @Setter@Getter@XmlElement(name="CompanionAds")
    private CompanionAds companionAds;
}
