package com.kritter.entity.vast.normal.threedotzero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="NonLinearClickThrough")
public class NonLinearClickThrough {
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
