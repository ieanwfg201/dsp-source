package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="AdSystem")
public class AdSystem {
    @Setter@Getter@XmlAttribute(name="version")
    private String version="3.0";
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
