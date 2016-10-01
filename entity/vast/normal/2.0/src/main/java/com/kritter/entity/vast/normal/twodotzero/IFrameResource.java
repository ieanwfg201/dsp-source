package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="IFrameResource")
public class IFrameResource {
    @Setter@Getter@XmlValue@XmlCDATA
    private String str;
}
