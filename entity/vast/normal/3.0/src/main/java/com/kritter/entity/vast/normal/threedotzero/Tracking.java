package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="Tracking")
public class Tracking {
    @Setter@XmlAttribute(name="event")
    private String event;
    @Setter@XmlValue@XmlCDATA
    private String str;
}
