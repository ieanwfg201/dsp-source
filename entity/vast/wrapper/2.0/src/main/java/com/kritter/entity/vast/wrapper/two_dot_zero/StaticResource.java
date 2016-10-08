package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="StaticResource")
public class StaticResource {
    @Setter@Getter@XmlAttribute(name="creativeType")
    private String creativeType;
    @Setter@Getter@XmlValue
    private String str;
}
