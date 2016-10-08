package com.kritter.entity.vast.wrapper.two_dot_zero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name="Ad")
public class Ad {
    @Setter@Getter@XmlAttribute(name="id")
    private String id="-1";
    @Setter@Getter@XmlElement(name="Wrapper")
    private Wrapper wrapper;
}
