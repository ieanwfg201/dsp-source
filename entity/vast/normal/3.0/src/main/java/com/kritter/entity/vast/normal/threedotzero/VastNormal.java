package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="VAST")
public class VastNormal {
    
    @Setter@XmlAttribute(name="version")
    private String version="3.0";
    @Setter@XmlElement(name="Error")
    private Error error;
    @Setter@XmlElement(name="Ad")
    private Ad ad;
    
}
