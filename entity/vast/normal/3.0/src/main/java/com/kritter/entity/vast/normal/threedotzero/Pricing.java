package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Pricing")
public class Pricing {
    @Setter@Getter@XmlAttribute(name="model")
    private String model;
    @Setter@Getter@XmlAttribute(name="currency")
    private String currency;
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
