package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="HTMLResource")
public class HTMLResource {
    @Setter@XmlValue@XmlCDATA
    private String str;
}
