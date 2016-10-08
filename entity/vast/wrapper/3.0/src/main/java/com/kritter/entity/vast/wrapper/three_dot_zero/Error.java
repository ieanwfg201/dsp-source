package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Error")
public class Error {
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
