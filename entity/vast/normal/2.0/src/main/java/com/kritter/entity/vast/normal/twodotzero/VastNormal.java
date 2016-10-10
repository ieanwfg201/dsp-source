package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="VAST")
public class VastNormal {
    
    @Setter@Getter@XmlAttribute(name="version")
    private String version="2.0";
    @Setter@Getter@XmlElement(name="Ad")
    private Ad ad;
}
