package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Extension")
public class Extension {
    @Setter@Getter@XmlAttribute(name="type")
    private String type;
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
