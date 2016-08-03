package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="StaticResource")
public class StaticResource {
    @Setter@XmlAttribute(name="creativeType")
    private String creativeType;
    @Setter@XmlValue@XmlCDATA
    private String str;
}
