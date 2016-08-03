package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="Extension")
public class Extension {
    @Setter@XmlAttribute(name="type")
    private String type;
    @Setter@XmlValue@XmlCDATA
    private String str;
}
