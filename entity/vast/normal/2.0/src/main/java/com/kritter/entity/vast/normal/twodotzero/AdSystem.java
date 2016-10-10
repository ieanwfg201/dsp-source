package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="AdSystem")
public class AdSystem {
    @Setter@Getter@XmlAttribute(name="version")
    private String version;
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
