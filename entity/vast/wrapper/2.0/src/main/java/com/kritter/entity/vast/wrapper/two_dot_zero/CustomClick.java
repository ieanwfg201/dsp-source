package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="CustomClick")
public class CustomClick {
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
    @Setter@Getter@XmlAttribute(name="id")
    private String id;
}
