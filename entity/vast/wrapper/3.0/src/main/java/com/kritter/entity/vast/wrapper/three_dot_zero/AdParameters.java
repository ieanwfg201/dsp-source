package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="AdParameters")
public class AdParameters {
    @Setter@Getter@XmlAttribute
    private String xmlEncoded;
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
