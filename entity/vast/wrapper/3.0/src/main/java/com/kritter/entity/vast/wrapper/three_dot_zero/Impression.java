package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="Impression")
public class Impression {
    @Setter@XmlAttribute(name="id")
    private String id="-1";
    @Setter@XmlValue@XmlCDATA
    private String str;
}
