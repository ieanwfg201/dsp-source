package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="AdSystem")
public class AdSystem {
    @Setter@Getter@XmlAttribute(name="version")
    private String version="2.0";
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
