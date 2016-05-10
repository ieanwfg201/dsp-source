package com.kritter.entity.vast.wrapper.three_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Ad")
public class Ad {
    @Setter@XmlAttribute(name="id")
    private String id="-1";
    @Setter@XmlElement(name="Wrapper")
    private Wrapper wrapper;
}
