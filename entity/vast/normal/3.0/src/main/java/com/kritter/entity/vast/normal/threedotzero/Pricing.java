package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="Pricing")
public class Pricing {
    @Setter@XmlAttribute(name="model")
    private String model;
    @Setter@XmlAttribute(name="currency")
    private String currency;
    @Setter@XmlValue@XmlCDATA
    private String str;
}
