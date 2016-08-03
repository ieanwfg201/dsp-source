package com.kritter.entity.vast.normal.twodotzero;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Setter;

@XmlRootElement(name="Ad")
public class Ad {
    @Setter@XmlAttribute(name="id")
    private String id;
    @Setter@XmlElement(name="InLine")
    private InLine inline;

}
