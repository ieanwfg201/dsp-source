package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="AdSystem")
public class AdSystem {
    @Setter@XmlAttribute(name="version")
    private String version="3.0";
    @Setter@XmlValue@XmlCDATA
    private String str;
}
