package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="CompanionClickThrough")
public class CompanionClickThrough {
    @Setter@XmlValue@XmlCDATA
    private String str;
}