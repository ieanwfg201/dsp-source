package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Setter;

@XmlRootElement(name="NonLinearClickThrough")
public class NonLinearClickThrough {
    @Setter@XmlValue@XmlCDATA
    private String str;
}
