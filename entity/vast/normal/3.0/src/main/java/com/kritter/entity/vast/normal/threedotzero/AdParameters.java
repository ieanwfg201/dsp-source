package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="AdParameters")
public class AdParameters {
    @Setter@XmlAttribute(name="xmlEncoded")
    private String xmlEncoded;
    @Setter@XmlValue@XmlCDATA
    private String str;
}
